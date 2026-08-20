package com.educalab.huellitasencasa.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.educalab.huellitasencasa.ui.theme.InkText
import kotlin.math.sin

/** Personaje infantil de veterinario/a: se usa como avatar seleccionable del perfil. */
data class VetAvatarStyle(
    val isGirl: Boolean,
    val scrubColor: Color,
    val hairColor: Color,
    val skinTone: Color
)

/** Ocho variantes (niño y niña veterinarios) para que la persona usuaria elija su avatar. */
val VetAvatarPresets = listOf(
    VetAvatarStyle(isGirl = false, scrubColor = Color(0xFF4FB0A5), hairColor = Color(0xFF3E2723), skinTone = Color(0xFFF1C27D)),
    VetAvatarStyle(isGirl = false, scrubColor = Color(0xFF5AC8FA), hairColor = Color(0xFF1B1B1B), skinTone = Color(0xFFE0AC69)),
    VetAvatarStyle(isGirl = false, scrubColor = Color(0xFFFF8A5B), hairColor = Color(0xFF8D5B3A), skinTone = Color(0xFFFFDBAC)),
    VetAvatarStyle(isGirl = false, scrubColor = Color(0xFF6FCF97), hairColor = Color(0xFFB5651D), skinTone = Color(0xFFC68642)),
    VetAvatarStyle(isGirl = true, scrubColor = Color(0xFFFF6B6B), hairColor = Color(0xFF1B1B1B), skinTone = Color(0xFFFFDBAC)),
    VetAvatarStyle(isGirl = true, scrubColor = Color(0xFF8E7CC3), hairColor = Color(0xFF8D5B3A), skinTone = Color(0xFFF1C27D)),
    VetAvatarStyle(isGirl = true, scrubColor = Color(0xFFFFC93C), hairColor = Color(0xFFF4C542), skinTone = Color(0xFFFFE0BD)),
    VetAvatarStyle(isGirl = true, scrubColor = Color(0xFF4FB0A5), hairColor = Color(0xFFB33F3F), skinTone = Color(0xFFC68642))
)

/**
 * Dibuja un/a veterinario/a infantil estilizado con Compose Canvas (bata, fonendoscopio y
 * un ligero balanceo continuo), pensado para usarse como avatar de perfil.
 */
@Composable
fun VetAvatarIllustration(style: VetAvatarStyle, modifier: Modifier = Modifier, size: Dp = 64.dp) {
    val transition = rememberInfiniteTransition(label = "vet_bob")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "bob"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val dy = sin(bob * Math.PI).toFloat() * h * 0.02f

        val headCenter = Offset(w * 0.5f, h * 0.38f + dy)
        val headRadius = w * 0.23f

        drawTorso(style, w, h, dy)
        if (style.isGirl) drawPigtails(style, headCenter, headRadius)
        drawCircle(color = style.skinTone, radius = headRadius * 0.24f, center = Offset(headCenter.x - headRadius * 0.95f, headCenter.y + headRadius * 0.05f))
        drawCircle(color = style.skinTone, radius = headRadius * 0.24f, center = Offset(headCenter.x + headRadius * 0.95f, headCenter.y + headRadius * 0.05f))
        drawCircle(color = style.skinTone, radius = headRadius, center = headCenter)
        drawHairCap(style, headCenter, headRadius)
        drawFace(headCenter, headRadius)
        drawStethoscope(w, h, dy)
    }
}

private fun DrawScope.drawTorso(style: VetAvatarStyle, w: Float, h: Float, dy: Float) {
    val top = h * 0.60f + dy
    drawRoundRect(
        color = style.scrubColor,
        topLeft = Offset(w * 0.19f, top),
        size = Size(w * 0.62f, h * 0.40f),
        cornerRadius = CornerRadius(w * 0.16f, w * 0.16f)
    )
    drawOval(
        color = Color.White,
        topLeft = Offset(w * 0.42f, top - h * 0.015f),
        size = Size(w * 0.16f, h * 0.08f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.9f),
        topLeft = Offset(w * 0.42f, h * 0.80f),
        size = Size(w * 0.16f, h * 0.12f),
        cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
    )
}

private fun DrawScope.drawPigtails(style: VetAvatarStyle, headCenter: Offset, headRadius: Float) {
    drawCircle(color = style.hairColor, radius = headRadius * 0.4f, center = Offset(headCenter.x - headRadius * 1.15f, headCenter.y + headRadius * 0.3f))
    drawCircle(color = style.hairColor, radius = headRadius * 0.4f, center = Offset(headCenter.x + headRadius * 1.15f, headCenter.y + headRadius * 0.3f))
}

private fun DrawScope.drawHairCap(style: VetAvatarStyle, headCenter: Offset, headRadius: Float) {
    drawArc(
        color = style.hairColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(headCenter.x - headRadius * 1.08f, headCenter.y - headRadius * 1.08f),
        size = Size(headRadius * 2.16f, headRadius * 2.16f)
    )
}

private fun DrawScope.drawFace(headCenter: Offset, headRadius: Float) {
    val eyeY = headCenter.y
    val eyeDx = headRadius * 0.36f
    drawCircle(color = InkText, radius = headRadius * 0.09f, center = Offset(headCenter.x - eyeDx, eyeY))
    drawCircle(color = InkText, radius = headRadius * 0.09f, center = Offset(headCenter.x + eyeDx, eyeY))
    drawArc(
        color = InkText,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(headCenter.x - headRadius * 0.32f, headCenter.y + headRadius * 0.08f),
        size = Size(headRadius * 0.64f, headRadius * 0.42f),
        style = Stroke(width = headRadius * 0.09f, cap = StrokeCap.Round)
    )
    val blush = Color(0xFFFFAFAF).copy(alpha = 0.5f)
    drawCircle(color = blush, radius = headRadius * 0.14f, center = Offset(headCenter.x - headRadius * 0.62f, headCenter.y + headRadius * 0.28f))
    drawCircle(color = blush, radius = headRadius * 0.14f, center = Offset(headCenter.x + headRadius * 0.62f, headCenter.y + headRadius * 0.28f))
}

private fun DrawScope.drawStethoscope(w: Float, h: Float, dy: Float) {
    val tube = Color(0xFFB8C4CC)
    val neckY = h * 0.60f + dy
    drawArc(
        color = tube,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(w * 0.34f, neckY - h * 0.03f),
        size = Size(w * 0.32f, h * 0.10f),
        style = Stroke(width = w * 0.025f, cap = StrokeCap.Round)
    )
    drawLine(
        color = tube,
        start = Offset(w * 0.5f, neckY + h * 0.06f),
        end = Offset(w * 0.5f, h * 0.78f),
        strokeWidth = w * 0.025f,
        cap = StrokeCap.Round
    )
    drawCircle(color = Color(0xFF8FA0AA), radius = w * 0.045f, center = Offset(w * 0.5f, h * 0.80f))
}
