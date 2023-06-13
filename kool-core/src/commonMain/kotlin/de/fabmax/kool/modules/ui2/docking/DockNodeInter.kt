package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import kotlin.math.max
import kotlin.math.min

sealed class DockNodeInter(
    dock: Dock,
    parent: DockNodeInter?,
    width: Dimension,
    height: Dimension
) : DockNode(dock, parent, width, height) {

    val childNodes = mutableStateListOf<DockNode>()

    override val isEmpty: Boolean
        get() = childNodes.isEmpty()

    override fun insertItem(dragItem: Dockable, position: SlotPosition) {
        if (position == SlotPosition.Center) {
            throw IllegalArgumentException("Center dock position is only valid for DockNodeLeaf")
        } else {
            insertChildNodeIntoParent(position, dragItem.preferredWidth, dragItem.preferredHeight).dock(dragItem)
        }
    }

    override fun getLeafNodeAt(screenPosPx: Vec2f): DockNodeLeaf? {
        for (child in childNodes) {
            if (child.isInBounds(screenPosPx)) {
                return child.getLeafNodeAt(screenPosPx)
            }
        }
        return null
    }

    override fun composeOverlay(uiScope: UiScope) {
        super.composeOverlay(uiScope)
        childNodes.forEach { it.composeOverlay(uiScope) }
    }

    fun removeChildNode(child: DockNode) {
        childNodes -= child
        child.parent = null

        if (childNodes.size == 1) {
            val onlyChild = childNodes[0]
            onlyChild.width.set(width.value)
            onlyChild.height.set(height.value)

            val p = parent
            if (p != null) {
                p.childNodes[index] = onlyChild
            } else {
                dock.root = onlyChild
            }
            onlyChild.parent = parent
        } else {
            checkChildNodesForGrow()
        }

        if (isEmpty) {
            parent?.removeChildNode(this)
        }
    }

    /**
     * Checks sizes of child nodes for presence of a growing node. For non-empty nodes, there always must be at least
     * one growing node in order to fill up the node's available space.
     */
    abstract fun checkChildNodesForGrow()
}

class DockNodeRow(
    dock: Dock,
    parent: DockNodeInter?,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std
) : DockNodeInter(dock, parent, width, height) {

    override val nodeName: String
        get() = "$index:row"

    override val slots: DockSlots = RowSlots(this)

    override fun checkChildNodesForGrow() {
        var anyGrow = false
        var largestDp = 0f
        var largestIdx = 0
        childNodes.forEachIndexed { i, c ->
            c.height.set(Grow.Std)
            if (c.width.value is Grow) {
                anyGrow = true
            } else {
                (c.width.value as? Dp)?.let { dp ->
                    if (dp.value > largestDp) {
                        largestDp = dp.value
                        largestIdx = i
                    }
                }
            }
        }
        if (!anyGrow) {
            childNodes[largestIdx].width.set(Grow.Std)
        }
    }

    override fun UiScope.composeNodeContent() {
        Row(Grow.Std, Grow.Std) {
            val borderW = dock.borderWidth.use()
            val borderC = dock.borderColor.use()
            childNodes.use().forEachIndexed { i, dockNode ->
                if (i > 0 && borderW.value > 0f) {
                    Box(width = borderW, height = Grow.Std) {
                        modifier.backgroundColor(borderC)
                    }
                }
                dockNode()
            }
        }
    }

    fun moveEdgeTo(edgeIndex: Int, screenPosX: Float) {
        val leftChild = childNodes.getOrNull(edgeIndex - 1) ?: return
        val rightChild = childNodes.getOrNull(edgeIndex) ?: return

        val leftUiNode = leftChild.uiNode ?: return
        val rightUiNode = rightChild.uiNode ?: return

        var targetPos = max(screenPosX, leftUiNode.leftPx + Dp(MIN_DOCK_DP).px)
        targetPos = min(targetPos, rightUiNode.rightPx - Dp(MIN_DOCK_DP).px)
        if (targetPos - leftUiNode.leftPx < Dp(MIN_DOCK_DP).px || rightUiNode.rightPx - targetPos < Dp(MIN_DOCK_DP).px) {
            return
        }

        val leftW = leftChild.width.value
        val rightW = rightChild.width.value
        if (leftW is Grow && rightW is Grow) {
            val sumW = leftW.weight + rightW.weight
            val sumPx = leftChild.nodeWidthPx + rightChild.nodeWidthPx
            val newCenter = targetPos - leftUiNode.leftPx
            leftChild.width.set(Grow(sumW * newCenter / sumPx))
            rightChild.width.set(Grow(sumW - sumW * newCenter / sumPx))
        } else {
            leftChild.width.set(Dp.fromPx(targetPos - leftUiNode.leftPx))
            rightChild.width.set(Dp.fromPx(rightUiNode.rightPx - targetPos))
        }

        checkChildNodesForGrow()
    }

    private class RowSlots(val dockNode: DockNodeRow) : DockSlots() {
        val top = SlotBox(SlotPosition.Top, alignY = AlignmentY.Top)
        val bottom = SlotBox(SlotPosition.Bottom, alignY = AlignmentY.Bottom)
        override val boxes = listOf(top, bottom)

        override fun UiScope.compose() {
            val parentRows = dockNode.countParentsOfType(DockNodeRow::class)
            with(top) { composeSlot(Grow.Std, sizeS, marginH = sizeL, marginV = sizeS * parentRows) }
            with(bottom) { composeSlot(Grow.Std, sizeS, marginH = sizeL, marginV = sizeS * parentRows) }
        }
    }
}

