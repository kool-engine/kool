package de.fabmax.kool.platform.vk

import de.fabmax.kool.math.getNumMipLevels
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.util.vkBytesPerPx
import de.fabmax.kool.platform.vk.util.vkFormat
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.createUint8Buffer
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import java.nio.ByteBuffer

object TextureLoader {
    fun createCubeTexture(sys: VkSystem, props: TextureProps, width: Int, height: Int, format: TexFormat = props.format) : LoadedTextureVk {
        val mipLevels = if (props.mipMapping) { getNumMipLevels(width, height) } else { 1 }

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

        val textureImage = Image(sys, imgConfig)
        val textureImageView =  ImageView(sys, textureImage.vkImage, textureImage.format, VK_IMAGE_ASPECT_COLOR_BIT,
                textureImage.mipLevels, VK_IMAGE_VIEW_TYPE_CUBE)
        val sampler = createSampler(sys, props, textureImage)

        val tex =  LoadedTextureVk(sys, format, textureImage, textureImageView, sampler)
        tex.setSize(width, height, 1)
        return tex
    }

    fun loadCubeMap(sys: VkSystem, props: TextureProps, cubeImg: TextureDataCube) : LoadedTextureVk {
        val width = cubeImg.width
        val height = cubeImg.height
        val dstFmt = checkFormat(cubeImg.format)
        val imageSize = width * height * dstFmt.vkBytesPerPx.toLong() * 6
        val mipLevels = if (props.mipMapping) { getNumMipLevels(width, height) } else { 1 }

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

        if (props.mipMapping) {
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
        val mipLevels = if (props.mipMapping) { getNumMipLevels(width, height) } else { 1 }

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

        val textureImage = Image(sys, imgConfig)
        val textureImageView = ImageView(sys, textureImage, VK_IMAGE_ASPECT_COLOR_BIT)
        val sampler = createSampler(sys, props, textureImage)

        val tex =  LoadedTextureVk(sys, format, textureImage, textureImageView, sampler)
        tex.setSize(width, height, depth)
        return tex
    }

    fun loadTexture2d(sys: VkSystem, props: TextureProps, img: TextureData) : LoadedTextureVk {
        val width = img.width
        val height = img.height
        val dstFmt = checkFormat(img.format)
        val buf = reshape(dstFmt, img)

        val imageSize = width * height * dstFmt.vkBytesPerPx.toLong()
        val mipLevels = if (props.mipMapping) { getNumMipLevels(width, height) } else { 1 }

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

    fun loadTexture3d(sys: VkSystem, props: TextureProps, img: TextureData3d) : LoadedTextureVk {
        val noMipMappingProps = props.copy(mipMapping = false)
        if (props.mipMapping) {
            logW { "Mip-mapping is not supported for 3D textures" }
        }

        val width = img.width
        val height = img.height
        val depth = img.depth
        val dstFmt = checkFormat(img.format)
        val buf = reshape(dstFmt, img)

        val imageSize = width * height * depth * dstFmt.vkBytesPerPx.toLong()
        val mipLevels = if (noMipMappingProps.mipMapping) { getNumMipLevels(width, height) } else { 1 }

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
            val samplerInfo = callocVkSamplerCreateInfo {
                sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                magFilter(props.magFilter.vkFilterMethod())
                minFilter(props.minFilter.vkFilterMethod())
                addressModeU(props.addressModeU.vkAddressMode())
                addressModeV(props.addressModeV.vkAddressMode())
                addressModeW(props.addressModeW.vkAddressMode())
                anisotropyEnable(props.maxAnisotropy > 1)
                maxAnisotropy(props.maxAnisotropy.toFloat())
                borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)
                unnormalizedCoordinates(false)
                compareEnable(false)
                compareOp(VK_COMPARE_OP_ALWAYS)
                when (props.minFilter) {
                    FilterMethod.NEAREST -> mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST)
                    FilterMethod.LINEAR -> mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR)
                }
                mipLodBias(0f)
                minLod(0f)
                maxLod(texImage.mipLevels.toFloat())
            }
            val ptr = mallocLong(1)
            check(vkCreateSampler(sys.device.vkDevice, samplerInfo, null, ptr) == VK_SUCCESS)
            return ptr[0]
        }
    }

    private fun checkFormat(format: TexFormat): TexFormat {
        if (format == TexFormat.RGB) {
            // fixme: check support for 3-channel textures on system init, for now we assume that 3-channel textures are not supported...
            return TexFormat.RGBA
        }
        return format
    }

    private fun reshape(dstFormat: TexFormat, img: TextureData): ByteBuffer {
        if (img.data !is Uint8BufferImpl) {
            TODO("Other texture data buffers than Uint8 are not yet implemented")
        }
        val imgData = img.data as Uint8BufferImpl

        // make sure buffer position is at 0
        imgData.buffer.rewind()

        if (img.format != dstFormat) {
            if (img.format == TexFormat.RGB && dstFormat == TexFormat.RGBA) {
                val reshaped = createUint8Buffer(img.width * img.height * img.depth * 4)
                for (i in 0 until img.width * img.height * img.depth) {
                    reshaped[i*4+0] = imgData[i*3+0]
                    reshaped[i*4+1] = imgData[i*3+1]
                    reshaped[i*4+2] = imgData[i*3+2]
                    reshaped[i*4+3] = 255.toByte()
                }
                return (reshaped as Uint8BufferImpl).buffer
            } else {
                throw IllegalArgumentException("${img.format} -> $dstFormat not implemented")
            }
        }
        return imgData.buffer
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
            vkCmdCopyBufferToImage(commandBuffer, buffer.vkBuffer, image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region)
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
            vkCmdCopyBufferToImage(commandBuffer, buffer.vkBuffer, image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, regions)
        }
    }
}