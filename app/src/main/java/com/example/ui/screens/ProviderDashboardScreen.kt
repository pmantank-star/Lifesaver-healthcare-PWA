package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.ProviderCredential
import com.example.model.ProviderSpecialty
import com.example.model.ProviderWallet
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.OmaCoral
import com.example.ui.theme.OmaMint
import com.example.ui.theme.OmaTeal
import com.example.ui.theme.OmaTealContainer
import com.example.ui.theme.OmaVerifiedBlue

@Composable
fun ProviderDashboardScreen(
    credential: ProviderCredential,
    wallet: ProviderWallet,
    isOnline: Boolean,
    incomingGig: Booking?,
    onToggleOnline: () -> Unit,
    onAcceptGig: () -> Unit,
    onDeclineGig: () -> Unit,
    onManageCredentials: () -> Unit,
    onTrackBooking: (String) -> Unit,
    onOpenMaternalCare: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("provider_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status & Online Toggle Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (isOnline) OmaTeal else Color(0xFF374151)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.bg_medical_overlay),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize(),
                        alpha = 0.12f
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isOnline) "ONLINE • DISPATCH READY" else "OFFLINE • UNAVAILABLE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isOnline) OmaMint else Color(0xFFD1D5DB)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = credential.fullName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${credential.specialty.displayName} • Lic #${credential.licenseNumber}",
                                fontSize = 12.sp,
                                color = OmaMint
                            )
                        }

                        Switch(
                            checked = isOnline,
                            onCheckedChange = { onToggleOnline() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = OmaCoral,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("provider_online_switch")
                        )
                    }
                }
            }
        }

        // Specialist Task-Specific Clinical Toolkit
        item {
            SpecialistTaskToolkitCard(specialty = credential.specialty)
        }

        // Incoming Dispatch Request Alert
        if (incomingGig != null && isOnline) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = OmaCoral),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("incoming_gig_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "⚡ INCOMING DISPATCH ALERT",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.White
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "~${incomingGig.etaMinutes}m ETA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = incomingGig.serviceName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = incomingGig.patientAddress,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.95f)
                            )
                        }

                        if (incomingGig.symptomNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Notes: \"${incomingGig.symptomNotes}\"",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDeclineGig,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Pass", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onAcceptGig,
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("accept_gig_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = OmaTeal, contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Accept Dispatch", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Wallet & Earnings Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = OmaTeal,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Earnings & Earnings Wallet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OmaTealContainer
                        ) {
                            Text(
                                text = "15% Commission",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OmaTeal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Available for Withdrawal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "₦${"%,d".format(wallet.availableBalance)}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OmaTeal
                            )
                        }

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = OmaCoral),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Instant Cashout", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailStatItem(title = "Lifetime Gigs", value = "${wallet.completedGigsCount} Completed", tint = OmaTeal)
                        DetailStatItem(title = "Gross Revenue", value = "₦${"%,d".format(wallet.totalEarnings)}", tint = OmaTeal)
                        DetailStatItem(title = "Rating", value = "${wallet.ratingAvg} ★", tint = Color(0xFFF59E0B))
                    }
                }
            }
        }

        // Credential Verification Status Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = OmaTeal,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Medical Credentials",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OmaVerifiedBlue.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = OmaVerifiedBlue, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "VERIFIED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OmaVerifiedBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Council: ${credential.licenseCouncil}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "License No: ${credential.licenseNumber}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmaTeal
                    )
                    Text(
                        text = "Document on File: ${credential.documentFileName}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onManageCredentials,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Credentials & Certifications", color = OmaTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenMaternalCare() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = ForestGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Maternal Care & Point-of-Care Vitals", fontWeight = FontWeight.Bold, color = ForestGreen)
                            Text("1,000 Days Continuum • Privacy & Screening", fontSize = 11.sp, color = ForestGreen)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ForestGreen)
                }
            }
        }
    }
}

@Composable
fun SpecialistTaskToolkitCard(
    specialty: ProviderSpecialty
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = OmaTeal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${specialty.badgeTitle} Clinical Toolkit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Task-specific clinical tools for your field",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (specialty) {
                ProviderSpecialty.SEXUAL_REPRODUCTIVE_HEALTH -> {
                    ToolkitActionTile(title = "Confidential SRH Case Logger", subtitle = "Record family planning & STI consultation details")
                    ToolkitActionTile(title = "Cervical HPV Specimen Tracker", subtitle = "Log pap smear / HPV swab collection IDs")
                    ToolkitActionTile(title = "Male Fertility & Prostate Triage", subtitle = "Log prostate symptoms & hormone levels")
                }
                ProviderSpecialty.PRIMARY_HEALTH_WORKER -> {
                    ToolkitActionTile(title = "Doorstep Immunization & Vaccine Audit", subtitle = "Record childhood / adult vaccine doses")
                    ToolkitActionTile(title = "Maternal-Child Vitals Triage", subtitle = "Log BP, blood glucose, and infant weight")
                }
                ProviderSpecialty.NURSE -> {
                    ToolkitActionTile(title = "Sterile Wound Dressing Log", subtitle = "Record exudate, stage & dressing type")
                    ToolkitActionTile(title = "IV Infusion Drip Rate Calculator", subtitle = "Calculate ml/hr and drops/min for IV fluids")
                }
                ProviderSpecialty.DOCTOR -> {
                    ToolkitActionTile(title = "E-Prescription & Lab Order Builder", subtitle = "Generate signed digital prescription")
                    ToolkitActionTile(title = "Diagnostic Clinical SOAP Notes", subtitle = "Log Subjective, Objective, Assessment & Plan")
                }
                ProviderSpecialty.PHYSIOTHERAPIST -> {
                    ToolkitActionTile(title = "Joint Range-of-Motion (ROM) Goniometer", subtitle = "Record flex/extension degrees")
                    ToolkitActionTile(title = "Home Rehab Exercise Prescription", subtitle = "Select ergonomic postures & stretching sets")
                }
                ProviderSpecialty.MENTAL_HEALTH -> {
                    ToolkitActionTile(title = "PHQ-9 & GAD-7 Assessment Calculator", subtitle = "Score depression & anxiety severity")
                    ToolkitActionTile(title = "Psychosocial Safety Plan Builder", subtitle = "Generate coping strategies & emergency contacts")
                }
                else -> {
                    ToolkitActionTile(title = "Rapid Diagnostic Test (RDT) Logger", subtitle = "Record Malaria, Typhoid & Glucose RDT results")
                    ToolkitActionTile(title = "Neighborhood Vitals Triage", subtitle = "Log blood pressure, HR & SpO2 readings")
                }
            }
        }
    }
}

@Composable
private fun ToolkitActionTile(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = OmaTealContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = OmaTeal,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                Text(text = subtitle, fontSize = 11.sp, color = DarkCharcoal.copy(alpha = 0.75f))
            }
        }
    }
}
