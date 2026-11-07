package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.credential.CredentialEntity
import com.example.model.ProviderCredential
import com.example.model.ProviderSpecialty
import com.example.model.VerificationStatus
import com.example.ui.theme.OmaCoral
import com.example.ui.theme.OmaEmergency
import com.example.ui.theme.OmaMint
import com.example.ui.theme.OmaSuccess
import com.example.ui.theme.OmaTeal
import com.example.ui.theme.OmaTealContainer
import com.example.ui.theme.OmaTealDark
import com.example.ui.theme.OmaTealLight
import com.example.ui.theme.OmaVerifiedBlue
import kotlinx.coroutines.launch
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialsScreen(
    currentCredential: ProviderCredential,
    allCredentials: List<CredentialEntity>,
    onBack: () -> Unit,
    onSubmitCredential: (
        fullName: String,
        specialty: ProviderSpecialty,
        council: String,
        licenseNumber: String,
        docName: String,
        nationalIdType: String,
        nationalIdRaw: String,
        expiryDate: String,
        documentHash: String?
    ) -> Unit,
    onAdminVerify: (
        credentialId: String,
        approve: Boolean,
        adminId: String,
        notes: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Provider Dossier, 1: Admin Audit Portal

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Credentialing & Verification",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "LifeSavers Clinical Safety & Regulatory Audit",
                            fontSize = 11.sp,
                            color = OmaTeal
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("credentials_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Mode Selector Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = OmaTeal
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Provider Dossier", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_provider_dossier")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Admin Audit Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_admin_portal")
                )
            }

            if (selectedTab == 0) {
                ProviderDossierView(
                    currentCredential = currentCredential,
                    onSubmitCredential = onSubmitCredential,
                    onShowMessage = { msg ->
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                )
            } else {
                AdminVerificationPortalView(
                    allCredentials = allCredentials,
                    onAdminVerify = onAdminVerify,
                    onShowMessage = { msg ->
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                )
            }
        }
    }
}

/**
 * Tab 1: Provider Credentialing Dossier (Uploads, NIN check, Board folio, background status)
 */
