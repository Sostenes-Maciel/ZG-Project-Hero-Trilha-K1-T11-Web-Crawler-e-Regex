package br.com.zg.crawler.exporter

import br.com.zg.crawler.model.VersaoTiss

interface HistoricoExporter {

    File exportar(List<VersaoTiss> versoes, String caminho)

}
