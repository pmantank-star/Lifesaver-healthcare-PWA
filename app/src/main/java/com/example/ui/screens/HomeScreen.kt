package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import com.example.model.Gender
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.HealthProvider
import com.example.model.ProviderSpecialty
import com.example.model.ServiceItem
import com.example.ui.components.RadarMapView
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.IceMint
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmBeige
import com.example.ui.theme.OmaCoral
import com.example.ui.theme.OmaEmergency
import com.example.ui.theme.OmaLinen
import com.example.ui.theme.OmaMint
import com.example.ui.theme.OmaOnLinenContainer
import com.example.ui.theme.OmaTeal
import com.example.ui.theme.OmaTealContainer
import com.example.ui.theme.OmaTealDark
import com.example.ui.theme.OmaVerifiedBlue

@Composable
fun HomeScreen(
    providers: List<HealthProvider>,
    services: List<ServiceItem>,
    activeBooking: Booking?,
    selectedSpecialty: ProviderSpecialty?,
    onSelectSpecialty: (ProviderSpecialty?) -> Unit,
    onSelectProvider: (HealthProvider) -> Unit,
    onSelectServiceForBooking: (ServiceItem) -> Unit,
    onNavigateToDispatchTracker: (String) -> Unit,
    onOpenFemTechSuite: (() -> Unit)? = null,
    currentUserGender: Gender = Gender.FEMALE,
    onToggleGender: ((Gender) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
    ) {
        // 1. Active Booking Banner (if any)
        if (activeBooking != null && activeBooking.status != BookingStatus.COMPLETED && activeBooking.status != BookingStatus.CANCELLED) {
            item {
                ActiveBookingQuickBanner(
                    booking = activeBooking,
                    onClick = { onNavigateToDispatchTracker(activeBooking.id) }
                )
            }
        }

        // 2. Hero Visual Section
        item {
            HeroBannerSection(
                onInstantDispatch = {
                    val defaultService = services.firstOrNull { it.isUrgent } ?: services.first()
                    onSelectServiceForBooking(defaultService)
                }
            )
        }

        // 3. Personalized Health Suite Entry Card with 1-Tap Gender Switcher
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Personalized Health Suite",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // 1-Tap Gender Switcher Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(WarmBeige)
                            .padding(2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (currentUserGender == Gender.FEMALE) ForestGreen else Color.Transparent,
                            modifier = Modifier.clickable { onToggleGender?.invoke(Gender.FEMALE) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Female,
                                    contentDescription = "Female Suite",
                                    tint = if (currentUserGender == Gender.FEMALE) PureWhite else DarkCharcoal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Female",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentUserGender == Gender.FEMALE) PureWhite else DarkCharcoal
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (currentUserGender == Gender.MALE) ForestGreen else Color.Transparent,
                            modifier = Modifier.clickable { onToggleGender?.invoke(Gender.MALE) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Male,
                                    contentDescription = "Male Suite",
                                    tint = if (currentUserGender == Gender.MALE) PureWhite else DarkCharcoal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Male",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentUserGender == Gender.MALE) PureWhite else DarkCharcoal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                FemTechSuiteBannerCard(
                    gender = currentUserGender,
                    onClick = { onOpenFemTechSuite?.invoke() }
                )
            }
        }

        // 4. Urgent Triage Callout
        item {
            UrgentTriageCallout(
                onFastTriage = {
                    val urgentService = services.firstOrNull { it.name.contains("Vitals") || it.isUrgent } ?: services.first()
                    onSelectServiceForBooking(urgentService)
                }
            )
        }

        // 4. Live Proximity Radar Map ("Bolt for Healthcare")
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = "Nearby Healthcare Workers",
                                tint = OmaTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Healthcare Radar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OmaTeal
                            )
                        }
                        Text(
                            text = "Tap any verified worker on the map for instant dispatch",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                RadarMapView(
                    providers = providers,
                    selectedProvider = null,
                    onProviderSelected = { onSelectProvider(it) }
                )
            }
        }

        // 5. Specialty Categories Chips
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Care Categories",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSpecialty == null,
                            onClick = { onSelectSpecialty(null) },
                            label = { Text("All Providers") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = OmaTealContainer,
                                labelColor = OmaTeal,
                                selectedContainerColor = OmaTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(
                        listOf(
                            ProviderSpecialty.CHILD_CARE_GIVER,
                            ProviderSpecialty.ELDERLY_CARE_GIVER
                        ) + ProviderSpecialty.entries.filter { it != ProviderSpecialty.CHILD_CARE_GIVER && it != ProviderSpecialty.ELDERLY_CARE_GIVER },
                        key = { it.name }
                    ) { specialty ->
                        FilterChip(
                            selected = selectedSpecialty == specialty,
                            onClick = { onSelectSpecialty(specialty) },
                            label = { Text(specialty.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = OmaTealContainer,
                                labelColor = OmaTeal,
                                selectedContainerColor = OmaTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 6. In-Home Services
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Home Visits",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                services.take(4).forEach { service ->
                    HomeServiceCard(
                        service = service,
                        onBook = { onSelectServiceForBooking(service) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // 7. Top Verified Providers List
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Nearby Care Providers",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                Spacer(modifier = Modifier.height(8.dp))

                providers.take(3).forEach { provider ->
                    NearbyProviderCard(
                        provider = provider,
                        onSelect = { onSelectProvider(provider) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun ActiveBookingQuickBanner(
    booking: Booking,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, OmaTeal.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 4.dp)
            .clickable(onClick = onClick)
            .testTag("active_booking_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Active Dispatch",
                        tint = OmaTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "LIVE DISPATCH IN PROGRESS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OmaTealDark
                    )
                    Text(
                        text = "${booking.providerName ?: "Provider"} • ${booking.status.label}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ETA ~${booking.etaMinutes} mins • Tap to track on map & chat",
                        fontSize = 11.sp,
                        color = ForestGreenDark
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View Dispatch",
                tint = OmaTeal
            )
        }
    }
}

@Composable
fun HeroBannerSection(
    onInstantDispatch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_hero_banner),
            contentDescription = "Healthcare at your doorstep",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient scrim using modern electric royal blue
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            OmaTeal.copy(alpha = 0.25f),
                            OmaTealDark.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        // Text & Action
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PureWhite.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "THE BOLT FOR HEALTHCARE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PureWhite,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Healthcare at your doorstep.",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = PureWhite
            )
            Text(
                text = "Verified nurses, doctors & therapists dispatched to your home in minutes.",
                fontSize = 12.sp,
                color = PureWhite.copy(alpha = 0.9f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onInstantDispatch,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberAccent,
                    contentColor = DarkCharcoal
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("hero_instant_dispatch_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = DarkCharcoal
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Request LifeSavers Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DarkCharcoal
                )
            }
        }
    }
}

@Composable
fun UrgentTriageCallout(
    onFastTriage: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = OmaEmergency.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmaEmergency.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(OmaEmergency.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "Emergency Alert",
                        tint = OmaEmergency,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Need Urgent In-Home Care?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmaEmergency
                    )
                    Text(
                        text = "Fast wound dressing, vitals, or acute medication administration",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(
                onClick = onFastTriage,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OmaEmergency),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OmaEmergency),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Triage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HomeServiceCard(
    service: ServiceItem,
    onBook: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onBook)
            .testTag("service_card_${service.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OmaTealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = service.name,
                            tint = OmaTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${service.specialty.displayName} • ~${service.durationMinutes} mins",
                            fontSize = 11.sp,
                            color = OmaTeal
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OmaTealContainer
                ) {
                    Text(
                        text = "Rate set by provider",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (service.isUrgent) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OmaLinen
                    ) {
                        Text(
                            text = "⚡ Instant Dispatch Available",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = OmaOnLinenContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = OmaTeal),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Book Home Visit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NearbyProviderCard(
    provider: HealthProvider,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("provider_card_${provider.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(OmaTealContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = provider.avatarInitials,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = OmaTeal
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = provider.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified License",
                            tint = OmaVerifiedBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = provider.title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OmaTealContainer
                ) {
                    Text(
                        text = "Available",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmaTeal,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${provider.rating} (${provider.reviewCount})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " • ${provider.distanceKm} km",
                        fontSize = 11.sp,
                        color = OmaTeal
                    )
                    Text(
                        text = " • ETA ${provider.etaMinutes}m",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmaTealDark
                    )
                }
            }
        }
    }
}

@Composable
fun FemTechSuiteBannerCard(
    gender: Gender = Gender.FEMALE,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ForestGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("femtech_suite_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AmberAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (gender == Gender.MALE) Icons.Default.Male else Icons.Default.Female,
                        contentDescription = "Health Suite",
                        tint = AmberAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AmberAccent
                        ) {
                            Text(
                                text = if (gender == Gender.MALE) "MEN'S HEALTH SUITE" else "UNICEF FEMTECH CALL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkCharcoal,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (gender == Gender.MALE) "Prostate, Fertility & SRH Suite" else "Maternal & Reproductive Health Suite",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = if (gender == Gender.MALE) "Prostate IPSS Score • Male Fertility • Confidential SRH" else "Cycle Calculator • 1,000 Days Care • Point-of-Care Vitals",
                        fontSize = 11.sp,
                        color = IceMint
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PureWhite
            ) {
                Text(
                    text = "Open",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
