package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color
import kotlin.math.max
import kotlin.math.min

open class WindowState {
    val xDp = mutableStateOf(Dp.ZERO)
    val yDp = mutableStateOf(Dp.ZERO)
    val width: MutableStateValue<Dimension> = mutableStateOf(WrapContent)
    val height: MutableStateValue<Dimension> = mutableStateOf(WrapContent)

    val closeButtonHovered = mutableStateOf(false)
    val minimizeButtonHovered = mutableStateOf(false)
    val maximizeButtonHovered = mutableStateOf(false)

    var borderFlags = 0
    var dragStartX = 0f
    var dragStartY = 0f
    var dragStartWidth = 0f
    var dragStartHeight = 0f

    val dockedTo = mutableStateOf<DockingHost.DockingPane?>(null)
}

interface WindowScope : UiScope {
    override val modifier: WindowModifier
    val windowState: WindowState
    val isDocked: Boolean get() = windowState.dockedTo.value != null

    fun getBorderFlags(localPosition: Vec2f, borderWidth: Dp): Int
}

open class WindowModifier(surface: UiSurface) : UiModifier(surface) {
    var titleBarColor: Color by property { it.colors.accentVariant }
    var isVerticallyResizable: Boolean by property(true)
    var isHorizontallyResizable: Boolean by property(true)
    var isMinimizedToTitle: Boolean by property(false)
    var minWidth: Dp by property { it.sizes.largeGap * 2f }
    var minHeight: Dp by property { it.sizes.largeGap * 2f }
    var maxWidth: Dp by property { Dp(10_000f) }
    var maxHeight: Dp by property { Dp(10_000f) }
    val onCloseClicked: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onMinimizeClicked: MutableList<(PointerEvent) -> Unit> by listProperty()
    val onMaximizeClicked: MutableList<(PointerEvent) -> Unit> by listProperty()
    var dockingHost: DockingHost? by property(null)
}

fun <T: WindowModifier> T.titleBarColor(color: Color): T { titleBarColor = color; return this }
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
fun <T: WindowModifier> T.onCloseClicked(block: (PointerEvent) -> Unit): T { onCloseClicked += block; return this }
fun <T: WindowModifier> T.onMinimizeClicked(block: (PointerEvent) -> Unit): T { onMinimizeClicked += block; return this }
fun <T: WindowModifier> T.onMaximizeClicked(block: (PointerEvent) -> Unit): T { onMaximizeClicked += block; return this }
fun <T: WindowModifier> T.dockingHost(dockingHost: DockingHost?): T { this.dockingHost = dockingHost; return this }

fun Window(
    state: WindowState,
    colors: Colors = Colors.darkColors(),
    sizes: Sizes = Sizes.medium(),
    name: String = "Window",
    content: WindowScope.() -> Unit
): UiSurface {
    val surface = UiSurface(colors, sizes, name)
    surface.content = {
        val window = uiNode.createChild(WindowNode::class, WindowNode.factory)
        window.state = state
        window.modifier
            .background(RoundRectBackground(colors.background, sizes.gap))
            .layout(ColumnLayout)

        // auto-register docking host if window was created in one
        (surface.parent as? DockingHost)?.let { window.modifier.dockingHost(it) }

        // compose user supplied window content
        window.content()

        // set window location and size according to window state
        val dock = state.dockedTo.use()
        if (dock != null) {
            // window is docked -> set position according to docking pane
            dock.setupDockPosition(window)
        } else {
            // floating window
            window.modifier
                .width(state.width.use())
                .height(if (window.modifier.isMinimizedToTitle) WrapContent else state.height.use())
                .align(AlignmentX.Start, AlignmentY.Top)
                .margin(start = state.xDp.use(), top = state.yDp.use())
        }

        // register resize hover and drag listeners if window is resizable
        if (window.modifier.isVerticallyResizable || window.modifier.isHorizontallyResizable) {
            window.modifier.hoverListener(window)
            window.modifier.dragListener(window)
        }
    }
    return surface
}

