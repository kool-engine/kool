package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.math.randomF
import de.fabmax.kool.math.randomI
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Color.Hsv
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

sealed class DragAndDropWindow(name: String, uiDemo: UiDemo) : DemoWindow(name, uiDemo) {

    private val items = mutableStateListOf<DndItem>()
    private val dndHandler = DndHandler()

    init {
        windowDockable.setFloatingBounds(width = Dp(500f), height = Dp(700f))
        for (i in 0..9) {
            items += DndItem.random()
        }
    }

    override fun onClose() {
        super.onClose()
        uiDemo.dndContext.removeHandler(dndHandler)
    }

    override fun UiScope.windowContent() = Column(Grow.Std, Grow.Std) {
        Text("Draggable items:") {
            modifier
                .margin(sizes.gap)
        }

        var areaWidth by remember(0f)
        val itemW = sizes.largeGap * 3f
        val itemH = sizes.largeGap * 3f

        ScrollArea(containerModifier = {
            it
                .margin(sizes.gap)
                .onPositioned { nd ->
                    dndHandler.targetNode = nd
                    uiDemo.dndContext.registerHandler(dndHandler)
                    areaWidth = nd.widthPx
                }
            if (dndHandler.isDragHover.use()) {
                it.border(RectBorder(colors.secondary, 2.dp))
            }
        }) {
            if (areaWidth > 0f) {
                val cols = max(1, floor((areaWidth - sizes.gap.px) / (itemW.px + sizes.gap.px)).toInt())
                items.use()

                Column {
                    for (i in items.indices step cols) {
                        Row {
                            for (j in i until min(i + cols, items.size)) {
                                DndItem(items[j], itemW, itemH, sizes.gap)
                            }
                        }
                    }
                }
            }
        }

        if (dndHandler.isDragInProgress.use()) {
            val dragItem = dndHandler.dragItem!!
            val posX = dndHandler.dragPosX.use() - itemW.px * 0.5f
            val posY = dndHandler.dragPosY.use() - itemH.px * 0.5f
            Popup(posX, posY) {
                // set popup to non-blocking, so that is does not capture the pointer, and we are able to hover
                // other UiSurfaces
                modifier.isBlocking(false)

                // draw the drag item
                DndItem(dragItem, itemW, itemH, Dp.ZERO)
            }
        }
    }

    private fun UiScope.DndItem(item: DndItem, itemW: Dimension, itemH: Dimension, margin: Dp) {
        Box {
            var isHovered by remember(false)
            modifier
                .size(itemW, itemH)
                .margin(margin)
                .backgroundColor(item.color)
                .onEnter { isHovered = true }
                .onExit { isHovered = false }
                .installDragAndDropHandler(uiDemo.dndContext, dndHandler) { item }

            if (isHovered) {
                modifier.border(RectBorder(colors.primary, 2.dp))
            }

            Text(item.label) {
                modifier
                    .font(sizes.largeText)
                    .size(Grow.Std, Grow.Std)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
            }
        }
    }

    private inner class DndHandler : DragAndDropHandler<DndItem> {
        val isDragHover = mutableStateOf(false)
        val isDragInProgress = mutableStateOf(false)
        val dragPosX = mutableStateOf(0f)
        val dragPosY = mutableStateOf(0f)
        var dragItem: DndItem? = null

        lateinit var targetNode: UiNode
        override val dropTarget: UiNode get() = targetNode

        override fun receive(dragItem: DndItem, dragPointer: PointerEvent, source: DragAndDropHandler<DndItem>?): Boolean {
            // Remove item first. This way the item is moved from its initial position to the end of the list in case
            // the item is dropped over the source list
            items.remove(dragItem)
            // Add item to the end of the item list
            items.add(dragItem)
            // Return true to indicate a successful item reception
            return true
        }

        override fun onDragStart(dragItem: DndItem, dragPointer: PointerEvent, source: DragAndDropHandler<DndItem>?) {
            if (source === this) {
                isDragInProgress.set(true)
                dragPosX.set(dragPointer.screenPosition.x)
                dragPosY.set(dragPointer.screenPosition.y)
                this.dragItem = dragItem
            }
        }

        override fun onDrag(
            dragItem: DndItem,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<DndItem>?,
            isHovered: Boolean
        ) {
            isDragHover.set(isHovered)
            dragPosX.set(dragPointer.screenPosition.x)
            dragPosY.set(dragPointer.screenPosition.y)
        }

        override fun onDragEnd(
            dragItem: DndItem,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<DndItem>?,
            target: DragAndDropHandler<DndItem>?,
            success: Boolean
        ) {
            // Remove item from the item list if drag and drop was successful and item was dropped over a different target
            if (success && source == this && target != this) {
                items.remove(dragItem)
            }
            isDragHover.set(false)
            isDragInProgress.set(false)
            this.dragItem = null
        }
    }

    class A(uiDemo: UiDemo) : DragAndDropWindow("Drag and Drop A", uiDemo)
    class B(uiDemo: UiDemo) : DragAndDropWindow("Drag and Drop B", uiDemo)

    class DndItem(val label: String, val color: Color) {
        companion object {
            fun random(): DndItem {
                val label = "${('A'.code + randomI(0..25)).toChar()}${('a'.code + randomI(0..25)).toChar()}"
                val color = Hsv(randomF(0f, 360f), randomF(0.5f, 1f), randomF(0.5f, 1f)).toSrgb()
                return DndItem(label, color)
            }
        }
    }
}
