package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.FloatInput
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.Texture2dInput
import de.fabmax.kool.pipeline.shading.Vec3fInput
import de.fabmax.kool.scene.Mesh

class DeferredOutputShader(private val pbrOutput: Texture2d, private val depth: Texture2d, bloom: Texture2d?, private val depthMode: DepthCompareOp) :
    ModeledShader(outputModel(bloom != null)) {

    val bloomStrength = FloatInput("uBloomStrength", 0.5f)
    val bloomMap = Texture2dInput("bloom", bloom)

    private val vignetteCfg = Vec3fInput("uVignetteCfg", Vec3f(0.4f, 0.71f, 0.25f))
    val vignetteStrength: Float
        get() = vignetteCfg.value.z
    val vignetteInnerRadius: Float
        get() = vignetteCfg.value.x
    val vignetteOuterRadius: Float
        get() = vignetteCfg.value.y

    fun setupVignette(strength: Float = vignetteStrength, innerRadius: Float = vignetteInnerRadius, outerRadius: Float = vignetteOuterRadius) {
        vignetteCfg.value = Vec3f(innerRadius, outerRadius, strength)
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.depthTest = depthMode
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        model.findNode<Texture2dNode>("deferredPbrOutput")?.sampler?.texture = pbrOutput
        model.findNode<Texture2dNode>("deferredDepthOutput")?.sampler?.texture = depth
        bloomStrength.connect(model)
        bloomMap.connect(model)
        vignetteCfg.connect(model)
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        private fun outputModel(isWithBloom: Boolean) = ShaderModel("DeferredOutputShader").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
            }
            fragmentStage {
                val linearColor = texture2dSamplerNode(texture2dNode("deferredPbrOutput"), ifTexCoords.output).outColor
                val color = if (isWithBloom) {
                    val bloom = texture2dSamplerNode(texture2dNode("bloom"), ifTexCoords.output).outColor
                    val bloomStrength = pushConstantNode1f("uBloomStrength").output
                    val bloomScaled = multiplyNode(bloom, bloomStrength).output
                    val composed = addNode(linearColor, bloomScaled).output
                    hdrToLdrNode(composed).outColor
                } else {
                    hdrToLdrNode(linearColor).outColor
                }
                addNode(VignetteNode(stage)).apply {
                    inColor = color
                    inTexCoord = ifTexCoords.output
                    inVignetteCfg = pushConstantNode3f("uVignetteCfg").output
                    colorOutput(outColor)
                }
                val depthSampler = texture2dSamplerNode(texture2dNode("deferredDepthOutput"), ifTexCoords.output)
                depthOutput(depthSampler.outColor)
            }
        }
    }

    private class VignetteNode(graph: ShaderGraph) : ShaderNode("vignette", graph) {
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
}