fun WindowScope.TitleBar(title: String, isDraggable: Boolean = true) {
    val windowModifier = modifier
    Row(Grow.Std) {
        modifier
            .padding(start = sizes.gap)
            .background(TitleBarBackground(windowModifier.titleBarColor, sizes.gap.px, windowModifier.isMinimizedToTitle))

        if (isDraggable) {
            modifier
                .onDragStart {
                    if (getBorderFlags(it.position, 4.dp) != 0) {
                        it.reject()
                    } else {
                        if (isDocked) {
                            // relocate window position if window was docked, such that cursor is centered over title bar
                            val widthPx = (windowState.width.value as? Dp)?.px ?: 100f
                            windowState.xDp.set(pxToDp(it.screenPosition.x - widthPx * 0.5f).dp)
                            windowState.yDp.set(this@TitleBar.modifier.marginTop)
                        }
                        windowState.dragStartX = windowState.xDp.value.px
                        windowState.dragStartY = windowState.yDp.value.px
                        windowModifier.dockingHost?.onWindowMoveStart(this@TitleBar)
                    }
                }
                .onDrag {
                    windowState.xDp.set(pxToDp(windowState.dragStartX + it.pointer.dragDeltaX.toFloat()).dp)
                    windowState.yDp.set(pxToDp(windowState.dragStartY + it.pointer.dragDeltaY.toFloat()).dp)
                    windowModifier.dockingHost?.onWindowMove(it)
                }
                .onDragEnd {
                    windowModifier.dockingHost?.onWindowMoveEnd(this@TitleBar)
                }
        }

        Text(title) {
            modifier
                .width(Grow.Std)
                .margin(horizontal = sizes.gap, vertical = sizes.smallGap * 0.5f)
                .textColor(colors.onAccent)
        }

        if (windowModifier.onMaximizeClicked.isNotEmpty()) {
            MaximizeButton(windowState) { ev -> windowModifier.onMaximizeClicked.forEach { it(ev) } }
        }
        if (windowModifier.onMinimizeClicked.isNotEmpty()) {
            MinimizeButton(windowState) { ev -> windowModifier.onMinimizeClicked.forEach { it(ev) } }
        }
        if (windowModifier.onCloseClicked.isNotEmpty()) {
            CloseButton(windowState) { ev -> windowModifier.onCloseClicked.forEach { it(ev) } }
        }
    }
}

class WindowNode(parent:UiNode?, surface: UiSurface) : UiNode(parent, surface), WindowScope, Hoverable, Draggable {
    override val modifier = WindowModifier(surface)

    lateinit var state: WindowState
    override val windowState: WindowState
        get() = state

    override fun onHover(ev: PointerEvent) {
        if (!ev.pointer.isDrag) {
            val borderFlags = getBorderFlags(ev.position, 4.dp)
            setResizeCursor(modifier.isVerticallyResizable, modifier.isHorizontallyResizable, borderFlags, ev.ctx)
        }
    }

    override fun onExit(ev: PointerEvent) {
        if (!ev.pointer.isDrag) {
            ev.ctx.inputMgr.cursorShape = InputManager.CursorShape.DEFAULT
        }
    }

    override fun onDragStart(ev: PointerEvent) {
        val startPos = MutableVec2f(ev.position)
        startPos.x -= ev.pointer.dragDeltaX.toFloat()
        startPos.y -= ev.pointer.dragDeltaY.toFloat()

        state.borderFlags = getBorderFlags(ev.position, 4.dp)
        if (modifier.isVerticallyResizable && state.borderFlags and V_BORDER != 0) {
            ev.ctx.inputMgr.cursorShape = InputManager.CursorShape.V_RESIZE
        } else if (modifier.isHorizontallyResizable && state.borderFlags and H_BORDER != 0) {
            ev.ctx.inputMgr.cursorShape = InputManager.CursorShape.H_RESIZE
        } else {
            ev.reject()
        }
        state.dragStartX = state.xDp.value.px
        state.dragStartY = state.yDp.value.px
        state.dragStartWidth = uiNode.widthPx
        state.dragStartHeight = uiNode.heightPx
    }

    override fun onDrag(ev: PointerEvent) {
        setResizeCursor(modifier.isVerticallyResizable, modifier.isHorizontallyResizable, state.borderFlags, ev.ctx)

        if (state.borderFlags and H_BORDER != 0) {
            val dx = ev.pointer.dragDeltaX.toFloat()
            if (state.borderFlags and RIGHT_BORDER != 0) {
                state.width.set(clampWidthToDp(state.dragStartWidth + dx))
            } else if (state.borderFlags and LEFT_BORDER != 0) {
                val w = clampWidthToDp(state.dragStartWidth - dx)
                state.width.set(w)
                state.xDp.set(pxToDp(state.dragStartX + state.dragStartWidth - w.px).dp)
            }
        }

        if (state.borderFlags and V_BORDER != 0) {
            val dy = ev.pointer.dragDeltaY.toFloat()
            if (state.borderFlags and BOTTOM_BORDER != 0) {
                state.height.set(clampHeightToDp(state.dragStartHeight + dy))
            } else if (state.borderFlags and TOP_BORDER != 0) {
                val h = clampHeightToDp(state.dragStartHeight - dy)
                state.height.set(h)
                state.yDp.set(pxToDp(state.dragStartY + state.dragStartHeight - h.px).dp)
            }
        }
    }

    override fun onDragEnd(ev: PointerEvent) {
        ev.ctx.inputMgr.cursorShape = InputManager.CursorShape.DEFAULT
    }

