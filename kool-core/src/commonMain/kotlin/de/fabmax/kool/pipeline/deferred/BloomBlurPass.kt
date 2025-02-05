package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.shading.BlurShader
import de.fabmax.kool.pipeline.shading.BlurShaderConfig
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import kotlin.math.sqrt

class BloomBlurPass(kernelSize: Int, thresholdPass: BloomThresholdPass) :
    OffscreenPass2dPingPong(
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RGBA_F16),
        initialSize = Vec2i(128, 128),
        name = "bloom-blur"
    )
{
    private val pingShader: BlurShader
    private val pongShader: BlurShader
    private var blurDirDirty = true

    val bloomMap: Texture2d get() = pong.colorTexture!!

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
        pingShader.blurInput = thresholdPass.colors[0].texture

        val pongCfg = BlurShaderConfig().apply {
            kernelRadius = kernelSize
        }
        pongShader = BlurShader(pongCfg)
        pongShader.blurInput = ping.colors[0].texture

        pingContent.fullScreenQuad(pingShader)
        pongContent.fullScreenQuad(pongShader)

        bloomStrength = 1f

        dependsOn(thresholdPass)
    }

    override fun update(ctx: KoolContext) {
        super.update(ctx)

        if (blurDirDirty) {
            blurDirDirty = false
            val sqrt2 = sqrt(2f)
            val dx = 1f / width * bloomScale * sqrt2
            val dy = 1f / height * bloomScale * sqrt2

            pingShader.direction = Vec2f(dx, dy)
            pongShader.direction = Vec2f(dx, -dy)
        }
    }

    override fun applySize(width: Int, height: Int, layers: Int) {
        super.applySize(width, height, layers)
        blurDirDirty = true
    }

    private fun Node.fullScreenQuad(quadShader: DrawShader) {
        isFrustumChecked = false
        addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "bloom-blur-mesh") {
            generateFullscreenQuad()
            shader = quadShader
        }
    }
}