package com.example.model

enum class UserRole {
    PATIENT,
    PROVIDER
}

enum class ProviderSpecialty(val displayName: String, val badgeTitle: String) {
    CHILD_CARE_GIVER("Child Caregiver", "Child Caregiver"),
    ELDERLY_CARE_GIVER("Elderly Caregiver", "Elderly Caregiver"),
    NURSE("Registered Nurse", "Nurse"),
    DOCTOR("Online Doctor", "Online Doctor"),
    PHYSIOTHERAPIST("Physiotherapist", "Physio"),
    MENTAL_HEALTH("Mental Health Counselor", "Counselor"),
    COMMUNITY_HEALTH("Community Health Worker", "CHW"),
    PRIMARY_HEALTH_WORKER("Primary Healthcare Worker", "PHW"),
    SEXUAL_REPRODUCTIVE_HEALTH("Sexual & Reproductive Health Specialist", "SRH Specialist")
}

enum class VerificationStatus {
    UNVERIFIED,
    PENDING_REVIEW,
    VERIFIED
}

data class HealthProvider(
    val id: String,
    val name: String,
    val title: String,
    val specialty: ProviderSpecialty,
    val rating: Float,
    val reviewCount: Int,
    val distanceKm: Float,
    val etaMinutes: Int,
    val baseRate: Int,
    val licenseNumber: String,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val yearsExperience: Int,
    val bio: String,
    val services: List<String>,
    val avatarInitials: String,
    val isOnline: Boolean = true,
    val completedGigs: Int,
    val latitude: Double = 6.4281,
    val longitude: Double = 3.4219,
    // Normalized radar coordinates from -1.0f to 1.0f around patient center
    val radarX: Float,
    val radarY: Float
)

data class ServiceItem(
    val id: String,
    val name: String,
    val specialty: ProviderSpecialty,
    val description: String,
    val durationMinutes: Int,
    val basePrice: Int,
    val isUrgent: Boolean = false,
    val requiredSupplies: List<String> = emptyList()
)

data class MedicalSupply(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val description: String,
    val inStock: Boolean = true,
    val unit: String
)

data class CartItem(
    val supply: MedicalSupply,
    val quantity: Int
)

enum class BookingStatus(val stepIndex: Int, val label: String, val subtitle: String) {
    SEARCHING(0, "Dispatching Provider", "Matching with nearest available verified health worker..."),
    MATCHED(1, "Request Accepted", "Health worker confirmed your dispatch"),
    EN_ROUTE(2, "Provider En Route", "On their way to your specified doorstep"),
    ARRIVED(3, "Arrived at Doorstep", "Provider is outside ready to enter"),
    IN_PROGRESS(4, "Care in Progress", "Administering healthcare procedure"),
    COMPLETED(5, "Visit Completed", "Care finished, digital summary ready"),
    CANCELLED(-1, "Cancelled", "Booking was cancelled")
}

data class Booking(
    val id: String,
    val serviceId: String,
    val serviceName: String,
    val providerId: String?,
    val providerName: String?,
    val providerTitle: String?,
    val providerSpecialty: ProviderSpecialty?,
    val patientName: String,
    val patientAddress: String,
    val patientPhone: String,
    val symptomNotes: String,
    val status: BookingStatus,
    val timestampMillis: Long = System.currentTimeMillis(),
    val baseCost: Int,
    val suppliesCost: Int,
    val totalCost: Int,
    val etaMinutes: Int,
    val suppliesIncluded: List<CartItem> = emptyList(),
    val vitalsRecorded: String? = null
)

enum class ChatMessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

data class ChatMessage(
    val id: String,
    val bookingId: String? = null,
    val senderName: String,
    val isFromPatient: Boolean,
    val text: String,
    val timestamp: String,
    val isEmergencyAlert: Boolean = false,
    val status: ChatMessageStatus = ChatMessageStatus.SENT
)

data class ProviderCredential(
    val id: String,
    val providerId: String,
    val fullName: String,
    val specialty: ProviderSpecialty,
    val licenseCouncil: String,
    val licenseNumber: String,
    val expiryDate: String,
    val nationalIdType: String,
    val nationalIdNumber: String,
    val documentFileName: String,
    val status: VerificationStatus,
    val submissionDate: String
)

data class ProviderWallet(
    val totalEarnings: Int,
    val availableBalance: Int,
    val platformFeeDeducted: Int, // 15% LifeSavers fee
    val completedGigsCount: Int,
    val ratingAvg: Float
)

enum class NotificationType {
    PROVIDER_DISPATCH_ALERT,
    PATIENT_ACCEPTED_ALERT,
    PATIENT_ARRIVED_ALERT,
    PATIENT_COMPLETED_ALERT,
    PROVIDER_CREDENTIAL_VERIFIED,
    PROVIDER_CREDENTIAL_REJECTED
}

data class LifeSaversNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val bookingId: String? = null,
    val etaMinutes: Int? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)

typealias OmaNotification = LifeSaversNotification

data class PatientReview(
    val id: String,
    val bookingId: String,
    val providerId: String,
    val providerName: String,
    val providerTitle: String,
    val providerSpecialty: ProviderSpecialty,
    val serviceName: String,
    val rating: Int,
    val testimonial: String,
    val dateText: String,
    val tags: List<String> = emptyList(),
    val verifiedBooking: Boolean = true,
    val avatarInitials: String = ""
)

// =========================================================================
// UNICEF FemTech Suite Data Models (Reproductive & Maternal Health Continuum)
// =========================================================================

data class FemTechCycleData(
    val lastPeriodStartDate: String = "2026-10-10",
    val cycleLengthDays: Int = 28,
    val periodLengthDays: Int = 5,
    val isPregnant: Boolean = false,
    val estimatedOvulationDate: String = "2026-10-24",
    val nextPeriodDate: String = "2026-11-07",
    val loggedSymptoms: List<String> = listOf("Mild Cramps", "Normal Flow", "Good Energy"),
    val privacyConsentEnabled: Boolean = true
)

data class PregnancyTrackerData(
    val gestationalAgeWeeks: Int = 18,
    val estimatedDueDate: String = "2027-03-28",
    val trimester: Int = 2,
    val babySizeComparison: String = "Size of a Sweet Potato (~14cm)",
    val keyMilestones: List<String> = listOf(
        "Fetal heart rate active (145 bpm)",
        "Kick movements detectable",
        "Anatomy ultrasound scheduled"
    )
)

data class MaternalHealthScreening(
    val id: String,
    val dateText: String,
    val hemoglobinLevelGdl: Float,
    val bloodPressureSys: Int,
    val bloodPressureDia: Int,
    val ironSupplementTaken: Boolean,
    val riskAssessmentLevel: String,
    val recommendedAction: String
)


