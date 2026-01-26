package com.fahim.shieldcheck.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ShieldBlue80,
    onPrimary = ShieldBlue20,
    primaryContainer = ShieldBlue40,
    onPrimaryContainer = ShieldBlue80,
    secondary = TrustGreen80,
    onSecondary = TrustGreen40,
    secondaryContainer = TrustGreen40,
    onSecondaryContainer = TrustGreen80,
    tertiary = AlertAmber80,
    onTertiary = AlertAmber40,
    tertiaryContainer = AlertAmber40,
    onTertiaryContainer = AlertAmber80,
    error = DangerRed80,
    onError = DangerRed40,
    errorContainer = DangerRed40,
    onErrorContainer = DangerRed80,
    background = SurfaceDark,
    onBackground = NeutralGrey80,
    surface = SurfaceDark,
    onSurface = NeutralGrey80
)

private val LightColorScheme = lightColorScheme(
    primary = ShieldBlue40,
    onPrimary = SurfaceLight,
    primaryContainer = ShieldBlue80,
    onPrimaryContainer = ShieldBlue20,
    secondary = TrustGreen40,
    onSecondary = SurfaceLight,
    secondaryContainer = TrustGreen80,
    onSecondaryContainer = TrustGreen40,
    tertiary = AlertAmber40,
    onTertiary = SurfaceLight,
    tertiaryContainer = AlertAmber80,
    onTertiaryContainer = AlertAmber40,
    error = DangerRed40,
    onError = SurfaceLight,
    errorContainer = DangerRed80,
    onErrorContainer = DangerRed40,
    background = SurfaceLight,
    onBackground = NeutralGrey40,
    surface = SurfaceLight,
    onSurface = NeutralGrey40
)

@Composable
fun ShieldCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
