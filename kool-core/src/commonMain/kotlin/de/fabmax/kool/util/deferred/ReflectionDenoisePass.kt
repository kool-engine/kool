package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.Texture2dInput
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color

class ReflectionDenoisePass(reflectionPass: ReflectionPass) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "ReflectionDenoisePass"
            setSize(reflectionPass.config.width, reflectionPass.config.height)
            addColorTexture(TexFormat.RGBA)
            clearDepthTexture()
        }) {

    private val noisyReflections = Texture2dInput("noisyRefl", reflectionPass.colorTexture)
    private val positionFlags = Texture2dInput("positionFlags")

    init {
        clearColor = Color(0f, 0f, 0f, 0f)

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
                        val noisyRefl = texture2dNode("noisyRefl")
                        val depthTex = texture2dNode("positionFlags")
                        val blurNd = addNode(BlurNode(noisyRefl, depthTex, stage))
                        blurNd.inScreenPos = ifTexCoords.output
                        colorOutput(blurNd.outColor)
                    }
                }
                shader = ModeledShader(model).apply {
                    onPipelineSetup += { builder, _, _ ->
                        builder.depthTest = DepthCompareOp.ALWAYS
                        builder.blendMode = BlendMode.DISABLED
                    }
                    onPipelineCreated += { _, _, _ ->
                        noisyReflections.connect(model)
                        positionFlags.connect(model)
                    }
                }
            }
        }

        dependsOn(reflectionPass)
    }

    fun setPositionInput(materialPass: MaterialPass) {
        positionFlags(materialPass.positionFlags)
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private inner class BlurNode(val noisyRefl: Texture2dNode, val depthTex: Texture2dNode, shaderGraph: ShaderGraph) :
            ShaderNode("blurNode", shaderGraph) {

        var inScreenPos = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
        val outColor = ShaderNodeIoVar(ModelVar4f("colorOut"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            dependsOn(noisyRefl)
            dependsOn(depthTex)
            super.setup(shaderGraph)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                int blurSize = ${ReflectionPass.NOISE_SIZE};
                vec2 texelSize = 1.0 / vec2(textureSize(${noisyRefl.name}, 0));
                float depthOri = ${generator.sampleTexture2d(depthTex.name, inScreenPos.ref2f())}.z;
                float depthThreshold = max(0.3, depthOri * 0.05);
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