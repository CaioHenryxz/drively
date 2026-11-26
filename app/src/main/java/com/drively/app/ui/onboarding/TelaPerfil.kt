package com.drively.app.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.drively.app.core.model.TipoVeiculo
import com.drively.app.ui.components.*
import com.drively.app.ui.theme.*

@Composable
fun TelaPerfil(onContinuar: (TipoVeiculo) -> Unit) {
    var sel by remember { mutableStateOf(TipoVeiculo.CARRO) }
    Column(Modifier.fillMaxSize().background(Bg).padding(18.dp)) {
        Spacer(Modifier.height(16.dp))
        IndicadorPassos(4, 0, Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(24.dp))
        LabelSecao("passo 1 de 3")
        Spacer(Modifier.height(8.dp))
        Text("Como você trabalha?", color = TextHigh, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("Define quais apps monitoramos e como calculamos seu lucro.", color = TextMid, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        OpcaoPerfil("🚗","Motorista de carro","Uber · 99 · InDrive", sel==TipoVeiculo.CARRO, Blue.copy(.15f)) { sel=TipoVeiculo.CARRO }
        Spacer(Modifier.height(12.dp))
        OpcaoPerfil("🏍️","Moto · entregador","iFood · Uber Moto · 99 Moto", sel==TipoVeiculo.MOTO, Amber.copy(.15f)) { sel=TipoVeiculo.MOTO }
        Spacer(Modifier.height(12.dp))
        OpcaoPerfil("🚲","Bicicleta / e-bike","iFood", sel==TipoVeiculo.BICICLETA, Green.copy(.12f)) { sel=TipoVeiculo.BICICLETA }
        Spacer(Modifier.weight(1f))
        BotaoPrimario("Continuar") { onContinuar(sel) }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun OpcaoPerfil(emoji:String, titulo:String, apps:String, sel:Boolean, bg:Color, onClick:()->Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .background(if(sel) Blue.copy(.12f) else Surface1)
        .border(BorderStroke(if(sel)1.5.dp else 0.5.dp, if(sel) Blue.copy(.5f) else BorderMid), RoundedCornerShape(16.dp))
        .clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(bg), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 20.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, color = TextHigh, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(apps, color = TextMid, fontSize = 11.sp)
        }
        Box(Modifier.size(22.dp).clip(RoundedCornerShape(11.dp)).background(if(sel) Blue else BorderMid), contentAlignment = Alignment.Center) {
            if (sel) Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
