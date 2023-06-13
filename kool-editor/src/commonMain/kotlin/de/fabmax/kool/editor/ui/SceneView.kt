package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import kotlin.math.roundToInt

class SceneView(ui: EditorUi) : EditorPanel("Scene View", ui) {

    private var viewBox: UiNode? = null

    override val windowSurface: UiSurface = EditorPanelWindow(false) {
        modifier.background(null)

        Column(Grow.Std, Grow.Std) {
            modifier.background(null)
            Row(Grow.Std, sizes.baseSize) {
                modifier
                    .padding(horizontal = sizes.gap - sizes.borderWidth)
                    .backgroundColor(UiColors.titleBg)
            }
            Box(width = Grow.Std, height = Grow.Std) {
                modifier
                    .padding(Dp.ZERO)
                    .background(null)
                viewBox = uiNode
            }
        }

        appModeControlButtons()
    }

    init {
        windowSurface.inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
        windowDockable.setFloatingBounds(width = Grow.Std, height = Grow.Std)
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