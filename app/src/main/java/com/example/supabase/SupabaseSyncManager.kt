package com.example.supabase

import android.util.Log
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.ChatMessage
import com.example.model.HealthProvider
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okio.Buffer

enum class SyncStatus {
    NOT_CONFIGURED,
    DISCONNECTED,
    CONNECTING,
    SYNCED,
    SYNCING,
    ERROR
}

class SupabaseSyncManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val tag = "SupabaseSyncManager"

    private val _syncStatus = MutableStateFlow(SyncStatus.NOT_CONFIGURED)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow<Long?>(null)
    val lastSyncTimestamp: StateFlow<Long?> = _lastSyncTimestamp.asStateFlow()

    private val _syncMessage = MutableStateFlow("Local Prototype Mode")
    val syncMessage: StateFlow<String> = _syncMessage.asStateFlow()

    private var pollingJob: Job? = null
    private val realtimeChannels = mutableListOf<io.github.jan.supabase.realtime.RealtimeChannel>()

    init {
        checkConfig()
    }

    fun checkConfig() {
        if (SupabaseConfig.isConfigured) {
            _syncStatus.value = SyncStatus.DISCONNECTED
            _syncMessage.value = "Supabase configured. Ready to sync."
        } else {
            _syncStatus.value = SyncStatus.NOT_CONFIGURED
            _syncMessage.value = "Supabase keys not yet configured. Operating in local mode."
        }
    }

    suspend fun testConnection(): Result<String> {
        _syncStatus.value = SyncStatus.CONNECTING
        _syncMessage.value = "Connecting to Supabase..."
        val result = SupabaseClient.testConnection()
        if (result.isSuccess) {
            _syncStatus.value = SyncStatus.SYNCED
            _syncMessage.value = result.getOrNull() ?: "Connected to Supabase"
            _lastSyncTimestamp.value = System.currentTimeMillis()
        } else {
            _syncStatus.value = SyncStatus.ERROR
            _syncMessage.value = result.exceptionOrNull()?.message ?: "Failed to connect to Supabase"
        }
        return result
    }

    suspend fun pushBooking(booking: Booking): Result<Booking> {
        val api = SupabaseClient.getDataApi() ?: return Result.failure(Exception("Supabase not configured"))
        return try {
            val dto = SupabaseBookingDto.fromDomainModel(booking)
            val response = api.createBooking(dto)
            if (response.isSuccessful) {
                _lastSyncTimestamp.value = System.currentTimeMillis()
                _syncStatus.value = SyncStatus.SYNCED
                _syncMessage.value = "Booking pushed to Supabase"
                val created = response.body()?.firstOrNull()?.toDomainModel() ?: booking
                Result.success(created)
            } else {
                val errorMsg = response.errorBody()?.string().orEmpty()
                Log.w(tag, "Failed to push booking: ${response.code()} $errorMsg")
                // Graceful fallback to domain booking
                Result.success(booking)
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception pushing booking to Supabase", e)
            Result.success(booking)
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus, etaMinutes: Int): Result<Boolean> {
        val api = SupabaseClient.getDataApi() ?: return Result.failure(Exception("Supabase not configured"))
        return try {
            val updates = mapOf<String, Any>(
                "status" to status.name,
                "eta_minutes" to etaMinutes
            )
            val response = api.updateBooking("eq.$bookingId", updates)
            if (response.isSuccessful) {
                _lastSyncTimestamp.value = System.currentTimeMillis()
                Result.success(true)
            } else {
                Log.w(tag, "Failed to update booking status: ${response.code()}")
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception updating booking status", e)
            Result.failure(e)
        }
    }

    suspend fun fetchRemoteBookings(): List<Booking> {
        val api = SupabaseClient.getDataApi() ?: return emptyList()
        return try {
            val response = api.getBookings(limit = 20)
            if (response.isSuccessful) {
                _lastSyncTimestamp.value = System.currentTimeMillis()
                _syncStatus.value = SyncStatus.SYNCED
                response.body()?.map { it.toDomainModel() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception fetching bookings from Supabase", e)
            emptyList()
        }
    }

    suspend fun pushChatMessage(message: ChatMessage): Result<ChatMessage> {
        val api = SupabaseClient.getDataApi() ?: return Result.success(message.copy(status = com.example.model.ChatMessageStatus.SENT))
        return try {
            val dto = SupabaseChatMessageDto.fromDomainModel(message.copy(status = com.example.model.ChatMessageStatus.SENT))
            val response = api.sendChatMessage(dto)
            if (response.isSuccessful) {
                _lastSyncTimestamp.value = System.currentTimeMillis()
                val created = response.body()?.firstOrNull()?.toDomainModel() ?: message.copy(status = com.example.model.ChatMessageStatus.SENT)
                Result.success(created)
            } else {
                Result.success(message.copy(status = com.example.model.ChatMessageStatus.SENT))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception pushing chat message to Supabase", e)
            Result.success(message.copy(status = com.example.model.ChatMessageStatus.SENT))
        }
    }

    suspend fun fetchChatMessages(bookingId: String? = null): List<ChatMessage> {
        val api = SupabaseClient.getDataApi() ?: return emptyList()
        return try {
            val filter = bookingId?.let { "eq.$it" }
            val response = api.getChatMessages(filter)
            if (response.isSuccessful) {
                response.body()?.map { it.toDomainModel() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception fetching chat messages from Supabase", e)
            emptyList()
        }
    }

    suspend fun fetchRemoteProviders(): List<HealthProvider> {
        val api = SupabaseClient.getDataApi() ?: return emptyList()
        return try {
            val response = api.getProviders()
            if (response.isSuccessful) {
                _lastSyncTimestamp.value = System.currentTimeMillis()
                _syncStatus.value = SyncStatus.SYNCED
                response.body()?.map { it.toDomainModel() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception fetching providers from Supabase", e)
            emptyList()
        }
    }

    suspend fun seedInitialProvidersIfEmpty(initialProviders: List<HealthProvider>) {
        val api = SupabaseClient.getDataApi() ?: return
        try {
            val existing = api.getProviders()
            if (existing.isSuccessful && existing.body().isNullOrEmpty()) {
                val dtos = initialProviders.map { SupabaseProviderDto(
                    id = it.id,
                    name = it.name,
                    title = it.title,
                    specialty = it.specialty.name,
                    rating = it.rating,
                    reviewCount = it.reviewCount,
                    distanceKm = it.distanceKm,
                    etaMinutes = it.etaMinutes,
                    baseRate = it.baseRate,
                    licenseNumber = it.licenseNumber,
                    isOnline = it.isOnline,
                    latitude = it.latitude,
                    longitude = it.longitude
                )}
                api.upsertProviders(dtos)
                Log.d(tag, "Seeded initial providers to Supabase public.providers table.")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error seeding initial providers to Supabase", e)
        }
    }

    fun startRealtimeSubscriptions(
        onBookingsSynced: (List<Booking>) -> Unit,
        onChatSynced: (List<ChatMessage>) -> Unit,
        onProvidersSynced: ((List<HealthProvider>) -> Unit)? = null,
        onChatMessageEvent: ((com.example.model.ChatMessage) -> Unit)? = null
    ) {
        stopRealtimeSubscriptions()
        if (!SupabaseConfig.isConfigured) return

        val client = SupabaseRealtime.get() ?: return

        _syncStatus.value = SyncStatus.CONNECTING
        _syncMessage.value = "Subscribing to Supabase realtime..."

        // Bookings channel
        val bookingsChannel = client.channel("public:bookings")
        bookingsChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "bookings"
        }.onEach { action ->
            when (action) {
                is PostgresAction.Insert,
                is PostgresAction.Update,
                is PostgresAction.Delete -> {
                    try {
                        val bookings = fetchRemoteBookings()
                        if (bookings.isNotEmpty()) {
                            onBookingsSynced(bookings)
                        } else if (action is PostgresAction.Delete) {
                            onBookingsSynced(emptyList())
                        }
                        _lastSyncTimestamp.value = System.currentTimeMillis()
                        _syncStatus.value = SyncStatus.SYNCED
                    } catch (e: Exception) {
                        Log.w(tag, "Error handling bookings realtime event", e)
                    }
                }
                else -> {}
            }
        }.launchIn(scope)

        // Chat messages channel
        val chatChannel = client.channel("public:chat_messages")
        chatChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "chat_messages"
        }.onEach { action ->
            try {
                if (action is PostgresAction.Insert) {
                    val record = action.record
                    if (record.isNotEmpty()) {
                        val moshi = com.squareup.moshi.Moshi.Builder()
                            .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                            .build()
                        val adapter = moshi.adapter(SupabaseChatMessageDto::class.java)
                        val buffer = Buffer()
                        val writer = com.squareup.moshi.JsonWriter.of(buffer)
                        writer.beginObject()
                        record.forEach { (k, v) ->
                            writer.name(k)
                            when (v) {
                                is String -> writer.value(v)
                                is Number -> writer.value(v)
                                is Boolean -> writer.value(v)
                                null -> writer.nullValue()
                                else -> writer.value(v.toString())
                            }
                        }
                        writer.endObject()
                        writer.close()
                        val dto = adapter.fromJson(buffer.readUtf8())
                        val msg = dto?.toDomainModel()
                        if (msg != null) {
                            onChatMessageEvent?.invoke(msg)
                        }
                    }
                    val messages = fetchChatMessages()
                    if (messages.isNotEmpty()) {
                        onChatSynced(messages)
                    }
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    _syncStatus.value = SyncStatus.SYNCED
                } else if (action is PostgresAction.Update) {
                    val record = action.record
                    if (record.isNotEmpty()) {
                        val moshi = com.squareup.moshi.Moshi.Builder()
                            .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                            .build()
                        val adapter = moshi.adapter(SupabaseChatMessageDto::class.java)
                        val buffer = Buffer()
                        val writer = com.squareup.moshi.JsonWriter.of(buffer)
                        writer.beginObject()
                        record.forEach { (k, v) ->
                            writer.name(k)
                            when (v) {
                                is String -> writer.value(v)
                                is Number -> writer.value(v)
                                is Boolean -> writer.value(v)
                                null -> writer.nullValue()
                                else -> writer.value(v.toString())
                            }
                        }
                        writer.endObject()
                        writer.close()
                        val dto = adapter.fromJson(buffer.readUtf8())
                        val msg = dto?.toDomainModel()
                        if (msg != null) {
                            onChatMessageEvent?.invoke(msg)
                        }
                    }
                    val messages = fetchChatMessages()
                    if (messages.isNotEmpty()) {
                        onChatSynced(messages)
                    }
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    _syncStatus.value = SyncStatus.SYNCED
                }
            } catch (e: Exception) {
                Log.w(tag, "Error handling chat_messages realtime event", e)
            }
        }.launchIn(scope)

        // Providers channel
        val providersChannel = client.channel("public:providers")
        providersChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "providers"
        }.onEach { action ->
            if (action is PostgresAction.Insert || action is PostgresAction.Update || action is PostgresAction.Delete) {
                try {
                    val providers = fetchRemoteProviders()
                    if (providers.isNotEmpty() || action is PostgresAction.Delete) {
                        onProvidersSynced?.invoke(providers)
                    }
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    _syncStatus.value = SyncStatus.SYNCED
                } catch (e: Exception) {
                    Log.w(tag, "Error handling providers realtime event", e)
                }
            }
        }.launchIn(scope)

        listOf(bookingsChannel, chatChannel, providersChannel).forEach { ch ->
            scope.launch {
                try {
                    ch.subscribe()
                    if (!realtimeChannels.contains(ch)) {
                        realtimeChannels.add(ch)
                    }
                    Log.d(tag, "Subscribed to realtime channel: ${ch.topic}")
                } catch (e: Exception) {
                    Log.e(tag, "Failed to subscribe to channel ${ch.topic}", e)
                    _syncStatus.value = SyncStatus.ERROR
                    _syncMessage.value = e.message ?: "Realtime subscription failed"
                }
            }
        }
    }

    fun startRealtimePolling(
        onBookingsSynced: (List<Booking>) -> Unit,
        onChatSynced: (List<ChatMessage>) -> Unit,
        onProvidersSynced: ((List<HealthProvider>) -> Unit)? = null
    ) {
        // Fallback polling kept for backward compatibility; prefer realtime subscriptions
        stopRealtimeSubscriptions()
        if (!SupabaseConfig.isConfigured) return

        _syncStatus.value = SyncStatus.SYNCING
        _syncMessage.value = "Falling back to polling sync..."

        pollingJob = scope.launch {
            while (isActive) {
                try {
                    val bookings = fetchRemoteBookings()
                    if (bookings.isNotEmpty()) {
                        onBookingsSynced(bookings)
                    }

                    val messages = fetchChatMessages()
                    if (messages.isNotEmpty()) {
                        onChatSynced(messages)
                    }

                    val providers = fetchRemoteProviders()
                    if (providers.isNotEmpty()) {
                        onProvidersSynced?.invoke(providers)
                    }
                    _syncStatus.value = SyncStatus.SYNCED
                } catch (e: Exception) {
                    Log.w(tag, "Polling error", e)
                    _syncStatus.value = SyncStatus.ERROR
                }
                delay(8000)
            }
        }
    }

    fun stopRealtimeSubscriptions() {
        pollingJob?.cancel()
        pollingJob = null
        val client = SupabaseRealtime.get()
        if (client != null) {
            realtimeChannels.forEach { ch ->
                scope.launch {
                    runCatching { client.realtime.removeChannel(ch) }
                }
            }
        }
        realtimeChannels.clear()
        if (_syncStatus.value == SyncStatus.SYNCED || _syncStatus.value == SyncStatus.CONNECTING) {
            _syncStatus.value = SyncStatus.DISCONNECTED
            _syncMessage.value = "Realtime disconnected"
        }
    }

    fun stopRealtimePolling() {
        stopRealtimeSubscriptions()
    }
}
