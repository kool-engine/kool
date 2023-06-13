package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

sealed class DockNode(
    val dock: Dock,
    var parent: DockNodeInter?,
    width: Dimension,
    height: Dimension
): Composable, DragAndDropHandler<Dockable> {

    abstract val nodeName: String
    abstract val isEmpty: Boolean
    protected abstract val slots: DockSlots

    val width = mutableStateOf(width)
    val height = mutableStateOf(height)

    val boundsLeftDp = mutableStateOf(Dp.ZERO)
    val boundsRightDp = mutableStateOf(Dp.ZERO)
    val boundsTopDp = mutableStateOf(Dp.ZERO)
    val boundsBottomDp = mutableStateOf(Dp.ZERO)

    var isPreviewDockPosition = true

    private val drawSlotSelector = mutableStateOf(false)
    private val dockPreview = mutableStateOf<SlotPosition?>(null)

    var uiNode: UiNode? = null
        private set
    val nodeWidthPx: Float get() = uiNode?.widthPx ?: 0f
    val nodeHeightPx: Float get() = uiNode?.heightPx ?: 0f

    protected val index: Int get() = parent?.childNodes?.indexOf(this) ?: 0

    override var dropTarget: UiNode? = null
        protected set

    override fun onDrag(
        dragItem: Dockable,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<Dockable>?,
        isHovered: Boolean
    ) {
        dockPreview.set(null)
        slots.boxes.forEach { box ->
            box.isHovered.set(dragPointer.screenPosition in box)
            if (isPreviewDockPosition && box.isHovered.value) {
                dockPreview.set(box.position)
            }
        }
        drawSlotSelector.set(isHovered)
    }

    override fun onDragEnd(
        dragItem: Dockable,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<Dockable>?,
        target: DragAndDropHandler<Dockable>?,
        success: Boolean
    ) {
        slots.boxes.forEach { box ->
            box.isHovered.set(false)
        }
        drawSlotSelector.set(false)
        dockPreview.set(null)
    }

    override fun receive(dragItem: Dockable, dragPointer: PointerEvent, source: DragAndDropHandler<Dockable>?): Boolean {
        val receiverSlot = slots.boxes.find { dragPointer.screenPosition in it }
        if (receiverSlot != null) {
            insertItem(dragItem, receiverSlot.position)
            return true
        }
        return false
    }

    protected abstract fun insertItem(dragItem: Dockable, position: SlotPosition)

    fun isInBounds(screenPosPx: Vec2f): Boolean {
        return uiNode?.isInBounds(screenPosPx) == true
    }

    abstract fun getLeafNodeAt(screenPosPx: Vec2f): DockNodeLeaf?

    fun getPath(): String {
        var path = nodeName
        var pIt = parent
        while (pIt != null) {
            path = pIt.nodeName + "/" + path
            pIt = pIt.parent
        }
        return path
    }

    fun isLeftEdgeMovable(): Boolean {
        val node = uiNode ?: return false
        val rootNode = dock.root.uiNode ?: return false
        return node.leftPx > rootNode.leftPx + 0.5f
    }

    fun isRightEdgeMovable(): Boolean {
        val node = uiNode ?: return false
        val rootNode = dock.root.uiNode ?: return false
        return node.rightPx < rootNode.rightPx - 0.5f
    }

    fun isTopEdgeMovable(): Boolean {
        val node = uiNode ?: return false
        val rootNode = dock.root.uiNode ?: return false
        return node.topPx > rootNode.topPx + 0.5f
    }

    fun isBottomEdgeMovable(): Boolean {
        val node = uiNode ?: return false
        val rootNode = dock.root.uiNode ?: return false
        return node.bottomPx < rootNode.bottomPx - 0.5f
    }

    fun moveLeftEdgeTo(screenX: Float) {
        // find parent row where the left edge is an inner edge
        var relevantChild = this
        var relevantParent = parent
        while (relevantParent != null && (relevantParent !is DockNodeRow || relevantChild.index == 0)) {
            relevantChild = relevantParent
            relevantParent = relevantParent.parent
        }
        (relevantParent as DockNodeRow?)?.moveEdgeTo(relevantChild.index, screenX)
    }

    fun moveRightEdgeTo(screenX: Float) {
        // find parent row where the right edge is an inner edge
        var relevantChild = this
        var relevantParent = parent
        while (relevantParent != null && (relevantParent !is DockNodeRow || relevantChild.index == relevantParent.childNodes.lastIndex)) {
            relevantChild = relevantParent
            relevantParent = relevantParent.parent
        }
        (relevantParent as DockNodeRow?)?.moveEdgeTo(relevantChild.index + 1, screenX)
    }

    fun moveTopEdgeTo(screenY: Float) {
        // find parent column where the top edge is an inner edge
        var relevantChild = this
        var relevantParent = parent
        while (relevantParent != null && (relevantParent !is DockNodeColumn || relevantChild.index == 0)) {
            relevantChild = relevantParent
            relevantParent = relevantParent.parent
        }
        (relevantParent as DockNodeColumn?)?.moveEdgeTo(relevantChild.index, screenY)
    }

    fun moveBottomEdgeTo(screenY: Float) {
        // find parent column where the top edge is an inner edge
        var relevantChild = this
        var relevantParent = parent
        while (relevantParent != null && (relevantParent !is DockNodeColumn || relevantChild.index == relevantParent.childNodes.lastIndex)) {
            relevantChild = relevantParent
            relevantParent = relevantParent.parent
        }
        (relevantParent as DockNodeColumn?)?.moveEdgeTo(relevantChild.index + 1, screenY)
    }

    fun <T: DockNode> countParentsOfType(nodeType: KClass<T>): Int {
        var count = 0
        var it = parent
        while (it != null) {
            if (nodeType.isInstance(it)) {
                count++
            }
            it = it.parent
        }
        return count
    }

    protected fun insertChildNodeIntoParent(
        position: SlotPosition,
        widthHint: Dp?,
        heightHint: Dp?
    ): DockNodeLeaf {
        var p = parent
        if (p == null || p::class != position.requiredNodeType) {
            p = if (position.requiredNodeType == DockNodeRow::class) {
                insertParentRow()
            } else {
                insertParentColumn()
            }
        }
        val insertWidth = widthHint ?: Grow.Std
        val insertHeigh = heightHint ?: Grow.Std
        val insertNode = insertChildNodeIntoParent(p, position, insertWidth, insertHeigh)
        p.checkChildNodesForGrow()
        return insertNode
    }

    private fun insertParentColumn(): DockNodeInter {
        val newParent = DockNodeColumn(dock, parent, width.value, height.value)
        insertParentNode(newParent)
        return newParent
    }

    private fun insertParentRow(): DockNodeInter {
        val newParent = DockNodeRow(dock, parent, width.value, height.value)
        insertParentNode(newParent)
        return newParent
    }

    /**
     * Replaces this node in its parent by a new node, which will become the parent of this node (i.e. insert a
     * new node in the connection of this node and its parent). This effectively changes a node's layout and inserts
     * the existing content as a child into the changed layout.
     */
    private fun insertParentNode(newParent: DockNodeInter) {
        val oldParent = parent
        if (oldParent != null) {
            if (index < 0) {
                throw IllegalStateException("Not in parent! self: ${getPath()}, parent: ${oldParent.getPath()}")
            }
            oldParent.childNodes[index] = newParent
        } else {
            dock.root = newParent
        }

        newParent.childNodes += this
        parent = newParent
        width.set(Grow.Std)
        height.set(Grow.Std)
    }

    /**
     * Insert a new leaf node into parent node, next to this node. New node is returned.
     */
    private fun insertChildNodeIntoParent(
        parent: DockNodeInter,
        position: SlotPosition,
        insertWidth: Dimension,
        insertHeight: Dimension
    ): DockNodeLeaf {
        val (selfW, insertW) = if (position.requiredNodeType == DockNodeRow::class) {
            computeInsertDimensions(nodeWidthPx, width.value, insertWidth)
        } else {
            width.value to width.value
        }
        val (selfH, insertH) = if (position.requiredNodeType == DockNodeRow::class) {
            height.value to height.value
        } else {
            computeInsertDimensions(nodeHeightPx, height.value, insertHeight)
        }

        val insertIdx = if (position == SlotPosition.Left || position == SlotPosition.Top) {
            index
        } else {
            index + 1
        }
        val insertNode = DockNodeLeaf(dock, parent, insertW, insertH)
        width.set(selfW)
        height.set(selfH)
        parent.childNodes.add(insertIdx, insertNode)
        return insertNode
    }

    private fun computeInsertDimensions(currentPx: Float, current: Dimension, insert: Dimension): Pair<Dimension, Dimension> {
        return when (current) {
            is Grow -> {
                when (insert) {
                    is Dp -> {
                        val newSelfW = current.weight * (1f - insert.px / currentPx).clamp(0.05f, 1f)
                        Grow(newSelfW) to insert
                    }
                    is Grow -> {
                        val s = current.weight / (1f + insert.weight)
                        Grow(s) to Grow(current.weight - s)
                    }
                    FitContent -> {
                        // FitContent does not make sense for docking -> simply split 50/50
                        Grow(current.weight * 0.5f) to Grow(current.weight * 0.5f)
                    }
                }
            }
            is Dp -> {
                when (insert) {
                    is Dp -> {
                        val clampedInsert = Dp(insert.value.clamp(MIN_DOCK_DP, current.value - MIN_DOCK_DP))
                        Dp(current.value - clampedInsert.value) to clampedInsert
                    }
                    is Grow -> {
                        val s = 1f / (1f + insert.weight)
                        Dp(current.value * s) to Dp(current.value * (1f - s))
                    }
                    FitContent -> {
                        // FitContent does not make sense for docking -> simply split 50/50
                        current * 0.5f to current * 0.5f
                    }
                }
            }
            FitContent -> {
                throw IllegalStateException("DockNode dimension is FitContent, which is not supported")
            }
        }
    }

    override fun UiScope.compose() = Box(width.use(), height.use()) {
        this@DockNode.uiNode = uiNode
        modifier
            .onPositioned {
                boundsLeftDp.set(Dp.fromPx(it.leftPx))
                boundsRightDp.set(Dp.fromPx(it.rightPx))
                boundsTopDp.set(Dp.fromPx(it.topPx))
                boundsBottomDp.set(Dp.fromPx(it.bottomPx))
            }

        composeNodeContent()

        this@DockNode.dropTarget = uiNode
        dock.dndContext.registerHandler(this@DockNode)
    }

    internal open fun composeOverlay(uiScope: UiScope) {
        uiScope.apply {
            Box {
                modifier
                    .margin(start = boundsLeftDp.use(), top = boundsTopDp.use())
                    .size(boundsRightDp.use() - boundsLeftDp.use(), boundsBottomDp.use() - boundsTopDp.use())

                dockPreview.use()?.let { dockPreview(it) }
                if (drawSlotSelector.use()) {
                    slots()
                }
            }
        }
    }

    protected abstract fun UiScope.composeNodeContent()

    private fun UiScope.dockPreview(previewPos: SlotPosition) {
        when (previewPos) {
            SlotPosition.Center -> {
                dockPreviewBox(true)
            }
            SlotPosition.Left -> {
                Row(Grow.Std, Grow.Std) {
                    dockPreviewBox(true)
                    dockPreviewBox(false)
                }
            }
            SlotPosition.Right -> {
                Row(Grow.Std, Grow.Std) {
                    dockPreviewBox(false)
                    dockPreviewBox(true)
                }
            }
            SlotPosition.Top -> {
                Column(Grow.Std, Grow.Std) {
                    dockPreviewBox(true)
                    dockPreviewBox(false)
                }
            }
            SlotPosition.Bottom -> {
                Column(Grow.Std, Grow.Std) {
                    dockPreviewBox(false)
                    dockPreviewBox(true)
                }
            }
        }
    }

    private fun UiScope.dockPreviewBox(withBg: Boolean) = Box {
        modifier.size(Grow.Std, Grow.Std)
        if (withBg) {
            modifier
                .background(RoundRectBackground(colors.secondaryAlpha(0.1f), sizes.smallGap))
                .border(RoundRectBorder(colors.primaryAlpha(0.3f), sizes.smallGap, sizes.smallGap * 0.5f))
        }
    }

    protected abstract class DockSlots : Composable {
        abstract val boxes: List<SlotBox>

        companion object {
            val UiScope.sizeL: Dp get() = sizes.largeGap * 4f
            val UiScope.sizeM: Dp get() = sizeL * 0.66f
            val UiScope.sizeS: Dp get() = sizeL * 0.4f
        }
    }

    companion object {
        const val MIN_DOCK_DP = 16f
    }

    protected class SlotBox(
        val position: SlotPosition,
        val alignX: AlignmentX = AlignmentX.Center,
        val alignY: AlignmentY = AlignmentY.Center,
    ) {
        val isHovered = mutableStateOf(false)

        private var slotUiNode: UiNode? = null

        fun UiScope.composeSlot(
            width: Dimension,
            height: Dimension,
            marginH: Dp = Dp.ZERO,
            marginV: Dp = Dp.ZERO
        ) {
            Box {
                slotUiNode = uiNode
                val alphaMod = if (isHovered.use()) 1f else 0.7f
                modifier
                    .size(width, height)
                    .margin(vertical = marginV, horizontal = marginH)
                    .align(xAlignment = alignX, yAlignment = alignY)
                    .background(RoundRectBackground(colors.primaryAlpha(0.8f * alphaMod), sizes.smallGap))
                    .border(RoundRectBorder(colors.primaryAlpha(1f * alphaMod), sizes.smallGap, sizes.smallGap * 0.5f))
            }
        }

        operator fun contains(posPx: Vec2f): Boolean {
            return slotUiNode?.isInBounds(posPx) == true
        }
    }

    enum class SlotPosition(val requiredNodeType: KClass<*>) {
        Center(DockNodeLeaf::class),
        Left(DockNodeRow::class),
        Right(DockNodeRow::class),
        Top(DockNodeColumn::class),
        Bottom(DockNodeColumn::class)
    }
}