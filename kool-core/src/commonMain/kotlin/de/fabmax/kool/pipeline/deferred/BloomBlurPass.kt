package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.shading.BlurShader
import de.fabmax.kool.pipeline.shading.BlurShaderConfig
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.Color
import kotlin.math.sqrt

class BloomBlurPass(kernelSize: Int, thresholdPass: BloomThresholdPass) :
        OffscreenRenderPass2dPingPong(renderPassConfig {
            name = "BloomBlurPass"
            addColorTexture(TexFormat.RGBA_F16)
            clearDepthTexture()
        }) {

    private val pingShader: BlurShader
    private val pongShader: BlurShader
    private var blurDirDirty = true

    val bloomMap: Texture2d
        get() = pong.colorTexture!!

    var bloomScale = 1f
        set(value) {
            field = value
            blurDirDirty = true
        }
    var bloomStrength: Float
        get() = pongShader.strength
        set(value) {
            pongShader.strength = value
        }

    init {
        pingPongPasses = 1

        val pingCfg = BlurShaderConfig().apply {
            kernelRadius = kernelSize
        }
        pingShader = BlurShader(pingCfg)
        pingShader.blurInput = thresholdPass.colorTexture

        val pongCfg = BlurShaderConfig().apply {
            kernelRadius = kernelSize
        }
        pongShader = BlurShader(pongCfg)
        pongShader.blurInput = ping.colorTexture

        pingContent.fullScreenQuad(pingShader)
        pongContent.fullScreenQuad(pongShader)
        ping.clearColor = Color(0f, 0f, 0f, 0f)
        pong.clearColor = Color(0f, 0f, 0f, 0f)

        bloomStrength = 1f

        dependsOn(thresholdPass)
    }

    override fun update(ctx: KoolContext) {
        super.update(ctx)

        if (blurDirDirty) {
            val sqrt2 = sqrt(2f)
            val dx = 1f / width * bloomScale * sqrt2
            val dy = 1f / height * bloomScale * sqrt2

            pingShader.direction = Vec2f(dx, dy)
            pongShader.direction = Vec2f(dx, -dy)
        }
    }

    override fun resize(width: Int, height: Int, ctx: KoolContext) {
        super.resize(width, height, ctx)
        blurDirDirty = true
    }

    private fun Node.fullScreenQuad(quadShader: Shader) {
        isFrustumChecked = false
        addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
            generateFullscreenQuad()
            shader = quadShader
        }
    }
}