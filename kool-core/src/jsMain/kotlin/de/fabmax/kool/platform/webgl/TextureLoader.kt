package de.fabmax.kool.platform.webgl

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.CubeMapTextureData
import de.fabmax.kool.TextureData
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.R16F
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.R8
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RED
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RG
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RG16F
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RG8
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RGB16F
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RGB8
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RGBA16F
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RGBA8
import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.Uint8BufferImpl
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.CLAMP_TO_EDGE
import org.khronos.webgl.WebGLRenderingContext.Companion.FLOAT
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.RGB
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
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_BYTE

object TextureLoader {
    fun loadTexture(ctx: JsContext, data: TextureData) : LoadedTexture {
        return when (data) {
            is BufferedTextureData -> loadTexture2d(ctx, data)
            is ImageTextureData -> loadTexture2d(ctx, data)
            is CubeMapTextureData -> loadTextureCube(ctx, data)
            else -> throw IllegalArgumentException("TextureData type not supported: $data")
        }
    }

    private fun loadTextureCube(ctx: JsContext, img: CubeMapTextureData) : LoadedTexture {
        val gl = ctx.gl
        val tex = gl.createTexture()
        gl.bindTexture(TEXTURE_CUBE_MAP, tex)
        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_X, img.right)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_X, img.left)
        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_Y, img.up)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_Y, img.down)
        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_Z, img.back)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_Z, img.front)
        gl.generateMipmap(TEXTURE_CUBE_MAP)
        return LoadedTexture(tex)
    }

    private fun loadTexture2d(ctx: JsContext, img: TextureData) : LoadedTexture {
        val gl = ctx.gl
        // fixme: is there a way to find out if the image has an alpha channel and set the texture format accordingly?
        val tex = gl.createTexture()
        gl.run {
            bindTexture(TEXTURE_2D, tex)
            texImage2d(this, TEXTURE_2D, img)

            texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE)
            texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE)
            texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, LINEAR)
            texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, LINEAR)

            generateMipmap(TEXTURE_2D)
        }
        return LoadedTexture(tex)
    }

    private fun texImage2d(gl: WebGLRenderingContext, target: Int, data: TextureData) {
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

    private val TexFormat.glInternalFormat: Int
        get() = when(this) {
            TexFormat.R -> R8
            TexFormat.RG -> RG8
            TexFormat.RGB -> RGB8
            TexFormat.RGBA -> RGBA8

            TexFormat.R_F16 -> R16F
            TexFormat.RG_F16 -> RG16F
            TexFormat.RGB_F16 -> RGB16F
            TexFormat.RGBA_F16 -> RGBA16F
        }

    private val TexFormat.glType: Int
        get() = when(this) {
            TexFormat.R -> UNSIGNED_BYTE
            TexFormat.RG -> UNSIGNED_BYTE
            TexFormat.RGB -> UNSIGNED_BYTE
            TexFormat.RGBA -> UNSIGNED_BYTE

            TexFormat.R_F16 -> FLOAT
            TexFormat.RG_F16 -> FLOAT
            TexFormat.RGB_F16 -> FLOAT
            TexFormat.RGBA_F16 -> FLOAT
        }

    private val TexFormat.glFormat: Int
        get() = when(this) {
            TexFormat.R -> RED
            TexFormat.RG -> RG
            TexFormat.RGB -> RGB
            TexFormat.RGBA -> RGBA

            TexFormat.R_F16 -> RED
            TexFormat.RG_F16 -> RG
            TexFormat.RGB_F16 -> RGB
            TexFormat.RGBA_F16 -> RGBA
        }
}