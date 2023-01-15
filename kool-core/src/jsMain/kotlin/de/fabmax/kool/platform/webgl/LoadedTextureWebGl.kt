package de.fabmax.kool.platform.webgl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.TEXTURE_3D
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.TEXTURE_WRAP_R
import de.fabmax.kool.platform.webgl.TextureLoader.arrayBufferView
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import org.khronos.webgl.WebGLRenderingContext.Companion.CLAMP_TO_EDGE
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER_COMPLETE
import org.khronos.webgl.WebGLRenderingContext.Companion.IMPLEMENTATION_COLOR_READ_FORMAT
import org.khronos.webgl.WebGLRenderingContext.Companion.IMPLEMENTATION_COLOR_READ_TYPE
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR_MIPMAP_LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.MIRRORED_REPEAT
import org.khronos.webgl.WebGLRenderingContext.Companion.NEAREST
import org.khronos.webgl.WebGLRenderingContext.Companion.NEAREST_MIPMAP_NEAREST
import org.khronos.webgl.WebGLRenderingContext.Companion.REPEAT
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MAG_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MIN_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_S
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_T
import org.khronos.webgl.WebGLTexture
import kotlin.math.min

class LoadedTextureWebGl(val ctx: JsContext, val target: Int, val texture: WebGLTexture?, estimatedSize: Int) : LoadedTexture {

    val texId = nextTexId++
    var isDestroyed = false
        private set

    override var width = 0
    override var height = 0
    override var depth = 0

    init {
        ctx.engineStats.textureAllocated(texId, estimatedSize)
    }

    fun setSize(width: Int, height: Int, depth: Int) {
        this.width = width
        this.height = height
        this.depth = depth
    }

    fun applySamplerProps(props: TextureProps) {
        val gl = ctx.gl
        gl.bindTexture(target, texture)

        gl.texParameteri(target, TEXTURE_MIN_FILTER, props.minFilter.glMinFilterMethod(props.mipMapping))
        gl.texParameteri(target, TEXTURE_MAG_FILTER, props.magFilter.glMagFilterMethod())
        gl.texParameteri(target, TEXTURE_WRAP_S, props.addressModeU.glAddressMode())
        gl.texParameteri(target, TEXTURE_WRAP_T, props.addressModeV.glAddressMode())
        if (target == TEXTURE_3D) {
            gl.texParameteri(target, TEXTURE_WRAP_R, props.addressModeW.glAddressMode())
        }

        val anisotropy = min(props.maxAnisotropy, ctx.glCapabilities.maxAnisotropy)
        if (anisotropy > 1) {
            gl.texParameteri(target, ctx.glCapabilities.glTextureMaxAnisotropyExt, anisotropy)
        }

        if (anisotropy > 1 && (props.minFilter == FilterMethod.NEAREST || props.magFilter == FilterMethod.NEAREST)) {
            logW { "Texture filtering is NEAREST but anisotropy is $anisotropy (> 1)" }
        }
    }

    override fun readTexturePixels(targetData: TextureData) {
        if (target != TEXTURE_2D) {
            throw IllegalStateException("readTexturePixels() is only supported for 2D textures")
        }
        if (targetData.width != width || targetData.height != height) {
            throw IllegalArgumentException("supplied targetData dimension does not match texture size " +
                    "(supplied: ${targetData.width} x ${targetData.height}, actual: $width x $height)")
        }

        with(ctx.gl) {
            val fb = createFramebuffer()
            bindFramebuffer(FRAMEBUFFER, fb)
            framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_2D, texture, 0)

            if (checkFramebufferStatus(FRAMEBUFFER) == FRAMEBUFFER_COMPLETE) {
                val format = getParameter(IMPLEMENTATION_COLOR_READ_FORMAT) as Int
                val type = getParameter(IMPLEMENTATION_COLOR_READ_TYPE) as Int
                readPixels(0, 0, width, height, format, type, targetData.arrayBufferView)
            } else {
                logE { "Failed reading pixels from framebuffer" }
            }

            deleteFramebuffer(fb)
        }
    }

    override fun dispose() {
        if (!isDestroyed) {
            isDestroyed = true
            ctx.gl.deleteTexture(texture)
            ctx.engineStats.textureDeleted(texId)
        }
    }

    private fun FilterMethod.glMinFilterMethod(mipMapping: Boolean): Int {
        return when(this) {
            FilterMethod.NEAREST -> if (mipMapping) NEAREST_MIPMAP_NEAREST else NEAREST
            FilterMethod.LINEAR -> if (mipMapping) LINEAR_MIPMAP_LINEAR else LINEAR
        }
    }

    private fun FilterMethod.glMagFilterMethod(): Int {
        return when (this) {
            FilterMethod.NEAREST -> NEAREST
            FilterMethod.LINEAR -> LINEAR
        }
    }

    private fun AddressMode.glAddressMode(): Int {
        return when(this) {
            AddressMode.CLAMP_TO_EDGE -> CLAMP_TO_EDGE
            AddressMode.MIRRORED_REPEAT -> MIRRORED_REPEAT
            AddressMode.REPEAT -> REPEAT
        }
    }

    companion object {
        private var nextTexId = 1L
    }
}