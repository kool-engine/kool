package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.getNumMipLevels
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.logW
import kotlin.math.max

inline fun renderPassConfig(block: OffscreenRenderPass.Config.() -> Unit): OffscreenRenderPass.Config {
    val config = OffscreenRenderPass.Config()
    config.block()
    return config
}

abstract class OffscreenRenderPass(config: Config) : RenderPass(config.name) {

    private val _size = MutableVec2i(config.size)
    val size: Vec2i get() = _size
    override val width: Int get() = _size.x
    override val height: Int get() = _size.y

    val colorAttachment = config.colorAttachment
    val depthAttachment = config.depthAttachment

    val numColorTextures = if (colorAttachment is TextureColorAttachment) colorAttachment.attachments.size else 0
    val numColorAttachments = if (colorAttachment is TextureColorAttachment) colorAttachment.attachments.size else 1

    val mipLevels = config.mipLevels
    val drawMipLevels = config.drawMipLevels
    var onSetupMipLevel: ((Int, KoolContext) -> Unit)? = null

    val dependencies = mutableListOf<RenderPass>()
    var isEnabled = true

    fun dependsOn(renderPass: RenderPass) {
        dependencies += renderPass
    }

    fun getColorTexProps(attachment: Int = 0): TextureProps {
        return (colorAttachment as TextureColorAttachment).attachments[attachment].getTextureProps(mipLevels > 1)
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

    open fun setSize(width: Int, height: Int, ctx: KoolContext) {
        if (width != this.width || height != this.height) {
            logT { "OffscreenPass $name resized to $width x $height" }
            applySize(width, height, ctx)
            for (v in views) {
                v.viewport.set(0, 0, width, height)
            }
        }
    }

    protected open fun applySize(width: Int, height: Int, ctx: KoolContext) {
        _size.set(width, height)
    }

    companion object {
        fun sortByDependencies(renderPasses: MutableList<OffscreenRenderPass>) {
            val open = mutableSetOf<OffscreenRenderPass>()
            val closed = mutableSetOf<OffscreenRenderPass>()

            renderPasses.forEach {
                open += it
            }
            renderPasses.clear()

            while (open.isNotEmpty()) {
                var anyClosed = false
                val openIt = open.iterator()
                while (openIt.hasNext()) {
                    val pass = openIt.next()
                    var close = true
                    for (j in pass.dependencies.indices) {
                        val dep = pass.dependencies[j]
                        if (dep !in closed) {
                            close = false
                            break
                        }
                    }
                    if (close) {
                        anyClosed = true
                        openIt.remove()
                        closed += pass
                        renderPasses += pass
                    }
                }
                if (!anyClosed) {
                    logE { "Failed to sort offscreen passes, remaining:" }
                    open.forEach { p ->
                        val missingPasses = p.dependencies.filter { it !in closed }.map { it.name }
                        logE { "  ${p.name}, missing dependencies: $missingPasses" }
                    }
                    break
                }
            }
        }
    }

    open class Config {
        var name = "OffscreenRenderPass"

        val size = MutableVec2i(128, 128)
        var mipLevels = 1
        var drawMipLevels = true

        var colorAttachment: ColorAttachment = RenderBufferColorAttachment(TexFormat.RGBA, false)
        var depthAttachment: DepthAttachment = RenderBufferDepthAttachment()

        fun size(size: Vec2i) = size(size.x, size.y)

        fun size(width: Int, height: Int) {
            size.set(max(1, width), max(1, height))
            if (size.x > width) logW { "Invalid OffscreenRenderPass width: $width" }
            if (size.y > height) logW { "Invalid OffscreenRenderPass height: $height" }
        }

        fun enableMipLevels(mipLevels: Int = getNumMipLevels(size.x, size.y), drawMipLevels: Boolean = true) {
            this.mipLevels = mipLevels
            this.drawMipLevels = drawMipLevels
        }

        fun colorTargetNone() {
            // even though it won't be used, we need some minimal color target to render to...
            colorTargetRenderBuffer(TexFormat.R)
        }

        fun colorTargetRenderBuffer(format: TexFormat, isMultiSampled: Boolean = false) {
            colorAttachment = RenderBufferColorAttachment(format, isMultiSampled)
        }

        fun colorTargetTexture(vararg formats: TexFormat) {
            val attachments = mutableListOf<TextureAttachmentConfig>()
            formats.forEach {
                attachments += TextureAttachmentConfigBuilder().apply { colorFormat = it }.build()
            }
            colorAttachment = TextureColorAttachment(attachments)
        }

        fun colorTargetTexture(numTextures: Int, block: TextureAttachmentConfigBuilder.(Int) -> Unit) {
            val attachments = mutableListOf<TextureAttachmentConfig>()
            for (i in 0 until numTextures) {
                val cfgBuilder = TextureAttachmentConfigBuilder()
                cfgBuilder.block(i)
                attachments += cfgBuilder.build()
            }
            colorAttachment = TextureColorAttachment(attachments)
        }

        fun depthTargetRenderBuffer() {
            depthAttachment = RenderBufferDepthAttachment()
        }

        fun depthTargetTexture(isUsedAsShadowMap: Boolean) {
            depthAttachment = TextureDepthAttachment(isUsedAsShadowMap)
        }
    }

    sealed interface ColorAttachment

    class RenderBufferColorAttachment(
        val colorFormat: TexFormat,
        val isMultiSampled: Boolean
    ) : ColorAttachment

    class TextureColorAttachment(
        val attachments: List<TextureAttachmentConfig>
    ) : ColorAttachment

    sealed interface DepthAttachment

    class RenderBufferDepthAttachment : DepthAttachment

    class TextureDepthAttachment(val isUsedAsShadowMap: Boolean) : DepthAttachment {
        val attachment = TextureAttachmentConfigBuilder().apply {
            if (isUsedAsShadowMap) {
                minFilter = FilterMethod.LINEAR
                magFilter = FilterMethod.LINEAR
                depthCompareOp = DepthCompareOp.LESS
            } else {
                minFilter = FilterMethod.NEAREST
                magFilter = FilterMethod.NEAREST
                depthCompareOp = DepthCompareOp.DISABLED
            }
        }.build()
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

        fun build(): TextureAttachmentConfig = TextureAttachmentConfig(this)
    }

    enum class RenderTarget {
        TEXTURE,
        RENDER_BUFFER
    }
}