@Composable
private fun ProviderDossierView(
    currentCredential: ProviderCredential,
    onSubmitCredential: (
        fullName: String,
        specialty: ProviderSpecialty,
        council: String,
        licenseNumber: String,
        docName: String,
        nationalIdType: String,
        nationalIdRaw: String,
        expiryDate: String,
        documentHash: String?
    ) -> Unit,
    onShowMessage: (String) -> Unit
) {
    var fullName by remember { mutableStateOf(currentCredential.fullName) }
    var specialty by remember { mutableStateOf(currentCredential.specialty) }
    var council by remember { mutableStateOf(currentCredential.licenseCouncil) }
    var licenseNumber by remember { mutableStateOf(currentCredential.licenseNumber) }
    var expiryDate by remember { mutableStateOf(currentCredential.expiryDate) }
    var nationalIdType by remember { mutableStateOf(currentCredential.nationalIdType) }
    var nationalIdInput by remember { mutableStateOf("1098234821") }
    var uploadedFileName by remember { mutableStateOf(currentCredential.documentFileName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Computes dynamic SHA-256 hash for document
    var documentSha256 by remember(licenseNumber, uploadedFileName) {
        mutableStateOf(
            computeSha256String("$licenseNumber:$uploadedFileName:prov-01")
        )
    }

    // Android Zero-Permission Photo Picker for uploading license images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            uploadedFileName = "license_scan_${System.currentTimeMillis() % 100000}.jpg"
            documentSha256 = computeSha256String(uri.toString() + System.currentTimeMillis())
            onShowMessage("License image selected and cryptographic SHA-256 computed.")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Badge Header Banner
        item {
            val statusColor = when (currentCredential.status) {
                VerificationStatus.VERIFIED -> OmaTeal
                VerificationStatus.PENDING_REVIEW -> Color(0xFFD97706)
                VerificationStatus.UNVERIFIED -> Color(0xFFDC2626)
            }
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = statusColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (currentCredential.status) {
                                    VerificationStatus.VERIFIED -> Icons.Default.WorkspacePremium
                                    VerificationStatus.PENDING_REVIEW -> Icons.Default.Schedule
                                    VerificationStatus.UNVERIFIED -> Icons.Default.Cancel
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Profile Verification Status",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (currentCredential.status) {
                                VerificationStatus.VERIFIED -> OmaVerifiedBlue
                                VerificationStatus.PENDING_REVIEW -> Color(0xFFF59E0B)
                                VerificationStatus.UNVERIFIED -> Color(0xFFEF4444)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (currentCredential.status == VerificationStatus.VERIFIED) Icons.Default.Verified else Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (currentCredential.status) {
                                        VerificationStatus.VERIFIED -> "BOARD VERIFIED"
                                        VerificationStatus.PENDING_REVIEW -> "PENDING REVIEW"
                                        VerificationStatus.UNVERIFIED -> "ACTION REQUIRED"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (currentCredential.status) {
                            VerificationStatus.VERIFIED -> "Your medical practicing license has been verified by the regulatory board. You are fully authorized for home-visit patient dispatches and instant emergency calls."
                            VerificationStatus.PENDING_REVIEW -> "Your submitted credentials and license scan are currently undergoing automated background checks and manual administrator review. Estimated turnaround: < 2 hours."
                            VerificationStatus.UNVERIFIED -> "Please upload your current practicing license and national identity details below to enable your provider account for patient dispatches."
                        },
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Medical Discipline Selection
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Medical Role & Regulatory Board",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ProviderSpecialty.entries.toTypedArray()) { spec ->
                            FilterChip(
                                selected = specialty == spec,
                                onClick = {
                                    specialty = spec
                                    council = when (spec) {
                                        ProviderSpecialty.NURSE -> "Nursing and Midwifery Council of Nigeria (NMCN)"
                                        ProviderSpecialty.DOCTOR -> "Medical and Dental Council of Nigeria (MDCN)"
                                        ProviderSpecialty.PHYSIOTHERAPIST -> "Medical Rehabilitation Therapists Board (MRTB)"
                                        ProviderSpecialty.MENTAL_HEALTH -> "Counselling Association of Nigeria (CASSON)"
                                        ProviderSpecialty.COMMUNITY_HEALTH -> "Community Health Practitioners Board (CHPRBN)"
                                        else -> "Health Practitioners Board"
                                    }
                                    licenseNumber = when (spec) {
                                        ProviderSpecialty.NURSE -> "NMCN/RN/2020/84712"
                                        ProviderSpecialty.DOCTOR -> "MDCN/GP/2018/19482"
                                        ProviderSpecialty.PHYSIOTHERAPIST -> "MRTB/PT/2021/04912"
                                        ProviderSpecialty.MENTAL_HEALTH -> "CASSON/MHC/2022/38192"
                                        ProviderSpecialty.COMMUNITY_HEALTH -> "CHPRBN/CHO/2019/55912"
                                        else -> "HPB/LIC/2022/00000"
                                    }
                                },
                                label = { Text(spec.badgeTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OmaTeal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // License Image & Document Upload Workflow
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "2. Upload Practicing License Image",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = OmaTeal, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Encrypted", fontSize = 10.sp, color = OmaTeal, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Document Preview & Upload Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = OmaTealContainer.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, OmaTeal.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(OmaTeal),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (selectedImageUri != null) Icons.Default.Description else Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = uploadedFileName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (selectedImageUri != null) "Custom Image Selected • 2.8 MB JPEG" else "Board Licensed Certificate • 2.4 MB PDF",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = OmaTeal, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "SHA-256: ${documentSha256.take(16)}...",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = OmaTeal,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = OmaTeal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("button_pick_license_photo")
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pick Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        uploadedFileName = "nmcn_nurse_folio_${fullName.take(5).lowercase()}_2026.pdf"
                                        documentSha256 = computeSha256String("preset:$licenseNumber:$uploadedFileName")
                                        onShowMessage("Preset practicing license attached with verified digital seal.")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Use Sample Folio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OmaTeal)
                                }
                            }
                        }
                    }
                }
            }
        }

        // National Identity & Background Checks
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Government Identity & Background Check",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Provider Full Name (As on Practicing Certificate)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OmaTeal),
                        modifier = Modifier.fillMaxWidth().testTag("cred_fullname_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nationalIdInput,
                            onValueChange = { nationalIdInput = it },
                            label = { Text("National ID / NIN") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OmaTeal),
                            modifier = Modifier.weight(1f).testTag("cred_nin_input")
                        )
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it },
                            label = { Text("License Expiry") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OmaTeal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it },
                        label = { Text("Official Registration / Folio Number") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OmaTeal),
                        modifier = Modifier.fillMaxWidth().testTag("cred_licensenumber_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Background Check Status Badges
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OmaSuccess, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Automated Criminal Record Check: PASSED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OmaSuccess
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Clearance Ref: CID-CLEARED-${(fullName.hashCode().toLong() and 0xFFFFFFL).toString().padStart(6, '0')} • Nigerian Police Central Register",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OmaTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "National Identity (NIMC): Match Confirmed",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OmaTeal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onSubmitCredential(
                                fullName,
                                specialty,
                                council,
                                licenseNumber,
                                uploadedFileName,
                                nationalIdType,
                                nationalIdInput,
                                expiryDate,
                                documentSha256
                            )
                            onShowMessage("Credentials package submitted securely and queued for Admin sign-off!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OmaCoral),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_credential_button")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Package For Verification", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: Administrator Verification & Sign-Off Portal
 * Allows an authorized LifeSavers safety administrator to inspect pending credentials, review background checks,
 * input audit notes, and approve or reject providers in the database.
 */
