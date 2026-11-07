package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =========================================================================
// Material 3 Color Schemes - Forest Green, Amber, Warm Beige, Ice Mint
// =========================================================================

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenLight,
    onPrimary = PureWhite,
    primaryContainer = ForestGreenDark,
    onPrimaryContainer = LightMint,
    secondary = AmberAccent,
    onSecondary = DarkCharcoal,
    secondaryContainer = Color(0xFF243B38),
    onSecondaryContainer = WarmBeige,
    tertiary = AmberAccentLight,
    onTertiary = DarkCharcoal,
    background = Color(0xFF0F1E1D),
    surface = Color(0xFF162A28),
    surfaceVariant = Color(0xFF203835),
    onBackground = LightMint,
    onSurface = LightMint,
    onSurfaceVariant = WarmBeige,
    outline = LifeSaversOutline
)

private val LightColorScheme = lightColorScheme(
    // Primary Brand Color: Deep Forest Green (#01514B)
    primary = ForestGreen,
    onPrimary = PureWhite,
    primaryContainer = WarmBeige,
    onPrimaryContainer = ForestGreen,

    // Secondary / Accent Color: Amber Accent (#F59E0B)
    secondary = AmberAccent,
    onSecondary = DarkCharcoal,
    secondaryContainer = WarmBeigeDark,
    onSecondaryContainer = DarkCharcoal,

    // Tertiary: Amber Highlights
    tertiary = AmberAccent,
    onTertiary = DarkCharcoal,

    // Background: Ice Mint (#DBE9EB)
    background = IceMint,
    onBackground = ForestGreen,

    // Surface: Pure White (#FFFFFF) for crisp white cards and containers
    surface = PureWhite,
    onSurface = DarkCharcoal,
    surfaceVariant = PureWhite,
    onSurfaceVariant = DarkCharcoal,

    outline = LifeSaversOutline
)

@Composable
fun LifeSaversTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun OmaTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LifeSaversTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
