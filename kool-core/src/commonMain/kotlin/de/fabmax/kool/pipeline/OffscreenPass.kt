package de.fabmax.kool.pipeline

import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Viewport
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
        val safeWidth = width.coerceAtLeast(1)
        val safeHeight = height.coerceAtLeast(1)
        val safeLayers = layers.coerceAtLeast(1)
        if (safeWidth != this.width || safeHeight != this.height || safeLayers != this.layers) {
            logT { "Resizing OffscreenPass $name to $safeWidth x $safeHeight x $safeLayers" }
            applySize(safeWidth, safeHeight, safeLayers)

            for (v in views) {
                if (v.isFillFramebuffer) {
                    v.viewport = Viewport(0, 0, safeWidth, safeHeight)
                }
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

        fun addColor(
            texFormat: TexFormat,
            clearColor: ClearColor = ClearColorFill(Color.BLACK),
            filterMethod: FilterMethod = FilterMethod.LINEAR
        ) {
            addColor {
                this.textureFormat = texFormat
                this.clearColor = clearColor
                this.samplerSettings = SamplerSettings()
                    .clamped()
                    .copy(minFilter = filterMethod, magFilter = filterMethod)
            }
        }

        fun depth(block: TextureAttachmentConfig.Builder.() -> Unit) {
            depth = TextureAttachmentConfig.Builder().apply(block).build()
        }

        fun defaultDepth() = depth {
            textureFormat = TexFormat.R_F32
            samplerSettings = SamplerSettings().clamped().nearest()
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
    val samplerSettings: SamplerSettings,
    val clearColor: ClearColor,
    val clearDepth: ClearDepth,
    val isTransient: Boolean
) {
    class Builder {
        var textureFormat: TexFormat = TexFormat.RGBA
        var samplerSettings = SamplerSettings().clamped()
        var clearColor: ClearColor = ClearColorFill(Color.BLACK)
        var clearDepth: ClearDepth = ClearDepthFill
        var isTransient: Boolean = false

        fun build(): TextureAttachmentConfig {
            return TextureAttachmentConfig(textureFormat, samplerSettings, clearColor, clearDepth, isTransient)
        }
    }
}
