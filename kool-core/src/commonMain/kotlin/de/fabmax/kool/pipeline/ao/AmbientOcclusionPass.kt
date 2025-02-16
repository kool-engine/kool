package de.fabmax.kool.pipeline.ao

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenShaderPipelineCfg
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.releaseWith
import kotlin.math.*

class AmbientOcclusionPass(val aoSetup: AoSetup, width: Int, height: Int) :
    OffscreenPass2d(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.R),
        initialSize = Vec2i(width, height),
        name = "ambient-occlusion"
    )
{

    var sceneCam: Camera? = null

    val aoPassShader = AoPassShader()

    var fwdNormalDepth: Texture2d? by aoPassShader::viewSpaceTex
    var deferredPosition: Texture2d? by aoPassShader::viewSpaceTex
    var deferredNormal: Texture2d? by aoPassShader::normalTex

    var radius: Float by aoPassShader::uRadius
    var strength: Float by aoPassShader::uStrength
    var power: Float by aoPassShader::uPower
    var bias: Float by aoPassShader::uBias

    var kernelSz = 16
        get() = aoPassShader.uKernelSize
        set(value) {
            if (value != field) {
                field = value
                generateKernels(value)
            }
        }

    init {
        mirrorIfInvertedClipY()
        drawNode.apply {
            addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
                generateFullscreenQuad()

                shader = aoPassShader

                val tmpVec2f = MutableVec2f()
                onUpdate += {
                    sceneCam?.let {
                        aoPassShader.uProj = it.proj
                        aoPassShader.uInvProj = it.invProj
                    }
                    aoPassShader.uNoiseScale = tmpVec2f.set(
                        this@AmbientOcclusionPass.width / NOISE_TEX_SIZE.toFloat(),
                        this@AmbientOcclusionPass.height / NOISE_TEX_SIZE.toFloat()
                    )
                }
            }
        }

        fwdNormalDepth = aoSetup.linearDepthPass?.normalDepthMap
        generateKernels(16)
    }

    private fun generateKernels(nKernels: Int) {
        val n = min(nKernels, MAX_KERNEL_SIZE)

        val scales = (0 until n)
            .map { lerp(0.1f, 1f, (it.toFloat() / n).pow(2)) }

        for (i in 0 until n) {
            val xi = hammersley(i, n)
            val phi = 2f * PI.toFloat() * xi.x
            val cosTheta = sqrt((1f - xi.y))
            val sinTheta = sqrt(1f - cosTheta * cosTheta)

            val k = MutableVec3f(sinTheta * cos(phi), sinTheta * sin(phi), cosTheta)
            aoPassShader.uKernel[i] = k.norm().mul(scales[i])
        }
        aoPassShader.uKernelSize = n
    }

    private fun radicalInverse(pBits: Int): Float {
        var bits = pBits.toLong()
        bits = (bits shl 16) or (bits shr 16)
        bits = ((bits and 0x55555555) shl 1) or ((bits and 0xAAAAAAAA) shr 1)
        bits = ((bits and 0x33333333) shl 2) or ((bits and 0xCCCCCCCC) shr 2)
        bits = ((bits and 0x0F0F0F0F) shl 4) or ((bits and 0xF0F0F0F0) shr 4)
        bits = ((bits and 0x00FF00FF) shl 8) or ((bits and 0xFF00FF00) shr 8)
        return bits.toFloat() / 0x100000000
    }

    private fun hammersley(i: Int, n: Int): Vec2f {
        return Vec2f(i.toFloat() / n.toFloat(), radicalInverse(i))
    }

    private fun lerp(a: Float, b: Float, f: Float): Float {
        return a + f * (b - a)
    }

    private fun aoPassProg() = KslProgram("Ambient Occlusion Pass").apply {
        val uv = interStageFloat2("uv")

        fullscreenQuadVertexStage(uv)

        fragmentStage {
            val noiseTex = texture2d("noiseTex")
            val viewSpaceTex = texture2d("viewSpaceTex")

            val uProj = uniformMat4("uProj")
            val uInvProj = uniformMat4("uInvProj")
            val uKernel = uniformFloat3Array("uKernel", MAX_KERNEL_SIZE)
            val uNoiseScale = uniformFloat2("uNoiseScale")
            val uKernelSize = uniformInt1("uKernelRange")
            val uRadius = uniformFloat1("uRadius")
            val uStrength = uniformFloat1("uStrength")
            val uPower = uniformFloat1("uPower")
            val uBias = uniformFloat1("uBias")

            main {
                val normal: KslVectorExpression<KslFloat3, KslFloat1>
                val origin: KslVectorExpression<KslFloat3, KslFloat1>
                val depthComponent: String

                if (aoSetup.isDeferred) {
                    depthComponent = "z"
                    normal = float3Var(sampleTexture(texture2d("normalTex"), uv.output).xyz)
                    origin = float3Var(sampleTexture(viewSpaceTex, uv.output).xyz)

                } else {
                    depthComponent = "a"
                    val normalDepth = float4Var(sampleTexture(viewSpaceTex, uv.output))
                    normal = float3Var(normalDepth.xyz)

                    val depth = float1Var(normalDepth.w)
                    val projPos = float4Var(Vec4f(0f, 0f, 1f, 1f).const)
                    projPos.xy set uv.output * 2f.const - 1f.const
                    projPos set uInvProj * projPos
                    origin = float3Var(projPos.xyz / projPos.w)
                    origin set origin * (depth / origin.z)
                }

                val occlFac = float1Var(1f.const)
                val linDistance = float1Var(-origin.z)
                `if`(linDistance gt 0f.const) {
                    val sampleR = float1Var(uRadius)
                    `if`(sampleR lt 0f.const) {
                        sampleR *= -linDistance
                    }

                    `if`(linDistance lt sampleR * 200f.const) {
                        // compute kernel rotation
                        val noiseCoord = float2Var(uv.output * uNoiseScale)
                        val rotVec = float3Var(sampleTexture(noiseTex, noiseCoord, 0f.const).xyz * 2f.const - 1f.const)
                        val tan1 = float3Var(normalize(cross(Vec3f(1f, 1.1337e-6f, 1.1337e-6f).const, normal)))
                        val tan2 = float3Var(cross(tan1, normal))
                        val tan1Rot = float3Var(tan1 * rotVec.x + tan2 * rotVec.y)
                        val tan2Rot = float3Var(cross(normal, tan1Rot))
                        val tbn = mat3Var(mat3Value(tan1Rot, tan2Rot, normal))

                        val occlusion = float1Var(0f.const)
                        val occlusionDiv = float1Var(0f.const)
                        fori(0.const, uKernelSize) { i ->
                            val kernel = float3Var(tbn * uKernel[i])
                            if (!aoSetup.isDeferred && KoolSystem.requireContext().backend.isInvertedNdcY) {
                                kernel.y *= (-1f).const
                            }
                            val samplePos = float3Var(origin + kernel * sampleR)
                            val sampleProj = float4Var(uProj * float4Value(samplePos, 1f.const))
                            sampleProj.xyz set sampleProj.xyz / sampleProj.w

                            `if`((sampleProj.x gt (-1f).const) and (sampleProj.x lt 1f.const) and
                                    (sampleProj.y gt (-1f).const) and (sampleProj.y lt 1f.const)) {

                                val sampleUv = float2Var(sampleProj.xy * 0.5f.const + 0.5f.const)
                                if (aoSetup.isDeferred && KoolSystem.requireContext().backend.isInvertedNdcY) {
                                    sampleUv.y set 1f.const - sampleUv.y
                                }
                                val sampleDepth = sampleTexture(viewSpaceTex, sampleUv, 0f.const).float1(depthComponent)
                                val rangeCheck = float1Var(1f.const - smoothStep(0f.const, 1f.const, abs(origin.z - sampleDepth) / (4f.const * sampleR)))
                                val occlusionInc = float1Var(clamp((sampleDepth - (samplePos.z + uBias)) * 10f.const, 0f.const, 1f.const))
                                occlusion += occlusionInc * rangeCheck
                                occlusionDiv += 1f.const
                            }
                        }
                        occlusion /= occlusionDiv
                        val distFac = float1Var(1f.const - smoothStep(sampleR * 150f.const, sampleR * 200f.const, linDistance))
                        occlFac set pow(clamp(1f.const - occlusion * distFac * uStrength, 0f.const, 1f.const), uPower)
                    }
                }
                colorOutput(float4Value(occlFac, 0f.const, 0f.const, 1f.const))
            }
        }
    }

    inner class AoPassShader : KslShader(aoPassProg(), fullscreenShaderPipelineCfg) {
        var noiseTex by texture2d("noiseTex", generateNoiseTex().also { it.releaseWith(this@AmbientOcclusionPass) })
        var viewSpaceTex by texture2d("viewSpaceTex")
        var normalTex by texture2d("normalTex")

        val uKernel = uniform3fv("uKernel", MAX_KERNEL_SIZE)
        var uProj by uniformMat4f("uProj")
        var uInvProj by uniformMat4f("uInvProj")
        var uNoiseScale by uniform2f("uNoiseScale")
        var uKernelSize by uniform1i("uKernelRange", 16)
        var uRadius by uniform1f("uRadius", 1f)
        var uStrength by uniform1f("uStrength", 1.25f)
        var uPower by uniform1f("uPower", 1.5f)
        var uBias by uniform1f("uBias", 0.05f)
    }

    companion object {
        const val MAX_KERNEL_SIZE = 64
        const val NOISE_TEX_SIZE = 4

        private fun generateNoiseTex(): Texture2d {
            val noiseLen = NOISE_TEX_SIZE * NOISE_TEX_SIZE
            val buf = Uint8Buffer(4 * noiseLen)
            val rotAngles = (0 until noiseLen).map { 2f * PI.toFloat() * it / noiseLen }.shuffled()

            for (i in 0 until (NOISE_TEX_SIZE * NOISE_TEX_SIZE)) {
                val ang = rotAngles[i]
                val x = cos(ang)
                val y = sin(ang)
                buf[i*4+0] = ((x * 0.5f + 0.5f) * 255).toInt().toUByte()
                buf[i*4+1] = ((y * 0.5f + 0.5f) * 255).toInt().toUByte()
                buf[i*4+2] = 0u
                buf[i*4+3] = 1u
            }

            val data = BufferedImageData2d(buf, NOISE_TEX_SIZE, NOISE_TEX_SIZE, TexFormat.RGBA)
            return Texture2d(TexFormat.RGBA, MipMapping.Off, SamplerSettings().nearest(), "ao_noise_tex") { data }
        }
    }
}

class AoSetup private constructor(val linearDepthPass: NormalLinearDepthMapPass?) {
    val isDeferred: Boolean
        get() = linearDepthPass == null
    val isForward: Boolean
        get() = linearDepthPass != null

    companion object {
        fun deferred() = AoSetup(null)
        fun forward(linearDepthPass: NormalLinearDepthMapPass) = AoSetup(linearDepthPass)
    }
}
