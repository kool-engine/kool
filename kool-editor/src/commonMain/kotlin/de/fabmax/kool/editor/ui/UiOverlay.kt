package de.fabmax.kool.editor.ui

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor

class UiOverlay(val ui: EditorUi) : UiSurface() {

    private val dndOverlay = DndOverlay()

    init {
        inputMode = InputCaptureMode.CaptureDisabled
        content = { overlayContent() }
    }

    private fun UiScope.overlayContent() {
        ui.dndController.registerHandler(dndOverlay, this@UiOverlay)

        dndOverlay()
    }

    private inner class DndOverlay : DragAndDropHandler<EditorDndItem<*>>, Composable {
        override val dropTarget: UiNode get() = viewport

        val dragItem = mutableStateOf<EditorDndItem<*>?>(null)
        val dragPos = mutableStateOf(Vec2f.ZERO)

        override fun onDragStart(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) {
            this.dragItem.set(dragItem)
            this.dragPos.set(dragPointer.screenPosition)
        }

        override fun onDragEnd(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?,
            target: DragAndDropHandler<EditorDndItem<*>>?,
            success: Boolean
        ) {
            this.dragItem.set(null)
        }

        override fun onDrag(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?,
            isHovered: Boolean
        ) {
            this.dragPos.set(dragPointer.screenPosition)
        }

        override fun receive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ): Boolean = false

        override fun UiScope.compose() {
            val item = dragItem.use() ?: return
            val pos = dragPos.use()

            Box {
                modifier.margin(start = Dp.fromPx(pos.x) + sizes.smallGap, top = Dp.fromPx(pos.y) + sizes.smallGap)
                val previewItem = item.preview
                if (previewItem != null) {
                    previewItem()
                } else {
                    modifier
                        .size(sizes.baseSize * 2, sizes.baseSize * 2)
                        .background(RoundRectBackground(MdColor.LIGHT_BLUE.withAlpha(0.5f), sizes.gap))
                }
            }

        }

    }
}