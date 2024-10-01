package de.fabmax.kool.pipeline.mssao

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.releaseWith
import kotlin.math.max

class GbufferDownsamplingPass(val gBuffer: GbufferPass, numLevels: Int) : OffscreenRenderPass2d(
    drawNode = Node(),
    attachmentConfig = AttachmentConfig(
        colorAttachments = ColorAttachmentTextures(
            listOf(TextureAttachmentConfig(TexFormat.RGBA_F16, SamplerSettings().clamped().nearest()))
        ),
        depthAttachment = DepthAttachmentTexture(),
        mipLevels = MipMode.Render(numLevels)
    ),
    initialSize = Vec2i(128),
    name = "mssao-downsample-pass"
) {
    private var sceneRenderCallback: SceneSizeRenderCallback? = null
    private val downsamplingShader = DownsamplingShader()

    var medianThresh = downsamplingShader.medianThresh

    private val mipLevelDatas = Array<MipLevelData?>(numLevels) { null }

    init {
        drawNode = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "mssao-downsample-mesh").apply {
            generateFullscreenQuad()
            shader = downsamplingShader
        }
    }

    fun install(scene: Scene) {
        sceneRenderCallback = SceneSizeRenderCallback(this, scene, 0.5f)
        scene.addOffscreenPass(this)
    }

    override fun setupMipLevel(mipLevel: Int) {
        val pipeline = downsamplingShader.createdPipeline ?: return

        // use individual bind groups for each mip-level to avoid changing uniform buffer contents while
        // previous mip-levels are not yet drawn
        var mipData = mipLevelDatas[mipLevel]
        if (mipData == null) {
            val srcLevel = max(0, mipLevel - 1)
            val mipLevelData = pipeline.pipelineData.copy()
            val samplerSettings = SamplerSettings().clamped().nearest().limitMipLevels(srcLevel, 1)
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

    private inner class MipLevelData(val mipLevel: Int, val data: BindGroupData, val samplerSettings: SamplerSettings) {
        private var isConfigured = false

        fun setup() {
            downsamplingShader.medianThresh = medianThresh

            if (isConfigured) return
            isConfigured = true

            downsamplingShader.srcMipLevel = max(0, mipLevel - 1)
            if (mipLevel == 0) {
                downsamplingShader.sampleNormalTex.set(gBuffer.colorTexture, samplerSettings)
                downsamplingShader.upperDepthTex.set(gBuffer.depthTexture, samplerSettings)
            } else {
                downsamplingShader.sampleNormalTex.set(colorTexture, samplerSettings)
                downsamplingShader.upperDepthTex.set(depthTexture, samplerSettings)
            }
        }
    }

    private class DownsamplingShader : KslShader("mssao-downsampling-shader") {
        var srcMipLevel by uniform1i("uSrcMipLevel")
        val sampleNormalTex = texture2d("tSampleNormal")
        val upperDepthTex = texture2d("tSampleDepth")
        var clipNear by uniform1f("uClipNear", 0.1f)
        var medianThresh by uniform1f("uMedianThresh", 1f)

        init {
            pipelineConfig = PipelineConfig(blendMode = BlendMode.DISABLED, depthTest = DepthCompareOp.ALWAYS)
            program.downsamplingProgram()
        }

        private fun KslProgram.downsamplingProgram() {
            val uv = interStageFloat2()
            fullscreenQuadVertexStage(uv)
            fragmentStage {
                main {
                    val sampleLevel = uniformInt1("uSrcMipLevel")
                    val clipNear = uniformFloat1("uClipNear")
                    val medianThresh = uniformFloat1("uMedianThresh")
                    val normalTex = texture2d("tSampleNormal")
                    val depthTex = texture2d("tSampleDepth", TextureSampleType.UNFILTERABLE_FLOAT)

                    val texScale = int2Var(textureSize2d(normalTex, sampleLevel) - Vec2i.ONES.const)
                    val sampleCoord = int2Var((texScale.toFloat2() * uv.output).toInt2())
                    val dnArray = float4Array(4, Vec4f.ZERO.const)

                    dnArray[0].xyz set texelFetch(normalTex, sampleCoord + int2Value(0, 0), sampleLevel).xyz
                    dnArray[0].w set clipNear / texelFetch(depthTex, sampleCoord + int2Value(0, 0), sampleLevel).x

                    dnArray[1].xyz set texelFetch(normalTex, sampleCoord + int2Value(0, 1), sampleLevel).xyz
                    dnArray[1].w set clipNear / texelFetch(depthTex, sampleCoord + int2Value(0, 1), sampleLevel).x

                    dnArray[2].xyz set texelFetch(normalTex, sampleCoord + int2Value(1, 0), sampleLevel).xyz
                    dnArray[2].w set clipNear / texelFetch(depthTex, sampleCoord + int2Value(1, 0), sampleLevel).x

                    dnArray[3].xyz set texelFetch(normalTex, sampleCoord + int2Value(1, 1), sampleLevel).xyz
                    dnArray[3].w set clipNear / texelFetch(depthTex, sampleCoord + int2Value(1, 1), sampleLevel).x

                    // bubble sort sampled values by depth
                    val swap = float4Var()
                    for (i in 0 until 3) {
                        for (j in (i + 1) until 4) {
                            `if`(dnArray[j].w lt dnArray[i].w) {
                                swap set dnArray[i]
                                dnArray[i] set dnArray[j]
                                dnArray[j] set swap
                            }
                        }
                    }

                    val dRange = float1Var(dnArray[3].w - dnArray[0].w)
                    val d = float1Var(dnArray[1].w)
                    val n = float3Var(dnArray[1].xyz)

                    `if`(dRange lt medianThresh) {
                        val n1 = dnArray[1].xyz
                        val n2 = dnArray[2].xyz
                        n set normalize(n1 + n2)
                        d set (dnArray[1].w + dnArray[2].w) * 0.5f.const
                    }

                    colorOutput(float4Value(n, 1f.const))
                    outDepth set clipNear / d
                }
            }
        }
    }
}