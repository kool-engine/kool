package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.math.float32ToFloat16
import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.VK10.*
import java.nio.ByteBuffer


class TextureLoaderVk(val backend: RenderBackendVk) {
    private val loadedTextures = mutableMapOf<String, ImageVk>()

    private val device: Device get() = backend.device

    fun loadTexture(tex: Texture<*>) {
        val data = checkNotNull(tex.uploadData)
        tex.uploadData = null

        check(tex.format == data.format) {
            "Image data format doesn't match texture format: ${data.format} != ${tex.format}"
        }

        var loaded = loadedTextures[data.id]
        if (loaded != null && loaded.isReleased) { loadedTextures -= data.id }

        loaded = when {
            tex is Texture1d && data is ImageData1d -> loadTexture1d(tex, data)
            tex is Texture2d && data is ImageData2d -> loadTexture2d(tex, data)
            tex is Texture3d && data is ImageData3d -> loadTexture3d(tex, data)
            tex is TextureCube && data is ImageDataCube -> loadTextureCube(tex, data)
            tex is Texture2dArray && data is ImageData3d -> loadTexture2dArray(tex, data)
            tex is TextureCubeArray && data is ImageDataCubeArray -> loadTextureCubeArray(tex, data)
            else -> error("Invalid texture / image data combination: ${tex::class.simpleName} / ${data::class.simpleName}")
        }
        tex.gpuTexture?.release()
        tex.gpuTexture = loaded
    }

    private fun loadTexture1d(tex: Texture1d, data: ImageData1d): ImageVk = loadTexture(
        tex = tex,
        type = VK_IMAGE_TYPE_1D,
        data = data,
        width = data.width,
    )

    private fun loadTexture2d(tex: Texture2d, data: ImageData2d): ImageVk = loadTexture(
        tex = tex,
        type = VK_IMAGE_TYPE_2D,
        data = data,
        width = data.width,
        height = data.height,
        mipMapping = tex.mipMapping,
    )

    private fun loadTexture3d(tex: Texture3d, data: ImageData3d): ImageVk = loadTexture(
        tex = tex,
        type = VK_IMAGE_TYPE_3D,
        data = data,
        width = data.width,
        height = data.height,
        depth = data.depth,
    )

    private fun loadTextureCube(tex: TextureCube, data: ImageDataCube): ImageVk = loadTexture(
        tex = tex,
        type = VK_IMAGE_TYPE_2D,
        data = data,
        width = data.width,
        height = data.height,
        layers = 6,
        mipMapping = tex.mipMapping,
        flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT
    )

    private fun loadTexture2dArray(tex: Texture2dArray, data: ImageData3d): ImageVk = loadTexture(
        tex = tex,
        type = VK_IMAGE_TYPE_2D,
        data = data,
        width = data.width,
        height = data.height,
        layers = data.depth,
        mipMapping = tex.mipMapping,
    )

    private fun loadTextureCubeArray(tex: TextureCubeArray, data: ImageDataCubeArray): ImageVk = loadTexture(
        tex = tex,
        type = VK_IMAGE_TYPE_2D,
        data = data,
        width = data.width,
        height = data.height,
        layers = 6 * data.slices,
        mipMapping = tex.mipMapping,
        flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT
    )

