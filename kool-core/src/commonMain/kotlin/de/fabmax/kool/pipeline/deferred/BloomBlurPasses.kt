package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.shading.BlurShader
import de.fabmax.kool.pipeline.shading.BlurShaderConfig
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import kotlin.math.sqrt

class BloomBlurPasses(kernelSize: Int, thresholdPass: BloomThresholdPass) {
    val blurX = OffscreenPass2d(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RGBA_F16),
        initialSize = Vec2i(128, 128),
        name = "bloom-blur-x",
    )
    val blurY = OffscreenPass2d(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RGBA_F16),
        initialSize = Vec2i(128, 128),
        name = "bloom-blur-y",
    )

    private val blurShaderX: BlurShader
    private val blurShaderY: BlurShader
    private var blurDirDirty = true

    val bloomMap: Texture2d get() = blurY.colorTexture!!

    var bloomScale = 1f
        set(value) {
            field = value
            blurDirDirty = true
        }
    var bloomStrength: Float
        get() = blurShaderY.strength
        set(value) {
            blurShaderY.strength = value
        }

    init {
        val pingCfg = BlurShaderConfig().apply {
            kernelRadius = kernelSize
        }
        blurShaderX = BlurShader(pingCfg)
        blurShaderX.blurInput = thresholdPass.colorTexture

        val pongCfg = BlurShaderConfig().apply {
            kernelRadius = kernelSize
        }
        blurShaderY = BlurShader(pongCfg)
        blurShaderY.blurInput = blurX.colorTexture

        blurX.drawNode.fullScreenQuad(blurShaderX)
        blurY.drawNode.fullScreenQuad(blurShaderY)

        bloomStrength = 1f

        blurX.dependsOn(thresholdPass)
        blurY.dependsOn(blurX)
    }

    fun setSize(width: Int, height: Int) {
        blurX.setSize(width, height)
        blurY.setSize(width, height)

        val sqrt2 = sqrt(2f)
        val dx = 1f / width * bloomScale * sqrt2
        val dy = 1f / height * bloomScale * sqrt2

        blurShaderX.direction = Vec2f(dx, dy)
        blurShaderY.direction = Vec2f(dx, -dy)
    }

    private fun Node.fullScreenQuad(quadShader: DrawShader) {
        isFrustumChecked = false
        addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "bloom-blur-mesh") {
            generateFullscreenQuad()
            shader = quadShader
        }
    }
}