package com.drively.app.ui.historico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drively.app.core.model.Corrida
import com.drively.app.data.remote.DrivelyRepository
import com.drively.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TelaHistorico() {
    var corridas by remember { mutableStateOf<List<Corrida>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch { corridas = DrivelyRepository.carregarCorridas() }
    }

    Column(Modifier.fillMaxSize().background(Bg)) {
        Spacer(Modifier.height(52.dp))
        Text("Histórico", color = TextHigh, fontSize = 24.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(14.dp))

        if (corridas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Nenhuma corrida ainda", color = TextMid, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(corridas) { c ->
                    val corV = when(c.veredito) { "VERDE"->Green "AMARELO"->Amber else->Red }
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface1).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(
                            when(c.app){"UBER"->Color.White "NOVENTA_NOVE"->Color(0xFFFFD500) "IFOOD"->Color(0xFFEA1D2C) else->TextMid}))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(when(c.app){"UBER"->"Uber" "NOVENTA_NOVE"->"99" "IFOOD"->"iFood" else->c.app},
                                color=TextHigh, fontSize=13.sp, fontWeight=FontWeight.SemiBold)
                            Text("${"%.1f".format(c.distanciaKm)} km", color=TextMid, fontSize=11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("R\$ ${"%.2f".format(c.lucroLiquido)}", color=corV, fontSize=14.sp, fontWeight=FontWeight.Bold)
                            Text("R\$ ${"%.2f".format(c.valorBruto)} bruto", color=TextLow, fontSize=10.sp)
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
