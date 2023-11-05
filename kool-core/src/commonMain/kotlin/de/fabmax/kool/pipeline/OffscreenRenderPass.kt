package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.getNumMipLevels
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.logW
import kotlin.math.max

inline fun renderPassConfig(block: OffscreenRenderPass.Config.() -> Unit): OffscreenRenderPass.Config {
    val config = OffscreenRenderPass.Config()
    config.block()
    return config
}

abstract class OffscreenRenderPass(drawNode: Node, config: Config) : RenderPass(drawNode) {

    private val _size = MutableVec2i(config.size)
    val size: Vec2i get() = _size
    val width: Int get() = _size.x
    val height: Int get() = _size.y

    val colorRenderTarget = config.colorRenderTarget
    val colorAttachments = if (colorRenderTarget == RenderTarget.TEXTURE) {
        config.colorAttachments.map { TextureAttachmentConfig(it) }
    } else {
        emptyList()
    }

    val depthRenderTarget = config.depthRenderTarget
    val depthAttachment = if (depthRenderTarget == RenderTarget.TEXTURE) {
        config.depthAttachment?.let { TextureAttachmentConfig(it) }
    } else {
        null
    }

    val mipLevels = config.mipLevels
    val drawMipLevels = config.drawMipLevels
    var onSetupMipLevel: ((Int, KoolContext) -> Unit)? = null

    var isEnabled = true

    init {
        if (colorRenderTarget == RenderTarget.TEXTURE && colorAttachments.isEmpty()) {
            throw IllegalStateException("colorAttachments must be configured if colorRenderTarget is TEXTURE")
        } else if (colorRenderTarget == RenderTarget.RENDER_BUFFER && config.colorAttachments.isNotEmpty()) {
            logW { "colorAttachments are ignored if colorRenderTarget is RENDER_BUFFER" }
        }
        if (depthRenderTarget == RenderTarget.TEXTURE && depthAttachment == null) {
            throw IllegalStateException("depthAttachment must be configured if depthRenderTarget is TEXTURE")
        } else if (depthRenderTarget == RenderTarget.RENDER_BUFFER && config.depthAttachment != null) {
            logW { "depthAttachment is ignored if depthRenderTarget is RENDER_BUFFER" }
        }

        name = config.name
    }

    fun getColorTexProps(colorAttachment: Int = 0): TextureProps {
        return colorAttachments[colorAttachment].getTextureProps(mipLevels > 1)
    }

    fun getMipWidth(mipLevel: Int, width: Int = this.width): Int {
        return if (mipLevel <= 0) width else width shr mipLevel
    }

    fun getMipHeight(mipLevel: Int, height: Int = this.height): Int {
        return if (mipLevel <= 0) height else height shr mipLevel
    }

    protected fun View.setFullscreenViewport(mipLevel: Int = 0) {
        viewport.set(0, 0, getMipWidth(mipLevel), getMipHeight(mipLevel))
    }

    open fun resize(width: Int, height: Int, ctx: KoolContext) {
        logT { "OffscreenPass $name resized to $width x $height" }
        applySize(width, height, ctx)
        for (v in views) {
            v.viewport.set(0, 0, width, height)
        }
    }

    protected open fun applySize(width: Int, height: Int, ctx: KoolContext) {
        _size.set(width, height)
    }

    open class Config {
        var name = "OffscreenRenderPass"

        val size = MutableVec2i(128, 128)
        var mipLevels = 1
        var drawMipLevels = true

        var colorRenderTarget = RenderTarget.TEXTURE
        var depthRenderTarget = RenderTarget.RENDER_BUFFER

        val colorAttachments = mutableListOf<TextureAttachmentConfigBuilder>()
        var depthAttachment: TextureAttachmentConfigBuilder? = null

        fun setSize(width: Int, height: Int) {
            size.set(max(1, width), max(1, height))
            if (size.x > width) logW { "Invalid OffscreenRenderPass width: $width" }
            if (size.y > height) logW { "Invalid OffscreenRenderPass height: $height" }
        }

        fun addMipLevels(mipLevels: Int = getNumMipLevels(size.x, size.y), drawMipLevels: Boolean = true) {
            this.mipLevels = mipLevels
            this.drawMipLevels = drawMipLevels
        }

        fun clearDepthTexture() {
            depthRenderTarget = RenderTarget.RENDER_BUFFER
            depthAttachment = null
        }

        fun setDepthTexture(usedAsShadowMap: Boolean) {
            depthRenderTarget = RenderTarget.TEXTURE
            depthAttachment = TextureAttachmentConfigBuilder().apply {
                if (usedAsShadowMap) {
                    minFilter = FilterMethod.LINEAR
                    magFilter = FilterMethod.LINEAR
                    depthCompareOp = DepthCompareOp.LESS
                } else {
                    minFilter = FilterMethod.NEAREST
                    magFilter = FilterMethod.NEAREST
                    depthCompareOp = DepthCompareOp.DISABLED
                }
            }
        }

        fun clearColorTexture() {
            colorRenderTarget = RenderTarget.RENDER_BUFFER
            colorAttachments.clear()
        }

        fun addColorTexture(format: TexFormat) {
            colorRenderTarget = RenderTarget.TEXTURE
            colorAttachments += TextureAttachmentConfigBuilder().apply {
                colorFormat = format
            }
        }

        fun addColorTexture(block: TextureAttachmentConfigBuilder.() -> Unit) {
            colorRenderTarget = RenderTarget.TEXTURE
            colorAttachments += TextureAttachmentConfigBuilder().apply(block)
        }
    }

    class TextureAttachmentConfig(builder: TextureAttachmentConfigBuilder) {
        val colorFormat = builder.colorFormat
        val minFilter = builder.minFilter
        val magFilter = builder.magFilter
        val addressModeU = builder.addressModeU
        val addressModeV = builder.addressModeV
        val depthCompareOp = builder.depthCompareOp
        val maxAnisotropy = 1

        val providedTexture: Texture? = builder.providedTexture
        val isProvided: Boolean
            get() = providedTexture != null

        fun getTextureProps(mipMapping: Boolean) = TextureProps(
                format = colorFormat,
                addressModeU = addressModeU,
                addressModeV = addressModeV,
                addressModeW = AddressMode.CLAMP_TO_EDGE,
                minFilter = minFilter,
                magFilter = magFilter,
                mipMapping = mipMapping,
                maxAnisotropy = maxAnisotropy
        )
    }

    class TextureAttachmentConfigBuilder {
        var colorFormat = TexFormat.RGBA
        var minFilter = FilterMethod.LINEAR
        var magFilter = FilterMethod.LINEAR
        var addressModeU = AddressMode.CLAMP_TO_EDGE
        var addressModeV = AddressMode.CLAMP_TO_EDGE
        var depthCompareOp = DepthCompareOp.DISABLED

        var providedTexture: Texture? = null
    }

    enum class RenderTarget {
        TEXTURE,
        RENDER_BUFFER
    }
}
