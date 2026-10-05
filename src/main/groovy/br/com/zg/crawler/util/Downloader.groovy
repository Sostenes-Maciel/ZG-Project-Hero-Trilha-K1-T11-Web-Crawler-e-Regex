package br.com.zg.crawler.util

import groovyx.net.http.HttpBuilder
import groovyx.net.http.optional.Download

class Downloader {

    void baixar(String url, String caminho) {

        File arquivo = new File(caminho)

        arquivo.parentFile.mkdirs()

        HttpBuilder.configure {
            request.uri = url
        }.get {
            Download.toFile(delegate, arquivo)
        }

        println "Arquivo baixado: ${arquivo.absolutePath}"
    }
}