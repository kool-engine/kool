package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.vk.util.vkFormat
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class VkOffscreenPass2d(val parentPass: OffscreenRenderPass2d) : OffscreenPass2dImpl {
    private var isCreated = false
    private var isCreationBlocked = false

    val drawMipLevels = parentPass.drawMipLevels
    val renderMipLevels: Int = if (drawMipLevels) { parentPass.mipLevels } else { 1 }

    private val resultImages = Array<Image?>(parentPass.colorTextures.size) { null }
    private val isCopyResult = parentPass.mipLevels > 1

    var renderPass: VkOffscreenRenderPass? = null
        private set

    override fun draw(ctx: KoolContext) {
        if (!isCreated && !isCreationBlocked) {
            create(ctx as Lwjgl3Context)
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
        if (parentPass.mipLevels == 1 || drawMipLevels) {
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
        val width = parentPass.getMipWidth(mipLevel)
        val height = parentPass.getMipHeight(mipLevel)

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
        if (parentPass.copyTargetsColor.isEmpty()) {
            return
        }
        val mipLevels = parentPass.mipLevels
        val srcImage = resultImages[0] ?: return

        memStack {
            srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
            for (i in parentPass.copyTargetsColor.indices) {
                val copyTarget = parentPass.copyTargetsColor[i]
                val texWidth = copyTarget.loadedTexture?.width ?: 0
                val texHeight = copyTarget.loadedTexture?.height ?: 0
                if (texWidth != parentPass.width || texHeight != parentPass.height) {
                    copyTarget.loadedTexture?.dispose()
                    copyTarget.createCopyTexColor(ctx)
                }
                val target = copyTarget.loadedTexture as LoadedTextureVk

                val imageCopy = callocVkImageCopyN(mipLevels) {
                    for (mipLevel in 0 until mipLevels) {
                        val width = parentPass.getMipWidth(mipLevel)
                        val height = parentPass.getMipHeight(mipLevel)

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

    override fun dispose(ctx: KoolContext) {
        val rp = renderPass
        val colorTexs = parentPass.colorTextures.map { it.loadedTexture }
        val depthTex = parentPass.depthTexture?.loadedTexture

        isCreated = false
        renderPass = null

        parentPass.colorTextures.forEachIndexed { i, tex ->
            if (!parentPass.colorAttachments[i].isProvided) {
                tex.clear()
            }
        }
        if (parentPass.depthAttachment?.isProvided == false) {
            parentPass.depthTexture?.clear()
        }

        launchDelayed(3) {
            rp?.destroyNow()

            colorTexs.forEachIndexed { i, loadedTex ->
                if (!parentPass.colorAttachments[i].isProvided) {
                    loadedTex?.dispose()
                }
            }
            if (parentPass.depthAttachment?.isProvided == false) {
                depthTex?.dispose()
            }
        }
    }

    private fun Texture2d.clear() {
        loadedTexture = null
        loadingState = Texture.LoadingState.NOT_LOADED
    }

    override fun applySize(width: Int, height: Int, ctx: KoolContext) {
        dispose(ctx)

        isCreationBlocked = true
        launchDelayed(3) {
            isCreationBlocked = false
        }
    }

    private fun create(ctx: Lwjgl3Context) {
        val sys = (ctx.backend as VkRenderBackend).vkSystem
        val pass = parentPass
        val width = parentPass.width
        val height = parentPass.height

        var isProvidedColor = false
        val formats = mutableListOf<TexFormat>()
        if (pass.colorRenderTarget == OffscreenRenderPass.RenderTarget.RENDER_BUFFER) {
            formats += TexFormat.R
        } else {
            isProvidedColor = pass.colorAttachments[0].providedTexture != null
            if (pass.colorAttachments.any { (it.providedTexture != null) != isProvidedColor }) {
                throw IllegalStateException("Mixed provided / created color attachments are not yet supported [OffscreenRenderPass2d: ${parentPass.name}]")
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
            for (i in parentPass.colorTextures.indices) {
                createTex(parentPass.colorTextures[i], i, true, rp, sys)
            }
        }
        if (!isProvidedDepth) {
            parentPass.depthTexture?.let {
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
                    val props = parentPass.getColorTexProps(iAttachment)
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
        val vkBackend = ctx.backend as VkRenderBackend
        val prev = loadedTexture
        if (prev != null) {
            launchDelayed(3) {
                prev.dispose()
            }
        }

        val width = parentPass.width
        val height = parentPass.height
        val tex = TextureLoader.createTexture(vkBackend.vkSystem, props, width, height, 1)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
        vkBackend.vkSystem.device.addDependingResource(tex)
    }
}