package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.logW

class DockNodeLeaf(
    dock: Dock,
    parent: DockNodeInter?,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std
) : DockNode(dock, parent, width, height) {

    val dockedItems = mutableStateListOf<Dockable>()
    override val isEmpty: Boolean
        get() = dockedItems.isEmpty()

    override val nodeName: String
        get() = "$index:leaf"

    override val slots: DockSlots = LeafSlots()

    fun dock(dockable: Dockable, index: Int = 0) {
        if (index in dockedItems.indices) {
            dockedItems.add(index, dockable)
        } else {
            dockedItems.add(dockable)
        }
        dockable.onDocked(this)
    }

    override fun insertDragItem(dragItem: Dockable, position: SlotPosition) {
        if (position == SlotPosition.Center) {
            dock(dragItem)
        } else {
            insertChildNodeIntoParent(position).dock(dragItem)
        }
    }

    override fun undock(dockable: Dockable) {
        if (dockedItems.remove(dockable)) {
            dockable.onUndocked(this)
        }
        if (isEmpty) {
            parent?.removeChildNode(this)
        }
    }

    fun isOnTop(dockable: Dockable): Boolean {
        return dockable === dockedItems.getOrNull(0)
    }

    fun bringToTop(dockable: Dockable) {
        if (isOnTop(dockable)) {
            return
        }
        if (dockedItems.remove(dockable)) {
            dockedItems.add(0, dockable)
            dockedItems.forEachIndexed { index, item -> item.dockOrderIndex = index }
        } else {
            logW { "requested Dockable $dockable to be on top, but is not present in docked items" }
        }
    }

    override fun getLeafNodeAt(screenPosPx: Vec2f): DockNodeLeaf? {
        if (isInBounds(screenPosPx)) {
            return this
        }
        return null
    }

    override fun UiScope.composeNodeContent() { }

    private class LeafSlots : DockSlots() {
        val left = SlotBox(SlotPosition.Left) //{ dockNode.insertChildNode(SlotPosition.Left) }
        val top = SlotBox(SlotPosition.Top) //{ dockNode.insertChildNode(SlotPosition.Top) }
        val center = SlotBox(SlotPosition.Center) //{ dockNode.parent?.removeChildNode(dockNode) }
        val bottom = SlotBox(SlotPosition.Bottom) //{ dockNode.insertChildNode(SlotPosition.Bottom) }
        val right = SlotBox(SlotPosition.Right) //{ dockNode.insertChildNode(SlotPosition.Right) }
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