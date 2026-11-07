package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PatientProfileData
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.HealthProvider
import com.example.model.MedicalSupply
import com.example.model.OmaUser
import com.example.model.PatientReview
import com.example.model.UserRole
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberAccentDark
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.IceMint
import com.example.ui.theme.IceMintDark
import com.example.ui.theme.LightMint
import com.example.ui.theme.OmaEmergency
import com.example.ui.theme.OmaSuccess
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmBeige
import java.text.NumberFormat
import java.util.Locale

private enum class ProfileTab(val title: String) {
    BOOKINGS("Bookings"),
    REVIEWS("Reviews"),
    PERSONAL_INFO("Personal Info"),
    FAVORITES("Favorites")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileSheet(
    currentUser: OmaUser?,
    patientProfile: PatientProfileData,
    bookings: List<Booking>,
    reviews: List<PatientReview> = emptyList(),
    favoriteProviders: List<HealthProvider>,
    onUpdateProfile: (PatientProfileData) -> Unit,
    onSubmitReview: (PatientReview) -> Unit = {},
    onToggleFavoriteProvider: (String) -> Unit,
    onTrackBooking: (Booking) -> Unit,
    onOpenCloudSync: () -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(ProfileTab.BOOKINGS.ordinal) }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = IceMint,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
        modifier = modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Header: User Profile Info Banner
            UserProfileHeader(
                user = currentUser,
                profileData = patientProfile,
                onClose = onDismiss
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Navigation Tabs for Mobile Responsiveness (Bookings, Reviews, Profile, Favorites)
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = WarmBeige,
                contentColor = ForestGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ForestGreen,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == ProfileTab.BOOKINGS.ordinal,
                    onClick = { selectedTab = ProfileTab.BOOKINGS.ordinal },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bookings (${bookings.size})",
                                fontWeight = if (selectedTab == ProfileTab.BOOKINGS.ordinal) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }
                    },
                    selectedContentColor = ForestGreen,
                    unselectedContentColor = DarkCharcoal.copy(alpha = 0.7f),
                    modifier = Modifier.testTag("tab_bookings")
                )

                Tab(
                    selected = selectedTab == ProfileTab.REVIEWS.ordinal,
                    onClick = { selectedTab = ProfileTab.REVIEWS.ordinal },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (selectedTab == ProfileTab.REVIEWS.ordinal) AmberAccentDark else DarkCharcoal.copy(alpha = 0.7f),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reviews (${reviews.size})",
                                fontWeight = if (selectedTab == ProfileTab.REVIEWS.ordinal) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }
                    },
                    selectedContentColor = ForestGreen,
                    unselectedContentColor = DarkCharcoal.copy(alpha = 0.7f),
                    modifier = Modifier.testTag("tab_reviews")
                )

                Tab(
                    selected = selectedTab == ProfileTab.PERSONAL_INFO.ordinal,
                    onClick = { selectedTab = ProfileTab.PERSONAL_INFO.ordinal },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Profile",
                                fontWeight = if (selectedTab == ProfileTab.PERSONAL_INFO.ordinal) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }
                    },
                    selectedContentColor = ForestGreen,
                    unselectedContentColor = DarkCharcoal.copy(alpha = 0.7f),
                    modifier = Modifier.testTag("tab_personal_info")
                )

                Tab(
                    selected = selectedTab == ProfileTab.FAVORITES.ordinal,
                    onClick = { selectedTab = ProfileTab.FAVORITES.ordinal },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Favorites (${favoriteProviders.size})",
                                fontWeight = if (selectedTab == ProfileTab.FAVORITES.ordinal) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }
                    },
                    selectedContentColor = ForestGreen,
                    unselectedContentColor = DarkCharcoal.copy(alpha = 0.7f),
                    modifier = Modifier.testTag("tab_favorites")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body content according to selected tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    ProfileTab.BOOKINGS.ordinal -> {
                        BookingHistoryTab(
                            bookings = bookings,
                            onTrackBooking = {
                                onTrackBooking(it)
                                onDismiss()
                            }
                        )
                    }
                    ProfileTab.REVIEWS.ordinal -> {
                        PatientReviewsTab(
                            reviews = reviews,
                            completedBookings = bookings.filter { it.status == BookingStatus.COMPLETED },
                            onSubmitReview = onSubmitReview
                        )
                    }
                    ProfileTab.PERSONAL_INFO.ordinal -> {
                        PersonalInfoTab(
                            initialProfile = patientProfile,
                            userEmail = currentUser?.email ?: patientProfile.email,
                            onSave = { updated ->
                                onUpdateProfile(updated)
                                Toast.makeText(context, "Personal information updated", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    ProfileTab.FAVORITES.ordinal -> {
                        SavedFavoritesTab(
                            favoriteProviders = favoriteProviders,
                            onToggleFavoriteProvider = onToggleFavoriteProvider
                        )
                    }
                }
            }

            // Bottom Actions Bar
            HorizontalDivider(color = IceMintDark, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onDismiss()
                        onOpenCloudSync()
                    },
                    modifier = Modifier.testTag("profile_cloud_sync_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "Cloud Backend",
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Supabase Backend",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreen
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            onDismiss()
                            onSignOut()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = OmaEmergency),
                        modifier = Modifier.testTag("profile_sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign Out",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Done", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun UserProfileHeader(
    user: OmaUser?,
    profileData: PatientProfileData,
    onClose: () -> Unit
) {
    Surface(
        color = ForestGreen,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Profile Avatar Circle
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(WarmBeige)
                        .border(BorderStroke(2.dp, AmberAccent), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (user?.displayName ?: profileData.fullName)
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2)
                            .joinToString("")
                            .ifBlank { "CA" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = ForestGreen
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user?.displayName ?: profileData.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Patient",
                            tint = AmberAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = user?.email ?: profileData.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = LightMint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = AmberAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AmberAccent)
                    ) {
                        Text(
                            text = "LifeSavers Patient Member",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PureWhite.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Profile",
                    tint = PureWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =========================================================================
// Tab 1: Booking History
// =========================================================================

@Composable
private fun BookingHistoryTab(
    bookings: List<Booking>,
    onTrackBooking: (Booking) -> Unit
) {
    var statusFilter by remember { mutableStateOf("ALL") }

    val filteredList = remember(bookings, statusFilter) {
        when (statusFilter) {
            "ACTIVE" -> bookings.filter { it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED }
            "COMPLETED" -> bookings.filter { it.status == BookingStatus.COMPLETED }
            else -> bookings
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "ALL" to "All (${bookings.size})",
                "ACTIVE" to "Active (${bookings.count { it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED }})",
                "COMPLETED" to "Completed (${bookings.count { it.status == BookingStatus.COMPLETED }})"
            ).forEach { (key, label) ->
                val isSelected = statusFilter == key
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) ForestGreen else PureWhite,
                    border = BorderStroke(1.dp, if (isSelected) ForestGreen else IceMintDark),
                    modifier = Modifier
                        .clickable { statusFilter = key }
                        .testTag("filter_bookings_$key")
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PureWhite else DarkCharcoal,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = ForestGreen.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No bookings found in this category",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkCharcoal.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredList, key = { it.id }) { booking ->
                    BookingHistoryCard(
                        booking = booking,
                        onTrackClick = { onTrackBooking(booking) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingHistoryCard(
    booking: Booking,
    onTrackClick: () -> Unit
) {
    val numberFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "NG")) }
    val formattedCost = remember(booking.totalCost) {
        "₦" + NumberFormat.getNumberInstance(Locale.US).format(booking.totalCost)
    }

    val isActive = booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED

    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, IceMintDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: ID, Timestamp & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = WarmBeige,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "#${booking.id}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isActive) "Active Now" else "Past Visit",
                        fontSize = 10.sp,
                        color = DarkCharcoal.copy(alpha = 0.6f)
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (booking.status) {
                        BookingStatus.COMPLETED -> OmaSuccess.copy(alpha = 0.15f)
                        BookingStatus.EN_ROUTE, BookingStatus.IN_PROGRESS, BookingStatus.ARRIVED -> AmberAccent.copy(alpha = 0.2f)
                        BookingStatus.MATCHED, BookingStatus.SEARCHING -> WarmBeige
                        BookingStatus.CANCELLED -> OmaEmergency.copy(alpha = 0.15f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when (booking.status) {
                            BookingStatus.COMPLETED -> OmaSuccess
                            BookingStatus.EN_ROUTE, BookingStatus.IN_PROGRESS, BookingStatus.ARRIVED -> AmberAccentDark
                            BookingStatus.MATCHED, BookingStatus.SEARCHING -> ForestGreen
                            BookingStatus.CANCELLED -> OmaEmergency
                        }
                    )
                ) {
                    Text(
                        text = booking.status.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (booking.status) {
                            BookingStatus.COMPLETED -> OmaSuccess
                            BookingStatus.EN_ROUTE, BookingStatus.IN_PROGRESS, BookingStatus.ARRIVED -> AmberAccentDark
                            BookingStatus.MATCHED, BookingStatus.SEARCHING -> ForestGreen
                            BookingStatus.CANCELLED -> OmaEmergency
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Service Title
            Text(
                text = booking.serviceName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = ForestGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Specialist Attending
            if (booking.providerName != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = AmberAccentDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${booking.providerName} (${booking.providerTitle ?: "Provider"})",
                        fontSize = 12.sp,
                        color = DarkCharcoal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Doorstep Address
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = booking.patientAddress,
                    fontSize = 11.sp,
                    color = DarkCharcoal.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Vitals Recorded (if present)
            if (!booking.vitalsRecorded.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = IceMint,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🩺 Vitals: ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = booking.vitalsRecorded,
                            fontSize = 10.sp,
                            color = DarkCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = IceMint, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Cost & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Paid",
                        fontSize = 10.sp,
                        color = DarkCharcoal.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formattedCost,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = ForestGreen
                    )
                }

                if (isActive) {
                    Button(
                        onClick = onTrackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("track_booking_button_${booking.id}")
                    ) {
                        Text(
                            text = "Track Live Dispatch",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onTrackClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, ForestGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "View Record",
                            fontSize = 11.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// Tab 2: Personal Information Management
// =========================================================================

@Composable
private fun PersonalInfoTab(
    initialProfile: PatientProfileData,
    userEmail: String,
    onSave: (PatientProfileData) -> Unit
) {
    var fullName by remember { mutableStateOf(initialProfile.fullName) }
    var phone by remember { mutableStateOf(initialProfile.phone) }
    var address by remember { mutableStateOf(initialProfile.doorstepAddress) }
    var emergencyContactName by remember { mutableStateOf(initialProfile.emergencyContactName) }
    var emergencyContactPhone by remember { mutableStateOf(initialProfile.emergencyContactPhone) }
    var bloodGroup by remember { mutableStateOf(initialProfile.bloodGroup) }
    var allergies by remember { mutableStateOf(initialProfile.allergies) }
    var chronicConditions by remember { mutableStateOf(initialProfile.chronicConditions) }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Section 1: Contact & Delivery Info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, IceMintDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Contact & Doorstep Info",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ForestGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ForestGreen) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            focusedLabelColor = ForestGreen,
                            cursorColor = ForestGreen
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Email (Account ID)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ForestGreen.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = IceMintDark,
                            disabledTextColor = DarkCharcoal.copy(alpha = 0.7f),
                            disabledLabelColor = DarkCharcoal.copy(alpha = 0.6f)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ForestGreen) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            focusedLabelColor = ForestGreen,
                            cursorColor = ForestGreen
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_phone")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Doorstep Healthcare Address") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreen) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            focusedLabelColor = ForestGreen,
                            cursorColor = ForestGreen
                        ),
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_address")
                    )
                }
            }
        }

        // Section 2: Clinical & Emergency Data
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, IceMintDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = AmberAccentDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Emergency & Clinical Data",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ForestGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emergencyContactName,
                        onValueChange = { emergencyContactName = it },
                        label = { Text("Emergency Contact (Name & Relationship)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            focusedLabelColor = ForestGreen,
                            cursorColor = ForestGreen
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_emergency_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = emergencyContactPhone,
                        onValueChange = { emergencyContactPhone = it },
                        label = { Text("Emergency Contact Phone") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ForestGreen) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            focusedLabelColor = ForestGreen,
                            cursorColor = ForestGreen
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_emergency_phone")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Blood Group Selection
                    Text(
                        text = "Blood Group",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(bloodGroups) { bg ->
                            val isSelected = bloodGroup == bg
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AmberAccent else WarmBeige,
                                border = BorderStroke(1.dp, if (isSelected) AmberAccentDark else IceMintDark),
                                modifier = Modifier
                                    .clickable { bloodGroup = bg }
                                    .testTag("blood_group_$bg")
                            ) {
                                Text(
                                    text = bg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkCharcoal,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Known Allergies (e.g. Penicillin, Latex)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            focusedLabelColor = ForestGreen,
                            cursorColor = ForestGreen
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_allergies")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = chronicConditions,
                        onValueChange = { chronicConditions = it },
                        label = { Text("Chronic Health Conditions (e.g. Asthma, None)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            focusedLabelColor = ForestGreen,
                            cursorColor = ForestGreen
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_conditions")
                    )
                }
            }
        }

        // Section 3: Save Button
        item {
            Button(
                onClick = {
                    onSave(
                        initialProfile.copy(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            doorstepAddress = address.trim(),
                            emergencyContactName = emergencyContactName.trim(),
                            emergencyContactPhone = emergencyContactPhone.trim(),
                            bloodGroup = bloodGroup,
                            allergies = allergies.trim(),
                            chronicConditions = chronicConditions.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Personal Information",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// =========================================================================
// Tab 3: Saved Favorites
// =========================================================================

@Composable
private fun SavedFavoritesTab(
    favoriteProviders: List<HealthProvider>,
    onToggleFavoriteProvider: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (favoriteProviders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = ForestGreen.copy(alpha = 0.35f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                text = "No saved specialists yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ForestGreen
            )
            Text(
                text = "Save providers for quick 1-tap access",
                        fontSize = 12.sp,
                        color = DarkCharcoal.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(favoriteProviders, key = { it.id }) { provider ->
                    FavoriteProviderCard(
                        provider = provider,
                        onRemoveFavorite = { onToggleFavoriteProvider(provider.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteProviderCard(
    provider: HealthProvider,
    onRemoveFavorite: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, IceMintDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fav_provider_${provider.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Specialist Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(WarmBeige)
                        .border(BorderStroke(1.5.dp, ForestGreen), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = provider.avatarInitials,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = provider.title,
                        fontSize = 11.sp,
                        color = DarkCharcoal.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = AmberAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${provider.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                    }
                }
            }

            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = AmberAccent,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
