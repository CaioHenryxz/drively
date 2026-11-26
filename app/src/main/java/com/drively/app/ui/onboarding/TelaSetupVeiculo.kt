package com.drively.app.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drively.app.core.calc.Calculadora
import com.drively.app.core.model.*
import com.drively.app.data.remote.DrivelyRepository
import com.drively.app.ui.components.*
import com.drively.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TelaSetupVeiculo(tipo: TipoVeiculo, onSalvo: () -> Unit) {
    val ehMoto = tipo != TipoVeiculo.CARRO
    var modelo by remember { mutableStateOf(if (ehMoto) "Honda CG 160 2022" else "Chevrolet Onix 2021") }
    var consumo by remember { mutableStateOf(if (ehMoto) "38.0" else "12.4") }
    var comb by remember { mutableStateOf("GASOLINA") }
    var preco by remember { mutableStateOf("6.09") }
    var ipva by remember { mutableStateOf(if (ehMoto) "280" else "980") }
    var seguro by remember { mutableStateOf(if (ehMoto) "60" else "210") }
    var manut by remember { mutableStateOf(if (ehMoto) "80" else "180") }
    var parcela by remember { mutableStateOf(if (ehMoto) "320" else "890") }
    var kmMes by remember { mutableStateOf(if (ehMoto) "4800" else "3200") }
    var carregando by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val veiculo by remember { derivedStateOf {
        Veiculo(tipo=tipo.name, modelo=modelo, consumoKmL=consumo.toDoubleOrNull()?:1.0,
            combustivel=comb, precoCombustivel=preco.toDoubleOrNull()?:0.0,
            ipvaAnual=ipva.toDoubleOrNull()?:0.0, seguroMensal=seguro.toDoubleOrNull()?:0.0,
            manutencaoMensal=manut.toDoubleOrNull()?:0.0, parcelaMensal=parcela.toDoubleOrNull()?:0.0,
            kmPorMes=kmMes.toDoubleOrNull()?:1.0)
    }}
    val custo = Calculadora.custoPorKm(veiculo)

    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Spacer(Modifier.height(16.dp))
        IndicadorPassos(4, 1, Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(24.dp))
        LabelSecao("passo 2 de 3 · veículo")
        Spacer(Modifier.height(8.dp))
        Text(if(ehMoto)"Sua moto" else "Seu carro", color=TextHigh, fontSize=22.sp, fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Esses dados formam seu custo real por km.", color=TextMid, fontSize=12.sp)
        Spacer(Modifier.height(16.dp))

        CampoTexto("Modelo", modelo, KeyboardType.Text) { modelo = it }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) { CampoTexto("Consumo (km/L)", consumo, KeyboardType.Decimal) { consumo = it } }
            Box(Modifier.weight(1f)) { CampoTexto("Preço comb. R\$/L", preco, KeyboardType.Decimal) { preco = it } }
        }
        Spacer(Modifier.height(10.dp))
        LabelSecao("combustível"); Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Chip("Gasolina", comb=="GASOLINA") { comb="GASOLINA" }
            Chip("Etanol",   comb=="ETANOL")   { comb="ETANOL" }
            if (!ehMoto) Chip("GNV", comb=="GNV") { comb="GNV" }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) { CampoTexto("IPVA anual R\$", ipva, KeyboardType.Number) { ipva = it } }
            Box(Modifier.weight(1f)) { CampoTexto("Seguro/mês R\$", seguro, KeyboardType.Number) { seguro = it } }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) { CampoTexto("Manutenção/mês", manut, KeyboardType.Number) { manut = it } }
            Box(Modifier.weight(1f)) { CampoTexto("Parcela/aluguel", parcela, KeyboardType.Number) { parcela = it } }
        }
        Spacer(Modifier.height(10.dp))
        CampoTexto("Km rodados por mês", kmMes, KeyboardType.Number) { kmMes = it }
        Spacer(Modifier.height(16.dp))

        // Resultado ao vivo
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Green.copy(.08f))
            .border(BorderStroke(0.5.dp, Green.copy(.28f)), RoundedCornerShape(14.dp))
            .padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                LabelSecao("custo operacional", cor = Green)
                Spacer(Modifier.height(4.dp))
                Text("R\$ ${"%.2f".format(custo)}", color=Green, fontSize=26.sp, fontWeight=FontWeight.Bold)
                Text("por km rodado", color=Green.copy(.6f), fontSize=10.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Break-even", color=TextLow, fontSize=9.sp)
                Text("> R\$ ${"%.2f".format(custo*2)}/km", color=TextHigh, fontSize=13.sp, fontWeight=FontWeight.Bold)
            }
        }

        if (erro.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(erro, color = Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(16.dp))
        BotaoPrimario(if(ehMoto)"Salvar moto" else "Salvar veículo", carregando = carregando) {
            scope.launch {
                carregando = true
                val ok = DrivelyRepository.salvarVeiculo(veiculo)
                if (ok) onSalvo() else erro = "Erro ao salvar. Verifique sua conexão."
                carregando = false
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
