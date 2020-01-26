package de.fabmax.kool.platform.webgl

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.TextureData
import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.Uint8BufferImpl
import org.khronos.webgl.WebGLRenderingContext.Companion.RGBA
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE0
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_BYTE

object TextureLoader {
    fun loadTexture(ctx: JsContext, data: TextureData) : LoadedTexture {
        return when (data) {
            is BufferedTextureData -> loadTexture(ctx, data)
            is ImageTextureData -> loadTexture(ctx, data)
            else -> throw IllegalArgumentException("TextureData not supported: $data")
        }
    }

    fun loadTexture(ctx: JsContext, img: BufferedTextureData) : LoadedTexture {
        val gl = ctx.gl

        val tex = gl.createTexture()
        gl.activeTexture(TEXTURE0)
        gl.bindTexture(TEXTURE_2D, tex)
        gl.texImage2D(TEXTURE_2D, 0, img.format.glInternalFormat, img.width, img.height, 0, img.format.glFormat, UNSIGNED_BYTE, (img.data as Uint8BufferImpl).buffer)
        gl.generateMipmap(TEXTURE_2D)

        return LoadedTexture(tex)
    }

    fun loadTexture(ctx: JsContext, img: ImageTextureData) : LoadedTexture {
        val gl = ctx.gl

        // fixme: is there a way to find out if the image has an alpha channel and set the texture format accordingly?
        val tex = gl.createTexture()
        gl.activeTexture(TEXTURE0)
        gl.bindTexture(TEXTURE_2D, tex)
        gl.texImage2D(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, img.data)
        gl.generateMipmap(TEXTURE_2D)

        return LoadedTexture(tex)
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