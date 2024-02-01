package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
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

    private val _size = MutableVec3i(config.size)
    val size: Vec3i get() = _size
    override val width: Int get() = _size.x
    override val height: Int get() = _size.y
    override val depth: Int get() = _size.z

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

    open fun setSize(width: Int, height: Int, depth: Int = 1) {
        if (width != this.width || height != this.height || depth != this.depth) {
            logT { "OffscreenPass $name resized to $width x $height x $depth" }
            applySize(width, height, depth)
            for (v in views) {
                v.viewport.set(0, 0, width, height)
            }
        }
    }

    protected open fun applySize(width: Int, height: Int, depth: Int) {
        _size.set(width, height, depth)
    }

    override fun release() {
        super.release()

        views.forEach {
            if (it.isReleaseDrawNode && !it.drawNode.isReleased) {
                it.drawNode.release()
            }
        }
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

        val size = MutableVec3i(128, 128, 1)
        var mipLevels = 1
        var drawMipLevels = true

        var colorAttachment: ColorAttachment = RenderBufferColorAttachment(TexFormat.RGBA, false)
        var depthAttachment: DepthAttachment = RenderBufferDepthAttachment

        fun size(size: Vec2i) = size(size.x, size.y)
        fun size(size: Vec3i) = size(size.x, size.y, size.z)

        fun size(width: Int, height: Int, depth: Int = 1) {
            size.set(max(1, width), max(1, height), max(1, depth))
            if (size.x > width) logW { "Invalid OffscreenRenderPass width: $width" }
            if (size.y > height) logW { "Invalid OffscreenRenderPass height: $height" }
            if (size.z > depth) logW { "Invalid OffscreenRenderPass depth: $depth" }
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
            depthAttachment = RenderBufferDepthAttachment
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

    data object RenderBufferDepthAttachment : DepthAttachment

    class TextureDepthAttachment(val isUsedAsShadowMap: Boolean) : DepthAttachment {
        val attachment = TextureAttachmentConfigBuilder().apply {
            if (isUsedAsShadowMap) {
                defaultSamplerSettings = defaultSamplerSettings.linear()
                depthCompareOp = DepthCompareOp.LESS
            } else {
                defaultSamplerSettings = defaultSamplerSettings.nearest()
                depthCompareOp = DepthCompareOp.DISABLED
            }
        }.build()
    }

    class TextureAttachmentConfig(builder: TextureAttachmentConfigBuilder) {
        val colorFormat = builder.colorFormat
        val defaultSamplerSettings = builder.defaultSamplerSettings
        val depthCompareOp = builder.depthCompareOp

        val providedTexture: Texture? = builder.providedTexture
        val isProvided: Boolean
            get() = providedTexture != null

        fun getTextureProps(mipMapping: Boolean) = TextureProps(
            format = colorFormat,
            generateMipMaps = mipMapping,
            defaultSamplerSettings = defaultSamplerSettings
        )
    }

    class TextureAttachmentConfigBuilder {
        var colorFormat = TexFormat.RGBA
        var defaultSamplerSettings = SamplerSettings().clamped()
        var depthCompareOp = DepthCompareOp.DISABLED

        var providedTexture: Texture? = null

        fun build(): TextureAttachmentConfig = TextureAttachmentConfig(this)
    }

    enum class RenderTarget {
        TEXTURE,
        RENDER_BUFFER
    }
}
