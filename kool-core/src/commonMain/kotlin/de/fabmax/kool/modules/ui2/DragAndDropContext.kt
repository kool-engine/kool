package de.fabmax.kool.modules.ui2

class DragAndDropContext<T: Any> {

    private val handlers = mutableSetOf<DragAndDropHandler<T>>()

    private var sourceHandler: DragAndDropHandler<T>? = null
    private var dragItem: T? = null

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

        val hovering = handlers.find { handler ->
            val target = handler.dropTarget
            target != null
                    && target.isInBounds(dragPointerEvent.screenPosition)
                    && target.surface.isOnTop(dragPointerEvent.screenPosition)
        }
        handlers.forEach { it.onDrag(item, dragPointerEvent, sourceHandler, hovering) }
    }

    fun endDrag(dragPointerEvent: PointerEvent) {
        val item = dragItem ?: return

        val targetHandler = handlers.find { handler ->
            val target = handler.dropTarget
            target != null
                    && target.isInBounds(dragPointerEvent.screenPosition)
                    && target.surface.isOnTop(dragPointerEvent.screenPosition)
        }
        val success = targetHandler?.receive(item, sourceHandler) ?: false
        handlers.forEach { it.onDragEnd(item, dragPointerEvent, sourceHandler, targetHandler, success) }
    }
}

interface DragAndDropHandler<T: Any> {
    val dropTarget: UiNode?

    fun receive(dragItem: T, source: DragAndDropHandler<T>?): Boolean

    fun onDragStart(dragItem: T, dragPointer: PointerEvent, source: DragAndDropHandler<T>?) { }
    fun onDrag(dragItem: T, dragPointer: PointerEvent, source: DragAndDropHandler<T>?, hovering: DragAndDropHandler<T>?) { }
    fun onDragEnd(dragItem: T, dragPointer: PointerEvent, source: DragAndDropHandler<T>?, target: DragAndDropHandler<T>?, success: Boolean) { }
}

fun <M: UiModifier, T: Any> M.installDragAndDropHandler(
    dndContext: DragAndDropContext<T>,
    handler: DragAndDropHandler<T>,
    dragItem: T
): M {
    onDragStart { dndContext.startDrag(dragItem, it, handler) }
    onDrag { dndContext.drag(it) }
    onDragEnd { dndContext.endDrag(it) }
    return this
}