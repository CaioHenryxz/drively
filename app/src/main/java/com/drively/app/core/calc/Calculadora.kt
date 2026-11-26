package com.drively.app.core.calc

import com.drively.app.core.model.*

object Calculadora {
    fun custoPorKm(v: Veiculo): Double {
        val comb = if (v.consumoKmL > 0) v.precoCombustivel / v.consumoKmL else 0.0
        val fixo = (v.seguroMensal + v.manutencaoMensal + v.parcelaMensal + v.ipvaAnual / 12.0)
        return comb + if (v.kmPorMes > 0) fixo / v.kmPorMes else 0.0
    }

    fun metaPorHora(m: Meta): Double {
        val h = m.diasPorSemana * 4.33 * m.horasPorDia
        return if (h > 0) m.lucroMensal / h else 0.0
    }

    fun avaliar(o: Oferta, v: Veiculo, m: Meta): ResultadoOferta {
        val cpm   = custoPorKm(v)
        val dist  = o.distanciaAteOrigemKm + o.distanciaCorridaKm
        val custo = dist * cpm
        val lucro = o.valorBruto - custo
        val rKm   = if (dist  > 0) lucro / dist  else 0.0
        val horas = (o.tempoAteOrigemMin + o.tempoCorridaMin) / 60.0
        val rH    = if (horas > 0) lucro / horas else 0.0
        val meta  = metaPorHora(m)
        val ver   = when { lucro <= 0 -> Veredito.VERMELHO; rH >= meta -> Veredito.VERDE; else -> Veredito.AMARELO }
        return ResultadoOferta(lucro, rKm, rH, ver, dist, custo)
    }
}
