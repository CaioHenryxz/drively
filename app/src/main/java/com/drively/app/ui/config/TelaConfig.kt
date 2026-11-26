package com.drively.app.ui.config

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drively.app.data.remote.DrivelyRepository
import com.drively.app.ui.components.BotaoSecundario
import com.drively.app.ui.components.CardDrively
import com.drively.app.ui.components.LabelSecao
import com.drively.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TelaConfig(onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var acessibilidadeAtiva by remember { mutableStateOf(isAcessibilidadeAtiva(context)) }
    val temOverlay = Settings.canDrawOverlays(context)

    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Spacer(Modifier.height(52.dp))
        Text("Configurações", color = TextHigh, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        // Status do serviço — mais importante
        LabelSecao("STATUS DO COPILOTO")
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(if(acessibilidadeAtiva) Color(0xFF052E16) else Color(0xFF2D0707))
            .padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if(acessibilidadeAtiva)"🟢" else "🔴", fontSize=22.sp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(if(acessibilidadeAtiva)"Copiloto ativo" else "Copiloto inativo",
                    color=if(acessibilidadeAtiva)Green else Red, fontSize=14.sp, fontWeight=FontWeight.Bold)
                Text(if(acessibilidadeAtiva)"Monitorando Uber · 99 · iFood" else "Toque Ativar para ligar",
                    color=TextMid, fontSize=11.sp)
            }
            if (!acessibilidadeAtiva) {
                Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Blue)
                    .clickable {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
                    }.padding(horizontal=12.dp, vertical=7.dp)) {
                    Text("Ativar", color=Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        LabelSecao("PERMISSÃO OVERLAY")
        Spacer(Modifier.height(8.dp))
        CardDrively {
            Row(Modifier.fillMaxWidth().clickable {
                if (!temOverlay) context.startActivity(
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply { flags=Intent.FLAG_ACTIVITY_NEW_TASK })
            }, verticalAlignment=Alignment.CenterVertically) {
                Text(if(temOverlay)"✅" else "⚠️", fontSize=20.sp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Sobrepor outros apps", color=TextHigh, fontSize=13.sp, fontWeight=FontWeight.Medium)
                    Text(if(temOverlay)"Ativo — HUD funciona" else "Necessário para o HUD",
                        color=if(temOverlay)Green else Amber, fontSize=11.sp)
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        LabelSecao("CONTA")
        Spacer(Modifier.height(8.dp))
        CardDrively {
            val user = DrivelyRepository.usuarioAtual()
            Text("E-mail", color=TextMid, fontSize=11.sp)
            Text(user?.email ?: "—", color=TextHigh, fontSize=13.sp, fontWeight=FontWeight.Medium)
        }

        Spacer(Modifier.height(16.dp))
        BotaoSecundario("Sair da conta") {
            scope.launch { DrivelyRepository.logout(); onLogout() }
        }
        Spacer(Modifier.height(80.dp))
    }
}

private fun isAcessibilidadeAtiva(ctx: Context): Boolean = try {
    val enabled = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: ""
    enabled.contains(ctx.packageName, ignoreCase=true)
} catch(e: Exception) { false }
