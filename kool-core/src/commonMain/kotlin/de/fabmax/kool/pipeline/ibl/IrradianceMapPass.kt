package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenCubeVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenCube
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logT
import kotlin.math.PI

class IrradianceMapPass private constructor(parentScene: Scene, hdriMap: Texture2d?, cubeMap: TextureCube?, size: Int) :
    OffscreenPassCube(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RG11B10_F),
        initialSize = Vec2i(size),
        name = "irradiance-map"
    )
{

    var isAutoRemove = true

    init {
        mirrorIfInvertedClipY()

        drawNode.apply {
            addMesh(Attribute.POSITIONS) {
                generateFullscreenCube()
                shader = IrradianceMapPassShader(hdriMap, cubeMap)
            }
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterPass {
            if (hdriMap != null) {
                logT { "Generated irradiance map from HDRI: ${hdriMap.name}" }
            } else {
                logT { "Generated irradiance map from cube map: ${cubeMap?.name}" }
            }
            if (isAutoRemove) {
                parentScene.removeOffscreenPass(this)
                launchDelayed(1) { release() }
            } else {
                isEnabled = false
            }
        }
    }

    private class IrradianceMapPassShader(hdri2d: Texture2d?, hdriCube: TextureCube?) : KslShader(
        KslProgram("Irradiance Map Pass").apply {
            val localPos = interStageFloat3("localPos")

            fullscreenCubeVertexStage(localPos)

            fragmentStage {
                val sampleEnvMap = hdri2d?.let { environmentMapSampler2d(this@apply, "hdri2d") }
                     ?: environmentMapSamplerCube(this@apply, "hdriCube")

                main {
                    val normal = float3Var(normalize(localPos.output))
                    val up = float3Var(Vec3f.Y_AXIS.const)
                    val right = float3Var(normalize(cross(up, normal)))
                    up set cross(normal, right)

                    val sampleDelta = 0.03737.const
                    val irradiance = float3Var(Vec3f.ZERO.const)
                    val nrSamples = int1Var(0.const)

                    val theta = float1Var(0f.const)
                    `for`(theta, theta lt (0.5 * PI).const, sampleDelta) {
                        val deltaPhi = float1Var(sampleDelta / sin(theta))
                        val phi = float1Var(0f.const)
                        `for`(phi, phi lt (2.0 * PI).const, deltaPhi) {
                            val tempVec = float3Var(right * cos(phi) + up * sin(phi))
                            val sampleVector = normal * cos(theta) + tempVec * sin(theta)
                            val envColor = float3Var(sampleEnvMap(sampleVector, 3f.const))
                            irradiance += envColor * cos(theta) * 0.6f.const
                            nrSamples += 1.const
                        }
                    }

                    colorOutput(irradiance * PI.const / nrSamples.toFloat1())
                }
            }
        },
        FullscreenShaderUtil.fullscreenShaderPipelineCfg
    ) {
        val hdri2dTex by texture2d("hdri2d", hdri2d)
        val hdriCubeTex by textureCube("hdriCube", hdriCube)
    }

    companion object {
        fun irradianceMap(scene: Scene, envTex: Texture<*>, size: Int = 16): IrradianceMapPass {
            return when (envTex) {
                is Texture2d -> IrradianceMapPass(scene, envTex, null, size)
                is TextureCube -> IrradianceMapPass(scene, null, envTex, size)
                else -> throw IllegalArgumentException("Supplied envTex must be either Texture2d (HDRI) or TextureCube")
            }
        }
    }
}