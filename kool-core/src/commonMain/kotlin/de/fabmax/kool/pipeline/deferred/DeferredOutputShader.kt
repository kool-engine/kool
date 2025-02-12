package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.util.Color

class DeferredOutputShader(cfg: DeferredPipelineConfig, deferredPipeline: DeferredPipeline) :
    KslShader(
        Model(cfg),
        PipelineConfig(
            blendMode = BlendMode.DISABLED,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = cfg.outputDepthTest
        )
    )
{
    private val noBloomMap = SingleColorTexture(Color(0f, 0f, 0f, 0f))

    var bloomMap by texture2d("bloom", noBloomMap)
    var isBloomEnabled = true

    private var currentLighting by texture2d("currentLighting")
    private var depthTex by texture2d("currentDepth")

    private var vignetteCfg by uniform3f("uVignetteCfg", Vec3f(0.4f, 0.71f, 0.25f))
    val vignetteStrength: Float
        get() = vignetteCfg.z
    val vignetteInnerRadius: Float
        get() = vignetteCfg.x
    val vignetteOuterRadius: Float
        get() = vignetteCfg.y

    var chromaticAberrationStrength by uniform3f("uChromaticAberration", Vec3f(-0.001f, 0.0f, 0.001f))
    var chromaticAberrationStrengthBloom by uniform3f("uChromaticAberration", Vec3f(-0.003f, 0.0f, 0.003f))

    init {
        deferredPipeline.scene.onRelease { noBloomMap.release() }
    }

    fun setupVignette(strength: Float = vignetteStrength, innerRadius: Float = vignetteInnerRadius, outerRadius: Float = vignetteOuterRadius) {
        vignetteCfg = Vec3f(innerRadius, outerRadius, strength)
    }

    fun setDeferredInput(current: DeferredPasses) {
        createdPipeline?.swapPipelineData(current)
        currentLighting = current.lightingPass.colorTexture
        depthTex = current.materialPass.depthTexture
        if (isBloomEnabled) {
            bloomMap = current.bloomPass?.bloomMap ?: noBloomMap
        } else {
            bloomMap = noBloomMap
        }
    }

    class Model(cfg: DeferredPipelineConfig) : KslProgram("Deferred output shader") {
        init {
            val texCoord = interStageFloat2("uv")
            fullscreenQuadVertexStage(texCoord)

            fragmentStage {
                val funSampleAberrated = functionFloat4("sampleAberrated") {
                    val tex = paramColorTex2d()
                    val uv = paramFloat2()
                    val strength = paramFloat3()

                    body {
                        val centerUv = float2Var(uv - 0.5f.const)
                        val str = float3Var(strength * smoothStep(0.2f.const, 0.45f.const, length(centerUv)))

                        val uvR = float2Var(centerUv * (1f.const + str.r))
                        val uvG = float2Var(centerUv * (1f.const + str.g))
                        val uvB = float2Var(centerUv * (1f.const + str.b))
                        val r = sampleTexture(tex, uvR + 0.5f.const).r
                        val g = sampleTexture(tex, uvG + 0.5f.const).g
                        val b = sampleTexture(tex, uvB + 0.5f.const).b

                        float4Value(r, g, b, 1f.const)
                    }
                }

                val funApplyVignette = functionFloat3("applyVignette") {
                    val inputColor = paramFloat3()
                    val str = paramFloat3()
                    val uv = paramFloat2()

                    body {
                        val screen = float2Var(uv - 0.5f.const)
                        val r = float1Var(length(screen))

                        val w = float1Var(smoothStep(str.x, str.y, r) * str.z)
                        mix(inputColor, Vec3f.ZERO.const, w)
                    }
                }

                main {
                    val currentLighting = texture2d("currentLighting")

                    val linearColor = float4Var()
                    if (cfg.isWithChromaticAberration) {
                        val str = uniformFloat3("uChromaticAberration")
                        linearColor set funSampleAberrated(currentLighting, texCoord.output, str)
                    } else {
                        linearColor set sampleTexture(currentLighting, texCoord.output)
                    }

                    if (cfg.isWithBloom) {
                        val bloomInput = texture2d("bloom")
                        if (cfg.isWithChromaticAberration) {
                            val str = uniformFloat3("uChromaticAberrationBloom")
                            linearColor += funSampleAberrated(bloomInput, texCoord.output, str)
                        } else {
                            linearColor += sampleTexture(bloomInput, texCoord.output)
                        }
                    }

                    val srgb = float3Var(convertColorSpace(linearColor.rgb, ColorSpaceConversion.LinearToSrgbHdr()))
                    if (cfg.isWithVignette) {
                        srgb set funApplyVignette(srgb, uniformFloat3("uVignetteCfg"), texCoord.output)
                    }
                    colorOutput(srgb)

                    outDepth set sampleTexture(texture2d("currentDepth", TextureSampleType.UNFILTERABLE_FLOAT), texCoord.output).r
                }
            }
        }
    }
}