package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.LaunchedEffect
import android.os.Build
import com.example.model.Gender
import com.example.model.NotificationType
import com.example.notification.LifeSaversNotificationManager
import com.example.ui.components.LifeSaversNotificationBanner
import com.example.location.LocationManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.HealthProvider
import com.example.model.ProviderSpecialty
import com.example.model.ServiceItem
import com.example.model.UserRole
import com.example.ui.components.AssetLoadingOverlay
import com.example.ui.components.LifeSaversTopAppBar
import com.example.ui.components.SupabaseSyncSheet
import com.example.ui.components.UserProfileSheet
import com.example.ui.screens.BookingFlowScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CredentialsScreen
import com.example.ui.screens.DispatchTrackerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProviderDashboardScreen
import com.example.ui.screens.ProviderDetailScreen
import com.example.ui.screens.ProvidersScreen
import com.example.ui.theme.LifeSaversCoral
import com.example.ui.theme.LifeSaversLinen
import com.example.ui.theme.LifeSaversMint
import com.example.ui.theme.LifeSaversOnLinenContainer
import com.example.ui.theme.LifeSaversTeal
import com.example.ui.theme.LifeSaversTealContainer
import com.example.ui.theme.LifeSaversTheme
import com.example.model.LifeSaversUser
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FemTechHealthScreen
import com.example.ui.screens.LogoRevealScreen
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.LifeSaversViewModel

enum class LifeSaversDestination(val label: String) {
    HOME("Home"),
    PROVIDERS("Providers"),
    DISPATCH("Dispatches"),
    CHAT("Consult"),
    FEMTECH("FemTech Suite"),
    PROVIDER_DASHBOARD("Gig Radar"),
    CREDENTIALS("Credentials"),
    LOGO_REVEAL("Logo Reveal")
}

