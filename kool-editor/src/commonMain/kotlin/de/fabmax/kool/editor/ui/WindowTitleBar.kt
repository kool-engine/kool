package de.fabmax.kool.editor.ui

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.Platform
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.PlatformFunctions
import de.fabmax.kool.editor.WindowButtonStyle
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.smoothStep
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.gizmo.GizmoFrame
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.sin

class WindowTitleBar(val editor: KoolEditor) : Composable {

    val isShowExportButton = mutableStateOf(KoolSystem.platform == Platform.Javascript)

    private val hoverHandler: WindowTitleHoverHandler get() = editor.ctx.windowTitleHoverHandler
    private val excludeBounds = List(3) { WindowTitleHoverHandler.HitBounds() }

    init {
        hoverHandler.apply {
            titleBarExcludeBounds += excludeBounds

            onClickClose = {
                editor.onExit()
                PlatformFunctions.closeWindow()
            }
            onClickMinimize = { PlatformFunctions.minimizeWindow() }
            onClickMaximize = { PlatformFunctions.toggleMaximizeWindow() }
        }
    }

    override fun UiScope.compose() {
        val marginTop = if (PlatformFunctions.isWindowMaximized) sizes.smallGap else Dp.ZERO
        modifier
            .layout(CellLayout)
            .alignY(AlignmentY.Top)
            .size(Grow.Std, FitContent)
            .background(null)
            .onClick {
                if (it.pointer.leftButtonRepeatedClickCount == 2) {
                    PlatformFunctions.toggleMaximizeWindow()
                }
            }

        Column(width = Grow.Std, height = sizes.heightWindowTitleBar + marginTop) {
            modifier.onPositioned { hoverHandler.setTitleBarBounds(it) }

            Box(width = Grow.Std, height = Grow.Std) {
                modifier
                    .alignY(AlignmentY.Top)
                    .background(TitleBgRenderer(colors.windowTitleBg, UiColors.windowTitleBgAccent, fade = TitleBgRenderer.FADE_STRONG))
            }
            Box(width = Grow.Std, height = sizes.borderWidth) {
                modifier.backgroundColor(UiColors.titleBg)
            }
        }

        Box(width = Grow.Std) {
            modifier.margin(top = marginTop)
            leftPanel()
            centerPanel()
            rightPanel()
        }
    }

    private fun UiScope.leftPanel() = Row(height = sizes.heightWindowTitleBar) {
        modifier.alignX(AlignmentX.Start)

        Box(width = sizes.panelBarWidth, height = Grow.Std) {
            Image {
                modifier
                    .align(AlignmentX.Center, AlignmentY.Center)
                    .iconImage(Icons.medium.koolIcon)
            }
        }

        Text("Kool Editor") {
            modifier
                .textColor(UiColors.titleText)
                .font(sizes.boldText.copy(glowColor = Color.BLACK.withAlpha(0.75f)))
                .alignY(AlignmentY.Center)
                .margin(start = sizes.gap)
        }
    }

    private fun UiScope.centerPanel() = Row {
        modifier
            .alignX(AlignmentX.Center)
            .onPositioned { excludeBounds[0].set(it) }
        appModeControlButtons()
    }

    private fun UiScope.rightPanel() = Row(height = sizes.heightWindowTitleBar) {
        modifier.alignX(AlignmentX.End)

        Row(height = Grow.Std) {
            modifier.onPositioned { excludeBounds[1].set(it) }

            Text("Transform Mode:") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(end = sizes.gap)
            }
            ComboBox {
                defaultComboBoxStyle()
                val selectedFrame = editor.gizmoOverlay.transformFrame.use()
                modifier
                    .width(sizes.baseSize * 2.5f)
                    .alignY(AlignmentY.Center)
                    .items(transformFrames)
                    .selectedIndex(transformFrames.indexOfFirst { it.frame == selectedFrame })
                    .onItemSelected { i ->
                        editor.gizmoOverlay.transformFrame.set(transformFrames[i].frame)
                    }
            }
        }

