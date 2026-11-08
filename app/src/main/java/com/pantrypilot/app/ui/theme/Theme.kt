package com.pantrypilot.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PantryLightColorScheme = lightColorScheme(
    primary = PantryOrange,
    onPrimary = OnPantryOrange,
    primaryContainer = PantryOrangeContainer,
    onPrimaryContainer = OnPantryOrangeContainer,

    secondary = PantryOrangeLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE9DE),
    onSecondaryContainer = Color(0xFF4A2112),

    tertiary = PantrySuccess,
    onTertiary = Color.White,
    tertiaryContainer = PantrySuccessContainer,
    onTertiaryContainer = Color(0xFF153D1B),

    background = PantryBackground,
    onBackground = PantryTextPrimary,

    surface = PantrySurface,
    onSurface = PantryOnSurface,
    surfaceVariant = PantrySurfaceVariant,
    onSurfaceVariant = PantryTextSecondary,
    surfaceTint = PantryOrange,

    error = PantryError,
    onError = Color.White,
    errorContainer = PantryErrorContainer,
    onErrorContainer = Color(0xFF410E0E),

    outline = PantryOutline,
    outlineVariant = PantryOutlineVariant,
    scrim = PantryScrim
)

@Composable
fun PantryPilotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PantryLightColorScheme,
        typography = PantryPilotTypography,
        content = content
    )
}
