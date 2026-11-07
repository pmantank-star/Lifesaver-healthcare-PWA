package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// Official Brand Color Palette Scheme
// =========================================================================

// Primary Brand Color: Deep Forest Green (#01514B)
// Usage: App bar/header background, primary buttons, major text headers, active icons
val ForestGreen = Color(0xFF01514B)
val ForestGreenDark = Color(0xFF003834)
val ForestGreenLight = Color(0xFF0D6861)

// Secondary / Accent Color: Amber Accent (#F59E0B)
// Usage: Floating Action Buttons (FAB), notification badges, call-to-action highlights, star ratings
val AmberAccent = Color(0xFFF59E0B)
val AmberAccentDark = Color(0xFFD97706)
val AmberAccentLight = Color(0xFFFCD34D)

// Surface Color: Warm Beige (#ECDDCF)
// Usage: Card backgrounds, modal bottom sheets, input field containers, elevated list items
val WarmBeige = Color(0xFFECDDCF)
val WarmBeigeDark = Color(0xFFD8C5B3)
val WarmBeigeLight = Color(0xFFF7EFE7)

// Background Color: Ice Mint (#DBE9EB)
// Usage: Main scaffold background, screen page backgrounds, soft section dividers
val IceMint = Color(0xFFDBE9EB)
val IceMintDark = Color(0xFFC3D8DC)
val IceMintLight = Color(0xFFEDF5F6)

// Typography & On-Color Contrast
val DarkCharcoal = Color(0xFF1A2B29) // Text on Ice Mint / Warm Beige / Amber Accent
val LightMint = Color(0xFFE6F0F0)    // Secondary text on Forest Green
val PureWhite = Color(0xFFFFFFFF)    // Primary text on Forest Green

// Centralized AppColors holder for direct Compose references
object AppColors {
    val Primary = ForestGreen
    val PrimaryDark = ForestGreenDark
    val PrimaryLight = ForestGreenLight
    val Secondary = AmberAccent
    val SecondaryDark = AmberAccentDark
    val Surface = PureWhite
    val SurfaceDark = WarmBeigeDark
    val Background = IceMint
    val OnPrimary = PureWhite
    val OnPrimaryVariant = LightMint
    val OnSurface = DarkCharcoal
    val OnBackground = ForestGreen
    val OnSecondary = DarkCharcoal
    val Divider = Color(0xFFB5CACD)
}

// Brand Theme Colors
val LifeSaversTeal = ForestGreen
val LifeSaversTealDark = ForestGreenDark
val LifeSaversTealLight = ForestGreenLight
val LifeSaversTealContainer = WarmBeige
val LifeSaversOnTealContainer = ForestGreen

val LifeSaversLinen = WarmBeige
val LifeSaversLinenContainer = WarmBeigeDark
val LifeSaversOnLinenContainer = DarkCharcoal

val LifeSaversCoral = AmberAccent
val LifeSaversCoralDark = AmberAccentDark
val LifeSaversCoralContainer = WarmBeige
val LifeSaversOnCoralContainer = DarkCharcoal

val LifeSaversMint = IceMint
val LifeSaversSurface = PureWhite
val LifeSaversSurfaceVariant = PureWhite
val LifeSaversOnSurface = DarkCharcoal
val LifeSaversOnSurfaceVariant = ForestGreen
val LifeSaversOutline = Color(0xFFB5CACD)

val LifeSaversSuccess = Color(0xFF10B981)
val LifeSaversWarning = AmberAccent
val LifeSaversEmergency = Color(0xFFDC2626)
val LifeSaversVerifiedBlue = ForestGreen

// Backwards compatibility aliases
val OmaTeal = LifeSaversTeal
val OmaTealDark = LifeSaversTealDark
val OmaTealLight = LifeSaversTealLight
val OmaTealContainer = LifeSaversTealContainer
val OmaOnTealContainer = LifeSaversOnTealContainer
val OmaLinen = LifeSaversLinen
val OmaLinenContainer = LifeSaversLinenContainer
val OmaOnLinenContainer = LifeSaversOnLinenContainer
val OmaCoral = LifeSaversCoral
val OmaCoralDark = LifeSaversCoralDark
val OmaCoralContainer = LifeSaversCoralContainer
val OmaOnCoralContainer = LifeSaversOnCoralContainer
val OmaMint = LifeSaversMint
val OmaSurface = LifeSaversSurface
val OmaSurfaceVariant = LifeSaversSurfaceVariant
val OmaOnSurface = LifeSaversOnSurface
val OmaOnSurfaceVariant = LifeSaversOnSurfaceVariant
val OmaOutline = LifeSaversOutline
val OmaSuccess = LifeSaversSuccess
val OmaWarning = LifeSaversWarning
val OmaEmergency = LifeSaversEmergency
val OmaVerifiedBlue = LifeSaversVerifiedBlue
