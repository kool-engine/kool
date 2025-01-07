package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.math.getNumMipLevels
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.vk.util.vkBytesPerPx
import de.fabmax.kool.pipeline.backend.vk.util.vkFormat
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import de.fabmax.kool.util.memStack
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import java.nio.ByteBuffer

object TextureLoader {
    fun createCubeTexture(sys: VkSystem, props: TextureProps, width: Int, height: Int, format: TexFormat = props.format) : LoadedTextureVk {
        val mipLevels = if (props.generateMipMaps) { getNumMipLevels(width, height) } else { 1 }

        val imgConfig = Image.Config()
        imgConfig.width = width
        imgConfig.height = height
        imgConfig.depth = 1
        imgConfig.mipLevels = mipLevels
        imgConfig.numSamples = VK_SAMPLE_COUNT_1_BIT
        imgConfig.format = format.vkFormat
        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
        imgConfig.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT
        imgConfig.allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
        imgConfig.arrayLayers = 6
        imgConfig.flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT

//        val textureImage = Image(sys, imgConfig)
//        val textureImageView =  ImageView(sys, textureImage.vkImage, textureImage.format, VK_IMAGE_ASPECT_COLOR_BIT,
//                textureImage.mipLevels, VK_IMAGE_VIEW_TYPE_CUBE, 6)
//        val sampler = createSampler(sys, props, textureImage)
//
//        val tex =  LoadedTextureVk(sys, format, textureImage, textureImageView, sampler)
//        tex.setSize(width, height, 1)
//        return tex
        TODO()
    }

