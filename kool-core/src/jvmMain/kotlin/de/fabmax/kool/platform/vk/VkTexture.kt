package de.fabmax.kool.platform.vk

import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logD
import org.lwjgl.util.vma.Vma
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_GPU_ONLY
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkFormatProperties
import java.io.FileInputStream
import javax.imageio.ImageIO
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.roundToInt

class VkTexture(val sys: VkSystem, val width: Int, val height: Int, val data: Uint8Buffer) : VkResource() {

    val textureImage: Image
    val textureImageView: ImageView
    val sampler: Long

    constructor(sys: VkSystem, path: String): this(sys, ImageTextureData().apply { setTexImage(ImageIO.read(FileInputStream(path))) })

    constructor(sys: VkSystem, img: ImageTextureData): this(sys, img.width, img.height, img.buffer!!)

    init {
        val imageSize = width * height * 4L
        val texData = ByteArray(imageSize.toInt()) { i -> data[i] }
        val mipLevels = log2(max(width, height).toDouble()).roundToInt() + 1

        val stagingAllocUsage = Vma.VMA_MEMORY_USAGE_CPU_ONLY
        val stagingBuffer = Buffer(sys, imageSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
        stagingBuffer.mapped {
            put(texData)
        }

        textureImage = Image(sys, width, height, mipLevels, VK_SAMPLE_COUNT_1_BIT, VK_FORMAT_R8G8B8A8_UNORM, VK_IMAGE_TILING_OPTIMAL,
            VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT, VMA_MEMORY_USAGE_GPU_ONLY)
        textureImage.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyBufferToImage(stagingBuffer, textureImage, width, height)
        generateMipmaps()
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
}