package com.drively.app.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TipoVeiculo  { CARRO, MOTO, BICICLETA }
enum class Combustivel  { GASOLINA, ETANOL, GNV, DIESEL }
enum class Veredito     { VERDE, AMARELO, VERMELHO }
enum class AppCorrida   { UBER, NOVENTA_NOVE, IFOOD, DESCONHECIDO }

@Serializable
data class Veiculo(
    val id: String? = null,
    @SerialName("user_id")           val userId: String = "",
    val tipo: String = "CARRO",
    val modelo: String = "",
    @SerialName("consumo_km_l")      val consumoKmL: Double = 12.0,
    val combustivel: String = "GASOLINA",
    @SerialName("preco_combustivel") val precoCombustivel: Double = 6.09,
    @SerialName("ipva_anual")        val ipvaAnual: Double = 0.0,
    @SerialName("seguro_mensal")     val seguroMensal: Double = 0.0,
    @SerialName("manutencao_mensal") val manutencaoMensal: Double = 0.0,
    @SerialName("parcela_mensal")    val parcelaMensal: Double = 0.0,
    @SerialName("km_por_mes")        val kmPorMes: Double = 3000.0
)

@Serializable
data class Meta(
    val id: String? = null,
    @SerialName("user_id")           val userId: String = "",
    @SerialName("lucro_mensal")      val lucroMensal: Double = 5000.0,
    @SerialName("dias_por_semana")   val diasPorSemana: Int = 5,
    @SerialName("horas_por_dia")     val horasPorDia: Int = 8
)

@Serializable
data class Corrida(
    val id: String? = null,
    @SerialName("user_id")               val userId: String = "",
    val app: String = "",
    @SerialName("valor_bruto")           val valorBruto: Double = 0.0,
    @SerialName("lucro_liquido")         val lucroLiquido: Double = 0.0,
    @SerialName("reais_por_km")          val reaisPorKm: Double = 0.0,
    @SerialName("reais_por_hora")        val reaisPorHora: Double = 0.0,
    @SerialName("distancia_km")          val distanciaKm: Double = 0.0,
    val veredito: String = "",
    @SerialName("nome_estabelecimento")  val nomeEstabelecimento: String = "",
    @SerialName("created_at")           val createdAt: String? = null
)

// Objeto local — oferta lida na tela (não vai pro banco diretamente)
data class Oferta(
    val app: AppCorrida,
    val valorBruto: Double,
    val distanciaCorridaKm: Double,
    val distanciaAteOrigemKm: Double,
    val tempoCorridaMin: Int,
    val tempoAteOrigemMin: Int,
    val notaPassageiro: Double = 0.0,
    val nomeEstabelecimento: String = "",
    val surgeMultiplier: Double = 1.0
)

data class ResultadoOferta(
    val lucroLiquido: Double,
    val reaisPorKm: Double,
    val reaisPorHora: Double,
    val veredito: Veredito,
    val distanciaTotalKm: Double,
    val custoEstimado: Double
)
