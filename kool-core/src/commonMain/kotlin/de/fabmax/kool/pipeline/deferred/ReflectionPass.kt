package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.releaseWith
import kotlin.random.Random

class ReflectionPass(val baseReflectionStep: Float) :
    OffscreenPass2d(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RGBA),
        Vec2i(128),
        name = "reflection-denoise"
    )
{

    private val ssrShader = ReflectionShader()

    var roughnessThresholdLow by ssrShader::roughnessThresholdLow
    var roughnessThresholdHigh by ssrShader::roughnessThresholdHigh
    var scrSpcReflectionIterations by ssrShader::maxIterations

    init {
        drawNode.apply {
            addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
                generateFullscreenQuad()
                shader = ssrShader
            }

            val noiseScaleVec = MutableVec2f()
            onUpdate += {
                ssrShader.noiseScale = noiseScaleVec.set(
                    this@ReflectionPass.width / NOISE_SIZE.toFloat(),
                    this@ReflectionPass.height / NOISE_SIZE.toFloat()
                )
            }
        }
    }

    fun setInput(lightingPass: PbrLightingPass, materialPass: MaterialPass) {
        ssrShader.createdPipeline?.swapPipelineData(materialPass)
        ssrShader.positionFlags = materialPass.positionFlags
        ssrShader.normalRoughness = materialPass.normalRoughness
        ssrShader.lightingPass = lightingPass.colorTexture
    }

    private inner class ReflectionShader : KslShader(ssrShaderModel(), FullscreenShaderUtil.fullscreenShaderPipelineCfg) {
        var positionFlags by texture2d("positionFlags")
        var normalRoughness by texture2d("normalRoughness")
        var lightingPass by texture2d("lightingPass")
        val ssrNoise by texture2d("ssrNoise", generateNoiseTex().also { it.releaseWith(this@ReflectionPass) })

        var roughnessThresholdLow by uniform1f("uRoughThreshLow", 0.5f)
        var roughnessThresholdHigh by uniform1f("uRoughThreshHigh", 0.6f)
        var maxIterations by uniform1i("uMaxIterations", 24)
        var noiseScale by uniform2f("uNoiseScale")
    }

    private fun ssrShaderModel() = KslProgram("Screen space reflection pass").apply {
        val texCoord = interStageFloat2("uv")

        fullscreenQuadVertexStage(texCoord)

        fragmentStage {
            val positionFlags = texture2d("positionFlags")
            val normalRough = texture2d("normalRoughness")
            val lightingPass = texture2d("lightingPass")
            val ssrNoise = texture2d("ssrNoise")

            val uNoiseScale = uniformFloat2("uNoiseScale")
            val uRoughnessThreshLow = uniformFloat1("uRoughThreshLow")
            val uRoughnessThreshHigh = uniformFloat1("uRoughThreshHigh")
            val uMaxIterations = uniformInt1("uMaxIterations")

            main {
                val uv = texCoord.output
                val posFlags = float4Var(sampleTexture(positionFlags, uv))
                val normalRoughness = float4Var(sampleTexture(normalRough, uv))
                val viewPos = posFlags.xyz
                val viewNormal = normalRoughness.xyz
                val roughness = normalRoughness.a

                `if`(roughness gt uRoughnessThreshHigh) {
                    discard()
                }
                val roughnessWeight = float1Var(1f.const - smoothStep(uRoughnessThreshLow, uRoughnessThreshHigh, roughness))

                val camData = deferredCameraData()
                val noiseCoord = float2Var(uv * uNoiseScale)
                val noise = float4Var(sampleTexture(ssrNoise, noiseCoord))
                val viewDir = float3Var(normalize(viewPos))
                val reflectDir = float3Var(reflect(viewDir, normalize(viewNormal)))
                val rayDir = float3Var(normalize(reflectDir + ((noise.xyz - 0.5f.const) * 2f.const) * roughness))

                val baseStepFac = 0.02f.const
                val stepIncFac = 1.2f.const
                val maxRefinements = 5.const

                val rayPos = float3Var(viewPos)
                val rayStepPos = float3Var(rayPos)
                val rayStep = float1Var(-viewPos.z * baseStepFac)
                val rayOffset = float1Var(noise.a)

                val sampleDepth = float1Var(0f.const)
                val dDepth = float1Var(0f.const)
                val refineHit = bool1Var(false.const)
                val iSteps = int1Var(0.const)
                val iRefinementSteps = int1Var(0.const)

                `while`((iSteps lt uMaxIterations) and (iRefinementSteps lt maxRefinements)) {
                    rayStepPos += rayDir * rayStep
                    rayPos set rayStepPos + rayDir * rayStep * rayOffset * stepIncFac
                    val projPos = float4Var(camData.projMat * float4Value(rayPos, 1f.const))
                    val samplePos = float3Var((projPos.xyz / projPos.w) * 0.5f.const + 0.5f.const)

                    `if`((samplePos.x lt 0f.const) or (samplePos.x gt 1f.const) or
                                (samplePos.y lt 0f.const) or (samplePos.y gt 1f.const) or
                                (samplePos.z gt 1f.const)
                    ) {
                        // sample position has left screen bounds
                        `break`()
                    }

                    sampleDepth set sampleTexture(positionFlags, samplePos.xy, 0f.const).z
                    // set a large depth if sampleDepth is positive (clear value)
                    sampleDepth -= 1e5f.const * step(0.1f.const, sampleDepth)

                    // diff between ray position depth and scene depth
                    //   negative -> ray pos is behind scene depth, i.e. covered by an object
                    //   positive -> ray pos is in front of scene
                    dDepth set rayPos.z - sampleDepth

                    `if`(!refineHit) {
                        // search for hit
                        `if`((dDepth lt 0f.const) and (dDepth gt -rayStep - 0.2f.const)) {
                            // hit -> roll back position to previous ray position and start refinement
                            rayStepPos set rayPos - rayDir * rayStep
                            rayOffset set 0f.const
                            rayStep *= 0.5f.const
                            refineHit set true.const
                        }.`else` {
                            // no hit, increase step size
                            rayStep *= stepIncFac
                        }
                    }.`else` {
                        // refine hit position
                        rayStep set 0.5f.const * abs(rayStep) * sign(dDepth)
                        iRefinementSteps += 1.const
                    }
                    iSteps += 1.const
                }

                rayPos set rayPos + rayDir * rayStep
                val projPos = float4Var(camData.projMat * float4Value(rayPos, 1f.const))
                val samplePos = float3Var((projPos.xyz / projPos.w) * 0.5f.const + 0.5f.const)
                if (KoolSystem.requireContext().backend.isInvertedNdcY) {
                    samplePos.y set 1f.const - samplePos.y
                }

                val sampleWeight = float1Var(
                    smoothStep(0f.const, 0.05f.const, samplePos.x) * (1f.const - smoothStep(0.95f.const, 1f.const, samplePos.x)) *
                            smoothStep(0f.const, 0.05f.const, samplePos.y) * (1f.const - smoothStep(0.95f.const, 1f.const, samplePos.y)) *
                            (1f.const - step(0.9999f.const, samplePos.z)) * (1f.const - smoothStep(0f.const, -rayPos.z / 10f.const, abs(dDepth)))
                )

                val reflectionColor = float3Var(sampleTexture(lightingPass, samplePos.xy, 0f.const).rgb)
                val reflectionAlpha = sampleWeight * roughnessWeight
                val outColor = float3Var(convertColorSpace(reflectionColor, ColorSpaceConversion.LinearToSrgb()))
                colorOutput(outColor, reflectionAlpha)
            }
        }
    }

    companion object {
        const val NOISE_SIZE = 4

        private fun generateNoiseTex(): Texture2d {
            val sz = NOISE_SIZE
            val buf = Uint8Buffer(sz * sz * 4)
            val rand = Random(0x1deadb0b)
            val vec = MutableVec3f()
            for (i in 0 until (sz * sz)) {
                do {
                    vec.set(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), rand.randomF(-1f, 1f))
                } while (vec.length() > 1f)
                vec.norm().mul(0.25f)
                buf[i * 4 + 0] = ((vec.x + 1f) * 127.5f).toInt().toUByte()
                buf[i * 4 + 1] = ((vec.y + 1f) * 127.5f).toInt().toUByte()
                buf[i * 4 + 2] = ((vec.z + 1f) * 127.5f).toInt().toUByte()
                buf[i * 4 + 3] = rand.randomI(0..255).toUByte()
            }
            val data = BufferedImageData2d(buf, sz, sz, TexFormat.RGBA)
            return Texture2d(format = TexFormat.RGBA, MipMapping.Off, SamplerSettings().nearest(), "ssr_noise_tex") { data }
        }
    }
}