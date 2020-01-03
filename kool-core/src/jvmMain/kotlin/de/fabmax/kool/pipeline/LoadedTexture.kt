package de.fabmax.kool.pipeline

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.CubeMapTextureData
import de.fabmax.kool.TextureData
import de.fabmax.kool.gl.GL_ALPHA
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.createUint8Buffer
import de.fabmax.kool.util.logD
import org.lwjgl.opengl.GL11.GL_RGB
import org.lwjgl.opengl.GL11.GL_RGBA
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkFormatProperties
import java.io.FileInputStream
import java.nio.ByteBuffer
import javax.imageio.ImageIO
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.roundToInt

actual class LoadedTexture(val sys: VkSystem, val data: TextureData) : VkResource() {
    val width = data.width
    val height = data.height

    val format: TexFormat
    val textureImage: Image
    val textureImageView: ImageView
    val sampler: Long

    init {
        val imgObjs = when(data) {
            is BufferedTextureData -> loadData(data)
            is CubeMapTextureData -> loadCubeData(data)
            else -> TODO()
        }

        format = imgObjs.format
        textureImage = imgObjs.image
        textureImageView = imgObjs.imageView
        sampler = imgObjs.sampler

        logD { "Texture created: Image: ${textureImage.vkImage}, view: ${textureImageView.vkImageView}, sampler: $sampler" }
    }

    private fun loadCubeData(cubeImg: CubeMapTextureData) : VkImgObjects {
        val dstFmt = checkFormat(cubeImg.format)
        val imageSize = width * height * dstFmt.channels.toLong() * 6

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
        textureImage.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyCubeBufferToImage(stagingBuffer, textureImage, width, height)
        textureImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        addDependingResource(textureImage)
        stagingBuffer.destroy()

        val textureImageView = ImageView(sys, textureImage.vkImage, textureImage.format, VK_IMAGE_ASPECT_COLOR_BIT,
                textureImage.mipLevels, VK_IMAGE_VIEW_TYPE_CUBE)
        addDependingResource(textureImageView)

        val sampler = createSampler(textureImage)
        return VkImgObjects(dstFmt, textureImage, textureImageView, sampler)
    }

    private fun loadData(img: BufferedTextureData) : VkImgObjects {
        val dstFmt = checkFormat(img.format)
        val buf = reshape(dstFmt, img)

        val imageSize = width * height * dstFmt.channels.toLong()
        val mipLevels = log2(max(width, height).toDouble()).roundToInt() + 1

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
        textureImage.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
        copyBufferToImage(stagingBuffer, textureImage, width, height)
        if (mipLevels > 1) {
            generateMipmaps(textureImage)
        } else {
            textureImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        }
        addDependingResource(textureImage)
        stagingBuffer.destroy()

        val textureImageView = ImageView(sys, textureImage, VK_IMAGE_ASPECT_COLOR_BIT)
        addDependingResource(textureImageView)

        val sampler = createSampler(textureImage)
        return VkImgObjects(dstFmt, textureImage, textureImageView, sampler)
    }

    private fun createSampler(texImage: Image): Long {
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
            return checkCreatePointer { vkCreateSampler(sys.device.vkDevice, samplerInfo, null, it) }
        }
    }

    private fun generateMipmaps(texImage: Image) {
        memStack {
            val formatProperties = VkFormatProperties.mallocStack(this)
            vkGetPhysicalDeviceFormatProperties(sys.physicalDevice.vkPhysicalDevice, texImage.format, formatProperties)
            check(formatProperties.optimalTilingFeatures() and VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT != 0) {
                "Texture image format does not support linear blitting!"
            }

            sys.commandPool.singleTimeCommands { commandBuffer ->
                val barrier = callocVkImageMemoryBarrierN(1) {
                    sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                    image(texImage.vkImage)
                    srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    subresourceRange {
                        it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        it.baseArrayLayer(0)
                        it.layerCount(1)
                        it.levelCount(1)
                    }
                }

                var mipWidth = texImage.width
                var mipHeight = texImage.height
                for (i in 1 until texImage.mipLevels) {
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
                            texImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                            texImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
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

                barrier.subresourceRange { it.baseMipLevel(texImage.mipLevels - 1) }
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

    private fun copyCubeBufferToImage(buffer: Buffer, image: Image, width: Int, height: Int) {
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
                val reshaped = createUint8Buffer(width * height * 4)
                for (i in 0 until width * height) {
                    reshaped[i*4+0] = img.data[i*3+0]
                    reshaped[i*4+1] = img.data[i*3+1]
                    reshaped[i*4+2] = img.data[i*3+2]
                    reshaped[i*4+3] = 255.toByte()
                }
                return (reshaped as Uint8BufferImpl).buffer
            } else {
                throw IllegalArgumentException("${img.format} -> $dstFormat not implemented")
            }
        }
        return (img.data as Uint8BufferImpl).buffer
    }

    override fun freeResources() {
        vkDestroySampler(sys.device.vkDevice, sampler, null)
        logD { "Destroyed texture" }
    }

    private class VkImgObjects(val format: TexFormat, val image: Image, val imageView: ImageView, val sampler: Long)

    companion object {
        fun fromFile(sys: VkSystem, path: String): LoadedTexture {
            return LoadedTexture(sys, ImageTextureData(ImageIO.read(FileInputStream(path))))
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
