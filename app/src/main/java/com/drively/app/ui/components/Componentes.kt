package com.drively.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.drively.app.ui.theme.*

@Composable
fun BotaoPrimario(texto: String, modifier: Modifier = Modifier, carregando: Boolean = false, habilitado: Boolean = true, onClick: () -> Unit) {
    Box(modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(14.dp))
        .background(if (habilitado && !carregando) Blue else Surface2)
        .clickable(enabled = habilitado && !carregando) { onClick() },
        contentAlignment = Alignment.Center) {
        if (carregando) CircularProgressIndicator(color = TextHigh, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        else Text(texto, color = if (habilitado) Color.White else TextLow, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
fun BotaoSecundario(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(14.dp))
        .border(BorderStroke(1.dp, BorderMid), RoundedCornerShape(14.dp))
        .clickable { onClick() }, contentAlignment = Alignment.Center) {
        Text(texto, color = TextMid, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun CardDrively(modifier: Modifier = Modifier, conteudo: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface1)
        .border(BorderStroke(0.5.dp, BorderMid), RoundedCornerShape(14.dp)).padding(16.dp), content = conteudo)
}

@Composable
fun IndicadorPassos(total: Int, atual: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(total) { i ->
            Box(Modifier.height(3.dp).width(if (i == atual) 24.dp else 18.dp)
                .clip(RoundedCornerShape(2.dp)).background(if (i == atual) Blue else BorderMid))
        }
    }
}

@Composable
fun LabelSecao(texto: String, cor: Color = TextLow, modifier: Modifier = Modifier) =
    Text(texto.uppercase(), color = cor, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, modifier = modifier)

@Composable
fun Chip(texto: String, selecionado: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier.clip(RoundedCornerShape(20.dp))
        .background(if (selecionado) Blue.copy(.15f) else Surface2)
        .border(BorderStroke(0.5.dp, if (selecionado) Blue.copy(.4f) else BorderMid), RoundedCornerShape(20.dp))
        .clickable { onClick() }.padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center) {
        Text(texto, color = if (selecionado) BlueLight else TextMid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CampoTexto(label: String, valor: String, tipo: androidx.compose.ui.text.input.KeyboardType, onChange: (String) -> Unit) {
    Column {
        Text(label, color = TextLow, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        TextField(valor, onChange, singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = tipo),
            textStyle = androidx.compose.ui.text.TextStyle(color = TextHigh, fontSize = 14.sp, fontWeight = FontWeight.Medium),
            colors = TextFieldDefaults.colors(focusedContainerColor = Surface2, unfocusedContainerColor = Surface2,
                focusedIndicatorColor = Blue, unfocusedIndicatorColor = Color.Transparent, cursorColor = Blue),
            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
    }
}
