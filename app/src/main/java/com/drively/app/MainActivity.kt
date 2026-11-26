package com.drively.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.drively.app.core.model.TipoVeiculo
import com.drively.app.data.remote.DrivelyRepository
import com.drively.app.ui.auth.TelaAuth
import com.drively.app.ui.nav.NavPrincipal
import com.drively.app.ui.onboarding.TelaMetas
import com.drively.app.ui.onboarding.TelaPerfil
import com.drively.app.ui.onboarding.TelaSetupVeiculo
import com.drively.app.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Estados possíveis do app.
 * A lógica de navegação fica AQUI — não em SharedPrefs.
 * O Supabase diz qual é o estado real.
 */
private enum class EstadoApp {
    CARREGANDO,         // verificando sessão
    NAO_AUTENTICADO,    // precisa fazer login/cadastro
    ONBOARDING_PERFIL,  // logado, mas sem veículo cadastrado
    ONBOARDING_VEICULO,
    ONBOARDING_METAS,
    PRONTO              // tudo ok → Home
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrivelyTheme {
                var estado by remember { mutableStateOf(EstadoApp.CARREGANDO) }
                var tipoPerfil by remember { mutableStateOf(TipoVeiculo.CARRO) }
                val scope = rememberCoroutineScope()

                // Ao iniciar, verifica sessão Supabase e decide onde ir
                LaunchedEffect(Unit) {
                    val usuario = DrivelyRepository.usuarioAtual()
                    estado = if (usuario == null) {
                        EstadoApp.NAO_AUTENTICADO
                    } else {
                        // Usuário está logado — verifica se onboarding está completo
                        if (DrivelyRepository.onboardingCompleto())
                            EstadoApp.PRONTO
                        else
                            EstadoApp.ONBOARDING_PERFIL
                    }
                }

                when (estado) {
                    EstadoApp.CARREGANDO -> {
                        // Tela de loading enquanto checa o Supabase
                        Box(Modifier.fillMaxSize().background(Bg), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Blue)
                        }
                    }

                    EstadoApp.NAO_AUTENTICADO -> TelaAuth {
                        // Após login/cadastro, vai pro onboarding
                        estado = EstadoApp.ONBOARDING_PERFIL
                    }

                    EstadoApp.ONBOARDING_PERFIL -> TelaPerfil { tipo ->
                        tipoPerfil = tipo
                        estado = EstadoApp.ONBOARDING_VEICULO
                    }

                    EstadoApp.ONBOARDING_VEICULO -> TelaSetupVeiculo(
                        tipo = tipoPerfil,
                        onSalvo = { estado = EstadoApp.ONBOARDING_METAS }
                    )

                    EstadoApp.ONBOARDING_METAS -> TelaMetas {
                        // Onboarding concluído — vai pra Home e nunca mais volta
                        estado = EstadoApp.PRONTO
                    }

                    EstadoApp.PRONTO -> NavPrincipal(
                        onLogout = { estado = EstadoApp.NAO_AUTENTICADO }
                    )
                }
            }
        }
    }
}
