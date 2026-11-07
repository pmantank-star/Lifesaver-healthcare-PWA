package com.example.ui.screens

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.WarmBeige
import com.example.ui.theme.IceMint
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.PureWhite
import com.example.ui.theme.OmaTeal
import com.example.ui.theme.OmaTealContainer
import com.example.ui.theme.OmaTealDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Autonomous, cinematic brand reveal screen for LifeSavers Healthcare matching
 * the reference video choreography and prominently showcasing the smiling healthcare team:
 * 1. Warm, smiling healthcare team portrait in royal blue scrubs is prominently
 *    displayed in full resolution across the background
 * 2. An elegant, modern elevated frosted glass card houses the animated brand lockup
 * 3. Circle medallion enters with elastic scale & opacity keyframes
 * 4. Unified lockup unrolls smoothly, ensuring the initial "LIF" letters
 *    are immediately and crisply visible with zero medallion overlap
 * 5. Tagline & trust pill enter with smooth opacity/scale keyframes
 * 6. Screen gracefully transitions into the app
 */
@Composable
fun LogoRevealScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Prevent back button from trapping the user
    BackHandler {
        onFinish()
    }

    // Audio playback for the brand reveal whoosh & chime
    DisposableEffect(Unit) {
        var player: MediaPlayer? = null
        try {
            player = MediaPlayer.create(context, R.raw.reveal_sound)
            player?.setOnCompletionListener { mp ->
                try { mp.release() } catch (_: Exception) {}
            }
            player?.start()
        } catch (_: Exception) {
            // Audio fallback if muted or device audio unavailable
        }

        onDispose {
            try {
                if (player?.isPlaying == true) {
                    player?.stop()
                }
                player?.release()
            } catch (_: Exception) {}
        }
    }

    // Keyframe Animation States for Stage Elements
    // Healthcare Team Backdrop Animation (prominent, full-vibrancy entrance)
    val teamAlpha = remember { Animatable(0f) }
    val teamScale = remember { Animatable(1.05f) }

    // Stage 1: Medallion Entry (Opacity & Scale Keyframes)
    val circleScale = remember { Animatable(0.35f) }
    val circleAlpha = remember { Animatable(0f) }

    // Stage 2: Typography Reveal & Gliding Expansion
    // "LIFESAVERS healthcare" unmasks from behind the circle with clear initial "LIF"
    val textRevealProgress = remember { Animatable(0f) } // 0f -> 1f
    val textAlpha = remember { Animatable(0f) }
    val textScale = remember { Animatable(0.92f) }

    // Stage 3: Tagline & Trust Pill Keyframe Entry (Opacity & Scale)
    val subtitleAlpha = remember { Animatable(0f) }
    val subtitleScale = remember { Animatable(0.90f) }
    val subtitleOffsetY = remember { Animatable(16f) }

    // Stage 4: Next Button Keyframe Entry (Opacity & Scale)
    val buttonAlpha = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0.85f) }

    // Stage 5: Outro Fade on Next click
    val screenAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Stage 0: Healthcare team portrait fades in vividly with slow cinematic settle
        launch {
            teamAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
        }
        launch {
            teamScale.animateTo(
                targetValue = 1.00f,
                animationSpec = tween(durationMillis = 5000, easing = FastOutSlowInEasing)
            )
        }

        // Stage 1: Circle Medallion Keyframe Entrance (Scale + Opacity)
        launch {
            circleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 420
                    0f at 0 with LinearEasing
                    0.65f at 160 with LinearEasing
                    1f at 420 with LinearEasing
                }
            )
        }
        launch {
            circleScale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 520
                    0.35f at 0 with FastOutSlowInEasing
                    1.08f at 360 with FastOutSlowInEasing // Subtle overshoot entry
                    0.98f at 460 with FastOutSlowInEasing
                    1.00f at 520 with FastOutSlowInEasing
                }
            )
        }

        // Stage 2: Typography Unmasking & Glide (starts at 380ms)
        delay(380)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0 with LinearEasing
                    0.80f at 160 with LinearEasing
                    1f at 400 with LinearEasing
                }
            )
        }
        launch {
            textScale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 650
                    0.92f at 0 with FastOutSlowInEasing
                    1.03f at 420 with FastOutSlowInEasing
                    1.00f at 650 with FastOutSlowInEasing
                }
            )
        }
        launch {
            textRevealProgress.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 680
                    0f at 0 with FastOutSlowInEasing
                    0.24f at 150 with FastOutSlowInEasing // Unveils "LIF" clearly and immediately
                    0.72f at 440 with FastOutSlowInEasing
                    1.00f at 680 with FastOutSlowInEasing
                }
            )
        }

        // Stage 3: Tagline & Trust Pill Keyframe Entry (starts at 950ms)
        delay(570)
        launch {
            subtitleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 450
                    0f at 0 with LinearEasing
                    0.75f at 220 with LinearEasing
                    1f at 450 with LinearEasing
                }
            )
        }
        launch {
            subtitleScale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 450
                    0.90f at 0 with FastOutSlowInEasing
                    1.02f at 320 with FastOutSlowInEasing
                    1.00f at 450 with FastOutSlowInEasing
                }
            )
        }
        launch {
            subtitleOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
        }

        // Stage 4: Next button entrance (smooth scale & fade-in)
        delay(250)
        launch {
            buttonAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )
        }
        launch {
            buttonScale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0.85f at 0 with FastOutSlowInEasing
                    1.06f at 280 with FastOutSlowInEasing
                    1.00f at 400 with FastOutSlowInEasing
                }
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .graphicsLayer {
                alpha = screenAlpha.value
            }
            .testTag("logo_reveal_screen"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Prominent Smiling Healthcare Team Portrait (100% visible, high-definition)
        Image(
            painter = painterResource(R.drawable.img_healthcare_team),
            contentDescription = "Smiling Healthcare Team Portrait",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = teamAlpha.value
                    scaleX = teamScale.value
                    scaleY = teamScale.value
                }
        )

        // 2. Cinematic Vignette (keeps the faces bright and vivid while grounding the bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        // 3. Floating Modern Translucent Frosted Card housing the Brand Reveal Lockup
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite.copy(alpha = 0.65f),
            shadowElevation = 16.dp,
            border = BorderStroke(1.5.dp, PureWhite.copy(alpha = 0.85f)),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .wrapContentSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                // Main Brand Lockup Stage: Centered Row ensuring Circle & Text never overlap
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .wrapContentSize()
                        .height(68.dp)
                ) {
                    // Circle Medallion (starts centered, glides naturally left as text emerges)
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .graphicsLayer {
                                scaleX = circleScale.value
                                scaleY = circleScale.value
                                alpha = circleAlpha.value
                            }
                            .clip(CircleShape)
                            .background(PureWhite)
                            .testTag("lifesavers_reveal_logo"),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.lifesavers_logo),
                            contentDescription = "LifeSavers Healthcare Logo",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    // Brand Typography Container: placed adjacent to medallion with dedicated margin
                    // Starts at width 0 and smoothly unrolls "LIFESAVERS healthcare"
                    Box(
                        modifier = Modifier
                            .padding(start = (12.dp * textRevealProgress.value))
                            .width((265.dp * textRevealProgress.value))
                            .height(48.dp)
                            .clipToBounds()
                            .graphicsLayer {
                                alpha = textAlpha.value
                                scaleX = textScale.value
                                scaleY = textScale.value
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.width(265.dp)
                        ) {
                            Text(
                                text = "LIFESAVERS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                ),
                                color = ForestGreen,
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "healthcare",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 0.3.sp
                                ),
                                color = ForestGreen,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                // Tagline & Clinical Trust Pillar: Card drops down & expands as logo reveals
                AnimatedVisibility(
                    visible = textRevealProgress.value > 0.35f,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(durationMillis = 400))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .graphicsLayer {
                                alpha = subtitleAlpha.value
                                scaleX = subtitleScale.value
                                scaleY = subtitleScale.value
                                translationY = subtitleOffsetY.value
                            }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AmberAccent,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "NURSES & ONLINE DOCTORS NEAR YOU",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp,
                                color = DarkCharcoal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = "Verified Medical Professionals at Your Doorstep",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.4.sp,
                            color = DarkCharcoal.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 4. Action: Next Button positioned down at the bottom of the screen
        Button(
            onClick = {
                coroutineScope.launch {
                    screenAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                    )
                    onFinish()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ForestGreen,
                contentColor = PureWhite
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 14.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp)
                .graphicsLayer {
                    alpha = buttonAlpha.value
                    scaleX = buttonScale.value
                    scaleY = buttonScale.value
                }
                .testTag("logo_reveal_next_button")
        ) {
            Text(
                text = "Next",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = PureWhite
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                tint = PureWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
