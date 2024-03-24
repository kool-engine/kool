package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.util.Color

fun UiScope.TitleBar(
    windowDockable: UiDockable,
    title: String = windowDockable.name,
    focusedBackgroundColor: Color = colors.secondary,
    unfocusedBackgroundColor: Color = colors.secondaryVariant,
    focusedTextColor: Color = colors.onSecondary,
    unfocusedTextColor: Color = colors.onSecondary,
    isDraggable: Boolean = true,
    isMinimizedToTitle: Boolean = false,
    showTabsIfDocked: Boolean = true,
    hideTitleWhenTabbed: Boolean = true,
    onCloseAction: ((PointerEvent) -> Unit)? = null,
    onMinimizeAction: ((PointerEvent) -> Unit)? = null,
    onMaximizeAction: ((PointerEvent) -> Unit)? = null,
    scopeName: String? = null
) {
    val isTabbed = if (showTabsIfDocked) {
        DockingTabsBar(windowDockable, onCloseAction = onCloseAction, scopeName = scopeName)
    } else {
        false
    }

    if (!isTabbed || !hideTitleWhenTabbed) {
        Row(Grow.Std, height = sizes.gap * 3f, scopeName = scopeName) {
            val color = if (surface.isFocused.use()) focusedBackgroundColor else unfocusedBackgroundColor
            val cornerR = if (windowDockable.isDocked.use()) 0f else sizes.gap.px
            modifier
                .padding(horizontal = sizes.gap)
                .background(TitleBarBackground(color, cornerR, isMinimizedToTitle))

            if (isDraggable) {
                with(windowDockable) {
                    registerDragCallbacks()
                }
            }

            Text(title) {
                modifier
                    .width(Grow.Std)
                    .margin(horizontal = sizes.gap, vertical = sizes.smallGap * 0.5f)
                    .textColor(if (surface.isFocused.use()) focusedTextColor else unfocusedTextColor)
                    .alignY(AlignmentY.Center)
            }

            onMinimizeAction?.let {
                MinimizeButton { ev -> it(ev) }
            }
            onMaximizeAction?.let {
                MaximizeButton { ev -> it(ev) }
            }
            onCloseAction?.let {
                CloseButton { ev -> it(ev) }
            }
        }
    } else {
        // add an empty row to avoid a hard layout change when the title bar changes visibility
        Row {  }
    }
}

