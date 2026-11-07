package com.example.data

import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.CartItem
import com.example.model.ChatMessage
import com.example.model.FemTechCycleData
import com.example.model.Gender
import com.example.model.HealthProvider
import com.example.model.MaternalHealthScreening
import com.example.model.MedicalSupply
import com.example.model.PatientReview
import com.example.model.PregnancyTrackerData
import com.example.model.ProviderCredential
import com.example.model.ProviderSpecialty
import com.example.model.ProviderWallet
import com.example.model.ServiceItem
import com.example.model.UserRole
import com.example.model.VerificationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PatientProfileData(
    val fullName: String = "Chidi Anya",
    val email: String = "chidi.anya@lifesavershealth.org",
    val phone: String = "+234 803 555 0192",
    val gender: Gender = Gender.FEMALE,
    val doorstepAddress: String = "14 Adeola Odeku St, Victoria Island, Lagos",
    val emergencyContactName: String = "Ngozi Anya (Spouse)",
    val emergencyContactPhone: String = "+234 802 991 4422",
    val bloodGroup: String = "O+",
    val allergies: String = "Penicillin, Sulfa antibiotics",
    val chronicConditions: String = "Mild Seasonal Asthma"
)

class LifeSaversRepository {

    // Current Role (Patient or Provider)
    private val _currentRole = MutableStateFlow(UserRole.PATIENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Current User Gender (Female or Male)
    private val _userGender = MutableStateFlow(Gender.FEMALE)
    val userGender: StateFlow<Gender> = _userGender.asStateFlow()

    fun setUserGender(gender: Gender) {
        _userGender.value = gender
        _patientProfile.update { it.copy(gender = gender) }
    }

    // Current User Location String
    private val _userAddress = MutableStateFlow("14 Adeola Odeku St, Victoria Island, Lagos")
    val userAddress: StateFlow<String> = _userAddress.asStateFlow()

    // Available Providers
    private val _providers = MutableStateFlow<List<HealthProvider>>(initialProviders())
    val providers: StateFlow<List<HealthProvider>> = _providers.asStateFlow()

    // Available Services
    private val _services = MutableStateFlow<List<ServiceItem>>(initialServices())
    val services: StateFlow<List<ServiceItem>> = _services.asStateFlow()

    // Medical Supplies
    private val _supplies = MutableStateFlow<List<MedicalSupply>>(initialSupplies())
    val supplies: StateFlow<List<MedicalSupply>> = _supplies.asStateFlow()

    // Shopping Cart for Supplies
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Active Bookings
    private val _bookings = MutableStateFlow<List<Booking>>(initialBookings())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    // Active Booking currently tracked
    private val _activeBooking = MutableStateFlow<Booking?>(_bookings.value.firstOrNull { it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED })
    val activeBooking: StateFlow<Booking?> = _activeBooking.asStateFlow()

    // Consultation Chat Messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(initialChatMessages())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // All Provider Credentials in Verification Pipeline
    private val _allCredentials = MutableStateFlow<List<com.example.credential.CredentialEntity>>(initialCredentials())
    val allCredentials: StateFlow<List<com.example.credential.CredentialEntity>> = _allCredentials.asStateFlow()

    // Active Provider's Credential
    private val _providerCredential = MutableStateFlow(_allCredentials.value.first().toDomainModel())
    val providerCredential: StateFlow<ProviderCredential> = _providerCredential.asStateFlow()

    // Provider Online Status & incoming Gig Radar
    private val _isProviderOnline = MutableStateFlow(true)
    val isProviderOnline: StateFlow<Boolean> = _isProviderOnline.asStateFlow()

    // Incoming Gig Alert for Provider Mode
    private val _incomingGigAlert = MutableStateFlow<Booking?>(null)
    val incomingGigAlert: StateFlow<Booking?> = _incomingGigAlert.asStateFlow()

    // Patient Personal Profile Data
    private val _patientProfile = MutableStateFlow(PatientProfileData())
    val patientProfile: StateFlow<PatientProfileData> = _patientProfile.asStateFlow()

    // Saved Favorites
    private val _favoriteProviderIds = MutableStateFlow<Set<String>>(setOf("prov-01", "prov-03"))
    val favoriteProviderIds: StateFlow<Set<String>> = _favoriteProviderIds.asStateFlow()

    private val _favoriteSupplyIds = MutableStateFlow<Set<String>>(setOf("sup-01", "sup-04"))
    val favoriteSupplyIds: StateFlow<Set<String>> = _favoriteSupplyIds.asStateFlow()

    // Patient Reviews & Testimonials for Booked Doctors and Nurses
    private val _patientReviews = MutableStateFlow<List<PatientReview>>(initialReviews())
    val patientReviews: StateFlow<List<PatientReview>> = _patientReviews.asStateFlow()

    // Provider Wallet
    private val _providerWallet = MutableStateFlow(
        ProviderWallet(
            totalEarnings = 184500,
            availableBalance = 52000,
            platformFeeDeducted = 27675,
            completedGigsCount = 14,
            ratingAvg = 4.95f
        )
    )
    val providerWallet: StateFlow<ProviderWallet> = _providerWallet.asStateFlow()

    // UNICEF FemTech Suite Data
    private val _femTechCycleData = MutableStateFlow(FemTechCycleData())
    val femTechCycleData: StateFlow<FemTechCycleData> = _femTechCycleData.asStateFlow()

    private val _pregnancyTrackerData = MutableStateFlow(PregnancyTrackerData())
    val pregnancyTrackerData: StateFlow<PregnancyTrackerData> = _pregnancyTrackerData.asStateFlow()

    private val _maternalHealthScreenings = MutableStateFlow<List<MaternalHealthScreening>>(
        listOf(
            MaternalHealthScreening(
                id = "scr-01",
                dateText = "2026-10-20",
                hemoglobinLevelGdl = 11.8f,
                bloodPressureSys = 118,
                bloodPressureDia = 78,
                ironSupplementTaken = true,
                riskAssessmentLevel = "NORMAL_LOW_RISK",
                recommendedAction = "Maintain iron supplementation and routine antenatal checkup."
            ),
            MaternalHealthScreening(
                id = "scr-02",
                dateText = "2026-09-15",
                hemoglobinLevelGdl = 10.9f,
                bloodPressureSys = 122,
                bloodPressureDia = 80,
                ironSupplementTaken = true,
                riskAssessmentLevel = "MILD_ANEMIA",
                recommendedAction = "Increased iron and dietary folate intake advised by Midwife."
            )
        )
    )
    val maternalHealthScreenings: StateFlow<List<MaternalHealthScreening>> = _maternalHealthScreenings.asStateFlow()

    fun updateFemTechCycleData(data: FemTechCycleData) {
        _femTechCycleData.value = data
    }

    fun submitReview(review: PatientReview) {
        _patientReviews.update { current ->
            val filtered = current.filterNot { it.bookingId == review.bookingId || it.id == review.id }
            listOf(review) + filtered
        }
    }

    fun updatePatientProfile(updated: PatientProfileData) {
        _patientProfile.value = updated
        _userAddress.value = updated.doorstepAddress
    }

    fun toggleFavoriteProvider(providerId: String) {
        _favoriteProviderIds.update { current ->
            if (current.contains(providerId)) current - providerId else current + providerId
        }
    }

    fun toggleFavoriteSupply(supplyId: String) {
        _favoriteSupplyIds.update { current ->
            if (current.contains(supplyId)) current - supplyId else current + supplyId
        }
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
    }

    fun updateAddress(newAddress: String) {
        _userAddress.value = newAddress
    }

    fun toggleProviderOnline() {
        _isProviderOnline.update { !it }
    }

    fun addToCart(supply: MedicalSupply) {
        _cart.update { currentList ->
            val existing = currentList.find { it.supply.id == supply.id }
            if (existing != null) {
                currentList.map {
                    if (it.supply.id == supply.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentList + CartItem(supply = supply, quantity = 1)
            }
        }
    }

    fun removeFromCart(supplyId: String) {
        _cart.update { currentList ->
            val existing = currentList.find { it.supply.id == supplyId }
            if (existing != null && existing.quantity > 1) {
                currentList.map {
                    if (it.supply.id == supplyId) it.copy(quantity = it.quantity - 1) else it
                }
            } else {
                currentList.filter { it.supply.id != supplyId }
            }
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun updateProvidersFromRemote(remoteProviders: List<HealthProvider>) {
        if (remoteProviders.isNotEmpty()) {
            _providers.update { current ->
                val remoteMap = remoteProviders.associateBy { it.id }
                current.map { local -> remoteMap[local.id] ?: local } + remoteProviders.filter { r -> current.none { c -> c.id == r.id } }
            }
        }
    }

    fun createBooking(
        service: ServiceItem,
        preferredProvider: HealthProvider?,
        patientName: String,
        patientAddress: String,
        patientPhone: String,
        symptomNotes: String,
        suppliesIncluded: List<CartItem>
    ): Booking {
        val suppliesTotal = suppliesIncluded.sumOf { it.supply.price * it.quantity }
        val assignedProvider = preferredProvider ?: _providers.value.firstOrNull { it.specialty == service.specialty && it.isOnline }
            ?: _providers.value.first()

        val newBooking = Booking(
            id = "LIFESAVERS-${System.currentTimeMillis() % 100000}",
            serviceId = service.id,
            serviceName = service.name,
            providerId = assignedProvider.id,
            providerName = assignedProvider.name,
            providerTitle = assignedProvider.title,
            providerSpecialty = assignedProvider.specialty,
            patientName = patientName.ifBlank { "Chidi Anya" },
            patientAddress = patientAddress.ifBlank { _userAddress.value },
            patientPhone = patientPhone.ifBlank { "+234 803 555 0192" },
            symptomNotes = symptomNotes.ifBlank { "Routine checkup and requested care procedure." },
            status = BookingStatus.SEARCHING,
            baseCost = service.basePrice,
            suppliesCost = suppliesTotal,
            totalCost = service.basePrice + suppliesTotal,
            etaMinutes = assignedProvider.etaMinutes,
            suppliesIncluded = suppliesIncluded
        )

        _bookings.update { listOf(newBooking) + it }
        _activeBooking.value = newBooking

        // Trigger incoming gig alert for provider mode simulation
        _incomingGigAlert.value = newBooking

        return newBooking
    }

    fun advanceBookingStatus(bookingId: String) {
        _bookings.update { list ->
            list.map { b ->
                if (b.id == bookingId) {
                    val nextStatus = when (b.status) {
                        BookingStatus.SEARCHING -> BookingStatus.MATCHED
                        BookingStatus.MATCHED -> BookingStatus.EN_ROUTE
                        BookingStatus.EN_ROUTE -> BookingStatus.ARRIVED
                        BookingStatus.ARRIVED -> BookingStatus.IN_PROGRESS
                        BookingStatus.IN_PROGRESS -> BookingStatus.COMPLETED
                        BookingStatus.COMPLETED -> BookingStatus.COMPLETED
                        BookingStatus.CANCELLED -> BookingStatus.CANCELLED
                    }
                    val updated = b.copy(status = nextStatus)
                    if (updated.status == BookingStatus.COMPLETED) {
                        // credit provider wallet
                        val providerCut = (b.baseCost * 0.85).toInt()
                        _providerWallet.update { w ->
                            w.copy(
                                totalEarnings = w.totalEarnings + b.baseCost,
                                availableBalance = w.availableBalance + providerCut,
                                platformFeeDeducted = w.platformFeeDeducted + (b.baseCost - providerCut),
                                completedGigsCount = w.completedGigsCount + 1
                            )
                        }
                    }
                    if (_activeBooking.value?.id == bookingId) {
                        _activeBooking.value = updated
                    }
                    updated
                } else {
                    b
                }
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        _bookings.update { list ->
            list.map { if (it.id == bookingId) it.copy(status = BookingStatus.CANCELLED) else it }
        }
        if (_activeBooking.value?.id == bookingId) {
            _activeBooking.value = null
        }
        if (_incomingGigAlert.value?.id == bookingId) {
            _incomingGigAlert.value = null
        }
    }

    fun dismissGigAlert() {
        _incomingGigAlert.value = null
    }

    fun acceptGigAlert() {
        val gig = _incomingGigAlert.value ?: return
        advanceBookingStatus(gig.id)
        _incomingGigAlert.value = null
    }

    fun sendChatMessage(text: String, isEmergency: Boolean = false): ChatMessage {
        val msg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}-${(0..9999).random()}",
            bookingId = _activeBooking.value?.id,
            senderName = if (_currentRole.value == UserRole.PATIENT) _patientProfile.value.fullName else _providerCredential.value.fullName,
            isFromPatient = _currentRole.value == UserRole.PATIENT,
            text = text,
            timestamp = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
            isEmergencyAlert = isEmergency,
            status = com.example.model.ChatMessageStatus.SENDING
        )
        _chatMessages.update { it + msg }
        return msg
    }

    fun addOrUpdateChatMessage(message: ChatMessage) {
        _chatMessages.update { current ->
            val existingIndex = current.indexOfFirst { it.id == message.id }
            if (existingIndex >= 0) {
                current.toMutableList().apply { set(existingIndex, message) }
            } else {
                current + message
            }
        }
    }

    fun updateChatMessageStatus(messageId: String, status: com.example.model.ChatMessageStatus) {
        _chatMessages.update { current ->
            current.map { if (it.id == messageId) it.copy(status = status) else it }
        }
    }

    fun mergeRemoteBookings(remoteBookings: List<Booking>) {
        if (remoteBookings.isEmpty()) return
        _bookings.update { localList ->
            val localMap = localList.associateBy { it.id }.toMutableMap()
            remoteBookings.forEach { remote ->
                localMap[remote.id] = remote
            }
            localMap.values.toList()
        }
        _activeBooking.value?.let { active ->
            val updated = remoteBookings.find { it.id == active.id }
            if (updated != null) {
                _activeBooking.value = updated
            }
        } ?: run {
            _activeBooking.value = remoteBookings.firstOrNull { it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED }
        }
    }

    fun mergeRemoteChatMessages(remoteMessages: List<ChatMessage>) {
        if (remoteMessages.isEmpty()) return
        _chatMessages.update { localList ->
            val localIds = localList.map { it.id }.toSet()
            val newUnique = remoteMessages.filter { it.id !in localIds }
            localList + newUnique
        }
    }

    private var verificationService: com.example.credential.CredentialVerificationService? = null

    fun setVerificationService(service: com.example.credential.CredentialVerificationService) {
        this.verificationService = service
    }

    fun submitCredential(
        fullName: String,
        specialty: ProviderSpecialty,
        council: String,
        licenseNumber: String,
        docName: String,
        nationalIdType: String = "National Identity Number (NIN)",
        nationalIdRaw: String = "1098234821",
        expiryDate: String = "2027-12-31",
        documentHash: String? = null
    ): com.example.credential.CredentialEntity {
        val service = verificationService
        val sha256 = documentHash ?: service?.computeSha256("$licenseNumber:$docName:prov-01")
            ?: "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4"
        val cleanId = nationalIdRaw.replace(" ", "").replace("-", "")
        val last4 = if (cleanId.length >= 4) cleanId.takeLast(4) else "3821"
        val maskedId = "****-****-$last4"

        val (bgStatus, bgRef) = service?.runCriminalBackgroundCheck(fullName) ?: Pair("PASSED", "CID-CLEARED-99210")

        val newEntity = com.example.credential.CredentialEntity(
            id = "cred-${System.currentTimeMillis() % 10000}",
            providerId = "prov-01",
            fullName = fullName,
            specialty = specialty.name,
            licenseCouncil = council,
            licenseNumber = licenseNumber,
            expiryDate = expiryDate,
            nationalIdType = nationalIdType,
            nationalIdMasked = maskedId,
            documentFileName = docName,
            documentSha256 = sha256,
            status = VerificationStatus.PENDING_REVIEW.name,
            backgroundCheckStatus = bgStatus,
            backgroundCheckReference = bgRef,
            councilRegistryVerified = true,
            submissionDate = "2026-10-17"
        )
        _allCredentials.update { listOf(newEntity) + it.filter { it.id != newEntity.id } }
        _providerCredential.value = newEntity.toDomainModel()
        return newEntity
    }

    fun adminVerifyCredential(
        credentialId: String,
        approve: Boolean,
        adminId: String = "ADM-LEAD-HEALTH",
        notes: String = "Verified practicing license with board registry and reviewed ID."
    ): com.example.credential.CredentialEntity? {
        val newStatus = if (approve) VerificationStatus.VERIFIED.name else "REJECTED"
        val timestamp = System.currentTimeMillis()
        var updatedEntity: com.example.credential.CredentialEntity? = null

        _allCredentials.update { list ->
            list.map { item ->
                if (item.id == credentialId) {
                    val updated = item.copy(
                        status = newStatus,
                        adminReviewerId = adminId,
                        adminReviewNotes = notes,
                        verifiedTimestamp = timestamp
                    )
                    updatedEntity = updated
                    _providers.update { provList ->
                        provList.map { p ->
                            if (p.id == updated.providerId || p.licenseNumber == updated.licenseNumber || p.name.contains(updated.fullName.take(5), ignoreCase = true)) {
                                p.copy(
                                    verificationStatus = if (approve) VerificationStatus.VERIFIED else VerificationStatus.UNVERIFIED,
                                    licenseNumber = updated.licenseNumber
                                )
                            } else p
                        }
                    }
                    if (_providerCredential.value.id == credentialId || _providerCredential.value.providerId == updated.providerId) {
                        _providerCredential.value = updated.toDomainModel()
                    }
                    updated
                } else item
            }
        }
        return updatedEntity
    }

    fun updateCredentialsFromDb(entities: List<com.example.credential.CredentialEntity>) {
        if (entities.isNotEmpty()) {
            _allCredentials.value = entities
            entities.find { it.providerId == "prov-01" || it.id == "cred-01" }?.let {
                _providerCredential.value = it.toDomainModel()
            }
        }
    }

    private fun initialCredentials(): List<com.example.credential.CredentialEntity> = listOf(
        com.example.credential.CredentialEntity(
            id = "cred-01",
            providerId = "prov-01",
            fullName = "Nurse Amara Okonkwo, RN",
            specialty = ProviderSpecialty.NURSE.name,
            licenseCouncil = "Nursing and Midwifery Council of Nigeria (NMCN)",
            licenseNumber = "NMCN/RN/2019/84712",
            expiryDate = "2027-11-30",
            nationalIdType = "National Identity Number (NIN)",
            nationalIdMasked = "****-****-3821",
            documentFileName = "amara_nmcn_practicing_license.pdf",
            documentSha256 = "c5d2b7e192a5482b4510b001a1154c5e3176191c496a79851722883f3ba9e782",
            status = VerificationStatus.VERIFIED.name,
            backgroundCheckStatus = "PASSED",
            backgroundCheckReference = "CID-CLEARED-84912",
            councilRegistryVerified = true,
            adminReviewerId = "ADM-LEAD-HEALTH",
            adminReviewNotes = "Directly verified on NMCN portal. Practicing folio active.",
            submissionDate = "2025-02-14",
            verifiedTimestamp = 1739520000000L
        ),
        com.example.credential.CredentialEntity(
            id = "cred-02",
            providerId = "prov-02",
            fullName = "Dr. Tunde Balogun, MD",
            specialty = ProviderSpecialty.DOCTOR.name,
            licenseCouncil = "Medical and Dental Council of Nigeria (MDCN)",
            licenseNumber = "MDCN/49102/2017",
            expiryDate = "2028-06-30",
            nationalIdType = "National Identity Number (NIN)",
            nationalIdMasked = "****-****-7719",
            documentFileName = "dr_balogun_mdcn_full_license.pdf",
            documentSha256 = "4e1837b980282b92f72a6b47a96898d9c25983791a82647c25838562817291a1",
            status = VerificationStatus.VERIFIED.name,
            backgroundCheckStatus = "PASSED",
            backgroundCheckReference = "CID-CLEARED-49102",
            councilRegistryVerified = true,
            adminReviewerId = "ADM-LEAD-HEALTH",
            adminReviewNotes = "MDCN registry verified. Clinical practice clearance active.",
            submissionDate = "2025-04-10",
            verifiedTimestamp = 1744272000000L
        ),
        com.example.credential.CredentialEntity(
            id = "cred-03",
            providerId = "prov-06",
            fullName = "Chioma Nwosu, B.Physio",
            specialty = ProviderSpecialty.PHYSIOTHERAPIST.name,
            licenseCouncil = "Medical Rehabilitation Therapists Board (MRTB)",
            licenseNumber = "MRTB/PT/2024/9182",
            expiryDate = "2027-08-31",
            nationalIdType = "National Identity Number (NIN)",
            nationalIdMasked = "****-****-5520",
            documentFileName = "chioma_nwosu_mrtb_practicing_certificate.pdf",
            documentSha256 = "91a8276b5417282b826471829375829104719284719284719284719284719284",
            status = VerificationStatus.PENDING_REVIEW.name,
            backgroundCheckStatus = "PASSED",
            backgroundCheckReference = "CID-CLEARED-91823",
            councilRegistryVerified = true,
            adminReviewerId = null,
            adminReviewNotes = null,
            submissionDate = "2026-10-15"
        )
    )

    fun updateProviderLocations(updated: List<HealthProvider>) {
        _providers.value = updated
    }

    private fun initialProviders(): List<HealthProvider> = listOf(
        HealthProvider(
            id = "prov-00",
            name = "Funmi Adewale",
            title = "Certified Child Caregiver",
            specialty = ProviderSpecialty.CHILD_CARE_GIVER,
            rating = 4.99f,
            reviewCount = 186,
            distanceKm = 0.7f,
            etaMinutes = 6,
            baseRate = 8000,
            licenseNumber = "CCG-LAG-90001",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 8,
            bio = "Experienced child caregiver providing nurturing care, feeding, hygiene, monitoring and developmental support at home for infants and toddlers.",
            services = listOf("Infant Care", "Toddler Supervision", "Child Hygiene & Feeding", "Night-time Child Care"),
            avatarInitials = "FA",
            completedGigs = 245,
            latitude = 6.4320,
            longitude = 3.4265,
            radarX = 0.15f,
            radarY = -0.28f
        ),
        HealthProvider(
            id = "prov-00b",
            name = "Grace Okoye",
            title = "Certified Elderly Caregiver",
            specialty = ProviderSpecialty.ELDERLY_CARE_GIVER,
            rating = 4.99f,
            reviewCount = 214,
            distanceKm = 0.8f,
            etaMinutes = 7,
            baseRate = 9000,
            licenseNumber = "ECG-LAG-90002",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 10,
            bio = "Compassionate elderly caregiver offering mobility assistance, medication reminders, companionship, hygiene, dementia care and fall prevention.",
            services = listOf("Elderly Companionship", "Mobility Assistance", "Medication Reminders", "Dementia & Palliative Care Support"),
            avatarInitials = "GO",
            completedGigs = 298,
            latitude = 6.4318,
            longitude = 3.4268,
            radarX = 0.18f,
            radarY = -0.30f
        ),
        HealthProvider(
            id = "prov-01",
            name = "Nurse Amara Okonkwo",
            title = "RN, Critical & Post-Op Wound Care Specialist",
            specialty = ProviderSpecialty.NURSE,
            rating = 4.96f,
            reviewCount = 138,
            distanceKm = 0.8f,
            etaMinutes = 9,
            baseRate = 18000,
            licenseNumber = "NMCN/RN/2019/84712",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 7,
            bio = "Certified emergency and community nurse with over 7 years in acute patient care, wound debridement, and post-cesarean recovery support.",
            services = listOf("Wound Dressing & Debridement", "IV Cannulation & Infusion", "Post-Natal Maternal Check", "Vitals & Glucose Monitoring"),
            avatarInitials = "AO",
            completedGigs = 320,
            latitude = 6.4325,
            longitude = 3.4270,
            radarX = 0.25f,
            radarY = -0.35f
        ),
        HealthProvider(
            id = "prov-02",
            name = "Dr. Tunde Balogun",
            title = "MD, Online Doctor & Teleconsultant GP",
            specialty = ProviderSpecialty.DOCTOR,
            rating = 4.92f,
            reviewCount = 94,
            distanceKm = 1.4f,
            etaMinutes = 14,
            baseRate = 32000,
            licenseNumber = "MDCN/49102/2017",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 9,
            bio = "Consultant family physician offering online video/audio teleconsultations, home clinical assessment, e-prescriptions, and emergency triage.",
            services = listOf("Online Doctor Video Consultation", "E-Prescription & Lab Ordering", "Pediatric Fever Assessment", "Elderly Health Review"),
            avatarInitials = "TB",
            completedGigs = 210,
            latitude = 6.4210,
            longitude = 3.4110,
            radarX = -0.42f,
            radarY = 0.28f
        ),
        HealthProvider(
            id = "prov-03",
            name = "Blessing Eze",
            title = "B.Physio, Orthopedic & Stroke Rehab Specialist",
            specialty = ProviderSpecialty.PHYSIOTHERAPIST,
            rating = 4.98f,
            reviewCount = 76,
            distanceKm = 1.9f,
            etaMinutes = 18,
            baseRate = 22000,
            licenseNumber = "MRTB/PT/2021/3391",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 5,
            bio = "Licensed physiotherapist focusing on mobility recovery, post-fracture joint therapy, stroke motor retraining, and ergonomic posture relief.",
            services = listOf("Post-Stroke Mobility Rehab", "Chronic Lower Back Therapy", "Post-Op Joint Mobilization", "Elderly Fall Prevention"),
            avatarInitials = "BE",
            completedGigs = 145,
            latitude = 6.4150,
            longitude = 3.4350,
            radarX = 0.55f,
            radarY = 0.48f
        ),
        HealthProvider(
            id = "prov-04",
            name = "Kolawole Adeleke",
            title = "MSc, Clinical Mental Health Counselor",
            specialty = ProviderSpecialty.MENTAL_HEALTH,
            rating = 4.89f,
            reviewCount = 62,
            distanceKm = 2.2f,
            etaMinutes = 20,
            baseRate = 20000,
            licenseNumber = "CASSON/MHC/2020/512",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 6,
            bio = "Trauma-informed counselor specializing in postpartum anxiety, burnout, grief, and emotional well-being via confidential in-home and tele-sessions.",
            services = listOf("Anxiety & Panic Relief", "Postpartum Emotional Support", "Burnout & Stress De-escalation", "Caregiver Respite Counseling"),
            avatarInitials = "KA",
            completedGigs = 98,
            latitude = 6.4420,
            longitude = 3.4120,
            radarX = -0.30f,
            radarY = -0.52f
        ),
        HealthProvider(
            id = "prov-05",
            name = "Halima Musa",
            title = "CHEW, Certified Community Health Worker",
            specialty = ProviderSpecialty.COMMUNITY_HEALTH,
            rating = 4.94f,
            reviewCount = 112,
            distanceKm = 0.5f,
            etaMinutes = 6,
            baseRate = 9500,
            licenseNumber = "CHPRBN/CHW/2022/881",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 4,
            bio = "Community health officer dedicated to accessible neighborhood healthcare, rapid malaria/typhoid test screening, immunization tracking, and routine vitals.",
            services = listOf("Rapid Malaria RDT & Vitals", "Blood Pressure & Sugar Check", "Childhood Immunization Audit", "Medication Adherence Support"),
            avatarInitials = "HM",
            completedGigs = 260,
            latitude = 6.4290,
            longitude = 3.4245,
            radarX = 0.12f,
            radarY = 0.22f
        ),
        HealthProvider(
            id = "prov-06",
            name = "Nurse Blessing Ejiofor",
            title = "RN, RM, Sexual & Reproductive Health Specialist",
            specialty = ProviderSpecialty.SEXUAL_REPRODUCTIVE_HEALTH,
            rating = 4.98f,
            reviewCount = 142,
            distanceKm = 1.1f,
            etaMinutes = 8,
            baseRate = 18000,
            licenseNumber = "NMCN/RM/2018/4412",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 8,
            bio = "Certified Nurse Midwife and Reproductive Health Officer specializing in confidential family planning, cervical HPV screening, lactation, and female wellness.",
            services = listOf("Cervical HPV & Pap Smear Screening", "Contraceptive & Family Planning", "Confidential STI Care", "Post-Natal & Lactation Care"),
            avatarInitials = "BE",
            completedGigs = 280,
            latitude = 6.4310,
            longitude = 3.4220,
            radarX = -0.18f,
            radarY = 0.25f
        ),
        HealthProvider(
            id = "prov-07",
            name = "Dr. Farouk Al-Hassan",
            title = "MBBS, FWACS, Andrologist & Reproductive Specialist",
            specialty = ProviderSpecialty.SEXUAL_REPRODUCTIVE_HEALTH,
            rating = 4.96f,
            reviewCount = 118,
            distanceKm = 1.8f,
            etaMinutes = 14,
            baseRate = 32000,
            licenseNumber = "MDCN/SURG/2016/9941",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 10,
            bio = "Consultant Urologist and Male Reproductive Health Specialist providing confidential assessments for prostate wellness, erectile dysfunction, male fertility, and STIs.",
            services = listOf("Male Fertility & Prostate Check", "Erectile Health & Hormonal Triage", "Confidential STI/STD Care", "Reproductive Health Review"),
            avatarInitials = "FH",
            completedGigs = 195,
            latitude = 6.4250,
            longitude = 3.4150,
            radarX = 0.32f,
            radarY = -0.40f
        ),
        HealthProvider(
            id = "prov-08",
            name = "Amina Bello",
            title = "CHO, Primary Healthcare Worker",
            specialty = ProviderSpecialty.PRIMARY_HEALTH_WORKER,
            rating = 4.92f,
            reviewCount = 88,
            distanceKm = 0.6f,
            etaMinutes = 6,
            baseRate = 8500,
            licenseNumber = "CHO/PHW/2021/3391",
            verificationStatus = VerificationStatus.VERIFIED,
            yearsExperience = 6,
            bio = "Primary Healthcare Officer skilled in doorstep immunizations, maternal-child health triage, blood pressure/glucose screening, and preventive health education.",
            services = listOf("Doorstep Primary Health & Immunization", "Maternal Vitals & Nutrition Check", "Childhood Vaccine Audit"),
            avatarInitials = "AB",
            completedGigs = 220,
            latitude = 6.4285,
            longitude = 3.4255,
            radarX = -0.10f,
            radarY = -0.15f
        )
    )

    private fun initialServices(): List<ServiceItem> = listOf(
        ServiceItem(
            id = "srv-00",
            name = "Child Care & Supervision",
            specialty = ProviderSpecialty.CHILD_CARE_GIVER,
            description = "Nurturing in-home childcare: feeding, hygiene, play, naps and safety monitoring.",
            durationMinutes = 240,
            basePrice = 8000,
            isUrgent = false,
            requiredSupplies = emptyList()
        ),
        ServiceItem(
            id = "srv-00b",
            name = "Elderly Care & Companionship",
            specialty = ProviderSpecialty.ELDERLY_CARE_GIVER,
            description = "Mobility assistance, medication reminders, companionship, hygiene and fall prevention for seniors.",
            durationMinutes = 240,
            basePrice = 9000,
            isUrgent = false,
            requiredSupplies = emptyList()
        ),
        ServiceItem(
            id = "srv-01",
            name = "Wound Dressing & Debridement",
            specialty = ProviderSpecialty.NURSE,
            description = "Aseptic wound cleaning, dressing change and infection monitoring at home.",
            durationMinutes = 30,
            basePrice = 15000,
            isUrgent = false,
            requiredSupplies = listOf("sup-01", "sup-02")
        ),
        ServiceItem(
            id = "srv-02",
            name = "IV Fluid Cannulation & Injections",
            specialty = ProviderSpecialty.NURSE,
            description = "Safe intravenous line placement, physician-prescribed infusion monitoring, or intramuscular injection administration.",
            durationMinutes = 60,
            basePrice = 18000,
            isUrgent = true,
            requiredSupplies = listOf("Normal Saline 500ml", "IV Infusion Set")
        ),
        ServiceItem(
            id = "srv-03",
            name = "Online Doctor Video Consultation & Assessment",
            specialty = ProviderSpecialty.DOCTOR,
            description = "Private online video/audio medical consultation, electronic prescription, lab test ordering, and clinical diagnosis by a licensed MD.",
            durationMinutes = 45,
            basePrice = 30000,
            isUrgent = false
        ),
        ServiceItem(
            id = "srv-04",
            name = "Post-Natal & Newborn Wellness Visit",
            specialty = ProviderSpecialty.NURSE,
            description = "Maternal episiotomy/C-section healing check, newborn umbilical cord dressing, jaundice screening, and lactation support.",
            durationMinutes = 60,
            basePrice = 22000,
            isUrgent = false
        ),
        ServiceItem(
            id = "srv-05",
            name = "In-Home Physiotherapy & Mobility Session",
            specialty = ProviderSpecialty.PHYSIOTHERAPIST,
            description = "Personalized manual therapy, stroke recovery gait retraining, joint decompression, and home mobility exercise coaching.",
            durationMinutes = 50,
            basePrice = 22000,
            isUrgent = false
        ),
        ServiceItem(
            id = "srv-06",
            name = "Mental Health & Stress De-escalation",
            specialty = ProviderSpecialty.MENTAL_HEALTH,
            description = "Safe, private one-on-one session for panic management, grief counseling, postpartum support, and emotional coping techniques.",
            durationMinutes = 50,
            basePrice = 20000,
            isUrgent = false
        ),
        ServiceItem(
            id = "srv-07",
            name = "Rapid Vitals & Diagnostic Screening",
            specialty = ProviderSpecialty.COMMUNITY_HEALTH,
            description = "On-the-spot blood pressure, heart rate, oxygen saturation, random blood glucose, and rapid malaria antigen test.",
            durationMinutes = 30,
            basePrice = 9500,
            isUrgent = true
        ),
        ServiceItem(
            id = "srv-08",
            name = "Confidential Sexual & Reproductive Health Care",
            specialty = ProviderSpecialty.SEXUAL_REPRODUCTIVE_HEALTH,
            description = "Private in-home SRHR consultation, family planning counseling, STI rapid testing, and reproductive wellness triage for men and women.",
            durationMinutes = 45,
            basePrice = 18000,
            isUrgent = false
        ),
        ServiceItem(
            id = "srv-09",
            name = "Male Reproductive, Fertility & Prostate Check",
            specialty = ProviderSpecialty.SEXUAL_REPRODUCTIVE_HEALTH,
            description = "Confidential andrologist assessment for prostate health, male fertility guidance, testosterone/hormonal review, and sexual health.",
            durationMinutes = 45,
            basePrice = 30000,
            isUrgent = false
        ),
        ServiceItem(
            id = "srv-10",
            name = "Cervical HPV & Pap Smear Home Screening",
            specialty = ProviderSpecialty.SEXUAL_REPRODUCTIVE_HEALTH,
            description = "In-home sterile swab specimen collection for cervical HPV and cervical cancer screening administered by a Nurse Midwife.",
            durationMinutes = 30,
            basePrice = 20000,
            isUrgent = false
        ),
        ServiceItem(
            id = "srv-11",
            name = "Doorstep Primary Health & Immunization Visit",
            specialty = ProviderSpecialty.PRIMARY_HEALTH_WORKER,
            description = "Primary healthcare triage, routine childhood/adult immunization audit, basic wound assessment, and health worker home visit.",
            durationMinutes = 35,
            basePrice = 8500,
            isUrgent = false
        )
    )

    private fun initialSupplies(): List<MedicalSupply> = listOf(
        MedicalSupply(
            id = "sup-01",
            name = "Sterile Gauze & Bandage Dressing Pack",
            category = "Wound & Dressings",
            price = 3500,
            description = "Hospital-grade sterile 10x10cm non-woven gauze swabs (10 pcs) with micropore hypoallergenic tape.",
            inStock = true,
            unit = "1 Pack"
        ),
        MedicalSupply(
            id = "sup-02",
            name = "Povidone-Iodine Antiseptic (100ml)",
            category = "Wound & Dressings",
            price = 2200,
            description = "10% w/v topical antiseptic solution for surgical disinfection and wound cleansing.",
            inStock = true,
            unit = "1 Bottle"
        ),
        MedicalSupply(
            id = "sup-03",
            name = "LifeSavers Emergency First-Aid Response Kit",
            category = "First Aid",
            price = 14500,
            description = "Complete trauma pouch with scissors, bandages, tourniquet, sterile alcohol pads, burn gel, and CPR shield.",
            inStock = true,
            unit = "Complete Kit"
        ),
        MedicalSupply(
            id = "sup-04",
            name = "Fingertip Pulse Oximeter SpO2",
            category = "Diagnostics",
            price = 8500,
            description = "OLED display device accurately tracking blood oxygen saturation levels and pulse rate within 6 seconds.",
            inStock = true,
            unit = "1 Device"
        ),
        MedicalSupply(
            id = "sup-05",
            name = "Digital Infrared Forehead Thermometer",
            category = "Diagnostics",
            price = 11000,
            description = "No-contact 1-second body temperature reading with high-fever color display alert.",
            inStock = true,
            unit = "1 Device"
        ),
        MedicalSupply(
            id = "sup-06",
            name = "Box of 100 Nitrile Medical Gloves",
            category = "Protective Gear",
            price = 5500,
            description = "Powder-free, textured fingertips, puncture-resistant medical examination grade gloves (Size M/L).",
            inStock = true,
            unit = "Box (100 pcs)"
        ),
        MedicalSupply(
            id = "sup-07",
            name = "Sterile Normal Saline 500ml & IV Infusion Set",
            category = "Medication Aids",
            price = 4200,
            description = "0.9% Sodium Chloride IV solution with sterile gravity drip infusion line and roller clamp.",
            inStock = true,
            unit = "1 Set"
        ),
        MedicalSupply(
            id = "sup-08",
            name = "Automatic Upper Arm Blood Pressure Monitor",
            category = "Diagnostics",
            price = 19500,
            description = "Digital BP cuff with irregular heartbeat detector, WHO classification bar, and 99-reading memory.",
            inStock = true,
            unit = "1 Monitor"
        )
    )

    private fun initialBookings(): List<Booking> = listOf(
        Booking(
            id = "LIFESAVERS-89412",
            serviceId = "srv-01",
            serviceName = "Wound Dressing & Sterile Care",
            providerId = "prov-01",
            providerName = "Nurse Amara Okonkwo",
            providerTitle = "RN, Critical & Post-Op Specialist",
            providerSpecialty = ProviderSpecialty.NURSE,
            patientName = "Chidi Anya",
            patientAddress = "14 Adeola Odeku St, Victoria Island, Lagos",
            patientPhone = "+234 803 555 0192",
            symptomNotes = "Post-cesarean incision site needs antiseptic cleaning and fresh sterile dressing. Slight redness around stitch line.",
            status = BookingStatus.EN_ROUTE,
            baseCost = 16000,
            suppliesCost = 5700,
            totalCost = 21700,
            etaMinutes = 7,
            suppliesIncluded = listOf(
                CartItem(
                    supply = MedicalSupply(
                        id = "sup-01",
                        name = "Sterile Gauze & Bandage Dressing Pack",
                        category = "Wound & Dressings",
                        price = 3500,
                        description = "Sterile gauze",
                        inStock = true,
                        unit = "1 Pack"
                    ),
                    quantity = 1
                ),
                CartItem(
                    supply = MedicalSupply(
                        id = "sup-02",
                        name = "Povidone-Iodine Antiseptic (100ml)",
                        category = "Wound & Dressings",
                        price = 2200,
                        description = "10% antiseptic",
                        inStock = true,
                        unit = "1 Bottle"
                    ),
                    quantity = 1
                )
            )
        ),
        Booking(
            id = "LIFESAVERS-88204",
            serviceId = "srv-03",
            serviceName = "Doctor Home Consultation & Assessment",
            providerId = "prov-02",
            providerName = "Dr. Babatunde Vance",
            providerTitle = "MBBS, FWACP, Family & Geriatric Physician",
            providerSpecialty = ProviderSpecialty.DOCTOR,
            patientName = "Chidi Anya",
            patientAddress = "14 Adeola Odeku St, Victoria Island, Lagos",
            patientPhone = "+234 803 555 0192",
            symptomNotes = "Persistent nocturnal dry cough and mild chest tightness for 4 days.",
            status = BookingStatus.COMPLETED,
            timestampMillis = System.currentTimeMillis() - 86400000L * 3,
            baseCost = 30000,
            suppliesCost = 0,
            totalCost = 30000,
            etaMinutes = 0,
            vitalsRecorded = "BP: 124/80 mmHg • HR: 74 bpm • Temp: 36.6°C • SpO2: 98%"
        ),
        Booking(
            id = "LIFESAVERS-87115",
            serviceId = "srv-07",
            serviceName = "Rapid Malaria RDT & Vitals",
            providerId = "prov-05",
            providerName = "Halima Musa",
            providerTitle = "CHEW, Certified Community Health Worker",
            providerSpecialty = ProviderSpecialty.COMMUNITY_HEALTH,
            patientName = "Chidi Anya",
            patientAddress = "14 Adeola Odeku St, Victoria Island, Lagos",
            patientPhone = "+234 803 555 0192",
            symptomNotes = "Chills and sudden evening fever spike. Rule out plasmodium infection.",
            status = BookingStatus.COMPLETED,
            timestampMillis = System.currentTimeMillis() - 86400000L * 10,
            baseCost = 9500,
            suppliesCost = 0,
            totalCost = 9500,
            etaMinutes = 0,
            vitalsRecorded = "Malaria RDT: Negative • Blood Sugar: 94 mg/dL • Temp: 37.1°C"
        ),
        Booking(
            id = "LIFESAVERS-86501",
            serviceId = "srv-05",
            serviceName = "In-Home Physiotherapy & Mobility Session",
            providerId = "prov-03",
            providerName = "Nkechi Eze",
            providerTitle = "B.PT, Orthopedic & Neuro-Rehab Physiotherapist",
            providerSpecialty = ProviderSpecialty.PHYSIOTHERAPIST,
            patientName = "Chidi Anya",
            patientAddress = "14 Adeola Odeku St, Victoria Island, Lagos",
            patientPhone = "+234 803 555 0192",
            symptomNotes = "Lumbar spine stiffness post-desk work, sciatica nerve release exercise.",
            status = BookingStatus.COMPLETED,
            timestampMillis = System.currentTimeMillis() - 86400000L * 21,
            baseCost = 22000,
            suppliesCost = 0,
            totalCost = 22000,
            etaMinutes = 0,
            vitalsRecorded = "Lumbar flexion improved 20° • Pain score: 2/10"
        )
    )

    private fun initialChatMessages(): List<ChatMessage> = listOf(
        ChatMessage(
            id = "msg-01",
            bookingId = "LIFESAVERS-89412",
            senderName = "Nurse Amara Okonkwo",
            isFromPatient = false,
            text = "Hello Chidi! I have accepted your dispatch request. I am packing sterile gauze and antiseptic now.",
            timestamp = "10:14 AM"
        ),
        ChatMessage(
            id = "msg-02",
            bookingId = "LIFESAVERS-89412",
            senderName = "You (Patient)",
            isFromPatient = true,
            text = "Thank you Nurse Amara. Please note the gate code is #402. The incision is on my lower abdomen.",
            timestamp = "10:16 AM"
        ),
        ChatMessage(
            id = "msg-03",
            bookingId = "LIFESAVERS-89412",
            senderName = "Nurse Amara Okonkwo",
            isFromPatient = false,
            text = "Understood. I am on Adeola Odeku St now, ETA is about 7 minutes. Please rest and keep the area dry.",
            timestamp = "10:18 AM"
        )
    )

    private fun initialReviews(): List<PatientReview> = listOf(
        PatientReview(
            id = "rev-01",
            bookingId = "LIFESAVERS-88204",
            providerId = "prov-02",
            providerName = "Dr. Babatunde Vance",
            providerTitle = "MBBS, FWACP, Family & Geriatric Physician",
            providerSpecialty = ProviderSpecialty.DOCTOR,
            serviceName = "Doctor Home Consultation & Assessment",
            rating = 5,
            testimonial = "Dr. Vance was exceptional. Arrived right on time at our Victoria Island residence with complete diagnostic gear. He spent 45 minutes listening to my symptoms, examined my chest thoroughly, and accurately diagnosed my bronchial allergy. His warmth and clinical clarity gave my family incredible peace of mind.",
            dateText = "3 days ago",
            tags = listOf("Punctual Arrival", "Thorough Examination", "Gentle Bedside Manner", "Clear Explanations"),
            verifiedBooking = true,
            avatarInitials = "BV"
        ),
        PatientReview(
            id = "rev-02",
            bookingId = "LIFESAVERS-87115",
            providerId = "prov-05",
            providerName = "Halima Musa",
            providerTitle = "CHEW, Certified Community Health Worker",
            providerSpecialty = ProviderSpecialty.COMMUNITY_HEALTH,
            serviceName = "Rapid Malaria RDT & Vitals",
            rating = 5,
            testimonial = "Halima arrived within 8 minutes of dispatch! Her needle technique for the finger prick was completely painless and her sterile field setup was textbook hospital-grade. She confirmed my negative malaria status in 15 minutes and checked my vitals. Truly a life-saving doorstep service.",
            dateText = "10 days ago",
            tags = listOf("Painless Needle Technique", "Hospital-Grade Hygiene", "Rapid RDT Results", "Reassuring"),
            verifiedBooking = true,
            avatarInitials = "HM"
        ),
        PatientReview(
            id = "rev-03",
            bookingId = "LIFESAVERS-86501",
            providerId = "prov-03",
            providerName = "Nkechi Eze",
            providerTitle = "B.PT, Orthopedic & Neuro-Rehab Physiotherapist",
            providerSpecialty = ProviderSpecialty.PHYSIOTHERAPIST,
            serviceName = "In-Home Physiotherapy & Mobility Session",
            rating = 5,
            testimonial = "Outstanding physical therapy session. Nkechi helped relieve acute lower back stiffness that had kept me off work. She showed me safe stretching postures and ergonomic adjustments for my home office desk. My pain dropped from 8/10 to 2/10 in a single visit.",
            dateText = "3 weeks ago",
            tags = listOf("Pain Relief Achieved", "Ergonomic Coaching", "Patient & Motivating", "Professional"),
            verifiedBooking = true,
            avatarInitials = "NE"
        )
    )
}

typealias OmaRepository = LifeSaversRepository

