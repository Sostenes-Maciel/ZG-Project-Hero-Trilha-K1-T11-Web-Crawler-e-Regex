package br.com.zg.crawler.service

import br.com.zg.crawler.exporter.CsvHistoricoExporter
import br.com.zg.crawler.exporter.HistoricoExporter
import br.com.zg.crawler.model.VersaoTiss
import br.com.zg.crawler.parser.TissHtmlParser
import br.com.zg.crawler.parser.TissRegexParser
import org.jsoup.nodes.Document

import java.time.YearMonth

class TissCrawler {

    private static final String URL_ANS = 'https://www.gov.br/ans/pt-br'
    private static final String TEXTO_LINK_PRESTADOR = 'Espaço do Prestador de Serviços de Saúde'
    private static final String TEXTO_LINK_TISS = 'TISS - Padrão para Troca de Informação de Saúde Suplementar'
    private static final String TEXTO_LINK_HISTORICO = 'todas as versões'
    private static final String TEXTO_LINK_TABELAS = 'acessar as planilhas'
    private static final String TEXTO_LINK_ERROS = 'tabela de erros no envio para a ans'

    private static final String DIRETORIO_PADRAO_TISS = './Downloads/Arquivos_padrao_TISS'
    private static final String CAMINHO_HISTORICO_CSV = './Downloads/Historico_TISS/historico_tiss.csv'
    private static final String DIRETORIO_TABELAS_RELACIONADAS = './Downloads/Tabelas_relacionadas'

    private static final YearMonth COMPETENCIA_MINIMA_PADRAO = YearMonth.of(2016, 1)

    private final HttpService httpService
    private final HistoricoExporter historicoExporter
    private final TissRegexParser regexParser
    private final TissHtmlParser htmlParser

    private Document paginaTissCache

    TissCrawler(
            HttpService httpService = new HttpService(),
            HistoricoExporter historicoExporter = new CsvHistoricoExporter(),
            TissRegexParser regexParser = new TissRegexParser(),
            TissHtmlParser htmlParser = null
    ) {
        this.httpService = httpService
        this.historicoExporter = historicoExporter
        this.regexParser = regexParser
        this.htmlParser = htmlParser ?: new TissHtmlParser(this.regexParser)
    }

    File baixarComponenteComunicacaoVigente() {
        Document paginaTiss = obterPaginaTiss()
        String urlVersao = htmlParser.extrairUrlVersaoVigente(paginaTiss)

        Document paginaVersao = httpService.obterDocumento(urlVersao)
        String urlComunicacao = htmlParser.extrairUrlComponenteComunicacao(paginaVersao)

        File arquivoBaixado = httpService.baixarArquivo(urlComunicacao, DIRETORIO_PADRAO_TISS)

        println 'Download do Componente de Comunicação concluído com sucesso.'
        return arquivoBaixado
    }

    File exportarHistoricoVersoes(YearMonth competenciaMinima = COMPETENCIA_MINIMA_PADRAO) {
        Document paginaTiss = obterPaginaTiss()
        String urlHistorico = htmlParser.extrairLinkPorTexto(paginaTiss, TEXTO_LINK_HISTORICO)

        Document paginaHistorico = httpService.obterDocumento(urlHistorico)
        List<VersaoTiss> todasVersoes = htmlParser.extrairTabelaHistorico(paginaHistorico)
        List<VersaoTiss> versoesFiltradas = filtrarVersoesAPartirDe(todasVersoes, competenciaMinima)

        File arquivoCsv = historicoExporter.exportar(versoesFiltradas, CAMINHO_HISTORICO_CSV)

        println 'Exportação do histórico de versões concluída com sucesso.'
        return arquivoCsv
    }

    File baixarTabelaErrosEnvio() {
        Document paginaTiss = obterPaginaTiss()
        String urlTabelas = htmlParser.extrairLinkPorTexto(paginaTiss, TEXTO_LINK_TABELAS)

        Document paginaTabelas = httpService.obterDocumento(urlTabelas)
        String urlErros = htmlParser.extrairLinkPorTexto(paginaTabelas, TEXTO_LINK_ERROS)

        File arquivoBaixado = httpService.baixarArquivo(urlErros, DIRETORIO_TABELAS_RELACIONADAS)

        println 'Download da Tabela de Erros concluído com sucesso.'
        return arquivoBaixado
    }

    List<VersaoTiss> filtrarVersoesAPartirDe(List<VersaoTiss> versoes, YearMonth competenciaMinima) {
        return versoes.findAll { VersaoTiss versao ->
            YearMonth competenciaVersao = regexParser.converterCompetenciaParaAnoMes(versao.competencia)
            !competenciaVersao.isBefore(competenciaMinima)
        }
    }

    private Document obterPaginaTiss() {
        if (paginaTissCache == null) {
            paginaTissCache = navegarAtePaginaTiss()
        }

        return paginaTissCache
    }

    private Document navegarAtePaginaTiss() {
        Document paginaAns = httpService.obterDocumento(URL_ANS)
        String urlPrestador = htmlParser.extrairLinkPorTexto(paginaAns, TEXTO_LINK_PRESTADOR)

        Document paginaPrestador = httpService.obterDocumento(urlPrestador)
        String urlTiss = htmlParser.extrairLinkPorTexto(paginaPrestador, TEXTO_LINK_TISS)

        return httpService.obterDocumento(urlTiss)
    }
}
