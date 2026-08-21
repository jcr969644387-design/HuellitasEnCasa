package com.educalab.huellitasencasa.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.educalab.huellitasencasa.domain.model.SpeciesCode
import kotlin.math.sin

/** Estado de ánimo visual de la mascota, derivado del bienestar general (no de un diagnóstico). */
enum class PetMood { FELIZ, NEUTRAL, CANSADO, HAMBRIENTO }

private data class SpeciesPalette(val body: Color, val bodyDark: Color, val accent: Color)

private fun paletteFor(species: SpeciesCode): SpeciesPalette = when (species) {
    SpeciesCode.PERRO -> SpeciesPalette(Color(0xFFE0A46E), Color(0xFFB5713A), Color(0xFFFFF3EA))
    SpeciesCode.GATO -> SpeciesPalette(Color(0xFFB9AFC7), Color(0xFF8A7DA6), Color(0xFFF1EDFA))
    SpeciesCode.CONEJO -> SpeciesPalette(Color(0xFFF3E7DA), Color(0xFFD9BFA6), Color(0xFFF2A6C0))
    SpeciesCode.HAMSTER -> SpeciesPalette(Color(0xFFE7B978), Color(0xFFC08A45), Color(0xFFFFF3D0))
    SpeciesCode.AVE -> SpeciesPalette(Color(0xFF7FCFC0), Color(0xFF4FB0A5), Color(0xFFFFC93C))
}

/**
 * Dibuja una mascota estilizada, plana y amigable con Compose Canvas. Se anima suavemente
 * (leve respiración/balanceo) y cambia de expresión según [mood]. No usa ninguna imagen remota:
 * todo se dibuja localmente con primitivas vectoriales.
 */
