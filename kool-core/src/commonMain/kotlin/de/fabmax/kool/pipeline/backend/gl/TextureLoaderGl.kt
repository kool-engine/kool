package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max

object TextureLoaderGl {

    fun loadTexture(tex: Texture, data: ImageData, backend: RenderBackendGl): LoadedTextureGl {
        val loaded = when {
            tex is Texture1d && data is ImageData1d -> loadTexture1dCompat(tex, data, backend)
            tex is Texture2d && data is ImageData2d -> loadTexture2d(tex, data, backend)
            tex is Texture3d && data is ImageData3d -> loadTexture3d(tex, data, backend)
            tex is TextureCube && data is ImageDataCube -> loadTextureCube(tex, data, backend)
            else -> error("Invalid texture / image data combination: ${tex::class.simpleName} / ${data::class.simpleName}")
        }
        tex.gpuTexture = loaded
        tex.loadingState = Texture.LoadingState.LOADED
        return loaded
    }

    private fun loadTexture1dCompat(tex: Texture1d, img: ImageData1d, backend: RenderBackendGl): LoadedTextureGl {
        // 1d texture internally uses a 2d texture
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, 1, 1)
        loadedTex.bind()
        gl.texImage1d(img)
        if (tex.props.generateMipMaps) {
            gl.generateMipmap(gl.TEXTURE_2D)
        }
        return loadedTex
    }

    private fun loadTexture2d(tex: Texture2d, img: ImageData2d, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, img.height, 1)
        loadedTex.bind()
        gl.texImage2d(gl.TEXTURE_2D, img)
        if (tex.props.generateMipMaps) {
            gl.generateMipmap(gl.TEXTURE_2D)
        }
        return loadedTex
    }

    private fun loadTexture3d(tex: Texture3d, img: ImageData3d, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_3D, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, img.height, img.depth)
        loadedTex.bind()
        gl.texImage3d(gl.TEXTURE_3D, img)
        if (tex.props.generateMipMaps) {
            gl.generateMipmap(gl.TEXTURE_3D)
        }
        return loadedTex
    }

    private fun loadTextureCube(tex: TextureCube, img: ImageDataCube, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_CUBE_MAP, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, img.height, 1)
        loadedTex.bind()
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_X, img.posX)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_X, img.negX)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_Y, img.posY)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_Y, img.negY)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_POSITIVE_Z, img.posZ)
        gl.texImage2d(gl.TEXTURE_CUBE_MAP_NEGATIVE_Z, img.negZ)
        if (tex.props.generateMipMaps) {
            gl.generateMipmap(gl.TEXTURE_CUBE_MAP)
        }
        return loadedTex
    }

    private fun ImageData.estimateTexSize(): Long {
        var width = 1
        var height = 1
        var depth = 1
        when (this) {
            is ImageData1d -> width = this.width
            is ImageData2d -> {
                width = this.width
                height = this.height
            }
            is ImageData3d -> {
                width = this.width
                height = this.height
                depth = this.depth
            }
            is ImageDataCube -> {
                width = this.width
                height = this.height
                depth = 6
            }
        }
        val mipLevels = floor(log2(max(width, height).toDouble())).toInt() + 1
        return Texture.estimatedTexSize(width, height, depth, mipLevels, format.pxSize).toLong()
    }
}