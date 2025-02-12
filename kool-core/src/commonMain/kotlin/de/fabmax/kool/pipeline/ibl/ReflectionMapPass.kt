package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenCubeVertexStage
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logT

class ReflectionMapPass private constructor(parentScene: Scene, hdriMap: Texture2d?, cubeMap: TextureCube?, size: Int) :
    OffscreenPassCube(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RG11B10_F),
        initialSize = Vec2i(size),
        mipMode = MipMode.Render(REFLECTION_MIP_LEVELS),
        name = "reflection-map"
    )
{

    var isAutoRemove = true

    init {
        mirrorIfInvertedClipY()

        val reflectionMapShader = ReflectionMapShader(hdriMap, cubeMap)
        drawNode.apply {
            addMesh(Attribute.POSITIONS, name = "reflectionMap") {
                generate {
                    cube { }
                }
                shader = reflectionMapShader
            }
        }

        onSetupMipLevel { mipLevel ->
            reflectionMapShader.createdPipeline?.swapPipelineData(mipLevel)
            reflectionMapShader.uRoughness = mipLevel.toFloat() / (numRenderMipLevels - 1) * 0.55f
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterPass {
            if (hdriMap != null) {
                logT { "Generated reflection map from HDRI: ${hdriMap.name}, size: $size x $size" }
            } else {
                logT { "Generated reflection map from cube map: ${cubeMap?.name}, size: $size x $size" }
            }
            if (isAutoRemove) {
                parentScene.removeOffscreenPass(this)
                launchDelayed(1) { release() }
            } else {
                isEnabled = false
            }
        }
    }

    private class ReflectionMapShader(hdri2d: Texture2d?, hdriCube: TextureCube?) : KslShader(
        KslProgram("Reflection Map Pass").apply {
            val localPos = interStageFloat3("localPos")

            fullscreenCubeVertexStage(localPos)

            fragmentStage {
                val uRoughness = uniformFloat1("uRoughness")
                val sampleEnvMap = hdri2d?.let { environmentMapSampler2d(this@apply, "hdri2d") }
                    ?: environmentMapSamplerCube(this@apply, "hdriCube")

                main {
                    val normal = float3Var(normalize(localPos.output))

                    `if`(uRoughness eq 0f.const) {
                        colorOutput(sampleEnvMap(normal, 0f.const))

                    }.`else` {
                        val mipLevel = float1Var(uRoughness * 16f.const)
                        val sampleCount = int1Var((1024f.const * (1f.const + mipLevel)).toInt1())
                        val totalWeight = float1Var(0f.const)
                        val prefilteredColor = float3Var(Vec3f.ZERO.const)
                        fori(0.const, sampleCount) { i ->
                            val xi = float2Var(hammersley(i, sampleCount))
                            val h = float3Var(importanceSampleGgx(xi, normal, uRoughness))
                            val l = float3Var(2f.const * dot(normal, h) * h - normal)

                            val nDotL = float1Var(max(dot(normal, l), 0f.const))
                            `if`(nDotL gt 0f.const) {
                                prefilteredColor += sampleEnvMap(l, mipLevel) * nDotL
                                totalWeight += nDotL
                            }
                        }
                        colorOutput(prefilteredColor / totalWeight)
                    }
                }
            }
        },
        FullscreenShaderUtil.fullscreenShaderPipelineCfg
    ) {
        var hdri2dTex by texture2d("hdri2d", hdri2d)
        var hdriCubeTex by textureCube("hdriCube", hdriCube)
        var uRoughness by uniform1f("uRoughness", 0f)
    }

    companion object {
        const val REFLECTION_MIP_LEVELS = 6

        fun reflectionMap(scene: Scene, envTex: Texture<*>, size: Int = 256): ReflectionMapPass {
            return when (envTex) {
                is Texture2d -> ReflectionMapPass(scene, envTex, null, size)
                is TextureCube -> ReflectionMapPass(scene, null, envTex, size)
                else -> throw IllegalArgumentException("Supplied envTex must be either Texture2d (HDRI) or TextureCube")
            }
        }
    }
}