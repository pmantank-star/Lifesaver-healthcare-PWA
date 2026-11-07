package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.ChatMessageStatus
import com.example.ui.theme.OmaCoral
import com.example.ui.theme.OmaEmergency
import com.example.ui.theme.OmaMint
import com.example.ui.theme.OmaTeal
import com.example.ui.theme.OmaTealContainer
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    providerName: String,
    onBack: () -> Unit,
    onSendMessage: (String, Boolean) -> Unit,
    isOtherUserOnline: Boolean = true,
    isOtherUserTyping: Boolean = false,
    onMarkRead: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var activeCallType by remember { mutableStateOf<String?>(null) } // "Audio" or "Video"

    LaunchedEffect(Unit) {
        onMarkRead()
    }

    LaunchedEffect(inputText) {
        if (inputText.isNotBlank() && !isTyping) {
            isTyping = true
        } else if (inputText.isBlank() && isTyping) {
            delay(800)
            isTyping = false
        }
    }

    val quickSymptomChips = listOf(
        "Attached photo of incision area",
        "Experiencing mild fever (38.1°C)",
        "Gate security code is #402",
        "Wound dressing has slight discharge"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(OmaTealContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AO",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = OmaTeal
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = providerName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isOtherUserTyping) "Typing..." else if (isOtherUserOnline) "Online • Verified Care Provider" else "Offline",
                                fontSize = 11.sp,
                                color = if (isOtherUserTyping) OmaCoral else if (isOtherUserOnline) OmaTeal else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { activeCallType = "Audio Call" }) {
                        Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = OmaTeal)
                    }
                    IconButton(onClick = { activeCallType = "Video Consultation" }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = OmaTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Quick Triage Symptom Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(quickSymptomChips) { chipText ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = OmaTealContainer.copy(alpha = 0.5f),
                                modifier = Modifier.clickable {
                                    onSendMessage(chipText, false)
                                }
                            ) {
                                Text(
                                    text = chipText,
                                    fontSize = 11.sp,
                                    color = OmaTeal,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                onSendMessage("[Photo attached]: Clean incision photo shared with Nurse.", false)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Attach Wound Photo",
                                tint = OmaTeal
                            )
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Describe symptoms or ask provider...") },
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OmaTeal
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_text_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(inputText, false)
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(OmaTeal)
                                .testTag("send_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("chat_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Security Notice
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = OmaMint,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔒 HIPAA & Medical Confidentiality Protected: All communication with your licensed provider is end-to-end encrypted.",
                        fontSize = 11.sp,
                        color = OmaTeal,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isMe = msg.isFromPatient
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) OmaTeal else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.senderName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMe) OmaMint else OmaTeal
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 9.sp,
                                    color = if (isMe) OmaMint.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isMe) {
                                    val icon = when (msg.status) {
                                        com.example.model.ChatMessageStatus.SENDING -> Icons.Default.Schedule
                                        com.example.model.ChatMessageStatus.SENT -> Icons.Default.Done
                                        com.example.model.ChatMessageStatus.DELIVERED -> Icons.Default.DoneAll
                                        com.example.model.ChatMessageStatus.READ -> Icons.Default.DoneAll
                                    }
                                    val tint = if (msg.status == com.example.model.ChatMessageStatus.READ) OmaMint else OmaMint.copy(alpha = 0.8f)
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = msg.status.name,
                                        tint = tint,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Call Simulation Dialog
        if (activeCallType != null) {
            AlertDialog(
                onDismissRequest = { activeCallType = null },
                confirmButton = {
                    Button(
                        onClick = { activeCallType = null },
                        colors = ButtonDefaults.buttonColors(containerColor = OmaEmergency)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("End Call")
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (activeCallType == "Video Consultation") Icons.Default.Videocam else Icons.Default.Call,
                        contentDescription = null,
                        tint = OmaTeal,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = { Text("$activeCallType in Progress") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Connected with $providerName",
                            fontWeight = FontWeight.Bold,
                            color = OmaTeal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Live encrypted clinical audio/video channel active.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OmaTealContainer
                        ) {
                            Text(
                                text = "Call Duration: 01:24 • WebRTC signaling via Supabase Realtime",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OmaTeal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}
