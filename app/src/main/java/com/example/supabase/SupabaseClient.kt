package com.example.supabase

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

    private const val TAG = "SupabaseClient"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private var currentUrlCached: String = ""
    private var currentKeyCached: String = ""
    private var cachedRetrofit: Retrofit? = null

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val anonKey = SupabaseConfig.currentAnonKey
        val userToken = SupabaseConfig.currentAuthToken

        val authHeader = if (!userToken.isNullOrBlank()) {
            "Bearer $userToken"
        } else if (anonKey.isNotBlank()) {
            "Bearer $anonKey"
        } else {
            ""
        }

        val requestBuilder = original.newBuilder()
            .header("Content-Type", "application/json")

        if (anonKey.isNotBlank()) {
            requestBuilder.header("apikey", anonKey)
        }
        if (authHeader.isNotBlank()) {
            requestBuilder.header("Authorization", authHeader)
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun getRetrofit(): Retrofit? {
        val url = SupabaseConfig.currentUrl
        val key = SupabaseConfig.currentAnonKey

        if (!SupabaseConfig.isConfigured) {
            return null
        }

        if (cachedRetrofit != null && currentUrlCached == url && currentKeyCached == key) {
            return cachedRetrofit
        }

        val normalizedUrl = if (url.endsWith("/")) url else "$url/"

        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl(normalizedUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            cachedRetrofit = retrofit
            currentUrlCached = url
            currentKeyCached = key
            retrofit
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create Supabase Retrofit client", e)
            null
        }
    }

    fun getDataApi(): SupabaseDataApi? = getRetrofit()?.create(SupabaseDataApi::class.java)

    fun getAuthApi(): SupabaseAuthApi? = getRetrofit()?.create(SupabaseAuthApi::class.java)

    suspend fun testConnection(): Result<String> {
        val api = getDataApi() ?: return Result.failure(Exception("Supabase is not configured with a valid URL and Anon Key."))
        return try {
            val response = api.getBookings(limit = 1)
            if (response.isSuccessful) {
                Result.success("Connected to Supabase successfully!")
            } else {
                val code = response.code()
                val errorBody = response.errorBody()?.string().orEmpty()
                if (code == 404 || errorBody.contains("relation \"public.bookings\" does not exist")) {
                    Result.success("Connected to Supabase! (Note: 'bookings' table needs to be created using the provided SQL script)")
                } else if (code == 401 || code == 403) {
                    Result.failure(Exception("Supabase Authentication error ($code). Check your Anon Key and RLS policies."))
                } else {
                    Result.failure(Exception("Supabase HTTP Error $code: $errorBody"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection test error", e)
            Result.failure(Exception("Network error connecting to Supabase: ${e.message}"))
        }
    }
}
