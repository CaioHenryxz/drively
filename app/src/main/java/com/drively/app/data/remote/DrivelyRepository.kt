package com.drively.app.data.remote

import com.drively.app.core.model.Corrida
import com.drively.app.core.model.Meta
import com.drively.app.core.model.Veiculo
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

/**
 * Repository: toda comunicação com o Supabase passa por aqui.
 * Auth + CRUD de veículo, meta e corridas.
 */
object DrivelyRepository {

    private val client get() = SupabaseClient.client

    // ── Auth ──────────────────────────────────────────────────────────────────

    suspend fun cadastrar(email: String, senha: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = senha
        }
    }

    suspend fun login(email: String, senha: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = senha
        }
    }

    suspend fun logout() {
        client.auth.signOut()
    }

    fun usuarioAtual() = client.auth.currentUserOrNull()

    fun userId(): String = client.auth.currentUserOrNull()?.id ?: ""

    // ── Veículo ───────────────────────────────────────────────────────────────

    suspend fun salvarVeiculo(v: Veiculo): Boolean {
        return try {
            val uid = userId()
            val comUid = v.copy(userId = uid)
            // upsert: atualiza se já existe, insere se não
            client.from("veiculos").upsert(comUid)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun carregarVeiculo(): Veiculo? {
        return try {
            val uid = userId()
            client.from("veiculos")
                .select { filter { eq("user_id", uid) } }
                .decodeSingleOrNull<Veiculo>()
        } catch (e: Exception) {
            null
        }
    }

    // ── Meta ──────────────────────────────────────────────────────────────────

    suspend fun salvarMeta(m: Meta): Boolean {
        return try {
            val uid = userId()
            client.from("metas").upsert(m.copy(userId = uid))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun carregarMeta(): Meta? {
        return try {
            val uid = userId()
            client.from("metas")
                .select { filter { eq("user_id", uid) } }
                .decodeSingleOrNull<Meta>()
        } catch (e: Exception) {
            null
        }
    }

    // ── Corridas ──────────────────────────────────────────────────────────────

    suspend fun salvarCorrida(c: Corrida): Boolean {
        return try {
            val uid = userId()
            client.from("corridas").insert(c.copy(userId = uid))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun carregarCorridas(limite: Int = 50): List<Corrida> {
        return try {
            val uid = userId()
            client.from("corridas")
                .select {
                    filter { eq("user_id", uid) }
                    order("created_at", Order.DESCENDING)
                    limit(limite.toLong())
                }
                .decodeList<Corrida>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun lucroHoje(): Double {
        return try {
            val uid = userId()
            val hoje = java.time.LocalDate.now().toString() // "2026-06-30"
            val corridas = client.from("corridas")
                .select {
                    filter {
                        eq("user_id", uid)
                        gte("created_at", "${hoje}T00:00:00")
                    }
                }
                .decodeList<Corrida>()
            corridas.sumOf { it.lucroLiquido }
        } catch (e: Exception) {
            0.0
        }
    }

    suspend fun corridasHoje(): Int {
        return try {
            val uid = userId()
            val hoje = java.time.LocalDate.now().toString()
            client.from("corridas")
                .select {
                    filter {
                        eq("user_id", uid)
                        gte("created_at", "${hoje}T00:00:00")
                    }
                }
                .decodeList<Corrida>()
                .size
        } catch (e: Exception) {
            0
        }
    }


    suspend fun lucroMes(): Double {
        return try {
            val uid = userId()
            val inicioMes = java.time.LocalDate.now().withDayOfMonth(1).toString()
            val corridas = client.from("corridas")
                .select { filter { eq("user_id", uid); gte("created_at", "${inicioMes}T00:00:00") } }
                .decodeList<Corrida>()
            corridas.sumOf { it.lucroLiquido }
        } catch (e: Exception) { 0.0 }
    }

    // ── Onboarding completo ───────────────────────────────────────────────────
    // Verifica se o usuário já tem veículo E meta cadastrados
    suspend fun onboardingCompleto(): Boolean {
        return carregarVeiculo() != null && carregarMeta() != null
    }
}
