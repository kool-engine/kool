package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureDataCube
import de.fabmax.kool.pipeline.TextureProps
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max

object TextureLoaderGl {
    fun loadTexture1d(props: TextureProps, img: TextureData, backend: RenderBackendGl): LoadedTextureGl {
        // 1d texture internally uses a 2d texture
        val gl = backend.gl
        val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), img.estimateTexSize(), backend)
        tex.setSize(img.width, 1, 1)
        tex.applySamplerProps(props)

        gl.texImage2d(gl.TEXTURE_2D, img)
        if (props.mipMapping) {
            gl.generateMipmap(gl.TEXTURE_2D)
        }
        return tex
    }

    fun loadTexture2d(props: TextureProps, img: TextureData, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), img.estimateTexSize(), backend)
        tex.setSize(img.width, img.height, 1)
        tex.applySamplerProps(props)

        gl.texImage2d(gl.TEXTURE_2D, img)
        if (props.mipMapping) {
            gl.generateMipmap(gl.TEXTURE_2D)
        }
        return tex
    }

    fun loadTexture3d(props: TextureProps, img: TextureData, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val tex = LoadedTextureGl(gl.TEXTURE_3D, gl.createTexture(), img.estimateTexSize(), backend)
        tex.setSize(img.width, img.height, img.depth)
        tex.applySamplerProps(props)

        gl.texImage3d(gl.TEXTURE_3D, img)
        if (props.mipMapping) {
            gl.generateMipmap(gl.TEXTURE_3D)
        }
        return tex
    }

    fun loadTextureCube(props: TextureProps, img: TextureData, backend: RenderBackendGl) : LoadedTextureGl {
        if (img !is TextureDataCube) {
            throw IllegalArgumentException("Provided TextureData must be of type TextureDataCube")
        }

        val gl = backend.gl
        val tex = LoadedTextureGl(gl.TEXTURE_CUBE_MAP, gl.createTexture(), img.estimateTexSize(), backend)
        tex.setSize(img.width, img.height, 1)
        tex.applySamplerProps(props)

        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_X, img.right)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_X, img.left)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_Y, img.up)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_Y, img.down)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_Z, img.back)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_Z, img.front)
        if (props.mipMapping) {
            gl.generateMipmap(gl.TEXTURE_CUBE_MAP)
        }
        return tex
    }

    private fun TextureData.estimateTexSize(): Int {
        val layers = if (this is TextureDataCube) 6 else depth
        val mipLevels = floor(log2(max(width, height).toDouble())).toInt() + 1
        return Texture.estimatedTexSize(width, height, layers, mipLevels, format.pxSize)
    }
}