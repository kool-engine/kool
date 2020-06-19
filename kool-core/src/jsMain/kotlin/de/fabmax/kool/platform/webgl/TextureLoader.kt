package de.fabmax.kool.platform.webgl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.Uint8BufferImpl
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.CLAMP_TO_EDGE
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR_MIPMAP_LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.MIRRORED_REPEAT
import org.khronos.webgl.WebGLRenderingContext.Companion.NEAREST
import org.khronos.webgl.WebGLRenderingContext.Companion.NEAREST_MIPMAP_NEAREST
import org.khronos.webgl.WebGLRenderingContext.Companion.NONE
import org.khronos.webgl.WebGLRenderingContext.Companion.REPEAT
import org.khronos.webgl.WebGLRenderingContext.Companion.RGBA
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_X
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_Y
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_Z
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_X
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_Y
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_Z
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MAG_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MIN_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_S
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_T
import org.khronos.webgl.WebGLRenderingContext.Companion.UNPACK_COLORSPACE_CONVERSION_WEBGL
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_BYTE
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min

object TextureLoader {
    fun loadTexture(ctx: JsContext, props: TextureProps, data: TextureData) : LoadedTextureWebGl {
        return when (data) {
            is BufferedTextureData -> loadTexture2d(ctx, props, data)
            is ImageTextureData -> loadTexture2d(ctx, props, data)
            is CubeMapTextureData -> loadTextureCube(ctx, props, data)
            else -> throw IllegalArgumentException("TextureData type not supported: $data")
        }
    }

    private fun loadTextureCube(ctx: JsContext, props: TextureProps, img: CubeMapTextureData) : LoadedTextureWebGl {
        val gl = ctx.gl
        val tex = gl.createTexture()
        gl.bindTexture(TEXTURE_CUBE_MAP, tex)
        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_X, img.right)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_X, img.left)
        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_Y, img.up)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_Y, img.down)
        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_Z, img.back)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_Z, img.front)

        if (props.mipMapping) {
            gl.generateMipmap(TEXTURE_CUBE_MAP)
        }

        val mipLevels = floor(log2(max(img.width, img.height).toDouble())).toInt() + 1
        val estSize = Texture.estimatedTexSize(img.right.width, img.right.height, img.format.pxSize, 6, mipLevels)
        return LoadedTextureWebGl(ctx, tex, estSize)
    }

    private fun loadTexture2d(ctx: JsContext, props: TextureProps, img: TextureData) : LoadedTextureWebGl {
        val gl = ctx.gl
        // fixme: is there a way to find out if the image has an alpha channel and set the texture format accordingly?
        val tex = gl.createTexture()

        gl.bindTexture(TEXTURE_2D, tex)
        texImage2d(gl, TEXTURE_2D, img)

        gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, props.minFilter.glMinFilterMethod(props.mipMapping))
        gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, props.magFilter.glMagFilterMethod())
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, props.addressModeU.glAddressMode())
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, props.addressModeV.glAddressMode())

        if (props.maxAnisotropy > 1 && ctx.glCapabilities.maxAnisotropy > 1f) {
            gl.texParameteri(TEXTURE_2D, ctx.glCapabilities.glTextureMaxAnisotropyExt, min(props.maxAnisotropy, ctx.glCapabilities.maxAnisotropy))
        }
        if (props.mipMapping) {
            gl.generateMipmap(TEXTURE_2D)
        }

        val mipLevels = floor(log2(max(img.width, img.height).toDouble())).toInt() + 1
        val estSize = Texture.estimatedTexSize(img.width, img.height, img.format.pxSize, 1, mipLevels)
        return LoadedTextureWebGl(ctx, tex, estSize)
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

    private fun texImage2d(gl: WebGLRenderingContext, target: Int, data: TextureData) {
        gl.pixelStorei(UNPACK_COLORSPACE_CONVERSION_WEBGL, NONE)
        when (data) {
            is BufferedTextureData -> {
                gl.texImage2D(target, 0, data.format.glInternalFormat, data.width, data.height, 0, data.format.glFormat, data.format.glType, (data.data as Uint8BufferImpl).buffer)
            }
            is ImageTextureData -> {
                gl.texImage2D(target, 0, RGBA, RGBA, UNSIGNED_BYTE, data.data)
            }
            else -> {
                throw IllegalArgumentException("Invalid TextureData type for texImage2d: $data")
            }
        }
    }
}