package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.Color

class BloomThresholdPass(deferredPipeline: DeferredPipeline, cfg: DeferredPipelineConfig) :
    OffscreenRenderPass2d(Node(), renderPassConfig {
        name = "BloomThresholdPass"
        addColorTexture(TexFormat.RGBA_F16)
        clearDepthTexture()
    }) {

    private val doAvgDownsampling = cfg.bloomAvgDownSampling
    private var samples = 3
    var outputShader = ThresholdShader(samples, cfg.bloomAvgDownSampling)
        private set

    private val quad: Mesh

    init {
        clearColor = Color.BLACK

        drawNode.apply {
            isFrustumChecked = false
            quad = addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
                isFrustumChecked = false
                generateFullscreenQuad()
                shader = outputShader
            }
        }
        drawNode.releaseWith(this)

        deferredPipeline.passes.forEach { dependsOn(it.lightingPass) }
    }

    fun setLightingInput(newPass: PbrLightingPass) {
        outputShader.inputTexture = newPass.colorTexture
    }

    fun setupDownSampling(samples: Int) {
        if (this.samples != samples) {
            this.samples = samples
            val tLower = outputShader.lowerThreshold
            val tUpper = outputShader.upperThreshold

            outputShader = ThresholdShader(samples, doAvgDownsampling)
            outputShader.lowerThreshold = tLower
            outputShader.upperThreshold = tUpper
            quad.shader = outputShader
        }
    }

    class ThresholdShader(samples: Int, avgDownSampling: Boolean) : KslShader(program(samples, avgDownSampling), pipelineCfg) {

        var inputTexture by texture2d("tInput")
        var lowerThreshold by uniform1f("uThresholdLower", 0.5f)
        var upperThreshold by uniform1f("uThresholdUpper", 1f)

        companion object {
            private val pipelineCfg = PipelineConfig().apply {
                depthTest = DepthCompareOp.DISABLED
            }

            private fun program(samples: Int, avgDownSampling: Boolean) = KslProgram("Bloom threshold pass").apply {
                val screenUv = interStageFloat2("uv")

                fullscreenQuadVertexStage(screenUv)

                fragmentStage {
                    val inputTex = texture2d("tInput")
                    val lowerThreshold = uniformFloat1("uThresholdLower")
                    val upperThreshold = uniformFloat1("uThresholdUpper")

                    val funSampleInput = functionFloat4("sampleInput") {
                        val sampleUv = paramFloat2("uv")
                        body {
                            val sample = float4Var(sampleTexture(inputTex, sampleUv))
                            `if` (any(isNan(sample))) {
                                sample set Vec4f.ZERO.const
                            }

                            val brightness = float1Var(dot(sample.rgb, Vec3f(0.333f).const))
                            val w = float1Var(smoothStep(lowerThreshold, upperThreshold, brightness))
                            float4Value(sample.rgb * w, brightness * w)
                        }
                    }

                    main {
                        val outputColor = float4Var(Vec4f.ZERO.const)
                        val step = float2Var(1f.const / textureSize2d(inputTex).toFloat2())
                        val offset = float2Var(Vec2f(samples.toFloat()).const * (-0.5f).const + 0.5f.const)
                        val sample = float4Var()

                        for (y in 0 until samples) {
                            for (x in 0 until samples) {
                                sample set funSampleInput(screenUv.output + (Vec2f(x.toFloat(), y.toFloat()).const + offset) * step)
                                if (avgDownSampling) {
                                    outputColor += sample
                                } else {
                                    `if`(sample.a gt outputColor.a) {
                                        outputColor set sample
                                    }
                                }
                            }
                        }

                        if (avgDownSampling) {
                            outputColor.rgb set outputColor.rgb * (1f / (samples * samples)).const
                        }
                        colorOutput(outputColor.rgb, 1f.const)
                    }
                }
            }
        }
    }
}