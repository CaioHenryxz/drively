package com.drively.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drively.app.data.remote.DrivelyRepository
import com.drively.app.ui.components.BotaoPrimario
import com.drively.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TelaAuth(onAutenticado: () -> Unit) {
    var isCadastro by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().background(Bg).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))

        // Logo
        Box(Modifier.size(64.dp).clip(RoundedCornerShape(20.dp)).background(Blue.copy(.18f)),
            contentAlignment = Alignment.Center) { Text("▲", color = Blue, fontSize = 28.sp) }
        Spacer(Modifier.height(16.dp))
        Text("Drively", color = TextHigh, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Copiloto financeiro", color = TextLow, fontSize = 12.sp)
        Spacer(Modifier.height(40.dp))

        // Tab login / cadastro
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Surface1)) {
            TabBtn("Criar conta", isCadastro, Modifier.weight(1f)) { isCadastro = true }
            TabBtn("Entrar", !isCadastro, Modifier.weight(1f)) { isCadastro = false }
        }
        Spacer(Modifier.height(20.dp))

        // E-mail
        Text("E-mail", color = TextLow, fontSize = 10.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(4.dp))
        TextField(email, { email = it.trim() }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            textStyle = androidx.compose.ui.text.TextStyle(color = TextHigh, fontSize = 14.sp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Surface2, unfocusedContainerColor = Surface2,
                focusedIndicatorColor = Blue, unfocusedIndicatorColor = Color.Transparent, cursorColor = Blue),
            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("seu@email.com", color = TextLow) })
        Spacer(Modifier.height(10.dp))

        // Senha
        Text("Senha", color = TextLow, fontSize = 10.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(4.dp))
        TextField(senha, { senha = it }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = androidx.compose.ui.text.TextStyle(color = TextHigh, fontSize = 14.sp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Surface2, unfocusedContainerColor = Surface2,
                focusedIndicatorColor = Blue, unfocusedIndicatorColor = Color.Transparent, cursorColor = Blue),
            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("mínimo 6 caracteres", color = TextLow) })
        Spacer(Modifier.height(6.dp))

        // Erro
        if (erro.isNotBlank()) {
            Text(erro, color = Red, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(6.dp))
        }

        Spacer(Modifier.height(8.dp))
        BotaoPrimario(
            texto = if (isCadastro) "Criar conta" else "Entrar",
            carregando = carregando,
            habilitado = email.isNotBlank() && senha.length >= 6
        ) {
            scope.launch {
                carregando = true
                erro = ""
                try {
                    if (isCadastro) DrivelyRepository.cadastrar(email, senha)
                    else DrivelyRepository.login(email, senha)
                    onAutenticado()
                } catch (e: Exception) {
                    erro = when {
                        e.message?.contains("Invalid login") == true -> "E-mail ou senha incorretos."
                        e.message?.contains("already registered") == true -> "E-mail já cadastrado. Tente entrar."
                        e.message?.contains("Password should be") == true -> "Senha muito curta (mínimo 6 caracteres)."
                        else -> "Erro: ${e.message}"
                    }
                } finally { carregando = false }
            }
        }

        Spacer(Modifier.weight(1f))
        Text("Seus dados são protegidos pela LGPD.", color = TextLow, fontSize = 10.sp)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TabBtn(label: String, ativo: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.clickable { onClick() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
        Text(label, color = if (ativo) Blue else TextMid,
            fontWeight = if (ativo) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
    }
}
