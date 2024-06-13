package de.fabmax.kool.editor.ui

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.Platform
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.PlatformFunctions
import de.fabmax.kool.editor.WindowButtonStyle
import de.fabmax.kool.modules.gizmo.GizmoFrame
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class WindowTitleBar(val editor: KoolEditor) : Composable {

    val isShowExportButton = mutableStateOf(KoolSystem.platform == Platform.JAVASCRIPT)

    override fun UiScope.compose() {
        modifier
            .layout(CellLayout)
            .alignY(AlignmentY.Top)
            .size(Grow.Std, FitContent)
            .onDragStart { PlatformFunctions.dragWindowStart(it.pointer) }
            .onDrag { PlatformFunctions.dragWindow(it.pointer) }
            .background(null)
            .onClick {
                if (it.pointer.leftButtonRepeatedClickCount == 2) {
                    PlatformFunctions.toggleMaximizeWindow()
                }
            }

        Column(width = Grow.Std, height = sizes.heightWindowTitleBar) {
            Box(width = Grow.Std, height = Grow.Std) {
                modifier
                    .alignY(AlignmentY.Top)
                    .background(TitleBgRenderer(colors.windowTitleBg, UiColors.windowTitleBgAccent, fade = TitleBgRenderer.FADE_STRONG))
            }
            Box(width = Grow.Std, height = sizes.borderWidth) {
                modifier.backgroundColor(UiColors.titleBg)
            }
        }

        leftPanel()
        centerPanel()
        rightPanel()
    }

    private fun UiScope.leftPanel() = Row(height = sizes.heightWindowTitleBar) {
        modifier.alignX(AlignmentX.Start)

        Box(width = sizes.panelBarWidth, height = Grow.Std) {
            Image {
                modifier
                    .align(AlignmentX.Center, AlignmentY.Center)
                    .iconImage(IconMap.medium.editorIcon)
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

    private fun UiScope.centerPanel() = Row(height = Grow.Std) {
        modifier.alignX(AlignmentX.Center)
        appModeControlButtons()
    }

    private fun UiScope.rightPanel() = Row(height = sizes.heightWindowTitleBar) {
        modifier.alignX(AlignmentX.End)


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

        if (isShowExportButton.use()) {
            divider(colors.strongDividerColor, marginStart = sizes.largeGap, marginEnd = sizes.largeGap, verticalMargin = sizes.gap)

            var isHovered by remember(false)
            val button = iconTextButton(
                icon = IconMap.small.download,
                text = "Save Project",
                bgColor = colors.componentBg,
                bgColorHovered = colors.componentBgHovered,
                bgColorClicked = colors.elevatedComponentBgHovered,
                width = sizes.baseSize * 3.5f,
                margin = sizes.gap,
                boxBlock = {
                    modifier
                        .onEnter { isHovered = true }
                        .onExit { isHovered = false }
                }
            ) {
                editor.exportProject()
            }

            if (isHovered) {
                saveProjectTooltip(button)
            }
        }

        when (PlatformFunctions.windowButtonStyle) {
            WindowButtonStyle.WINDOWS -> windowButtonsWindows()
            WindowButtonStyle.NONE -> { }
        }
    }

    private fun UiScope.windowButtonsWindows() {
        val maxIcon = if (PlatformFunctions.isWindowMaximized) IconMap.medium.demaximizeWin else IconMap.medium.maximizeWin

        Box(width = sizes.largeGap) { }

        windowButton(IconMap.medium.minimizeWin, colors.componentBgHovered) { PlatformFunctions.minimizeWindow() }
        windowButton(maxIcon, colors.componentBgHovered) { PlatformFunctions.toggleMaximizeWindow() }
        windowButton(IconMap.medium.closeWin, MdColor.RED tone 700) {
            editor.onExit()
            PlatformFunctions.closeWindow()
        }
    }

    private fun UiScope.windowButton(icon: IconProvider, hoverColor: Color, onClick: () -> Unit) =
        Box(width = sizes.heightWindowTitleBar * 1.15f, height = Grow.Std) {
            var isHovered by remember(false)
            modifier
                .onEnter { isHovered = true }
                .onExit { isHovered = false }
                .onClick { onClick() }
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

            Text("Save project and unzip it. Then open the unzipped folder in a terminal:") {
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

    companion object {
        private val transformFrames = listOf(
            TransformFrameOption(GizmoFrame.GLOBAL, "Global"),
            TransformFrameOption(GizmoFrame.PARENT, "Parent"),
            TransformFrameOption(GizmoFrame.LOCAL, "Local"),
        )
    }
}