package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = ForestGreenContainer,
    onPrimaryContainer = OnForestGreenContainer,
    secondary = TractorTerracotta,
    onSecondary = Color.White,
    secondaryContainer = TractorTerracottaContainer,
    onSecondaryContainer = OnTractorTerracottaContainer,
    tertiary = DieselGold,
    onTertiary = Color.White,
    tertiaryContainer = DieselGoldContainer,
    onTertiaryContainer = OnDieselGoldContainer,
    background = HarvestCream,
    onBackground = SoilCharcoal,
    surface = PureCardWhite,
    onSurface = SoilCharcoal,
    surfaceVariant = WarmSand,
    onSurfaceVariant = SoilMuted,
    surfaceContainerLow = PureCardWhite,
    surfaceContainer = WarmSand,
    surfaceContainerHigh = WarmSandHigh,
    outline = WarmOutline,
    outlineVariant = Color(0xFFE3DCCF),
    error = PendingRed,
    onError = Color.White,
    errorContainer = PendingRedBg,
    onErrorContainer = Color(0xFF410E03)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,
    onPrimary = DarkOnPrimaryGreen,
    primaryContainer = ForestGreen,
    onPrimaryContainer = ForestGreenContainer,
    secondary = DarkSecondaryOrange,
    onSecondary = DarkOnSecondaryOrange,
    secondaryContainer = TractorTerracottaDark,
    onSecondaryContainer = TractorTerracottaContainer,
    tertiary = DarkTertiaryGold,
    onTertiary = DarkOnTertiaryGold,
    tertiaryContainer = Color(0xFF5C3B00),
    onTertiaryContainer = DieselGoldContainer,
    background = DarkFieldBg,
    onBackground = DarkTextPrimary,
    surface = DarkFieldSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkFieldSurfaceHigh,
    onSurfaceVariant = DarkTextMuted,
    surfaceContainerLow = DarkFieldSurface,
    surfaceContainer = DarkFieldSurfaceHigh,
    surfaceContainerHigh = Color(0xFF2B3A33),
    outline = DarkOutline,
    outlineVariant = Color(0xFF2E3A34),
    error = Color(0xFFFFB4A3),
    onError = Color(0xFF631705),
    errorContainer = Color(0xFF8C250E),
    onErrorContainer = Color(0xFFFFDAD2)
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun TractorNondTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
