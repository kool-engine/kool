package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.vk.util.vkFormat
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class VkOffscreenPassCube(val parentPass: OffscreenRenderPassCube) : BaseReleasable(), OffscreenPassCubeImpl {
    private var isCreated = false
    private var isCreationBlocked = false

    var renderPass: VkOffscreenRenderPass? = null
        private set

    lateinit var image: Image
    lateinit var imageView: ImageView
    var sampler: Long = 0L

    fun draw() {
        if (!isCreated && !isCreationBlocked) {
            create()
        }
    }

    fun copyToTextures(commandBuffer: VkCommandBuffer, ctx: Lwjgl3Context) {
//        if (parentPass.copyTargetsColor.isEmpty()) {
//            return
//        }
//        val mipLevels = parentPass.numTextureMipLevels
//
//        memStack {
//            image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
//            for (i in parentPass.copyTargetsColor.indices) {
//                val copyTarget = parentPass.copyTargetsColor[i]
//                val texWidth = copyTarget.loadedTexture?.width ?: 0
//                val texHeight = copyTarget.loadedTexture?.height ?: 0
//                if (texWidth != parentPass.width || texHeight != parentPass.height) {
//                    copyTarget.loadedTexture?.release()
//                    copyTarget.createCopyTexColor(ctx)
//                }
//                val target = copyTarget.loadedTexture as LoadedTextureVk
//
//                val imageCopy = callocVkImageCopyN(mipLevels * 6) {
//                    for (mipLevel in 0 until mipLevels) {
//                        val width = parentPass.width shr mipLevel
//                        val height = parentPass.height shr mipLevel
//
//                        for (face in 0 until 6) {
//                            this[mipLevel * 6 + face].apply {
//                                srcSubresource {
//                                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//                                    it.mipLevel(mipLevel)
//                                    it.baseArrayLayer(face)
//                                    it.layerCount(1)
//                                }
//                                srcOffset { it.set(0, 0, 0) }
//                                dstSubresource {
//                                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//                                    it.mipLevel(mipLevel)
//                                    it.baseArrayLayer(face)
//                                    it.layerCount(1)
//                                }
//                                dstOffset { it.set(0, 0, 0) }
//                                extent { it.set(width, height, 1) }
//                            }
//                        }
//                    }
//                }
//                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
//                vkCmdCopyImage(commandBuffer, image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, target.textureImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
//                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//            }
//            image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//        }
    }

    private fun destroyBuffers() {
        val rp = renderPass
        val colorTexs = parentPass.colorTextures.map { it.gpuTexture }
        val depthTex = parentPass.depthTexture?.gpuTexture

        isCreated = false
        renderPass = null

        parentPass.colorTextures.forEachIndexed { i, tex ->
            if (parentPass.colorAttachments !is OffscreenRenderPass.ColorAttachmentTextures) {
                tex.clear()
            }
        }
        if (parentPass.depthAttachment !is OffscreenRenderPass.DepthAttachmentTexture) {
            parentPass.depthTexture?.clear()
        }

        launchDelayed(3) {
            rp?.destroyNow()
            colorTexs.forEachIndexed { i, loadedTex ->
                if (parentPass.colorAttachments !is OffscreenRenderPass.ColorAttachmentTextures) {
                    loadedTex?.release()
                }
            }
            if (parentPass.depthAttachment !is OffscreenRenderPass.DepthAttachmentTexture) {
                depthTex?.release()
            }
        }
    }

    override fun release() {
        destroyBuffers()
    }

    private fun TextureCube.clear() {
        gpuTexture = null
        loadingState = Texture.LoadingState.NOT_LOADED
    }

    override fun applySize(width: Int, height: Int) {
        destroyBuffers()

        isCreationBlocked = true
        launchDelayed(3) {
            isCreationBlocked = false
        }
    }

    fun transitionTexLayout(commandBuffer: VkCommandBuffer, dstLayout: Int) {
        memStack {
            image.transitionLayout(this, commandBuffer, dstLayout)
        }
    }

    fun generateMipmaps(commandBuffer: VkCommandBuffer, dstLayout: Int) {
        if (parentPass.mipMode == RenderPass.MipMode.Generate) {
            memStack {
                image.generateMipmaps(this, commandBuffer, dstLayout)
            }
        }
    }

    fun copyView(commandBuffer: VkCommandBuffer, viewDir: OffscreenRenderPassCube.ViewDirection, mipLevel: Int) {
        val rp = renderPass ?: return

        val width = parentPass.width shr mipLevel
        val height = parentPass.height shr mipLevel

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
            vkCmdCopyImage(commandBuffer, rp.image.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, image.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            parentPass.colorTexture!!.loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun create() {
        val sys: VkSystem = TODO()// = (KoolSystem.requireContext().backend as VkRenderBackend).vkSystem
        val pass = parentPass
        val cfg = (pass.colorAttachments as OffscreenRenderPass.ColorAttachmentTextures).attachments[0]
        val rp = VkOffscreenRenderPass(sys, pass.size.x, pass.size.y, true, cfg.textureFormat.vkFormat)
        createTex(rp, sys)
        renderPass = rp
        isCreated = true
    }

    private fun createTex(rp: VkOffscreenRenderPass, sys: VkSystem) {
        TODO()
//        val imgConfig = Image.Config()
//        imgConfig.width = rp.maxWidth
//        imgConfig.height = rp.maxHeight
//        imgConfig.mipLevels = parentPass.numTextureMipLevels
//        imgConfig.numSamples = VK_SAMPLE_COUNT_1_BIT
//        imgConfig.format = rp.colorFormats[0]
//        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
//        imgConfig.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT
//        imgConfig.allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
//        imgConfig.arrayLayers = 6
//        imgConfig.flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT
//
//        image = Image(sys, imgConfig)
//        imageView = ImageView(sys, image.vkImage, image.format, VK_IMAGE_ASPECT_COLOR_BIT, image.mipLevels, VK_IMAGE_VIEW_TYPE_CUBE, 6)
//        sampler = createSampler(sys, image)
//
//        image.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//
//        val loadedTex = LoadedTextureVk(sys, rp.texFormat, image, imageView, sampler)
//        loadedTex.setSize(rp.maxWidth, rp.maxHeight, 1)
//        rp.addDependingResource(loadedTex)
//
//        parentPass.colorTexture!!.apply {
//            gpuTexture = loadedTex
//            loadingState = Texture.LoadingState.LOADING
//        }
    }

    private fun TextureCube.createCopyTexColor(ctx: Lwjgl3Context) {
//        val vkBackend = ctx.backend as VkRenderBackend
//        val prev = gpuTexture
//        if (prev != null) {
//            launchDelayed(3) {
//                prev.release()
//            }
//        }
//
//        val width = parentPass.width
//        val height = parentPass.height
//        val tex = TextureLoader.createCubeTexture(vkBackend.vkSystem, props, width, height)
//        gpuTexture = tex
//        loadingState = Texture.LoadingState.LOADED
//        vkBackend.vkSystem.device.addDependingResource(tex)
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
            check(vkCreateSampler(sys.logicalDevice.vkDevice, samplerInfo, null, ptr) == VK_SUCCESS)
            return ptr[0]
        }
    }

    companion object {
        private val VIEW_TO_CUBE_LAYER_MAP = IntArray(6) { i ->
            when (i) {
                OffscreenRenderPassCube.ViewDirection.POS_X.index -> 0
                OffscreenRenderPassCube.ViewDirection.NEG_X.index -> 1
                OffscreenRenderPassCube.ViewDirection.POS_Y.index -> 2
                OffscreenRenderPassCube.ViewDirection.NEG_Y.index -> 3
                OffscreenRenderPassCube.ViewDirection.POS_Z.index -> 4
                OffscreenRenderPassCube.ViewDirection.NEG_Z.index -> 5
                else -> 0
            }
        }
    }
}