@Composable
fun PetIllustration(
    species: SpeciesCode,
    mood: PetMood,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    val palette = paletteFor(species)
    val transition = rememberInfiniteTransition(label = "pet_breath")
    val breath by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "breath"
    )
    val blink by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3200
                0f at 0
                0f at 2900
                1f at 3000
                0f at 3100
                0f at 3200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val bob = sin(breath * Math.PI).toFloat() * h * 0.015f

        val bodyCenter = Offset(w / 2f, h * 0.64f + bob)
        val headCenter = Offset(w / 2f, h * 0.37f + bob)
        val bodyRadius = w * 0.29f
        val headRadius = w * 0.3f

        // Sombra suave
        drawOval(
            color = Color(0x22000000),
            topLeft = Offset(w * 0.28f, h * 0.92f),
            size = Size(w * 0.44f, h * 0.06f)
        )

        // Orejas / apéndices específicos por especie (detrás del cuerpo)
        drawSpeciesAppendages(species, palette, headCenter, headRadius)

        // Cuerpo
        drawCircle(color = palette.body, radius = bodyRadius, center = bodyCenter)
        // Patas, para que se apoye en el suelo en vez de flotar como una figura ovalada
        drawFeet(species, palette, bodyCenter, bodyRadius)
        // Cabeza
        drawCircle(color = palette.body, radius = headRadius, center = headCenter)
        // Mejillas/pecho claro
        drawCircle(color = palette.accent, radius = headRadius * 0.5f, center = Offset(headCenter.x, headCenter.y + headRadius * 0.35f))
        // Hocico/pico especifico de la especie, para que se distinga de un huevo liso
        drawSpeciesSnout(species, headCenter, headRadius)
        // Sonrojo, para una expresión más cálida y menos plana
        val blushColor = Color(0xFFFFAFAF).copy(alpha = 0.4f)
        drawCircle(blushColor, radius = headRadius * 0.16f, center = Offset(headCenter.x - headRadius * 0.68f, headCenter.y + headRadius * 0.1f))
        drawCircle(blushColor, radius = headRadius * 0.16f, center = Offset(headCenter.x + headRadius * 0.68f, headCenter.y + headRadius * 0.1f))

        drawFace(mood, palette, headCenter, headRadius, blink)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSpeciesAppendages(
    species: SpeciesCode,
    palette: SpeciesPalette,
    headCenter: Offset,
    headRadius: Float
) {
    when (species) {
        SpeciesCode.PERRO -> {
            // orejas caídas, mas cercanas a la cabeza para no romper la silueta redondeada
            drawOval(palette.bodyDark, topLeft = Offset(headCenter.x - headRadius * 1.0f, headCenter.y - headRadius * 0.55f), size = Size(headRadius * 0.55f, headRadius * 1.0f))
            drawOval(palette.bodyDark, topLeft = Offset(headCenter.x + headRadius * 0.45f, headCenter.y - headRadius * 0.55f), size = Size(headRadius * 0.55f, headRadius * 1.0f))
        }
        SpeciesCode.GATO -> {
            val p1 = androidx.compose.ui.graphics.Path().apply {
                moveTo(headCenter.x - headRadius * 0.9f, headCenter.y - headRadius * 0.3f)
                lineTo(headCenter.x - headRadius * 0.55f, headCenter.y - headRadius * 1.25f)
                lineTo(headCenter.x - headRadius * 0.15f, headCenter.y - headRadius * 0.5f)
                close()
            }
            val p2 = androidx.compose.ui.graphics.Path().apply {
                moveTo(headCenter.x + headRadius * 0.9f, headCenter.y - headRadius * 0.3f)
                lineTo(headCenter.x + headRadius * 0.55f, headCenter.y - headRadius * 1.25f)
                lineTo(headCenter.x + headRadius * 0.15f, headCenter.y - headRadius * 0.5f)
                close()
            }
            drawPath(p1, palette.body)
            drawPath(p2, palette.body)
        }
        SpeciesCode.CONEJO -> {
            drawOval(palette.body, topLeft = Offset(headCenter.x - headRadius * 0.75f, headCenter.y - headRadius * 2.1f), size = Size(headRadius * 0.5f, headRadius * 1.9f))
            drawOval(palette.body, topLeft = Offset(headCenter.x + headRadius * 0.25f, headCenter.y - headRadius * 2.1f), size = Size(headRadius * 0.5f, headRadius * 1.9f))
            drawOval(palette.accent, topLeft = Offset(headCenter.x - headRadius * 0.63f, headCenter.y - headRadius * 1.9f), size = Size(headRadius * 0.26f, headRadius * 1.5f))
            drawOval(palette.accent, topLeft = Offset(headCenter.x + headRadius * 0.37f, headCenter.y - headRadius * 1.9f), size = Size(headRadius * 0.26f, headRadius * 1.5f))
        }
        SpeciesCode.HAMSTER -> {
            drawCircle(palette.bodyDark, radius = headRadius * 0.35f, center = Offset(headCenter.x - headRadius * 0.75f, headCenter.y - headRadius * 0.65f))
            drawCircle(palette.bodyDark, radius = headRadius * 0.35f, center = Offset(headCenter.x + headRadius * 0.75f, headCenter.y - headRadius * 0.65f))
        }
        SpeciesCode.AVE -> {
            val wing = androidx.compose.ui.graphics.Path().apply {
                moveTo(headCenter.x - headRadius * 0.2f, headCenter.y + headRadius * 0.9f)
                quadraticBezierTo(headCenter.x - headRadius * 1.4f, headCenter.y + headRadius * 0.6f, headCenter.x - headRadius * 0.6f, headCenter.y + headRadius * 1.9f)
                close()
            }
            drawPath(wing, palette.bodyDark)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFeet(
    species: SpeciesCode,
    palette: SpeciesPalette,
    bodyCenter: Offset,
    bodyRadius: Float
) {
    when (species) {
        SpeciesCode.AVE -> {
            val legColor = Color(0xFFE8A23C)
            val legTopY = bodyCenter.y + bodyRadius * 0.55f
            val legBottomY = bodyCenter.y + bodyRadius * 0.82f
            drawLine(legColor, Offset(bodyCenter.x - bodyRadius * 0.16f, legTopY), Offset(bodyCenter.x - bodyRadius * 0.16f, legBottomY), strokeWidth = bodyRadius * 0.07f, cap = StrokeCap.Round)
            drawLine(legColor, Offset(bodyCenter.x + bodyRadius * 0.16f, legTopY), Offset(bodyCenter.x + bodyRadius * 0.16f, legBottomY), strokeWidth = bodyRadius * 0.07f, cap = StrokeCap.Round)
        }
        else -> {
            drawOval(palette.bodyDark, topLeft = Offset(bodyCenter.x - bodyRadius * 0.62f, bodyCenter.y + bodyRadius * 0.64f), size = Size(bodyRadius * 0.4f, bodyRadius * 0.26f))
            drawOval(palette.bodyDark, topLeft = Offset(bodyCenter.x + bodyRadius * 0.22f, bodyCenter.y + bodyRadius * 0.64f), size = Size(bodyRadius * 0.4f, bodyRadius * 0.26f))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSpeciesSnout(
    species: SpeciesCode,
    headCenter: Offset,
    headRadius: Float
) {
    when (species) {
        SpeciesCode.PERRO -> {
            drawCircle(Color(0xFF2E241D), radius = headRadius * 0.065f, center = Offset(headCenter.x, headCenter.y + headRadius * 0.28f))
        }
        SpeciesCode.GATO -> {
            val nose = androidx.compose.ui.graphics.Path().apply {
                moveTo(headCenter.x - headRadius * 0.06f, headCenter.y + headRadius * 0.22f)
                lineTo(headCenter.x + headRadius * 0.06f, headCenter.y + headRadius * 0.22f)
                lineTo(headCenter.x, headCenter.y + headRadius * 0.3f)
                close()
            }
            drawPath(nose, Color(0xFFEF9AAE))
        }
        SpeciesCode.CONEJO -> {
            drawCircle(Color(0xFFEF9AAE), radius = headRadius * 0.07f, center = Offset(headCenter.x, headCenter.y + headRadius * 0.26f))
        }
        SpeciesCode.HAMSTER -> {
            drawCircle(Color(0xFF2E241D), radius = headRadius * 0.06f, center = Offset(headCenter.x, headCenter.y + headRadius * 0.26f))
        }
        SpeciesCode.AVE -> {
            val beak = androidx.compose.ui.graphics.Path().apply {
                moveTo(headCenter.x - headRadius * 0.16f, headCenter.y + headRadius * 0.12f)
                lineTo(headCenter.x + headRadius * 0.16f, headCenter.y + headRadius * 0.12f)
                lineTo(headCenter.x, headCenter.y + headRadius * 0.38f)
                close()
            }
            drawPath(beak, Color(0xFFFFC93C))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBlinkingEye(
    center: Offset,
    radius: Float,
    blink: Float,
    color: Color
) {
    val eyeHeight = (radius * 2f * (1f - blink * 0.92f)).coerceAtLeast(radius * 0.25f)
    drawOval(
        color = color,
        topLeft = Offset(center.x - radius, center.y - eyeHeight / 2f),
        size = Size(radius * 2f, eyeHeight)
    )
    // Brillo del ojo: aporta mucha vida a la ilustración con muy poco costo
    drawCircle(
        color = Color.White.copy(alpha = (1f - blink).coerceIn(0f, 1f)),
        radius = radius * 0.32f,
        center = Offset(center.x - radius * 0.32f, center.y - eyeHeight * 0.22f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFace(
    mood: PetMood,
    palette: SpeciesPalette,
    headCenter: Offset,
    headRadius: Float,
    blink: Float
) {
    val eyeY = headCenter.y - headRadius * 0.05f
    val eyeDx = headRadius * 0.38f
    val eyeColor = Color(0xFF2E241D)

    when (mood) {
        PetMood.FELIZ -> {
            // ojos en forma de "^" felices
            drawArcEye(Offset(headCenter.x - eyeDx, eyeY), headRadius * 0.22f, eyeColor)
            drawArcEye(Offset(headCenter.x + eyeDx, eyeY), headRadius * 0.22f, eyeColor)
            drawArc(
                color = eyeColor, startAngle = 20f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(headCenter.x - headRadius * 0.28f, headCenter.y + headRadius * 0.05f),
                size = Size(headRadius * 0.56f, headRadius * 0.4f),
                style = Stroke(width = headRadius * 0.09f, cap = StrokeCap.Round)
            )
        }
        PetMood.NEUTRAL -> {
            drawBlinkingEye(Offset(headCenter.x - eyeDx, eyeY), headRadius * 0.09f, blink, eyeColor)
            drawBlinkingEye(Offset(headCenter.x + eyeDx, eyeY), headRadius * 0.09f, blink, eyeColor)
            drawLine(
                eyeColor,
                start = Offset(headCenter.x - headRadius * 0.18f, headCenter.y + headRadius * 0.32f),
                end = Offset(headCenter.x + headRadius * 0.18f, headCenter.y + headRadius * 0.32f),
                strokeWidth = headRadius * 0.07f,
                cap = StrokeCap.Round
            )
        }
        PetMood.CANSADO -> {
            drawLine(eyeColor, Offset(headCenter.x - eyeDx - headRadius * 0.12f, eyeY), Offset(headCenter.x - eyeDx + headRadius * 0.12f, eyeY), strokeWidth = headRadius * 0.08f, cap = StrokeCap.Round)
            drawLine(eyeColor, Offset(headCenter.x + eyeDx - headRadius * 0.12f, eyeY), Offset(headCenter.x + eyeDx + headRadius * 0.12f, eyeY), strokeWidth = headRadius * 0.08f, cap = StrokeCap.Round)
            drawArc(
                color = eyeColor, startAngle = 200f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(headCenter.x - headRadius * 0.28f, headCenter.y + headRadius * 0.15f),
                size = Size(headRadius * 0.56f, headRadius * 0.3f),
                style = Stroke(width = headRadius * 0.07f, cap = StrokeCap.Round)
            )
        }
        PetMood.HAMBRIENTO -> {
            drawBlinkingEye(Offset(headCenter.x - eyeDx, eyeY), headRadius * 0.1f, blink, eyeColor)
            drawBlinkingEye(Offset(headCenter.x + eyeDx, eyeY), headRadius * 0.1f, blink, eyeColor)
            drawOval(
                color = eyeColor,
                topLeft = Offset(headCenter.x - headRadius * 0.16f, headCenter.y + headRadius * 0.18f),
                size = Size(headRadius * 0.32f, headRadius * 0.34f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArcEye(center: Offset, r: Float, color: Color) {
    drawArc(
        color = color,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(center.x - r, center.y - r * 0.6f),
        size = Size(r * 2f, r * 1.2f),
        style = Stroke(width = r * 0.35f, cap = StrokeCap.Round)
    )
}
