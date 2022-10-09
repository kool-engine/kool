package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.min
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import kotlin.math.abs
import kotlin.math.max

class DockingHost : Group() {

    private val dockingSurface = DockingSurface()
    private val childSurfaces = mutableListOf<UiSurface>()

    private var hoveredDockingNode: DockingNode? = null
    private var hoveredDockingSlot: DockingSlot? = null

    var focusedSurface: UiSurface? = null
        private set

    var maxSplitDepth = 4

    init {
        +dockingSurface
    }

    @Suppress("UNUSED_PARAMETER")
    fun onWindowMoveStart(ev: PointerEvent, window: WindowScope) {
        window.windowState.dockedTo.value?.undock(window)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onWindowMove(ev: PointerEvent, window: WindowScope) {
        updateHoveredPane(ev.screenPosition)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onWindowMoveEnd(ev: PointerEvent, window: WindowScope) {
        hoveredDockingNode?.isDrawSlots?.set(false)
        hoveredDockingNode = null

        hoveredDockingSlot?.let {
            it.dock(window)
            it.isHovered.set(false)
        }
        hoveredDockingSlot = null
    }

    fun isResizableBorder(screenPos: Vec2f): Boolean {
        return getNodeContainingSplitEdgeAt(screenPos) != null
    }

    fun getNodeContainingSplitEdgeAt(screenPos: Vec2f): DockingNode? {
        return dockingSurface.rootNode.getNodeContainingSplitEdgeAt(screenPos)
    }

    private fun updateHoveredPane(screenPos: Vec2f) {
        val hoverNode = dockingSurface.getNodeAt(screenPos)
        if (hoveredDockingNode != hoverNode) {
            hoveredDockingNode?.isDrawSlots?.set(false)
        }
        hoveredDockingNode = hoverNode
        hoverNode?.isDrawSlots?.set(true)

        val hoverSlot = hoverNode?.getSlotAt(screenPos)
        if (hoveredDockingSlot != hoverSlot) {
            hoveredDockingSlot?.isHovered?.set(false)
        }
        hoveredDockingSlot = hoverSlot
        hoverSlot?.isHovered?.set(true)
    }

    override fun update(updateEvent: RenderPass.UpdateEvent) {
        // sort child surfaces:
        // floating surfaces are always on top of docked surfaces
        // surface with the latest user input is last / on top (within it's docked / undocked group)
        // 2nd to last is the docking surface, so that overlay is drawn over background surfaces but behind drag
        // surface

        intChildren.sortBy {
            if (it is UiSurface) {
                it.lastInput + if (it.isDocked.value) 0.0 else 1e9
            } else {
                0.0
            }
        }

        val latestInputSurface = childSurfaces.maxByOrNull { it.lastInput }
        if (latestInputSurface != focusedSurface && (latestInputSurface?.lastInput ?: 0.0) > (focusedSurface?.lastInput ?: 0.0)) {
            focusedSurface?.isFocused?.set(false)
            focusedSurface = latestInputSurface
            focusedSurface?.isFocused?.set(true)
        }
//        focusedSurface?.let { removeNode(it) }
        removeNode(dockingSurface)
        addNode(dockingSurface, max(0, childSurfaces.lastIndex))
//        focusedSurface?.let { addNode(it) }
        super.update(updateEvent)
    }

    override fun addNode(node: Node, index: Int) {
        if (node is UiSurface) {
            childSurfaces += node
        }
        super.addNode(node, index)
    }

    override fun removeNode(node: Node): Boolean {
        if (node is UiSurface) {
            childSurfaces -= node
        }
        return super.removeNode(node)
    }

    private inner class DockingSurface : UiSurface(name = "DockingSurface") {
        val rootNode = DockingNode(null, DockPosition.Center, 1f, 1f).apply { isMergeIfHalfEmpty = false }

        init {
            content = {
                rootNode.mergeEmptyChildren()
                rootNode.updateDepth()
                rootNode()
            }
            // fixme: temporary, at some point docking surface will need input, but it must not block it...
            isInputEnabled = false
        }

        fun getNodeAt(screenPos: Vec2f): DockingNode? {
            return rootNode.getNodeAt(screenPos)
        }
    }

    inner class DockingNode(
        var parent: DockingNode?,
        val position: DockPosition,
        widthWeight: Float,
        heightWeight: Float
    ) : ComposableComponent {

        var depth = 0
        private var childNodes: Pair<DockingNode, DockingNode>? = null
        private val slots = DockPosition.values().map { DockingSlot(it, this) }
        val isDrawSlots = mutableStateOf(false)
        val widthWeight = mutableStateOf(widthWeight)
        val heightWeight = mutableStateOf(heightWeight)

        val dockedWindows = mutableListOf<WindowScope>()
        val dockingHost: DockingHost get() = this@DockingHost

        val isLeaf: Boolean get() = childNodes == null
        val isEmpty: Boolean get() = isLeaf && dockedWindows.isEmpty()
        val canSplit: Boolean get() = depth < maxSplitDepth && !(isMergeIfHalfEmpty && isEmpty)
        val isSplitHorizontally: Boolean get() = childNodes?.first?.position == DockPosition.Start

        val boundsMinPx = MutableVec2f()
        val boundsMaxPx = MutableVec2f()
        val width: Float get() = boundsMaxPx.x - boundsMinPx.x
        val height: Float get() = boundsMaxPx.y - boundsMinPx.y

        var isMergeIfHalfEmpty = true

        operator fun contains(point: Vec2f): Boolean {
            return point.x >= boundsMinPx.x && point.x <= boundsMaxPx.x && point.y >= boundsMinPx.y && point.y <= boundsMaxPx.y
        }

        fun undock(window: WindowScope) {
            dockedWindows -= window
            window.windowState.dockedTo.set(null)
            window.surface.isDocked.set(false)
        }

        fun dock(window: WindowScope) {
            if (!isLeaf) {
                throw IllegalStateException("Windows can only be docked to leaf nodes")
            }
            dockedWindows += window
            window.apply {
                surface.isDocked.set(true)
                windowState.dockedTo.set(this@DockingNode)
                windowState.setWindowBounds(
                    Dp.fromPx(boundsMinPx.x), Dp.fromPx(boundsMinPx.y),
                    Dp.fromPx(width), Dp.fromPx(height),
                )
            }
        }

        fun getNodeContainingSplitEdgeAt(screenPos: Vec2f): DockingNode? {
            val (a, b) = childNodes ?: return null
            if (screenPos !in this) {
                return null
            }

            return if (isSplitHorizontally && abs(screenPos.x - a.boundsMaxPx.x) < WindowNode.RESIZE_BORDER_WIDTH.px) {
                this
            } else if (!isSplitHorizontally && abs(screenPos.y - a.boundsMaxPx.y) < WindowNode.RESIZE_BORDER_WIDTH.px) {
                this
            } else if (screenPos in a) {
                a.getNodeContainingSplitEdgeAt(screenPos)
            } else {
                b.getNodeContainingSplitEdgeAt(screenPos)
            }
        }

        fun moveSplitEdgeTo(screenPos: Vec2f) {
            val (a, b) = childNodes ?: return

            if (isSplitHorizontally) {
                val minRemaining = min(width * 0.5f, Dp(50f).px)
                val splitX = (screenPos.x - boundsMinPx.x).clamp(minRemaining, width - minRemaining)
                val weightA = (splitX / width)
                a.widthWeight.set(weightA)
                b.widthWeight.set(1f - weightA)

            } else {
                val minRemaining = min(height * 0.5f, Dp(50f).px)
                val splitY = (screenPos.y - boundsMinPx.y).clamp(minRemaining, height - minRemaining)
                val weightA = (splitY / height)
                a.heightWeight.set(weightA)
                b.heightWeight.set(1f - weightA)
            }
        }

        private fun updateChildWindowBounds() {
            val x = Dp.fromPx(boundsMinPx.x)
            val y = Dp.fromPx(boundsMinPx.y)
            val w = Dp.fromPx(boundsMaxPx.x - boundsMinPx.x)
            val h = Dp.fromPx(boundsMaxPx.y - boundsMinPx.y)
            dockedWindows.forEach {
                it.windowState.setWindowBounds(x, y, w, h)
            }
        }

        override fun UiScope.compose() = Box(Grow(widthWeight.use()), Grow(heightWeight.use())) {
            modifier
                .onPositioned {
                    boundsMinPx.set(it.leftPx, it.topPx)
                    boundsMaxPx.set(it.rightPx, it.bottomPx)
                    updateChildWindowBounds()
                }

            composeChildren()

            if (isDrawSlots.use()) {
                if (canSplit) {
                    slots.forEach { it() }
                } else {
                    slots.first { it.position == DockPosition.Center }.invoke()
                }
            }
        }

        private fun UiScope.composeChildren() {
            val (a, b) = childNodes ?: return
            Box(Grow.Std, Grow.Std) {
                modifier.layout(if (isSplitHorizontally) RowLayout else ColumnLayout)
                a()
                b()
            }
        }

        fun mergeEmptyChildren() {
            val (a, b) = childNodes ?: return
            a.mergeEmptyChildren()
            b.mergeEmptyChildren()

            if (isMergeIfHalfEmpty) {
                // method a: merge children if one of them is empty
                if (a.isEmpty || b.isEmpty) {
                    val nonEmpty = if (a.isEmpty) b else a
                    childNodes = null

                    dockedWindows += nonEmpty.dockedWindows
                    dockedWindows.forEach { it.windowState.dockedTo.set(this) }

                    childNodes = nonEmpty.childNodes
                    childNodes?.let { (a, b) ->
                        a.parent = this
                        b.parent = this
                    }
                }

            } else {
                // method b: merge (i.e. remove) children only if both are empty
                // less layout changes than method a but gaps between nodes can appear
                if (a.isEmpty && b.isEmpty) {
                    childNodes = null
                }
            }
        }

        fun updateDepth() {
            depth = (parent?.depth ?: -1) + 1
            childNodes?.let { (a, b) ->
                a.updateDepth()
                b.updateDepth()
            }
        }

        fun split(insertPos: DockPosition, widthPxHint: Float, heightPxHint: Float): DockingNode {
            if (!isLeaf) {
                throw IllegalStateException("Only leaf nodes can be split")
            }
            if (!canSplit) {
                return this
            }

            var insertWidthW = 1f
            var insertHeightW = 1f
            var opposingWidthW = 1f
            var opposingHeightW = 1f
            val insertFirst: Boolean
            val opposingPos: DockPosition
            when (insertPos) {
                DockPosition.Start -> {
                    insertWidthW = (widthPxHint / width).clamp(0.2f, 0.8f)
                    opposingWidthW = 1f - insertWidthW
                    opposingPos = DockPosition.End
                    insertFirst = true
                }
                DockPosition.End -> {
                    insertWidthW = (widthPxHint / width).clamp(0.2f, 0.8f)
                    opposingWidthW = 1f - insertWidthW
                    opposingPos = DockPosition.Start
                    insertFirst = false
                }
                DockPosition.Top -> {
                    insertHeightW = (heightPxHint / height).clamp(0.2f, 0.8f)
                    opposingHeightW = 1f - insertHeightW
                    opposingPos = DockPosition.Bottom
                    insertFirst = true
                }
                DockPosition.Bottom -> {
                    insertHeightW = (heightPxHint / height).clamp(0.2f, 0.8f)
                    opposingHeightW = 1f - insertHeightW
                    opposingPos = DockPosition.Top
                    insertFirst = false
                }
                DockPosition.Center -> return this
            }

            // add newly spawned, empty node
            val insertNode = DockingNode(this, insertPos, insertWidthW, insertHeightW)

            // add split node on opposing side which will contain the windows previously docked in this node
            val splitNode = DockingNode(this, opposingPos, opposingWidthW, opposingHeightW)
            splitNode.dockedWindows += dockedWindows
            dockedWindows.forEach { it.windowState.dockedTo.set(splitNode) }
            dockedWindows.clear()

            childNodes = if (insertFirst) {
                insertNode to splitNode
            } else {
                splitNode to insertNode
            }

            return insertNode
        }

        fun getNodeAt(screenPos: Vec2f): DockingNode? {
            if (isLeaf) {
                return this
            } else {
                childNodes?.let { (a, b) ->
                    if (screenPos in a) return a.getNodeAt(screenPos)
                    if (screenPos in b) return b.getNodeAt(screenPos)
                }
            }
            return null
        }

        fun getSlotAt(screenPos: Vec2f): DockingSlot? {
            return slots.find { screenPos in it }
        }
    }

    class DockingSlot(val position: DockPosition, val node: DockingNode) : ComposableComponent {
        val isHovered = mutableStateOf(false)

        val boundsMinPx = MutableVec2f()
        val boundsMaxPx = MutableVec2f()

        override fun UiScope.compose() {
            val isHv = isHovered.use()
            val maxW = (uiNode.widthPx * 0.2f).dp
            val maxH = (uiNode.heightPx * 0.2f).dp
            val hoverScale = if (isHv) 1.1f else 1f
            Box {
                when (position) {
                    DockPosition.Top -> {
                        modifier.width(min(300.dp, maxW) * hoverScale)
                        modifier.height(min(100.dp, maxH) * hoverScale)
                    }
                    DockPosition.End -> {
                        modifier.width(min(100.dp, maxW) * hoverScale)
                        modifier.height(min(300.dp, maxH) * hoverScale)
                    }
                    DockPosition.Bottom -> {
                        modifier.width(min(300.dp, maxW) * hoverScale)
                        modifier.height(min(100.dp, maxH) * hoverScale)
                    }
                    DockPosition.Start -> {
                        modifier.width(min(100.dp, maxW) * hoverScale)
                        modifier.height(min(300.dp, maxH) * hoverScale)
                    }
                    DockPosition.Center -> {
                        modifier.width(min(200.dp, maxW) * hoverScale)
                        modifier.height(min(200.dp, maxH) * hoverScale)
                    }
                }

                modifier
                    .align(position.alignX, position.alignY)
                    .backgroundColor(
                        if (isHv) colors.primary.withAlpha(0.3f) else colors.primaryVariant.withAlpha(0.2f)
                    )
                    .border(
                        RectBorder(
                            if (isHv) colors.primary.withAlpha(0.5f) else colors.primaryVariant.withAlpha(0.4f),
                            sizes.smallGap * 0.5f
                        )
                    )
                    .onPositioned {
                        boundsMinPx.set(it.leftPx, it.topPx)
                        boundsMaxPx.set(it.rightPx, it.bottomPx)
                    }
            }
        }

        operator fun contains(point: Vec2f): Boolean {
            return point.x >= boundsMinPx.x && point.x <= boundsMaxPx.x && point.y >= boundsMinPx.y && point.y <= boundsMaxPx.y
        }

        fun dock(window: WindowScope) {
            if (position == DockPosition.Center) {
                node.dock(window)
            } else {
                node.split(position, window.uiNode.widthPx, window.uiNode.heightPx).dock(window)
            }
        }
    }

    enum class DockPosition(val alignX: AlignmentX, val alignY: AlignmentY) {
        Start(AlignmentX.Start, AlignmentY.Center),
        End(AlignmentX.End, AlignmentY.Center),
        Top(AlignmentX.Center, AlignmentY.Top),
        Bottom(AlignmentX.Center, AlignmentY.Bottom),
        Center(AlignmentX.Center, AlignmentY.Center)
    }
}