package de.fabmax.kool.pipeline.mssao

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.scene.Scene
import kotlin.math.roundToInt

class SceneSizeRenderCallback(
    val renderPass: OffscreenRenderPass2d,
    val scene: Scene,
    val sizeFactor: Float = 1f,
    val sizeMultiple: Float = 16f,
    installCallback: Boolean = true
) {
    val callback: (KoolContext) -> Unit = { onRenderScene() }

    init {
        if (installCallback) {
            scene.onRenderScene += callback
        }
    }

    private fun onRenderScene() {
        val nW = (scene.mainRenderPass.viewport.width / sizeMultiple).roundToInt() * sizeMultiple
        val nH = (scene.mainRenderPass.viewport.height / sizeMultiple).roundToInt() * sizeMultiple

        val mapW = (nW * sizeFactor).roundToInt()
        val mapH = (nH * sizeFactor).roundToInt()
        if (renderPass.isEnabled && mapW > 0 && mapH > 0) {
            renderPass.setSize(mapW, mapH)
        }
    }
}