class DockNodeColumn(
    dock: Dock,
    parent: DockNodeInter?,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std
) : DockNodeInter(dock, parent, width, height) {

    override val nodeName: String
        get() = "$index:col"

    override val slots: DockSlots = ColumnSlots(this)

    override fun checkChildNodesForGrow() {
        var anyGrow = false
        var largestDp = 0f
        var largestIdx = 0
        childNodes.forEachIndexed { i, c ->
            c.width.set(Grow.Std)
            if (c.height.value is Grow) {
                anyGrow = true
            } else {
                (c.height.value as? Dp)?.let { dp ->
                    if (dp.value > largestDp) {
                        largestDp = dp.value
                        largestIdx = i
                    }
                }
            }
        }
        if (!anyGrow) {
            childNodes[largestIdx].height.set(Grow.Std)
        }
    }

    override fun UiScope.composeNodeContent() {
        Column(Grow.Std, Grow.Std) {
            val borderW = dock.borderWidth.use()
            val borderC = dock.borderColor.use()
            childNodes.use().forEachIndexed { i, dockNode ->
                if (i > 0 && borderW.value > 0f) {
                    Box(width = Grow.Std, height = borderW) {
                        modifier.backgroundColor(borderC)
                    }
                }
                dockNode()
            }
        }
    }

    fun moveEdgeTo(edgeIndex: Int, screenPosY: Float) {
        val topChild = childNodes.getOrNull(edgeIndex - 1) ?: return
        val botChild = childNodes.getOrNull(edgeIndex) ?: return

        val topUiNode = topChild.uiNode ?: return
        val botUiNode = botChild.uiNode ?: return

        var targetPos = max(screenPosY, topUiNode.topPx + Dp(MIN_DOCK_DP).px)
        targetPos = min(targetPos, botUiNode.bottomPx - Dp(MIN_DOCK_DP).px)
        if (targetPos - topUiNode.topPx < Dp(MIN_DOCK_DP).px || botUiNode.bottomPx - targetPos < Dp(MIN_DOCK_DP).px) {
            return
        }

        val topH = topChild.height.value
        val botH = botChild.height.value
        if (topH is Grow && botH is Grow) {
            val sumH = topH.weight + botH.weight
            val sumPx = topChild.nodeHeightPx + botChild.nodeHeightPx
            val newCenter = targetPos - topUiNode.topPx
            topChild.height.set(Grow(sumH * newCenter / sumPx))
            botChild.height.set(Grow(sumH - sumH * newCenter / sumPx))
        } else {
            topChild.height.set(Dp.fromPx(targetPos - topUiNode.topPx))
            botChild.height.set(Dp.fromPx(botUiNode.bottomPx - targetPos))
        }

        checkChildNodesForGrow()
    }

    private class ColumnSlots(val dockNode: DockNodeColumn) : DockSlots() {
        val left = SlotBox(SlotPosition.Left, alignX = AlignmentX.Start)
        val right = SlotBox(SlotPosition.Right, alignX = AlignmentX.End)
        override val boxes = listOf(left, right)

        override fun UiScope.compose() {
            val parentRows = dockNode.countParentsOfType(DockNodeColumn::class)
            with(left) { composeSlot(sizeS, Grow.Std, marginV = sizeL, marginH = sizeS * parentRows) }
            with(right) { composeSlot(sizeS, Grow.Std, marginV = sizeL, marginH = sizeS * parentRows) }
        }
    }
}
