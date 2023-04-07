package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.div
import de.fabmax.kool.modules.ksl.lang.y
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenCubeVertexStage
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.createUint8Buffer
import de.fabmax.kool.util.logD
import kotlin.math.PI

class GradientCubeGenerator(scene: Scene, gradientTex: Texture1d, size: Int = 128) :
    OffscreenRenderPassCube(Node(), renderPassConfig {
        name = "GradientEnvGenerator"
        setSize(size, size)
        addColorTexture(TexFormat.RGBA_F16)
        clearDepthTexture()
    }) {

    init {
        drawNode.apply {
            textureMesh {
                generate {
                    cube {
                        centered()
                    }
                }
                shader = GradientEnvShader(gradientTex)
            }
        }

        // remove render pass as soon as the gradient texture is loaded and rendered
        onAfterDraw += { ctx ->
            logD { "Generated gradient cube map" }
            scene.removeOffscreenPass(this)
            ctx.runDelayed(1) { dispose(ctx) }
        }
    }

    private fun gradientEnvModel() = ShaderModel("gradientEnvModel()").apply {
        val ifFragPos: StageInterfaceNode

        vertexStage {
            val mvpNode = mvpNode()
            val localPos = attrPositions().output
            val worldPos = vec3TransformNode(localPos, mvpNode.outModelMat, 1f).outVec3
            ifFragPos = stageInterfaceNode("ifFragPos", worldPos)
            positionOutput = vec4TransformNode(localPos, mvpNode.outMvpMat).outVec4
        }
        fragmentStage {
            val uv = addNode(PosToUvNode(stage))
            uv.inPos = ifFragPos.output
            val sampler = texture2dSamplerNode(texture2dNode("gradTex"), uv.outUv)
            val linColor = gammaNode(sampler.outColor).outColor
            colorOutput(linColor)
        }
    }

    private class PosToUvNode(graph: ShaderGraph) : ShaderNode("posToUv", graph) {
        var inPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
        val outUv = ShaderNodeIoVar(ModelVar2f("outUv"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inPos)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outUv.declare()} = vec2(acos(normalize(${inPos.ref3f()}).y) / $PI, 0.0);")
        }
    }

    companion object {
        suspend fun makeGradientTex(gradient: ColorGradient, ctx: KoolContext, size: Int = 256): Texture2d {
            val buf = createUint8Buffer(size * 4)

            val color = MutableColor()
            for (i in 0 until size) {
                gradient.getColorInterpolated(1f - i.toFloat() / size, color)
                buf[i * 4 + 0] = ((color.r * 255f).toInt().toByte())
                buf[i * 4 + 1] = ((color.g * 255f).toInt().toByte())
                buf[i * 4 + 2] = ((color.b * 255f).toInt().toByte())
                buf[i * 4 + 3] = (255.toByte())
            }

            val data = TextureData2d(buf, size, 1, TexFormat.RGBA)
            val props = TextureProps(addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE, mipMapping = false, maxAnisotropy = 1)
            return ctx.assetMgr.loadAndPrepareTexture(data, props, "gradientEnvTex")
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