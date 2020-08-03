package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.mesh

class ReflectionDenoisePass(reflectionPass: ReflectionPass, positionAo: Texture) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "ReflectionDenoisePass"
            setSize(reflectionPass.config.width, reflectionPass.config.height)
            addColorTexture(TexFormat.RGBA)
            clearDepthTexture()
        }) {

    init {
        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
                    }
                }

                val model = ShaderModel("ReflectionDenoisePass").apply {
                    val ifTexCoords: StageInterfaceNode
                    vertexStage {
                        ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                        positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
                    }
                    fragmentStage {
                        val noisyRefl = textureNode("noisyRefl")
                        val depthTex = textureNode("positionAo")
                        val blurNd = addNode(BlurNode(noisyRefl, depthTex, stage))
                        blurNd.inScreenPos = ifTexCoords.output
                        colorOutput(blurNd.outColor)
                    }
                }
                shader = ModeledShader(model).apply {
                    onPipelineCreated += { _, _, _ ->
                        model.findNode<TextureNode>("noisyRefl")!!.sampler.texture = reflectionPass.colorTexture
                        model.findNode<TextureNode>("positionAo")!!.sampler.texture = positionAo
                    }
                }
            }
        }

        dependsOn(reflectionPass)
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private inner class BlurNode(val noisyRefl: TextureNode, val depthTex: TextureNode, shaderGraph: ShaderGraph) :
            ShaderNode("blurNode", shaderGraph) {

        var inScreenPos = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
        var radius = ShaderNodeIoVar(ModelVar1fConst(1f))

        val outColor = ShaderNodeIoVar(ModelVar4f("colorOut"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            dependsOn(noisyRefl)
            dependsOn(depthTex)
            dependsOn(inScreenPos, radius)
            super.setup(shaderGraph)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                int blurSize = ${ReflectionPass.NOISE_SIZE};
                vec2 texelSize = 1.0 / vec2(textureSize(${noisyRefl.name}, 0));
                float depthOri = ${generator.sampleTexture2d(depthTex.name, inScreenPos.ref2f())}.z;
                float depthThreshold = ${radius.ref1f()} * 0.1;
                
                ${outColor.declare()} = vec4(0.0);
                float weight = 0.0;
                vec2 hlim = vec2(float(-blurSize) * 0.5 + 0.5);
                for (int x = 0; x < blurSize; x++) {
                    for (int y = 0; y < blurSize; y++) {
                        vec2 uv = ${inScreenPos.ref2f()} + (hlim + vec2(float(x), float(y))) * texelSize;
                        float w = 1.0 - step(depthThreshold, abs(${generator.sampleTexture2d(depthTex.name, "uv")}.z - depthOri)) * 0.99;
                        
                        $outColor += ${generator.sampleTexture2d(noisyRefl.name, "uv")} * w;
                        weight += w;
                    }
                }
                $outColor /= weight;
            """)
        }
    }
}