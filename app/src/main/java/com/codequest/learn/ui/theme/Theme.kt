package com.codequest.learn.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = Neutral950,
    primaryContainer = Blue900,
    onPrimaryContainer = Blue400,

    secondary = Green500,
    onSecondary = Neutral950,
    secondaryContainer = Green900,
    onSecondaryContainer = Green400,

    tertiary = Yellow500,
    onTertiary = Neutral950,
    tertiaryContainer = Yellow900,
    onTertiaryContainer = Yellow400,

    error = Red500,
    onError = Neutral950,
    errorContainer = Red900,
    onErrorContainer = Red400,

    background = Neutral950,
    onBackground = Neutral100,

    surface = Neutral900,
    onSurface = Neutral100,
    surfaceVariant = Neutral800,
    onSurfaceVariant = Neutral300,

    outline = Neutral700,
    outlineVariant = Neutral800
)

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF4FF),
    onPrimaryContainer = Color(0xFF0969DA),

    secondary = Green500,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDAFBE1),
    onSecondaryContainer = Color(0xFF1A7F37),

    tertiary = Yellow500,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF8C5),
    onTertiaryContainer = Color(0xFF9A6700),

    error = Red500,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBE9),
    onErrorContainer = Color(0xFFCF222E),

    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF1F2328),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2328),
    surfaceVariant = Color(0xFFEAEEF2),
    onSurfaceVariant = Color(0xFF57606A),

    outline = Color(0xFFD0D7DE),
    outlineVariant = Color(0xFFE1E4E8)
)

@Composable
fun CodeQuestTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
