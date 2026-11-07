package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HealthProvider
import com.example.model.ProviderSpecialty
import com.example.ui.theme.OmaCoral
import com.example.ui.theme.OmaLinen
import com.example.ui.theme.OmaMint
import com.example.ui.theme.OmaOnLinenContainer
import com.example.ui.theme.OmaTeal
import com.example.ui.theme.OmaTealContainer
import com.example.ui.theme.OmaVerifiedBlue
import kotlin.math.sqrt

@Composable
fun RadarMapView(
    providers: List<HealthProvider>,
    selectedProvider: HealthProvider?,
    onProviderSelected: (HealthProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseRatio by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRatio"
    )

    var tappedProvider by remember { mutableStateOf<HealthProvider?>(null) }
    val activeProvider = selectedProvider ?: tappedProvider

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        OmaTealContainer,
                        Color(0xFFEFF7F8),
                        OmaTealContainer
                    )
                )
            )
            .border(1.5.dp, OmaTeal.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .testTag("radar_map_container")
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = minOf(centerX, centerY) * 0.85f

        // High-Tech Medical Cross Texture Map Layer
        Image(
            painter = painterResource(id = com.example.R.drawable.bg_medical_overlay),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.18f
        )

        // Canvas for Radar Grid, Pulse Circles, and Map Lines
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(providers) {
                    detectTapGestures { tapOffset ->
                        // Hit test providers
                        var closest: HealthProvider? = null
                        var minDistance = Float.MAX_VALUE

                        providers.forEach { prov ->
                            val px = centerX + (prov.radarX * maxRadius)
                            val py = centerY + (prov.radarY * maxRadius)
                            val dx = tapOffset.x - px
                            val dy = tapOffset.y - py
                            val dist = sqrt(dx * dx + dy * dy)
                            if (dist < 60f && dist < minDistance) {
                                minDistance = dist
                                closest = prov
                            }
                        }

                        if (closest != null) {
                            tappedProvider = closest
                            onProviderSelected(closest!!)
                        }
                    }
                }
        ) {
            // Concentric range circles (1km, 2km, 3km)
            val rings = listOf(0.35f, 0.65f, 0.95f)
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            rings.forEachIndexed { index, ratio ->
                val r = maxRadius * ratio
                drawCircle(
                    color = OmaTeal.copy(alpha = 0.28f),
                    radius = r,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1.2f, pathEffect = dashEffect)
                )
            }

            // Crosshair lines
            drawLine(
                color = OmaTeal.copy(alpha = 0.18f),
                start = Offset(centerX - maxRadius, centerY),
                end = Offset(centerX + maxRadius, centerY),
                strokeWidth = 1f
            )
            drawLine(
                color = OmaTeal.copy(alpha = 0.18f),
                start = Offset(centerX, centerY - maxRadius),
                end = Offset(centerX, centerY + maxRadius),
                strokeWidth = 1f
            )

            // Dynamic pulsating wave ring
            val pulseRadius = maxRadius * pulseRatio
            val pulseAlpha = (1.0f - pulseRatio).coerceIn(0f, 0.35f)
            drawCircle(
                color = OmaTeal.copy(alpha = pulseAlpha),
                radius = pulseRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.5f)
            )

            // Draw Provider Nodes on the Radar
            providers.forEach { prov ->
                val px = centerX + (prov.radarX * maxRadius)
                val py = centerY + (prov.radarY * maxRadius)
                val isSelected = activeProvider?.id == prov.id

                val nodeColor = when (prov.specialty) {
                    ProviderSpecialty.NURSE -> Color(0xFF2DD4BF)
                    ProviderSpecialty.DOCTOR -> Color(0xFF38BDF8)
                    ProviderSpecialty.PHYSIOTHERAPIST -> Color(0xFFFBBF24)
                    ProviderSpecialty.MENTAL_HEALTH -> Color(0xFFA78BFA)
                    ProviderSpecialty.COMMUNITY_HEALTH -> Color(0xFF34D399)
                    else -> Color(0xFF2DD4BF)
                }

                // Outer glow if selected
                if (isSelected) {
                    drawCircle(
                        color = OmaCoral.copy(alpha = 0.4f),
                        radius = 24f,
                        center = Offset(px, py)
                    )
                }

                // Node circle
                drawCircle(
                    color = if (isSelected) OmaCoral else nodeColor,
                    radius = if (isSelected) 14f else 10f,
                    center = Offset(px, py)
                )

                // White inner core
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 6f else 4f,
                    center = Offset(px, py)
                )
            }

            // Center: Patient Doorstep Pin
            drawCircle(
                color = OmaTeal.copy(alpha = 0.25f),
                radius = 20f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = OmaTeal,
                radius = 12f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = OmaLinen,
                radius = 5f,
                center = Offset(centerX, centerY)
            )
        }

        // Overlay Badge: Top Left Live Status
        Box(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = OmaTeal
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Bolt for Healthcare",
                        tint = OmaLinen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "10 Nurses & 3 Online Docs Nearby",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Center Tag: You (Doorstep)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 34.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = OmaTeal
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Doorstep",
                        tint = OmaLinen,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Your Doorstep",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Bottom Selected Provider Quick Preview Card
        if (activeProvider != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = OmaTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProviderSelected(activeProvider) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(OmaTealContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeProvider.avatarInitials,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = OmaTeal
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeProvider.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified License",
                                        tint = OmaVerifiedBlue,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = "${activeProvider.specialty.badgeTitle} • ${activeProvider.distanceKm} km away (ETA ${activeProvider.etaMinutes}m)",
                                    fontSize = 11.sp,
                                    color = OmaTealContainer
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OmaLinen
                        ) {
                            Text(
                                text = "Dispatch",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OmaOnLinenContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
