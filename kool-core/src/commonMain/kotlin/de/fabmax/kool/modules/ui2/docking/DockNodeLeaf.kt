package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.logE

class DockNodeLeaf(
    dock: Dock,
    parent: DockNodeInter?,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std
) : DockNode(dock, parent, width, height) {

    val dockedItems = mutableStateListOf<Dockable>()
    override val isEmpty: Boolean
        get() = dockedItems.isEmpty()

    private var topItem: Dockable? = null
    val dockItemOnTop: Dockable?
        get() = topItem ?: dockedItems.firstOrNull()

    override val nodeName: String
        get() = "$index:leaf"

    override val slots: DockSlots = LeafSlots(this)

    override fun insertItem(dragItem: Dockable, position: SlotPosition) {
        if (position == SlotPosition.Center) {
            dock(dragItem)
        } else {
            insertChildNodeIntoParent(position, dragItem.preferredWidth, dragItem.preferredHeight).dock(dragItem)
        }
    }

    fun dock(dockable: Dockable, index: Int = -1, bringToTop: Boolean = true, usePreferredSize: Boolean = dockedItems.isEmpty()) {
        if (usePreferredSize) {
            val p = parent
            val prefH = dockable.preferredHeight
            val prefW = dockable.preferredWidth
            when {
                p is DockNodeColumn && p.childNodes.size > 1 && prefH != null -> height.set(prefH)
                p is DockNodeRow && p.childNodes.size > 1 && prefW != null -> width.set(prefW)
            }
            parent?.checkChildNodesForGrow()
        }

        if (index in dockedItems.indices) {
            dockedItems.add(index, dockable)
        } else {
            dockedItems.add(dockable)
        }
        dockable.dockedTo.set(this)

        if (bringToTop) {
            bringToTop(dockable)
        }
    }

    fun bringToTop(dockable: Dockable) {
        if (dockable !in dockedItems) {
            logE { "bringToTop() called with an Dockable not docked to this node" }
        } else {
            topItem = dockable
        }
    }

    fun isOnTop(dockable: Dockable): Boolean {
        return dockable == dockItemOnTop
    }

    fun undock(dockable: Dockable, removeIfEmpty: Boolean = true) {
        if (topItem == dockable) {
            topItem = null
        }
        if (dockedItems.remove(dockable) && dockable.dockedTo.value == this) {
            dockable.dockedTo.set(null)
        }
        if (removeIfEmpty && isEmpty) {
            parent?.removeChildNode(this)
        }
    }

    override fun getLeafNodeAt(screenPosPx: Vec2f): DockNodeLeaf? {
        if (isInBounds(screenPosPx)) {
            return this
        }
        return null
    }

    override fun UiScope.composeNodeContent() { }

    private class LeafSlots(val dockNode: DockNodeLeaf) : DockSlots() {
        val left = SlotBox(SlotPosition.Left)
        val top = SlotBox(SlotPosition.Top)
        val center = SlotBox(SlotPosition.Center)
        val bottom = SlotBox(SlotPosition.Bottom)
        val right = SlotBox(SlotPosition.Right)
        override val boxes = listOf(left, top, center, bottom, right)

        override fun UiScope.compose() = Row {
            modifier.align(AlignmentX.Center, AlignmentY.Center)
            Column {
                modifier
                    .margin(sizes.smallGap)
                    .alignY(AlignmentY.Center)
                with(left) { composeSlot(sizeM, sizeL) }
            }
            Column {
                modifier.margin(sizes.smallGap)
                with(top) { composeSlot(sizeL, sizeM) }
                with(center) { composeSlot(sizeL, sizeL, marginV = sizes.smallGap) }
                with(bottom) { composeSlot(sizeL, sizeM) }
            }
            Column {
                modifier
                    .margin(sizes.smallGap)
                    .alignY(AlignmentY.Center)
                with(right) { composeSlot(sizeM, sizeL) }
            }
        }
    }
}