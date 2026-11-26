package com.drively.app.ui.onboarding

import android.content.Context
import androidx.compose.runtime.*
import com.drively.app.core.model.TipoVeiculo

/**
 * Fluxo de onboarding atualizado:
 * PERFIL → VEÍCULO → LGPD → METAS
 *
 * A tela de LGPD entra entre veículo e metas conforme solicitado.
 * IndicadorPassos atualizado para 4 passos.
 */
enum class PassoOnboarding { PERFIL, VEICULO, LGPD, METAS }

@Composable
fun FluxoOnboarding(context: Context, onConcluido: () -> Unit) {
    var passo by remember { mutableStateOf(PassoOnboarding.PERFIL) }
    var tipo  by remember { mutableStateOf(TipoVeiculo.CARRO) }

    when (passo) {
        PassoOnboarding.PERFIL  -> TelaPerfil { t ->
            tipo  = t
            passo = PassoOnboarding.VEICULO
        }
        PassoOnboarding.VEICULO -> TelaSetupVeiculo(tipo) {
            passo = PassoOnboarding.LGPD
        }
        PassoOnboarding.LGPD    -> TelaLGPD {
            passo = PassoOnboarding.METAS
        }
        PassoOnboarding.METAS   -> TelaMetas {
            onConcluido()
        }
    }
}
