package br.com.zg.crawler.service

import groovyx.net.http.HttpBuilder
import groovyx.net.http.optional.Download
import org.jsoup.nodes.Document

class HttpService {

    Document obterDocumento(String url) {
        HttpBuilder http = HttpBuilder.configure {
            request.uri = url
        }

        return http.get(Document) {
        }
    }

    File baixarArquivo(String url, String diretorioDestino) {
        String nomeArquivo = extrairNomeArquivo(url)
        File diretorio = new File(diretorioDestino)

        if (!diretorio.exists()) {
            diretorio.mkdirs()
        }

        File arquivoDestino = new File(diretorio, nomeArquivo)

        HttpBuilder.configure {
            request.uri = url
        }.get {
            Download.toFile(delegate, arquivoDestino)
        }

        println "Arquivo baixado: ${arquivoDestino.absolutePath}"
        return arquivoDestino
    }

    private String extrairNomeArquivo(String url) {
        List<String> partes = url.tokenize('/')

        if (partes.isEmpty()) {
            throw new IllegalArgumentException("Não foi possível extrair o nome do arquivo da URL: ${url}")
        }

        return partes.last()
    }
}
