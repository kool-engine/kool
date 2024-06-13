package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorEditMode
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import kotlin.math.roundToInt

class SceneView(ui: EditorUi) : EditorPanel("Scene View", IconMap.medium.camera, ui) {

    val isShowOverlays = mutableStateOf(true)
    val isShowKeyInfo = mutableStateOf(false)
    val toolbar = FloatingToolbar(editor)
    val keyInfo = KeyInfo(ui)

    private var viewBox: UiNode? = null
    private val boxSelector = BoxSelector()

    override val windowSurface: UiSurface = editorPanel(false) {
        modifier.background(null)

        Column(Grow.Std, Grow.Std) {
            modifier.background(null)

            if (isShowOverlays.use()) {
                Box(width = Grow.Std, height = Grow.Std) {
                    modifier
                        .padding(Dp.ZERO)
                        .background(null)
                    viewBox = uiNode

                    if (editor.editMode.mode.use() == EditorEditMode.Mode.BOX_SELECT) {
                        // enabled box selection mode
                        surface.inputMode = UiSurface.InputCaptureMode.CapturePassthrough
                        boxSelector()
                    } else {
                        // disabled box selection mode
                        surface.inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
                        boxSelector.isBoxSelect.set(false)
                    }
                }
            }
        }

        if (isShowOverlays.use()) {
            toolbar()
            if (isShowKeyInfo.use()) {
                keyInfo()
            }
        }
    }

    init {
        windowSurface.inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
        windowDockable.setFloatingBounds(width = Grow.Std, height = Grow.Std)
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
}