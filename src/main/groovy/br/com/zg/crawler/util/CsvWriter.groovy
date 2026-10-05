package br.com.zg.crawler.util

import br.com.zg.crawler.model.VersaoTiss

class CsvWriter {

    void salvarHistorico(List<VersaoTiss> versoes, String caminho) {

        File arquivo = new File(caminho)

        arquivo.parentFile.mkdirs()

        arquivo.withWriter('UTF-8') { writer ->

            writer.writeLine('Competência,Publicação,Início de Vigência')

            versoes.each { versao ->
                writer.writeLine(
                        "${versao.competencia},${versao.publicacao},${versao.inicioVigencia}"
                )
            }
        }

        println "Histórico salvo em: ${arquivo.absolutePath}"
    }
}