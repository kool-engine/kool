package de.fabmax.kool.pipeline.mssao

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.toVec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.depthToViewSpacePos
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.releaseWith

class MssaoPass(
    val gBuffer: GbufferPass,
    val downsampler: GbufferDownsamplingPass,
    val numLevels: Int
) : OffscreenRenderPass2d(
    drawNode = Node(),
    attachmentConfig = AttachmentConfig(
        colorAttachments = ColorAttachmentTextures(
            listOf(TextureAttachmentConfig(TexFormat.R, SamplerSettings().clamped().linear()))
        ),
        depthAttachment = DepthAttachmentTexture(),
        mipLevels = MipMode.Render(numLevels, MipMapRenderOrder.LowerResolutionFirst)
    ),
    initialSize = Vec2i(128),
    name = "mssao-ao-pass"
) {

    private var scene: Scene? = null
    private var sceneRenderCallback: SceneSizeRenderCallback? = null
    private val mssaoShader = MssaoShader()

    var aoPower = mssaoShader.aoPower
    var aoDistance = mssaoShader.maxDist

    private val mipLevelDatas = Array<MipLevelData?>(numLevels) { null }

    init {
        drawNode = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "mssao-ao-mesh").apply {
            generateFullscreenQuad()
            shader = mssaoShader
        }
    }

    fun install(scene: Scene) {
        this.scene = scene
        sceneRenderCallback = SceneSizeRenderCallback(this, scene)
        scene.addOffscreenPass(this)
    }

    override fun setupMipLevel(mipLevel: Int) {
        val pipeline = mssaoShader.createdPipeline ?: return

        // use individual bind groups for each mip-level to avoid changing uniform buffer contents while
        // previous mip-levels are not yet drawn
        var mipData = mipLevelDatas[mipLevel]
        if (mipData == null) {
            val lowerLevel = if (mipLevel < numLevels - 1) mipLevel + 1 else 0
            val mipLevelData = pipeline.pipelineData.copy()
            val samplerSettings = SamplerSettings().clamped().nearest().limitMipLevels(lowerLevel, 1)
            mipData = MipLevelData(mipLevel, mipLevelData, samplerSettings)
            mipLevelDatas[mipLevel] = mipData

            // previous pipelineData won't be auto-released with the pipeline itself (because it is replaced by
            // the new one) -> release it with the render pass instead
            pipeline.pipelineData.releaseWith(this)
        }
        pipeline.pipelineData = mipData.data

        // after mip-level bind group is set we can safely set the mip-level specific uniform values
        mipData.setup()
    }

    private inner class MipLevelData(val mipLevel: Int, val data: BindGroupData, val prevLevelSamplerSettings: SamplerSettings) {
        private var isConfigured = false

        fun setup() {
            updateParams()

            if (isConfigured) return
            isConfigured = true

            mssaoShader.mipLevel = mipLevel
            mssaoShader.lowerAoTex.set(colorTexture, prevLevelSamplerSettings)
            mssaoShader.lowerAoMipLevel = if (mipLevel < numLevels - 1) mipLevel + 1 else 0

            mssaoShader.gPrevNormalTex.set(downsampler.colorTexture)
            mssaoShader.gPrevDepthTex.set(downsampler.depthTexture)

            if (mipLevel == 0) {
                mssaoShader.gBufferLevel = 0
                mssaoShader.gBufferLowerLevel = 0
                mssaoShader.gNormalTex.set(gBuffer.colorTexture)
                mssaoShader.gDepthTex.set(gBuffer.depthTexture)
                for (i in sampleKernelDense.indices) {
                    mssaoShader.kernel[i] = sampleKernelDense[i]
                    mssaoShader.kernelSize = sampleKernelDense.size
                }

            } else {
                mssaoShader.gBufferLevel = mipLevel - 1
                mssaoShader.gBufferLowerLevel = mssaoShader.gBufferLevel + 1
                mssaoShader.gNormalTex.set(downsampler.colorTexture)
                mssaoShader.gDepthTex.set(downsampler.depthTexture)
                for (i in sampleKernelSparse.indices) {
                    mssaoShader.kernel[i] = sampleKernelSparse[i]
                    mssaoShader.kernelSize = sampleKernelSparse.size
                }
            }
        }

        private fun updateParams() {
            mssaoShader.aoPower = aoPower
            mssaoShader.maxDist = aoDistance
            scene?.let {
                mssaoShader.viewportHeight = it.mainRenderPass.height
                mssaoShader.camClipNear = it.camera.clipNear
                mssaoShader.camViewParams = it.camera.viewParams
            }
        }
    }

    private class MssaoShader : KslShader("mssao-ao-shader") {
        val kernel = uniform2iv("kernel", 36)
        var kernelSize by uniform1i("kernelSize")

        var aoPower by uniform1f("aoPower", 1.3f)
        var maxDist by uniform1f("maxDist", 2f)

        var viewportHeight by uniform1i("viewportHeight")
        var camViewParams by uniform4f("camViewParams")
        var camClipNear by uniform1f("camClipNear")

        var mipLevel by uniform1i("mipLevel")
        var lowerAoMipLevel by uniform1i("lowerAoMipLevel")
        var gBufferLevel by uniform1i("gBufferLevel")
        var gBufferLowerLevel by uniform1i("gBufferLowerLevel")

        val gNormalTex = texture2d("gNormalTex")
        val gDepthTex = texture2d("gDepthTex")
        val gPrevNormalTex = texture2d("gPrevNormalTex")
        val gPrevDepthTex = texture2d("gPrevDepthTex")
        val lowerAoTex = texture2d("lowerAoTex")

        init {
            pipelineConfig = PipelineConfig(blendMode = BlendMode.DISABLED, depthTest = DepthCompareOp.ALWAYS)
            program.mssaoProgram()
        }

        private fun KslProgram.mssaoProgram() {
            val uv = interStageFloat2()
            fullscreenQuadVertexStage(uv)

            fragmentStage {
                val kernel = uniformInt2Array("kernel", 36)
                val kernelSize = uniformInt1("kernelSize")

                val aoPower = uniformFloat1("aoPower")
                val maxDist = uniformFloat1("maxDist")
                val viewportHeight = uniformInt1("viewportHeight")
                val viewParams = uniformFloat4("camViewParams")
                val clipNear = uniformFloat1("camClipNear")

                val level = uniformInt1("mipLevel")
                val lowerLevel = uniformInt1("lowerAoMipLevel")
                val gBufferLevel = uniformInt1("gBufferLevel")
                val gBufferLowerLevel = uniformInt1("gBufferLowerLevel")

                val gDepth = texture2d("gDepthTex", TextureSampleType.UNFILTERABLE_FLOAT)
                val gNormal = texture2d("gNormalTex")
                val gPrevDepth = texture2d("gPrevDepthTex", TextureSampleType.UNFILTERABLE_FLOAT)
                val gPrevNormal = texture2d("gPrevNormalTex")
                val lowerAo = texture2d("lowerAoTex")

                val sampleBlurred = functionFloat1("sampleBlurred") {
                    val coord = paramInt2()
                    body {
                        val s = float1Var()

                        s += texelFetch(lowerAo, coord + int2Value(-1, -1), lowerLevel).x * (1/16f).const
                        s += texelFetch(lowerAo, coord + int2Value(1, -1), lowerLevel).x * (1/16f).const
                        s += texelFetch(lowerAo, coord + int2Value(-1, 1), lowerLevel).x * (1/16f).const
                        s += texelFetch(lowerAo, coord + int2Value(1, 1), lowerLevel).x * (1/16f).const

                        s += texelFetch(lowerAo, coord + int2Value(0, -1), lowerLevel).x * (1/8f).const
                        s += texelFetch(lowerAo, coord + int2Value(-1, 0), lowerLevel).x * (1/8f).const
                        s += texelFetch(lowerAo, coord + int2Value(1, 0), lowerLevel).x * (1/8f).const
                        s += texelFetch(lowerAo, coord + int2Value(0, 1), lowerLevel).x * (1/8f).const

                        s += texelFetch(lowerAo, coord, lowerLevel).x * (1/4f).const
                        s
                    }
                }

                main {

                    val clipXy = float2Var(uv.output * 2f.const - 1f.const)
                    val pxScale = float2Var(textureSize2d(gDepth, gBufferLevel).toFloat2())
                    val pxCoord = int2Var((uv.output * pxScale).toInt2())

                    val depth = float1Var(clipNear / texelFetch(gDepth, pxCoord, gBufferLevel).x)
                    val viewPos = float3Var(depthToViewSpacePos(depth, clipXy, viewParams))
                    val normal = float3Var(texelFetch(gNormal, pxCoord, gBufferLevel).xyz)
                    normal.z set float1Var(sqrt(1f.const - normal.x * normal.x - normal.y * normal.y))

                    val dSample = float1Var()
                    val posSample = float3Var()

                    // compute sample radius based on distance and mip level
                    val r = int1Var((2f.const * (viewportHeight.toFloat1() * maxDist) / (2f.const * depth * viewParams.y)).toInt1() shr level)
                    val sampleN = int1Var(min(kernelSize, (r * r)))

                    // compute current level ao value
                    val posDiff = float3Var()
                    val dist = float1Var()
                    val ao1 = float1Var()

                    fori(0.const, sampleN) { i ->
                        dSample set clipNear / texelFetch(gDepth, pxCoord + kernel[i], gBufferLevel).x
                        posSample set depthToViewSpacePos(dSample, clipXy + (kernel[i].toFloat2() * 2f.const / pxScale), viewParams)

                        posDiff set posSample - viewPos
                        dist set length(posDiff)
                        posDiff set posDiff / dist
                        dist /= maxDist

                        ao1 += (1f.const - min(1f.const, dist * dist)) * saturate(dot(posDiff, normal))
                    }
                    ao1 set ao1 / kernelSize.toFloat1()

                    // bilateral up-sampling of previous level ao value
                    val ao2 = float1Var(0f.const)
                    `if`(level lt lowerLevel) {
                        val td = 16f.const
                        val tn = 8f.const

                        val lowerSz = int2Var(pxScale.toInt2() / 2.const - Vec2i.ONES.const)
                        val prevPx0 = int2Var(min((pxCoord - Vec2i.ONES.const) / 2.const + Vec2i(0, 0).const, lowerSz))
                        val prevPx1 = int2Var(min((pxCoord - Vec2i.ONES.const) / 2.const + Vec2i(1, 0).const, lowerSz))
                        val prevPx2 = int2Var(min((pxCoord - Vec2i.ONES.const) / 2.const + Vec2i(0, 1).const, lowerSz))
                        val prevPx3 = int2Var(min((pxCoord - Vec2i.ONES.const) / 2.const + Vec2i(1, 1).const, lowerSz))

                        val d0 = float1Var(clipNear / texelFetch(gPrevDepth, prevPx0, gBufferLowerLevel).x)
                        val d1 = float1Var(clipNear / texelFetch(gPrevDepth, prevPx1, gBufferLowerLevel).x)
                        val d2 = float1Var(clipNear / texelFetch(gPrevDepth, prevPx2, gBufferLowerLevel).x)
                        val d3 = float1Var(clipNear / texelFetch(gPrevDepth, prevPx3, gBufferLowerLevel).x)

                        val wd0 = float1Var(pow(1f.const / (1f.const + abs(d0 - depth)), td))
                        val wd1 = float1Var(pow(1f.const / (1f.const + abs(d1 - depth)), td))
                        val wd2 = float1Var(pow(1f.const / (1f.const + abs(d2 - depth)), td))
                        val wd3 = float1Var(pow(1f.const / (1f.const + abs(d3 - depth)), td))

                        val n0 = float3Var(texelFetch(gPrevNormal, prevPx0, gBufferLowerLevel).xyz)
                        n0.z set float1Var(sqrt(1f.const - n0.x * n0.x - n0.y * n0.y))
                        val n1 = float3Var(texelFetch(gPrevNormal, prevPx1, gBufferLowerLevel).xyz)
                        n1.z set float1Var(sqrt(1f.const - n1.x * n1.x - n1.y * n1.y))
                        val n2 = float3Var(texelFetch(gPrevNormal, prevPx2, gBufferLowerLevel).xyz)
                        n2.z set float1Var(sqrt(1f.const - n2.x * n2.x - n2.y * n2.y))
                        val n3 = float3Var(texelFetch(gPrevNormal, prevPx3, gBufferLowerLevel).xyz)
                        n3.z set float1Var(sqrt(1f.const - n3.x * n3.x - n3.y * n3.y))

                        val wn0 = float1Var(pow(dot(normal, n0) * 0.5f.const + 0.5f.const, tn))
                        val wn1 = float1Var(pow(dot(normal, n1) * 0.5f.const + 0.5f.const, tn))
                        val wn2 = float1Var(pow(dot(normal, n2) * 0.5f.const + 0.5f.const, tn))
                        val wn3 = float1Var(pow(dot(normal, n3) * 0.5f.const + 0.5f.const, tn))

                        val wbx0 = float1Var(0.25f.const)
                        val wby0 = float1Var(0.25f.const)
                        `if`(pxCoord.x % 2.const ne 0.const) { wbx0 set 0.75f.const }
                        `if`(pxCoord.y % 2.const ne 0.const) { wby0 set 0.75f.const }
                        val wbx1 = 1f.const - wbx0
                        val wby1 = 1f.const - wby0

                        ao2 set 0f.const
                        ao2 += sampleBlurred(prevPx0) * wbx0 * wby0 * wn0 * wd0
                        ao2 += sampleBlurred(prevPx1) * wbx1 * wby0 * wn1 * wd1
                        ao2 += sampleBlurred(prevPx2) * wbx0 * wby1 * wn2 * wd2
                        ao2 += sampleBlurred(prevPx3) * wbx1 * wby1 * wn3 * wd3
                    }

                    val finalAo = float1Var(1f.const - pow(1f.const - max(ao1, ao2), aoPower))
                    `if`(level eq 0.const) {
                        finalAo set 1f.const - finalAo
                    }
                    colorOutput(float4Value(finalAo, 0f.const, 0f.const, 1f.const))
                }
            }
        }
    }

    companion object {
        private val sampleKernelDense: List<Vec2i> = buildList {
            for (y in -2..2) {
                for (x in -2..2) {
                    if (x != 0 || y != 0) {
                        add(Vec2i(x, y))
                    }
                }
            }
            sortBy { it.toVec2f().length() }
        }

        private val sampleKernelSparse: List<Vec2i> = buildList {
            for (y in -5..5 step 2) {
                for (x in -5..5 step 2) {
                    if (x != 0 || y != 0) {
                        add(Vec2i(x, y))
                    }
                }
            }
            sortBy { it.toVec2f().length() }
        }
    }
}