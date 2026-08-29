package com.noor.prayertv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = EmeraldDeep,
    onPrimaryContainer = TextPrimary,
    secondary = EmeraldAccent,
    onSecondary = Color.Black,
    background = BgPrimary,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = BgSurfaceFocused,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NoorPrayerTvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // TV always dark - but we respect param
    val colorScheme = DarkColorScheme

    // Wrap with Material3 for non-TV components + TV material theme internally
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// TV Material3 theme holder - use inside MaterialTheme
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvThemeWrapper(content: @Composable () -> Unit) {
    androidx.tv.material3.MaterialTheme(
        content = content
    )
}
