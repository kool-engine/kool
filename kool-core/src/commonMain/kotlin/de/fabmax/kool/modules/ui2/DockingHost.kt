package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.min
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node

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
        // reorder child surfaces: the one with the latest user input is last / on top
        // 2nd to last is the docking surface, so that overlay is drawn over background surfaces but behind drag
        // surface
        val latestInputSurface = childSurfaces.maxByOrNull { it.lastInput }
        if (latestInputSurface != focusedSurface && (latestInputSurface?.lastInput ?: 0.0) > (focusedSurface?.lastInput ?: 0.0)) {
            focusedSurface?.isFocused?.set(false)
            focusedSurface = latestInputSurface
            focusedSurface?.isFocused?.set(true)
        }
        focusedSurface?.let { removeNode(it) }
        removeNode(dockingSurface)
        addNode(dockingSurface)
        focusedSurface?.let { addNode(it) }
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
        val rootNode = DockingNode(null, DockPosition.Center).apply { isMergeIfHalfEmpty = false }

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

    inner class DockingNode(var parent: DockingNode?, val position: DockPosition) : ComposableComponent {
        var depth = 0
        private val childNodes = mutableListOf<DockingNode>()
        private val slots = DockPosition.values().map { DockingSlot(it, this) }
        val isDrawSlots = mutableStateOf(false)

        val dockedWindows = mutableListOf<WindowScope>()

        val isLeaf: Boolean get() = childNodes.isEmpty()
        val isEmpty: Boolean get() = isLeaf && dockedWindows.isEmpty()
        val canSplit: Boolean get() = depth < maxSplitDepth && !(isMergeIfHalfEmpty && isEmpty)

        val boundsMinPx = MutableVec2f()
        val boundsMaxPx = MutableVec2f()

        var isMergeIfHalfEmpty = true


        operator fun contains(point: Vec2f): Boolean {
            return point.x >= boundsMinPx.x && point.x <= boundsMaxPx.x && point.y >= boundsMinPx.y && point.y <= boundsMaxPx.y
        }

        fun undock(window: WindowScope) {
            dockedWindows -= window
            window.windowState.dockedTo.set(null)
        }

        fun dock(window: WindowScope) {
            if (!isLeaf) {
                throw IllegalStateException("Windows can only be docked to leaf nodes")
            }
            dockedWindows += window
            window.apply {
                windowState.dockedTo.set(this@DockingNode)
                windowState.setWindowBounds(
                    pxToDp(boundsMinPx.x).dp, pxToDp(boundsMinPx.y).dp,
                    pxToDp(boundsMaxPx.x - boundsMinPx.x).dp, pxToDp(boundsMaxPx.y - boundsMinPx.y).dp,
                )
            }
        }

        private fun UiScope.updateChildWindowBounds() {
            val x = pxToDp(boundsMinPx.x).dp
            val y = pxToDp(boundsMinPx.y).dp
            val w = pxToDp(boundsMaxPx.x - boundsMinPx.x).dp
            val h = pxToDp(boundsMaxPx.y - boundsMinPx.y).dp
            dockedWindows.forEach {
                it.windowState.setWindowBounds(x, y, w, h)
            }
        }

        override fun UiScope.compose() = Box(Grow.Std, Grow.Std) {
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
            if (isLeaf) {
                return
            }
            val a = childNodes[0]
            val b = childNodes[1]
            if (a.position == DockPosition.Left || a.position == DockPosition.Right) {
                Row(Grow.Std, Grow.Std) {
                    if (a.position == DockPosition.Left) { a(); b() } else { b(); a() }
                }
            } else {
                Column(Grow.Std, Grow.Std) {
                    if (a.position == DockPosition.Top) { a(); b() } else { b(); a() }
                }
            }
        }

        fun mergeEmptyChildren() {
            if (isLeaf) {
                return
            }
            val a = childNodes[0]
            val b = childNodes[1]
            a.mergeEmptyChildren()
            b.mergeEmptyChildren()

            if (isMergeIfHalfEmpty) {
                // method a: merge children if one of them is empty
                if (a.isEmpty || b.isEmpty) {
                    val nonEmpty = if (a.isEmpty) b else a
                    childNodes.clear()

                    dockedWindows += nonEmpty.dockedWindows
                    dockedWindows.forEach { it.windowState.dockedTo.set(this) }

                    childNodes += nonEmpty.childNodes
                    childNodes.forEach { it.parent = this }
                }

            } else {
                // method b: merge (i.e. remove) children only if both are empty
                // less layout changes than method a but gaps between nodes can appear
                if (a.isEmpty && b.isEmpty) {
                    childNodes.clear()
                }
            }
        }

        fun updateDepth() {
            depth = (parent?.depth ?: -1) + 1
            childNodes.forEach { it.updateDepth() }
        }

        fun split(newNodePos: DockPosition): DockingNode {
            if (!isLeaf) {
                throw IllegalStateException("Only leaf nodes can be split")
            }

            if (!canSplit) {
                return this
            }

            // add newly spawned, empty node
            val newNode = DockingNode(this, newNodePos)
            childNodes += newNode

            // add split node on opposing side which will contain the windows previously docked in this node
            val splitPos = when (newNodePos) {
                DockPosition.Left -> DockPosition.Right
                DockPosition.Right -> DockPosition.Left
                DockPosition.Top -> DockPosition.Bottom
                DockPosition.Bottom -> DockPosition.Top
                DockPosition.Center -> throw IllegalArgumentException("Center is not a valid split position")
            }
            val splitNode = DockingNode(this, splitPos)
            splitNode.dockedWindows += dockedWindows
            dockedWindows.forEach { it.windowState.dockedTo.set(splitNode) }
            dockedWindows.clear()
            childNodes += splitNode

            return newNode
        }

        fun getNodeAt(screenPos: Vec2f): DockingNode? {
            if (isLeaf) {
                return this
            } else {
                childNodes.forEach {
                    if (screenPos in it) {
                        return it.getNodeAt(screenPos)
                    }
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
            val maxW = (uiNode.widthPx * 0.25f).dp
            val maxH = (uiNode.heightPx * 0.25f).dp
            val hoverScale = if (isHv) 1.1f else 1f
            Box {
                when (position) {
                    DockPosition.Top -> {
                        modifier.width(min(300.dp, maxW) * hoverScale)
                        modifier.height(min(100.dp, maxH) * hoverScale)
                    }
                    DockPosition.Right -> {
                        modifier.width(min(100.dp, maxW) * hoverScale)
                        modifier.height(min(300.dp, maxH) * hoverScale)
                    }
                    DockPosition.Bottom -> {
                        modifier.width(min(300.dp, maxW) * hoverScale)
                        modifier.height(min(100.dp, maxH) * hoverScale)
                    }
                    DockPosition.Left -> {
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
                        if (isHv) colors.accent.withAlpha(0.3f) else colors.accentVariant.withAlpha(0.2f)
                    )
                    .border(
                        RectBorder(
                            if (isHv) colors.accent.withAlpha(0.5f) else colors.accentVariant.withAlpha(0.4f),
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
                node.split(position).dock(window)
            }
        }
    }

    enum class DockPosition(val alignX: AlignmentX, val alignY: AlignmentY) {
        Top(AlignmentX.Center, AlignmentY.Top),
        Right(AlignmentX.End, AlignmentY.Center),
        Bottom(AlignmentX.Center, AlignmentY.Bottom),
        Left(AlignmentX.Start, AlignmentY.Center),
        Center(AlignmentX.Center, AlignmentY.Center)
    }
}