package com.example.supabase

import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.ChatMessage
import com.example.model.HealthProvider
import com.example.model.ProviderSpecialty
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseBookingDto(
    @Json(name = "id") val id: String,
    @Json(name = "service_id") val serviceId: String,
    @Json(name = "service_name") val serviceName: String,
    @Json(name = "patient_name") val patientName: String,
    @Json(name = "address") val address: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "symptoms") val symptoms: String? = null,
    @Json(name = "provider_id") val providerId: String? = null,
    @Json(name = "provider_name") val providerName: String? = null,
    @Json(name = "status") val status: String = "ASSIGNED",
    @Json(name = "eta_minutes") val etaMinutes: Int = 15,
    @Json(name = "total_price") val totalPrice: Int = 0,
    @Json(name = "has_supplies") val hasSupplies: Boolean = false
) {
    fun toDomainModel(): Booking {
        val mappedStatus = try {
            BookingStatus.valueOf(status.uppercase())
        } catch (_: Exception) {
            BookingStatus.MATCHED
        }

        return Booking(
            id = id,
            serviceId = serviceId,
            serviceName = serviceName,
            providerId = providerId,
            providerName = providerName,
            providerTitle = if (providerId != null) "RN" else null,
            providerSpecialty = ProviderSpecialty.NURSE,
            patientName = patientName,
            patientAddress = address,
            patientPhone = phone,
            symptomNotes = symptoms.orEmpty(),
            status = mappedStatus,
            baseCost = totalPrice,
            suppliesCost = 0,
            totalCost = totalPrice,
            etaMinutes = etaMinutes,
            suppliesIncluded = emptyList()
        )
    }

    companion object {
        fun fromDomainModel(booking: Booking): SupabaseBookingDto {
            return SupabaseBookingDto(
                id = booking.id,
                serviceId = booking.serviceId,
                serviceName = booking.serviceName,
                patientName = booking.patientName,
                address = booking.patientAddress,
                phone = booking.patientPhone,
                symptoms = booking.symptomNotes,
                providerId = booking.providerId,
                providerName = booking.providerName,
                status = booking.status.name,
                etaMinutes = booking.etaMinutes,
                totalPrice = booking.totalCost,
                hasSupplies = booking.suppliesIncluded.isNotEmpty()
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseChatMessageDto(
    @Json(name = "id") val id: String,
    @Json(name = "booking_id") val bookingId: String? = null,
    @Json(name = "sender_id") val senderId: String = "user",
    @Json(name = "sender_name") val senderName: String,
    @Json(name = "is_provider") val isProvider: Boolean = false,
    @Json(name = "content") val content: String,
    @Json(name = "timestamp") val timestamp: String,
    @Json(name = "status") val status: String? = null
) {
    fun toDomainModel(): ChatMessage {
        val mappedStatus = try {
            com.example.model.ChatMessageStatus.valueOf(status?.uppercase() ?: "SENT")
        } catch (_: Exception) {
            com.example.model.ChatMessageStatus.SENT
        }
        return ChatMessage(
            id = id,
            bookingId = bookingId,
            senderName = senderName,
            isFromPatient = !isProvider,
            text = content,
            timestamp = timestamp,
            status = mappedStatus
        )
    }

    companion object {
        fun fromDomainModel(message: ChatMessage): SupabaseChatMessageDto {
            return SupabaseChatMessageDto(
                id = message.id,
                bookingId = message.bookingId,
                senderId = if (message.isFromPatient) "patient" else "provider",
                senderName = message.senderName,
                isProvider = !message.isFromPatient,
                content = message.text,
                timestamp = message.timestamp,
                status = message.status.name
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseProviderDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "title") val title: String,
    @Json(name = "specialty") val specialty: String,
    @Json(name = "rating") val rating: Float = 5.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "distance_km") val distanceKm: Float = 1.0f,
    @Json(name = "eta_minutes") val etaMinutes: Int = 15,
    @Json(name = "base_rate") val baseRate: Int = 15000,
    @Json(name = "license_number") val licenseNumber: String? = null,
    @Json(name = "is_online") val isOnline: Boolean = true,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null
) {
    fun toDomainModel(): HealthProvider {
        val mappedSpecialty = try {
            ProviderSpecialty.valueOf(specialty.uppercase())
        } catch (_: Exception) {
            ProviderSpecialty.NURSE
        }

        return HealthProvider(
            id = id,
            name = name,
            title = title,
            specialty = mappedSpecialty,
            rating = rating,
            reviewCount = reviewCount,
            distanceKm = distanceKm,
            etaMinutes = etaMinutes,
            baseRate = baseRate,
            licenseNumber = licenseNumber ?: "MDCN-VERIFIED",
            yearsExperience = 5,
            bio = "Verified clinician on the LifeSavers Network.",
            services = emptyList(),
            avatarInitials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString(""),
            completedGigs = 50,
            latitude = latitude ?: 6.4281,
            longitude = longitude ?: 3.4219,
            radarX = 0f,
            radarY = 0f
        )
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseAuthRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "data") val data: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    @Json(name = "user") val user: SupabaseUserDto? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUserDto(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "user_metadata") val userMetadata: Map<String, String>? = null
)
