package br.com.zg.crawler.model

class VersaoTiss {

    String competencia
    String publicacao
    String inicioVigencia

    VersaoTiss(String competencia, String publicacao, String inicioVigencia) {
        this.competencia = competencia
        this.publicacao = publicacao
        this.inicioVigencia = inicioVigencia
    }

    @Override
    String toString() {
        return "${competencia} | ${publicacao} | ${inicioVigencia}"
    }
}