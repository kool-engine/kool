package de.fabmax.kool.modules.ksl.blocks

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

        val uvdx = float2Var(dpdx(inTexCoords))
        val uvdy = float2Var(dpdy(inTexCoords))
        val viewDir = float3Var(normalize(inPositionWorldSpace - camData.position))
        val pixelPos = float2Var((inPositionClipSpace.xy / inPositionClipSpace.w + 1f.const) * 0.5f.const * camData.viewport.zw)

        val sampleScale = float1Var(inStrength / abs(dot(inNormalWorldSpace, viewDir)))
        val sampleDir = float3Var(viewDir - inNormalWorldSpace * dot(viewDir, inNormalWorldSpace))

        val sampleExt = float3Var(inPositionWorldSpace + sampleDir * sampleScale)
        val sampleExtProj = float4Var(camData.viewProjMat * float4Value(sampleExt, 1f.const))
        val sampleExtPixel = float2Var((sampleExtProj.xy / sampleExtProj.w + 1f.const) * 0.5f.const * camData.viewport.zw - pixelPos)

        val sampleUv = float2Var(inTexCoords)
        val prevSampleUv = float2Var(inTexCoords)
        val prevH = float1Var(0f.const)
        val hStart = float1Var(noise12(pixelPos) * step)

        repeat(inMaxSteps) { i ->
            val hLimit = float1Var(hStart + i.toFloat1() * step)
            val h = float1Var(1f.const - sampleTexture(parallaxMap, sampleUv).x)

            `if` (h lt hLimit) {
                val afterDepth = float1Var(h - hLimit)
                val beforeDepth = float1Var(prevH - hLimit + step)
                val weight = float1Var(afterDepth / (afterDepth - beforeDepth))
                sampleUv set prevSampleUv * weight + sampleUv * (1f.const - weight)
                `break`()

            }.`else` {
                prevH set h
                prevSampleUv set sampleUv

                val sampleOffset = float2Var(sampleExtPixel * min(h, hLimit))
                sampleUv set inTexCoords + uvdx * sampleOffset.x + uvdy * sampleOffset.y
            }
        }

        outDisplacedTexCoords set sampleUv
        // todo:
        outDisplacedWorldPos set inPositionWorldSpace
    }
}

data class ParallaxMapConfig(
    val isParallaxMapped: Boolean = false,
    val parallaxMapName: String = "",
    val defaultParallaxMap: Texture2d? = null,
    val strength: Float = 0f,
    val maxSteps: Int = 0,
    val textureChannel: Int = 0,
) {
    class Builder {
        var isParallaxMapped: Boolean = false
        var parallaxMapName: String = "tParallaxMap"
        var defaultParallaxMap: Texture2d? = null
        var strength: Float = 0.5f
        var maxSteps = 8
        var textureChannel = 0

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
        )
    }
}