typealias OmaDestination = LifeSaversDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeSaversTheme {
                val viewModel: LifeSaversViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val context = LocalContext.current
                val locationManager = remember { LocationManager(context) }
                val notificationManager = remember { LifeSaversNotificationManager(context) }

                LaunchedEffect(Unit) {
                    authViewModel.initAuthManager(context)
                    viewModel.initializeAppAssets(
                        context = context,
                        locationManager = locationManager,
                        notificationManager = notificationManager
                    )
                }

                val authState by authViewModel.uiState.collectAsStateWithLifecycle()
                val isAssetInitializing by viewModel.isAssetInitializing.collectAsStateWithLifecycle()
                val assetInitMessage by viewModel.assetInitMessage.collectAsStateWithLifecycle()
                val assetInitProgress by viewModel.assetInitProgress.collectAsStateWithLifecycle()
                var showLogoRevealOnStart by remember { mutableStateOf(true) }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (showLogoRevealOnStart) {
                        LogoRevealScreen(
                            onFinish = { showLogoRevealOnStart = false }
                        )
                    } else if (authState.currentUser == null) {
                        AuthScreen(
                            authViewModel = authViewModel,
                            onAuthSuccess = { user ->
                                viewModel.switchRole(user.role)
                            },
                            onWatchLogoReveal = {
                                showLogoRevealOnStart = true
                            }
                        )
                    } else {
                        LifeSaversApp(
                            viewModel = viewModel,
                            authViewModel = authViewModel,
                            currentUser = authState.currentUser,
                            onWatchLogoReveal = {
                                showLogoRevealOnStart = true
                            }
                        )
                    }

                    // Loading indicator overlay during initial asset initialization (shown after reveal)
                    if (!showLogoRevealOnStart) {
                        AssetLoadingOverlay(
                            isInitializing = isAssetInitializing,
                            statusMessage = assetInitMessage,
                            progress = assetInitProgress,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LifeSaversApp(
    viewModel: LifeSaversViewModel,
    authViewModel: AuthViewModel? = null,
    currentUser: LifeSaversUser? = null,
    onWatchLogoReveal: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val userAddress by viewModel.userAddress.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val providers by viewModel.filteredProviders.collectAsStateWithLifecycle()
    val services by viewModel.filteredServices.collectAsStateWithLifecycle()
    val activeBooking by viewModel.activeBooking.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val providerCredential by viewModel.providerCredential.collectAsStateWithLifecycle()
    val isProviderOnline by viewModel.isProviderOnline.collectAsStateWithLifecycle()
    val incomingGigAlert by viewModel.incomingGigAlert.collectAsStateWithLifecycle()
    val providerWallet by viewModel.providerWallet.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedSpecialty by viewModel.selectedSpecialty.collectAsStateWithLifecycle()
    val selectedProvider by viewModel.selectedProvider.collectAsStateWithLifecycle()
    val selectedService by viewModel.selectedService.collectAsStateWithLifecycle()
    val supplies by viewModel.supplies.collectAsStateWithLifecycle()
    val inAppNotification by viewModel.inAppNotification.collectAsStateWithLifecycle()
    val userCoordinates by viewModel.userCoordinates.collectAsStateWithLifecycle()
    val allCredentials by viewModel.allCredentials.collectAsStateWithLifecycle()

    val femTechCycleData by viewModel.femTechCycleData.collectAsStateWithLifecycle()
    val pregnancyTrackerData by viewModel.pregnancyTrackerData.collectAsStateWithLifecycle()
    val maternalHealthScreenings by viewModel.maternalHealthScreenings.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    val notificationManager = remember { LifeSaversNotificationManager(context) }

    // Request permissions for location and notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permsToRequest = mutableListOf<String>()
        if (!locationManager.hasLocationPermission()) {
            permsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!notificationManager.hasNotificationPermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permsToRequest.toTypedArray())
        }
    }

    var currentScreen by remember { mutableStateOf(LifeSaversDestination.HOME) }
    var inDetailView by remember { mutableStateOf(false) }
    var inBookingFlow by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showSupabaseSheet by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    val syncStatus by viewModel.syncManager.syncStatus.collectAsStateWithLifecycle()
    val patientProfile by viewModel.patientProfile.collectAsStateWithLifecycle()
    val favoriteProviders by viewModel.favoriteProviders.collectAsStateWithLifecycle()
    val favoriteSupplies by viewModel.favoriteSupplies.collectAsStateWithLifecycle()
    val allBookings by viewModel.bookings.collectAsStateWithLifecycle()
    val patientReviews by viewModel.patientReviews.collectAsStateWithLifecycle()
    val userGender by viewModel.userGender.collectAsStateWithLifecycle()

    // Synchronize currentScreen default when role toggles
    fun toggleRole() {
        val nextRole = if (currentRole == UserRole.PATIENT) UserRole.PROVIDER else UserRole.PATIENT
        viewModel.switchRole(nextRole)
        inDetailView = false
        inBookingFlow = false
        currentScreen = if (nextRole == UserRole.PATIENT) LifeSaversDestination.HOME else LifeSaversDestination.PROVIDER_DASHBOARD
    }

    Scaffold(
        topBar = {
            LifeSaversTopAppBar(
                currentRole = currentRole,
                userAddress = userAddress,
                onRoleToggle = { toggleRole() },
                onLocationClick = { showAddressDialog = true },
                currentUser = currentUser,
                onSignOut = { authViewModel?.signOut() },
                onLogoClick = onWatchLogoReveal ?: {
                    inDetailView = false
                    inBookingFlow = false
                    currentScreen = LifeSaversDestination.LOGO_REVEAL
                },
                syncStatus = syncStatus,
                onCloudSyncClick = { showSupabaseSheet = true },
                onProfileClick = { showProfileSheet = true }
            )
        },
        bottomBar = {
            if (!inDetailView && !inBookingFlow && currentScreen != LifeSaversDestination.CHAT) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    if (currentRole == UserRole.PATIENT) {
                        NavigationBarItem(
                            selected = currentScreen == LifeSaversDestination.HOME,
                            onClick = { currentScreen = LifeSaversDestination.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LifeSaversTeal,
                                selectedTextColor = LifeSaversTeal,
                                indicatorColor = LifeSaversTealContainer
                            ),
                            modifier = Modifier.testTag("nav_home")
                        )
                        NavigationBarItem(
                            selected = currentScreen == LifeSaversDestination.PROVIDERS,
                            onClick = { currentScreen = LifeSaversDestination.PROVIDERS },
                            icon = { Icon(Icons.Default.People, contentDescription = "Providers") },
                            label = { Text("Providers", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LifeSaversTeal,
                                selectedTextColor = LifeSaversTeal,
                                indicatorColor = LifeSaversTealContainer
                            ),
                            modifier = Modifier.testTag("nav_providers")
                        )
                        NavigationBarItem(
                            selected = currentScreen == LifeSaversDestination.FEMTECH,
                            onClick = { currentScreen = LifeSaversDestination.FEMTECH },
                            icon = { Icon(Icons.Default.Female, contentDescription = "FemTech") },
                            label = { Text("FemTech", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LifeSaversTeal,
                                selectedTextColor = LifeSaversTeal,
                                indicatorColor = LifeSaversTealContainer
                            ),
                            modifier = Modifier.testTag("nav_femtech")
                        )
                        NavigationBarItem(
                            selected = currentScreen == LifeSaversDestination.DISPATCH,
                            onClick = { currentScreen = LifeSaversDestination.DISPATCH },
                            icon = { Icon(Icons.Default.ElectricBolt, contentDescription = "Dispatches") },
                            label = { Text("Tracker", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LifeSaversTeal,
                                selectedTextColor = LifeSaversTeal,
                                indicatorColor = LifeSaversTealContainer
                            ),
                            modifier = Modifier.testTag("nav_dispatch")
                        )
                        NavigationBarItem(
                            selected = currentScreen == LifeSaversDestination.CHAT,
                            onClick = { currentScreen = LifeSaversDestination.CHAT },
                            icon = { Icon(Icons.Default.Chat, contentDescription = "Consult") },
                            label = { Text("Consult", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LifeSaversTeal,
                                selectedTextColor = LifeSaversTeal,
                                indicatorColor = LifeSaversTealContainer
                            ),
                            modifier = Modifier.testTag("nav_chat")
                        )
                    } else {
                        // Provider Navigation
                        NavigationBarItem(
                            selected = currentScreen == LifeSaversDestination.PROVIDER_DASHBOARD,
                            onClick = { currentScreen = LifeSaversDestination.PROVIDER_DASHBOARD },
                            icon = { Icon(Icons.Default.NearMe, contentDescription = "Radar Gigs") },
                            label = { Text("Gig Radar", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LifeSaversOnLinenContainer,
                                selectedTextColor = LifeSaversOnLinenContainer,
                                indicatorColor = LifeSaversLinen
                            ),
                            modifier = Modifier.testTag("nav_provider_radar")
                        )
                        NavigationBarItem(
                            selected = currentScreen == LifeSaversDestination.CREDENTIALS,
                            onClick = { currentScreen = LifeSaversDestination.CREDENTIALS },
                            icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "Credentials") },
                            label = { Text("Credentials", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LifeSaversOnLinenContainer,
                                selectedTextColor = LifeSaversOnLinenContainer,
                                indicatorColor = LifeSaversLinen
                            ),
                            modifier = Modifier.testTag("nav_provider_credentials")
                        )
                        NavigationBarItem(
                            selected = currentScreen == LifeSaversDestination.CHAT,
                            onClick = { currentScreen = LifeSaversDestination.CHAT },
                            icon = { Icon(Icons.Default.Chat, contentDescription = "Consult") },
                            label = { Text("Consult", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LifeSaversCoral,
                                selectedTextColor = LifeSaversCoral,
                                indicatorColor = LifeSaversCoral.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_provider_chat")
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when {
                    inBookingFlow && selectedService != null -> {
                        BookingFlowScreen(
                            service = selectedService!!,
                            preferredProvider = selectedProvider,
                            cartItems = cart,
                            defaultAddress = userAddress,
                            onBack = { inBookingFlow = false },
                            onConfirmDispatch = { name, addr, ph, sym ->
                                viewModel.bookRequestedService(
                                    patientName = name,
                                    patientAddress = addr,
                                    patientPhone = ph,
                                    symptomNotes = sym,
                                    includeCartSupplies = false
                                )
                                inBookingFlow = false
                                currentScreen = LifeSaversDestination.DISPATCH
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    inDetailView && selectedProvider != null -> {
                        ProviderDetailScreen(
                            provider = selectedProvider!!,
                            onBack = { inDetailView = false },
                            onBookVisit = { prov ->
                                viewModel.selectProvider(prov)
                                inDetailView = false
                                inBookingFlow = true
                            },
                            onStartChat = { prov ->
                                viewModel.selectProvider(prov)
                                inDetailView = false
                                currentScreen = LifeSaversDestination.CHAT
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    screen == LifeSaversDestination.HOME -> {
                        HomeScreen(
                            providers = providers,
                            services = services,
                            activeBooking = activeBooking,
                            selectedSpecialty = selectedSpecialty,
                            onSelectSpecialty = { viewModel.selectSpecialty(it) },
                            onSelectProvider = { provider ->
                                viewModel.selectProvider(provider)
                                inDetailView = true
                            },
                            onSelectServiceForBooking = { service ->
                                viewModel.selectService(service)
                                inBookingFlow = true
                            },
                            onNavigateToDispatchTracker = {
                                currentScreen = LifeSaversDestination.DISPATCH
                            },
                            onOpenFemTechSuite = {
                                currentScreen = LifeSaversDestination.FEMTECH
                            },
                            currentUserGender = userGender,
                            onToggleGender = { viewModel.setUserGender(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    screen == LifeSaversDestination.FEMTECH -> {
                        if (currentRole == UserRole.PATIENT) {
                            FemTechHealthScreen(
                                cycleData = femTechCycleData,
                                pregnancyData = pregnancyTrackerData,
                                screenings = maternalHealthScreenings,
                                onUpdateCycleData = { updated ->
                                    viewModel.updateFemTechCycleData(updated)
                                },
                                onRequestMaternalDispatch = {
                                    val maternalService = services.firstOrNull { it.specialty == ProviderSpecialty.NURSE || it.name.contains("Vitals") } ?: services.first()
                                    viewModel.selectService(maternalService)
                                    inBookingFlow = true
                                },
                                onBack = { currentScreen = LifeSaversDestination.HOME },
                                userGender = userGender,
                                isProvider = false,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            FemTechHealthScreen(
                                cycleData = femTechCycleData,
                                pregnancyData = pregnancyTrackerData,
                                screenings = maternalHealthScreenings,
                                onUpdateCycleData = { updated ->
                                    viewModel.updateFemTechCycleData(updated)
                                },
                                onRequestMaternalDispatch = {
                                    val maternalService = services.firstOrNull { it.specialty == ProviderSpecialty.NURSE || it.name.contains("Vitals") } ?: services.first()
                                    viewModel.selectService(maternalService)
                                    inBookingFlow = true
                                },
                                onBack = { currentScreen = LifeSaversDestination.PROVIDER_DASHBOARD },
                                userGender = userGender,
                                isProvider = true,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    screen == LifeSaversDestination.PROVIDERS -> {
                        ProvidersScreen(
                            providers = providers,
                            searchQuery = searchQuery,
                            selectedSpecialty = selectedSpecialty,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onSelectSpecialty = { viewModel.selectSpecialty(it) },
                            onSelectProvider = { provider ->
                                viewModel.selectProvider(provider)
                                inDetailView = true
                            },
                            onDirectDispatch = { provider ->
                                viewModel.selectProvider(provider)
                                if (services.isNotEmpty()) {
                                    viewModel.selectService(services.first())
                                    inBookingFlow = true
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    screen == LifeSaversDestination.DISPATCH -> {
                        if (activeBooking != null) {
                            DispatchTrackerScreen(
                                booking = activeBooking!!,
                                onBack = { currentScreen = LifeSaversDestination.HOME },
                                onOpenChat = { currentScreen = LifeSaversDestination.CHAT },
                                onSimulateAdvance = { viewModel.advanceBookingStatus(activeBooking!!.id) },
                                onCancelBooking = { viewModel.cancelBooking(activeBooking!!.id) },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            currentScreen = LifeSaversDestination.HOME
                        }
                    }

                    screen == LifeSaversDestination.CHAT -> {
                        ChatScreen(
                            messages = chatMessages,
                            providerName = activeBooking?.providerName ?: selectedProvider?.name ?: "Provider",
                            onBack = { currentScreen = if (currentRole == UserRole.PATIENT) LifeSaversDestination.HOME else LifeSaversDestination.PROVIDER_DASHBOARD },
                            onSendMessage = { text, isEmergency -> viewModel.sendChatMessage(text, isEmergency) },
                            isOtherUserOnline = if (currentRole == UserRole.PATIENT) isProviderOnline else true,
                            onMarkRead = { viewModel.markChatRead() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    screen == LifeSaversDestination.PROVIDER_DASHBOARD -> {
                        ProviderDashboardScreen(
                            credential = providerCredential,
                            wallet = providerWallet,
                            isOnline = isProviderOnline,
                            incomingGig = incomingGigAlert,
                            onToggleOnline = { viewModel.toggleProviderOnline() },
                            onAcceptGig = { viewModel.acceptGigAlert() },
                            onDeclineGig = { viewModel.dismissGigAlert() },
                            onManageCredentials = { currentScreen = LifeSaversDestination.CREDENTIALS },
                            onTrackBooking = { currentScreen = LifeSaversDestination.DISPATCH },
                            onOpenMaternalCare = { currentScreen = LifeSaversDestination.FEMTECH },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    screen == LifeSaversDestination.CREDENTIALS -> {
                        CredentialsScreen(
                            currentCredential = providerCredential,
                            allCredentials = allCredentials,
                            onBack = { currentScreen = LifeSaversDestination.PROVIDER_DASHBOARD },
                            onSubmitCredential = { name, spec, council, licNum, doc, idType, idRaw, exp, hash ->
                                viewModel.submitCredential(name, spec, council, licNum, doc, idType, idRaw, exp, hash)
                            },
                            onAdminVerify = { id, approve, adminId, notes ->
                                viewModel.adminVerifyCredential(id, approve, adminId, notes)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    screen == LifeSaversDestination.LOGO_REVEAL -> {
                        LogoRevealScreen(
                            onFinish = {
                                currentScreen = if (currentRole == UserRole.PATIENT) LifeSaversDestination.HOME else LifeSaversDestination.PROVIDER_DASHBOARD
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Real-Time In-App Heads-Up Notification Banner
            LifeSaversNotificationBanner(
                notification = inAppNotification,
                onDismiss = { viewModel.dismissInAppNotification() },
                onActionClick = { notif ->
                    viewModel.dismissInAppNotification()
                    when (notif.type) {
                        NotificationType.PROVIDER_DISPATCH_ALERT -> {
                            viewModel.switchRole(UserRole.PROVIDER)
                            currentScreen = LifeSaversDestination.PROVIDER_DASHBOARD
                            inDetailView = false
                            inBookingFlow = false
                        }
                        NotificationType.PATIENT_ACCEPTED_ALERT,
                        NotificationType.PATIENT_ARRIVED_ALERT,
                        NotificationType.PATIENT_COMPLETED_ALERT -> {
                            viewModel.switchRole(UserRole.PATIENT)
                            currentScreen = LifeSaversDestination.DISPATCH
                            inDetailView = false
                            inBookingFlow = false
                        }
                        NotificationType.PROVIDER_CREDENTIAL_VERIFIED,
                        NotificationType.PROVIDER_CREDENTIAL_REJECTED -> {
                            viewModel.switchRole(UserRole.PROVIDER)
                            currentScreen = LifeSaversDestination.CREDENTIALS
                            inDetailView = false
                            inBookingFlow = false
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
            )
        }
    }

    // Doorstep Address Picker Dialog
    if (showAddressDialog) {
        var inputAddress by remember { mutableStateOf(userAddress) }
        val presets = listOf(
            "14 Adeola Odeku St, Victoria Island, Lagos",
            "Block 4, Admiralty Way, Lekki Phase 1, Lagos",
            "28 Isaac John St, Ikeja GRA, Lagos",
            "12 Aminu Kano Crescent, Wuse II, Abuja"
        )

        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = LifeSaversCoral)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Change Doorstep Address", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Set your exact address to match with the closest nearby health worker:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LifeSaversTealContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (locationManager.hasLocationPermission()) {
                                    viewModel.refreshLocation()
                                    val coords = viewModel.userCoordinates.value
                                    inputAddress = "GPS: Lat ${String.format("%.4f", coords.latitude)}, Lon ${String.format("%.4f", coords.longitude)}"
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = LifeSaversTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Use Current GPS Location", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LifeSaversTeal)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputAddress,
                        onValueChange = { inputAddress = it },
                        label = { Text("Address") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LifeSaversTeal),
                        modifier = Modifier.fillMaxWidth().testTag("doorstep_address_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Quick Presets:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LifeSaversTeal)
                    Spacer(modifier = Modifier.height(6.dp))
                    presets.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { inputAddress = preset }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAddress(inputAddress)
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LifeSaversTeal)
                ) {
                    Text("Update Location")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSupabaseSheet) {
        SupabaseSyncSheet(
            syncManager = viewModel.syncManager,
            onDismiss = { showSupabaseSheet = false }
        )
    }

    if (showProfileSheet) {
        UserProfileSheet(
            currentUser = currentUser,
            patientProfile = patientProfile,
            bookings = allBookings,
            reviews = patientReviews,
            favoriteProviders = favoriteProviders,
            onUpdateProfile = { updated ->
                viewModel.updatePatientProfile(updated)
                authViewModel?.updateProfile(updated.fullName, updated.phone)
            },
            onSubmitReview = { review ->
                viewModel.submitPatientReview(review)
            },
            onToggleFavoriteProvider = { providerId ->
                viewModel.toggleFavoriteProvider(providerId)
            },
            onTrackBooking = { booking ->
                inDetailView = false
                inBookingFlow = false
                currentScreen = LifeSaversDestination.DISPATCH
            },
            onOpenCloudSync = {
                showSupabaseSheet = true
            },
            onSignOut = {
                authViewModel?.signOut()
            },
            onDismiss = {
                showProfileSheet = false
            }
        )
    }
}

@Composable
fun OmaApp(
    viewModel: LifeSaversViewModel,
    authViewModel: AuthViewModel? = null,
    currentUser: LifeSaversUser? = null,
    onWatchLogoReveal: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LifeSaversApp(
        viewModel = viewModel,
        authViewModel = authViewModel,
        currentUser = currentUser,
        onWatchLogoReveal = onWatchLogoReveal,
        modifier = modifier
    )
}
