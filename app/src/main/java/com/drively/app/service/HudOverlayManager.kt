package com.drively.app.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.savedstate.*
import com.drively.app.core.model.*
import com.drively.app.ui.theme.*

object HudOverlayManager {
    private var wm: WindowManager? = null
    private var view: ComposeView? = null
    private val h = Handler(Looper.getMainLooper())

    fun init(ctx: Context) { wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    fun mostrar(ctx: Context, oferta: Oferta, r: ResultadoOferta) {
        h.post {
            esconderInternal()
            if (!Settings.canDrawOverlays(ctx)) return@post
            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP }

            val owner = MinimalLifecycleOwner().also { it.start() }
            val cv = ComposeView(ctx).apply {
                setViewTreeLifecycleOwner(owner); setViewTreeViewModelStoreOwner(owner)
                setViewTreeSavedStateRegistryOwner(owner)
                setContent {
                    DrivelyTheme { HudBar(oferta, r) { esconder() } }
                }
            }
            view = cv; wm?.addView(cv, lp)
            h.postDelayed({ esconder() }, 12_000)
        }
    }

    fun esconder() { h.post { esconderInternal() } }
    private fun esconderInternal() { view?.let { try { wm?.removeView(it) } catch (_: Exception) {} }; view = null }
}

@Composable
private fun HudBar(oferta: Oferta, r: ResultadoOferta, onFechar: () -> Unit) {
    val corBg = when(r.veredito) { Veredito.VERDE->Color(0xFF052E16); Veredito.AMARELO->Color(0xFF1C1407); else->Color(0xFF2D0707) }
    val cor   = when(r.veredito) { Veredito.VERDE->Green; Veredito.AMARELO->Amber; else->Red }
    val emoji = when(r.veredito) { Veredito.VERDE->"🟢"; Veredito.AMARELO->"🟡"; else->"🔴" }
    val app   = when(oferta.app) { AppCorrida.UBER->"Uber"; AppCorrida.NOVENTA_NOVE->"99"; AppCorrida.IFOOD->"iFood"; else->"App" }

    Column(Modifier.fillMaxWidth().background(corBg)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            HudCol("R$%.2f".format(r.lucroLiquido), "LUCRO", cor)
            HudDiv()
            HudCol("R$%.2f".format(r.reaisPorKm), "R$/KM")
            HudDiv()
            HudCol("R$%.0f/h".format(r.reaisPorHora), "R$/H")
            HudDiv()
            HudCol("%.1fkm".format(oferta.distanciaAteOrigemKm), if(oferta.app==AppCorrida.IFOOD)"LOJA" else "BUSCA")
            HudDiv()
            Text("$emoji", fontSize = 18.sp)
            Box(Modifier.size(28.dp).clickable{onFechar()}, contentAlignment=Alignment.Center){
                Text("✕", color=TextMid, fontSize=14.sp)
            }
        }
        Box(Modifier.fillMaxWidth().background(cor.copy(.15f)).padding(horizontal=14.dp, vertical=4.dp)){
            Text("$emoji  $app · R$%.2f bruto · vale aceitar?".format(oferta.valorBruto), color=cor, fontSize=11.sp, fontWeight=FontWeight.Bold)
        }
    }
}

@Composable
private fun HudCol(v: String, l: String, c: Color = TextHigh) =
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(v, color=c, fontSize=11.sp, fontWeight=FontWeight.Bold)
        if(l.isNotBlank()) Text(l, color=TextLow, fontSize=8.sp)
    }

@Composable private fun HudDiv() = Box(Modifier.height(24.dp).width(0.5.dp).background(Color.White.copy(.08f)))

class MinimalLifecycleOwner : SavedStateRegistryOwner, ViewModelStoreOwner {
    private val lr = LifecycleRegistry(this)
    private val sc = SavedStateRegistryController.create(this)
    private val vs = ViewModelStore()
    override val lifecycle: Lifecycle get() = lr
    override val savedStateRegistry: SavedStateRegistry get() = sc.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = vs
    fun start() { sc.performRestore(null); lr.currentState = Lifecycle.State.STARTED }
}
