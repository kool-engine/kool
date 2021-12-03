package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.Texture2dInput
import de.fabmax.kool.pipeline.shading.Vec3fInput
import de.fabmax.kool.scene.Mesh

class DeferredOutputShader(cfg: DeferredPipelineConfig, bloom: Texture2d?) :
    ModeledShader(outputModel(cfg)) {

    private val currentLighting = Texture2dInput("currentLighting")
    private val depthTex = Texture2dInput("currentDepth")

    val bloomMap = Texture2dInput("bloom", bloom)

    private val depthMode: DepthCompareOp = cfg.outputDepthTest

    private val vignetteCfg = Vec3fInput("uVignetteCfg", Vec3f(0.4f, 0.71f, 0.25f))
    val vignetteStrength: Float
        get() = vignetteCfg.value.z
    val vignetteInnerRadius: Float
        get() = vignetteCfg.value.x
    val vignetteOuterRadius: Float
        get() = vignetteCfg.value.y

    val chromaticAberrationStrength = Vec3fInput("uChromaticAberration", Vec3f(-0.002f, 0.0f, 0.002f))
    val chromaticAberrationStrengthBloom = Vec3fInput("uChromaticAberrationBloom", Vec3f(-0.006f, 0.0f, 0.006f))

    fun setupVignette(strength: Float = vignetteStrength, innerRadius: Float = vignetteInnerRadius, outerRadius: Float = vignetteOuterRadius) {
        vignetteCfg.value = Vec3f(innerRadius, outerRadius, strength)
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.depthTest = depthMode
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        currentLighting.connect(model)
        depthTex.connect(model)
        bloomMap.connect(model)
        vignetteCfg.connect(model)
        chromaticAberrationStrength.connect(model)
        chromaticAberrationStrengthBloom.connect(model)
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    fun setDeferredInput(current: DeferredPasses) {
        currentLighting(current.lightingPass.colorTexture)
        depthTex(current.materialPass.depthTexture)
    }

    companion object {
        private fun outputModel(cfg: DeferredPipelineConfig) = ShaderModel("DeferredOutputShader").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
            }
            fragmentStage {
                val linearColor = if (cfg.isWithChromaticAberration) {
                    addNode(ChromaticAberrationSamplerNode(stage).apply {
                        inTexture = texture2dNode("currentLighting")
                        inTexCoord = ifTexCoords.output
                        inStrength = pushConstantNode3f("uChromaticAberration").output
                    }).outColor
                } else {
                    texture2dSamplerNode(texture2dNode("currentLighting"), ifTexCoords.output).outColor
                }

                val color = if (cfg.isWithBloom) {
                    val bloom = if (cfg.isWithChromaticAberration) {
                        addNode(ChromaticAberrationSamplerNode(stage).apply {
                            inTexture = texture2dNode("bloom")
                            inTexCoord = ifTexCoords.output
                            inStrength = pushConstantNode3f("uChromaticAberrationBloom").output
                        }).outColor
                    } else {
                        texture2dSamplerNode(texture2dNode("bloom"), ifTexCoords.output).outColor
                    }

                    val composed = addNode(linearColor, bloom).output
                    hdrToLdrNode(composed).outColor
                } else {
                    hdrToLdrNode(linearColor).outColor
                }

                if (cfg.isWithVignette) {
                    addNode(VignetteNode(stage)).apply {
                        inColor = color
                        inTexCoord = ifTexCoords.output
                        inVignetteCfg = pushConstantNode3f("uVignetteCfg").output
                        colorOutput(outColor)
                    }
                } else {
                    colorOutput(color)
                }

                val depthSampler = texture2dSamplerNode(texture2dNode("currentDepth"), ifTexCoords.output)
                depthOutput(depthSampler.outColor)
            }
        }
    }

    class VignetteNode(graph: ShaderGraph) : ShaderNode("vignette", graph) {
        lateinit var inColor: ShaderNodeIoVar
        lateinit var inTexCoord: ShaderNodeIoVar
        lateinit var inVignetteCfg: ShaderNodeIoVar
        val outColor = ShaderNodeIoVar(ModelVar4f("vignette_out"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inColor, inTexCoord, inVignetteCfg)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                ${outColor.declare()} = $inColor;
                if (${inVignetteCfg.ref3f()}.z > 0.0) {
                    float screenX = $inTexCoord.x - 0.5;
                    float screenY = $inTexCoord.y - 0.5;
                    float screenR = sqrt(screenX * screenX + screenY * screenY);
                    float vignetteW = smoothstep(${inVignetteCfg.ref3f()}.x, ${inVignetteCfg.ref3f()}.y, screenR) * ${inVignetteCfg.ref3f()}.z;
                    $outColor = mix($inColor, vec4(0.0, 0.0, 0.0, 1.0), vignetteW);
                }
            """)
        }
    }

    class ChromaticAberrationSamplerNode(graph: ShaderGraph) : ShaderNode("chromaticAberration_${graph.nextNodeId}", graph) {
        lateinit var inTexCoord: ShaderNodeIoVar
        lateinit var inTexture: Texture2dNode
        lateinit var inStrength: ShaderNodeIoVar
        val outColor = ShaderNodeIoVar(ModelVar4f("${name}_chromaticColorOut"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inTexCoord)
            dependsOn(inTexture)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendFunction("sampleAberrated", """
                vec4 sampleAberrated(sampler2D tex, vec2 uv, vec3 strength) {
                    vec2 centerUv = uv - vec2(0.5, 0.5);
                    float screenR = length(centerUv);
                    strength *= smoothstep(0.2, 0.45, screenR);
                    
                    vec2 uvRed = centerUv * (1.0 + strength.r);
                    vec2 uvGreen = centerUv * (1.0 + strength.g);
                    vec2 uvBlue = centerUv * (1.0 + strength.b);
                    
                    vec4 r = ${generator.sampleTexture2d("tex", "vec2(0.5) + uvRed")};
                    vec4 g = ${generator.sampleTexture2d("tex", "vec2(0.5) + uvGreen")};
                    vec4 b = ${generator.sampleTexture2d("tex", "vec2(0.5) + uvBlue")};
                    return vec4(r.r, g.g, b.b, (r.a + g.a + b.a) / 3.0);
                }
            """)

            generator.appendMain("""
                ${outColor.declare()} = sampleAberrated(${inTexture.name}, ${inTexCoord.ref2f()}, ${inStrength.ref3f()});
            """)
        }
    }
}