    private fun loadTexture(
        tex: Texture<*>,
        type: Int,
        data: ImageData,
        width: Int,
        height: Int = 1,
        depth: Int = 1,
        layers: Int = 1,
        mipMapping: MipMapping = MipMapping.Off,
        flags: Int = 0
    ): ImageVk {
        if (mipMapping != tex.mipMapping) {
            logW { "Texture ${tex.name} requests mip-mapping ${tex.mipMapping} but is forced to $mipMapping due to texture format" }
        }
        val srcUsage = if (mipMapping.isMipMapped) VK_IMAGE_USAGE_TRANSFER_SRC_BIT else 0
        val levels = when (mipMapping) {
            MipMapping.Full -> numMipLevels(width, height, depth)
            is MipMapping.Limited -> mipMapping.numLevels
            MipMapping.Off -> 1
        }
        val imgInfo = ImageInfo(
            imageType = type,
            format = data.format.vk,
            width = width,
            height = height,
            depth = depth,
            arrayLayers = layers,
            mipLevels = levels,
            samples = VK_SAMPLE_COUNT_1_BIT,
            usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT or srcUsage,
            flags = flags,
            aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
            label = tex.name
        )
        val image = ImageVk(backend, imgInfo)

        val bufSize = width * height * depth * layers * data.format.vkBytesPerPx.toLong()
        backend.memManager.stagingBuffer(bufSize, bufferDeleteTicks = 0) { stagingBuf ->
            copyTextureData(data, stagingBuf, data.format)

            backend.commandPool.singleShotCommands { commandBuffer ->
                val dstLayout = when {
                    mipMapping.isMipMapped -> VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL
                    else -> VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL
                }
                image.copyFromBuffer(stagingBuf, commandBuffer, dstLayout)
                if (mipMapping.isMipMapped) {
                    memStack { image.generateMipmaps(this, commandBuffer) }
                }
            }
        }
        return image
    }

    private fun copyTextureData(src: ImageData, dst: VkBuffer, dstFormat: TexFormat) {
        val dstBuf = checkNotNull(dst.mapped) { "Staging buffer is not mapped" }
        when (src) {
            is BufferedImageData1d -> dstBuf.putTextureData(src.data, dstFormat)
            is BufferedImageData2d -> dstBuf.putTextureData(src.data, dstFormat)
            is BufferedImageData3d -> dstBuf.putTextureData(src.data, dstFormat)
            is ImageDataCube -> {
                copyTextureData(src.posX, dst, dstFormat)
                copyTextureData(src.negX, dst, dstFormat)
                copyTextureData(src.posY, dst, dstFormat)
                copyTextureData(src.negY, dst, dstFormat)
                copyTextureData(src.posZ, dst, dstFormat)
                copyTextureData(src.negZ, dst, dstFormat)
            }
            is ImageDataCubeArray -> {
                src.cubes.forEachIndexed { i, cube ->
                    copyTextureData(cube.posX, dst, dstFormat)
                    copyTextureData(cube.negX, dst, dstFormat)
                    copyTextureData(cube.posY, dst, dstFormat)
                    copyTextureData(cube.negY, dst, dstFormat)
                    copyTextureData(cube.posZ, dst, dstFormat)
                    copyTextureData(cube.negZ, dst, dstFormat)
                }
            }
            is ImageData2dArray -> {
                for (i in src.images.indices) {
                    copyTextureData(src.images[i], dst, dstFormat)
                }
            }
            else -> error("Not implemented: ${src::class.simpleName}")
        }
    }

    private fun ByteBuffer.putTextureData(src: Buffer, dstFormat: TexFormat) {
        when (src) {
            is Uint8BufferImpl -> src.useRaw { put(it) }
            is Uint16BufferImpl -> src.useRaw { shorts ->
                asShortBuffer().put(shorts)
                position(position() + shorts.limit() * 2)
            }
            is Int32BufferImpl -> src.useRaw { ints ->
                asIntBuffer().put(ints)
                position(position() + ints.limit() * 4)
            }
            is Float32BufferImpl -> src.useRaw { floats ->
                if (dstFormat.isF16) {
                    for (i in 0 until floats.limit()) {
                        float32ToFloat16(floats[i]) { high, low ->
                            put(low)
                            put(high)
                        }
                    }
                } else {
                    asFloatBuffer().put(floats)
                    position(position() + floats.limit() * 4)
                }
            }
            else -> error("src buffer must be any of Uint8Buffer, Uint16Buffer, Int32Buffer, Float32Buffer")
        }
    }
}