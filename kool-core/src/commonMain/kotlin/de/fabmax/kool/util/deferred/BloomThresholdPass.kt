package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.Texture2dInput
import de.fabmax.kool.pipeline.shading.Vec2fInput
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color

class BloomThresholdPass(val cfg: DeferredPipelineConfig, val pbrPass: PbrLightingPass) : OffscreenRenderPass2d(Group(), renderPassConfig {
    name = "BloomThresholdPass"
    setSize(0, 0)
    addColorTexture(TexFormat.RGBA_F16)
    clearDepthTexture()
}) {

    private var samples = 3
    var outputShader = ThresholdShader(samples, cfg.bloomAvgDownSampling)
        private set

    private val quad: Mesh

    init {
        outputShader.inputTexture(pbrPass.colorTexture)
        dependsOn(pbrPass)

        clearColor = Color.RED

        (drawNode as Group).apply {
            isFrustumChecked = false
            quad = mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                isFrustumChecked = false
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
                    }
                }
                shader = outputShader
            }
            +quad
        }
    }

    fun setupDownSampling(samples: Int) {
        if (this.samples != samples) {
            this.samples = samples
            val thresholds = Vec2f(outputShader.thresholds.value)

            outputShader = ThresholdShader(samples, cfg.bloomAvgDownSampling)
            outputShader.thresholds.value = thresholds
            outputShader.inputTexture(pbrPass.colorTexture)
            quad.shader = outputShader
        }
    }

    class ThresholdShader(samples: Int, avgDownSampling: Boolean) : ModeledShader(shaderModel(samples, avgDownSampling)) {
        val inputTexture = Texture2dInput("tInput")
        val thresholds = Vec2fInput("uThreshold", Vec2f(1f, 2f))

        var lowerThreshold: Float
            get() = thresholds.value.x
            set(value) {
                thresholds.value = Vec2f(value, thresholds.value.y)
            }
        var upperThreshold: Float
            get() = thresholds.value.y
            set(value) {
                thresholds.value = Vec2f(thresholds.value.x, value)
            }

        override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
            inputTexture.connect(model)
            thresholds.connect(model)
            super.onPipelineCreated(pipeline, mesh, ctx)
        }

        companion object {
            fun shaderModel(samples: Int, avgDownSampling: Boolean) = ShaderModel("defaultBlurModel").apply {
                val ifTexCoords: StageInterfaceNode

                vertexStage {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                    positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
                }
                fragmentStage {
                    addNode(ThresholdNode(samples, avgDownSampling, stage).apply {
                        inTexture = texture2dNode("tInput")
                        inTexCoord = ifTexCoords.output
                        inThreshold = pushConstantNode2f("uThreshold").output
                        colorOutput(outColor)
                    })
                }
            }
        }
    }

    private class ThresholdNode(val samples: Int, val avgDownSampling: Boolean, graph: ShaderGraph) : ShaderNode("thresholdNd", graph) {
        lateinit var inTexture: Texture2dNode
        lateinit var inTexCoord: ShaderNodeIoVar
        lateinit var inThreshold: ShaderNodeIoVar

        val outColor = ShaderNodeIoVar(ModelVar4f("outColor"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inTexture)
            dependsOn(inTexCoord, inThreshold)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendFunction("sampleBlurInTex", """
                vec4 sampleWithThreshold(vec2 texCoord, vec2 threshold) {
                    vec4 color = ${generator.sampleTexture2d(inTexture.name, "texCoord")};
                    float brightness = dot(color.rgb, vec3(1.0, 1.0, 1.0)) * 0.333;
                    float w = smoothstep(threshold.x, threshold.y, brightness);
                    return vec4(color.rgb * w, brightness * w);
                }
            """)

            // avg brightness
            generator.appendMain("""
                ${outColor.declare()} = vec4(0.0);
                vec2 step = vec2(1.0) / vec2(textureSize(${inTexture.name}, 0));
                vec2 offset = vec2(float($samples)) * -0.5;
                vec4 tmpColor = vec4(0.0);
            """)
            for (y in 0 until samples) {
                for (x in 0 until samples) {
                    if (avgDownSampling) {
                        // average brightness
                        // for some reason the if is required to avoid artifacts?!
                        generator.appendMain("""
                            tmpColor = sampleWithThreshold(${inTexCoord.ref2f()} + (vec2(float($x), float($y)) + offset) * step, ${inThreshold.ref2f()});
                            if (tmpColor.a > 0.0) { $outColor += tmpColor; }
                        """)
                    } else {
                        // max brightness
                        generator.appendMain("""
                            tmpColor = sampleWithThreshold(${inTexCoord.ref2f()} + (vec2(float($x), float($y)) + offset) * step, ${inThreshold.ref2f()});
                            if (tmpColor.a > $outColor.a) { $outColor = tmpColor; }
                        """)
                    }
                }
            }
            if (avgDownSampling) {
                generator.appendMain("$outColor.rgb *= float(${1f / (samples * samples)});")
            }
            generator.appendMain("$outColor.a = 1.0;")
        }
    }
}