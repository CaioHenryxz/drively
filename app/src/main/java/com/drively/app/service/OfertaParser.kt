package com.drively.app.service

import com.drively.app.core.model.AppCorrida
import com.drively.app.core.model.Oferta

object OfertaParser {
    private val VALOR  = Regex("""R\$\s*([\d.,]+)""")
    private val KM     = Regex("""([\d.,]+)\s*km""", RegexOption.IGNORE_CASE)
    private val M      = Regex("""([\d.,]+)\s*m\b""", RegexOption.IGNORE_CASE)
    private val TEMPO  = Regex("""(\d+)\s*min""", RegexOption.IGNORE_CASE)
    private val NOTA   = Regex("""(\d+[.,]\d+)\s*\(""")
    private val SURGE  = Regex("""([\d.,]+)\s*x""", RegexOption.IGNORE_CASE)

    fun parse(pkg: String, textos: List<String>): Oferta? {
        val app = when { pkg.contains("ubercab")->"UBER"; pkg.contains("taxis99")->"99"; pkg.contains("ifood")->"IFOOD"; else->return null }
        val flat = textos.joinToString(" | ")
        return when (app) {
            "UBER"  -> parseUber(flat)
            "99"    -> parse99(flat)
            "IFOOD" -> parseIfood(flat, textos)
            else    -> null
        }
    }

    private fun parseUber(flat: String): Oferta? {
        if (!flat.contains("Aceitar", ignoreCase = true)) return null
        val valores = VALOR.findAll(flat).map { num(it.groupValues[1]) }.toList()
        val bruto   = valores.maxOrNull() ?: return null
        val kms     = KM.findAll(flat).map { num(it.groupValues[1]) }.toList()
        val tempos  = TEMPO.findAll(flat).map { it.groupValues[1].toIntOrNull() ?: 0 }.toList()
        val nota    = NOTA.find(flat)?.groupValues?.get(1)?.let { num(it) } ?: 0.0
        return Oferta(AppCorrida.UBER, bruto, kms.getOrElse(1){kms.getOrElse(0){0.0}}, kms.getOrElse(0){0.0}, tempos.getOrElse(1){0}, tempos.getOrElse(0){0}, nota)
    }

    private fun parse99(flat: String): Oferta? {
        if (!flat.contains("Aceitar por", ignoreCase = true)) return null
        val valores = VALOR.findAll(flat).map { num(it.groupValues[1]) }.toList()
        val bruto   = valores.maxOrNull() ?: return null
        val kms     = (KM.findAll(flat).map { num(it.groupValues[1]) } + M.findAll(flat).map { num(it.groupValues[1])/1000.0 }).sortedBy{it}.toList()
        val tempos  = TEMPO.findAll(flat).map { it.groupValues[1].toIntOrNull() ?: 0 }.toList()
        val surge   = SURGE.find(flat)?.groupValues?.get(1)?.let { num(it) } ?: 1.0
        return Oferta(AppCorrida.NOVENTA_NOVE, bruto, kms.getOrElse(1){kms.getOrElse(0){0.0}}, kms.getOrElse(0){0.0}, tempos.getOrElse(1){0}, tempos.getOrElse(0){0}, surgeMultiplier=surge)
    }

    private fun parseIfood(flat: String, textos: List<String>): Oferta? {
        if (!flat.contains("Aceitar", ignoreCase = true) && !flat.contains("Recusar", ignoreCase = true)) return null
        val valores = VALOR.findAll(flat).map { num(it.groupValues[1]) }.toList()
        val bruto   = valores.maxOrNull() ?: return null
        val kms     = KM.findAll(flat).map { num(it.groupValues[1]) }.toList()
        val tempos  = TEMPO.findAll(flat).map { it.groupValues[1].toIntOrNull() ?: 0 }.toList()
        val nome    = textos.firstOrNull { it.length in 3..40 && !it.contains(Regex("""R\$|\d+\s*min|km|Aceitar|Recusar""", RegexOption.IGNORE_CASE)) } ?: ""
        return Oferta(AppCorrida.IFOOD, bruto, kms.getOrElse(1){kms.getOrElse(0){0.0}}, kms.getOrElse(0){0.0}, tempos.getOrElse(1){0}, tempos.getOrElse(0){0}, nomeEstabelecimento=nome)
    }

    private fun num(s: String) = s.replace(",", ".").toDoubleOrNull() ?: 0.0
}
