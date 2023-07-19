package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene

class DndController(uiScene: Scene) {

    val dndContext = DragAndDropContext<EditorDndItem>()

    private val surfaceHandlers = mutableMapOf<UiSurface, MutableSet<DragAndDropHandler<EditorDndItem>>>()

    init {
        uiScene.onRenderScene += {
            dndContext.clearHandlers()
            surfaceHandlers.values.forEach {
                dndContext.handlers += it
            }
        }
    }

    fun registerHandler(dndHandler: DragAndDropHandler<EditorDndItem>, surface: UiSurface) {
        surfaceHandlers.getOrPut(surface) {
            val surfaceHandlers = mutableSetOf<DragAndDropHandler<EditorDndItem>>()
            surface.onCompose { surfaceHandlers.clear() }
            surfaceHandlers
        }.add(dndHandler)
    }
}

class EditorDndItem(val item: Any)

open class DndHandler(override var dropTarget: UiNode) : DragAndDropHandler<EditorDndItem> {

    val isHovered = mutableStateOf(false)

    override fun onDragStart(dragItem: EditorDndItem, dragPointer: PointerEvent, source: DragAndDropHandler<EditorDndItem>?) { }

    override fun onDrag(
        dragItem: EditorDndItem,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<EditorDndItem>?,
        isHovered: Boolean
    ) {
        this.isHovered.set(isHovered)
    }

    override fun onDragEnd(
        dragItem: EditorDndItem,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<EditorDndItem>?,
        target: DragAndDropHandler<EditorDndItem>?,
        success: Boolean
    ) {
        isHovered.set(false)
    }

    override fun receive(dragItem: EditorDndItem, dragPointer: PointerEvent, source: DragAndDropHandler<EditorDndItem>?): Boolean {
        return false
    }
}
