package com.drively.app.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drively.app.ui.config.TelaConfig
import com.drively.app.ui.historico.TelaHistorico
import com.drively.app.ui.home.TelaHome
import com.drively.app.ui.theme.*

enum class Tab { HOME, HISTORICO, CONFIG }

@Composable
fun NavPrincipal(onLogout: () -> Unit) {
    var tab by remember { mutableStateOf(Tab.HOME) }
    Box(Modifier.fillMaxSize()) {
        when (tab) {
            Tab.HOME      -> TelaHome()
            Tab.HISTORICO -> TelaHistorico()
            Tab.CONFIG    -> TelaConfig(onLogout = onLogout)
        }
        Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Surface0),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            NavItem("🏠","Início",   tab==Tab.HOME)      { tab=Tab.HOME }
            NavItem("📋","Histórico",tab==Tab.HISTORICO) { tab=Tab.HISTORICO }
            NavItem("⚙️","Config",   tab==Tab.CONFIG)    { tab=Tab.CONFIG }
        }
    }
}

@Composable
private fun NavItem(emoji:String, label:String, ativo:Boolean, onClick:()->Unit) {
    Column(Modifier.clickable{onClick()}.padding(horizontal=24.dp, vertical=10.dp),
        horizontalAlignment=Alignment.CenterHorizontally) {
        Text(emoji, fontSize=22.sp)
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize=10.sp, fontWeight=if(ativo)FontWeight.Bold else FontWeight.Normal,
            color=if(ativo)Blue else TextMid)
    }
}
