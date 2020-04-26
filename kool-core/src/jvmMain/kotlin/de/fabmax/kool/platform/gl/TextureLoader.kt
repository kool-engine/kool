package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.Uint8BufferImpl
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL30.*
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min

object TextureLoader {
    fun loadTexture(ctx: Lwjgl3Context, props: TextureProps, data: TextureData) : LoadedTextureGl {
        val backend = ctx.renderBackend as GlRenderBackend
        return when (data) {
            is BufferedTextureData -> loadTexture2d(ctx, backend, props, data)
            is ImageTextureData -> loadTexture2d(ctx, backend, props, data)
            is CubeMapTextureData -> loadTextureCube(ctx, props, data)
            else -> throw IllegalArgumentException("TextureData type not supported: $data")
        }
    }

    private fun loadTextureCube(ctx: Lwjgl3Context, props: TextureProps, img: CubeMapTextureData) : LoadedTextureGl {
        val tex = glGenTextures()
        glBindTexture(GL_TEXTURE_CUBE_MAP, tex)
        texImage2d(GL_TEXTURE_CUBE_MAP_POSITIVE_X, img.right)
        texImage2d(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, img.left)
        texImage2d(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, img.up)
        texImage2d(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, img.down)
        texImage2d(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, img.back)
        texImage2d(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, img.front)

        if (props.mipMapping) {
            glGenerateMipmap(GL_TEXTURE_CUBE_MAP)
        }

        val mipLevels = floor(log2(max(img.width, img.height).toDouble())).toInt() + 1
        val estSize = Texture.estimatedTexSize(img.right.width, img.right.height, img.format.pxSize, 6, mipLevels)
        return LoadedTextureGl(ctx, tex, estSize)
    }

    private fun loadTexture2d(ctx: Lwjgl3Context, backend: GlRenderBackend, props: TextureProps, img: TextureData) : LoadedTextureGl {
        val tex = GL11.glGenTextures()

        glBindTexture(GL_TEXTURE_2D, tex)
        texImage2d(GL_TEXTURE_2D, img)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, props.minFilter.glMinFilterMethod(props.mipMapping))
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, props.magFilter.glMagFilterMethod())
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, props.addressModeU.glAddressMode())
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, props.addressModeV.glAddressMode())

        if (props.maxAnisotropy > 1 && backend.glCapabilities.maxAnisotropy > 1f) {
            glTexParameteri(GL_TEXTURE_2D, backend.glCapabilities.glTextureMaxAnisotropyExt, min(props.maxAnisotropy, backend.glCapabilities.maxAnisotropy))
        }
        if (props.mipMapping) {
            glGenerateMipmap(GL_TEXTURE_2D)
        }

        val mipLevels = floor(log2(max(img.width, img.height).toDouble())).toInt() + 1
        val estSize = Texture.estimatedTexSize(img.width, img.height, img.format.pxSize, 1, mipLevels)
        return LoadedTextureGl(ctx, tex, estSize)
    }

    private fun FilterMethod.glMinFilterMethod(mipMapping: Boolean): Int {
        return when(this) {
            FilterMethod.NEAREST -> if (mipMapping) GL_NEAREST_MIPMAP_NEAREST else GL_NEAREST
            FilterMethod.LINEAR -> if (mipMapping) GL_LINEAR_MIPMAP_LINEAR else GL_LINEAR
        }
    }

    private fun FilterMethod.glMagFilterMethod(): Int {
        return when (this) {
            FilterMethod.NEAREST -> GL_NEAREST
            FilterMethod.LINEAR -> GL_LINEAR
        }
    }

    private fun AddressMode.glAddressMode(): Int {
        return when(this) {
            AddressMode.CLAMP_TO_BORDER -> GL_CLAMP_TO_EDGE
            AddressMode.CLAMP_TO_EDGE -> GL_CLAMP_TO_EDGE
            AddressMode.MIRRORED_REPEAT -> GL_MIRRORED_REPEAT
            AddressMode.REPEAT -> GL_REPEAT
        }
    }

    private fun texImage2d(target: Int, data: TextureData) {
        when (data) {
            is BufferedTextureData -> {
                glTexImage2D(target, 0, data.format.glInternalFormat, data.width, data.height, 0, data.format.glFormat, data.format.glType, (data.data as Uint8BufferImpl).buffer)
            }
            else -> {
                throw IllegalArgumentException("Invalid TextureData type for texImage2d: $data")
            }
        }
    }
}