package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.BlurShader
import de.fabmax.kool.pipeline.shading.BlurShaderConfig
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color

class BloomPass(val pbrPass: PbrLightingPass) :
        OffscreenRenderPass2dPingPong(renderPassConfig {
            name = "BloomPass"
            setSize(pbrPass.config.width, pbrPass.config.height)
            addColorTexture(TexFormat.RGBA_F16)
            clearDepthTexture()
        }) {

    private val pingShader: BlurShader
    private val pongShader: BlurShader

    val bloomMap: Texture2d
        get() = pong.colorTexture!!
    var minBrightnessLower: Float
        get() = pingShader.minBrightnessLower
        set(value) { pingShader.minBrightnessLower = value }
    var minBrightnessUpper: Float
        get() = pingShader.minBrightnessUpper
        set(value) { pingShader.minBrightnessUpper = value }
    var bloomRadius = 1f
        set(value) {
            field = value
            updateBloomRadius()
        }

    init {
        pingPongPasses = 1

        val pingCfg = BlurShaderConfig().apply {
            kernelRadius = 16
            isWithMinBrightness = true
        }
        pingShader = BlurShader(pingCfg).apply { isVertical = false }
        pingShader.blurInput = pbrPass.colorTexture

        val pongCfg = BlurShaderConfig().apply {
            kernelRadius = 12
        }
        pongShader = BlurShader(pongCfg).apply { isVertical = true }
        pongShader.blurInput = ping.colorTexture

        pingContent.fullScreenQuad(pingShader)
        pongContent.fullScreenQuad(pongShader)
        ping.clearColor = Color(0f, 0f, 0f, 0f)
        pong.clearColor = Color(0f, 0f, 0f, 0f)

        dependsOn(pbrPass)
    }

    fun setMinBrightnessThresholds(lower: Float, upper: Float) {
        minBrightnessLower = lower
        minBrightnessUpper = upper
    }

    override fun resize(width: Int, height: Int, ctx: KoolContext) {
        super.resize(width, height, ctx)
        updateBloomRadius()
    }

    private fun updateBloomRadius() {
        val ar = width.toFloat() / height
        val bloomRadius = Vec2f(bloomRadius / 800f, bloomRadius * ar / 800f)
        pingShader.radiusFac = bloomRadius
        pongShader.radiusFac = bloomRadius
    }

    private fun Group.fullScreenQuad(quadShader: Shader) {
        isFrustumChecked = false
        +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
            isFrustumChecked = false
            generate {
                rect {
                    size.set(1f, 1f)
                    mirrorTexCoordsY()
                }
            }
            shader = quadShader
        }
    }
}