    override fun getBorderFlags(localPosition: Vec2f, borderWidth: Dp): Int {
        val borderPx = borderWidth.px
        var flags = 0
        if (localPosition.y < borderPx) {
            flags = TOP_BORDER
        } else if (localPosition.y > heightPx - borderPx) {
            flags = BOTTOM_BORDER
        }
        if (localPosition.x < borderPx) {
            flags = flags or LEFT_BORDER
        } else if (localPosition.x > widthPx - borderPx) {
            flags = flags or RIGHT_BORDER
        }
        return flags
    }

    private fun clampWidthToDp(widthPx: Float): Dp {
        return pxToDp(min(modifier.maxWidth.px, max(modifier.minWidth.px, widthPx))).dp
    }

    private fun clampHeightToDp(heightPx: Float): Dp {
        return pxToDp(min(modifier.maxHeight.px, max(modifier.minHeight.px, heightPx))).dp
    }

    private fun setResizeCursor(isV: Boolean, isH: Boolean, borderFlags: Int, ctx: KoolContext) {
        if (isV && borderFlags and V_BORDER != 0) {
            ctx.inputMgr.cursorShape = InputManager.CursorShape.V_RESIZE
        } else if (isH && borderFlags and H_BORDER != 0) {
            ctx.inputMgr.cursorShape = InputManager.CursorShape.H_RESIZE
        } else {
            ctx.inputMgr.cursorShape = InputManager.CursorShape.DEFAULT
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> WindowNode = { parent, surface -> WindowNode(parent, surface) }

        const val TOP_BORDER = 1
        const val BOTTOM_BORDER = 2
        const val LEFT_BORDER = 4
        const val RIGHT_BORDER = 8
        const val V_BORDER = TOP_BORDER or BOTTOM_BORDER
        const val H_BORDER = LEFT_BORDER or RIGHT_BORDER
    }
}

class TitleBarBackground(val bgColor: Color, val cornerRadius: Float, val roundedBottom: Boolean) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) = node.run {
        if (roundedBottom) {
            getUiPrimitives().localRoundRect(0f, 0f, widthPx, heightPx, cornerRadius, bgColor)
        } else {
            getUiPrimitives().localRoundRect(0f, 0f, widthPx, heightPx + cornerRadius, cornerRadius, bgColor)
        }
    }
}

fun UiScope.TitleBarButton(
    hoverState: MutableStateValue<Boolean>,
    onClick: (PointerEvent) -> Unit,
    background: UiRenderer<UiNode>
) {
    Box {
        modifier
            .width(sizes.gap * 2f)
            .height(sizes.gap * 2f)
            .alignY(AlignmentY.Center)
            .margin(horizontal = sizes.gap)
            .padding(if (hoverState.use()) 0.dp else sizes.smallGap * 0.25f)
            .onEnter { hoverState.set(true) }
            .onExit { hoverState.set(false) }
            .onClick(onClick)
            .background(background)
    }
}

fun UiScope.CloseButton(state: WindowState, onClick: (PointerEvent) -> Unit) =
    TitleBarButton(state.closeButtonHovered, onClick, CloseButtonBackground)
fun UiScope.MinimizeButton(state: WindowState, onClick: (PointerEvent) -> Unit) =
    TitleBarButton(state.minimizeButtonHovered, onClick, MinimizeButtonBackground)
fun UiScope.MaximizeButton(state: WindowState, onClick: (PointerEvent) -> Unit) =
    TitleBarButton(state.maximizeButtonHovered, onClick, MaximizeButtonBackground)

object CloseButtonBackground : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) = node.run {
        val r = innerWidthPx * 0.5f
        getUiPrimitives().localCircle(widthPx * 0.5f, heightPx * 0.5f, r, colors.onAccent)
        getPlainBuilder().configured(colors.accent) {
            translate(widthPx * 0.5f, heightPx * 0.5f, 0f)
            rotate(45f, Vec3f.Z_AXIS)
            rect {
                size.set(r * 1.3f, r * 0.2f)
                origin.set(size.x * -0.5f, size.y * -0.5f, 0f)
            }
            rect {
                size.set(r * 0.2f, r * 1.3f)
                origin.set(size.x * -0.5f, size.y * -0.5f, 0f)
            }
        }
    }
}

object MinimizeButtonBackground : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) = node.run {
        val r = innerWidthPx * 0.5f
        val draw = getUiPrimitives()
        draw.localCircle(widthPx * 0.5f, heightPx * 0.5f, r, colors.onAccent)
        draw.localRect(widthPx * 0.5f - r * 0.6f, heightPx * 0.5f - r * 0.1f, r * 1.2f, r * 0.2f, colors.accent)
    }
}

object MaximizeButtonBackground : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) = node.run {
        val r = innerWidthPx * 0.5f
        val draw = getUiPrimitives()
        draw.localCircle(widthPx * 0.5f, heightPx * 0.5f, r, colors.onAccent)
        draw.localRectBorder(widthPx * 0.5f - r * 0.5f, heightPx * 0.5f - r * 0.5f, r, r, 1.5f.dp.px, colors.accent)
    }
}
