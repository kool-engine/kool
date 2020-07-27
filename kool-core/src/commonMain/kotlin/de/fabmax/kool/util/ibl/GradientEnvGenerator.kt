package de.fabmax.kool.util.ibl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.createUint8Buffer
import de.fabmax.kool.util.logD

class GradientEnvGenerator(gradient: ColorGradient, ctx: KoolContext, size: Int = 128) :
        OffscreenRenderPassCube(Group(), renderPassConfig {
            name = "GradientEnvGenerator"
            setSize(size, size)
            addColorTexture(TexFormat.RGBA_F16)
            clearDepthTexture()
        }) {

    private val gradientTex = makeGradientTex(gradient, size * 2, ctx)

    init {
        (drawNode as Group).apply {
            +textureMesh {
                generate {
                    icoSphere {
                        radius = 10f
                        steps = 2
                    }
                }
                shader = ModeledShader.TextureColor(gradientTex, "gradTex", gradientEnvModel())
                shader!!.onPipelineSetup += { builder, _, _ ->
                    builder.depthTest = DepthCompareOp.DISABLED
                    builder.cullMethod = CullMethod.NO_CULLING
                }
            }
        }

        ctx.addBackgroundRenderPass(this)

        // remove render pass as soon as the gradient texture is loaded and rendered
        onAfterDraw += {
            logD { "Generated gradient cube map" }
            ctx.removeBackgroundRenderPass(this)
            ctx.runDelayed(1) {
                println("gradient disposed")
                dispose(ctx)
            }
        }
    }

    private fun makeGradientTex(gradient: ColorGradient, size: Int, ctx: KoolContext): Texture {
        val buf = createUint8Buffer(size * 4)

        for (i in 0 until size) {
            val c = gradient.getColor(1f - i.toFloat() / size)
            buf[i * 4 + 0] = ((c.r * 255f).toByte())
            buf[i * 4 + 1] = ((c.g * 255f).toByte())
            buf[i * 4 + 2] = ((c.b * 255f).toByte())
            buf[i * 4 + 3] = (255.toByte())
        }

        val data = BufferedTextureData(buf, 1, size, TexFormat.RGBA)
        val props = TextureProps(addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE, mipMapping = false, maxAnisotropy = 1)
        return ctx.assetMgr.loadAndPrepareTexture(data, props, "gradientEnvTex")
    }


    private fun gradientEnvModel() = ShaderModel("gradientEnvModel()").apply {
        val ifTexCoords: StageInterfaceNode

        vertexStage {
            ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
            positionOutput = simpleVertexPositionNode().outVec4
        }
        fragmentStage {
            val sampler = textureSamplerNode(textureNode("gradTex"), ifTexCoords.output)
            val linColor = gammaNode(sampler.outColor).outColor
            colorOutput(unlitMaterialNode(linColor).outColor)
        }
    }

}