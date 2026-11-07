package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.supabase.SupabaseConfig
import com.example.supabase.SupabaseSyncManager
import com.example.supabase.SyncStatus
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.IceMint
import com.example.ui.theme.LightMint
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseSyncSheet(
    syncManager: SupabaseSyncManager,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val configState by SupabaseConfig.configState.collectAsStateWithLifecycle()
    val syncStatus by syncManager.syncStatus.collectAsStateWithLifecycle()
    val syncMessage by syncManager.syncMessage.collectAsStateWithLifecycle()
    val lastSyncTime by syncManager.lastSyncTimestamp.collectAsStateWithLifecycle()

    var inputUrl by remember(configState.url) { mutableStateOf(configState.url) }
    var inputKey by remember(configState.anonKey) { mutableStateOf(configState.anonKey) }
    var isTesting by remember { mutableStateOf(false) }
    var showSqlSchema by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PureWhite,
        modifier = Modifier.testTag("supabase_sync_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ForestGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Supabase Cloud",
                        tint = ForestGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Supabase Cloud Sync",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ForestGreen
                    )
                    Text(
                        text = "Real-time dispatch, chat & clinician backend",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkCharcoal.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Status Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (syncStatus) {
                        SyncStatus.SYNCED -> Color(0xFFECFDF5)
                        SyncStatus.CONNECTING, SyncStatus.SYNCING -> Color(0xFFFEF3C7)
                        SyncStatus.ERROR -> Color(0xFFFEF2F2)
                        else -> IceMint
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (syncStatus) {
                        SyncStatus.SYNCED -> Color(0xFF10B981)
                        SyncStatus.CONNECTING, SyncStatus.SYNCING -> AmberAccent
                        SyncStatus.ERROR -> Color(0xFFEF4444)
                        else -> ForestGreen.copy(alpha = 0.3f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Icon(
                        imageVector = when (syncStatus) {
                            SyncStatus.SYNCED -> Icons.Default.CloudDone
                            SyncStatus.CONNECTING, SyncStatus.SYNCING -> Icons.Default.CloudQueue
                            SyncStatus.ERROR -> Icons.Default.Error
                            else -> Icons.Default.CloudOff
                        },
                        contentDescription = "Status",
                        tint = when (syncStatus) {
                            SyncStatus.SYNCED -> Color(0xFF059669)
                            SyncStatus.CONNECTING, SyncStatus.SYNCING -> AmberAccent
                            SyncStatus.ERROR -> Color(0xFFDC2626)
                            else -> ForestGreen
                        },
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (syncStatus) {
                                SyncStatus.SYNCED -> "Connected to Supabase"
                                SyncStatus.CONNECTING -> "Connecting..."
                                SyncStatus.SYNCING -> "Syncing data..."
                                SyncStatus.ERROR -> "Connection Error"
                                else -> "Local Offline Prototype Mode"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = syncMessage,
                            fontSize = 12.sp,
                            color = DarkCharcoal.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Supabase Project Credentials Form
            Text(
                text = "Supabase API Configuration",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputUrl,
                onValueChange = { inputUrl = it },
                label = { Text("Supabase Project URL") },
                placeholder = { Text("https://xyzcompany.supabase.co") },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = ForestGreen) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForestGreen,
                    focusedLabelColor = ForestGreen
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("supabase_url_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = inputKey,
                onValueChange = { inputKey = it },
                label = { Text("Supabase Anon Key (Public)") },
                placeholder = { Text("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = ForestGreen) },
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForestGreen,
                    focusedLabelColor = ForestGreen
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("supabase_anon_key_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        isTesting = true
                        SupabaseConfig.saveCustomCredentials(inputUrl, inputKey)
                        coroutineScope.launch {
                            val result = syncManager.testConnection()
                            isTesting = false
                            Toast.makeText(
                                context,
                                if (result.isSuccess) "Connected to Supabase!" else (result.exceptionOrNull()?.message ?: "Connection failed"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isTesting && inputUrl.isNotBlank() && inputKey.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("supabase_save_connect_button")
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            color = PureWhite,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Save & Connect", fontWeight = FontWeight.Bold)
                }

                if (configState.isConfigured) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                syncManager.testConnection()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                        modifier = Modifier.testTag("supabase_sync_now_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = ForestGreen.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(16.dp))

            // 1-Click Copy SQL Schema Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IceMint.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Supabase Database Schema (SQL)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ForestGreen
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Supabase SQL Schema", SupabaseConfig.schemaSql)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "SQL Schema copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_sql_schema_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy SQL", tint = ForestGreen)
                        }
                    }

                    Text(
                        text = "Includes tables for bookings, chat_messages, and providers with Row Level Security & Realtime enabled.",
                        fontSize = 11.sp,
                        color = DarkCharcoal.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showSqlSchema = !showSqlSchema },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(if (showSqlSchema) "Hide SQL Code" else "View SQL Code", fontSize = 11.sp)
                    }

                    AnimatedVisibility(visible = showSqlSchema) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = SupabaseConfig.schemaSql,
                                color = Color(0xFFF8FAFC),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
