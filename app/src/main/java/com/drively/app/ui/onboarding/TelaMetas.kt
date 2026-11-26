package com.drively.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drively.app.core.calc.Calculadora
import com.drively.app.core.model.Meta
import com.drively.app.data.remote.DrivelyRepository
import com.drively.app.ui.components.*
import com.drively.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TelaMetas(onSalvo: () -> Unit) {
    var mensal by remember { mutableStateOf("5000") }
    var dias by remember { mutableStateOf(5) }
    var horas by remember { mutableStateOf(8) }
    var carregando by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val meta by remember { derivedStateOf { Meta(lucroMensal = mensal.toDoubleOrNull() ?: 5000.0, diasPorSemana = dias, horasPorDia = horas) } }
    val porHora = Calculadora.metaPorHora(meta)
    val porDia  = meta.lucroMensal / (dias * 4.33)

    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Spacer(Modifier.height(16.dp))
        IndicadorPassos(4, 3, Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(24.dp))
        LabelSecao("passo 3 de 3 · último!")
        Spacer(Modifier.height(8.dp))
        Text("Sua meta mensal", color = TextHigh, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Define o alvo do semáforo HUD.", color = TextMid, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))

        Text("Meta de lucro líquido mensal", color = TextLow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        TextField(mensal, { mensal = it }, singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = TextHigh, fontSize = 20.sp, fontWeight = FontWeight.Bold),
            colors = TextFieldDefaults.colors(focusedContainerColor = Surface2, unfocusedContainerColor = Surface2,
                focusedIndicatorColor = Blue, unfocusedIndicatorColor = Color.Transparent, cursorColor = Blue),
            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
            prefix = { Text("R$ ", color = TextMid, fontSize = 16.sp) })

        Spacer(Modifier.height(16.dp))
        LabelSecao("dias por semana"); Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf(3,4,5,6,7).forEach { d -> Chip("$d dias", dias == d) { dias = d } }
        }
        Spacer(Modifier.height(12.dp))
        LabelSecao("horas por dia"); Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf(6,8,10,12).forEach { h -> Chip("${h}h", horas == h) { horas = h } }
        }
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Surface1).padding(12.dp)) {
                Text("R$%.0f/dia".format(porDia), color = BlueLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Meta diária", color = TextMid, fontSize = 10.sp)
            }
            Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Surface1).padding(12.dp)) {
                Text("R$%.2f/h".format(porHora), color = Green, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Alvo HUD", color = TextMid, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(11.dp)).background(Surface1).padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("🟢 >R$%.0f/h".format(porHora), color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("🟡 R$%.0f–%.0f".format(porHora*.7, porHora), color = Amber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("🔴 <R$%.0f/h".format(porHora*.7), color = Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        if (erro.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text(erro, color = Red, fontSize = 12.sp) }

        Spacer(Modifier.height(16.dp))
        BotaoPrimario("Começar a usar o Drively", carregando = carregando) {
            scope.launch {
                carregando = true
                val ok = DrivelyRepository.salvarMeta(meta)
                if (ok) onSalvo() else erro = "Erro ao salvar. Verifique sua conexão."
                carregando = false
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
