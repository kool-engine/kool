package de.fabmax.kool.modules.ui2

import de.fabmax.kool.input.CursorShape
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

open class WindowState {
    val x = mutableStateOf(Dp.ZERO)
    val y = mutableStateOf(Dp.ZERO)
    val width: MutableStateValue<Dimension> = mutableStateOf(FitContent)
    val height: MutableStateValue<Dimension> = mutableStateOf(FitContent)

    val floatingAlignmentX = mutableStateOf(AlignmentX.Start)
    val floatingAlignmentY = mutableStateOf(AlignmentY.Top)

    val widthDocked: MutableStateValue<Dp> = mutableStateOf(Dp.ZERO)
    val heightDocked: MutableStateValue<Dp> = mutableStateOf(Dp.ZERO)

    var preferredDockingWidth: Dimension? = null
    var preferredDockingHeight: Dimension? = null

    var borderFlags = 0
    var isDrag = false
    var dragStartX = 0f
    var dragStartY = 0f
    var dragStartWidth = 0f
    var dragStartHeight = 0f

    val isVisible = mutableStateOf(true)
    val isFocused = mutableStateOf(true)
    val dockedTo = mutableStateOf<DockingContainer?>(null)

    fun setWindowLocation(x: Dp, y: Dp, alignX: AlignmentX = AlignmentX.Start, alignY: AlignmentY = AlignmentY.Top) {
        this.x.set(x)
        this.y.set(y)
        this.floatingAlignmentX.set(alignX)
        this.floatingAlignmentY.set(alignY)
    }

    fun setWindowSize(width: Dimension, height: Dimension) {
        this.width.set(width)
        this.height.set(height)
    }

    fun setWindowBounds(x: Dp, y: Dp, width: Dimension, height: Dimension) {
        setWindowLocation(x, y)
        setWindowSize(width, height)
    }

    fun setDockedWindowBounds(x: Dp, y: Dp, width: Dp, height: Dp) {
        setWindowLocation(x, y)
        widthDocked.set(width)
        heightDocked.set(height)
    }
}

interface WindowScope : UiScope {
    override val modifier: WindowModifier

    val windowState: WindowState
    val dockingHost: DockingHost? get() = windowState.dockedTo.value?.dockingHost
    val isDocked: Boolean get() = windowState.dockedTo.value != null

    fun getResizeBorderFlags(localPosition: Vec2f): Int
}

open class WindowModifier(surface: UiSurface) : UiModifier(surface) {
    var titleBarColor: Color by property { it.colors.secondaryVariant }
    var borderColor: Color? by property { it.colors.secondaryVariantAlpha(0.3f) }
    var backgroundColor: Color? by property { it.colors.background }
    var isVerticallyResizable: Boolean by property(true)
    var isHorizontallyResizable: Boolean by property(true)
    var isMinimizedToTitle: Boolean by property(false)
    var minWidth: Dp by property { it.sizes.largeGap * 2f }
    var minHeight: Dp by property { it.sizes.largeGap * 2f }
    var maxWidth: Dp by property { Dp(10_000f) }
    var maxHeight: Dp by property { Dp(10_000f) }
    var dockingHost: DockingHost? by property(null)
}

fun <T: WindowModifier> T.titleBarColor(color: Color): T { titleBarColor = color; return this }
fun <T: WindowModifier> T.borderColor(color: Color?): T { borderColor = color; return this }
fun <T: WindowModifier> T.backgroundColor(color: Color?): T { backgroundColor = color; return this }
fun <T: WindowModifier> T.isResizable(horizontally: Boolean = isHorizontallyResizable, vertically: Boolean = isVerticallyResizable): T {
    isHorizontallyResizable = horizontally
    isVerticallyResizable = vertically
    return this
}
fun <T: WindowModifier> T.isMinimizedToTitle(flag: Boolean): T { isMinimizedToTitle = flag; return this }
fun <T: WindowModifier> T.minSize(width: Dp = minWidth, height: Dp = minHeight): T {
    minWidth = width
    minHeight = height
    return this
}
fun <T: WindowModifier> T.maxSize(width: Dp = maxWidth, height: Dp = maxHeight): T {
    maxWidth = width
    maxHeight = height
    return this
}
fun <T: WindowModifier> T.dockingHost(dockingHost: DockingHost?): T { this.dockingHost = dockingHost; return this }

