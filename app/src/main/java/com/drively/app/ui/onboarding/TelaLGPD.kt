package com.drively.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drively.app.ui.components.BotaoPrimario
import com.drively.app.ui.components.IndicadorPassos
import com.drively.app.ui.components.LabelSecao
import com.drively.app.ui.theme.*

@Composable
fun TelaLGPD(onAceitar: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        IndicadorPassos(4, 2, Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(24.dp))

        LabelSecao("passo 3 de 4 · privacidade")
        Spacer(Modifier.height(8.dp))
        Text("O que o Drively lê", color = TextHigh, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            "Transparência total — veja exatamente o que acessamos no seu celular antes de continuar.",
            color = TextMid, fontSize = 13.sp, lineHeight = 19.sp
        )
        Spacer(Modifier.height(20.dp))

        // O que lê
        LabelSecao("o que o drively lê", cor = Green)
        Spacer(Modifier.height(8.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF052E16).copy(.6f))
                .padding(0.dp)
        ) {
            ItemLGPD(
                cor = Green,
                titulo = "Notificações dos apps de corrida",
                desc = "Uber, 99, iFood — só para calcular seu lucro líquido em tempo real",
                isLast = false
            )
            ItemLGPD(
                cor = Green,
                titulo = "Conteúdo da tela (Acessibilidade)",
                desc = "Lemos os valores da oferta que aparecem na tela do Uber/iFood — distância, valor, tempo. Nada mais.",
                isLast = false
            )
            ItemLGPD(
                cor = Green,
                titulo = "Localização aproximada",
                desc = "Para identificar sua cidade e calibrar o mapa de calor regional",
                isLast = true
            )
        }

        Spacer(Modifier.height(16.dp))

        // O que NÃO lê
        LabelSecao("o que nunca tocamos", cor = Red)
        Spacer(Modifier.height(8.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF2D0707).copy(.5f))
                .padding(0.dp)
        ) {
            ItemLGPD(cor = Red, titulo = "Mensagens, WhatsApp ou e-mails", desc = "Zero acesso a comunicações pessoais", isLast = false)
            ItemLGPD(cor = Red, titulo = "Senhas ou dados bancários", desc = "Nunca lemos dados financeiros seus", isLast = false)
            ItemLGPD(cor = Red, titulo = "Conversas com passageiros ou clientes", desc = "Não acessamos o chat dos apps", isLast = false)
            ItemLGPD(cor = Red, titulo = "Câmera ou microfone", desc = "Não usamos câmera nem gravamos áudio", isLast = true)
        }

        Spacer(Modifier.height(16.dp))

        // Nota LGPD
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A2E50))
                .padding(14.dp)
        ) {
            Text("🔒", fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "Protegido pela LGPD",
                    color = Color(0xFF7EC8FF), fontSize = 13.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    "Conforme a Lei 13.709/2018, você pode revogar o acesso quando quiser em Configurações → Privacidade.",
                    color = TextMid, fontSize = 11.sp, lineHeight = 17.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        BotaoPrimario("Entendi e aceito") { onAceitar() }
        Spacer(Modifier.height(8.dp))
        Text(
            "Ao continuar, você concorda com nossa Política de Privacidade.",
            color = TextLow, fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ItemLGPD(cor: Color, titulo: String, desc: String, isLast: Boolean) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(7.dp)
                    .clip(RoundedCornerShape(3.5.dp))
                    .background(cor)
            )
            Spacer(Modifier.width(8.dp))
            Text(titulo, color = TextHigh, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(3.dp))
        Text(desc, color = TextMid, fontSize = 11.sp, lineHeight = 16.sp,
            modifier = Modifier.padding(start = 15.dp))
    }
    if (!isLast) {
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(Color.White.copy(.06f)))
    }
}
