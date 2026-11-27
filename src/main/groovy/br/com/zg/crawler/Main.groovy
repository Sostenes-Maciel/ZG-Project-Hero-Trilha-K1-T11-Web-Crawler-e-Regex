package br.com.zg.crawler

import br.com.zg.crawler.service.TissCrawler

class Main {

    static void main(String[] args) {
        TissCrawler crawler = new TissCrawler()

        crawler.baixarComponenteComunicacaoVigente()
        crawler.exportarHistoricoVersoes()
        crawler.baixarTabelaErrosEnvio()
    }
}