fun Window(
    state: WindowState,
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium,
    name: String = "Window",
    block: WindowScope.() -> Unit
): UiSurface {
    val surface = UiSurface(colors, sizes, name)

    // create the initial empty window scope
    surface.windowScope = surface.viewport.createChild(WindowNode::class, WindowNode.factory).apply { this.state = state }

    surface.content = {
        val window = uiNode.createChild(WindowNode::class, WindowNode.factory)
        window.state = state
        window.modifier.layout(ColumnLayout)

        surface.windowScope = window

        if (state.isVisible.use()) {
            if (state.isFocused.use()) {
                window.modifier
                    .titleBarColor(this.colors.secondary)
                    .borderColor(this.colors.secondaryAlpha(0.3f))
            }

            // auto-register docking host if window was created in one
            (surface.parent as? DockingHost)?.let { window.modifier.dockingHost(it) }

            // compose user supplied window content
            window.block()

            window.modifier.backgroundColor?.let { bgColor ->
                if (window.isDocked) {
                    window.modifier.background(RectBackground(bgColor))
                } else {
                    window.modifier.background(RoundRectBackground(bgColor, this.sizes.gap))
                }
            }
            window.modifier.borderColor?.let {
                if (window.isDocked) {
                    window.modifier.border(RectBorder(it, Dp.fromPx(1f)))
                } else {
                    window.modifier.border(RoundRectBorder(it, this.sizes.gap, Dp.fromPx(1f)))
                }
            }

            // set window location and size according to window state
            if (window.isDocked) {
                window.modifier
                    .width(state.widthDocked.use())
                    .height(state.heightDocked.use())
                    .align(AlignmentX.Start, AlignmentY.Top)
                    .margin(start = state.x.use(), top = state.y.use())
            } else {
                window.modifier
                    .width(state.width.use())
                    .height(if (window.modifier.isMinimizedToTitle) FitContent else state.height.use())
                    .align(state.floatingAlignmentX.use(), state.floatingAlignmentY.use())

                if (state.floatingAlignmentX.value == AlignmentX.End) {
                    window.modifier.margin(end = state.x.use())
                } else {
                    window.modifier.margin(start = state.x.use())
                }
                if (state.floatingAlignmentY.value == AlignmentY.Bottom) {
                    window.modifier.margin(bottom = state.y.use())
                } else {
                    window.modifier.margin(top = state.y.use())
                }
            }

            // register resize hover and drag listeners if window is resizable
            if (window.modifier.isVerticallyResizable || window.modifier.isHorizontallyResizable) {
                window.modifier.hoverListener(window)
                window.modifier.dragListener(window)
            }
            // register window default click listener: does nothing but consume unused click events inside the
            // window, so that the window gains focus within a DockingHost
            if (window.modifier.background != null) {
                window.modifier.onClick(window)
            }
        }
    }
    return surface
}

class WindowMoveDragHandler(val window: WindowScope) : Draggable {
    override fun onDragStart(ev: PointerEvent) {
        with(window) {
            if (getResizeBorderFlags(ev.position) != 0) {
                ev.reject()
            }
        }
    }

    override fun onDrag(ev: PointerEvent) {
        with(window) {
            val mvX = ev.pointer.dragDeltaX.toFloat()
            val mvY = ev.pointer.dragDeltaY.toFloat()

            if (!windowState.isDrag) {
                val dist = sqrt(mvX * mvX + mvY * mvY)
                if (dist > PointerInput.MAX_CLICK_MOVE_PX) {
                    windowState.isDrag = true
                    windowState.dragStartX = window.uiNode.leftPx
                    windowState.dragStartY = window.uiNode.topPx
                    modifier.dockingHost?.onWindowMoveStart(ev, this)
                }
            } else {
                if (ev.screenPosition.x > window.uiNode.rightPx) {
                    windowState.dragStartX = ev.screenPosition.x - window.uiNode.widthPx * 0.5f - mvX
                }
                windowState.setWindowLocation(
                    Dp.fromPx(windowState.dragStartX + mvX),
                    Dp.fromPx(windowState.dragStartY + mvY)
                )

                modifier.dockingHost?.onWindowMove(ev, this)
            }
            Unit
        }
    }

    override fun onDragEnd(ev: PointerEvent) {
        window.modifier.dockingHost?.onWindowMoveEnd(ev, window)
        window.windowState.isDrag = false
    }
}

class WindowNode(parent:UiNode?, surface: UiSurface) : UiNode(parent, surface), WindowScope, Clickable, Draggable, Hoverable {
    override val modifier = WindowModifier(surface)

    lateinit var state: WindowState
    override val windowState: WindowState
        get() = state

    private var resizingDockingNode: DockingContainer? = null

    override fun onClick(ev: PointerEvent) {
        // default window click handler is empty, but it consumes the click event so that the surface input time
        // is updated when the used clicks into the window. This way the window gains focus in case a DockingHost
        // is preset
    }

    override fun onEnter(ev: PointerEvent) = onHover(ev)

