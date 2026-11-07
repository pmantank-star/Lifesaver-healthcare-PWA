package com.example.supabase

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseDataApi {

    // -------------------------------------------------------------
    // Bookings & Dispatches
    // -------------------------------------------------------------
    @GET("rest/v1/bookings?select=*&order=updated_at.desc")
    suspend fun getBookings(
        @Query("limit") limit: Int = 50
    ): Response<List<SupabaseBookingDto>>

    @POST("rest/v1/bookings")
    suspend fun createBooking(
        @Body booking: SupabaseBookingDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<SupabaseBookingDto>>

    @PATCH("rest/v1/bookings")
    suspend fun updateBooking(
        @Query("id") idFilter: String, // e.g., "eq.b-101"
        @Body bookingUpdate: Map<String, Any>,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<SupabaseBookingDto>>

    // -------------------------------------------------------------
    // Chat & Telehealth Messages
    // -------------------------------------------------------------
    @GET("rest/v1/chat_messages?select=*&order=created_at.asc")
    suspend fun getChatMessages(
        @Query("booking_id") bookingIdFilter: String? = null // e.g. "eq.b-101"
    ): Response<List<SupabaseChatMessageDto>>

    @POST("rest/v1/chat_messages")
    suspend fun sendChatMessage(
        @Body message: SupabaseChatMessageDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<SupabaseChatMessageDto>>

    // -------------------------------------------------------------
    // Healthcare Providers
    // -------------------------------------------------------------
    @GET("rest/v1/providers?select=*")
    suspend fun getProviders(): Response<List<SupabaseProviderDto>>

    @POST("rest/v1/providers")
    suspend fun upsertProviders(
        @Body providers: List<SupabaseProviderDto>,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates"
    ): Response<Void>
}

interface SupabaseAuthApi {

    @POST("auth/v1/signup")
    suspend fun signUp(
        @Body request: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Body request: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/logout")
    suspend fun signOut(): Response<Void>
}