fun UiScope.DockingTabsBar(
    windowDockable: UiDockable,
    isDragToUndock: Boolean = true,
    onCloseAction: ((PointerEvent) -> Unit)? = null,
    scopeName: String? = null
): Boolean {
    val dockNode = windowDockable.dockedTo.use()
    val nodeCount = dockNode?.dockedItems?.use()?.count { !it.isHidden } ?: 0

    if (dockNode != null && nodeCount > 1) {
        Row(width = Grow.Std, height = sizes.gap * 3f, scopeName = scopeName) {
            modifier.backgroundColor(colors.secondaryVariant.mix(Color.BLACK, 0.2f))

            dockNode.dockedItems.filter { !it.isHidden }.forEach { item ->
                Box {
                    modifier
                        .margin(horizontal = sizes.smallGap)
                        .alignY(AlignmentY.Bottom)

                    Button(item.name) {
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
                            .textAlignX(AlignmentX.Start)
                            .onClick { dockNode.bringToTop(item) }

                        if (item == this@DockingTabsBar && onCloseAction != null) {
                            modifier
                                .text(modifier.text + "       ")
                                .padding(end = 0.dp)

                            CloseButton(
                                buttonMod = {
                                    it
                                        .align(AlignmentX.End, AlignmentY.Top)
                                        .margin(top = sizes.smallGap, end = sizes.smallGap)
                                        .width(sizes.gap * 1.5f)
                                        .height(sizes.gap * 1.5f)
                                }
                            ) { ev -> onCloseAction(ev) }
                        }
                    }

                    if (item == windowDockable) {
                        // active tab indicator
                        Box(Grow.Std, sizes.borderWidth * 2f) {
                            modifier
                                .backgroundColor(if (surface.isFocused.use()) colors.primary else colors.primaryVariant)
                                .alignY(AlignmentY.Bottom)
                        }
                        if (isDragToUndock) {
                            with(windowDockable) {
                                registerDragCallbacks(false)
                            }
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
    onClick: (PointerEvent) -> Unit,
    background: TitleButtonBg,
    buttonMod: ((ButtonModifier) -> Unit)?
) {
    Button {
        background.isHovered = isHovered
        modifier
            .width(sizes.gap * 2f)
            .height(sizes.gap * 2f)
            .alignY(AlignmentY.Center)
            .margin(horizontal = sizes.smallGap)
            .padding(if (isHovered) 0.dp else sizes.smallGap * 0.25f)
            .isClickFeedback(false)
            .onClick(onClick)
            .background(background)

        buttonMod?.invoke(modifier)
    }
}

fun UiScope.CloseButton(
    foreground: Color = colors.secondary,
    foregroundHover: Color = colors.primary,
    background: Color = colors.background,
    backgroundHover: Color = colors.backgroundVariant,
    buttonMod: ((ButtonModifier) -> Unit)? = null,
    onClick: (PointerEvent) -> Unit) = TitleBarButton(onClick, CloseButtonBackground(foreground, foregroundHover, background, backgroundHover), buttonMod)
fun UiScope.MinimizeButton(
    foreground: Color = colors.secondary,
    foregroundHover: Color = colors.primary,
    background: Color = colors.background,
    backgroundHover: Color = colors.backgroundVariant,
    buttonMod: ((ButtonModifier) -> Unit)? = null,
    onClick: (PointerEvent) -> Unit) = TitleBarButton(onClick, MinimizeButtonBackground(foreground, foregroundHover, background, backgroundHover), buttonMod)
fun UiScope.MaximizeButton(
    foreground: Color = colors.secondary,
    foregroundHover: Color = colors.primary,
    background: Color = colors.background,
    backgroundHover: Color = colors.backgroundVariant,
    buttonMod: ((ButtonModifier) -> Unit)? = null,
    onClick: (PointerEvent) -> Unit) = TitleBarButton(onClick, MaximizeButtonBackground(foreground, foregroundHover, background, backgroundHover), buttonMod)

abstract class TitleButtonBg : UiRenderer<UiNode> {
    var isHovered = false
}

class CloseButtonBackground(
    private val foreground: Color,
    private val foregroundHover: Color,
    private val background: Color,
    private val backgroundHover: Color,
) : TitleButtonBg() {
    override fun renderUi(node: UiNode) = node.run {
        val r = innerWidthPx * 0.5f
        val bgColor = if (isHovered) backgroundHover else background
        val fgColor = if (isHovered) foregroundHover else foreground
        getUiPrimitives().localCircle(widthPx * 0.5f, heightPx * 0.5f, r, bgColor)
        getPlainBuilder().configured(fgColor) {
            translate(widthPx * 0.5f, heightPx * 0.5f, 0f)
            rotate(45f.deg, Vec3f.Z_AXIS)
            rect {
                size.set(r * 1.3f, r * 0.2f)
            }
            rect {
                size.set(r * 0.2f, r * 1.3f)
            }
        }
    }
}

class MinimizeButtonBackground(
    private val foreground: Color,
    private val foregroundHover: Color,
    private val background: Color,
    private val backgroundHover: Color,
) : TitleButtonBg() {
    override fun renderUi(node: UiNode) = node.run {
        val r = innerWidthPx * 0.5f
        val draw = getUiPrimitives()
        val bgColor = if (isHovered) backgroundHover else background
        val fgColor = if (isHovered) foregroundHover else foreground
        draw.localCircle(widthPx * 0.5f, heightPx * 0.5f, r, bgColor)
        draw.localRect(widthPx * 0.5f - r * 0.6f, heightPx * 0.5f - r * 0.1f, r * 1.2f, r * 0.2f, fgColor)
    }
}

class MaximizeButtonBackground(
    private val foreground: Color,
    private val foregroundHover: Color,
    private val background: Color,
    private val backgroundHover: Color,
) : TitleButtonBg() {
    override fun renderUi(node: UiNode) = node.run {
        val r = innerWidthPx * 0.5f
        val draw = getUiPrimitives()
        val bgColor = if (isHovered) backgroundHover else background
        val fgColor = if (isHovered) foregroundHover else foreground
        draw.localCircle(widthPx * 0.5f, heightPx * 0.5f, r, bgColor)
        draw.localRectBorder(widthPx * 0.5f - r * 0.5f, heightPx * 0.5f - r * 0.5f, r, r, 1.5f.dp.px, fgColor)
    }
}
