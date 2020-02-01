package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.util.vkBytesPerPx
import de.fabmax.kool.platform.vk.util.vkFormat
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.createUint8Buffer
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import java.nio.ByteBuffer
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max

object TextureLoader {
    fun loadCubeMap(sys: VkSystem, cubeImg: CubeMapTextureData) : LoadedTexture {
        val width = cubeImg.width
        val height = cubeImg.height
        val dstFmt = checkFormat(cubeImg.format)
        val imageSize = width * height * dstFmt.vkBytesPerPx.toLong() * 6

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

        val imgConfig = Image.Config()
        imgConfig.width = width
        imgConfig.height = height
        imgConfig.mipLevels = 1
        imgConfig.numSamples = VK_SAMPLE_COUNT_1_BIT
        imgConfig.format = dstFmt.vkFormat
        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
        imgConfig.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT
        imgConfig.allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
        imgConfig.arrayLayers = 6
        imgConfig.flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT
        val textureImage = Image(sys, imgConfig)
        textureImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyCubeBufferToImage(sys, stagingBuffer, textureImage, width, height)
        textureImage.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        stagingBuffer.destroy()

        val textureImageView = ImageView(sys, textureImage.vkImage, textureImage.format, VK_IMAGE_ASPECT_COLOR_BIT,
                textureImage.mipLevels, VK_IMAGE_VIEW_TYPE_CUBE)

        val sampler = createSampler(sys, textureImage)
        return LoadedTexture(sys, dstFmt, textureImage, textureImageView, sampler)
    }

    fun loadTexture(sys: VkSystem, img: BufferedTextureData) : LoadedTexture {
        val width = img.width
        val height = img.height
        val dstFmt = checkFormat(img.format)
        val buf = reshape(dstFmt, img)

        val imageSize = width * height * dstFmt.vkBytesPerPx.toLong()
        val mipLevels = floor(log2(max(width, height).toDouble())).toInt() + 1

        val stagingAllocUsage = Vma.VMA_MEMORY_USAGE_CPU_ONLY
        val stagingBuffer = Buffer(sys, imageSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
        stagingBuffer.mapped {
            put(buf)
        }

        val imgConfig = Image.Config()
        imgConfig.width = width
        imgConfig.height = height
        imgConfig.mipLevels = mipLevels
        imgConfig.numSamples = VK_SAMPLE_COUNT_1_BIT
        imgConfig.format = dstFmt.vkFormat
        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
        imgConfig.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT
        imgConfig.allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
        val textureImage = Image(sys, imgConfig)
        textureImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyBufferToImage(sys, stagingBuffer, textureImage, width, height)
        if (mipLevels > 1) {
            textureImage.generateMipmaps(sys)
        } else {
            textureImage.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        }
        stagingBuffer.destroy()

        val textureImageView = ImageView(sys, textureImage, VK_IMAGE_ASPECT_COLOR_BIT)

        val sampler = createSampler(sys, textureImage)
        return LoadedTexture(sys, dstFmt, textureImage, textureImageView, sampler)
    }

    private fun createSampler(sys: VkSystem, texImage: Image): Long {
        memStack {
            val samplerInfo = callocVkSamplerCreateInfo {
                sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                magFilter(VK_FILTER_LINEAR)
                minFilter(VK_FILTER_LINEAR)
                addressModeU(VK_SAMPLER_ADDRESS_MODE_REPEAT)
                addressModeV(VK_SAMPLER_ADDRESS_MODE_REPEAT)
                addressModeW(VK_SAMPLER_ADDRESS_MODE_REPEAT)
                anisotropyEnable(true)
                maxAnisotropy(16f)
                borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)
                unnormalizedCoordinates(false)
                compareEnable(false)
                compareOp(VK_COMPARE_OP_ALWAYS)
                mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR)
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
        if (img.format != dstFormat) {
            if (img.format == TexFormat.RGB && dstFormat == TexFormat.RGBA) {
                val imgData = img.data as Uint8BufferImpl
                val reshaped = createUint8Buffer(img.width * img.height * 4)
                for (i in 0 until img.width * img.height) {
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
        return (img.data as Uint8BufferImpl).buffer
    }

    private fun copyBufferToImage(sys: VkSystem, buffer: Buffer, image: Image, width: Int, height: Int) {
        sys.transferCommandPool.singleTimeCommands { commandBuffer ->
            val region = callocVkBufferImageCopyN(1) {
                bufferOffset(0L)
                bufferRowLength(0)
                bufferImageHeight(0)
                imageSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.mipLevel(0)
                    it.baseArrayLayer(0)
                    it.layerCount(1)
                }
                imageOffset { it.set(0, 0, 0) }
                imageExtent { it.set(width, height, 1) }
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