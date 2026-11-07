package com.example.supabase

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SupabaseConfig {

    private const val PREFS_NAME = "supabase_config_prefs"
    private const val KEY_URL = "custom_supabase_url"
    private const val KEY_ANON_KEY = "custom_supabase_anon_key"
    private const val KEY_AUTH_TOKEN = "supabase_auth_token"

    private var prefs: SharedPreferences? = null

    private val _configState = MutableStateFlow(ConfigState(isConfigured = false, url = "", anonKey = ""))
    val configState: StateFlow<ConfigState> = _configState.asStateFlow()

    data class ConfigState(
        val isConfigured: Boolean,
        val url: String,
        val anonKey: String,
        val authToken: String? = null
    )

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        refresh()
    }

    fun refresh() {
        val buildUrl = try { BuildConfig.SUPABASE_URL } catch (_: Exception) { "" }
        val buildKey = try { BuildConfig.SUPABASE_ANON_KEY } catch (_: Exception) { "" }

        val storedUrl = prefs?.getString(KEY_URL, null)
        val storedKey = prefs?.getString(KEY_ANON_KEY, null)
        val storedToken = prefs?.getString(KEY_AUTH_TOKEN, null)

        val activeUrl = when {
            !storedUrl.isNullOrBlank() -> storedUrl.trim()
            !buildUrl.isNullOrBlank() && (buildUrl.startsWith("http://") || buildUrl.startsWith("https://")) -> buildUrl.trim()
            else -> ""
        }

        val activeKey = when {
            !storedKey.isNullOrBlank() -> storedKey.trim()
            !buildKey.isNullOrBlank() && !buildKey.contains("DEFAULT_") -> buildKey.trim()
            else -> ""
        }

        val isValid = activeUrl.startsWith("http://") || activeUrl.startsWith("https://")
        val isConfigured = isValid && activeKey.isNotBlank()

        _configState.value = ConfigState(
            isConfigured = isConfigured,
            url = if (activeUrl.endsWith("/")) activeUrl.dropLast(1) else activeUrl,
            anonKey = activeKey,
            authToken = storedToken
        )
    }

    fun saveCustomCredentials(url: String, anonKey: String) {
        prefs?.edit()
            ?.putString(KEY_URL, url.trim())
            ?.putString(KEY_ANON_KEY, anonKey.trim())
            ?.apply()
        refresh()
    }

    fun saveAuthToken(token: String?) {
        prefs?.edit()
            ?.putString(KEY_AUTH_TOKEN, token)
            ?.apply()
        refresh()
    }

    fun clearCustomCredentials() {
        prefs?.edit()?.clear()?.apply()
        refresh()
    }

    val currentUrl: String
        get() = _configState.value.url

    val currentAnonKey: String
        get() = _configState.value.anonKey

    val isConfigured: Boolean
        get() = _configState.value.isConfigured

    val currentAuthToken: String?
        get() = _configState.value.authToken

    /**
     * Ready-to-execute Supabase SQL Schema for the user's Supabase SQL Editor.
     */
    val schemaSql: String = """
        -- ==========================================================
        -- LifeSavers Healthcare: Supabase SQL Setup Schema
        -- Run this script in your Supabase Project -> SQL Editor
        -- ==========================================================

        -- 1. Bookings & Real-Time Dispatches Table
        CREATE TABLE IF NOT EXISTS public.bookings (
            id TEXT PRIMARY KEY,
            service_id TEXT NOT NULL,
            service_name TEXT NOT NULL,
            patient_name TEXT NOT NULL,
            address TEXT NOT NULL,
            phone TEXT NOT NULL,
            symptoms TEXT,
            provider_id TEXT,
            provider_name TEXT,
            status TEXT NOT NULL DEFAULT 'ASSIGNED',
            eta_minutes INT NOT NULL DEFAULT 15,
            total_price INT NOT NULL DEFAULT 0,
            has_supplies BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
            updated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
        );

        -- 2. Consultation & Telehealth Chat Messages Table
        CREATE TABLE IF NOT EXISTS public.chat_messages (
            id TEXT PRIMARY KEY,
            booking_id TEXT NOT NULL,
            sender_id TEXT NOT NULL,
            sender_name TEXT NOT NULL,
            is_provider BOOLEAN NOT NULL DEFAULT FALSE,
            content TEXT NOT NULL,
            timestamp TEXT NOT NULL,
            created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
        );

        -- 3. Healthcare Specialists Table
        CREATE TABLE IF NOT EXISTS public.providers (
            id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            title TEXT NOT NULL,
            specialty TEXT NOT NULL,
            rating FLOAT NOT NULL DEFAULT 5.0,
            review_count INT NOT NULL DEFAULT 0,
            distance_km FLOAT NOT NULL DEFAULT 1.0,
            eta_minutes INT NOT NULL DEFAULT 15,
            base_rate INT NOT NULL DEFAULT 15000,
            license_number TEXT,
            is_online BOOLEAN NOT NULL DEFAULT TRUE,
            latitude FLOAT,
            longitude FLOAT,
            updated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
        );

        -- 4. Enable Row Level Security (RLS) with Public Anon Access
        ALTER TABLE public.bookings ENABLE ROW LEVEL SECURITY;
        ALTER TABLE public.chat_messages ENABLE ROW LEVEL SECURITY;
        ALTER TABLE public.providers ENABLE ROW LEVEL SECURITY;

        CREATE POLICY "Allow public read-write for bookings" ON public.bookings
            FOR ALL USING (true) WITH CHECK (true);

        CREATE POLICY "Allow public read-write for chat_messages" ON public.chat_messages
            FOR ALL USING (true) WITH CHECK (true);

        CREATE POLICY "Allow public read-write for providers" ON public.providers
            FOR ALL USING (true) WITH CHECK (true);

        -- 5. Enable Supabase Realtime Publication
        ALTER PUBLICATION supabase_realtime ADD TABLE public.bookings;
        ALTER PUBLICATION supabase_realtime ADD TABLE public.chat_messages;
        ALTER PUBLICATION supabase_realtime ADD TABLE public.providers;
    """.trimIndent()
}