        if (isShowExportButton.use()) {
            divider(colors.strongDividerColor, marginStart = sizes.largeGap, marginEnd = sizes.largeGap, verticalMargin = sizes.gap)

            Row(height = Grow.Std) {
                modifier
                    .margin(end = sizes.smallGap)
                    .onPositioned { excludeBounds[2].set(it) }

                exportButton()
                iconButton(
                    icon = Icons.small.github,
                    width = sizes.editItemHeight,
                    height = sizes.editItemHeight
                ) {
                    KoolSystem.requireContext().openUrl("https://github.com/fabmax/kool")
                }
            }

        }

        when (PlatformFunctions.windowButtonStyle) {
            WindowButtonStyle.WINDOWS -> windowButtonsWindows()
            WindowButtonStyle.NONE -> { }
        }
    }

    private fun UiScope.exportButton() = Box {
        var isHovered by remember(false)
        var isClickFeedback by remember(false)
        var isExporting by remember(false)

        val color = when {
            isExporting -> null
            isClickFeedback -> colors.elevatedComponentBgHovered
            isHovered -> colors.componentBgHovered
            else -> colors.componentBg
        }
        color?.let {
            modifier.background(RoundRectBackground(it, sizes.smallGap))
        }

        modifier
            .align(AlignmentX.Center, AlignmentY.Center)
            .margin(end = sizes.largeGap)
            .width(sizes.baseSize * 3.5f)
            .height(sizes.editItemHeight)
            .padding(horizontal = sizes.gap)
            .onPointer { isClickFeedback = it.pointer.isLeftButtonDown }
            .onEnter { isHovered = true }
            .onExit {
                isHovered = false
                isClickFeedback = false
            }
            .onClick {
                if (!isExporting) {
                    isExporting = true
                    launchOnMainThread {
                        editor.exportProject()
                        isExporting = false
                    }
                }
            }

        if (isHovered) {
            saveProjectTooltip(this)
        }

        Row {
            modifier.align(AlignmentX.Center, AlignmentY.Center)

            if (!isExporting) {
                Image {
                    modifier
                        .alignY(AlignmentY.Center)
                        .iconImage(Icons.small.download, Color.WHITE)
                        .margin(end = sizes.gap)
                }
            } else {
                Box(Icons.small.iconSize, Icons.small.iconSize) {
                    modifier.background(ExportButtonBackground())
                }
            }
            Text("Save Project") {
                modifier
                    .alignY(AlignmentY.Center)
                    .textColor(Color.WHITE)
                    .margin(horizontal = sizes.gap)
            }
        }
    }

    private fun UiScope.windowButtonsWindows() = Row(height = Grow.Std) {
        modifier.margin(start = sizes.largeGap)
        val maxIcon = if (PlatformFunctions.isWindowMaximized) Icons.medium.demaximizeWin else Icons.medium.maximizeWin

        windowButton(
            icon = Icons.medium.minimizeWin,
            hoverColor = colors.componentBgHovered,
            isHovered = hoverHandler.isMinButtonHovered.use(),
            onPositioned = { hoverHandler.setMinButtonBounds(it) }
        )

        windowButton(
            icon = maxIcon,
            hoverColor = colors.componentBgHovered,
            isHovered = hoverHandler.isMaxButtonHovered.use(),
            onPositioned = { hoverHandler.setMaxButtonBounds(it) }
        )

        windowButton(
            icon = Icons.medium.closeWin,
            hoverColor = MdColor.RED tone 700,
            isHovered = hoverHandler.isCloseButtonHovered.use(),
            onPositioned = { hoverHandler.setCloseButtonBounds(it) }
        )
    }

    private fun UiScope.windowButton(
        icon: IconProvider,
        hoverColor: Color,
        isHovered: Boolean,
        onPositioned: (UiNode) -> Unit
    ) = Box(width = sizes.heightWindowTitleBar * 1.15f, height = Grow.Std) {
        modifier.onPositioned(onPositioned)
        if (isHovered) {
            modifier.backgroundColor(hoverColor)
        }
        val tint = if (isHovered) Color.WHITE else UiColors.secondaryBright
        Image {
            modifier
                .align(AlignmentX.Center, AlignmentY.Center)
                .iconImage(icon, tint)
        }
    }

    private fun UiScope.saveProjectTooltip(button: UiScope) = Popup(
        screenPxX = button.uiNode.rightPx - 250.dp.px,
        screenPxY = button.uiNode.bottomPx + sizes.gap.px,
        width = 250.dp,
        height = FitContent,
        layout = CellLayout
    ) {
        modifier
            .background(RoundRectBackground(colors.background, sizes.smallGap))
            .border(RoundRectBorder(colors.componentBg, sizes.smallGap, sizes.borderWidth))

        Column(width = Grow.Std) {
            modifier.margin(sizes.gap)

            Text("Download Project Files") {
                modifier.font(sizes.boldText)
            }

            Text("Save project and unzip it. Then open the unzipped folder in IntelliJ or a terminal:") {
                modifier
                    .width(Grow.Std)
                    .isWrapText(true)
                    .margin(vertical = sizes.largeGap)
            }

            Text("Run editor locally:") { modifier.margin(top = sizes.largeGap) }

            Text("./gradlew runEditor") {
                modifier
                    .font(editor.ui.consoleFont.use())
                    .margin(top = sizes.smallGap)
                    .width(Grow.Std)
                    .padding(sizes.smallGap)
                    .background(RoundRectBackground(colors.backgroundVariant, sizes.smallGap))
                    .border(RoundRectBorder(colors.componentBg, sizes.smallGap, sizes.borderWidth))
            }

            Text("Run app:") { modifier.margin(top = sizes.largeGap) }

            Text("./gradlew runApp") {
                modifier
                    .font(editor.ui.consoleFont.use())
                    .margin(top = sizes.smallGap)
                    .width(Grow.Std)
                    .padding(sizes.smallGap)
                    .background(RoundRectBackground(colors.backgroundVariant, sizes.smallGap))
                    .border(RoundRectBorder(colors.componentBg, sizes.smallGap, sizes.borderWidth))
            }
        }
    }

    private class TransformFrameOption(val frame: GizmoFrame, val label: String) {
        override fun toString(): String = label
    }

    private class ExportButtonBackground : UiRenderer<UiNode> {
        override fun renderUi(node: UiNode) {
            node.apply {
                val ctX = leftPx + (rightPx - leftPx) * 0.5f
                val ctY = topPx + (bottomPx - topPx) * 0.5f

                val rMaj = (rightPx - leftPx) / 2.75f
                val rMin = (rightPx - leftPx) / 8f

                val prims = node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                val ang2 = ((Time.gameTime % 6.0) * -60.0).toRad().toFloat()
                val maxSz1 = Vec2f(cos(ang2), sin(ang2))
                for (i in 1..5) {
                    val ang = ((Time.gameTime % 3.0) * 120.0 + i * 40.0).toRad().toFloat()
                    val pos = Vec2f(cos(ang), sin(ang))
                    val maxDot = maxSz1 dot pos
                    val dot = smoothStep(0.3f, 1f, maxDot)
                    val sz = dot * 0.4f + 0.6f
                    val color = gradient.getColor(dot)
                    prims.circle(ctX + pos.x * rMaj, ctY + pos.y * rMaj, sz * rMin, clipBoundsPx, color)
                }
            }
            node.surface.triggerUpdate()
        }

        companion object {
            val gradient = ColorGradient(Color("ffb703ff"), Color.WHITE)
        }
    }

    companion object {
        private val transformFrames = listOf(
            TransformFrameOption(GizmoFrame.GLOBAL, "Global"),
            TransformFrameOption(GizmoFrame.PARENT, "Parent"),
            TransformFrameOption(GizmoFrame.LOCAL, "Local"),
        )
    }
}