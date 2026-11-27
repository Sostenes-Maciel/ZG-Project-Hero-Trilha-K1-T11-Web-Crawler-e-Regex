package br.com.zg.crawler.exporter

import br.com.zg.crawler.model.VersaoTiss
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class CsvHistoricoExporterTest {

    @TempDir
    Path diretorioTemporario

    @Test
    void deveExportarListaDeVersoesParaArquivoCsv() {
        CsvHistoricoExporter exporter = new CsvHistoricoExporter()
        List<VersaoTiss> versoes = [
                new VersaoTiss('jan/2016', '15/01/2016', '15/01/2016'),
                new VersaoTiss('set/2026', '30/09/2026', '01/10/2026')
        ]

        File destino = diretorioTemporario.resolve('historico_tiss.csv').toFile()
        File arquivoGerado = exporter.exportar(versoes, destino.absolutePath)

        assertTrue(arquivoGerado.exists())

        List<String> linhas = arquivoGerado.readLines('UTF-8')
        assertEquals(3, linhas.size())
        assertEquals('Competência,Publicação,Início de Vigência', linhas[0])
        assertEquals('jan/2016,15/01/2016,15/01/2016', linhas[1])
        assertEquals('set/2026,30/09/2026,01/10/2026', linhas[2])
    }
}
