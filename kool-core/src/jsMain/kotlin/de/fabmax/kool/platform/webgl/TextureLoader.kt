package de.fabmax.kool.platform.webgl

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.CubeMapTextureData
import de.fabmax.kool.TextureData
import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.Uint8BufferImpl
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.FLOAT
import org.khronos.webgl.WebGLRenderingContext.Companion.RGBA
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE0
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_X
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_Y
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_Z
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_X
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_Y
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_Z
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
        gl.activeTexture(TEXTURE0)
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
        gl.activeTexture(TEXTURE0)
        gl.bindTexture(TEXTURE_2D, tex)
        texImage2d(gl, TEXTURE_2D, img)
        gl.generateMipmap(TEXTURE_2D)
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
            TexFormat.R -> js("WebGL2RenderingContext.R8") as Int
            TexFormat.RG -> js("WebGL2RenderingContext.RG8") as Int
            TexFormat.RGB -> js("WebGL2RenderingContext.RGB8") as Int
            TexFormat.RGBA -> js("WebGL2RenderingContext.RGBA8") as Int

            TexFormat.R_F16 -> js("WebGL2RenderingContext.R16F") as Int
            TexFormat.RG_F16 -> js("WebGL2RenderingContext.RG16F") as Int
            TexFormat.RGB_F16 -> js("WebGL2RenderingContext.RGB16F") as Int
            TexFormat.RGBA_F16 -> js("WebGL2RenderingContext.RGBA16F") as Int
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
            TexFormat.R -> js("WebGL2RenderingContext.RED") as Int
            TexFormat.RG -> js("WebGL2RenderingContext.RG") as Int
            TexFormat.RGB -> js("WebGL2RenderingContext.RGB") as Int
            TexFormat.RGBA -> js("WebGL2RenderingContext.RGBA") as Int

            TexFormat.R_F16 -> js("WebGL2RenderingContext.RED") as Int
            TexFormat.RG_F16 -> js("WebGL2RenderingContext.RG") as Int
            TexFormat.RGB_F16 -> js("WebGL2RenderingContext.RGB") as Int
            TexFormat.RGBA_F16 -> js("WebGL2RenderingContext.RGBA") as Int
        }
}