    override fun onHover(ev: PointerEvent) {
        val borderFlags = getResizeBorderFlags(ev.position)
        setResizeCursor(borderFlags)
        if (borderFlags == 0) {
            ev.reject()
        }
    }

    override fun onDragStart(ev: PointerEvent) {
        val startPos = MutableVec2f(ev.position)
        startPos.x -= ev.pointer.dragDeltaX.toFloat()
        startPos.y -= ev.pointer.dragDeltaY.toFloat()

        state.borderFlags = getResizeBorderFlags(ev.position)
        if (state.borderFlags and V_BORDER != 0) {
            PointerInput.cursorShape = CursorShape.V_RESIZE
        } else if (state.borderFlags and H_BORDER != 0) {
            PointerInput.cursorShape = CursorShape.H_RESIZE
        } else {
            ev.reject()
        }

        if (isDocked) {
            resizingDockingNode = dockingHost?.getNodeContainingSplitEdgeAt(ev.screenPosition)
        }

        state.dragStartX = state.x.value.px
        state.dragStartY = state.y.value.px
        state.dragStartWidth = uiNode.widthPx
        state.dragStartHeight = uiNode.heightPx
    }

    override fun onDrag(ev: PointerEvent) {
        setResizeCursor(state.borderFlags)

        if (isDocked) {
            resizingDockingNode?.moveSplitEdgeTo(ev.screenPosition)

        } else {
            if (state.borderFlags and H_BORDER != 0) {
                val dx = ev.pointer.dragDeltaX.toFloat()
                if (state.borderFlags and RIGHT_BORDER != 0) {
                    state.width.set(clampWidthToDp(state.dragStartWidth + dx))
                } else if (state.borderFlags and LEFT_BORDER != 0) {
                    val w = clampWidthToDp(state.dragStartWidth - dx)
                    state.width.set(w)
                    state.x.set(Dp.fromPx(state.dragStartX + state.dragStartWidth - w.px))
                }
            }

            if (state.borderFlags and V_BORDER != 0) {
                val dy = ev.pointer.dragDeltaY.toFloat()
                if (state.borderFlags and BOTTOM_BORDER != 0) {
                    state.height.set(clampHeightToDp(state.dragStartHeight + dy))
                } else if (state.borderFlags and TOP_BORDER != 0) {
                    val h = clampHeightToDp(state.dragStartHeight - dy)
                    state.height.set(h)
                    state.y.set(Dp.fromPx(state.dragStartY + state.dragStartHeight - h.px))
                }
            }
        }
    }

    override fun onDragEnd(ev: PointerEvent) {
        resizingDockingNode = null
    }

    override fun getResizeBorderFlags(localPosition: Vec2f): Int {
        val borderPx = RESIZE_BORDER_WIDTH.px
        var flags = 0
        if (modifier.isVerticallyResizable) {
            if (localPosition.y < borderPx) {
                flags = TOP_BORDER
            } else if (localPosition.y > heightPx - borderPx) {
                flags = BOTTOM_BORDER
            }
        }
        if (modifier.isHorizontallyResizable) {
            if (localPosition.x < borderPx) {
                flags = flags or LEFT_BORDER
            } else if (localPosition.x > widthPx - borderPx) {
                flags = flags or RIGHT_BORDER
            }
        }

        if (flags != 0 && dockingHost?.isResizableBorder(toScreen(localPosition)) == false) {
            // window is docked and hovered edge is non-resizable
            flags = 0
        }

        return flags
    }

    private fun clampWidthToDp(widthPx: Float): Dp {
        return Dp.fromPx(min(modifier.maxWidth.px, max(modifier.minWidth.px, widthPx)))
    }

    private fun clampHeightToDp(heightPx: Float): Dp {
        return Dp.fromPx(min(modifier.maxHeight.px, max(modifier.minHeight.px, heightPx)))
    }

    private fun setResizeCursor(borderFlags: Int) {
        if (borderFlags and V_BORDER != 0) {
            PointerInput.cursorShape = CursorShape.V_RESIZE
        } else if (borderFlags and H_BORDER != 0) {
            PointerInput.cursorShape = CursorShape.H_RESIZE
        } else {
            PointerInput.cursorShape = CursorShape.DEFAULT
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> WindowNode = { parent, surface -> WindowNode(parent, surface) }

        val RESIZE_BORDER_WIDTH = Dp(6f)

        const val TOP_BORDER = 1
        const val BOTTOM_BORDER = 2
        const val LEFT_BORDER = 4
        const val RIGHT_BORDER = 8
        const val V_BORDER = TOP_BORDER or BOTTOM_BORDER
        const val H_BORDER = LEFT_BORDER or RIGHT_BORDER
    }
}
