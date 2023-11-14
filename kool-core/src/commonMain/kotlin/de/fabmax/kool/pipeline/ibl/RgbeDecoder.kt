package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenShaderPipelineCfg
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logD
import kotlin.math.max

class RgbeDecoder(parentScene: Scene, hdriTexture: Texture2d, brightness: Float = 1f) :
    OffscreenRenderPass2d(Node(), renderPassConfig {
        val w = hdriTexture.loadedTexture?.width ?: 1024
        val h = hdriTexture.loadedTexture?.height ?: 512
        val sz = max(w, h)

        name = "RgbeDecoder"
        size.set(sz, sz)
        addColorTexture(TexFormat.RGBA_F16)
        clearDepthTexture()
        addMipLevels(drawMipLevels = false)
    }) {

    var isAutoRemove = true

    init {
        clearColor = null
        drawNode.apply {
            addTextureMesh {
                generateFullscreenQuad()
                shader = RgbeDecoderShader(hdriTexture, brightness)
            }
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterDraw += { ctx ->
            logD { "Converted RGBe to linear: ${hdriTexture.name}" }
            if (isAutoRemove) {
                parentScene.removeOffscreenPass(this)
                launchDelayed(1) { release() }
            } else {
                isEnabled = false
            }
        }
    }

    class RgbeDecoderShader(hdriTexture: Texture2d, brightness: Float) : KslShader(
        KslProgram("RGBe Decoder").apply {
            val uv = interStageFloat2("uv")

            fullscreenQuadVertexStage(uv)

            fragmentStage {
                val rgbeTex = texture2d("rgbeTex")
                val uMaxBrightness = uniformFloat3("uMaxBrightness")
                val uBrightness = uniformFloat1("uBrightness")

                main {
                    val rgbe = float4Var(sampleTexture(rgbeTex, uv.output))
                    val exp = float1Var(rgbe.w * 255f.const - 128f.const)
                    colorOutput(min(rgbe.rgb * pow(2f.const, exp) * uBrightness, uMaxBrightness))
                }
            }
        },
        fullscreenShaderPipelineCfg
    ) {
        val rgbeTex by texture2d("rgbeTex", hdriTexture)
        val uBrightness by uniform1f("uBrightness", brightness)
        val uMaxBrightness by uniform3f("uMaxBrightness", Vec3f(20f))
    }

    override fun release() {
        drawNode.release()
        super.release()
    }
}