package com.drively.app.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * PASSO OBRIGATÓRIO: substitua as duas constantes abaixo.
 * Onde achar: supabase.com → seu projeto → Settings → API
 */
object SupabaseClient {
    const val SUPABASE_URL      = "https://fvootphcvagezeivozce.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_publishable_qql8Xeez70Z1hrWEG0v0NA_3uUcx6qD"

    val client = createSupabaseClient(SUPABASE_URL, SUPABASE_ANON_KEY) {
        install(Auth)
        install(Postgrest)
    }
}
