package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.div
import de.fabmax.kool.modules.ksl.lang.y
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenCubeVertexStage
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logD
import kotlin.math.PI

class GradientCubeGenerator(scene: Scene, gradientTex: Texture1d, size: Int = 128) :
    OffscreenPassCube(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RGBA_F16),
        initialSize = Vec2i(size),
        name = "gradient-cube"
    )
{

    init {
        drawNode.apply {
            addTextureMesh {
                generate {
                    cube { }
                }
                shader = GradientEnvShader(gradientTex)
            }
        }

        // remove render pass as soon as the gradient texture is loaded and rendered
        onAfterPass {
            logD { "Generated gradient cube map" }
            scene.removeOffscreenPass(this)
            launchDelayed(1) {
                drawNode.release()
                release()
            }
        }
    }

    private class GradientEnvShader(gradient: Texture1d) : KslShader(
        KslProgram("Reflection Map Pass").apply {
            val localPos = interStageFloat3("localPos")

            fullscreenCubeVertexStage(localPos)

            fragmentStage {
                main {
                    val normal = float3Var(normalize(localPos.output))
                    colorOutput(sampleTexture(texture1d("gradientTex"), acos(normal.y) / PI.const))
                }
            }
        },
        FullscreenShaderUtil.fullscreenShaderPipelineCfg
    ) {
        val gradientTex by texture1d("gradientTex", gradient)
    }
}