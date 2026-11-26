package br.com.zg.crawler.parser

import br.com.zg.crawler.model.VersaoTiss
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows

class TissHtmlParserTest {

    private TissHtmlParser htmlParser

    @BeforeEach
    void setUp() {
        htmlParser = new TissHtmlParser(new TissRegexParser())
    }

    @Test
    void deveExtrairLinkPorTexto() {
        String html = '''
            <html>
                <body>
                    <a href="https://www.gov.br/ans/pt-br/prestador">Espaço do Prestador de Serviços de Saúde</a>
                </body>
            </html>
        '''
        Document documento = Jsoup.parse(html, 'https://www.gov.br')

        String url = htmlParser.extrairLinkPorTexto(documento, 'Espaço do Prestador')
        assertEquals('https://www.gov.br/ans/pt-br/prestador', url)
    }

    @Test
    void deveLancarExcecaoQuandoLinkNaoForEncontrado() {
        Document documento = Jsoup.parse('<html><body></body></html>', 'https://www.gov.br')

        assertThrows(IllegalStateException) {
            htmlParser.extrairLinkPorTexto(documento, 'Link Inexistente')
        }
    }

    @Test
    void deveExtrairUrlDoComponenteDeComunicacao() {
        String html = '''
            <html>
                <body>
                    <table>
                        <tr>
                            <td>Componente de Comunicação</td>
                            <td><a href="/arquivos/PadraoTISSComunicacao.zip">Baixar</a></td>
                        </tr>
                    </table>
                </body>
            </html>
        '''
        Document documento = Jsoup.parse(html, 'https://www.gov.br')

        String url = htmlParser.extrairUrlComponenteComunicacao(documento)
        assertEquals('https://www.gov.br/arquivos/PadraoTISSComunicacao.zip', url)
    }

    @Test
    void deveExtrairTabelaDeHistoricoMapeandoColunasPeloCabecalho() {
        String html = '''
            <html>
                <body>
                    <table>
                        <thead>
                            <tr>
                                <th>Competência</th>
                                <th>Publicação</th>
                                <th>Início de Vigência</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Jan/2016</td>
                                <td>15/1/2016</td>
                                <td>15/01/2016</td>
                            </tr>
                            <tr>
                                <td>Set/2026</td>
                                <td>30/9/2026</td>
                                <td>1/10/2026</td>
                            </tr>
                        </tbody>
                    </table>
                </body>
            </html>
        '''
        Document documento = Jsoup.parse(html)

        List<VersaoTiss> versoes = htmlParser.extrairTabelaHistorico(documento)

        assertEquals(2, versoes.size())
        assertEquals(new VersaoTiss('jan/2016', '15/01/2016', '15/01/2016'), versoes[0])
        assertEquals(new VersaoTiss('set/2026', '30/09/2026', '01/10/2026'), versoes[1])
    }
}
