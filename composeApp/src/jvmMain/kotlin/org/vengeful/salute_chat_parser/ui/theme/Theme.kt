package org.vengeful.salute_chat_parser.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = darkCyan,
    secondary = blue,
    tertiary = pink,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8E8E8),
    error = Color(0xFFBA1A1A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = deepBlue,
    onSurface = deepBlue,
    onSurfaceVariant = blue,
    onError = Color.White,
    primaryContainer = Color(0xFFB3E5E5),
    secondaryContainer = Color(0xFFB3D4E5),
    tertiaryContainer = Color(0xFFFFE5F0),
    onPrimaryContainer = Color(0xFF002020),
    onSecondaryContainer = Color(0xFF001F2E),
    onTertiaryContainer = Color(0xFF3F0024),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF6F7979),
    outlineVariant = Color(0xFFBFC9C9),
    scrim = Color.Black,
    inverseSurface = deepBlue,
    inverseOnSurface = Color(0xFFF0F0F0),
    inversePrimary = Color(0xFF4DD1D1),
    surfaceDim = Color(0xFFD5D5D5),
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF0F0F0),
    surfaceContainer = Color(0xFFEAEAEA),
    surfaceContainerHigh = Color(0xFFE4E4E4),
    surfaceContainerHighest = Color(0xFFDEDEDE)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4DD1D1),
    secondary = Color(0xFF8BC5D9),
    tertiary = Color(0xFFFFB3D1),
    background = deepBlue,
    surface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFF2F2F2F),
    error = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF003737),
    onSecondary = Color(0xFF00344D),
    onTertiary = Color(0xFF66003D),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFFBFC9C9),
    onError = Color(0xFF690005),
    primaryContainer = Color(0xFF004D4D),
    secondaryContainer = Color(0xFF004D6A),
    tertiaryContainer = Color(0xFF8F0057),
    onPrimaryContainer = Color(0xFFB3E5E5),
    onSecondaryContainer = Color(0xFFB3D4E5),
    onTertiaryContainer = Color(0xFFFFD9E5),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF899393),
    outlineVariant = Color(0xFF3F4949),
    scrim = Color.Black,
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF1A1A1A),
    inversePrimary = darkCyan,
    surfaceDim = Color(0xFF0F0F0F),
    surfaceBright = Color(0xFF383838),
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF1F1F1F),
    surfaceContainerHigh = Color(0xFF292929),
    surfaceContainerHighest = Color(0xFF333333)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}