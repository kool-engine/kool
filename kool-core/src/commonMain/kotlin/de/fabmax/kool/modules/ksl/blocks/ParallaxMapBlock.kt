package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d

fun KslScopeBuilder.parallaxMapBlock(cfg: ParallaxMapConfig, block: ParallaxMapBlock.() -> Unit): ParallaxMapBlock {
    val parallaxMapBlock = ParallaxMapBlock(cfg, parentStage.program.nextName("parallaxMapBlock"), this)
    ops += parallaxMapBlock.apply(block)
    return parallaxMapBlock
}

class ParallaxMapBlock(val cfg: ParallaxMapConfig, name: String, parentScope: KslScopeBuilder) :
    KslBlock(name, parentScope)
{
    val inPositionClipSpace = inFloat4("inPositionClipSpace")
    val inPositionWorldSpace = inFloat3("inPositionWorldSpace")
    val inNormalWorldSpace = inFloat3("inNormalWorldSpace")
    val inTexCoords = inFloat2("inTexCoords")
    val inStrength = inFloat1("inStrength")
    val inMaxSteps = inInt1("inMaxSteps")

    val outDisplacedTexCoords = outFloat2()
    val outDisplacedWorldPos = outFloat3()
    val outDdx = outFloat2()
    val outDdy = outFloat2()

    init {
        body.apply {
            if (cfg.isParallaxMapped) {
                parallaxMapping()
            } else {
                outDisplacedTexCoords set inTexCoords
                outDisplacedWorldPos set inPositionWorldSpace
            }
        }
    }

    private fun KslScopeBuilder.parallaxMapping() {
        val camData = parentStage.program.cameraData()
        val parallaxMap = parentStage.program.texture2d(cfg.parallaxMapName)

        val step = float1Var(1f.const / inMaxSteps.toFloat1())

        outDdx set dpdx(inTexCoords)
        outDdy set dpdy(inTexCoords)
        val viewDir = float3Var(normalize(inPositionWorldSpace - camData.position))

        val proj = float2Var((inPositionClipSpace.xy / inPositionClipSpace.w + 1f.const) * 0.5f.const)
        if (KoolSystem.requireContext().backend.isInvertedNdcY) {
            proj.y set 1f.const - proj.y
        }
        val pixelPos = float2Var(proj * camData.viewport.zw)

        val sampleScale = float1Var(inStrength / abs(dot(inNormalWorldSpace, viewDir)))
        val sampleDir = float3Var(viewDir - inNormalWorldSpace * dot(viewDir, inNormalWorldSpace))

        val sampleExt = float3Var(inPositionWorldSpace + sampleDir * sampleScale)
        val sampleExtProj = float4Var(camData.viewProjMat * float4Value(sampleExt, 1f.const))
        proj set (sampleExtProj.xy / sampleExtProj.w + 1f.const) * 0.5f.const
        if (KoolSystem.requireContext().backend.isInvertedNdcY) {
            proj.y set 1f.const - proj.y
        }
        val sampleExtPixel = float2Var(proj * camData.viewport.zw - pixelPos)

        val sampleUv = float2Var(inTexCoords)
        val prevSampleUv = float2Var(inTexCoords)
        val prevH = float1Var(0f.const)
        val hStart = float1Var(noise21(pixelPos) * step)

        repeat(inMaxSteps) { i ->
            val hLimit = float1Var(hStart + i.toFloat1() * step)
            val h = float1Var(1f.const - sampleTextureGrad(parallaxMap, sampleUv, outDdx, outDdy).x)

            `if` (h lt hLimit) {
                val afterDepth = float1Var(h - hLimit)
                val beforeDepth = float1Var(prevH - hLimit + step)
                val weight = float1Var(afterDepth / (afterDepth - beforeDepth))
                sampleUv set prevSampleUv * weight + sampleUv * (1f.const - weight)
                prevH set prevH * weight + h * (1f.const - weight)
                `break`()

            }.`else` {
                prevH set h
                prevSampleUv set sampleUv

                val sampleOffset = float2Var(sampleExtPixel * min(h, hLimit))
                sampleUv set inTexCoords + outDdx * sampleOffset.x + outDdy * sampleOffset.y
            }
        }

        outDisplacedTexCoords set sampleUv
        outDisplacedWorldPos set inPositionWorldSpace + viewDir * (prevH * sampleScale)
    }
}

data class ParallaxMapConfig(
    val isParallaxMapped: Boolean = false,
    val parallaxMapName: String = "",
    val defaultParallaxMap: Texture2d? = null,
    val strength: Float = 0f,
    val maxSteps: Int = 0,
    val textureChannel: Int = 0,
    val isAdjustFragmentDepth: Boolean = true,
    val isPreciseShadows: Boolean = true,
) {
    class Builder {
        var isParallaxMapped: Boolean = false
        var parallaxMapName: String = "tParallaxMap"
        var defaultParallaxMap: Texture2d? = null
        var strength: Float = 0.5f
        var maxSteps = 16
        var textureChannel = 0
        var isAdjustFragmentDepth: Boolean = true
        var isPreciseShadows: Boolean = true

        fun clearParallaxMap(): Builder {
            isParallaxMapped = false
            defaultParallaxMap = null
            return this
        }

        fun useParallaxMap(
            texture: Texture2d? = null,
            strength: Float = 0.5f,
            maxSteps: Int = 8,
            textureChannel: Int = 0,
            parallaxMapName: String = "tParallaxMapName",
        ): Builder {
            this.isParallaxMapped = true
            this.defaultParallaxMap = texture
            this.strength = strength
            this.maxSteps = maxSteps
            this.textureChannel = textureChannel
            this.parallaxMapName = parallaxMapName
            return this
        }

        fun build() = ParallaxMapConfig(
            isParallaxMapped = isParallaxMapped,
            parallaxMapName = parallaxMapName,
            defaultParallaxMap = defaultParallaxMap,
            strength = strength,
            maxSteps = maxSteps,
            textureChannel = textureChannel,
            isAdjustFragmentDepth = isAdjustFragmentDepth,
            isPreciseShadows = isPreciseShadows
        )
    }
}
