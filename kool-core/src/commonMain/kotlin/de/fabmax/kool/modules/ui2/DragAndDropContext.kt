package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.input.PointerInput

class DragAndDropContext<T: Any> {

    val handlers = mutableSetOf<DragAndDropHandler<T>>()

    private var sourceHandler: DragAndDropHandler<T>? = null
    private var dragItem: T? = null

    val isDrag: Boolean get() = dragItem != null

    fun registerHandler(handler: DragAndDropHandler<T>) {
        handlers += handler
    }

    fun removeHandler(handler: DragAndDropHandler<T>) {
        handlers -= handler
    }

    fun clearHandlers() {
        handlers.clear()
    }

    fun startDrag(dragItem: T, dragPointerEvent: PointerEvent, sourceHandler: DragAndDropHandler<T>?) {
        this.dragItem = dragItem
        this.sourceHandler = sourceHandler
        handlers.forEach { it.onDragStart(dragItem, dragPointerEvent, sourceHandler) }
    }

    fun drag(dragPointerEvent: PointerEvent) {
        val item = dragItem ?: return

        handlers.forEach { handler ->
            val target = handler.dropTarget
            val isHovered = target != null
                    && target.isInBounds(dragPointerEvent.screenPosition)
                    && target.surface.isOnTop(dragPointerEvent.screenPosition)

            handler.onDrag(item, dragPointerEvent, sourceHandler, isHovered)
        }
    }

    fun endDrag(dragPointerEvent: PointerEvent) {
        val item = dragItem ?: return
        dragItem = null

        var success = false
        var targetHandler: DragAndDropHandler<T>? = null
        for (handler in handlers) {
            val target = handler.dropTarget
            val isHovered = target != null
                    && target.isInBounds(dragPointerEvent.screenPosition)
                    && target.surface.isOnTop(dragPointerEvent.screenPosition)
            if (isHovered) {
                success = handler.receive(item, dragPointerEvent, sourceHandler)
            }
            if (success) {
                targetHandler = handler
                break
            }
        }
        handlers.forEach { it.onDragEnd(item, dragPointerEvent, sourceHandler, targetHandler, success) }
    }

    fun cancelDrag() {
        val item = dragItem ?: return
        dragItem = null
        val pointerEvent = PointerEvent(PointerInput.primaryPointer, KoolSystem.requireContext())
        handlers.forEach { it.onDragEnd(item, pointerEvent, sourceHandler, null, false) }
    }
}

interface DragAndDropHandler<T: Any> {
    val dropTarget: UiNode?

    fun receive(dragItem: T, dragPointer: PointerEvent, source: DragAndDropHandler<T>?): Boolean

    fun onDragStart(dragItem: T, dragPointer: PointerEvent, source: DragAndDropHandler<T>?) { }
    fun onDrag(dragItem: T, dragPointer: PointerEvent, source: DragAndDropHandler<T>?, isHovered: Boolean) { }
    fun onDragEnd(dragItem: T, dragPointer: PointerEvent, source: DragAndDropHandler<T>?, target: DragAndDropHandler<T>?, success: Boolean) { }
}

fun <M: UiModifier, T: Any> M.installDragAndDropHandler(
    dndContext: DragAndDropContext<T>,
    handler: DragAndDropHandler<T>?,
    dragItem: () -> T?
): M {
    onDragStart { dragItem()?.let { item -> dndContext.startDrag(item, it, handler) } }
    onDrag { dndContext.drag(it) }
    onDragEnd { dndContext.endDrag(it) }
    return this
}