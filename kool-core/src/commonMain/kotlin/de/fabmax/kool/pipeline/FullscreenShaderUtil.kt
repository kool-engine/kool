package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.Mesh

object FullscreenShaderUtil {

    /**
     * Generates a single quad stretching from (-1, -1, 0) to (1, 1, 0) as used by most fullscreen shaders.
     */
    fun Mesh.generateFullscreenQuad(mirrorTexCoordsY: Boolean = !KoolSystem.requireContext().backend.isInvertedNdcY) {
        isFrustumChecked = false
        generate {
            rect {
                size.set(2f, 2f)
                if (mirrorTexCoordsY) {
                    mirrorTexCoordsY()
                }
            }
        }
    }

    /**
     * Generates the vertex stage for a simple fullscreen generator shader.
     */
    fun KslProgram.fullscreenQuadVertexStage(uv: KslInterStageVector<KslFloat2, KslFloat1>?) {
        vertexStage {
            main {
                uv?.let { it.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name) }
                outPosition set float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
            }
        }
    }

    fun Mesh.generateFullscreenCube() {
        isFrustumChecked = false
        generate {
            cube { }
        }
    }

    fun KslProgram.fullscreenCubeVertexStage(localPos: KslInterStageVector<KslFloat3, KslFloat1>?) {
        vertexStage {
            main {
                val vertexPos = float3Var(vertexAttribFloat3(Attribute.POSITIONS.name))
                localPos?.let { it.input set vertexPos }
                outPosition set cameraData().viewProjMat * float4Value(vertexPos, 1f.const)
            }
        }
    }

    val fullscreenShaderPipelineCfg = PipelineConfig(
        blendMode = BlendMode.DISABLED,
        cullMethod = CullMethod.NO_CULLING,
        depthTest = DepthCompareOp.ALWAYS,
    )
}