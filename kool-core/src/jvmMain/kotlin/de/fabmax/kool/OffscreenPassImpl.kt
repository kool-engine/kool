package de.fabmax.kool

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3ContextVk
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.util.OffscreenRenderPass
import de.fabmax.kool.platform.vk.util.vkFormat
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import kotlin.math.pow
import kotlin.math.roundToInt

actual class OffscreenPass2dImpl actual constructor(val offscreenPass: OffscreenPass2d) {
    actual val texture: Texture = OffscreenTexture2d(true)
    actual val depthTexture: Texture = OffscreenTexture2d(false)

    var renderPass: OffscreenRenderPass? = null
        private set

    init {
        if (offscreenPass.mipLevels > 1) {
            TODO("not yet implemented, requires copying framebuffer color attachment")
        }
    }

    actual fun dispose(ctx: KoolContext) {
        ctx as Lwjgl3ContextVk
        ctx.vkSystem.renderLoop.runDelayed(3) {
            renderPass?.destroyNow()
            texture.dispose()
        }
    }

    fun getRenderPass(sys: VkSystem): OffscreenRenderPass {
        if (renderPass == null) {
            createRenderPass(sys)
        }
        return renderPass!!
    }

    private fun createRenderPass(sys: VkSystem) {
        val rp = OffscreenRenderPass(sys, offscreenPass.texWidth, offscreenPass.texHeight, false, offscreenPass.colorFormat.vkFormat)
        (texture as OffscreenTexture2d).create(sys, rp)
        (depthTexture as OffscreenTexture2d).create(sys, rp)
        renderPass = rp
    }

    private inner class OffscreenTexture2d(val isColor: Boolean): Texture(loader = null) {
        fun create(sys: VkSystem, rp: OffscreenRenderPass) {
            loadedTexture = if (isColor) {
                LoadedTexture(sys, rp.texFormat, rp.image, rp.imageView, rp.sampler, true)
            } else {
                LoadedTexture(sys, rp.texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
            }
            loadingState = LoadingState.LOADED
        }
    }
}


actual class OffscreenPassCubeImpl actual constructor(val offscreenPass: OffscreenPassCube) {
    actual val texture: CubeMapTexture = OffscreenTextureCube()

    var renderPass: OffscreenRenderPass? = null
        private set

    actual fun dispose(ctx: KoolContext) {
        ctx as Lwjgl3ContextVk
        ctx.vkSystem.renderLoop.runDelayed(3) {
            renderPass?.destroyNow()
            texture.dispose()
        }
    }

    fun transitionTexLayout(commandBuffer: VkCommandBuffer, dstLayout: Int) {
        texture as OffscreenTextureCube
        memStack {
            texture.image.transitionLayout(this, commandBuffer, dstLayout)
        }
    }

    fun generateMipmaps(commandBuffer: VkCommandBuffer, dstLayout: Int) {
        if (offscreenPass.mipLevels > 1) {
            memStack {
                (texture as OffscreenTextureCube).image.generateMipmaps(this, commandBuffer, dstLayout)
            }
        }
    }

    fun copyView(commandBuffer: VkCommandBuffer, viewDir: OffscreenPassCube.ViewDirection) {
        val rp = renderPass ?: return

        var mipLevel = 0
        var width = offscreenPass.texWidth
        var height = offscreenPass.texHeight
        if (offscreenPass.targetMipLevel > 0) {
            width = (width * 0.5.pow(offscreenPass.targetMipLevel)).roundToInt()
            height = (height * 0.5.pow(offscreenPass.targetMipLevel)).roundToInt()
            mipLevel = offscreenPass.targetMipLevel
        }

        texture as OffscreenTextureCube

        memStack {
            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)

            val layer = VIEW_TO_CUBE_LAYER_MAP[viewDir.index]
            val imageCopy = callocVkImageCopyN(1) {
                srcSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.mipLevel(0)
                    it.baseArrayLayer(0)
                    it.layerCount(1)
                }
                srcOffset { it.set(0, 0, 0) }
                dstSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.mipLevel(mipLevel)
                    it.baseArrayLayer(layer)
                    it.layerCount(1)
                }
                dstOffset { it.set(0, 0, 0) }
                extent { it.set(width, height, 1) }
            }
            vkCmdCopyImage(commandBuffer, rp.image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, texture.image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            texture.loadingState = Texture.LoadingState.LOADED
        }
    }

    fun getRenderPass(sys: VkSystem): OffscreenRenderPass {
        if (renderPass == null) {
            createRenderPass(sys)
        }
        return renderPass!!
    }

    private fun createRenderPass(sys: VkSystem) {
        val rp = OffscreenRenderPass(sys, offscreenPass.texWidth, offscreenPass.texHeight, true, offscreenPass.colorFormat.vkFormat)
        (texture as OffscreenTextureCube).create(sys, rp)
        renderPass = rp
    }

    companion object {
        private val VIEW_TO_CUBE_LAYER_MAP = IntArray(6) { i ->
            when (i) {
                OffscreenPassCube.ViewDirection.RIGHT.index -> 0
                OffscreenPassCube.ViewDirection.LEFT.index -> 1
                OffscreenPassCube.ViewDirection.UP.index -> 2
                OffscreenPassCube.ViewDirection.DOWN.index -> 3
                OffscreenPassCube.ViewDirection.FRONT.index -> 4
                OffscreenPassCube.ViewDirection.BACK.index -> 5
                else -> 0
            }
        }
    }

    private inner class OffscreenTextureCube: CubeMapTexture(
            TextureProps(addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE, addressModeW = AddressMode.CLAMP_TO_EDGE),
            loader = null) {

        lateinit var image: Image
        lateinit var imageView: ImageView
        var sampler: Long = 0L

        fun create(sys: VkSystem, rp: OffscreenRenderPass) {
            val imgConfig = Image.Config()
            imgConfig.width = rp.maxWidth
            imgConfig.height = rp.maxHeight
            imgConfig.mipLevels = offscreenPass.mipLevels
            imgConfig.numSamples = VK_SAMPLE_COUNT_1_BIT
            imgConfig.format = rp.colorFormat
            imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
            imgConfig.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT
            imgConfig.allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
            imgConfig.arrayLayers = 6
            imgConfig.flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT

            image = Image(sys, imgConfig)
            imageView = ImageView(sys, image.vkImage, image.format, VK_IMAGE_ASPECT_COLOR_BIT, image.mipLevels, VK_IMAGE_VIEW_TYPE_CUBE)
            sampler = createSampler(sys, image)

            image.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)

            val loadedTex = LoadedTexture(sys, rp.texFormat, image, imageView, sampler)
            rp.addDependingResource(loadedTex)

            loadedTexture = loadedTex
            loadingState = LoadingState.LOADING
        }

        private fun createSampler(sys: VkSystem, texImage: Image): Long {
            memStack {
                val samplerInfo = callocVkSamplerCreateInfo {
                    sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                    magFilter(VK_FILTER_LINEAR)
                    minFilter(VK_FILTER_LINEAR)
                    addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    anisotropyEnable(false)
                    maxAnisotropy(1f)
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
}