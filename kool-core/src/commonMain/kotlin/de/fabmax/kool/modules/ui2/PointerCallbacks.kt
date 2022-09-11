package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext

class PointerCallbacks {
    var onRawPointer: ((PointerEvent) -> Unit)? = null
    var onClick: ((PointerEvent) -> Unit)? = null
    var onWheelX: ((PointerEvent) -> Unit)? = null
    var onWheelY: ((PointerEvent) -> Unit)? = null

    var onEnter: ((PointerEvent) -> Unit)? = null
    var onExit: ((PointerEvent) -> Unit)? = null
    var onHover: ((PointerEvent) -> Unit)? = null

    var onDragStart: ((PointerEvent) -> Unit)? = null
    var onDrag: ((PointerEvent) -> Unit)? = null
    var onDragEnd: ((PointerEvent) -> Unit)? = null

    val hasAnyCallback: Boolean
        get() = onRawPointer != null ||
                onClick != null ||
                onWheelX != null ||
                onWheelY != null ||
                onEnter != null ||
                onExit != null ||
                onHover != null ||
                onDragStart != null ||
                onDrag != null ||
                onDragEnd != null

    val hasAnyHoverCallback: Boolean
        get() = onEnter != null ||
                onExit != null ||
                onHover != null

    val hasAnyDragCallback: Boolean
        get() = onDragStart != null ||
                onDrag != null ||
                onDragEnd != null

    fun resetDefaults() {
        onRawPointer = null
        onClick = null
        onWheelX = null
        onWheelY = null
        onEnter = null
        onExit = null
        onHover = null
        onDragStart = null
        onDrag = null
        onDragEnd = null
    }
}

class PointerEvent(val pointer: InputManager.Pointer, val ctx: KoolContext) {
    var isConsumed = true

    fun reject() {
        isConsumed = false
    }
}

fun <T: UiModifier> T.onRawPointer(block: (PointerEvent) -> Unit): T { pointerCallbacks.onRawPointer = block; return this }
fun <T: UiModifier> T.onClick(block: (PointerEvent) -> Unit): T { pointerCallbacks.onClick = block; return this }
fun <T: UiModifier> T.onWheelX(block: (PointerEvent) -> Unit): T { pointerCallbacks.onWheelX = block; return this }
fun <T: UiModifier> T.onWheelY(block: (PointerEvent) -> Unit): T { pointerCallbacks.onWheelY = block; return this }

fun <T: UiModifier> T.onEnter(block: (PointerEvent) -> Unit): T { pointerCallbacks.onEnter = block; return this }
fun <T: UiModifier> T.onExit(block: (PointerEvent) -> Unit): T { pointerCallbacks.onExit = block; return this }
fun <T: UiModifier> T.onHover(block: (PointerEvent) -> Unit): T { pointerCallbacks.onHover = block; return this }

fun <T: UiModifier> T.onDragStart(block: (PointerEvent) -> Unit): T { pointerCallbacks.onDragStart = block; return this }
fun <T: UiModifier> T.onDrag(block: (PointerEvent) -> Unit): T { pointerCallbacks.onDrag = block; return this }
fun <T: UiModifier> T.onDragEnd(block: (PointerEvent) -> Unit): T { pointerCallbacks.onDragEnd = block; return this }
