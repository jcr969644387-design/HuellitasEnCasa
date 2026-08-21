package com.educalab.huellitasencasa.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.educalab.huellitasencasa.ui.theme.HuellitasLeaf
import com.educalab.huellitasencasa.ui.theme.HuellitasTeal

/** Registro compartido de las zonas de destino (drop targets) por identificador. */
class DropTargetRegistry {
    private val zones = mutableMapOf<String, Rect>()
    fun register(id: String, rect: Rect) { zones[id] = rect }
    fun hitTest(point: Offset): String? = zones.entries.firstOrNull { it.value.contains(point) }?.key
}

@Composable
fun rememberDropTargetRegistry(): DropTargetRegistry = remember { DropTargetRegistry() }

/** Marca un composable como zona donde se pueden soltar tarjetas arrastrables. */
fun Modifier.dropTarget(id: String, registry: DropTargetRegistry): Modifier = this.onGloballyPositioned { coords ->
    val pos = coords.positionInRoot()
    registry.register(id, Rect(pos, coords.size.toSize()))
}

private fun androidx.compose.ui.unit.IntSize.toSize() = Size(width.toFloat(), height.toFloat())

/**
 * Tarjeta arrastrable real: sigue el dedo mientras se arrastra y, al soltar, informa a
 * [onDropped] con el id de la zona de destino (o null si se soltó fuera de cualquier zona).
 *
 * La posición de anclaje ([anchor]) solo se actualiza cuando la tarjeta NO se está arrastrando:
 * así se evita que el propio desplazamiento visual del arrastre (el modificador `offset`)
 * contamine la posición base y se calculen puntos de soltado incorrectos. El punto usado para
 * la detección de colisión es el CENTRO de la tarjeta (no su esquina superior izquierda), para
 * que soltar sobre una zona se sienta natural aunque no quede perfectamente alineada.
 */
@Composable
fun DraggableCard(
    registry: DropTargetRegistry,
    onDropped: (targetId: String?) -> Unit,
    modifier: Modifier = Modifier,
    onDragging: (currentCenter: Offset) -> Unit = {},
    content: @Composable (isDragging: Boolean) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }
    var anchor by remember { mutableStateOf(Offset.Zero) }
    var cardSize by remember { mutableStateOf(Size.Zero) }

    fun currentCenter(): Offset = Offset(
        anchor.x + offset.x + cardSize.width / 2f,
        anchor.y + offset.y + cardSize.height / 2f
    )

    Box(
        modifier = modifier
            .onGloballyPositioned { coords ->
                if (!dragging) {
                    anchor = coords.positionInRoot()
                    cardSize = coords.size.toSize()
                }
            }
            .zIndex(if (dragging) 10f else 0f)
            .let {
                if (dragging) it.offset { IntOffset(offset.x.toInt(), offset.y.toInt()) } else it
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd = {
                        val target = registry.hitTest(currentCenter())
                        dragging = false
                        offset = Offset.Zero
                        onDropped(target)
                    },
                    onDragCancel = {
                        dragging = false
                        offset = Offset.Zero
                        onDropped(null)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offset += dragAmount
                    onDragging(currentCenter())
                }
            }
    ) {
        content(dragging)
    }
}

/** Estado visual de una zona de destino, para dar información clara mas alla del color. */
enum class DropZoneState { VACIA, RESALTADA, LLENA }

@Composable
fun DropZoneBox(
    id: String,
    registry: DropTargetRegistry,
    state: DropZoneState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val borderWidth by animateDpAsState(if (state == DropZoneState.RESALTADA) 3.dp else 1.dp, label = "zone_border")
    val (background, border) = when (state) {
        DropZoneState.VACIA -> Color(0x11000000) to Color(0x22000000)
        DropZoneState.RESALTADA -> HuellitasTeal.copy(alpha = 0.22f) to HuellitasTeal
        DropZoneState.LLENA -> HuellitasLeaf.copy(alpha = 0.18f) to HuellitasLeaf
    }
    Box(
        modifier = modifier
            .dropTarget(id, registry)
            .background(background, RoundedCornerShape(16.dp))
            .border(borderWidth, border, RoundedCornerShape(16.dp))
    ) {
        content()
    }
}
