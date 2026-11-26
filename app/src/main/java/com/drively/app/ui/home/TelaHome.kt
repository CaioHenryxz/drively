package com.drively.app.ui.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drively.app.core.model.Meta
import com.drively.app.data.remote.DrivelyRepository
import com.drively.app.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TelaHome() {
    val context   = LocalContext.current
    val scope     = rememberCoroutineScope()

    // ── estado ────────────────────────────────────────────────────────────────
    var lucroHoje    by remember { mutableStateOf(0.0) }
    var lucroMes     by remember { mutableStateOf(0.0) }
    var qtdHoje      by remember { mutableStateOf(0) }
    var meta         by remember { mutableStateOf<Meta?>(null) }
    var carregando   by remember { mutableStateOf(true) }

    // verifica acessibilidade ao recompor (usuário pode ativar e voltar)
    var hudAtivo by remember { mutableStateOf(isAcessibilidadeAtiva(context)) }

    LaunchedEffect(Unit) {
        scope.launch {
            lucroHoje  = DrivelyRepository.lucroHoje()
            lucroMes   = DrivelyRepository.lucroMes()
            qtdHoje    = DrivelyRepository.corridasHoje()
            meta       = DrivelyRepository.carregarMeta()
            carregando = false
        }
    }

    // re-checa quando o app volta ao foco
    DisposableEffect(Unit) {
        onDispose { }
    }

    // ── metas derivadas ───────────────────────────────────────────────────────
    val metaMensal = meta?.lucroMensal ?: 0.0
    val metaDiaria = if ((meta?.diasPorSemana ?: 0) > 0)
        metaMensal / ((meta?.diasPorSemana ?: 5) * 4.33) else 0.0

    val progDia = if (metaDiaria > 0) (lucroHoje / metaDiaria).coerceIn(0.0, 1.0).toFloat() else 0f
    val progMes = if (metaMensal > 0) (lucroMes  / metaMensal ).coerceIn(0.0, 1.0).toFloat() else 0f

    val animDia by animateFloatAsState(progDia, tween(900), label = "dia")
    val animMes by animateFloatAsState(progMes, tween(900), label = "mes")

    fun corProg(p: Float) = when { p >= .8f -> Green; p >= .4f -> Amber; else -> Blue }

    val mesAtual = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale("pt","BR"))
        .replaceFirstChar { it.uppercase() }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(52.dp))
        Text("Hoje", color = TextMid, fontSize = 12.sp)
        Text("Seu dia", color = TextHigh, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // ── card de aviso: HUD inativo ────────────────────────────────────────
        if (!hudAtivo) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1C1407))
                    .clickable {
                        context.startActivity(
                            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        )
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⚠️", fontSize = 22.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Copiloto inativo",
                        color = Amber, fontSize = 13.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "O HUD não está lendo as ofertas. Toque aqui para ativar o Drively em Acessibilidade.",
                        color = TextMid, fontSize = 11.sp, lineHeight = 16.sp
                    )
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Amber.copy(.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Ativar", color = Amber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── hero card ─────────────────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF1A3680), Color(0xFF0E1D4A))))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    "LUCRO LÍQUIDO DO DIA",
                    color = Color(0xFF7EC8FF).copy(.65f),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "R\$ ${"%.2f".format(lucroHoje)}",
                    color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "$qtdHoje corrida${if (qtdHoje != 1) "s" else ""} hoje",
                    color = Color(0xFF7EC8FF).copy(.55f), fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── progress: meta diária ─────────────────────────────────────────────
        ProgressCard(
            titulo   = "Meta diária",
            atual    = lucroHoje,
            alvo     = metaDiaria,
            progAnim = animDia,
            cor      = corProg(animDia),
            sufixo   = "hoje"
        )

        Spacer(Modifier.height(10.dp))

        // ── progress: meta mensal ─────────────────────────────────────────────
        ProgressCard(
            titulo   = "Meta mensal — $mesAtual",
            atual    = lucroMes,
            alvo     = metaMensal,
            progAnim = animMes,
            cor      = corProg(animMes),
            sufixo   = "de R\$ ${"%.0f".format(metaMensal)}"
        )

        // ── estado vazio ──────────────────────────────────────────────────────
        if (!carregando && qtdHoje == 0) {
            Spacer(Modifier.height(28.dp))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🚗", fontSize = 40.sp)
                Spacer(Modifier.height(12.dp))
                Text("Nenhuma corrida hoje ainda",
                    color = TextMid, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Abra o Uber ou iFood.\nO Drively analisa automaticamente.",
                    color = TextLow, fontSize = 12.sp,
                    textAlign = TextAlign.Center, lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ProgressCard(
    titulo: String, atual: Double, alvo: Double,
    progAnim: Float, cor: Color, sufixo: String
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF111929))
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom) {
            Text(titulo, color = TextMid, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("${(progAnim * 100).toInt()}%",
                color = cor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(8.dp)
            .clip(RoundedCornerShape(4.dp)).background(Color.White.copy(.08f))) {
            Box(Modifier.fillMaxWidth(progAnim).height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.horizontalGradient(listOf(cor, cor.copy(.6f)))))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("R\$ ${"%.2f".format(atual)} $sufixo",
                color = cor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            if (alvo > 0 && atual < alvo)
                Text("faltam R\$ ${"%.2f".format((alvo - atual).coerceAtLeast(0.0))}",
                    color = TextLow, fontSize = 11.sp)
            else if (atual >= alvo && alvo > 0)
                Text("✓ meta batida!", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun isAcessibilidadeAtiva(ctx: Context): Boolean = try {
    val enabled = Settings.Secure.getString(
        ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: ""
    enabled.contains(ctx.packageName, ignoreCase = true)
} catch (e: Exception) { false }
