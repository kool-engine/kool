package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.getNumMipLevels
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

inline fun renderPassConfig(block: OffscreenRenderPass.ConfigBuilder.() -> Unit): OffscreenRenderPass.Config {
    val builder = OffscreenRenderPass.ConfigBuilder()
    builder.block()
    return OffscreenRenderPass.Config(builder)
}

abstract class OffscreenRenderPass(drawNode: Node, val config: Config) : RenderPass(drawNode) {
    var isEnabled = true

    var width = config.width
        protected set
    var height = config.height
        protected set

    override var camera: Camera = PerspectiveCamera().apply { projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN }

    var onSetupMipLevel: ((Int, KoolContext) -> Unit)? = null

    init {
        name = config.name
        clearColors = Array(config.nColorAttachments) { null }
        applyMipViewport(0)

        if (width == 0) {
            // config is set to dynamic width, start with some non-zero value
            width = 16
        }
        if (height == 0) {
            // config is set to dynamic height, start with some non-zero value
            height = 16
        }
    }

    fun getColorTexProps(colorAttachment: Int = 0): TextureProps {
        return config.colorAttachments[colorAttachment].getTextureProps(config.mipLevels > 1)
    }

    fun getMipWidth(mipLevel: Int): Int {
        return if (mipLevel <= 0) width else width shr mipLevel
    }

    fun getMipHeight(mipLevel: Int): Int {
        return if (mipLevel <= 0) height else height shr mipLevel
    }

    fun applyMipViewport(mipLevel: Int) {
        viewport.set(0, 0, getMipWidth(mipLevel), getMipHeight(mipLevel))
    }

    fun resize(width: Int, height: Int, ctx: KoolContext) {
        if (config.width == 0 && config.height == 0) {
            applySize(width, height, ctx)
            applyMipViewport(0)
        } else {
            logE { "OffscreenRenderPass $name cannot be resized: Size is fixed to ${config.width} x ${config.height}" }
        }
    }

    protected open fun applySize(width: Int, height: Int, ctx: KoolContext) {
        this.width = width
        this.height = height
    }

    open class Config(builder: ConfigBuilder) {
        val name = builder.name

        val width = builder.width
        val height = builder.height
        val mipLevels = builder.mipLevels
        val drawMipLevels = builder.drawMipLevels

        val depthRenderTarget = builder.depthRenderTarget
        val colorRenderTarget = builder.colorRenderTarget

        val depthAttachment = if (depthRenderTarget == RenderTarget.TEXTURE) {
            builder.depthAttachment?.let { TextureAttachmentConfig(it) }
        } else {
            null
        }
        val colorAttachments = if (colorRenderTarget == RenderTarget.TEXTURE) {
            builder.colorAttachments.map { TextureAttachmentConfig(it) }
        } else {
            emptyList()
        }
        val nColorAttachments = colorAttachments.size

        init {
            if (colorRenderTarget == RenderTarget.TEXTURE && colorAttachments.isEmpty()) {
                throw IllegalStateException("colorAttachments must be configured if colorRenderTarget is TEXTURE")
            } else if (colorRenderTarget == RenderTarget.RENDER_BUFFER && builder.colorAttachments.isNotEmpty()) {
                logW { "colorAttachments are ignored if colorRenderTarget is RENDER_BUFFER" }
            }
            if (depthRenderTarget == RenderTarget.TEXTURE && depthAttachment == null) {
                throw IllegalStateException("depthAttachment must be configured if depthRenderTarget is TEXTURE")
            } else if (depthRenderTarget == RenderTarget.RENDER_BUFFER && builder.depthAttachment != null) {
                logW { "depthAttachment is ignored if depthRenderTarget is RENDER_BUFFER" }
            }
        }
    }

    open class ConfigBuilder {
        var name = "OffscreenRenderPass"

        var width = 0
        var height = 0
        var mipLevels = 1
        var drawMipLevels = true

        var colorRenderTarget = RenderTarget.TEXTURE
        var depthRenderTarget = RenderTarget.RENDER_BUFFER

        val colorAttachments = mutableListOf<TextureAttachmentConfigBuilder>()
        var depthAttachment: TextureAttachmentConfigBuilder? = null

        fun setDynamicSize() {
            width = 0
            height = 0
        }

        fun setSize(width: Int, height: Int) {
            this.width = width
            this.height = height
        }

        fun addMipLevels(width: Int = this.width, height: Int = this.height, drawMipLevels: Boolean = true) {
            mipLevels = getNumMipLevels(width, height)
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
