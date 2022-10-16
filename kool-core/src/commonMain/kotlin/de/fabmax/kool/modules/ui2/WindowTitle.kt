package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color

fun WindowScope.TitleBar(
    title: String = surface.name ?: "Window",
    isDraggable: Boolean = true,
    showTabsIfDocked: Boolean = true,
    hideTitleWhenTabbed: Boolean = true,
    onCloseAction: ((PointerEvent) -> Unit)? = null,
    onMinimizeAction: ((PointerEvent) -> Unit)? = null,
    onMaximizeAction: ((PointerEvent) -> Unit)? = null
) {
    val isTabbed = if (showTabsIfDocked) {
        DockingTabsBar()
    } else {
        false
    }

    if (!isTabbed || !hideTitleWhenTabbed) {
        val windowModifier = modifier
        Row(Grow.Std, height = sizes.gap * 3f) {
            val cornerR = if (isDocked) 0f else sizes.gap.px
            modifier
                .padding(horizontal = sizes.gap)
                .background(TitleBarBackground(windowModifier.titleBarColor, cornerR, windowModifier.isMinimizedToTitle))

            if (isDraggable) {
                modifier.dragListener(WindowMoveDragHandler(this@TitleBar))
            }

            Text(title) {
                modifier
                    .width(Grow.Std)
                    .margin(horizontal = sizes.gap, vertical = sizes.smallGap * 0.5f)
                    .textColor(colors.onSecondary)
                    .alignY(AlignmentY.Center)
            }

            onMinimizeAction?.let {
                MinimizeButton(windowState) { ev -> it(ev) }
            }
            onMaximizeAction?.let {
                MaximizeButton(windowState) { ev -> it(ev) }
            }
            onCloseAction?.let {
                CloseButton(windowState) { ev -> it(ev) }
            }
        }
    } else {
        // add an empty row to avoid a hard layout change when the title bar changes visibility
        Row {  }
    }
}

fun WindowScope.DockingTabsBar(isDraggable: Boolean = true): Boolean {
    val dockingContainer = windowState.dockedTo.use()

    if (dockingContainer != null && dockingContainer.dockedWindows.size > 1) {
        Row(width = Grow.Std, height = sizes.gap * 3f) {
            modifier.backgroundColor(colors.secondaryVariant.mix(Color.BLACK, 0.2f))

            dockingContainer.dockedWindows.use().forEachIndexed { i, wnd ->
                Box {
                    modifier
                        .margin(horizontal = sizes.smallGap)
                        .alignY(AlignmentY.Bottom)

                    Button(wnd.surface.name ?: "Window $i") {
                        modifier.onClick { wnd.surface.bringToTop() }

                        // set a bit different button style: click feedback is disabled (doesn't work with the way
                        // the tabs are switched)
                        // also we use a custom background to get a more "tabbie" look
                        val bgColor = if (isHovered) {
                            colors.secondary
                        } else {
                            colors.secondaryVariant.mix(colors.secondary, 0.25f)
                        }
                        modifier
                            .background(RectBackground(bgColor))
                            .isClickFeedback(false)
                    }

                    if (wnd == this@DockingTabsBar) {
                        // active tab indicator
                        Box(Grow.Std, sizes.borderWidth * 2f) {
                            modifier
                                .backgroundColor(if (windowState.isFocused.use()) colors.primary else colors.primaryVariant)
                                .alignY(AlignmentY.Bottom)
                        }
                        if (isDraggable) {
                            modifier.dragListener(WindowMoveDragHandler(this@DockingTabsBar))
                        }
                    }
                }
            }
        }
        return true
    } else {
        // add an empty row to avoid a hard layout change when the tab row changes visibility
        Row {  }
        return false
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
            .margin(horizontal = sizes.smallGap)
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
        getUiPrimitives().localCircle(widthPx * 0.5f, heightPx * 0.5f, r, colors.background)
        getPlainBuilder().configured(colors.secondary) {
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
        draw.localCircle(widthPx * 0.5f, heightPx * 0.5f, r, colors.background)
        draw.localRect(widthPx * 0.5f - r * 0.6f, heightPx * 0.5f - r * 0.1f, r * 1.2f, r * 0.2f, colors.secondary)
    }
}

object MaximizeButtonBackground : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) = node.run {
        val r = innerWidthPx * 0.5f
        val draw = getUiPrimitives()
        draw.localCircle(widthPx * 0.5f, heightPx * 0.5f, r, colors.background)
        draw.localRectBorder(widthPx * 0.5f - r * 0.5f, heightPx * 0.5f - r * 0.5f, r, r, 1.5f.dp.px, colors.secondary)
    }
}