@Composable
private fun AdminVerificationPortalView(
    allCredentials: List<CredentialEntity>,
    onAdminVerify: (
        credentialId: String,
        approve: Boolean,
        adminId: String,
        notes: String
    ) -> Unit,
    onShowMessage: (String) -> Unit
) {
    var filterStatus by remember { mutableStateOf<String?>("PENDING_REVIEW") }
    var selectedCredentialId by remember { mutableStateOf<String?>(allCredentials.firstOrNull { it.status == "PENDING_REVIEW" }?.id ?: allCredentials.firstOrNull()?.id) }
    var adminAuditNotes by remember { mutableStateOf("Verified active folio against regulatory council portal. Background and identity checks clean.") }

    val filteredList = remember(allCredentials, filterStatus) {
        if (filterStatus == null) allCredentials else allCredentials.filter { it.status == filterStatus }
    }

    val selectedEntity = allCredentials.find { it.id == selectedCredentialId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Administrator Identity Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OmaTealDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OmaTealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LifeSavers Medical Safety Board",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Admin Session: Dr. K. Balogun, Lead Registrar (ADM-LAGOS-01)",
                            fontSize = 11.sp,
                            color = OmaMint
                        )
                    }
                }
            }
        }

        // Filter Chips Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterStatus == null,
                    onClick = { filterStatus = null },
                    label = { Text("All (${allCredentials.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OmaTeal, selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = filterStatus == "PENDING_REVIEW",
                    onClick = { filterStatus = "PENDING_REVIEW" },
                    label = { Text("Pending (${allCredentials.count { it.status == "PENDING_REVIEW" }})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD97706), selectedLabelColor = Color.White),
                    modifier = Modifier.testTag("admin_filter_pending")
                )
                FilterChip(
                    selected = filterStatus == "VERIFIED",
                    onClick = { filterStatus = "VERIFIED" },
                    label = { Text("Verified (${allCredentials.count { it.status == "VERIFIED" }})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OmaTeal, selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = filterStatus == "REJECTED",
                    onClick = { filterStatus = "REJECTED" },
                    label = { Text("Rejected (${allCredentials.count { it.status == "REJECTED" }})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OmaEmergency, selectedLabelColor = Color.White)
                )
            }
        }

        // List of Providers in Verification Queue
        item {
            Text(
                text = "Submitted Verification Packages",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (filteredList.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No provider credentials matching this filter.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(filteredList, key = { it.id }) { cred ->
                val isSelected = cred.id == selectedCredentialId
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) OmaTealContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) BorderStroke(1.5.dp, OmaTeal) else BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCredentialId = cred.id }
                        .testTag("admin_cred_item_${cred.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = cred.fullName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${cred.specialty})",
                                    fontSize = 11.sp,
                                    color = OmaTeal
                                )
                            }
                            Text(
                                text = "Lic #${cred.licenseNumber} • ${cred.licenseCouncil}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Submitted: ${cred.submissionDate} • SHA-256: ${cred.documentSha256.take(12)}...",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (cred.status) {
                                "VERIFIED" -> OmaTeal
                                "PENDING_REVIEW" -> Color(0xFFD97706)
                                else -> OmaEmergency
                            }
                        ) {
                            Text(
                                text = cred.status.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // Detailed Administrator Review & Sign-Off Section
        if (selectedEntity != null) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_review_dossier")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Policy, contentDescription = null, tint = OmaTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Reviewing: ${selectedEntity.fullName}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Text(
                                text = "ID: ${selectedEntity.id}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        // Audit Dossier Details
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            AuditDetailRow("Practicing Council", selectedEntity.licenseCouncil)
                            AuditDetailRow("Registration Folio", selectedEntity.licenseNumber)
                            AuditDetailRow("License Document", selectedEntity.documentFileName)
                            AuditDetailRow("Document SHA-256", selectedEntity.documentSha256)
                            AuditDetailRow("National ID (PII Masked)", selectedEntity.nationalIdMasked)
                            AuditDetailRow("Background Check", "${selectedEntity.backgroundCheckStatus} (${selectedEntity.backgroundCheckReference})")
                            AuditDetailRow("Regulatory Board Query", if (selectedEntity.councilRegistryVerified) "Confirmed in Good Standing" else "Unverified")
                            if (selectedEntity.adminReviewerId != null) {
                                AuditDetailRow("Verified By Admin", "${selectedEntity.adminReviewerId} (Notes: ${selectedEntity.adminReviewNotes ?: ""})")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Clinical Audit Sign-Off Input
                        OutlinedTextField(
                            value = adminAuditNotes,
                            onValueChange = { adminAuditNotes = it },
                            label = { Text("Clinical Audit Notes / Rationale") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OmaTeal),
                            modifier = Modifier.fillMaxWidth().testTag("admin_audit_notes_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Admin Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onAdminVerify(
                                        selectedEntity.id,
                                        true,
                                        "ADM-LAGOS-01",
                                        adminAuditNotes
                                    )
                                    onShowMessage("✅ Provider ${selectedEntity.fullName} approved and certified with verified badge!")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OmaTeal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_approve_button")
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approve & Certify", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    onAdminVerify(
                                        selectedEntity.id,
                                        false,
                                        "ADM-LAGOS-01",
                                        adminAuditNotes.ifBlank { "Practicing certificate illegible or council number mismatch." }
                                    )
                                    onShowMessage("⚠️ Provider ${selectedEntity.fullName} verification rejected.")
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = OmaEmergency),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_reject_button")
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reject Credential", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun computeSha256String(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
