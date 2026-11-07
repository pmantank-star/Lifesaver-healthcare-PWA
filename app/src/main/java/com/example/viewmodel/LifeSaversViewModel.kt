package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.credential.LifeSaversDatabase
import com.example.data.LifeSaversRepository
import com.example.model.Booking
import com.example.model.CartItem
import com.example.model.ChatMessage
import com.example.model.Gender
import com.example.model.HealthProvider
import com.example.model.MedicalSupply
import com.example.model.PatientReview
import com.example.model.ProviderCredential
import com.example.model.ProviderSpecialty
import com.example.model.ProviderWallet
import com.example.model.ServiceItem
import com.example.model.UserRole
import com.example.location.LocationManager
import com.example.location.UserCoordinates
import com.example.model.BookingStatus
import com.example.model.FemTechCycleData
import com.example.model.LifeSaversNotification
import com.example.notification.LifeSaversNotificationManager
import com.example.supabase.SupabaseConfig
import com.example.supabase.SupabaseSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LifeSaversViewModel(
    private val repository: LifeSaversRepository = LifeSaversRepository()
) : ViewModel() {

    val syncManager: SupabaseSyncManager = SupabaseSyncManager(viewModelScope)

    private var locationManager: LocationManager? = null
    private var notificationManager: LifeSaversNotificationManager? = null

    // Tracked user coordinates
    private val _userCoordinates = MutableStateFlow(LocationManager.DEFAULT_COORDINATES)
    val userCoordinates: StateFlow<UserCoordinates> = _userCoordinates.asStateFlow()

    // Real-time In-App Notification alert state
    private val _inAppNotification = MutableStateFlow<LifeSaversNotification?>(null)
    val inAppNotification: StateFlow<LifeSaversNotification?> = _inAppNotification.asStateFlow()

    // Initial asset and service initialization tracking
    private val _isAssetInitializing = MutableStateFlow(true)
    val isAssetInitializing: StateFlow<Boolean> = _isAssetInitializing.asStateFlow()

    private val _assetInitMessage = MutableStateFlow("Initializing LifeSavers network...")
    val assetInitMessage: StateFlow<String> = _assetInitMessage.asStateFlow()

    private val _assetInitProgress = MutableStateFlow(0.20f)
    val assetInitProgress: StateFlow<Float> = _assetInitProgress.asStateFlow()

    // Specified notification radius for healthcare worker discovery
    val alertRadiusKm: Float = 5.0f

    val currentRole: StateFlow<UserRole> = repository.currentRole
    val userGender: StateFlow<Gender> = repository.userGender
    val userAddress: StateFlow<String> = repository.userAddress
    val cart: StateFlow<List<CartItem>> = repository.cart
    val activeBooking: StateFlow<Booking?> = repository.activeBooking
    val bookings: StateFlow<List<Booking>> = repository.bookings
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val providerCredential: StateFlow<ProviderCredential> = repository.providerCredential
    val isProviderOnline: StateFlow<Boolean> = repository.isProviderOnline
    val incomingGigAlert: StateFlow<Booking?> = repository.incomingGigAlert
    val providerWallet: StateFlow<ProviderWallet> = repository.providerWallet
    val allCredentials: StateFlow<List<com.example.credential.CredentialEntity>> = repository.allCredentials

    // UNICEF FemTech Suite
    val femTechCycleData = repository.femTechCycleData
    val pregnancyTrackerData = repository.pregnancyTrackerData
    val maternalHealthScreenings = repository.maternalHealthScreenings

    fun updateFemTechCycleData(data: FemTechCycleData) {
        repository.updateFemTechCycleData(data)
    }

    fun setUserGender(gender: Gender) {
        repository.setUserGender(gender)
    }

    // Search and filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSpecialty = MutableStateFlow<ProviderSpecialty?>(null)
    val selectedSpecialty: StateFlow<ProviderSpecialty?> = _selectedSpecialty.asStateFlow()

    // Navigation and selection targets
    private val _selectedProvider = MutableStateFlow<HealthProvider?>(null)
    val selectedProvider: StateFlow<HealthProvider?> = _selectedProvider.asStateFlow()

    private val _selectedService = MutableStateFlow<ServiceItem?>(null)
    val selectedService: StateFlow<ServiceItem?> = _selectedService.asStateFlow()

    // Filtered providers based on search & specialty
    val filteredProviders: StateFlow<List<HealthProvider>> = combine(
        repository.providers,
        _searchQuery,
        _selectedSpecialty
    ) { providers, query, specialty ->
        providers.filter { provider ->
            val matchesSpecialty = specialty == null || provider.specialty == specialty
            val matchesQuery = query.isBlank() ||
                provider.name.contains(query, ignoreCase = true) ||
                provider.title.contains(query, ignoreCase = true) ||
                provider.services.any { it.contains(query, ignoreCase = true) }
            matchesSpecialty && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All services
    val services: StateFlow<List<ServiceItem>> = repository.services

    // Filtered services
    val filteredServices: StateFlow<List<ServiceItem>> = combine(
        repository.services,
        _searchQuery,
        _selectedSpecialty
    ) { services, query, specialty ->
        services.filter { item ->
            val matchesSpecialty = specialty == null || item.specialty == specialty
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true)
            matchesSpecialty && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Supplies
    val supplies: StateFlow<List<MedicalSupply>> = repository.supplies

    // Patient Personal Profile Data
    val patientProfile: StateFlow<com.example.data.PatientProfileData> = repository.patientProfile
    val favoriteProviderIds: StateFlow<Set<String>> = repository.favoriteProviderIds
    val favoriteSupplyIds: StateFlow<Set<String>> = repository.favoriteSupplyIds
    val patientReviews: StateFlow<List<PatientReview>> = repository.patientReviews

    val favoriteProviders: StateFlow<List<HealthProvider>> = combine(
        repository.providers,
        repository.favoriteProviderIds
    ) { providersList, favIds ->
        providersList.filter { it.id in favIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSupplies: StateFlow<List<MedicalSupply>> = combine(
        repository.supplies,
        repository.favoriteSupplyIds
    ) { suppliesList, favIds ->
        suppliesList.filter { it.id in favIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updatePatientProfile(updated: com.example.data.PatientProfileData) {
        repository.updatePatientProfile(updated)
    }

    fun submitPatientReview(review: PatientReview) {
        repository.submitReview(review)
    }

    fun toggleFavoriteProvider(providerId: String) {
        repository.toggleFavoriteProvider(providerId)
    }

    fun toggleFavoriteSupply(supplyId: String) {
        repository.toggleFavoriteSupply(supplyId)
    }

    fun switchRole(role: UserRole) {
        repository.switchRole(role)
    }

    fun initLocationManager(manager: LocationManager) {
        this.locationManager = manager
        viewModelScope.launch {
            manager.trackLocationUpdates().collect { coords ->
                updateCoordinatesAndRank(coords)
            }
        }
    }

    fun refreshLocation() {
        val manager = locationManager ?: return
        viewModelScope.launch {
            val coords = manager.getLastKnownLocation()
            updateCoordinatesAndRank(coords)
        }
    }

    fun updateCoordinatesAndRank(coords: UserCoordinates) {
        _userCoordinates.value = coords
        val manager = locationManager ?: return
        val ranked = manager.matchAndRankProviders(coords, repository.providers.value)
        repository.updateProviderLocations(ranked)
    }

    fun updateAddress(newAddress: String) {
        repository.updateAddress(newAddress)
        val presetCoords = when {
            newAddress.contains("Lekki", ignoreCase = true) -> UserCoordinates(6.4474, 3.4723)
            newAddress.contains("Ikeja", ignoreCase = true) -> UserCoordinates(6.5944, 3.3541)
            newAddress.contains("Abuja", ignoreCase = true) -> UserCoordinates(9.0765, 7.4782)
            else -> UserCoordinates(6.4281, 3.4219)
        }
        updateCoordinatesAndRank(presetCoords)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectSpecialty(specialty: ProviderSpecialty?) {
        _selectedSpecialty.value = if (_selectedSpecialty.value == specialty) null else specialty
    }

    fun selectProvider(provider: HealthProvider?) {
        _selectedProvider.value = provider
    }

    fun selectService(service: ServiceItem?) {
        _selectedService.value = service
    }

    fun addToCart(supply: MedicalSupply) {
        repository.addToCart(supply)
    }

    fun removeFromCart(supplyId: String) {
        repository.removeFromCart(supplyId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun initNotificationManager(manager: LifeSaversNotificationManager) {
        this.notificationManager = manager
        viewModelScope.launch {
            manager.inAppNotification.collect { notif ->
                _inAppNotification.value = notif
            }
        }
    }

    fun dismissInAppNotification() {
        notificationManager?.dismissInAppNotification()
        _inAppNotification.value = null
    }

    fun bookRequestedService(
        patientName: String,
        patientAddress: String,
        patientPhone: String,
        symptomNotes: String,
        includeCartSupplies: Boolean
    ): Booking? {
        val service = _selectedService.value ?: repository.services.value.firstOrNull() ?: return null
        val suppliesToAttach = if (includeCartSupplies) cart.value else emptyList()

        val booking = repository.createBooking(
            service = service,
            preferredProvider = _selectedProvider.value,
            patientName = patientName,
            patientAddress = patientAddress,
            patientPhone = patientPhone,
            symptomNotes = symptomNotes,
            suppliesIncluded = suppliesToAttach
        )

        if (includeCartSupplies) {
            repository.clearCart()
        }

        // Notify all verified providers within specified radius (e.g. 5.0 km) immediately
        val nearbyProviders = repository.providers.value.filter {
            it.distanceKm <= alertRadiusKm && it.isOnline
        }
        notificationManager?.notifyNearbyProvidersOfRequest(
            booking = booking,
            radiusKm = alertRadiusKm,
            notifiedProvidersCount = nearbyProviders.size.coerceAtLeast(1)
        )

        // Push to Supabase Cloud Backend
        viewModelScope.launch {
            syncManager.pushBooking(booking)
        }

        return booking
    }

    fun advanceBookingStatus(bookingId: String) {
        val prev = repository.bookings.value.find { it.id == bookingId }
        repository.advanceBookingStatus(bookingId)
        val updated = repository.bookings.value.find { it.id == bookingId }

        if (prev != null && updated != null) {
            // Check state transition to notify patient
            if (prev.status == BookingStatus.SEARCHING && (updated.status == BookingStatus.MATCHED || updated.status == BookingStatus.EN_ROUTE)) {
                notificationManager?.notifyPatientOfAcceptance(updated)
            } else if (updated.status == BookingStatus.ARRIVED && prev.status != BookingStatus.ARRIVED) {
                notificationManager?.notifyPatientOfArrival(updated)
            } else if (updated.status == BookingStatus.COMPLETED && prev.status != BookingStatus.COMPLETED) {
                notificationManager?.notifyPatientOfCompletion(updated)
            }

            // Sync updated status to Supabase Cloud
            viewModelScope.launch {
                syncManager.updateBookingStatus(updated.id, updated.status, updated.etaMinutes)
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        repository.cancelBooking(bookingId)
    }

    fun sendChatMessage(text: String, isEmergency: Boolean = false) {
        if (text.isNotBlank()) {
            val msg = repository.sendChatMessage(text, isEmergency)
            viewModelScope.launch {
                val result = syncManager.pushChatMessage(msg)
                result.onSuccess { created ->
                    repository.updateChatMessageStatus(msg.id, com.example.model.ChatMessageStatus.DELIVERED)
                    repository.addOrUpdateChatMessage(created.copy(status = com.example.model.ChatMessageStatus.DELIVERED))
                }
            }
        }
    }

    fun markChatRead() {
        val msgs = repository.chatMessages.value
        msgs.filter { !it.isFromPatient && it.status != com.example.model.ChatMessageStatus.READ }.forEach { m ->
            repository.updateChatMessageStatus(m.id, com.example.model.ChatMessageStatus.READ)
        }
    }

    fun toggleProviderOnline() {
        repository.toggleProviderOnline()
    }

    fun dismissGigAlert() {
        repository.dismissGigAlert()
    }

    fun acceptGigAlert() {
        val gig = incomingGigAlert.value
        repository.acceptGigAlert()
        if (gig != null) {
            val updated = repository.bookings.value.find { it.id == gig.id } ?: gig.copy(status = BookingStatus.MATCHED)
            notificationManager?.notifyPatientOfAcceptance(updated)
            viewModelScope.launch {
                syncManager.updateBookingStatus(updated.id, updated.status, updated.etaMinutes)
            }
        }
    }

    private var credentialService: com.example.credential.CredentialVerificationService? = null
    private var credentialDao: com.example.credential.CredentialDao? = null

    fun initDatabase(context: android.content.Context) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val db = LifeSaversDatabase.getInstance(context)
            val dao = db.credentialDao()
            credentialDao = dao
            val service = com.example.credential.CredentialVerificationService(dao)
            credentialService = service
            repository.setVerificationService(service)

            // Seed initial credentials into Room DB if empty
            val initialCreds = repository.allCredentials.value
            initialCreds.forEach { cred ->
                if (dao.getCredentialById(cred.id) == null) {
                    dao.insertCredential(cred)
                }
            }

            // Continuously sync Room DB changes back to ViewModel/Repository StateFlow
            dao.getAllCredentials().collect { dbEntities ->
                if (dbEntities.isNotEmpty()) {
                    repository.updateCredentialsFromDb(dbEntities)
                }
            }
        }
    }

    fun initializeAppAssets(
        context: android.content.Context,
        locationManager: LocationManager? = null,
        notificationManager: LifeSaversNotificationManager? = null
    ) {
        viewModelScope.launch {
            _isAssetInitializing.value = true
            _assetInitProgress.value = 0.20f
            _assetInitMessage.value = "Restoring clinical database & credentials..."
            initDatabase(context)

            // Initialize Supabase configuration and sync (realtime subscriptions)
            SupabaseConfig.init(context)
            syncManager.checkConfig()
            if (SupabaseConfig.isConfigured) {
                syncManager.seedInitialProvidersIfEmpty(repository.providers.value)
            }
            syncManager.startRealtimeSubscriptions(
                onBookingsSynced = { remoteBookings ->
                    repository.mergeRemoteBookings(remoteBookings)
                },
                onChatSynced = { remoteMessages ->
                    repository.mergeRemoteChatMessages(remoteMessages)
                },
                onProvidersSynced = { remoteProviders ->
                    repository.updateProvidersFromRemote(remoteProviders)
                },
                onChatMessageEvent = { msg ->
                    repository.addOrUpdateChatMessage(msg)
                }
            )

            if (locationManager != null) {
                _assetInitProgress.value = 0.55f
                _assetInitMessage.value = "Calibrating doorstep dispatch radar..."
                initLocationManager(locationManager)
            }

            if (notificationManager != null) {
                _assetInitProgress.value = 0.75f
                _assetInitMessage.value = "Connecting emergency notification dispatch..."
                initNotificationManager(notificationManager)
            }

            _assetInitProgress.value = 0.90f
            _assetInitMessage.value = "Synchronizing provider catalog & supplies..."
            kotlinx.coroutines.delay(400)

            _assetInitProgress.value = 1.0f
            _assetInitMessage.value = "Clinical assets ready"
            kotlinx.coroutines.delay(180)
            _isAssetInitializing.value = false
        }
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
    ) {
        val entity = repository.submitCredential(
            fullName = fullName,
            specialty = specialty,
            council = council,
            licenseNumber = licenseNumber,
            docName = docName,
            nationalIdType = nationalIdType,
            nationalIdRaw = nationalIdRaw,
            expiryDate = expiryDate,
            documentHash = documentHash
        )
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            credentialDao?.insertCredential(entity)
        }
    }

    fun adminVerifyCredential(
        credentialId: String,
        approve: Boolean,
        adminId: String = "ADM-LEAD-HEALTH",
        notes: String = "Verified practicing license with board registry and reviewed ID."
    ) {
        val updated = repository.adminVerifyCredential(credentialId, approve, adminId, notes)
        if (updated != null) {
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                credentialDao?.updateAdminVerification(
                    id = credentialId,
                    status = updated.status,
                    adminId = adminId,
                    notes = notes,
                    timestamp = System.currentTimeMillis()
                )
            }
            notificationManager?.notifyProviderOfCredentialResult(
                providerName = updated.fullName,
                isApproved = approve,
                adminNotes = notes
            )
        }
    }
}

typealias OmaViewModel = LifeSaversViewModel
