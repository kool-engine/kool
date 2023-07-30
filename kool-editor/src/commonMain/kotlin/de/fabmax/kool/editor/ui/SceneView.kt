package de.fabmax.kool.editor.ui

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

    private fun UiScope.panelBar(dockNode: DockNodeLeaf) {
        if (dockNode.dockedItems.size > 1) {
            dockNode.dockedItems.mapNotNull { editorPanels[it.name] }.forEach { panel ->
                panelButton(panel, dockNode)
            }
        }
    }

    fun applyViewportTo(targetScene: Scene) {
        targetScene.mainRenderPass.useWindowViewport = false
        targetScene.onRenderScene += { ctx ->
            viewBox?.let { box ->
                val x = box.leftPx.roundToInt()
                val w = box.rightPx.roundToInt() - x
                val h = box.bottomPx.roundToInt() - box.topPx.roundToInt()
                val y = ctx.windowHeight - box.bottomPx.roundToInt()
                targetScene.mainRenderPass.viewport.set(x, y, w, h)
            }
        }
    }
}