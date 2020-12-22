package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.Uint8BufferImpl
import org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL30.*
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max

object TextureLoader {
    fun loadTexture1d(ctx: Lwjgl3Context, props: TextureProps, img: TextureData) : LoadedTextureGl {
        // 1d texture internally uses a 2d texture to be compatible with glsl version 300 es
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), img.estimateTexSize())
        tex.setSize(img.width, 1, 1)
        tex.applySamplerProps(props)

        texImage2d(GL_TEXTURE_2D, img)
        if (props.mipMapping) {
            glGenerateMipmap(GL_TEXTURE_2D)
        }
        return tex
    }

    fun loadTexture2d(ctx: Lwjgl3Context, props: TextureProps, img: TextureData) : LoadedTextureGl {
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), img.estimateTexSize())
        tex.setSize(img.width, img.height, 1)
        tex.applySamplerProps(props)

        texImage2d(GL_TEXTURE_2D, img)
        if (props.mipMapping) {
            glGenerateMipmap(GL_TEXTURE_2D)
        }
        return tex
    }

    fun loadTexture3d(ctx: Lwjgl3Context, props: TextureProps, img: TextureData) : LoadedTextureGl {
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_3D, glGenTextures(), img.estimateTexSize())
        tex.setSize(img.width, img.height, img.depth)
        tex.applySamplerProps(props)

        if (img is TextureData3d) {
            glTexImage3D(GL_TEXTURE_3D, 0, img.format.glInternalFormat, img.width, img.height, img.depth, 0, img.format.glFormat, img.format.glType, (img.data as Uint8BufferImpl).buffer)
        } else {
            throw IllegalArgumentException("Provided TextureData must be of type TextureData3d")
        }
        if (props.mipMapping) {
            glGenerateMipmap(GL_TEXTURE_3D)
        }
        return tex
    }

    fun loadTextureCube(ctx: Lwjgl3Context, props: TextureProps, img: TextureDataCube) : LoadedTextureGl {
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_CUBE_MAP, glGenTextures(), img.estimateTexSize())
        tex.setSize(img.width, img.height, 1)
        tex.applySamplerProps(props)

        texImage2d(GL_TEXTURE_CUBE_MAP_POSITIVE_X, img.right)
        texImage2d(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, img.left)
        texImage2d(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, img.up)
        texImage2d(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, img.down)
        texImage2d(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, img.back)
        texImage2d(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, img.front)
        if (props.mipMapping) {
            glGenerateMipmap(GL_TEXTURE_CUBE_MAP)
        }
        return tex
    }

    private fun TextureData.estimateTexSize(): Int {
        val layers = when (this) {
            is TextureDataCube -> 6
            is TextureData3d -> depth
            else -> 1
        }

        val mipLevels = floor(log2(max(width, height).toDouble())).toInt() + 1
        return Texture.estimatedTexSize(width, height, layers, mipLevels, format.pxSize)
    }

    private fun texImage2d(target: Int, data: TextureData) {
        when (data) {
            is TextureData1d -> {
                glTexImage2D(target, 0, data.format.glInternalFormat, data.width, 1, 0, data.format.glFormat, data.format.glType, (data.data as Uint8BufferImpl).buffer)
            }
            is TextureData2d -> {
                glTexImage2D(target, 0, data.format.glInternalFormat, data.width, data.height, 0, data.format.glFormat, data.format.glType, (data.data as Uint8BufferImpl).buffer)
            }
            else -> {
                throw IllegalArgumentException("Invalid TextureData type for texImage2d: $data")
            }
        }
    }
}