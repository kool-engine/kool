package de.fabmax.kool.editor.ui

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.gizmo.GizmoFrame
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.DockNodeLeaf
import de.fabmax.kool.scene.Scene
import kotlin.math.roundToInt

class SceneView(ui: EditorUi) : EditorPanel("Scene View", IconMap.medium.CAMERA, ui) {

    val isBoxSelectMode = mutableStateOf(false).onChange {
        if (it) {
            // enabled box selection mode
            windowSurface.inputMode = UiSurface.InputCaptureMode.CapturePassthrough
        } else {
            // disabled box selection mode
            windowSurface.inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
            boxSelector.isBoxSelect.set(false)
        }
    }

    val isShowToolbar = mutableStateOf(true)
    val isShowExportButton = mutableStateOf(KoolSystem.isJavascript)
    val toolbar = FloatingToolbar(ui)

    private var viewBox: UiNode? = null
    private val boxSelector = BoxSelector()

    override val windowSurface: UiSurface = editorPanel(false) {
        modifier.background(null)

        Column(Grow.Std, Grow.Std) {
            modifier.background(null)
            Row(Grow.Std, sizes.baseSize) {
                modifier
                    .padding(horizontal = sizes.gap - sizes.borderWidth)
                    .backgroundColor(UiColors.titleBg)
                    .onPointer { it.pointer.consume() }

                windowDockable.dockedTo.use()?.let { panelBar(it) }
            }
            Box(width = Grow.Std, height = Grow.Std) {
                modifier
                    .padding(Dp.ZERO)
                    .background(null)
                viewBox = uiNode

                if (isBoxSelectMode.use()) {
                    boxSelector()
                }
            }
        }

        appModeControlButtons()
        if (isShowToolbar.use()) {
            toolbar()
        }
    }

    init {
        windowSurface.inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
        windowDockable.setFloatingBounds(width = Grow.Std, height = Grow.Std)
    }

    private fun RowScope.panelBar(dockNode: DockNodeLeaf) {
        if (dockNode.dockedItems.size > 1) {
            dockNode.dockedItems.mapNotNull { editorPanels[it.name] }.forEach { panel ->
                panelButton(panel, dockNode)
            }
        }

        Box(width = Grow.Std) {  }

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
                icon = IconMap.small.DOWNLOAD,
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
                    .font(ui.consoleFont.use())
                    .margin(top = sizes.smallGap)
                    .width(Grow.Std)
                    .padding(sizes.smallGap)
                    .background(RoundRectBackground(colors.backgroundVariant, sizes.smallGap))
                    .border(RoundRectBorder(colors.componentBg, sizes.smallGap, sizes.borderWidth))
            }

            Text("Run app:") { modifier.margin(top = sizes.largeGap) }

            Text("./gradlew runApp") {
                modifier
                    .font(ui.consoleFont.use())
                    .margin(top = sizes.smallGap)
                    .width(Grow.Std)
                    .padding(sizes.smallGap)
                    .background(RoundRectBackground(colors.backgroundVariant, sizes.smallGap))
                    .border(RoundRectBorder(colors.componentBg, sizes.smallGap, sizes.borderWidth))
            }
        }
    }

    fun applyViewportTo(targetScene: Scene) {
        targetScene.mainRenderPass.useWindowViewport = false
        targetScene.onRenderScene += {
            viewBox?.let { box ->
                val x = box.leftPx.roundToInt()
                val w = box.rightPx.roundToInt() - x
                val y = box.topPx.roundToInt()
                val h = box.bottomPx.roundToInt() - y
                targetScene.mainRenderPass.viewport.set(x, y, w, h)
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