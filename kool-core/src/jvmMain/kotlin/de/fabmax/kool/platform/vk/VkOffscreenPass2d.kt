package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.vk.util.vkFormat
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class VkOffscreenPass2d(val parentPass: OffscreenPass2dImpl) : OffscreenPass2dImpl.BackendImpl {
    private var isCreated = false
    private var isCreationBlocked = false

    val drawMipLevels = parentPass.offscreenPass.drawMipLevels
    val renderMipLevels: Int = if (drawMipLevels) { parentPass.offscreenPass.mipLevels } else { 1 }

    private val resultImages = Array<Image?>(parentPass.offscreenPass.colorTextures.size) { null }
    private val isCopyResult = parentPass.offscreenPass.mipLevels > 1

    var renderPass: VkOffscreenRenderPass? = null
        private set

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated && !isCreationBlocked) {
            create(ctx)
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
        if (parentPass.offscreenPass.mipLevels == 1 || drawMipLevels) {
            return
        }

        memStack {
            for (i in resultImages.indices) {
                resultImages[i]?.generateMipmaps(this, commandBuffer)
            }
        }
    }

    fun copyMipView(commandBuffer: VkCommandBuffer, mipLevel: Int) {
        if (!isCopyResult) {
            return
        }

        val rp = renderPass ?: return
        val width = parentPass.offscreenPass.getMipWidth(mipLevel)
        val height = parentPass.offscreenPass.getMipHeight(mipLevel)

        memStack {
            for (i in rp.images.indices) {
                val srcImage = rp.images[i]
                srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
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
                vkCmdCopyImage(commandBuffer, srcImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, resultImages[i]!!.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
                srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            }
        }
    }

    fun copyToTextures(commandBuffer: VkCommandBuffer, ctx: Lwjgl3Context) {
        if (parentPass.offscreenPass.copyTargetsColor.isEmpty()) {
            return
        }
        val mipLevels = parentPass.offscreenPass.mipLevels
        val srcImage = resultImages[0] ?: return

        memStack {
            srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
            for (i in parentPass.offscreenPass.copyTargetsColor.indices) {
                val copyTarget = parentPass.offscreenPass.copyTargetsColor[i]
                val texWidth = copyTarget.loadedTexture?.width ?: 0
                val texHeight = copyTarget.loadedTexture?.height ?: 0
                if (texWidth != parentPass.offscreenPass.width || texHeight != parentPass.offscreenPass.height) {
                    copyTarget.loadedTexture?.dispose()
                    copyTarget.createCopyTexColor(ctx)
                }
                val target = copyTarget.loadedTexture as LoadedTextureVk

                val imageCopy = callocVkImageCopyN(mipLevels) {
                    for (mipLevel in 0 until mipLevels) {
                        val width = parentPass.offscreenPass.getMipWidth(mipLevel)
                        val height = parentPass.offscreenPass.getMipHeight(mipLevel)

                        this[mipLevel].apply {
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
                    }
                }
                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                vkCmdCopyImage(commandBuffer, srcImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, target.textureImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            }
            srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        }
    }

    override fun dispose(ctx: Lwjgl3Context) {
        val rp = renderPass
        val colorTexs = parentPass.offscreenPass.colorTextures.map { it.loadedTexture }
        val depthTex = parentPass.offscreenPass.depthTexture?.loadedTexture

        isCreated = false
        renderPass = null

        parentPass.offscreenPass.colorTextures.forEachIndexed { i, tex ->
            if (!parentPass.offscreenPass.colorAttachments[i].isProvided) {
                tex.clear()
            }
        }
        if (parentPass.offscreenPass.depthAttachment?.isProvided == false) {
            parentPass.offscreenPass.depthTexture?.clear()
        }

        launchDelayed(3) {
            rp?.destroyNow()

            colorTexs.forEachIndexed { i, loadedTex ->
                if (!parentPass.offscreenPass.colorAttachments[i].isProvided) {
                    loadedTex?.dispose()
                }
            }
            if (parentPass.offscreenPass.depthAttachment?.isProvided == false) {
                depthTex?.dispose()
            }
        }
    }

    private fun Texture2d.clear() {
        loadedTexture = null
        loadingState = Texture.LoadingState.NOT_LOADED
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)

        isCreationBlocked = true
        launchDelayed(3) {
            isCreationBlocked = false
        }
    }

    private fun create(ctx: Lwjgl3Context) {
        val sys = (ctx.renderBackend as VkRenderBackend).vkSystem
        val pass = parentPass.offscreenPass
        val width = parentPass.offscreenPass.width
        val height = parentPass.offscreenPass.height

        var isProvidedColor = false
        val formats = mutableListOf<TexFormat>()
        if (pass.colorRenderTarget == OffscreenRenderPass.RenderTarget.RENDER_BUFFER) {
            formats += TexFormat.R
        } else {
            isProvidedColor = pass.colorAttachments[0].providedTexture != null
            if (pass.colorAttachments.any { (it.providedTexture != null) != isProvidedColor }) {
                throw IllegalStateException("Mixed provided / created color attachments are not yet supported [OffscreenRenderPass2d: ${parentPass.offscreenPass.name}]")
            }
            pass.colorAttachments.forEach { formats += it.colorFormat }
        }

        val colorAttachments = if (isProvidedColor) {
            val images = mutableListOf<Image>()
            val imageViews = mutableListOf<ImageView>()
            val samplers = mutableListOf<Long>()
            pass.colorAttachments.forEach {
                val vkTex = it.providedTexture!!.loadedTexture as LoadedTextureVk
                images += vkTex.textureImage
                imageViews += vkTex.textureImageView
                samplers += vkTex.sampler
            }
            VkOffscreenRenderPass.ProvidedColorAttachments(false, images, imageViews, samplers)
        } else {
            VkOffscreenRenderPass.CreatedColorAttachments(sys, width, height, false, formats.map { it.vkFormat }, VK_FILTER_LINEAR)
        }

        val isProvidedDepth = pass.depthAttachment?.providedTexture != null
        val depthAttachment = if (pass.depthRenderTarget == OffscreenRenderPass.RenderTarget.RENDER_BUFFER) {
            VkOffscreenRenderPass.CreatedDepthAttachment(sys, width, height, false, VK_FILTER_NEAREST, VK_COMPARE_OP_NEVER)
        } else {
            if (isProvidedDepth) {
                val vkTex = pass.depthAttachment!!.providedTexture!!.loadedTexture as LoadedTextureVk
                VkOffscreenRenderPass.ProvidedDepthAttachment(false, vkTex.textureImage, vkTex.textureImageView, vkTex.sampler)
            } else {
                val depth = pass.depthAttachment!!
                val filterMethod = if (depth.minFilter == FilterMethod.LINEAR) VK_FILTER_LINEAR else VK_FILTER_NEAREST
                val depthCompareOp = if (depth.depthCompareOp != DepthCompareOp.DISABLED) VK_COMPARE_OP_LESS else VK_COMPARE_OP_NEVER
                VkOffscreenRenderPass.CreatedDepthAttachment(sys, width, height, false, filterMethod, depthCompareOp)
            }
        }

        val rp = VkOffscreenRenderPass(sys, width, height, colorAttachments, isProvidedColor, depthAttachment, isProvidedDepth)
        if (!isProvidedColor) {
            for (i in parentPass.offscreenPass.colorTextures.indices) {
                createTex(parentPass.offscreenPass.colorTextures[i], i, true, rp, sys)
            }
        }
        if (!isProvidedDepth) {
            parentPass.offscreenPass.depthTexture?.let {
                createTex(it, 0, false, rp, sys)
            }
        }
        renderPass = rp
        isCreated = true
    }

    private fun createTex(tex: Texture2d, iAttachment: Int, isColor: Boolean, rp: VkOffscreenRenderPass, sys: VkSystem) {
        tex.apply {
            if (isCopyResult) {
                val vkTex = if (isColor) {
                    val props = parentPass.offscreenPass.getColorTexProps(iAttachment)
                    val cpTex = TextureLoader.createTexture(sys, props, rp.maxWidth, rp.maxHeight, 1)
                    cpTex.textureImage.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                    resultImages[iAttachment] = cpTex.textureImage
                    cpTex
                } else {
                    LoadedTextureVk(sys, rp.texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
                }
                vkTex.setSize(rp.maxWidth, rp.maxHeight, 1)
                loadedTexture = vkTex
                loadingState = Texture.LoadingState.LOADED

            } else {
                val vkTex = if (isColor) {
                    val rpTex = LoadedTextureVk(sys, rp.getTexFormat(iAttachment), rp.images[iAttachment], rp.imageViews[iAttachment], rp.samplers[iAttachment], true)
                    resultImages[iAttachment] = rpTex.textureImage
                    rpTex
                } else {
                    LoadedTextureVk(sys, rp.texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
                }
                vkTex.setSize(rp.maxWidth, rp.maxHeight, 1)
                loadedTexture = vkTex
                loadingState = Texture.LoadingState.LOADED
            }
        }
    }

    private fun Texture2d.createCopyTexColor(ctx: Lwjgl3Context) {
        val vkBackend = ctx.renderBackend as VkRenderBackend
        val prev = loadedTexture
        if (prev != null) {
            launchDelayed(3) {
                prev.dispose()
            }
        }

        val width = parentPass.offscreenPass.width
        val height = parentPass.offscreenPass.height
        val tex = TextureLoader.createTexture(vkBackend.vkSystem, props, width, height, 1)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
        vkBackend.vkSystem.device.addDependingResource(tex)
    }
}