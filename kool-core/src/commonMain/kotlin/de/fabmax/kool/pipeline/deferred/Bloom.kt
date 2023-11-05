package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.logT
import kotlin.math.roundToInt

class Bloom(deferredPipeline: DeferredPipeline, cfg: DeferredPipelineConfig) : DeferredPassSwapListener {

    val thresholdPass = BloomThresholdPass(deferredPipeline, cfg)
    val blurPass = BloomBlurPass(cfg.bloomKernelSize, thresholdPass)

    var desiredMapHeight = 400

    var isEnabled: Boolean
        get() = thresholdPass.isEnabled && blurPass.isEnabled
        set(value) {
            thresholdPass.isEnabled = value
            blurPass.isEnabled = value
        }

    val thresholdMap: Texture2d
        get() = thresholdPass.colorTexture!!

    val bloomMap: Texture2d
        get() = blurPass.bloomMap

    var bloomScale: Float
        get() = blurPass.bloomScale
        set(value) { blurPass.bloomScale = value }
    var bloomStrength: Float
        get() = blurPass.bloomStrength
        set(value) { blurPass.bloomStrength = value }
    var lowerThreshold: Float
        get() = thresholdPass.outputShader.lowerThreshold
        set(value) {
            thresholdPass.outputShader.lowerThreshold = value
        }
    var upperThreshold: Float
        get() = thresholdPass.outputShader.upperThreshold
        set(value) {
            thresholdPass.outputShader.upperThreshold = value
        }

    override fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses) {
        thresholdPass.setLightingInput(currentPasses.lightingPass)
    }

    fun checkSize(viewportW: Int, viewportH: Int, ctx: KoolContext) {
        if (isEnabled) {
            val bestSamples = (viewportH / desiredMapHeight.toFloat()).roundToInt().clamp(1, 8)
            thresholdPass.setupDownSampling(bestSamples)

            val bloomMapW = (viewportW / bestSamples.toFloat()).roundToInt()
            val bloomMapH = (viewportH / bestSamples.toFloat()).roundToInt()
            if (bloomMapW > 0 && bloomMapH > 0 && (bloomMapW != thresholdPass.width || bloomMapH != thresholdPass.height)) {
                logT { "Bloom threshold down sampling: $bestSamples" }
                thresholdPass.resize(bloomMapW, bloomMapH, ctx)
                blurPass.resize(bloomMapW, bloomMapH, ctx)
            }
        }
    }
}