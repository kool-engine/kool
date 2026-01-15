package de.fabmax.kool.modules.compose.modifiers

import de.fabmax.kool.modules.ui2.*
import me.dvyy.compose.mini.modifier.Modifier

fun Modifier.dragListener(draggable: Draggable) = edit<UiModifier> {
    it.dragListener(draggable)
}

inline fun Modifier.onDragStart(crossinline block: (ev: PointerEvent) -> Unit) = edit<UiModifier> {
    it.onDragStart { block(it) }
}

inline fun Modifier.onDrag(crossinline block: (ev: PointerEvent) -> Unit) = edit<UiModifier> {
    it.onDrag { block(it) }
}

inline fun Modifier.onDragEnd(crossinline block: (ev: PointerEvent) -> Unit) = edit<UiModifier> {
    it.onDragEnd { block(it) }
}
