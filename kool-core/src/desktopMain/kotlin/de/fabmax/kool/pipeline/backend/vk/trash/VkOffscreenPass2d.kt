package de.fabmax.kool.pipeline.backend.vk.trash

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.vk.Image
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.pipeline.backend.vk.callocVkImageCopyN
import de.fabmax.kool.pipeline.backend.vk.vk
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class VkOffscreenPass2d(val parentPass: OffscreenRenderPass2d) : BaseReleasable(), OffscreenPass2dImpl {
    private var isCreated = false
    private var isCreationBlocked = false

    private val resultImages = Array<Image?>(parentPass.colorTextures.size) { null }
    private val isCopyResult = parentPass.numTextureMipLevels > 1

    var renderPass: OffscreenRenderPassVk? = null
        private set

    fun draw() {
        if (!isCreated && !isCreationBlocked) {
            create()
        }
    }

    fun transitionTexLayout(commandBuffer: VkCommandBuffer, dstLayout: Int) {
        if (!isCopyResult) {
            return
        }

        memStack {
            for (i in resultImages.indices) {
                resultImages[i]?.transitionLayout(this, commandBuffer, dstLayout)
            }
        }
    }

    fun generateMipLevels(commandBuffer: VkCommandBuffer) {
        if (parentPass.mipMode != RenderPass.MipMode.Generate) {
            return
        }

        memStack {
            for (i in resultImages.indices) {
//                resultImages[i]?.generateMipmaps(this, commandBuffer)
            }
        }
    }

    fun blitFrom(src: VkOffscreenPass2d, commandBuffer: VkCommandBuffer, mipLevel: Int) {
        logE { "Blitting render passes is not yet implemented on Vulkan backend" }

        val rp = src.renderPass ?: return
        val srcWidth = src.parentPass.width shr mipLevel
        val srcHeight = src.parentPass.height shr mipLevel
        val width = parentPass.width shr mipLevel
        val height = parentPass.height shr mipLevel

        if (srcWidth != width || srcHeight != height) {
            logE { "Render pass blitting requires source and destination pass to have the same size" }
        }

        // fixme: image copy into a swap chain image does not work
        memStack {
            for (i in rp.images.indices) {
                val srcImage = rp.images[i]
//                srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                val imageCopy = callocVkImageCopyN(1) {
                    srcSubresource {
                        it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        it.mipLevel(mipLevel)
                        it.baseArrayLayer(0)
                        it.layerCount(1)
                    }
                    srcOffset { it.set(0, 0, 0) }
                    dstSubresource {
                        it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        it.mipLevel(mipLevel)
                        it.baseArrayLayer(0)
                        it.layerCount(1)
                    }
                    dstOffset { it.set(0, 0, 0) }
                    extent { it.set(width, height, 1) }
                }
                vkCmdCopyImage(commandBuffer, srcImage.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, resultImages[i]!!.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
//                srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            }
        }
    }

    fun copyMipView(commandBuffer: VkCommandBuffer, mipLevel: Int) {
        if (!isCopyResult) {
            return
        }

        val rp = renderPass ?: return
        val width = parentPass.width shr mipLevel
        val height = parentPass.height shr mipLevel

        memStack {
            for (i in rp.images.indices) {
                val srcImage = rp.images[i]
//                srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
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
                        it.baseArrayLayer(0)
                        it.layerCount(1)
                    }
                    dstOffset { it.set(0, 0, 0) }
                    extent { it.set(width, height, 1) }
                }
                vkCmdCopyImage(commandBuffer, srcImage.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, resultImages[i]!!.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
//                srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            }
        }
    }

    fun copyToTextures(commandBuffer: VkCommandBuffer, ctx: Lwjgl3Context) {
//        if (parentPass.copyTargetsColor.isEmpty()) {
//            return
//        }
//        val mipLevels = parentPass.numTextureMipLevels
//        val srcImage = resultImages[0] ?: return
//
//        memStack {
//            srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
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
//                val imageCopy = callocVkImageCopyN(mipLevels) {
//                    for (mipLevel in 0 until mipLevels) {
//                        val width = parentPass.width shr mipLevel
//                        val height = parentPass.height shr mipLevel
//
//                        this[mipLevel].apply {
//                            srcSubresource {
//                                it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//                                it.mipLevel(mipLevel)
//                                it.baseArrayLayer(0)
//                                it.layerCount(1)
//                            }
//                            srcOffset { it.set(0, 0, 0) }
//                            dstSubresource {
//                                it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//                                it.mipLevel(mipLevel)
//                                it.baseArrayLayer(0)
//                                it.layerCount(1)
//                            }
//                            dstOffset { it.set(0, 0, 0) }
//                            extent { it.set(width, height, 1) }
//                        }
//                    }
//                }
//                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
//                vkCmdCopyImage(commandBuffer, srcImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, target.textureImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
//                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//            }
//            srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
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

    private fun Texture2d.clear() {
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

    private fun create() {
        val backend: RenderBackendVk = TODO()// = (KoolSystem.requireContext().backend as VkRenderBackend).vkSystem
        val pass = parentPass
        val width = parentPass.width
        val height = parentPass.height

        var isMultiSampling = false
        var isProvidedColor = false
        val formats = mutableListOf<TexFormat>()
        if (pass.colorAttachments is OffscreenRenderPass.ColorAttachmentNone) {
            isMultiSampling = false
            formats += TexFormat.R
        } else if (pass.colorAttachments is OffscreenRenderPass.ColorAttachmentTextures) {
            val cfgs = pass.colorAttachments.attachments
            cfgs.forEach { formats += it.textureFormat }
        }

        val colorAttachments = OffscreenRenderPassVk.CreatedColorAttachments(backend, width, height, false, formats.map { it.vk }, VK_FILTER_LINEAR, isMultiSampling)

        var isProvidedDepth = false
        val depthAttachment = if (pass.depthAttachment !is OffscreenRenderPass.DepthAttachmentTexture) {
            OffscreenRenderPassVk.CreatedDepthAttachment(backend, width, height, false, VK_FILTER_NEAREST, VK_COMPARE_OP_NEVER, isMultiSampling)
        } else {
            val texDepth = pass.depthAttachment
            val depth = texDepth.attachment
            val filterMethod = if (depth.defaultSamplerSettings.minFilter == FilterMethod.LINEAR) VK_FILTER_LINEAR else VK_FILTER_NEAREST
            //val depthCompareOp = if (depth.depthCompareOp != DepthCompareOp.DISABLED) VK_COMPARE_OP_LESS else VK_COMPARE_OP_NEVER
            val depthCompareOp = VK_COMPARE_OP_LESS
            OffscreenRenderPassVk.CreatedDepthAttachment(backend, width, height, false, filterMethod, depthCompareOp, isMultiSampling)
        }

        val rp = OffscreenRenderPassVk(
            backend,
            width,
            height,
            colorAttachments,
            isProvidedColor,
            depthAttachment,
            isProvidedDepth,
            isMultiSampling
        )
        if (!isProvidedColor) {
            for (i in parentPass.colorTextures.indices) {
                createTex(parentPass.colorTextures[i], i, true, rp, backend)
            }
        }
        if (!isProvidedDepth) {
            parentPass.depthTexture?.let {
                createTex(it, 0, false, rp, backend)
            }
        }
        renderPass = rp
        isCreated = true
    }

    private fun createTex(tex: Texture2d, iAttachment: Int, isColor: Boolean, rp: OffscreenRenderPassVk, backend: RenderBackendVk) {
        tex.apply {
            val texFormat = TexFormat.R//rp.getTexFormat(0)
            if (isCopyResult) {
                val vkTex = if (isColor) {
                    val props = parentPass.createColorTextureProps(iAttachment)
                    val cpTex = TextureLoaderOld.createTexture(backend, props, rp.maxWidth, rp.maxHeight, 1)
//                    cpTex.textureImage.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                    resultImages[iAttachment] = cpTex.textureImage
                    cpTex
                } else {
                    LoadedTextureVkOld(backend, texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
                }
                vkTex.setSize(rp.maxWidth, rp.maxHeight, 1)
                gpuTexture = vkTex
                loadingState = Texture.LoadingState.LOADED

            } else {
                val vkTex = if (isColor) {
                    val rpTex = LoadedTextureVkOld(
                        backend,
                        TexFormat.R,//rp.getTexFormat(iAttachment),
                        rp.images[iAttachment],
                        rp.imageViews[iAttachment],
                        rp.samplers[iAttachment],
                        true
                    )
                    resultImages[iAttachment] = rpTex.textureImage
                    rpTex
                } else {
                    LoadedTextureVkOld(backend, texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
                }
                vkTex.setSize(rp.maxWidth, rp.maxHeight, 1)
                gpuTexture = vkTex
                loadingState = Texture.LoadingState.LOADED
            }
        }
    }

    private fun Texture2d.createCopyTexColor(ctx: Lwjgl3Context) {
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
//        val tex = TextureLoader.createTexture(vkBackend.vkSystem, props, width, height, 1)
//        gpuTexture = tex
//        loadingState = Texture.LoadingState.LOADED
//        vkBackend.vkSystem.device.addDependingResource(tex)
    }
}