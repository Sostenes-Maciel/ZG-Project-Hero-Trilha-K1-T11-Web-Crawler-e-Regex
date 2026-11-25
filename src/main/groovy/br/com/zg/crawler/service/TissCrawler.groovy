package br.com.zg.crawler.service

import br.com.zg.crawler.model.VersaoTiss
import br.com.zg.crawler.parser.TissRegexParser
import br.com.zg.crawler.util.CsvWriter
import br.com.zg.crawler.util.Downloader
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.time.YearMonth

class TissCrawler {

    private final HttpService httpService
    private final Downloader downloader
    private final CsvWriter csvWriter
    private final TissRegexParser regexParser

    TissCrawler() {
        this.httpService = new HttpService()
        this.downloader = new Downloader()
        this.csvWriter = new CsvWriter()
        this.regexParser = new TissRegexParser()
    }

    void executarTask1() {

        Document documentTiss = acessarPaginaTiss()
        String urlVersao = buscarUrlVersaoVigente(documentTiss)

        Document documentVersao = httpService.getDocument(urlVersao)

        Element linhaComunicacao = buscarLinhaComunicacao(documentVersao)

        String urlComunicacao = linhaComunicacao.select('a').first().attr('abs:href')

        String nomeArquivo = urlComunicacao.tokenize('/').last()

        String caminho = "./Downloads/Aquivos_padrao_TISS/${nomeArquivo}"

        downloader.baixar(urlComunicacao, caminho)

        println "Task 1 concluída com sucesso."
    }

    private Document acessarPaginaTiss() {

        String urlAns = 'https://www.gov.br/ans/pt-br'

        Document documentAns = httpService.getDocument(urlAns)

        Element linkPrestador = documentAns.select('a')
                .find { it.text().contains('Espaço do Prestador de Serviços de Saúde') }

        String urlPrestador = linkPrestador.attr('abs:href')

        Document documentPrestador = httpService.getDocument(urlPrestador)

        Element linkTiss = documentPrestador.select('a')
                .find { it.text().contains('TISS - Padrão para Troca de Informação de Saúde Suplementar') }

        String urlTiss = linkTiss.attr('abs:href')

        return httpService.getDocument(urlTiss)
    }

    private String buscarUrlVersaoVigente(Document documentTiss) {

        Element linkVersao = documentTiss.select('a')
                .find { Element elemento ->
                    regexParser.contemPadraoVersaoVigente(elemento.text())
                }

        if (!linkVersao) {
            throw new IllegalStateException('Link da versão vigente não encontrado.')
        }

        return linkVersao.attr('abs:href')
    }

    private Element buscarLinhaComunicacao(Document documentVersao) {

        Element linha = documentVersao.select('table tr')
                .find {
                    it.select('td').first()?.text()?.contains('Componente de Comunicação')
                }

        if (!linha) {
            throw new IllegalStateException('Componente de Comunicação não encontrado.')
        }

        return linha
    }

    void executarTask2() {

        Document documentTiss = acessarPaginaTiss()

        Element linkHistorico = documentTiss.select('a')
                .find {
                    it.text().contains('todas as versões')
                }

        if (!linkHistorico) {
            throw new IllegalStateException('Link do histórico não encontrado.')
        }

        String urlHistorico = linkHistorico.attr('abs:href')

        Document documentHistorico = httpService.getDocument(urlHistorico)

        List<VersaoTiss> versoes = extrairHistorico(documentHistorico)

        String caminhoCsv = './Downloads/Historico_TISS/historico_tiss.csv'

        csvWriter.salvarHistorico(versoes, caminhoCsv)

        println "Task 2 concluída com sucesso."
    }
    private List<VersaoTiss> extrairHistorico(Document documentHistorico) {

        List<VersaoTiss> versoes = []

        documentHistorico.select('table tr').each { Element linha ->

            Elements celulas = linha.select('td')

            if (celulas.size() < 3) {
                return
            }

            String competencia = regexParser.extrairCompetencia(celulas[0].text())

            if (!competencia && celulas.size() > 1) {
                competencia = regexParser.extrairCompetencia(celulas[1].text())
            }

            if (!competencia) {
                return
            }

            if (regexParser.converterCompetenciaParaAnoMes(competencia).isBefore(YearMonth.of(2016, 1))) {
                return
            }

            String publicacao = regexParser.normalizarData(celulas[1].text())
            String inicioVigencia = regexParser.normalizarData(celulas[2].text())

            versoes.add(
                    new VersaoTiss(
                            competencia,
                            publicacao,
                            inicioVigencia
                    )
            )
        }

        return versoes
    }

    void executarTask3() {

        Document documentTiss = acessarPaginaTiss()

        Element linkTabelas = documentTiss.select('a')
                .find {
                    it.text().contains('acessar as planilhas')
                }

        if (!linkTabelas) {
            throw new IllegalStateException('Link das tabelas relacionadas não encontrado.')
        }

        String urlTabelas = linkTabelas.attr('abs:href')

        Document documentTabelas = httpService.getDocument(urlTabelas)

        Element linkErros = documentTabelas.select('a')
                .find {
                    it.text().toLowerCase().contains('tabela de erros no envio para a ans')
                }

        if (!linkErros) {
            throw new IllegalStateException('Tabela de erros no envio para a ANS não encontrada.')
        }

        String urlErros = linkErros.attr('abs:href')

        String nomeArquivo = urlErros.tokenize('/').last()

        String caminho = "./Downloads/Tabelas_relacionadas/${nomeArquivo}"

        downloader.baixar(urlErros, caminho)

        println "Task 3 concluída com sucesso."
    }

}