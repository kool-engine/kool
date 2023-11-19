package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.Texture2d

class Reflections(cfg: DeferredPipelineConfig) : DeferredPassSwapListener {

    val reflectionPass = ReflectionPass(cfg.baseReflectionStep)
    val denoisePass = ReflectionDenoisePass(reflectionPass)

    val reflectionMap: Texture2d
        get() = denoisePass.colorTexture!!

    // reflection map size relative to screen resolution
    var mapSize = 0.5f

    var isEnabled: Boolean
        get() = reflectionPass.isEnabled && denoisePass.isEnabled
        set(value) {
            reflectionPass.isEnabled = value
            denoisePass.isEnabled = value
        }

    fun checkSize(viewportW: Int, viewportH: Int, ctx: KoolContext) {
        if (isEnabled) {
            val width = (viewportW * mapSize).toInt().clamp(1, 4096)
            val height = (viewportH * mapSize).toInt().clamp(1, 4096)
            if (isEnabled) {
                reflectionPass.setSize(width, height, ctx)
                denoisePass.setSize(width, height, ctx)
            }
        }
    }

    override fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses) {
        reflectionPass.setInput(currentPasses.lightingPass, currentPasses.materialPass)
        denoisePass.setPositionInput(currentPasses.materialPass)
    }
}