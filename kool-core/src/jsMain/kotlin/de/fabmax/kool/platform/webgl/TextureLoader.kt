package de.fabmax.kool.platform.webgl

import de.fabmax.kool.JsImpl.gl
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.*
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.TEXTURE_3D
import de.fabmax.kool.util.Uint8BufferImpl
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.NONE
import org.khronos.webgl.WebGLRenderingContext.Companion.RGBA
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_X
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_Y
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_NEGATIVE_Z
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_X
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_Y
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_Z
import org.khronos.webgl.WebGLRenderingContext.Companion.UNPACK_COLORSPACE_CONVERSION_WEBGL
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_BYTE
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max

object TextureLoader {
    fun loadTexture1d(ctx: JsContext, props: TextureProps, img: TextureData) : LoadedTextureWebGl {
        // 1d texture internally uses a 2d texture
        val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, gl.createTexture(), img.estimateTexSize())
        tex.setSize(img.width, 1, 1)
        tex.applySamplerProps(props)

        texImage2d(gl, TEXTURE_2D, img)
        if (props.mipMapping) {
            gl.generateMipmap(TEXTURE_2D)
        }
        return tex
    }

    fun loadTexture2d(ctx: JsContext, props: TextureProps, img: TextureData) : LoadedTextureWebGl {
        val gl = ctx.gl
        val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, gl.createTexture(), img.estimateTexSize())
        tex.setSize(img.width, img.height, 1)
        tex.applySamplerProps(props)

        texImage2d(gl, TEXTURE_2D, img)
        if (props.mipMapping) {
            gl.generateMipmap(TEXTURE_2D)
        }
        return tex
    }

    fun loadTexture3d(ctx: JsContext, props: TextureProps, img: TextureData) : LoadedTextureWebGl {
        val tex = LoadedTextureWebGl(ctx, TEXTURE_3D, gl.createTexture(), img.estimateTexSize())
        tex.setSize(img.width, img.height, img.depth)
        tex.applySamplerProps(props)

        when (img) {
            is TextureData3d -> {
                gl.texImage3D(TEXTURE_3D, 0, img.format.glInternalFormat, img.width, img.height, img.depth, 0, img.format.glFormat, img.format.glType, (img.data as Uint8BufferImpl).buffer)
            }
            is ImageAtlasTextureData -> {
                gl.texStorage3D(TEXTURE_3D, 1, img.format.glInternalFormat, img.width, img.height, img.depth)
                for (z in 0 until img.depth) {
                    gl.texSubImage3D(TEXTURE_3D, 0, 0, 0, z, img.width, img.height, 1, img.format.glFormat, img.format.glType, img.data[z])
                }
            }
        }
        if (props.mipMapping) {
            gl.generateMipmap(TEXTURE_3D)
        }
        return tex
    }

    fun loadTextureCube(ctx: JsContext, props: TextureProps, img: TextureDataCube) : LoadedTextureWebGl {
        val gl = ctx.gl
        val tex = LoadedTextureWebGl(ctx, TEXTURE_CUBE_MAP, gl.createTexture(), img.estimateTexSize())
        tex.setSize(img.width, img.height, 1)
        tex.applySamplerProps(props)

        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_X, img.right)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_X, img.left)
        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_Y, img.up)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_Y, img.down)
        texImage2d(gl, TEXTURE_CUBE_MAP_POSITIVE_Z, img.back)
        texImage2d(gl, TEXTURE_CUBE_MAP_NEGATIVE_Z, img.front)
        if (props.mipMapping) {
            gl.generateMipmap(TEXTURE_CUBE_MAP)
        }
        return tex
    }

    private fun TextureData.estimateTexSize(): Int {
        val layers = if (this is TextureDataCube) 6 else 1
        val mipLevels = floor(log2(max(width, height).toDouble())).toInt() + 1
        return Texture.estimatedTexSize(width, height, layers, mipLevels, format.pxSize)
    }

    private fun texImage2d(gl: WebGLRenderingContext, target: Int, data: TextureData) {
        gl.pixelStorei(UNPACK_COLORSPACE_CONVERSION_WEBGL, NONE)
        when (data) {
            is TextureData1d -> {
                gl.texImage2D(target, 0, data.format.glInternalFormat, data.width, 1, 0, data.format.glFormat, data.format.glType, (data.data as Uint8BufferImpl).buffer)
            }
            is TextureData2d -> {
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