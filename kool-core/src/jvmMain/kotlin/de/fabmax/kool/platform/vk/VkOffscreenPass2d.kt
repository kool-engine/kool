package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.vk.util.vkFormat
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class VkOffscreenPass2d(val parentPass: OffscreenPass2dImpl) : OffscreenPass2dImpl.BackendImpl {
    private var isCreated = false
    private var isCreationBlocked = false

    var renderPass: VkOffscreenRenderPass? = null
        private set

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated && !isCreationBlocked) {
            create(ctx)
        }
    }

    fun copyToTextures(commandBuffer: VkCommandBuffer, ctx: Lwjgl3Context) {
        if (parentPass.offscreenPass.copyTargetsColor.isEmpty()) {
            return
        }
        val mipLevels = parentPass.offscreenPass.config.mipLevels
        val rp = renderPass ?: return

        memStack {
            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
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
                vkCmdCopyImage(commandBuffer, rp.image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, target.textureImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            }
            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        }
    }

    override fun dispose(ctx: Lwjgl3Context) {
        val rp = renderPass
        val colorTexs = parentPass.offscreenPass.colorTextures.map { it.loadedTexture }
        val depthTex = parentPass.offscreenPass.depthTexture?.loadedTexture

        isCreated = false
        renderPass = null

        parentPass.offscreenPass.colorTextures.forEachIndexed { i, tex ->
            if (!parentPass.offscreenPass.config.colorAttachments[i].isProvided) {
                tex.clear()
            }
        }
        if (parentPass.offscreenPass.config.depthAttachment?.isProvided == false) {
            parentPass.offscreenPass.depthTexture?.clear()
        }

        ctx.runDelayed(3) {
            rp?.destroyNow()
            colorTexs.forEachIndexed { i, loadedTex ->
                if (!parentPass.offscreenPass.config.colorAttachments[i].isProvided) {
                    loadedTex?.dispose()
                }
            }
            if (parentPass.offscreenPass.config.depthAttachment?.isProvided == false) {
                depthTex?.dispose()
            }
        }
    }

    private fun Texture.clear() {
        loadedTexture = null
        loadingState = Texture.LoadingState.NOT_LOADED
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)

        isCreationBlocked = true
        ctx.runDelayed(3) {
            isCreationBlocked = false
        }
    }

    private fun create(ctx: Lwjgl3Context) {
        val sys = (ctx.renderBackend as VkRenderBackend).vkSystem
        val cfg = parentPass.offscreenPass.config
        val width = parentPass.offscreenPass.width
        val height = parentPass.offscreenPass.height

        var isProvidedColor = false
        val formats = mutableListOf<TexFormat>()
        if (cfg.colorRenderTarget == OffscreenRenderPass.RenderTarget.RENDER_BUFFER) {
            formats += TexFormat.R
        } else {
            isProvidedColor = cfg.colorAttachments[0].providedTexture != null
            if (cfg.colorAttachments.any { (it.providedTexture != null) != isProvidedColor }) {
                throw IllegalStateException("Mixed provided / created color attachments are not yet supported [OffscreenRenderPass2d: ${parentPass.offscreenPass.name}]")
            }
            cfg.colorAttachments.forEach { formats += it.colorFormat }
        }

        val colorAttachments = if (isProvidedColor) {
            val images = mutableListOf<Image>()
            val imageViews = mutableListOf<ImageView>()
            val samplers = mutableListOf<Long>()
            cfg.colorAttachments.forEach {
                val vkTex = it.providedTexture!!.loadedTexture as LoadedTextureVk
                images += vkTex.textureImage
                imageViews += vkTex.textureImageView
                samplers += vkTex.sampler
            }
            VkOffscreenRenderPass.ProvidedColorAttachments(false, images, imageViews, samplers)
        } else {
            VkOffscreenRenderPass.CreatedColorAttachments(sys, width, height, false, formats.map { it.vkFormat }, VK_FILTER_LINEAR)
        }

        val isProvidedDepth = cfg.depthAttachment?.providedTexture != null
        val depthAttachment = if (cfg.depthRenderTarget == OffscreenRenderPass.RenderTarget.RENDER_BUFFER) {
            VkOffscreenRenderPass.CreatedDepthAttachment(sys, width, height, false, VK_FILTER_NEAREST, VK_COMPARE_OP_NEVER)
        } else {
            if (isProvidedDepth) {
                val vkTex = cfg.depthAttachment!!.providedTexture!!.loadedTexture as LoadedTextureVk
                VkOffscreenRenderPass.ProvidedDepthAttachment(false, vkTex.textureImage, vkTex.textureImageView, vkTex.sampler)
            } else {
                val depth = cfg.depthAttachment!!
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

    private fun createTex(tex: Texture, iAttachment: Int, isColor: Boolean, rp: VkOffscreenRenderPass, sys: VkSystem) {
        tex.apply {
            val vkTex = if (isColor) {
                LoadedTextureVk(sys, rp.getTexFormat(iAttachment), rp.images[iAttachment], rp.imageViews[iAttachment], rp.samplers[iAttachment], true)
            } else {
                LoadedTextureVk(sys, rp.texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
            }
            vkTex.setSize(rp.maxWidth, rp.maxHeight)
            loadedTexture = vkTex
            loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun Texture.createCopyTexColor(ctx: Lwjgl3Context) {
        val vkBackend = ctx.renderBackend as VkRenderBackend
        val prev = loadedTexture
        if (prev != null) {
            ctx.runDelayed(3) {
                prev.dispose()
            }
        }

        val width = parentPass.offscreenPass.width
        val height = parentPass.offscreenPass.height
        val tex = TextureLoader.createTexture(vkBackend.vkSystem, props, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
        vkBackend.vkSystem.device.addDependingResource(tex)
    }
}