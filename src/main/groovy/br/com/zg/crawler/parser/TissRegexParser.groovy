package br.com.zg.crawler.parser

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher
import java.util.regex.Pattern

class TissRegexParser {

    private static final Pattern PADRAO_COMPETENCIA =
            ~/(?i)\b(jan|fev|mar|abr|mai|jun|jul|ago|set|out|nov|dez)\s*\/\s*(\d{4})\b/

    private static final Pattern PADRAO_DATA =
            ~/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/

    private static final Pattern PADRAO_LINK_VERSAO =
            ~/(?i)versão\s+.+\/\d{4}/

    private static final DateTimeFormatter FORMATADOR_MES_ANO =
            DateTimeFormatter.ofPattern('MM/yyyy')

    private static final Map<String, String> MESES = [
            jan: '01',
            fev: '02',
            mar: '03',
            abr: '04',
            mai: '05',
            jun: '06',
            jul: '07',
            ago: '08',
            set: '09',
            out: '10',
            nov: '11',
            dez: '12'
    ].asImmutable()

    boolean contemPadraoVersaoVigente(String texto) {
        if (!texto) {
            return false
        }

        Matcher matcher = PADRAO_LINK_VERSAO.matcher(texto)
        return matcher.find()
    }

    String extrairCompetencia(String texto) {
        if (!texto) {
            return null
        }

        Matcher matcher = PADRAO_COMPETENCIA.matcher(texto)

        if (matcher.find()) {
            String mes = matcher.group(1).toLowerCase()
            String ano = matcher.group(2)
            return "${mes}/${ano}"
        }

        return null
    }

    String normalizarData(String data) {
        if (!data) {
            return ''
        }

        String dataLimpa = data.trim()
        Matcher matcher = PADRAO_DATA.matcher(dataLimpa)

        if (matcher.find()) {
            String dia = matcher.group(1).padLeft(2, '0')
            String mes = matcher.group(2).padLeft(2, '0')
            String ano = matcher.group(3)

            return "${dia}/${mes}/${ano}"
        }

        return dataLimpa
    }

    YearMonth converterCompetenciaParaAnoMes(String competencia) {
        String competenciaExtraida = extrairCompetencia(competencia)

        if (!competenciaExtraida) {
            throw new IllegalArgumentException("Competência inválida: ${competencia}")
        }

        String[] partes = competenciaExtraida.split('/')
        String mesNumero = MESES[partes[0]]
        String ano = partes[1]

        return YearMonth.parse("${mesNumero}/${ano}", FORMATADOR_MES_ANO)
    }
}
