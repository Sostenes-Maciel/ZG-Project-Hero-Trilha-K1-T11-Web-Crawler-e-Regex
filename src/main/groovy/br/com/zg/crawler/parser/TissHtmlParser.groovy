package br.com.zg.crawler.parser

import br.com.zg.crawler.model.VersaoTiss
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class TissHtmlParser {

    private final TissRegexParser regexParser

    TissHtmlParser(TissRegexParser regexParser = new TissRegexParser()) {
        this.regexParser = regexParser
    }

    String extrairLinkPorTexto(Document documento, String trechoTexto) {
        String trechoNormalizado = trechoTexto.toLowerCase()

        Element link = documento.select('a')
                .find { Element elemento ->
                    elemento.text().toLowerCase().contains(trechoNormalizado)
                }

        if (link == null) {
            throw new IllegalStateException("Link não encontrado para o texto: ${trechoTexto}")
        }

        return link.attr('abs:href')
    }

    String extrairUrlVersaoVigente(Document documentoTiss) {
        Element linkVersao = documentoTiss.select('a')
                .find { Element elemento ->
                    regexParser.contemPadraoVersaoVigente(elemento.text())
                }

        if (linkVersao == null) {
            throw new IllegalStateException('Link da versão vigente não encontrado.')
        }

        return linkVersao.attr('abs:href')
    }

    String extrairUrlComponenteComunicacao(Document documentoVersao) {
        Element linha = documentoVersao.select('table tr')
                .find { Element elementoLinha ->
                    elementoLinha.select('td').first()?.text()?.contains('Componente de Comunicação')
                }

        if (linha == null) {
            throw new IllegalStateException('Componente de Comunicação não encontrado.')
        }

        Element link = linha.select('a').first()

        if (link == null) {
            throw new IllegalStateException('Link do Componente de Comunicação não encontrado.')
        }

        return link.attr('abs:href')
    }

    List<VersaoTiss> extrairTabelaHistorico(Document documentoHistorico) {
        Element tabela = documentoHistorico.select('table').first()

        if (tabela == null) {
            throw new IllegalStateException('Tabela de histórico não encontrada.')
        }

        Map<String, Integer> indices = mapearIndicesColunas(tabela)
        int indiceCompetencia = indices.getOrDefault('competencia', 0)
        int indicePublicacao = indices.getOrDefault('publicacao', 1)
        int indiceInicioVigencia = indices.getOrDefault('inicioVigencia', 2)
        int indiceMaximo = Math.max(indiceCompetencia, Math.max(indicePublicacao, indiceInicioVigencia))

        List<VersaoTiss> versoes = []

        tabela.select('tr').each { Element linha ->
            Elements celulas = linha.select('td')

            if (celulas.size() <= indiceMaximo) {
                return
            }

            int deslocamento = 0
            String competencia = regexParser.extrairCompetencia(celulas.get(indiceCompetencia).text())

            if (!competencia && celulas.size() > indiceMaximo + 1) {
                competencia = regexParser.extrairCompetencia(celulas.get(indiceCompetencia + 1).text())
                if (competencia) {
                    deslocamento = 1
                }
            }

            if (!competencia) {
                return
            }

            String publicacao = regexParser.normalizarData(
                    celulas.get(indicePublicacao + deslocamento).text()
            )
            String inicioVigencia = regexParser.normalizarData(
                    celulas.get(indiceInicioVigencia + deslocamento).text()
            )

            versoes.add(new VersaoTiss(competencia, publicacao, inicioVigencia))
        }

        return versoes
    }

    Map<String, Integer> mapearIndicesColunas(Element tabela) {
        Map<String, Integer> indices = [
                competencia   : 0,
                publicacao    : 1,
                inicioVigencia: 2
        ]

        Element linhaCabecalho = tabela.select('thead tr').first() ?: tabela.select('tr').first()

        if (linhaCabecalho == null) {
            return indices
        }

        Elements colunasCabecalho = linhaCabecalho.select('th')

        colunasCabecalho.eachWithIndex { Element coluna, int indice ->
            String nome = coluna.text().toLowerCase()

            if (nome.contains('competência') || nome.contains('competencia')) {
                indices['competencia'] = indice
            } else if (nome.contains('publicação') || nome.contains('publicacao')) {
                indices['publicacao'] = indice
            } else if (nome.contains('vigência') || nome.contains('vigencia')) {
                indices['inicioVigencia'] = indice
            }
        }

        return indices
    }
}
