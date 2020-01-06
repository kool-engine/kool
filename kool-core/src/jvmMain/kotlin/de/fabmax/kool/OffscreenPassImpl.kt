package de.fabmax.kool

import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.util.OffscreenRenderPass
import de.fabmax.kool.platform.vk.util.vkFormat
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

actual class OffscreenPassImpl actual constructor(texWidth: Int, texHeight: Int, isCube: Boolean) : OffscreenPass(texWidth, texHeight, isCube) {
    actual val texture: Texture
    actual val textureCube: CubeMapTexture
        get() = texture as CubeMapTexture

    var renderPass: OffscreenRenderPass? = null
        private set

    init {
        texture = if (isCube) {
            OffscreenTextureCube()
        } else {
            OffscreenTexture2d()
        }
    }

    fun getRenderPass(sys: VkSystem): OffscreenRenderPass {
        if (renderPass == null) {
            createRenderPass(sys)
        }
        return renderPass!!
    }

    fun copyView(commandBuffer: VkCommandBuffer, viewDir: ViewDirection) {
        val rp = renderPass ?: return

        memStack {
            texture as OffscreenTextureCube

            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)

            val layer = VIEW_TO_CUBE_LAYER_MAP[viewDir.index]
            texture.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, layer)
            val imageCopy = callocVkImageCopyN(1) {
                srcSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.baseArrayLayer(0)
                    it.mipLevel(0)
                    it.layerCount(1)
                }
                srcOffset { it.set(0, 0, 0) }
                dstSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.baseArrayLayer(layer)
                    it.mipLevel(0)
                    it.layerCount(1)
                }
                dstOffset { it.set(0, 0, 0) }
                extent {
                    it.width(rp.fbWidth)
                    it.height(rp.fbHeight)
                    it.depth(1)
                }
            }
            vkCmdCopyImage(commandBuffer, rp.image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, texture.image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)

            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            texture.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, layer)
            texture.loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun createRenderPass(sys: VkSystem) {
        val rp = OffscreenRenderPass(sys, texWidth, texHeight, isCube)
        if (isCube) {
            (texture as OffscreenTextureCube).create(sys, rp)
        } else {
            (texture as OffscreenTexture2d).create(sys, rp)
        }
        renderPass = rp
    }

    companion object {
        private val VIEW_TO_CUBE_LAYER_MAP = IntArray(6) { i ->
            when (i) {
                ViewDirection.RIGHT.index -> 0
                ViewDirection.LEFT.index -> 1
                ViewDirection.UP.index -> 2
                ViewDirection.DOWN.index -> 3
                ViewDirection.FRONT.index -> 4
                ViewDirection.BACK.index -> 5
                else -> 0
            }
        }
    }

    private inner class OffscreenTexture2d: Texture(loader = null) {
        fun create(sys: VkSystem, rp: OffscreenRenderPass) {
            loadedTexture = LoadedTexture(sys, rp.texFormat, rp.image, rp.imageView, rp.sampler, true)
            loadingState = LoadingState.LOADED
        }
    }

    private inner class OffscreenTextureCube: CubeMapTexture(loader = null) {
        lateinit var image: Image
        lateinit var imageView: ImageView
        var sampler: Long = 0L

        fun create(sys: VkSystem, rp: OffscreenRenderPass) {
            val imgConfig = Image.Config()
            imgConfig.width = rp.fbWidth
            imgConfig.height = rp.fbHeight
            imgConfig.mipLevels = 1
            imgConfig.numSamples = VK_SAMPLE_COUNT_1_BIT
            imgConfig.format = rp.texFormat.vkFormat
            imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
            imgConfig.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT
            imgConfig.allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
            imgConfig.arrayLayers = 6
            imgConfig.flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT

            image = Image(sys, imgConfig)
            imageView = ImageView(sys, image.vkImage, image.format, VK_IMAGE_ASPECT_COLOR_BIT, image.mipLevels, VK_IMAGE_VIEW_TYPE_CUBE)
            sampler = createSampler(sys, image)

            val loadedTex = LoadedTexture(sys, rp.texFormat, image, imageView, sampler)
            rp.addDependingResource(loadedTex)

            loadedTexture = loadedTex
            loadingState = LoadingState.LOADING
        }
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
}