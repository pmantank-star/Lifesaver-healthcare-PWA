package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.model.UserRole
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.WarmBeige
import com.example.ui.theme.IceMint
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.LightMint
import com.example.ui.theme.PureWhite
import com.example.ui.theme.OmaCoral
import com.example.ui.theme.OmaLinen
import com.example.ui.theme.OmaOnLinenContainer
import com.example.ui.theme.OmaTeal
import com.example.ui.theme.OmaTealContainer
import com.example.ui.theme.OmaTealDark

import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.model.LifeSaversUser
import com.example.model.OmaUser

import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import com.example.supabase.SyncStatus

@Composable
fun LifeSaversTopAppBar(
    currentRole: UserRole,
    userAddress: String,
    onRoleToggle: () -> Unit,
    onLocationClick: () -> Unit,
    currentUser: LifeSaversUser? = null,
    onSignOut: (() -> Unit)? = null,
    onLogoClick: (() -> Unit)? = null,
    syncStatus: SyncStatus? = null,
    onCloudSyncClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showAccountDialog by remember { mutableStateOf(false) }
    Surface(
        color = ForestGreen,
        tonalElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Row 1: Brand & Role Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentUser != null) {
                    // When logged in: App logo and name removed and replaced with user profile icon
                    IconButton(
                        onClick = {
                            if (onProfileClick != null) {
                                onProfileClick()
                            } else {
                                showAccountDialog = true
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("user_account_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WarmBeige)
                                .border(BorderStroke(1.5.dp, AmberAccent), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Profile: ${currentUser.displayName}",
                                tint = ForestGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                } else {
                    // When not logged in: Show App Logo & Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = onLogoClick != null) { onLogoClick?.invoke() }
                            .testTag("appbar_logo_button")
                    ) {
                        Image(
                            painter = painterResource(R.drawable.lifesavers_logo),
                            contentDescription = "LifeSavers Healthcare Logo",
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LIFESAVERS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                ),
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "healthcare",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 0.3.sp
                                ),
                                color = LightMint
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Role Toggle Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(WarmBeige)
                            .clickable(onClick = onRoleToggle)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("role_toggle_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentRole == UserRole.PATIENT) Icons.Default.Person else Icons.Default.Badge,
                                contentDescription = "Role Mode",
                                tint = ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentRole == UserRole.PATIENT) "Patient" else "Provider",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Role",
                                tint = ForestGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            if (showAccountDialog && currentUser != null) {
                AlertDialog(
                    onDismissRequest = { showAccountDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Profile",
                                tint = OmaTeal,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LifeSavers Account", fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = currentUser.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = currentUser.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Account Role: ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = if (currentUser.role == UserRole.PATIENT) "Patient" else "Healthcare Specialist",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (currentUser.role == UserRole.PATIENT) OmaTeal else OmaCoral,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (currentUser.specialty != null) {
                                Text(
                                    text = "Specialty: ${currentUser.specialty.displayName}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (!currentUser.licenseNumber.isNullOrBlank()) {
                                Text(
                                    text = "License: ${currentUser.licenseNumber}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (currentUser.isGoogleUser) {
                                Text(
                                    text = "✓ Authenticated with Google Credential Manager",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OmaTeal
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showAccountDialog = false
                                onSignOut?.invoke()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Sign Out",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Out")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAccountDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Location Bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = WarmBeige,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onLocationClick)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Patient Doorstep Location",
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Doorstep: $userAddress",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Change",
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
fun OmaTopAppBar(
    currentRole: UserRole,
    userAddress: String,
    onRoleToggle: () -> Unit,
    onLocationClick: () -> Unit,
    currentUser: LifeSaversUser? = null,
    onSignOut: (() -> Unit)? = null,
    onLogoClick: (() -> Unit)? = null,
    syncStatus: SyncStatus? = null,
    onCloudSyncClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LifeSaversTopAppBar(
        currentRole = currentRole,
        userAddress = userAddress,
        onRoleToggle = onRoleToggle,
        onLocationClick = onLocationClick,
        currentUser = currentUser,
        onSignOut = onSignOut,
        onLogoClick = onLogoClick,
        syncStatus = syncStatus,
        onCloudSyncClick = onCloudSyncClick,
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}
