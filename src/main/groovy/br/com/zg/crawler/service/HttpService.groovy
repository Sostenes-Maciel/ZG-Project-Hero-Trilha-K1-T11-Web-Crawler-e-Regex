package br.com.zg.crawler.service

import groovyx.net.http.HttpBuilder
import org.jsoup.nodes.Document

class HttpService {

    Document getDocument(String url) {

        def http = HttpBuilder.configure {
            request.uri = url
        }

        return http.get(Document) {
        }
    }
}