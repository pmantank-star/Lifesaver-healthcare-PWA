package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.PatientReview
import com.example.model.ProviderSpecialty
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
import java.util.UUID

private enum class ReviewFilter(val label: String) {
    ALL("All Reviews"),
    DOCTORS("Doctors"),
    NURSES("Nurses & CHW"),
    PHYSIO("Physiotherapy"),
    FIVE_STARS("5 Stars Only")
}

@Composable
fun PatientReviewsTab(
    reviews: List<PatientReview>,
    completedBookings: List<Booking>,
    onSubmitReview: (PatientReview) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(ReviewFilter.ALL) }
    var showWriteDialog by remember { mutableStateOf(false) }
    var bookingToReview by remember { mutableStateOf<Booking?>(null) }
    val context = LocalContext.current

    val filteredReviews by remember(reviews, selectedFilter) {
        derivedStateOf {
            when (selectedFilter) {
                ReviewFilter.ALL -> reviews
                ReviewFilter.DOCTORS -> reviews.filter { it.providerSpecialty == ProviderSpecialty.DOCTOR }
                ReviewFilter.NURSES -> reviews.filter {
                    it.providerSpecialty == ProviderSpecialty.NURSE ||
                        it.providerSpecialty == ProviderSpecialty.COMMUNITY_HEALTH
                }
                ReviewFilter.PHYSIO -> reviews.filter { it.providerSpecialty == ProviderSpecialty.PHYSIOTHERAPIST }
                ReviewFilter.FIVE_STARS -> reviews.filter { it.rating == 5 }
            }
        }
    }

    val averageRating by remember(reviews) {
        derivedStateOf {
            if (reviews.isEmpty()) 5.0f
            else reviews.map { it.rating }.average().toFloat()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("patient_reviews_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Overall Rating & Quality Metrics Header Card
        item {
            ReviewSummaryCard(
                reviewsCount = reviews.size,
                averageRating = averageRating,
                onWriteReviewClick = {
                    val availableToReview = completedBookings.firstOrNull()
                    bookingToReview = availableToReview
                    showWriteDialog = true
                }
            )
        }

        // 2. Filter Chips Row
        item {
            ReviewFilterChips(
                selectedFilter = selectedFilter,
                onSelectFilter = { selectedFilter = it },
                allCount = reviews.size,
                doctorCount = reviews.count { it.providerSpecialty == ProviderSpecialty.DOCTOR },
                nurseCount = reviews.count {
                    it.providerSpecialty == ProviderSpecialty.NURSE ||
                        it.providerSpecialty == ProviderSpecialty.COMMUNITY_HEALTH
                }
            )
        }

        // 3. Reviews List or Empty State
        if (filteredReviews.isEmpty()) {
            item {
                EmptyReviewsState(
                    selectedFilter = selectedFilter,
                    onResetFilter = { selectedFilter = ReviewFilter.ALL }
                )
            }
        } else {
            items(filteredReviews, key = { it.id }) { review ->
                ReviewTestimonialCard(
                    review = review,
                    onEditClick = {
                        val matchingBooking = completedBookings.find { it.id == review.bookingId }
                        bookingToReview = matchingBooking ?: Booking(
                            id = review.bookingId,
                            serviceId = "srv-default",
                            serviceName = review.serviceName,
                            providerId = review.providerId,
                            providerName = review.providerName,
                            providerTitle = review.providerTitle,
                            providerSpecialty = review.providerSpecialty,
                            patientName = "Patient",
                            patientAddress = "",
                            patientPhone = "",
                            symptomNotes = "",
                            status = BookingStatus.COMPLETED,
                            baseCost = 0,
                            suppliesCost = 0,
                            totalCost = 0,
                            etaMinutes = 0
                        )
                        showWriteDialog = true
                    }
                )
            }
        }

        // Extra bottom spacing
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Write / Edit Review Dialog
    if (showWriteDialog) {
        WriteReviewDialog(
            targetBooking = bookingToReview,
            availableBookings = completedBookings,
            existingReview = reviews.find { it.bookingId == bookingToReview?.id },
            onDismiss = {
                showWriteDialog = false
                bookingToReview = null
            },
            onSubmit = { newReview ->
                onSubmitReview(newReview)
                showWriteDialog = false
                bookingToReview = null
                Toast.makeText(context, "Review published successfully! Thank you for your feedback.", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@Composable
private fun ReviewSummaryCard(
    reviewsCount: Int,
    averageRating: Float,
    onWriteReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, IceMintDark.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("reviews_summary_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Verified Clinical Care",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                    Text(
                        text = "Patient Experience & Testimonials",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightMint
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "100% Genuine",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score and breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Rating Number & Stars
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f", averageRating),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestGreen
                    )
                    RatingStarsBar(rating = averageRating.toInt(), starSize = 16)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$reviewsCount published",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkCharcoal.copy(alpha = 0.7f)
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp)
                        .background(IceMintDark)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Breakdown rating lines
                Column(modifier = Modifier.weight(1f)) {
                    RatingDistributionBar(label = "5 ★", fraction = 1.0f)
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingDistributionBar(label = "4 ★", fraction = 0.0f)
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingDistributionBar(label = "3 ★", fraction = 0.0f)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = IceMint, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Prompt Button: Write / Add Testimonial
            Button(
                onClick = onWriteReviewClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("write_review_button")
            ) {
                Icon(
                    imageVector = Icons.Default.RateReview,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Write a Review for a Visit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RatingDistributionBar(
    label: String,
    fraction: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkCharcoal,
            modifier = Modifier.width(26.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            color = AmberAccent,
            trackColor = IceMintDark.copy(alpha = 0.5f),
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${(fraction * 100).toInt()}%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = DarkCharcoal.copy(alpha = 0.6f),
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ReviewFilterChips(
    selectedFilter: ReviewFilter,
    onSelectFilter: (ReviewFilter) -> Unit,
    allCount: Int,
    doctorCount: Int,
    nurseCount: Int,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ReviewFilter.entries) { filter ->
            val label = when (filter) {
                ReviewFilter.ALL -> "All ($allCount)"
                ReviewFilter.DOCTORS -> "Doctors ($doctorCount)"
                ReviewFilter.NURSES -> "Nurses & CHW ($nurseCount)"
                ReviewFilter.PHYSIO -> "Physiotherapy"
                ReviewFilter.FIVE_STARS -> "5 Stars ★"
            }
            val isSelected = selectedFilter == filter
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) ForestGreen else PureWhite,
                border = BorderStroke(
                    1.dp,
                    if (isSelected) ForestGreen else IceMintDark.copy(alpha = 0.8f)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectFilter(filter) }
                    .testTag("filter_chip_${filter.name.lowercase()}")
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) PureWhite else DarkCharcoal,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReviewTestimonialCard(
    review: PatientReview,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, IceMintDark.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("review_card_${review.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Provider Avatar, Name, Title, Specialty Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Specialty-tinted avatar
                val avatarColor = when (review.providerSpecialty) {
                    ProviderSpecialty.DOCTOR -> ForestGreen
                    ProviderSpecialty.NURSE -> Color(0xFF00796B)
                    ProviderSpecialty.COMMUNITY_HEALTH -> Color(0xFF00897B)
                    ProviderSpecialty.PHYSIOTHERAPIST -> Color(0xFF3949AB)
                    ProviderSpecialty.MENTAL_HEALTH -> Color(0xFF6A1B9A)
                    else -> ForestGreen
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.avatarInitials.ifEmpty { review.providerName.take(2).uppercase() },
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = review.providerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarkCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Specialty Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = WarmBeige
                        ) {
                            Text(
                                text = review.providerSpecialty.badgeTitle,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = review.providerTitle,
                        fontSize = 11.sp,
                        color = DarkCharcoal.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Star Rating, Rating Label, and Verified Booking Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingStarsBar(rating = review.rating, starSize = 18)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${review.rating}.0 • Exceptional",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AmberAccentDark
                    )
                }

                if (review.verifiedBooking) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LightMint)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Verified Visit",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Service Name and Date chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = review.serviceName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ForestGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "• ${review.dateText}",
                    fontSize = 11.sp,
                    color = DarkCharcoal.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Testimonial quote body
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IceMint.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, IceMintDark.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = ForestGreen.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = review.testimonial,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = DarkCharcoal,
                        fontStyle = FontStyle.Normal
                    )
                }
            }

            // Compliment Badges / Tags
            if (review.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    review.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = WarmBeige.copy(alpha = 0.7f),
                            border = BorderStroke(0.5.dp, ForestGreenLight.copy(alpha = 0.3f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "✓ $tag",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ForestGreen
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Booking reference & Edit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref #${review.bookingId}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkCharcoal.copy(alpha = 0.5f)
                )

                TextButton(
                    onClick = onEditClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Update Feedback",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
            }
        }
    }
}

@Composable
fun RatingStarsBar(
    rating: Int,
    starSize: Int = 16,
    onRatingSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isFilled = i <= rating
            val icon = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder
            val tint = if (isFilled) AmberAccent else DarkCharcoal.copy(alpha = 0.3f)

            Icon(
                imageVector = icon,
                contentDescription = "$i Stars",
                tint = tint,
                modifier = Modifier
                    .size(starSize.dp)
                    .then(
                        if (onRatingSelected != null) {
                            Modifier
                                .clip(CircleShape)
                                .clickable { onRatingSelected(i) }
                                .padding(2.dp)
                        } else Modifier
                    )
            )
        }
    }
}

@Composable
private fun EmptyReviewsState(
    selectedFilter: ReviewFilter,
    onResetFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(IceMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RateReview,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Reviews Found",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "No testimonials matched the filter \"${selectedFilter.label}\".",
                fontSize = 12.sp,
                color = DarkCharcoal.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onResetFilter,
                border = BorderStroke(1.dp, ForestGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Show All Reviews",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WriteReviewDialog(
    targetBooking: Booking?,
    availableBookings: List<Booking>,
    existingReview: PatientReview?,
    onDismiss: () -> Unit,
    onSubmit: (PatientReview) -> Unit
) {
    var selectedBooking by remember {
        mutableStateOf(targetBooking ?: availableBookings.firstOrNull())
    }
    var rating by remember { mutableIntStateOf(existingReview?.rating ?: 5) }
    var testimonialText by remember { mutableStateOf(existingReview?.testimonial ?: "") }

    val availableTags = listOf(
        "Punctual Arrival",
        "Gentle Bedside Manner",
        "Clear Explanations",
        "Thorough Diagnosis",
        "Hospital-Grade Hygiene",
        "Patient & Empathetic",
        "Rapid Relief",
        "Great Communication"
    )

    val selectedTags = remember {
        mutableStateListOf<String>().apply {
            if (existingReview != null) {
                addAll(existingReview.tags)
            } else {
                addAll(listOf("Punctual Arrival", "Gentle Bedside Manner"))
            }
        }
    }

    val ratingLabel = when (rating) {
        5 -> "5.0 • Exceptional Care"
        4 -> "4.0 • Great Experience"
        3 -> "3.0 • Satisfactory Visit"
        2 -> "2.0 • Needs Improvement"
        else -> "1.0 • Poor Care"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (existingReview != null) "Update Review" else "Rate & Review Care",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = DarkCharcoal.copy(alpha = 0.6f)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Provider Selection if multiple bookings available
                if (selectedBooking != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LightMint,
                        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (selectedBooking?.providerName ?: "Dr").take(2).uppercase(),
                                    color = PureWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedBooking?.providerName ?: "Healthcare Provider",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                                Text(
                                    text = "${selectedBooking?.serviceName} (Ref #${selectedBooking?.id})",
                                    fontSize = 11.sp,
                                    color = DarkCharcoal.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Star Rating Selector
                Text(
                    text = "Tap to rate experience:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkCharcoal
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WarmBeige.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RatingStarsBar(
                            rating = rating,
                            starSize = 32,
                            onRatingSelected = { rating = it },
                            modifier = Modifier.testTag("star_rating_selector")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ratingLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberAccentDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compliments Chips
                Text(
                    text = "Select compliments:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkCharcoal
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) ForestGreen else PureWhite,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ForestGreen else IceMintDark
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                                }
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PureWhite else DarkCharcoal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Testimonial Input
                Text(
                    text = "Your Testimonial:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkCharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = testimonialText,
                    onValueChange = { testimonialText = it },
                    placeholder = {
                        Text(
                            "Share your experience with this doctor or nurse (punctuality, diagnosis, care provided)...",
                            fontSize = 12.sp,
                            color = DarkCharcoal.copy(alpha = 0.5f)
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = IceMintDark,
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_comment_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val booking = selectedBooking ?: return@Button
                    val finalComment = if (testimonialText.isBlank()) {
                        "Exceptional healthcare visit. The practitioner arrived on time and administered gentle, professional care."
                    } else {
                        testimonialText.trim()
                    }

                    val review = PatientReview(
                        id = existingReview?.id ?: "rev-${UUID.randomUUID().toString().take(6)}",
                        bookingId = booking.id,
                        providerId = booking.providerId ?: "prov-01",
                        providerName = booking.providerName ?: "Healthcare Provider",
                        providerTitle = booking.providerTitle ?: "Licensed Practitioner",
                        providerSpecialty = booking.providerSpecialty ?: ProviderSpecialty.NURSE,
                        serviceName = booking.serviceName,
                        rating = rating,
                        testimonial = finalComment,
                        dateText = "Just now",
                        tags = selectedTags.toList(),
                        verifiedBooking = true,
                        avatarInitials = (booking.providerName ?: "HP").take(2).uppercase()
                    )
                    onSubmit(review)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("submit_review_button")
            ) {
                Text(
                    text = if (existingReview != null) "Update Review" else "Publish Review",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkCharcoal.copy(alpha = 0.7f))
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp)
    )
}
