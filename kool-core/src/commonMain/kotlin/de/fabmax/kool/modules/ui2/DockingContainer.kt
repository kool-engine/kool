package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.min
import kotlin.math.abs

class DockingContainer(
    val dockingHost: DockingHost,
    var parent: DockingContainer?,
    val position: DockingHost.DockPosition,
    widthWeight: Float,
    heightWeight: Float
) : Composable {

    var depth = 0

    val dockedWindows = mutableStateListOf<WindowScope>()
    var childContainers: Pair<DockingContainer, DockingContainer>? = null
        private set

    val isLeaf: Boolean get() = childContainers == null
    val isEmpty: Boolean get() = isLeaf && dockedWindows.isEmpty()
    val canSplit: Boolean get() = depth < dockingHost.maxSplitDepth && !(isMergeIfHalfEmpty && isEmpty)
    val isSplitHorizontally: Boolean get() = childContainers?.first?.position == DockingHost.DockPosition.Start

    private val slots = DockingHost.DockPosition.values().map { DockingHost.DockingSlot(it, this) }
    val isDrawSlots = mutableStateOf(false)

    var isMergeIfHalfEmpty = false
    val widthWeight = mutableStateOf(widthWeight)
    val heightWeight = mutableStateOf(heightWeight)
    val boundsMinPx = MutableVec2f()
    val boundsMaxPx = MutableVec2f()
    val width: Float get() = boundsMaxPx.x - boundsMinPx.x
    val height: Float get() = boundsMaxPx.y - boundsMinPx.y

    val dockMarginStart = mutableStateOf(Dp.ZERO)
    val dockMarginEnd = mutableStateOf(Dp.ZERO)
    val dockMarginTop = mutableStateOf(Dp.ZERO)
    val dockMarginBottom = mutableStateOf(Dp.ZERO)

    var customCompositer: (UiScope.(DockingContainer) -> Unit)? = null

    operator fun contains(point: Vec2f): Boolean {
        return point.x >= boundsMinPx.x && point.x <= boundsMaxPx.x && point.y >= boundsMinPx.y && point.y <= boundsMaxPx.y
    }

    fun bringToTop(window: WindowScope) {
        dockedWindows.forEach { it.windowState.isVisible.set(it == window) }
    }

    fun isOnTop(window: WindowScope): Boolean {
        return window == getWindowOnTop()
    }

    fun getWindowOnTop(): WindowScope? {
        return dockedWindows.maxByOrNull { it.surface.lastInputTime }
    }

    fun undock(window: WindowScope) {
        dockedWindows -= window
        window.windowState.dockedTo.set(null)
        getWindowOnTop()?.let { bringToTop(it) }
        dockingHost.dockingListeners.forEach { it.onWindowUndocked(window, this) }
    }

    fun getNearestLeaf(): DockingContainer {
        val (a, b) = childContainers ?: return this
        val aLeaf = a.getNearestLeaf()
        val bLeaf = b.getNearestLeaf()
        return if (aLeaf.depth < bLeaf.depth) aLeaf else bLeaf
    }

    fun dock(window: WindowScope) {
        if (!isLeaf) {
            throw IllegalStateException("Windows can only be docked to leaf nodes")
        }
        dockedWindows += window
        window.apply {
            windowState.dockedTo.set(this@DockingContainer)
            windowState.setDockedWindowBounds(
                Dp.fromPx(boundsMinPx.x), Dp.fromPx(boundsMinPx.y),
                Dp.fromPx(width), Dp.fromPx(height),
            )
        }
        bringToTop(window)
        dockingHost.dockingListeners.forEach { it.onWindowDocked(window, this) }
    }

    fun getNodeContainingSplitEdgeAt(screenPos: Vec2f): DockingContainer? {
        val (a, b) = childContainers ?: return null
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
        val (a, b) = childContainers ?: return

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
            it.windowState.setDockedWindowBounds(x, y, w, h)
        }
    }

    override fun UiScope.compose() {
        if (customCompositer != null) {
            customCompositer?.invoke(this, this@DockingContainer)
        } else {
            composeContent(Grow(widthWeight.use()), Grow(heightWeight.use()))
        }
    }

    fun UiScope.composeContent(width: Dimension, height: Dimension) = Box(width, height) {
        modifier
            .margin(dockMarginStart.use(), dockMarginEnd.use(), dockMarginTop.use(), dockMarginBottom.use())
            .onPositioned {
                boundsMinPx.set(it.leftPx, it.topPx)
                boundsMaxPx.set(it.rightPx, it.bottomPx)
                updateChildWindowBounds()
            }

        childContainers?.let { (a, b) ->
            Box(Grow.Std, Grow.Std) {
                modifier.layout(if (isSplitHorizontally) RowLayout else ColumnLayout)
                a()
                b()
            }
        }

        if (isDrawSlots.use()) {
            if (canSplit) {
                slots.forEach { it() }
            } else {
                slots.first { it.position == DockingHost.DockPosition.Center }.invoke()
            }
        }
    }

    fun mergeEmptyChildren() {
        val (a, b) = childContainers ?: return
        a.mergeEmptyChildren()
        b.mergeEmptyChildren()

        if (isMergeIfHalfEmpty && (a.isEmpty || b.isEmpty)) {
            // method a: merge children if one of them is empty
            val nonEmpty = if (a.isEmpty) b else a
            childContainers = null

            dockedWindows += nonEmpty.dockedWindows
            dockedWindows.forEach { it.windowState.dockedTo.set(this) }

            childContainers = nonEmpty.childContainers
            childContainers?.let { (a, b) ->
                a.parent = this
                b.parent = this
            }
            dockingHost.dockingListeners.forEach { it.onDockingContainerMerged(this) }

        } else if (!isMergeIfHalfEmpty && (a.isEmpty && b.isEmpty)) {
            // method b: merge (i.e. remove) children only if both are empty
            // less layout changes than method a but gaps between nodes can appear
            childContainers = null
            dockingHost.dockingListeners.forEach { it.onDockingContainerMerged(this) }
        }
    }

    fun updateDepth() {
        depth = (parent?.depth ?: -1) + 1
        childContainers?.let { (a, b) ->
            a.updateDepth()
            b.updateDepth()
        }
    }

    fun xWeightByWidthPx(widthPx: Float, minWeight: Float = 0.2f, maxWeight: Float = 0.8f): Float {
        var w = (widthPx / width).clamp(minWeight, maxWeight)
        if (w.isNaN()) {
            w = 0.5f
        }
        return w
    }

    fun yWeightByHeightPx(heightPx: Float, minWeight: Float = 0.2f, maxWeight: Float = 0.8f): Float {
        var w = (heightPx / height).clamp(minWeight, maxWeight)
        if (w.isNaN()) {
            w = 0.5f
        }
        return w
    }

    fun split(insertPos: DockingHost.DockPosition, splitWeightX: Float, splitWeightY: Float): DockingContainer {
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
        val opposingPos: DockingHost.DockPosition
        when (insertPos) {
            DockingHost.DockPosition.Start -> {
                insertWidthW = splitWeightX
                opposingWidthW = 1f - insertWidthW
                opposingPos = DockingHost.DockPosition.End
                insertFirst = true
            }
            DockingHost.DockPosition.End -> {
                insertWidthW = splitWeightX
                opposingWidthW = 1f - insertWidthW
                opposingPos = DockingHost.DockPosition.Start
                insertFirst = false
            }
            DockingHost.DockPosition.Top -> {
                insertHeightW = splitWeightY
                opposingHeightW = 1f - insertHeightW
                opposingPos = DockingHost.DockPosition.Bottom
                insertFirst = true
            }
            DockingHost.DockPosition.Bottom -> {
                insertHeightW = splitWeightY
                opposingHeightW = 1f - insertHeightW
                opposingPos = DockingHost.DockPosition.Top
                insertFirst = false
            }
            DockingHost.DockPosition.Center -> return this
        }

        // weights become NaN when the container wasn't layouted yet and its width / height is still 0
        if (insertWidthW.isNaN()) insertWidthW = 1f
        if (insertHeightW.isNaN()) insertHeightW = 1f
        if (opposingWidthW.isNaN()) opposingWidthW = 1f
        if (opposingHeightW.isNaN()) opposingHeightW = 1f

        // add newly spawned, empty node
        val insertNode = DockingContainer(dockingHost, this, insertPos, insertWidthW, insertHeightW)

        // add split node on opposing side which will contain the windows previously docked in this node
        val splitNode = DockingContainer(dockingHost, this, opposingPos, opposingWidthW, opposingHeightW)
        splitNode.dockedWindows += dockedWindows
        dockedWindows.forEach { it.windowState.dockedTo.set(splitNode) }
        dockedWindows.clear()

        childContainers = if (insertFirst) {
            insertNode to splitNode
        } else {
            splitNode to insertNode
        }
        dockingHost.dockingListeners.forEach { it.onDockingContainerSplit(this) }

        return insertNode
    }

    fun getNodeAt(screenPos: Vec2f): DockingContainer? {
        if (isLeaf) {
            return this
        } else {
            childContainers?.let { (a, b) ->
                if (screenPos in a) return a.getNodeAt(screenPos)
                if (screenPos in b) return b.getNodeAt(screenPos)
            }
        }
        return null
    }

    fun getSlotAt(screenPos: Vec2f): DockingHost.DockingSlot? {
        return slots.find { screenPos in it }
    }
}