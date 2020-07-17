package de.fabmax.kool.platform.webgl

import de.fabmax.kool.pipeline.AddressMode
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.JsContext
import org.khronos.webgl.WebGLRenderingContext.Companion.CLAMP_TO_EDGE
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR_MIPMAP_LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.MIRRORED_REPEAT
import org.khronos.webgl.WebGLRenderingContext.Companion.NEAREST
import org.khronos.webgl.WebGLRenderingContext.Companion.NEAREST_MIPMAP_NEAREST
import org.khronos.webgl.WebGLRenderingContext.Companion.REPEAT
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

    init {
        ctx.engineStats.textureAllocated(texId, estimatedSize)
    }

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun applySamplerProps(props: TextureProps) {
        val gl = ctx.gl
        gl.bindTexture(target, texture)

        gl.texParameteri(target, TEXTURE_MIN_FILTER, props.minFilter.glMinFilterMethod(props.mipMapping))
        gl.texParameteri(target, TEXTURE_MAG_FILTER, props.magFilter.glMagFilterMethod())
        gl.texParameteri(target, TEXTURE_WRAP_S, props.addressModeU.glAddressMode())
        gl.texParameteri(target, TEXTURE_WRAP_T, props.addressModeV.glAddressMode())

        val anisotropy = min(props.maxAnisotropy, ctx.glCapabilities.maxAnisotropy)
        if (anisotropy > 1) {
            gl.texParameteri(target, ctx.glCapabilities.glTextureMaxAnisotropyExt, anisotropy)
        }
    }

    override fun dispose() {
        isDestroyed = true
        ctx.gl.deleteTexture(texture)
        ctx.engineStats.textureDeleted(texId)
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
            AddressMode.CLAMP_TO_BORDER -> CLAMP_TO_EDGE
            AddressMode.CLAMP_TO_EDGE -> CLAMP_TO_EDGE
            AddressMode.MIRRORED_REPEAT -> MIRRORED_REPEAT
            AddressMode.REPEAT -> REPEAT
        }
    }

    companion object {
        private var nextTexId = 1L
    }
}