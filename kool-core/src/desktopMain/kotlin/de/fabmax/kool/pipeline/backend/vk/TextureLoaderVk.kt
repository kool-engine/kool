package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32BufferImpl
import de.fabmax.kool.util.Uint16BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import org.lwjgl.vulkan.VK10.*


class TextureLoaderVk(val backend: RenderBackendVk) {
    private val loadedTextures = mutableMapOf<String, ImageVk>()

    private val device: Device get() = backend.device

    fun loadTexture(tex: Texture<*>) {
        val data = checkNotNull(tex.uploadData)
        tex.uploadData = null

        check(tex.props.format == data.format) {
            "Image data format doesn't match texture format: ${data.format} != ${tex.props.format}"
        }

        var loaded = loadedTextures[data.id]
        if (loaded != null && loaded.isReleased) { loadedTextures -= data.id }

        loaded = when {
//            tex is Texture1d && data is ImageData1d -> loadTexture1d(tex, data)
            tex is Texture2d && data is ImageData2d -> loadTexture2d(tex, data)
//            tex is Texture3d && data is ImageData3d -> loadTexture3d(tex, data)
//            tex is TextureCube && data is ImageDataCube -> loadTextureCube(tex, data)
//            tex is Texture2dArray && data is ImageData3d -> loadTexture2dArray(tex, data)
//            tex is TextureCubeArray && data is ImageDataCubeArray -> loadTextureCubeAray(tex, data)
            else -> error("Invalid texture / image data combination: ${tex::class.simpleName} / ${data::class.simpleName}")
        }
        tex.gpuTexture = loaded
        tex.loadingState = Texture.LoadingState.LOADED
    }

    private fun loadTexture2d(tex: Texture2d, data: ImageData2d): ImageVk {
        val isMipMap = tex.props.generateMipMaps
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = data.format.vk,
            width = data.width,
            height = data.height,
            depth = 1,
            arrayLayers = 1,
            mipLevels = if (isMipMap) numMipLevels(data.width, data.height) else 1,
            samples = VK_SAMPLE_COUNT_1_BIT,
            usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_SAMPLED_BIT
        )
        val image = ImageVk(backend, imgInfo)

        val bufSize = data.width * data.height * data.format.vkBytesPerPx.toLong()
        backend.memManager.stagingBuffer(bufSize) { stagingBuf ->
            val dstLayout = if (isMipMap) VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL else VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL
            data.copyToStagingBuffer(stagingBuf)
            backend.commandPool.singleShotCommands {
                image.copyFromBuffer(stagingBuf, it, dstLayout)
            }
        }

        if (isMipMap) {
            image.generateMipmaps()
        }
        return image
    }

    private fun ImageData.copyToStagingBuffer(target: VkBuffer) {
        val targetBuf = checkNotNull(target.mapped) { "Staging buffer is not mapped" }
        when (this) {
            is BufferedImageData1d -> when (val buf = data) {
                is Uint8BufferImpl -> buf.useRaw { targetBuf.put(it) }
                is Uint16BufferImpl -> buf.useRaw { targetBuf.asShortBuffer().put(it) }
                is Int32BufferImpl -> buf.useRaw { targetBuf.asIntBuffer().put(it) }
                is Float32BufferImpl -> buf.useRaw { targetBuf.asFloatBuffer().put(it) }
                else -> error("ImageData buffer must be any of Uint8Buffer, Uint16Buffer, Int32Buffer, Float32Buffer")
            }
            is BufferedImageData2d -> when (val buf = data) {
                is Uint8BufferImpl -> buf.useRaw { targetBuf.put(it) }
                is Uint16BufferImpl -> buf.useRaw { targetBuf.asShortBuffer().put(it) }
                is Int32BufferImpl -> buf.useRaw { targetBuf.asIntBuffer().put(it) }
                is Float32BufferImpl -> buf.useRaw { targetBuf.asFloatBuffer().put(it) }
                else -> error("ImageData buffer must be any of Uint8Buffer, Uint16Buffer, Int32Buffer, Float32Buffer")
            }
            else -> error("Invalid ImageData type for texImage2d: $this")
        }
    }
}