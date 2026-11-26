package com.drively.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.drively.app.core.calc.Calculadora
import com.drively.app.core.model.Meta
import com.drively.app.core.model.Veiculo
import com.drively.app.data.remote.DrivelyRepository
import kotlinx.coroutines.*

class DrivelyReaderService : AccessibilityService() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var ultimoHash = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        HudOverlayManager.init(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (!listOf("ubercab","taxis99","ifood").any { pkg.contains(it) }) return
        val textos = extrairTextos(rootInActiveWindow) 
        val hash = textos.hashCode()
        if (hash == ultimoHash) return
        ultimoHash = hash
        val oferta = OfertaParser.parse(pkg, textos) ?: run { HudOverlayManager.esconder(); return }
        scope.launch {
            val v = DrivelyRepository.carregarVeiculo() ?: Veiculo()
            val m = DrivelyRepository.carregarMeta() ?: Meta()
            val r = Calculadora.avaliar(oferta, v, m)
            HudOverlayManager.mostrar(applicationContext, oferta, r)
        }
    }

    override fun onInterrupt() { HudOverlayManager.esconder() }
    override fun onDestroy() { super.onDestroy(); scope.cancel(); HudOverlayManager.esconder() }

    private fun extrairTextos(node: AccessibilityNodeInfo?): List<String> {
        node ?: return emptyList()
        val res = mutableListOf<String>()
        node.text?.toString()?.trim()?.let { if (it.length > 1) res.add(it) }
        node.contentDescription?.toString()?.trim()?.let { if (it.length > 1 && it != res.lastOrNull()) res.add(it) }
        for (i in 0 until node.childCount) res.addAll(extrairTextos(node.getChild(i)))
        return res
    }
}