    fun loadTextureCube(sys: VkSystem, props: TextureProps, cubeImg: ImageData) : LoadedTextureVk {
        if (cubeImg !is ImageDataCube) {
            throw IllegalArgumentException("Provided TextureData must be of type TextureDataCube")
        }

        val width = cubeImg.width
        val height = cubeImg.height
        val dstFmt = checkFormat(cubeImg.format)
        val imageSize = width * height * dstFmt.vkBytesPerPx.toLong() * 6
        val mipLevels = if (props.generateMipMaps) { getNumMipLevels(width, height) } else { 1 }

        val stagingAllocUsage = Vma.VMA_MEMORY_USAGE_CPU_ONLY
        val stagingBuffer = Buffer(sys, imageSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
        stagingBuffer.mapped {
            // order: +x, -x, +y, -y, +z, -z
            put(reshape(dstFmt, cubeImg.right))
            put(reshape(dstFmt, cubeImg.left))
            put(reshape(dstFmt, cubeImg.up))
            put(reshape(dstFmt, cubeImg.down))
            put(reshape(dstFmt, cubeImg.back))
            put(reshape(dstFmt, cubeImg.front))
        }

        if (props.generateMipMaps) {
            logW { "Mip-map generation for cube maps not yet implemented" }
        }

        val tex = createCubeTexture(sys, props, width, height, dstFmt)
        tex.textureImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyCubeBufferToImage(sys, stagingBuffer, tex.textureImage, width, height)
        stagingBuffer.destroy()

        if (mipLevels > 1) {
            logE { "Mipmap generation for cube maps not yet supported" }
            //tex.textureImage.generateMipmaps(sys)
        } else {
            tex.textureImage.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        }
        return tex
    }

    fun createTexture(sys: VkSystem, props: TextureProps, width: Int, height: Int, depth: Int, format: TexFormat = props.format) : LoadedTextureVk {
        val mipLevels = if (props.generateMipMaps) { getNumMipLevels(width, height) } else { 1 }

        val imgConfig = Image.Config()
        imgConfig.width = width
        imgConfig.height = height
        imgConfig.depth = depth
        imgConfig.mipLevels = mipLevels
        imgConfig.numSamples = VK_SAMPLE_COUNT_1_BIT
        imgConfig.format = format.vkFormat
        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
        imgConfig.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT
        imgConfig.allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY

//        val textureImage = Image(sys, imgConfig)
//        val textureImageView = ImageView(sys, textureImage, VK_IMAGE_ASPECT_COLOR_BIT)
//        val sampler = createSampler(sys, props, textureImage)
//
//        val tex =  LoadedTextureVk(sys, format, textureImage, textureImageView, sampler)
//        tex.setSize(width, height, depth)
//        return tex
        TODO()
    }

    fun loadTexture1d(sys: VkSystem, props: TextureProps, img: ImageData) : LoadedTextureVk {
        // 1d texture internally uses a 2d texture
        return loadTexture2d(sys, props, img)
    }

    fun loadTexture2d(sys: VkSystem, props: TextureProps, img: ImageData) : LoadedTextureVk {
        img as ImageData2d
        val width = img.width
        val height = img.height
        val dstFmt = checkFormat(img.format)
        val buf = reshape(dstFmt, img)

        val imageSize = width * height * dstFmt.vkBytesPerPx.toLong()
        val mipLevels = if (props.generateMipMaps) { getNumMipLevels(width, height) } else { 1 }

        val stagingAllocUsage = Vma.VMA_MEMORY_USAGE_CPU_ONLY
        val stagingBuffer = Buffer(sys, imageSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
        stagingBuffer.mapped {
            put(buf)
        }

        val tex = createTexture(sys, props, width, height, 1, dstFmt)
        tex.textureImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyBufferToImage(sys, stagingBuffer, tex.textureImage, width, height, 1)
        stagingBuffer.destroy()

        if (mipLevels > 1) {
            tex.textureImage.generateMipmaps(sys)
        } else {
            tex.textureImage.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        }
        return tex
    }

    fun loadTexture3d(sys: VkSystem, props: TextureProps, img: ImageData) : LoadedTextureVk {
        if (img !is BufferedImageData3d) {
            throw IllegalArgumentException("Provided TextureData must be of type TextureData3d")
        }

        val noMipMappingProps = props.copy(generateMipMaps = false)
        if (props.generateMipMaps) {
            logW { "Mip-mapping is not supported for 3D textures" }
        }

        val width = img.width
        val height = img.height
        val depth = img.depth
        val dstFmt = checkFormat(img.format)
        val buf = reshape(dstFmt, img)

        val imageSize = width * height * depth * dstFmt.vkBytesPerPx.toLong()
        val mipLevels = if (noMipMappingProps.generateMipMaps) { getNumMipLevels(width, height) } else { 1 }

        val stagingAllocUsage = Vma.VMA_MEMORY_USAGE_CPU_ONLY
        val stagingBuffer = Buffer(sys, imageSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
        stagingBuffer.mapped {
            put(buf)
        }

        val tex = createTexture(sys, noMipMappingProps, width, height, depth, dstFmt)
        tex.textureImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyBufferToImage(sys, stagingBuffer, tex.textureImage, width, height, depth)
        stagingBuffer.destroy()

        if (mipLevels > 1) {
            tex.textureImage.generateMipmaps(sys)
        } else {
            tex.textureImage.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        }
        return tex
    }

    private fun FilterMethod.vkFilterMethod(): Int {
        return when (this) {
            FilterMethod.NEAREST -> VK_FILTER_NEAREST
            FilterMethod.LINEAR -> VK_FILTER_LINEAR
        }
    }

    private fun AddressMode.vkAddressMode(): Int {
        return when(this) {
            AddressMode.CLAMP_TO_EDGE -> VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE
            AddressMode.MIRRORED_REPEAT -> VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT
            AddressMode.REPEAT -> VK_SAMPLER_ADDRESS_MODE_REPEAT
        }
    }

    private fun createSampler(sys: VkSystem, props: TextureProps, texImage: Image): Long {
        memStack {
            val samplerSettings = props.defaultSamplerSettings
            val isAnisotropy = props.generateMipMaps &&
                samplerSettings.minFilter == FilterMethod.LINEAR &&
                samplerSettings.magFilter == FilterMethod.LINEAR

            val samplerInfo = callocVkSamplerCreateInfo {
                sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                magFilter(samplerSettings.magFilter.vkFilterMethod())
                minFilter(samplerSettings.minFilter.vkFilterMethod())
                addressModeU(samplerSettings.addressModeU.vkAddressMode())
                addressModeV(samplerSettings.addressModeV.vkAddressMode())
                addressModeW(samplerSettings.addressModeW.vkAddressMode())
                anisotropyEnable(isAnisotropy)
                maxAnisotropy(if (isAnisotropy) samplerSettings.maxAnisotropy.toFloat() else 1f)
                borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)
                unnormalizedCoordinates(false)
                compareEnable(false)
                compareOp(VK_COMPARE_OP_ALWAYS)
                when (samplerSettings.minFilter) {
                    FilterMethod.NEAREST -> mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST)
                    FilterMethod.LINEAR -> mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR)
                }
                mipLodBias(0f)
                minLod(0f)
                maxLod(texImage.mipLevels.toFloat())
            }
            val ptr = mallocLong(1)
            check(vkCreateSampler(sys.logicalDevice.vkDevice, samplerInfo, null, ptr) == VK_SUCCESS)
            return ptr[0]
        }
    }

    @Suppress("DEPRECATION")
    private fun checkFormat(format: TexFormat): TexFormat {
        if (format == TexFormat.RGB) {
            // fixme: check support for 3-channel textures on system init, for now we assume that 3-channel textures are not supported...
            return TexFormat.RGBA
        }
        return format
    }

