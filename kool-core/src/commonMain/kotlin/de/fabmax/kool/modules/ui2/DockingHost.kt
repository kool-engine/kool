package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.min
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import kotlin.math.max

interface DockingListener {
    fun onWindowDocked(window: WindowScope, dockedTo: DockingContainer) { }
    fun onWindowUndocked(window: WindowScope, dockedTo: DockingContainer) { }

    fun onDockingContainerSplit(splitContainer: DockingContainer) { }
    fun onDockingContainerMerged(splitContainer: DockingContainer) { }
}

class DockingHost : Group() {

    val dockingSurface = DockingSurface()
    val childSurfaces = mutableListOf<UiSurface>()

    val dockingListeners = mutableListOf<DockingListener>()

    private var hoveredDockingNode: DockingContainer? = null
    private var hoveredDockingSlot: DockingSlot? = null
    private var focusedSurface: UiSurface? = null

    var maxSplitDepth = 4

    init {
        +dockingSurface
    }

    fun dockWindow(
        window: WindowScope,
        path: List<Pair<DockPosition, Dimension>>
    ) {
        var container = dockingSurface.rootContainer
        for (p in path) {
            if (container.isLeaf) {
                container.split(p.first, p.second)
            }
            val (a, b) = container.childContainers ?: break

            container = if (a.position == p.first) {
                a
            } else if (b.position == p.first) {
                b
            } else {
                break
            }
        }
        container.getNearestLeaf().dock(window)
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

    fun getNodeContainingSplitEdgeAt(screenPos: Vec2f): DockingContainer? {
        return dockingSurface.rootContainer.getNodeContainingSplitEdgeAt(screenPos)
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
        // floating surface with the latest user input is last / on top
        // docked window with highest windowState.dockingContainerZ is last / on top
        // 2nd to last is the docking surface, so that overlay is drawn over background surfaces but behind drag
        // surface
        intChildren.sortBy {
            if (it is UiSurface) {
                val window = it.windowScope
                if (window == null || !window.isDocked) {
                    // undocked window, make sure it is on top
                    it.lastInputTime + 1e9
                } else {
                    it.lastInputTime
                }
            } else {
                0.0
            }
        }

        val latestInputSurface = childSurfaces.maxByOrNull { it.lastInputTime }
        if (latestInputSurface != focusedSurface && (latestInputSurface?.lastInputTime ?: 0.0) > (focusedSurface?.lastInputTime ?: 0.0)) {
            focusedSurface?.windowScope?.windowState?.isFocused?.set(false)
            focusedSurface = latestInputSurface
            focusedSurface?.windowScope?.windowState?.isFocused?.set(true)
        }
        removeNode(dockingSurface)
        addNode(dockingSurface, max(0, childSurfaces.lastIndex))
        super.update(updateEvent)
    }

    override fun addNode(node: Node, index: Int) {
        if (node is UiSurface) {
            node.windowScope?.windowState?.isFocused?.set(false)
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

    inner class DockingSurface : UiSurface(name = "DockingSurface") {
        val rootContainer = DockingContainer(
            this@DockingHost,
            null,
            DockPosition.Center, Grow.Std, Grow.Std
        ).apply { isMergeIfHalfEmpty = false }

        init {
            content = {
                rootContainer.mergeEmptyChildren()
                rootContainer.updateDepth()
                rootContainer()
            }
            inputMode = InputCaptureMode.CaptureDisabled
        }

        fun getNodeAt(screenPos: Vec2f): DockingContainer? {
            return rootContainer.getNodeAt(screenPos)
        }
    }

    class DockingSlot(val position: DockPosition, val container: DockingContainer) : Composable {
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
                container.dock(window)
            } else {
                val insertDim = if (position.isHorizontal) {
                    Grow(container.xWeightByWidthPx(window.uiNode.widthPx))
                } else {
                    Grow(container.yWeightByHeightPx(window.uiNode.heightPx))
                }
                container.split(position, insertDim).dock(window)
            }
        }
    }

    enum class DockPosition(val alignX: AlignmentX, val alignY: AlignmentY, val isHorizontal: Boolean) {
        Start(AlignmentX.Start, AlignmentY.Center, true),
        End(AlignmentX.End, AlignmentY.Center, true),
        Top(AlignmentX.Center, AlignmentY.Top, false),
        Bottom(AlignmentX.Center, AlignmentY.Bottom, false),
        Center(AlignmentX.Center, AlignmentY.Center, false)
    }
}