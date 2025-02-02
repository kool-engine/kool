package de.fabmax.kool.pipeline

import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logT

abstract class OffscreenPass(
    attachmentConfig: AttachmentConfig,
    initialSize: Vec3i,
    name: String
) : RenderPass(name, attachmentConfig.mipLevels) {

    private val _size = MutableVec3i(initialSize)
    override val size: Vec3i get() = _size

    val colorAttachments: ColorAttachment = attachmentConfig.colorAttachments
    val depthAttachment: DepthAttachment = attachmentConfig.depthAttachment

    override val numSamples: Int = 1
    val numColorAttachments: Int
        get() = if (colorAttachments is ColorAttachmentTextures) colorAttachments.attachments.size else 1

    override val clearColors = MutableList<ClearColor>(numColorAttachments) { ClearColorFill(Color.BLACK) }
    override var clearDepth: ClearDepth = ClearDepthFill

    fun createColorTextureProps(attachment: Int = 0): TextureProps {
        check(colorAttachments is ColorAttachmentTextures)
        return colorAttachments.attachments[attachment]
            .createTextureProps(mipMode.hasMipLevels)
    }

    fun createDepthTextureProps(): TextureProps {
        check(depthAttachment is DepthAttachmentTexture)
        return depthAttachment.attachment
            .createTextureProps(mipMode.hasMipLevels)
    }

    protected fun View.setFullscreenViewport() {
        viewport.set(0, 0, width, height)
    }

    protected fun setSize(width: Int, height: Int, depth: Int) {
        if (width != this.width || height != this.height || depth != this.depth) {
            logT { "OffscreenPass $name resized to $width x $height x $depth" }
            applySize(width, height, depth)

            // fixme: resetting all viewports is probably not always right
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
        fun ColorAttachmentTextures(vararg textureFormats: TexFormat) =
            ColorAttachmentTextures(textureFormats.map { TextureAttachmentConfig(it) })

        fun colorAttachmentDefaultDepth(vararg textureFormats: TexFormat): AttachmentConfig =
            AttachmentConfig(ColorAttachmentTextures(*textureFormats))

        fun colorAttachmentTextureDepth(vararg textureFormats: TexFormat): AttachmentConfig =
            AttachmentConfig(
                ColorAttachmentTextures(*textureFormats),
                DepthAttachmentTexture()
            )

        fun colorAttachmentNoDepth(vararg textureFormats: TexFormat): AttachmentConfig =
            AttachmentConfig(
                ColorAttachmentTextures(*textureFormats),
                DepthAttachmentNone
            )
    }

    data class AttachmentConfig(
        val colorAttachments: ColorAttachment,
        val depthAttachment: DepthAttachment = DepthAttachmentRender,
        val mipLevels: MipMode = MipMode.None
    )

    sealed interface ColorAttachment

    /**
     * Render pass texture color attachment. The color texture(s) can be used by other passes after the render pass is
     * rendered.
     */
    data class ColorAttachmentTextures(val attachments: List<TextureAttachmentConfig>) : ColorAttachment

    /**
     * Discard the color output.
     */
    data object ColorAttachmentNone : ColorAttachment

    sealed interface DepthAttachment

    /**
     * Render pass texture depth attachment. The depth texture can be used by other passes after the render pass is
     * rendered.
     */
    class DepthAttachmentTexture(
        val attachment: TextureAttachmentConfig = TextureAttachmentConfig(
            textureFormat = TexFormat.R_F32,
            defaultSamplerSettings = SamplerSettings().clamped().nearest()
        )
    ) : DepthAttachment

    /**
     * Default depth attachment, cannot be used by other render passes but provides the usual depth testing
     * functionality inside the render pass.
     */
    data object DepthAttachmentRender : DepthAttachment

    /**
     * Don't use any depth attachment. This means there is no depth testing available inside this render pass, which
     * is fine for things like single full-screen quads used by many filter shaders, etc.
     */
    data object DepthAttachmentNone : DepthAttachment

    data class TextureAttachmentConfig(
        val textureFormat: TexFormat = TexFormat.RGBA,
        val defaultSamplerSettings: SamplerSettings = SamplerSettings().clamped(),
    ) {
        fun createTextureProps(generateMipMaps: Boolean) = TextureProps(
            format = textureFormat,
            generateMipMaps = generateMipMaps,
            defaultSamplerSettings = defaultSamplerSettings
        )
    }
}