    @Suppress("UNUSED_PARAMETER")
    private fun reshape(dstFormat: TexFormat, img: ImageData): ByteBuffer {
        TODO()
//        return when (val imgData = img.data) {
//            is Uint8BufferImpl -> reshapeUint8(dstFormat, img, imgData)
//            is Float32BufferImpl -> reshapeFloat32(dstFormat, img, imgData)
//            else -> TODO("Other texture data buffers than Uint8 and Float32 are not yet implemented, provided: ${img.data::class}, dstFormat: $dstFormat")
//        }
    }

//    private fun reshapeUint8(dstFormat: TexFormat, img: ImageData, imgData: Uint8BufferImpl): ByteBuffer {
//        if (img.format != dstFormat) {
//            if (img.format == TexFormat.RGB && dstFormat == TexFormat.RGBA) {
//                val reshaped = Uint8Buffer(img.width * img.height * img.depth * 4)
//                for (i in 0 until img.width * img.height * img.depth) {
//                    reshaped[i*4+0] = imgData[i*3+0]
//                    reshaped[i*4+1] = imgData[i*3+1]
//                    reshaped[i*4+2] = imgData[i*3+2]
//                    reshaped[i*4+3] = 255u
//                }
//                return (reshaped as Uint8BufferImpl).getRawBuffer()
//            } else {
//                throw IllegalArgumentException("${img.format} -> $dstFormat not implemented")
//            }
//        }
//        return imgData.getRawBuffer()
//    }
//
//    private fun reshapeFloat32(dstFormat: TexFormat, img: ImageData, imgData: Float32BufferImpl): ByteBuffer {
//        if (img.format == dstFormat) {
//            val reshaped = Uint8Buffer(img.width * img.height * img.depth * img.format.vkBytesPerPx)
//            if (dstFormat.isF32) {
//                for (i in 0 until img.width * img.height * img.depth * img.format.channels) {
//                    reshaped.putF32(i, imgData[i])
//                }
//            } else {
//                for (i in 0 until img.width * img.height * img.depth * img.format.channels) {
//                    reshaped.putF16(i, imgData[i])
//                }
//            }
//            return (reshaped as Uint8BufferImpl).getRawBuffer()
//
//        } else if (img.format == TexFormat.RGB_F16 && dstFormat == TexFormat.RGBA_F16) {
//            val reshaped = Uint8Buffer(img.width * img.height * img.depth * 8)
//            for (i in 0 until img.width * img.height * img.depth) {
//                reshaped.putF16(i*4+0, imgData[i*3+0])
//                reshaped.putF16(i*4+1, imgData[i*3+1])
//                reshaped.putF16(i*4+2, imgData[i*3+2])
//                reshaped.putF16(i*4+3, 1f)
//            }
//            return (reshaped as Uint8BufferImpl).getRawBuffer()
//
//        } else if (img.format == TexFormat.RGB_F32 && dstFormat == TexFormat.RGBA_F32) {
//            val reshaped = Uint8Buffer(img.width * img.height * img.depth * 16)
//            for (i in 0 until img.width * img.height * img.depth) {
//                reshaped.putF32(i*4+0, imgData[i*3+0])
//                reshaped.putF32(i*4+1, imgData[i*3+1])
//                reshaped.putF32(i*4+2, imgData[i*3+2])
//                reshaped.putF32(i*4+3, 1f)
//            }
//            return (reshaped as Uint8BufferImpl).getRawBuffer()
//        }
//        throw IllegalArgumentException("${img.format} -> $dstFormat not implemented")
//    }

    private fun Uint8Buffer.putF32(index: Int, f32: Float) {
        val f32bits = f32.toBits()
        val byteI = index * 2
        this[byteI] = (f32bits and 0xff).toUByte()
        this[byteI+1] = ((f32bits shr 8) and 0xff).toUByte()
        this[byteI+2] = ((f32bits shr 16) and 0xff).toUByte()
        this[byteI+3] = ((f32bits shr 24) and 0xff).toUByte()
    }

    private fun Uint8Buffer.putF16(index: Int, f32: Float) {
        // from: https://stackoverflow.com/questions/3026441/float32-to-float16
        val f32bits = f32.toBits()
        var f16bits = (f32bits shr 31) shl 5
        var tmp = (f32bits shr 23) and 0xff
        tmp = (tmp - 0x70) and ((((0x70 - tmp) shr 4) shr 27) and 0x1f)
        f16bits = (f16bits or tmp) shl 10
        f16bits = f16bits or ((f32bits shr 13) and 0x3ff)

        val byteI = index * 2
        this[byteI] = (f16bits and 0xff).toUByte()
        this[byteI+1] = (f16bits shr 8).toUByte()
    }

    private fun copyBufferToImage(sys: VkSystem, buffer: Buffer, image: Image, width: Int, height: Int, depth: Int) {
        sys.transferCommandPool.singleTimeCommands { commandBuffer ->
            val region = callocVkBufferImageCopyN(1) {
                imageSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.mipLevel(0)
                    it.baseArrayLayer(0)
                    it.layerCount(1)
                }
                imageOffset { it.set(0, 0, 0) }
                imageExtent { it.set(width, height, depth) }
            }
            vkCmdCopyBufferToImage(commandBuffer, buffer.vkBuffer, image.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region)
        }
    }

    private fun copyCubeBufferToImage(sys: VkSystem, buffer: Buffer, image: Image, width: Int, height: Int) {
        sys.transferCommandPool.singleTimeCommands { commandBuffer ->
            val regions = callocVkBufferImageCopyN(6) {
                for (face in 0..5) {
                    this[face].apply {
                        bufferOffset(width * height * 4L * face)
                        imageSubresource {
                            it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                            it.mipLevel(0)
                            it.baseArrayLayer(face)
                            it.layerCount(1)
                        }
                        imageExtent { it.set(width, height, 1) }
                    }
                }
            }
            vkCmdCopyBufferToImage(commandBuffer, buffer.vkBuffer, image.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, regions)
        }
    }
}