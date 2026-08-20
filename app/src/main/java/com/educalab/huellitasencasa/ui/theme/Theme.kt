package com.educalab.huellitasencasa.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val HuellitasColorScheme = lightColorScheme(
    primary = HuellitasOrange,
    onPrimary = SurfaceWhite,
    primaryContainer = HuellitasOrangeLight,
    onPrimaryContainer = HuellitasBrown,
    secondary = HuellitasTeal,
    onSecondary = SurfaceWhite,
    secondaryContainer = HuellitasTealLight,
    onSecondaryContainer = HuellitasBrown,
    tertiary = HuellitasLavender,
    onTertiary = SurfaceWhite,
    background = HuellitasCream,
    onBackground = InkText,
    surface = SurfaceWhite,
    onSurface = InkText,
    surfaceVariant = HuellitasYellowLight,
    onSurfaceVariant = HuellitasBrown,
    error = HuellitasCoral,
    onError = SurfaceWhite
)

private val HuellitasShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun HuellitasEnCasaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HuellitasColorScheme,
        typography = HuellitasTypography,
        shapes = HuellitasShapes,
        content = content
    )
}
