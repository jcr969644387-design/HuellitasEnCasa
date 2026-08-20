package com.educalab.huellitasencasa.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.educalab.huellitasencasa.R
import com.educalab.huellitasencasa.domain.logic.ProgressStateResolver
import com.educalab.huellitasencasa.util.DrawableCatalog

/** Barra de indicador (alimentación, higiene...) con icono, etiqueta y valor: nunca solo color. */
@Composable
fun IndicatorBar(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animated by animateFloatAsState(targetValue = value / 100f, animationSpec = tween(500), label = "indicator")
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text("$value%", style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.18f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated.coerceIn(0f, 1f))
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
            )
        }
    }
}

/** Icono + etiqueta de estado (bloqueado/disponible/iniciado/completado/dominado): nunca solo color. */
@Composable
fun ProgressStateChip(state: ProgressStateResolver.State, modifier: Modifier = Modifier) {
    val (iconName, label) = when (state) {
        ProgressStateResolver.State.BLOQUEADO -> "estado_bloqueado" to "Bloqueado"
        ProgressStateResolver.State.DISPONIBLE -> "estado_disponible" to "Disponible"
        ProgressStateResolver.State.INICIADO -> "estado_iniciado" to "Iniciado"
        ProgressStateResolver.State.COMPLETADO -> "estado_completado" to "Completado"
        ProgressStateResolver.State.DOMINADO -> "estado_dominado" to "¡Dominado!"
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(DrawableCatalog.resolve(iconName)),
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = Color.Unspecified
        )
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

/** Tarjeta ilustrada de un módulo del centro de experiencia (Home hub). */
@Composable
fun ModuleCard(
    title: String,
    subtitle: String,
    iconRes: String,
    accentColor: Color,
    state: ProgressStateResolver.State,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.95f)
            .clickable(enabled = state != ProgressStateResolver.State.BLOQUEADO, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(DrawableCatalog.resolve(iconRes)),
                    contentDescription = title,
                    modifier = Modifier.size(34.dp),
                    tint = Color.Unspecified
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f, fill = false))
            Spacer(Modifier.height(8.dp))
            ProgressStateChip(state)
        }
    }
}

/** Fila de estrellas para mostrar una puntuación breve (0-3), con icono, nunca solo texto. */
@Composable
fun StarsRow(filled: Int, total: Int = 3, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(total) { index ->
            Icon(
                imageVector = if (index < filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

/** Burbuja de texto breve del personaje guía / consejo educativo. */
@Composable
fun TipBubble(text: String, modifier: Modifier = Modifier, accentColor: Color = MaterialTheme.colorScheme.secondary) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.motif_paw_single),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = accentColor
            )
        }
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Insignia coleccionable en forma de medallón, en gris si aún no se ha desbloqueado. */
@Composable
fun BadgeMedallion(name: String, iconRes: String, unlocked: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(84.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (unlocked) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(DrawableCatalog.resolve(iconRes)),
                contentDescription = name,
                modifier = Modifier.size(64.dp),
                tint = if (unlocked) Color.Unspecified else MaterialTheme.colorScheme.outline
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            name,
            style = MaterialTheme.typography.labelMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier.padding(vertical = 6.dp)
    )
}

@Composable
fun AppLogo(modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 96.dp) {
    Icon(
        painter = painterResource(R.drawable.logo_huellitas),
        contentDescription = stringResource(R.string.app_name),
        modifier = modifier.size(size),
        tint = Color.Unspecified
    )
}
