package br.com.zg.crawler.service

import br.com.zg.crawler.model.VersaoTiss
import br.com.zg.crawler.parser.TissHtmlParser
import br.com.zg.crawler.parser.TissRegexParser
import br.com.zg.crawler.util.CsvWriter
import org.jsoup.nodes.Document

import java.time.YearMonth

class TissCrawler {

    private final HttpService httpService
    private final CsvWriter csvWriter
    private final TissRegexParser regexParser
    private final TissHtmlParser htmlParser

    TissCrawler() {
        this.httpService = new HttpService()
        this.csvWriter = new CsvWriter()
        this.regexParser = new TissRegexParser()
        this.htmlParser = new TissHtmlParser(this.regexParser)
    }

    void executarTask1() {
        Document documentTiss = acessarPaginaTiss()
        String urlVersao = htmlParser.extrairUrlVersaoVigente(documentTiss)

        Document documentVersao = httpService.obterDocumento(urlVersao)
        String urlComunicacao = htmlParser.extrairUrlComponenteComunicacao(documentVersao)

        httpService.baixarArquivo(urlComunicacao, './Downloads/Aquivos_padrao_TISS')

        println "Task 1 concluída com sucesso."
    }

    private Document acessarPaginaTiss() {
        String urlAns = 'https://www.gov.br/ans/pt-br'
        Document documentAns = httpService.obterDocumento(urlAns)

        String urlPrestador = htmlParser.extrairLinkPorTexto(
                documentAns,
                'Espaço do Prestador de Serviços de Saúde'
        )
        Document documentPrestador = httpService.obterDocumento(urlPrestador)

        String urlTiss = htmlParser.extrairLinkPorTexto(
                documentPrestador,
                'TISS - Padrão para Troca de Informação de Saúde Suplementar'
        )

        return httpService.obterDocumento(urlTiss)
    }

    void executarTask2() {
        Document documentTiss = acessarPaginaTiss()
        String urlHistorico = htmlParser.extrairLinkPorTexto(documentTiss, 'todas as versões')

        Document documentHistorico = httpService.obterDocumento(urlHistorico)
        List<VersaoTiss> versoes = htmlParser.extrairTabelaHistorico(documentHistorico)
                .findAll { VersaoTiss versao ->
                    !regexParser.converterCompetenciaParaAnoMes(versao.competencia)
                            .isBefore(YearMonth.of(2016, 1))
                }

        String caminhoCsv = './Downloads/Historico_TISS/historico_tiss.csv'
        csvWriter.salvarHistorico(versoes, caminhoCsv)

        println "Task 2 concluída com sucesso."
    }

    void executarTask3() {
        Document documentTiss = acessarPaginaTiss()
        String urlTabelas = htmlParser.extrairLinkPorTexto(documentTiss, 'acessar as planilhas')

        Document documentTabelas = httpService.obterDocumento(urlTabelas)
        String urlErros = htmlParser.extrairLinkPorTexto(
                documentTabelas,
                'tabela de erros no envio para a ans'
        )

        httpService.baixarArquivo(urlErros, './Downloads/Tabelas_relacionadas')

        println "Task 3 concluída com sucesso."
    }
}
