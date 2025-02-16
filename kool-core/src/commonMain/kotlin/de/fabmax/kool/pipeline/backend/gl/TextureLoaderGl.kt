package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.pipeline.*

object TextureLoaderGl {
    private val loadedTextures = mutableMapOf<String, LoadedTextureGl>()

    init {
        KoolSystem.getContextOrNull()?.onShutdown += { loadedTextures.clear() }
    }

    fun loadTexture(tex: Texture<*>, backend: RenderBackendGl): LoadedTextureGl {
        val data = checkNotNull(tex.uploadData)
        tex.uploadData = null

        check(tex.format == data.format) {
            "Image data format doesn't match texture format: ${data.format} != ${tex.format}"
        }

        var loaded = loadedTextures[data.id]
        if (loaded != null && loaded.isReleased) { loadedTextures -= data.id }
        loaded = loadedTextures.getOrPut(data.id) {
            when {
                tex is Texture1d && data is ImageData1d -> loadTexture1dCompat(tex, data, backend)
                tex is Texture2d && data is ImageData2d -> loadTexture2d(tex, data, backend)
                tex is Texture3d && data is ImageData3d -> loadTexture3d(tex, data, backend)
                tex is TextureCube && data is ImageDataCube -> loadTextureCube(tex, data, backend)
                tex is Texture2dArray && data is ImageData3d -> loadTexture2dArray(tex, data, backend)
                tex is TextureCubeArray && data is ImageDataCubeArray -> loadTextureCubeArray(tex, data, backend)
                else -> error("Invalid texture / image data combination: ${tex::class.simpleName} / ${data::class.simpleName}")
            }
        }
        tex.gpuTexture?.release()
        tex.gpuTexture = loaded
        return loaded
    }

    private fun loadTexture1dCompat(tex: Texture1d, img: ImageData1d, backend: RenderBackendGl): LoadedTextureGl {
        // 1d texture internally uses a 2d texture
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, 1, 1)
        loadedTex.bind()
        gl.texImage1d(img)
        if (tex.mipMapping.isMipMapped) {
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
        if (tex.mipMapping.isMipMapped) {
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
        if (tex.mipMapping.isMipMapped) {
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
        if (tex.mipMapping.isMipMapped) {
            gl.generateMipmap(gl.TEXTURE_CUBE_MAP)
        }
        return loadedTex
    }

    private fun loadTexture2dArray(tex: Texture2dArray, img: ImageData3d, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_2D_ARRAY, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, img.height, img.depth)
        loadedTex.bind()

        if (img is ImageData2dArray) {
            val levels = if (tex.mipMapping.isMipMapped) numMipLevels(img.width, img.height) else 1
            gl.texStorage3d(gl.TEXTURE_2D_ARRAY, levels, img.format.glInternalFormat(gl), img.width, img.height, img.images.size)
            for (i in img.images.indices) {
                gl.texSubImage3d(gl.TEXTURE_2D_ARRAY, 0, 0, 0, i, img.width, img.height, 1, img.format.glFormat(gl), img.format.glType(gl), img.images[i])
            }
        } else {
            gl.texImage3d(gl.TEXTURE_2D_ARRAY, img)
        }

        if (tex.mipMapping.isMipMapped) {
            gl.generateMipmap(gl.TEXTURE_2D_ARRAY)
        }
        return loadedTex
    }

    private fun loadTextureCubeArray(tex: TextureCubeArray, img: ImageDataCubeArray, backend: RenderBackendGl) : LoadedTextureGl {
        val gl = backend.gl
        val loadedTex = LoadedTextureGl(gl.TEXTURE_CUBE_MAP_ARRAY, gl.createTexture(), backend, tex, img.estimateTexSize())
        loadedTex.setSize(img.width, img.height, img.slices)
        loadedTex.bind()
        val levels = if (tex.mipMapping.isMipMapped) numMipLevels(img.width, img.height) else 1
        gl.texStorage3d(gl.TEXTURE_CUBE_MAP_ARRAY, levels, img.format.glInternalFormat(gl), img.width, img.height, 6 * img.slices)
        img.cubes.forEachIndexed { i, cube ->
            gl.texSubImage3d(gl.TEXTURE_CUBE_MAP_ARRAY, 0, 0, 0, i * 6 + 0, img.width, img.height, 1, img.format.glFormat(gl), img.format.glType(gl), cube.posX)
            gl.texSubImage3d(gl.TEXTURE_CUBE_MAP_ARRAY, 0, 0, 0, i * 6 + 1, img.width, img.height, 1, img.format.glFormat(gl), img.format.glType(gl), cube.negX)
            gl.texSubImage3d(gl.TEXTURE_CUBE_MAP_ARRAY, 0, 0, 0, i * 6 + 2, img.width, img.height, 1, img.format.glFormat(gl), img.format.glType(gl), cube.posY)
            gl.texSubImage3d(gl.TEXTURE_CUBE_MAP_ARRAY, 0, 0, 0, i * 6 + 3, img.width, img.height, 1, img.format.glFormat(gl), img.format.glType(gl), cube.negY)
            gl.texSubImage3d(gl.TEXTURE_CUBE_MAP_ARRAY, 0, 0, 0, i * 6 + 4, img.width, img.height, 1, img.format.glFormat(gl), img.format.glType(gl), cube.posZ)
            gl.texSubImage3d(gl.TEXTURE_CUBE_MAP_ARRAY, 0, 0, 0, i * 6 + 5, img.width, img.height, 1, img.format.glFormat(gl), img.format.glType(gl), cube.negZ)
        }
        if (tex.mipMapping.isMipMapped) {
            gl.generateMipmap(gl.TEXTURE_CUBE_MAP_ARRAY)
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
            is ImageDataCubeArray -> {
                width = this.width
                height = this.height
                depth = 6 * this.slices
            }
        }
        return Texture.estimatedTexSize(width, height, depth, numMipLevels(width, height), format.pxSize).toLong()
    }
}