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
}

fun UiScope.Windowed(
    state: WindowState,
    title: String,
    titleBarColor: Color = colors.accentVariant,
    isMovable: Boolean = true,
    isVerticallyResizable: Boolean = true,
    isHorizontallyResizable: Boolean = true,
    minWidth: Dp = sizes.largeGap * 2f,
    maxWidth: Dp = 10_000f.dp,
    minHeight: Dp = sizes.largeGap * 2f,
    maxHeight: Dp = 10_000f.dp,
    isMinimizedToTitleBar: Boolean = false,
    onCloseClicked: ((PointerEvent) -> Unit)? = null,
    onMinimizeClicked: ((PointerEvent) -> Unit)? = null,
    onMaximizeClicked: ((PointerEvent) -> Unit)? = null,
    titleBarContent: UiScope.(String) -> Unit = {
        Text(it) {
            modifier
                .width(Grow.Std)
                .margin(horizontal = sizes.gap, vertical = sizes.smallGap * 0.5f)
                .textColor(colors.onAccent)
        }
    },
    block: UiScope.() -> Unit
) {
    modifier
        .width(state.width.use())
        .height(if (isMinimizedToTitleBar) WrapContent else state.height.use())
        .align(AlignmentX.Start, AlignmentY.Top)
        .margin(start = state.xDp.use(), top = state.yDp.use())
        .background(RoundRectBackground(colors.background, sizes.gap))
        .layout(ColumnLayout)

    if (isVerticallyResizable || isHorizontallyResizable) {
        ResizeHelper(
            state, this, isVerticallyResizable, isHorizontallyResizable,
            minWidth, maxWidth, minHeight, maxHeight
        )
    }

    Row(Grow.Std) {
        modifier
            .padding(start = sizes.gap)
            .background(TitleBarBackground(titleBarColor, sizes.gap.px, isMinimizedToTitleBar))

        if (isMovable) {
            modifier
                .onDragStart {
                    if (ResizeHelper.getBorder(uiNode, it.position, 4.dp.px) != 0) {
                        it.reject()
                    } else {
                        state.dragStartX = state.xDp.value.px
                        state.dragStartY = state.yDp.value.px
                    }
                }
                .onDrag {
                    state.xDp.set(pxToDp(state.dragStartX + it.pointer.dragDeltaX.toFloat()).dp)
                    state.yDp.set(pxToDp(state.dragStartY + it.pointer.dragDeltaY.toFloat()).dp)
                }
        }

        titleBarContent(title)
        onMaximizeClicked?.let { MaximizeButton(state, it) }
        onMinimizeClicked?.let { MinimizeButton(state, it) }
        onCloseClicked?.let { CloseButton(state, it) }
    }

    if (!isMinimizedToTitleBar) {
        Box {
            modifier
                .width(Grow.Std)
                .height(Grow.Std)

            block()
        }
    }
}

private class ResizeHelper(
    val state: WindowState,
    val uiScope: UiScope,
    val isVerticallyResizable: Boolean,
    val isHorizontallyResizable: Boolean,
    val minWidth: Dp,
    val maxWidth: Dp,
    val minHeight: Dp,
    val maxHeight: Dp
) {

    private val onHover: (PointerEvent) -> Unit = {
        uiScope.apply {
            if (!it.pointer.isDrag) {
                val borderFlags = getBorder(uiNode, it.position, 4.dp.px)
                setResizeCursor(isVerticallyResizable, isHorizontallyResizable, borderFlags, it.ctx)
            }
        }
    }

    private val onExit: (PointerEvent) -> Unit = {
        if (!it.pointer.isDrag) {
            it.ctx.inputMgr.cursorShape = InputManager.CursorShape.DEFAULT
        }
    }

    private val onDragStart: (PointerEvent) -> Unit = {
        uiScope.apply {
            val startPos = MutableVec2f(it.position)
            startPos.x -= it.pointer.dragDeltaX.toFloat()
            startPos.y -= it.pointer.dragDeltaY.toFloat()

            state.borderFlags = getBorder(uiNode, it.position, 4.dp.px)
            if (isVerticallyResizable && state.borderFlags and V_BORDER != 0) {
                it.ctx.inputMgr.cursorShape = InputManager.CursorShape.V_RESIZE
            } else if (isHorizontallyResizable && state.borderFlags and H_BORDER != 0) {
                it.ctx.inputMgr.cursorShape = InputManager.CursorShape.H_RESIZE
            } else {
                it.reject()
            }
            state.dragStartX = state.xDp.value.px
            state.dragStartY = state.yDp.value.px
            state.dragStartWidth = uiNode.widthPx
            state.dragStartHeight = uiNode.heightPx
        }
    }

    private val onDrag: (PointerEvent) -> Unit = {
        uiScope.apply {
            setResizeCursor(isVerticallyResizable, isHorizontallyResizable, state.borderFlags, it.ctx)

            if (state.borderFlags and H_BORDER != 0) {
                val dx = it.pointer.dragDeltaX.toFloat()
                if (state.borderFlags and RIGHT_BORDER != 0) {
                    state.width.set(clampWidthToDp(state.dragStartWidth + dx))
                } else if (state.borderFlags and LEFT_BORDER != 0) {
                    val w = clampWidthToDp(state.dragStartWidth - dx)
                    state.width.set(w)
                    state.xDp.set(pxToDp(state.dragStartX + state.dragStartWidth - w.px).dp)
                }
            }

            if (state.borderFlags and V_BORDER != 0) {
                val dy = it.pointer.dragDeltaY.toFloat()
                if (state.borderFlags and BOTTOM_BORDER != 0) {
                    state.height.set(clampHeightToDp(state.dragStartHeight + dy))
                } else if (state.borderFlags and TOP_BORDER != 0) {
                    val h = clampHeightToDp(state.dragStartHeight - dy)
                    state.height.set(h)
                    state.yDp.set(pxToDp(state.dragStartY + state.dragStartHeight - h.px).dp)
                }
            }
        }
    }

    private val onDragEnd: (PointerEvent) -> Unit = {
        it.ctx.inputMgr.cursorShape = InputManager.CursorShape.DEFAULT
    }

    init {
        uiScope.modifier
            .onHover(onHover)
            .onExit(onExit)
            .onDragStart(onDragStart)
            .onDrag(onDrag)
            .onDragEnd(onDragEnd)
    }

    private fun clampWidthToDp(widthPx: Float): Dp {
        return uiScope.run {
            pxToDp(min(maxWidth.px, max(minWidth.px, widthPx))).dp
        }
    }

    private fun clampHeightToDp(heightPx: Float): Dp {
        return uiScope.run {
            pxToDp(min(maxHeight.px, max(minHeight.px, heightPx))).dp
        }
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
        const val TOP_BORDER = 1
        const val BOTTOM_BORDER = 2
        const val LEFT_BORDER = 4
        const val RIGHT_BORDER = 8
        const val V_BORDER = TOP_BORDER or BOTTOM_BORDER
        const val H_BORDER = LEFT_BORDER or RIGHT_BORDER

        fun getBorder(node: UiNode, pos: Vec2f, borderWidth: Float): Int {
            var flags = 0
            if (pos.y < borderWidth) {
                flags = TOP_BORDER
            } else if (pos.y > node.heightPx - borderWidth) {
                flags = BOTTOM_BORDER
            }
            if (pos.x < borderWidth) {
                flags = flags or LEFT_BORDER
            } else if (pos.x > node.widthPx - borderWidth) {
                flags = flags or RIGHT_BORDER
            }
            return flags
        }
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
