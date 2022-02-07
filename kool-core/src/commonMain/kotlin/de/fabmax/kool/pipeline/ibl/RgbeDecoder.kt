package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.logD
import kotlin.math.max

class RgbeDecoder(parentScene: Scene, hdriTexture: Texture2d, brightness: Float = 1f) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            val w = hdriTexture.loadedTexture?.width ?: 1024
            val h = hdriTexture.loadedTexture?.height ?: 512
            val sz = max(w, h)

            name = "RgbeDecoder"
            setSize(sz, sz)
            addColorTexture(TexFormat.RGBA_F16)

            clearDepthTexture()
            addMipLevels(drawMipLevels = false)
        }) {

    init {
        (drawNode as Group).apply {
            +textureMesh {
                isFrustumChecked = false
                generate {
                    rect {  }
                }

                val texName = "colorTex"
                val model = ShaderModel("RgbeDecoder").apply {
                    val ifTexCoords: StageInterfaceNode
                    vertexStage {
                        ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                        positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
                    }
                    fragmentStage {
                        val decoded = addNode(RgbeDecoderNode(stage)).apply {
                            inRgbe = texture2dSamplerNode(texture2dNode(texName), ifTexCoords.output).outColor
                        }
                        colorOutput(multiplyNode(decoded.outColor, brightness).output)
                    }
                }
                shader = ModeledShader.TextureColor(hdriTexture, texName, model).apply {
                    onPipelineSetup += { builder, _, _ -> builder.cullMethod = CullMethod.NO_CULLING }
                }
            }
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterDraw += { ctx ->
            logD { "Converted RGBe to linear: ${hdriTexture.name}" }
            parentScene.removeOffscreenPass(this)
            ctx.runDelayed(1) {
                dispose(ctx)
            }
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }
}