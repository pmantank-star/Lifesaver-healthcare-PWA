package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NotificationType
import com.example.model.LifeSaversNotification
import com.example.model.OmaNotification
import com.example.ui.theme.LifeSaversCoral
import com.example.ui.theme.LifeSaversSuccess
import com.example.ui.theme.LifeSaversTeal
import com.example.ui.theme.LifeSaversTealContainer

@Composable
fun LifeSaversNotificationBanner(
    notification: LifeSaversNotification?,
    onDismiss: () -> Unit,
    onActionClick: (LifeSaversNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (notification != null) {
            val isProviderAlert = notification.type == NotificationType.PROVIDER_DISPATCH_ALERT
            val isAcceptanceAlert = notification.type == NotificationType.PATIENT_ACCEPTED_ALERT

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isProviderAlert) LifeSaversCoral else LifeSaversTeal
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clickable { onActionClick(notification) }
                    .testTag("notification_banner")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notification.type) {
                                        NotificationType.PROVIDER_DISPATCH_ALERT -> Icons.Default.ElectricBolt
                                        NotificationType.PATIENT_ACCEPTED_ALERT -> Icons.Default.CheckCircle
                                        NotificationType.PATIENT_ARRIVED_ALERT -> Icons.Default.Home
                                        NotificationType.PATIENT_COMPLETED_ALERT -> Icons.Default.MedicalServices
                                        NotificationType.PROVIDER_CREDENTIAL_VERIFIED -> Icons.Default.Verified
                                        NotificationType.PROVIDER_CREDENTIAL_REJECTED -> Icons.Default.Security
                                    },
                                    contentDescription = null,
                                    tint = if (isProviderAlert) LifeSaversCoral else LifeSaversTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (isProviderAlert) "PROVIDER ALERT" else "CARE UPDATE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = notification.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = notification.message,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onActionClick(notification) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = if (isProviderAlert) LifeSaversCoral else LifeSaversTeal
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isProviderAlert) Icons.Default.ElectricBolt else Icons.Default.Navigation,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (notification.type) {
                                    NotificationType.PROVIDER_DISPATCH_ALERT -> "Review & Accept"
                                    NotificationType.PATIENT_ACCEPTED_ALERT -> "Track Arrival (~${notification.etaMinutes ?: 8}m)"
                                    NotificationType.PATIENT_ARRIVED_ALERT -> "Open Door"
                                    NotificationType.PATIENT_COMPLETED_ALERT -> "View Receipt"
                                    NotificationType.PROVIDER_CREDENTIAL_VERIFIED -> "View Verified Seal"
                                    NotificationType.PROVIDER_CREDENTIAL_REJECTED -> "Review Details"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OmaNotificationBanner(
    notification: LifeSaversNotification?,
    onDismiss: () -> Unit,
    onActionClick: (LifeSaversNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    LifeSaversNotificationBanner(
        notification = notification,
        onDismiss = onDismiss,
        onActionClick = onActionClick,
        modifier = modifier
    )
}
