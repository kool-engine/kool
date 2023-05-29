package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.Mesh

object FullscreenShaderUtil {

    /**
     * Generates a single quad stretching from (-1, -1, 0) to (1, 1, 0) as used by most fullscreen shaders.
     */
    fun Mesh.generateFullscreenQuad(mirrorTexCoordsY: Boolean = true) {
        isFrustumChecked = false
        generate {
            rect {
                origin.set(-1f, -1f, 0f)
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
    fun KslProgram.fullscreenQuadVertexStage(uv: KslInterStageVector<KslTypeFloat2, KslTypeFloat1>?) {
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

    fun KslProgram.fullscreenCubeVertexStage(localPos: KslInterStageVector<KslTypeFloat3, KslTypeFloat1>?) {
        vertexStage {
            main {
                val vertexPos = float3Var(vertexAttribFloat3(Attribute.POSITIONS.name))
                localPos?.let { it.input set vertexPos }
                outPosition set mvpMatrix().matrix * float4Value(vertexPos, 1f.const)
            }
        }
    }

    val fullscreenShaderPipelineCfg = KslShader.PipelineConfig().apply {
        blendMode = BlendMode.DISABLED
        cullMethod = CullMethod.NO_CULLING
        depthTest = DepthCompareOp.DISABLED
    }
}