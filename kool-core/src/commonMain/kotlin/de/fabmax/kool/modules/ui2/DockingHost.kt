package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.min
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.logD

class DockingHost : Group() {

    private val dockingSurface = DockingSurface()
    private val childSurfaces = mutableListOf<UiSurface>()

    private var hoveredDockingPane: DockingPane? = null
    private var hoveredDockingSlot: DockingSlot? = null

    var focusedSurface: UiSurface? = null
        private set

    init {
        +dockingSurface
    }

    fun onWindowMoveStart(window: WindowScope) {
        window.windowState.dockedTo.value?.undock(window)
    }

    fun onWindowMove(ev: PointerEvent) {
        updateHoveredPane(ev.screenPosition)
    }

    fun onWindowMoveEnd(window: WindowScope) {
        hoveredDockingPane?.isDrawSlots?.set(false)
        hoveredDockingPane = null

        hoveredDockingSlot?.let {
            it.dock(window)
            it.isHovered.set(false)
        }
        hoveredDockingSlot = null
    }

    private fun updateHoveredPane(screenPos: Vec2f) {
        val hoverPane = dockingSurface.getPaneAt(screenPos)
        if (hoveredDockingPane != hoverPane) {
            hoveredDockingPane?.isDrawSlots?.set(false)
        }
        hoveredDockingPane = hoverPane
        hoverPane.isDrawSlots.set(true)

        val hoverSlot = hoverPane.getSlotAt(screenPos)
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

    private class DockingSurface : UiSurface(name = "DockingSurface") {
        val rootPane = DockingPane(null, DockPosition.Center)

        init {
            content = {
                rootPane()
            }
            // fixme: temporary, at some point docking surface will need input, but it must not block it...
            isInputEnabled = false
        }

        fun getPaneAt(screenPos: Vec2f): DockingPane {
            return rootPane.getPaneAt(screenPos)
        }
    }

    class DockingPane(val parent: DockingPane?, val position: DockPosition) : ComposableComponent {
        private val childPanes = mutableStateListOf<DockingPane>()
        private val slots = DockPosition.values().map { DockingSlot(it, this) }
        val isDrawSlots = mutableStateOf(false)

        val dockedWindows = mutableListOf<WindowScope>()

        val isEmpty: Boolean get() = dockedWindows.isEmpty() && childPanes.isEmpty()

        private val panePos = MutableVec2f()
        private val paneSize = MutableVec2f()

        val path: String

        init {
            var path = "$position"
            var it = parent
            while (it != null) {
                path = "${it.position}->$path"
                it = it.parent
            }
            this.path = path
        }

        fun setupDockPosition(target: UiScope) {
            target.apply {
                modifier
                    .align(AlignmentX.Start, AlignmentY.Top)
                    .margin(start = pxToDp(panePos.x).dp, top = pxToDp(panePos.y).dp)
                    .size(pxToDp(paneSize.x).dp, pxToDp(paneSize.y).dp)
            }
        }

        override fun UiScope.compose() = Box(Grow.Std, Grow.Std) {
            childPanes.removeAll { it.isEmpty }
            if (childPanes.isNotEmpty()) {
                val first = childPanes[0]
                if (first.position == DockPosition.East || first.position == DockPosition.West) {
                    composeChildrenHorizontally()
                } else {
                    composeChildrenVertically()
                }
            } else {
                CenterPane()
            }

            if (isDrawSlots.use()) {
                slots.forEach { it() }
            }
        }

        private fun UiScope.composeChildrenHorizontally() {
            modifier.layout(RowLayout)

            this@DockingPane[DockPosition.West]?.invoke()
            Column(Grow.Std, Grow.Std) {
                this@DockingPane[DockPosition.North]?.invoke()
                CenterPane()
                this@DockingPane[DockPosition.South]?.invoke()
            }
            this@DockingPane[DockPosition.East]?.invoke()
        }

        private fun UiScope.composeChildrenVertically() {
            modifier.layout(ColumnLayout)

            this@DockingPane[DockPosition.North]?.invoke()
            Row(Grow.Std, Grow.Std) {
                this@DockingPane[DockPosition.West]?.invoke()
                CenterPane()
                this@DockingPane[DockPosition.East]?.invoke()
            }
            this@DockingPane[DockPosition.South]?.invoke()
        }

        private fun UiScope.CenterPane() = Box(Grow.Std, Grow.Std) {
            modifier
                .onPositioned {
                    panePos.set(it.leftPx, it.topPx)
                    paneSize.set(it.widthPx, it.heightPx)
                    logD { "$path: $panePos x $paneSize" }
                }
        }

        operator fun contains(point: Vec2f): Boolean {
            return point.x >= panePos.x && point.x <= panePos.x + paneSize.x
                    && point.y >= panePos.y && point.y <= panePos.y + paneSize.y
        }

        fun getPaneAt(screenPos: Vec2f): DockingPane {
            return childPanes.find { it.contains(screenPos) }?.getPaneAt(screenPos) ?: this
        }

        fun getSlotAt(screenPos: Vec2f): DockingSlot? {
            return slots.find { screenPos in it }
        }

        operator fun get(position: DockPosition): DockingPane? {
            return if (position == DockPosition.Center) {
                this
            } else {
                childPanes.find { it.position == position }
            }
        }

        fun getOrCreatePaneAtPosition(position: DockPosition): DockingPane {
            return this[position] ?: DockingPane(this, position).also { childPanes += it }
        }

        fun undock(window: WindowScope) {
            dockedWindows -= window
            window.windowState.dockedTo.set(null)
        }

        fun dock(window: WindowScope) {
            val prevDockedTo = window.windowState.dockedTo.value
            if (prevDockedTo !== this) {
                prevDockedTo?.undock(window)

                dockedWindows += window
                window.windowState.dockedTo.set(this)
                logD { "Window \"${window.surface.name}\" docked to slot: $path" }
            }
        }
    }

    class DockingSlot(val position: DockPosition, val parentPane: DockingPane) : ComposableComponent {
        val isHovered = mutableStateOf(false)

        private val slotPos = MutableVec2f()
        private val slotSize = MutableVec2f()

        override fun UiScope.compose() {
            val isHv = isHovered.use()
            val maxW = (uiNode.widthPx * 0.25f).dp
            val maxH = (uiNode.heightPx * 0.25f).dp
            val hoverScale = if (isHv) 1.1f else 1f
            Box {
                when (position) {
                    DockPosition.North -> {
                        modifier.width(min(300.dp, maxW) * hoverScale)
                        modifier.height(min(100.dp, maxH) * hoverScale)
                    }
                    DockPosition.East -> {
                        modifier.width(min(100.dp, maxW) * hoverScale)
                        modifier.height(min(300.dp, maxH) * hoverScale)
                    }
                    DockPosition.South -> {
                        modifier.width(min(300.dp, maxW) * hoverScale)
                        modifier.height(min(100.dp, maxH) * hoverScale)
                    }
                    DockPosition.West -> {
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
                        slotPos.set(it.leftPx, it.topPx)
                        slotSize.set(it.widthPx, it.heightPx)
                    }
            }
        }

        operator fun contains(point: Vec2f): Boolean {
            return point.x >= slotPos.x && point.x <= slotPos.x + slotSize.x
                    && point.y >= slotPos.y && point.y <= slotPos.y + slotSize.y
        }

        fun dock(window: WindowScope) {
            parentPane.getOrCreatePaneAtPosition(position).dock(window)
        }
    }

    enum class DockPosition(val alignX: AlignmentX, val alignY: AlignmentY) {
        North(AlignmentX.Center, AlignmentY.Top),
        East(AlignmentX.End, AlignmentY.Center),
        South(AlignmentX.Center, AlignmentY.Bottom),
        West(AlignmentX.Start, AlignmentY.Center),
        Center(AlignmentX.Center, AlignmentY.Center)
    }
}