package br.com.zg.crawler.exporter

import br.com.zg.crawler.model.VersaoTiss

class CsvHistoricoExporter implements HistoricoExporter {

    private static final String CABECALHO_CSV = 'Competência,Publicação,Início de Vigência'
    private static final String CODIFICACAO_UTF8 = 'UTF-8'

    @Override
    File exportar(List<VersaoTiss> versoes, String caminho) {
        File arquivo = new File(caminho)

        if (arquivo.parentFile != null && !arquivo.parentFile.exists()) {
            arquivo.parentFile.mkdirs()
        }

        arquivo.withWriter(CODIFICACAO_UTF8) { BufferedWriter writer ->
            writer.writeLine(CABECALHO_CSV)

            versoes.each { VersaoTiss versao ->
                writer.writeLine(
                        "${versao.competencia},${versao.publicacao},${versao.inicioVigencia}"
                )
            }
        }

        println "Histórico salvo em: ${arquivo.absolutePath}"
        return arquivo
    }
}
