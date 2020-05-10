package de.fabmax.kool.util.aoMapGen

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.OffscreenRenderPass2D
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.mesh

class AoDenoisePass(aoPass: AmbientOcclusionPass) : OffscreenRenderPass2D(Group(), aoPass.texWidth, aoPass.texHeight, colorFormat = TexFormat.R) {
    init {
        (drawNode as Group).apply {
            camera = OrthographicCamera().apply {
                projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN
                isKeepAspectRatio = false
                left = 0f
                right = 1f
                top = 1f
                bottom = 0f
            }
            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
                    }
                }

                val model = ShaderModel("AoDenoisePass").apply {
                    val ifTexCoords: StageInterfaceNode
                    vertexStage {
                        ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                        positionOutput = simpleVertexPositionNode().outPosition
                    }
                    fragmentStage {
                        val noisyAo = textureNode("noisyAo")
                        val blurNd = addNode(BlurNode(noisyAo, stage))
                        blurNd.inScreenPos = ifTexCoords.output
                        colorOutput = blurNd.outColor
                    }
                }
                pipelineLoader = ModeledShader(model).apply {
                    onCreated += {
                        model.findNode<TextureNode>("noisyAo")!!.sampler.texture = aoPass.colorTexture
                    }
                }
            }
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private inner class BlurNode(val noisyAo: TextureNode, shaderGraph: ShaderGraph) : ShaderNode("blurNode", shaderGraph) {
        var inScreenPos = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))

        val outColor = ShaderNodeIoVar(ModelVar4f("colorOut"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            dependsOn(noisyAo)
            dependsOn(inScreenPos)
            super.setup(shaderGraph)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                int blurSize = 4;
                vec2 texelSize = 1.0 / vec2(textureSize(${noisyAo.name}, 0));
                float result = 0.0;
                vec2 hlim = vec2(float(-blurSize) * 0.5 + 0.5);
                for (int x = 0; x < blurSize; x++) {
                    for (int y = 0; y < blurSize; y++) {
                        vec2 offset = (hlim + vec2(float(x), float(y))) * texelSize;
                        result += ${generator.sampleTexture2d(noisyAo.name, "${inScreenPos.ref2f()} + offset")}.r;
                    }
                }
                result /= float(blurSize * blurSize);
                ${outColor.declare()} = vec4(result, result, result, 1.0);
            """)
        }
    }
}