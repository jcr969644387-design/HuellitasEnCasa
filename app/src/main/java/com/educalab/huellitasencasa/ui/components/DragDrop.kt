package com.educalab.huellitasencasa.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

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

private fun androidx.compose.ui.unit.IntSize.toSize() = androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat())

/**
 * Tarjeta arrastrable real: sigue el dedo mientras se arrastra y, al soltar, informa a
 * [onDropped] con el id de la zona de destino (o null si se soltó fuera de cualquier zona).
 */
@Composable
fun DraggableCard(
    registry: DropTargetRegistry,
    onDropped: (targetId: String?) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (isDragging: Boolean) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }
    var rootPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned { rootPosition = it.positionInRoot() }
            .zIndex(if (dragging) 10f else 0f)
            .let {
                if (dragging) it.offset { IntOffset(offset.x.toInt(), offset.y.toInt()) } else it
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd = {
                        dragging = false
                        val releasePoint = Offset(rootPosition.x + offset.x, rootPosition.y + offset.y)
                        val target = registry.hitTest(releasePoint)
                        offset = Offset.Zero
                        onDropped(target)
                    },
                    onDragCancel = { dragging = false; offset = Offset.Zero }
                ) { change, dragAmount ->
                    change.consume()
                    offset += dragAmount
                }
            }
    ) {
        content(dragging)
    }
}

@Composable
fun DropZoneBox(
    id: String,
    registry: DropTargetRegistry,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .dropTarget(id, registry)
            .background(
                if (isHighlighted) Color(0x334FB0A5) else Color(0x11000000),
                RoundedCornerShape(16.dp)
            )
    ) {
        content()
    }
}
