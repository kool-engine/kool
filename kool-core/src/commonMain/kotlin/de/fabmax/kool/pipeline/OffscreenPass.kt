package de.fabmax.kool.pipeline

import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logT

abstract class OffscreenPass(
    numSamples: Int,
    mipMode: MipMode,
    initialSize: Vec3i,
    name: String
) : RenderPass(numSamples, mipMode, name) {

    private val _size = MutableVec3i(initialSize)
    override val size: Vec3i get() = _size

    protected fun setSize(width: Int, height: Int, layers: Int) {
        if (width != this.width || height != this.height || layers != this.layers) {
            logT { "Resizing OffscreenPass $name to $width x $height x $layers" }
            applySize(width, height, layers)

            // fixme: resetting all viewports is probably not always right
            for (v in views) {
                v.viewport.set(0, 0, width, height)
            }
        }
    }

    protected open fun applySize(width: Int, height: Int, layers: Int) {
        _size.set(width, height, layers)
    }

    override fun release() {
        super.release()
        views.forEach {
            if (it.isReleaseDrawNode && !it.drawNode.isReleased) {
                it.drawNode.release()
            }
        }
    }
}

fun AttachmentConfig(block: AttachmentConfig.Builder.() -> Unit): AttachmentConfig {
    return AttachmentConfig.Builder().apply(block).build()
}

data class AttachmentConfig(
    val colors: List<TextureAttachmentConfig>,
    val depth: TextureAttachmentConfig?
) {
    companion object {
        fun singleColorNoDepth(texFormat: TexFormat, clearColor: ClearColor = ClearColorFill(Color.BLACK)): AttachmentConfig {
            return AttachmentConfig {
                addColor(texFormat)
                noDepth()
            }
        }

        fun singleColorDefaultDepth(texFormat: TexFormat, clearColor: ClearColor = ClearColorFill(Color.BLACK)): AttachmentConfig {
            return AttachmentConfig {
                addColor(texFormat)
                defaultDepth()
            }
        }
    }

    class Builder {
        val colors = mutableListOf<TextureAttachmentConfig>()
        var depth: TextureAttachmentConfig? = null

        init {
            defaultDepth()
        }

        fun addColor(block: TextureAttachmentConfig.Builder.() -> Unit) {
            colors += TextureAttachmentConfig.Builder().apply(block).build()
        }

        fun addColor(texFormat: TexFormat, clearColor: ClearColor = ClearColorFill(Color.BLACK)) {
            addColor {
                this.textureFormat = texFormat
                this.clearColor = clearColor
            }
        }

        fun depth(block: TextureAttachmentConfig.Builder.() -> Unit) {
            depth = TextureAttachmentConfig.Builder().apply(block).build()
        }

        fun defaultDepth() = depth {
            textureFormat = TexFormat.R_F32
            defaultSamplerSettings = SamplerSettings().clamped().nearest()
        }

        fun transientDepth() = depth {
            textureFormat = TexFormat.R_F32
            isTransient = true
        }

        fun noDepth() {
            depth = null
        }

        fun build(): AttachmentConfig {
            return AttachmentConfig(colors.toList(), depth)
        }
    }
}

fun TextureAttachmentConfig(block: TextureAttachmentConfig.Builder.() -> Unit): TextureAttachmentConfig {
    return TextureAttachmentConfig.Builder().apply(block).build()
}

data class TextureAttachmentConfig(
    val textureFormat: TexFormat,
    val defaultSamplerSettings: SamplerSettings,
    val clearColor: ClearColor,
    val clearDepth: ClearDepth,
    val isTransient: Boolean
) {
    fun createTextureProps(isMipMapped: Boolean) = TextureProps(
        format = textureFormat,
        isMipMapped = isMipMapped,
        defaultSamplerSettings = defaultSamplerSettings
    )

    class Builder {
        var textureFormat: TexFormat = TexFormat.RGBA
        var defaultSamplerSettings = SamplerSettings().clamped()
        var clearColor: ClearColor = ClearColorFill(Color.BLACK)
        var clearDepth: ClearDepth = ClearDepthFill
        var isTransient: Boolean = false

        fun build(): TextureAttachmentConfig {
            return TextureAttachmentConfig(textureFormat, defaultSamplerSettings, clearColor, clearDepth, isTransient)
        }
    }
}
