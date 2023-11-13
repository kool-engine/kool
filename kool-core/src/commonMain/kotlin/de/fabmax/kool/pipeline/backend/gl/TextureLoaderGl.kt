package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max

object TextureLoaderGl {
    fun loadTexture1d(tex: Texture1d, img: TextureData, backend: RenderBackendGl): LoadedTextureGl {
        // 1d texture internally uses a 2d texture
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, 1, 1)
        loadedTex.applySamplerProps(tex.props)

        gl.texImage2d(gl.TEXTURE_2D, img)
        if (tex.props.mipMapping) {
            gl.generateMipmap(gl.TEXTURE_2D)
        }
        return loadedTex
    }

    fun loadTexture2d(tex: Texture2d, img: TextureData, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, img.height, 1)
        loadedTex.applySamplerProps(tex.props)

        gl.texImage2d(gl.TEXTURE_2D, img)
        if (tex.props.mipMapping) {
            gl.generateMipmap(gl.TEXTURE_2D)
        }
        return loadedTex
    }

    fun loadTexture3d(tex: Texture3d, img: TextureData, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_3D, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, img.height, img.depth)
        loadedTex.applySamplerProps(tex.props)

        gl.texImage3d(gl.TEXTURE_3D, img)
        if (tex.props.mipMapping) {
            gl.generateMipmap(gl.TEXTURE_3D)
        }
        return loadedTex
    }

    fun loadTextureCube(tex: TextureCube, img: TextureData, backend: RenderBackendGl) : LoadedTextureGl {
        if (img !is TextureDataCube) {
            throw IllegalArgumentException("Provided TextureData must be of type TextureDataCube")
        }

        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_CUBE_MAP, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, img.height, 1)
        loadedTex.applySamplerProps(tex.props)

        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_X, img.right)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_X, img.left)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_Y, img.up)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_Y, img.down)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_Z, img.back)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_Z, img.front)
        if (tex.props.mipMapping) {
            gl.generateMipmap(gl.TEXTURE_CUBE_MAP)
        }
        return loadedTex
    }

    private fun TextureData.estimateTexSize(): Long {
        val layers = if (this is TextureDataCube) 6 else depth
        val mipLevels = floor(log2(max(width, height).toDouble())).toInt() + 1
        return Texture.estimatedTexSize(width, height, layers, mipLevels, format.pxSize).toLong()
    }
}