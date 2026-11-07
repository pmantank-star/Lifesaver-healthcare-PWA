package com.example.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.realtime.Realtime

object SupabaseRealtime {
    private var client: SupabaseClient? = null

    fun get(): SupabaseClient? {
        if (!SupabaseConfig.isConfigured) return null
        if (client != null) return client
        client = createSupabaseClient(
            supabaseUrl = SupabaseConfig.currentUrl.trimEnd('/'),
            supabaseKey = SupabaseConfig.currentAnonKey
        ) {
            install(Realtime)
        }
        return client
    }

    fun clear() {
        client = null
    }
}
