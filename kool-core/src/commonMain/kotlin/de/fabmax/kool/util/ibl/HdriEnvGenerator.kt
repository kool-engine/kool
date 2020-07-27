package de.fabmax.kool.util.ibl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.logD
import kotlin.math.log2

class HdriEnvGenerator(parentScene: Scene, hdriTexture: Texture, size: Int = 512) :
        OffscreenRenderPassCube(Group(), renderPassConfig {
            name = "HdriEnvGenerator"
            setSize(size, size)
            addColorTexture(TexFormat.RGBA_F16)
            clearDepthTexture()
            mipLevels = log2(size.toDouble()).toInt()
        }) {

    private val uMipLevel = Uniform1f(0.0f, "uMipLevel")

    init {
        onSetupMipLevel = { mipLevel, _ ->
            uMipLevel.value = mipLevel.toFloat()
        }

        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS)) {
                isFrustumChecked = false
                generate {
                    cube { centered() }
                }

                val texName = "colorTex"
                val model = ShaderModel("HdriEnvGenerator").apply {
                    val ifLocalPos: StageInterfaceNode
                    vertexStage {
                        ifLocalPos = stageInterfaceNode("ifLocalPos", attrPositions().output)
                        positionOutput = simpleVertexPositionNode().outVec4
                    }
                    fragmentStage {
                        val tex = textureNode(texName)
                        val equiRectSampler = equiRectSamplerNode(tex, ifLocalPos.output, true).apply {
                            texLod = pushConstantNode1f(uMipLevel).output
                        }
                        colorOutput(equiRectSampler.outColor)
                    }
                }
                shader = ModeledShader.TextureColor(hdriTexture, texName, model).apply {
                    onPipelineSetup += { builder, _, _ -> builder.cullMethod = CullMethod.NO_CULLING }
                }
            }
        }

        parentScene.addOffscreenPass(this)

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterDraw += { ctx ->
            logD { "Generated cube map from HDRI: ${hdriTexture.name}" }
            parentScene.removeOffscreenPass(this)
            ctx.runDelayed(1) {
                dispose(ctx)
            }
        }
    }
}