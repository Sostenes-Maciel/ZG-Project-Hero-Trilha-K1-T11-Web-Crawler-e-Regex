package br.com.zg.crawler.parser

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.YearMonth

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue

class TissRegexParserTest {

    private TissRegexParser regexParser

    @BeforeEach
    void setUp() {
        regexParser = new TissRegexParser()
    }

    @Test
    void deveIdentificarLinkDaVersaoVigente() {
        assertTrue(regexParser.contemPadraoVersaoVigente('Clique aqui para acessar a versão Novembro/2025'))
        assertTrue(regexParser.contemPadraoVersaoVigente('Versão 04/2026'))
        assertFalse(regexParser.contemPadraoVersaoVigente('Clique aqui para acessar todas as versões'))
    }

    @Test
    void deveExtrairCompetenciaNormalizadaEmMinusculo() {
        assertEquals('jan/2016', regexParser.extrairCompetencia('Jan/2016'))
        assertEquals('set/2026', regexParser.extrairCompetencia('Competência: SET / 2026'))
        assertNull(regexParser.extrairCompetencia('Texto sem competência'))
    }

    @Test
    void deveNormalizarDataComZeroAEsquerda() {
        assertEquals('01/02/2026', regexParser.normalizarData('1/2/2026'))
        assertEquals('15/01/2016', regexParser.normalizarData(' 15/01/2016 '))
    }

    @Test
    void deveConverterCompetenciaParaYearMonth() {
        YearMonth resultado = regexParser.converterCompetenciaParaAnoMes('jan/2016')
        assertEquals(YearMonth.of(2016, 1), resultado)
    }

    @Test
    void deveLancarExcecaoParaCompetenciaInvalida() {
        assertThrows(IllegalArgumentException) {
            regexParser.converterCompetenciaParaAnoMes('invalida/2026')
        }
    }
}
