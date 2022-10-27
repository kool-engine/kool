package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.min
import de.fabmax.kool.util.Time
import kotlin.math.abs
import kotlin.math.max

class DockingContainer(
    val dockingHost: DockingHost,
    var parent: DockingContainer?,
    val position: DockingHost.DockPosition,
    initWidth: Dimension,
    initHeight: Dimension
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
    val width = mutableStateOf(initWidth)
    val height = mutableStateOf(initHeight)
    val boundsMinPx = MutableVec2f()
    val boundsMaxPx = MutableVec2f()
    val widthPx: Float get() = boundsMaxPx.x - boundsMinPx.x
    val heightPx: Float get() = boundsMaxPx.y - boundsMinPx.y

    val dockMarginStart = mutableStateOf(Dp.ZERO)
    val dockMarginEnd = mutableStateOf(Dp.ZERO)
    val dockMarginTop = mutableStateOf(Dp.ZERO)
    val dockMarginBottom = mutableStateOf(Dp.ZERO)

    var customCompositer: (UiScope.(DockingContainer) -> Unit)? = null

    operator fun contains(point: Vec2f): Boolean {
        return point.x >= boundsMinPx.x && point.x <= boundsMaxPx.x && point.y >= boundsMinPx.y && point.y <= boundsMaxPx.y
    }

    fun bringToTop(window: WindowScope) {
        val maxInputTime = max(Time.gameTime, dockedWindows.maxOf { it.surface.lastInputTime } + 0.001)
        window.surface.lastInputTime = maxInputTime
        dockedWindows.forEach { it.windowState.isVisible.set(it == window) }
    }

    fun isOnTop(window: WindowScope): Boolean {
        return window == getWindowOnTop()
    }

    fun getWindowOnTop(): WindowScope? {
        return dockedWindows.maxByOrNull { it.surface.lastInputTime }
    }

    fun getPath(): List<Pair<DockingHost.DockPosition, Dimension>> {
        val result = mutableListOf<Pair<DockingHost.DockPosition, Dimension>>()

        var it: DockingContainer? = this
        while (it != null) {
            val dim = if (position.isHorizontal) width.value else height.value
            result += position to dim
            it = it.parent
        }
        result.reverse()
        return result
    }

    fun undock(window: WindowScope) {
        dockedWindows -= window
        window.windowState.dockedTo.set(null)
        getWindowOnTop()?.let { bringToTop(it) }
        dockingHost.dockingListeners.forEach { it.onWindowUndocked(window, this) }
        parent?.mergeEmptyChildren()
        dockingHost.dockingSurface.triggerUpdate()
    }

    fun getNearestLeaf(): DockingContainer {
        val (a, b) = childContainers ?: return this
        val aLeaf = a.getNearestLeaf()
        val bLeaf = b.getNearestLeaf()
        return if (aLeaf.depth < bLeaf.depth) aLeaf else bLeaf
    }

    fun dock(window: WindowScope, bringToTop: Boolean) {
        if (!isLeaf) {
            throw IllegalStateException("Windows can only be docked to leaf nodes")
        }
        dockedWindows += window
        window.apply {
            windowState.dockedTo.set(this@DockingContainer)
            windowState.setDockedWindowBounds(
                Dp.fromPx(boundsMinPx.x), Dp.fromPx(boundsMinPx.y),
                Dp.fromPx(widthPx), Dp.fromPx(heightPx),
            )
        }
        if (bringToTop) {
            bringToTop(window)
        } else {
            // make sure only top window is visible, this might be the docked window even if bring to top is false
            getWindowOnTop()?.let { bringToTop(it) }
        }
        dockingHost.dockingListeners.forEach { it.onWindowDocked(window, this) }
        dockingHost.dockingSurface.triggerUpdate()
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
            val minRemaining = min(widthPx * 0.5f, Dp(8f).px)
            val splitX = (screenPos.x - boundsMinPx.x).clamp(minRemaining, widthPx - minRemaining)
            val weightA = (splitX / widthPx)
            if (a.width.value is Grow && b.width.value is Grow) {
                a.width.set(Grow(weightA))
                b.width.set(Grow(1f - weightA))

            } else if (b.width.value is Grow) {
                // set width of a to absolute dp value, b remains grow
                a.width.set(Dp.fromPx(splitX))
                b.width.set(Grow.Std)

            } else {
                // set width of b to absolute dp value, a is forced to grow
                a.width.set(Grow.Std)
                b.width.set(Dp.fromPx(widthPx - splitX))
            }

        } else {
            val minRemaining = min(heightPx * 0.5f, Dp(50f).px)
            val splitY = (screenPos.y - boundsMinPx.y).clamp(minRemaining, heightPx - minRemaining)
            val weightA = (splitY / heightPx)
            if (a.height.value is Grow && b.height.value is Grow) {
                a.height.set(Grow(weightA))
                b.height.set(Grow(1f - weightA))

            } else if (b.height.value is Grow) {
                // set height of a to absolute dp value, b remains grow
                a.height.set(Dp.fromPx(splitY))
                b.height.set(Grow.Std)

            } else {
                // set height of b to absolute dp value, a is forced to grow
                a.height.set(Grow.Std)
                b.height.set(Dp.fromPx(widthPx - splitY))
            }
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
            composeContent(width.use(), height.use())
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
        var w = (widthPx / this.widthPx).clamp(minWeight, maxWeight)
        if (w.isNaN()) {
            w = 0.5f
        }
        return w
    }

    fun yWeightByHeightPx(heightPx: Float, minWeight: Float = 0.2f, maxWeight: Float = 0.8f): Float {
        var w = (heightPx / this.heightPx).clamp(minWeight, maxWeight)
        if (w.isNaN()) {
            w = 0.5f
        }
        return w
    }

    private fun getOpposingDimension(insertDim: Dimension): Dimension {
        return when (insertDim) {
            FitContent -> Grow.Std
            is Dp -> Grow.Std
            is Grow -> Grow((1f - insertDim.weight).clamp(0.05f, 0.95f))
        }
    }

    fun split(insertPos: DockingHost.DockPosition, insertDim: Dimension): DockingContainer {
        if (!isLeaf) {
            throw IllegalStateException("Only leaf nodes can be split")
        }
        if (!canSplit) {
            return this
        }

        var insertWidth: Dimension = Grow.Std
        var insertHeight: Dimension = Grow.Std
        var opposingWidth: Dimension = Grow.Std
        var opposingHeight: Dimension = Grow.Std
        val insertFirst: Boolean
        val opposingPos: DockingHost.DockPosition
        when (insertPos) {
            DockingHost.DockPosition.Start -> {
                insertWidth = insertDim
                opposingWidth = getOpposingDimension(insertDim)
                opposingPos = DockingHost.DockPosition.End
                insertFirst = true
            }
            DockingHost.DockPosition.End -> {
                insertWidth = insertDim
                opposingWidth = getOpposingDimension(insertDim)
                opposingPos = DockingHost.DockPosition.Start
                insertFirst = false
            }
            DockingHost.DockPosition.Top -> {
                insertHeight = insertDim
                opposingHeight = getOpposingDimension(insertDim)
                opposingPos = DockingHost.DockPosition.Bottom
                insertFirst = true
            }
            DockingHost.DockPosition.Bottom -> {
                insertHeight = insertDim
                opposingHeight = getOpposingDimension(insertDim)
                opposingPos = DockingHost.DockPosition.Top
                insertFirst = false
            }
            DockingHost.DockPosition.Center -> return this
        }

        // add newly spawned, empty node
        val insertNode = DockingContainer(dockingHost, this, insertPos, insertWidth, insertHeight)

        // add split node on opposing side which will contain the windows previously docked in this node
        val splitNode = DockingContainer(dockingHost, this, opposingPos, opposingWidth, opposingHeight)
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