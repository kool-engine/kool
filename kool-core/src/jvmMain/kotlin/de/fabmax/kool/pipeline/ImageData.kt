package de.fabmax.kool.pipeline

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.gl.GL_ALPHA
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.createUint8Buffer
import de.fabmax.kool.util.logD
import org.lwjgl.opengl.GL11.GL_RGB
import org.lwjgl.opengl.GL11.GL_RGBA
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkFormatProperties
import java.io.FileInputStream
import javax.imageio.ImageIO
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.roundToInt

actual class LoadedTexture(val sys: VkSystem, val width: Int, val height: Int, val data: Uint8Buffer, val format: TexFormat) : VkResource() {
    val textureImage: Image
    val textureImageView: ImageView
    val sampler: Long

    init {
        val imageSize = width * height * format.channels.toLong()
        val mipLevels = log2(max(width, height).toDouble()).roundToInt() + 1

        val buf = (data as Uint8BufferImpl).buffer

        val stagingAllocUsage = Vma.VMA_MEMORY_USAGE_CPU_ONLY
        val stagingBuffer = Buffer(sys, imageSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
        stagingBuffer.mapped {
            put(buf)
        }

        textureImage = Image(sys, width, height, mipLevels, VK_SAMPLE_COUNT_1_BIT, format.vkFormat, VK_IMAGE_TILING_OPTIMAL,
                VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT, Vma.VMA_MEMORY_USAGE_GPU_ONLY)
        textureImage.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyBufferToImage(stagingBuffer, textureImage, width, height)
        if (mipLevels > 0) {
            generateMipmaps()
        }
        addDependingResource(textureImage)
        stagingBuffer.destroy()

        textureImageView = ImageView(sys, textureImage, VK_IMAGE_ASPECT_COLOR_BIT)
        addDependingResource(textureImageView)

        sampler = createSampler()

        logD { "Texture created: Image: ${textureImage.vkImage}, view: ${textureImageView.vkImageView}, sampler: $sampler" }
    }

    private fun createSampler(): Long {
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
                maxLod(textureImage.mipLevels.toFloat())
            }
            return checkCreatePointer { vkCreateSampler(sys.device.vkDevice, samplerInfo, null, it) }
        }
    }

    private fun generateMipmaps() {
        memStack {
            val formatProperties = VkFormatProperties.mallocStack(this)
            vkGetPhysicalDeviceFormatProperties(sys.physicalDevice.vkPhysicalDevice, textureImage.format, formatProperties)
            check(formatProperties.optimalTilingFeatures() and VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT != 0) {
                "Texture image format does not support linear blitting!"
            }

            sys.commandPool.singleTimeCommands { commandBuffer ->
                val barrier = callocVkImageMemoryBarrierN(1) {
                    sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                    image(textureImage.vkImage)
                    srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    subresourceRange {
                        it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        it.baseArrayLayer(0)
                        it.layerCount(1)
                        it.levelCount(1)
                    }
                }

                var mipWidth = textureImage.width
                var mipHeight = textureImage.height
                for (i in 1 until textureImage.mipLevels) {
                    barrier
                            .subresourceRange { it.baseMipLevel(i - 1) }
                            .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                            .newLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                            .srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
                            .dstAccessMask(VK_ACCESS_TRANSFER_READ_BIT)

                    vkCmdPipelineBarrier(commandBuffer,
                            VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT, 0,
                            null, null, barrier)

                    val blit = callocVkImageBlitN(1) {
                        srcOffsets(0).set(0, 0, 0)
                        srcOffsets(1).set(mipWidth, mipHeight, 1)
                        srcSubresource {
                            it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                            it.mipLevel(i - 1)
                            it.baseArrayLayer(0)
                            it.layerCount(1)
                        }
                        dstOffsets(0).set(0, 0, 0)
                        dstOffsets(1)
                                .x(if (mipWidth > 1) mipWidth / 2 else 1)
                                .y(if (mipHeight > 1) mipHeight / 2 else 1)
                                .z(1)
                        dstSubresource {
                            it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                            it.mipLevel(i)
                            it.baseArrayLayer(0)
                            it.layerCount(1)
                        }
                    }

                    vkCmdBlitImage(commandBuffer,
                            textureImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                            textureImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                            blit, VK_FILTER_LINEAR)

                    barrier
                            .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                            .newLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                            .srcAccessMask(VK_ACCESS_TRANSFER_READ_BIT)
                            .dstAccessMask(VK_ACCESS_SHADER_READ_BIT)

                    vkCmdPipelineBarrier(commandBuffer,
                            VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0,
                            null, null, barrier)

                    if (mipWidth > 1) { mipWidth /= 2 }
                    if (mipHeight > 1) { mipHeight /= 2 }
                }

                barrier.subresourceRange { it.baseMipLevel(textureImage.mipLevels - 1) }
                        .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                        .newLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                        .srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
                        .dstAccessMask(VK_ACCESS_SHADER_READ_BIT)

                vkCmdPipelineBarrier(commandBuffer,
                        VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0,
                        null, null, barrier)
            }
        }
    }

    private fun copyBufferToImage(buffer: Buffer, image: Image, width: Int, height: Int) {
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

    override fun freeResources() {
        vkDestroySampler(sys.device.vkDevice, sampler, null)
        logD { "Destroyed texture" }
    }

    companion object {
        fun fromFile(sys: VkSystem, path: String): LoadedTexture {
            return fromImageTextureData(sys, ImageTextureData().apply { setTexImage(ImageIO.read(FileInputStream(path))) })
        }

        fun fromBufferedTextureData(sys: VkSystem, img: BufferedTextureData): LoadedTexture {
            val buffer = img.data
            buffer.flip()
            val (format, data) = checkFormat(img.format, buffer, img.width, img.height)
            return LoadedTexture(sys, img.width, img.height, data, format)
        }

        fun fromImageTextureData(sys: VkSystem, img: ImageTextureData): LoadedTexture {
            val (format, data) = checkFormat(img.format, img.buffer!!, img.width, img.height)
            return LoadedTexture(sys, img.width, img.height, data, format)
        }

        private fun checkFormat(format: TexFormat, data: Uint8Buffer, width: Int, height: Int): Pair<TexFormat, Uint8Buffer> {
            var fmt = format
            var buf = data

            if (format == TexFormat.RGB) {
                // fixme: check support for 3-channel textures on system init, for now we assume that 3-channel textures are not supported...
                val reshaped = createUint8Buffer(width * height * 4)
                for (i in 0 until width * height) {
                    reshaped[i*4+0] = data[i*3+0]
                    reshaped[i*4+1] = data[i*3+1]
                    reshaped[i*4+2] = data[i*3+2]
                    reshaped[i*4+3] = 255.toByte()
                }
                buf = reshaped
                fmt = TexFormat.RGBA
            }
            return fmt to buf
        }
    }
}

val TexFormat.vkFormat: Int
    get() = when(this) {
        TexFormat.ALPHA -> VK_FORMAT_R8_UNORM
        TexFormat.RGB -> VK_FORMAT_R8G8B8_UNORM
        TexFormat.RGBA -> VK_FORMAT_R8G8B8A8_UNORM
    }

val TexFormat.glFormat: Int
    get() = when(this) {
        TexFormat.ALPHA -> GL_ALPHA
        TexFormat.RGB -> GL_RGB
        TexFormat.RGBA -> GL_RGBA
    }