package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.OffscreenPass2dImpl
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.vk.util.vkFormat
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import kotlin.math.pow
import kotlin.math.roundToInt

class VkOffscreenPass2d(val parentPass: OffscreenPass2dImpl) : OffscreenPass2dImpl.BackendImpl {
    private var isCreated = false
    private var blockCreation = false

    var renderPass: VkOffscreenRenderPass? = null
        private set

    var colorAttachments: VkOffscreenRenderPass.ColorAttachments? = null
    var depthAttachment: VkOffscreenRenderPass.DepthAttachment? = null

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated && !blockCreation) {
            create(ctx)
        }
    }

    fun copyToTextures(commandBuffer: VkCommandBuffer, ctx: Lwjgl3Context) {
        if (parentPass.offscreenPass.copyTargetsColor.isEmpty()) {
            return
        }
        val rp = renderPass ?: return

        var mipLevel = 0
        var width = parentPass.offscreenPass.texWidth
        var height = parentPass.offscreenPass.texHeight
        if (parentPass.offscreenPass.targetMipLevel > 0) {
            width = (width * 0.5.pow(parentPass.offscreenPass.targetMipLevel)).roundToInt()
            height = (height * 0.5.pow(parentPass.offscreenPass.targetMipLevel)).roundToInt()
            mipLevel = parentPass.offscreenPass.targetMipLevel
        }

        memStack {
            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
            for (i in parentPass.offscreenPass.copyTargetsColor.indices) {
                val copyTarget = parentPass.offscreenPass.copyTargetsColor[i]
                val texWidth = copyTarget.loadedTexture?.width ?: 0
                val texHeight = copyTarget.loadedTexture?.height ?: 0
                if (texWidth != parentPass.offscreenPass.texWidth || texHeight != parentPass.offscreenPass.texHeight) {
                    copyTarget.loadedTexture?.dispose()
                    copyTarget.createCopyTexColor(ctx)
                }
                val target = copyTarget.loadedTexture as LoadedTextureVk

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
                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                vkCmdCopyImage(commandBuffer, rp.image.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, target.textureImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
                target.textureImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            }
            rp.image.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
        }
    }

    override fun dispose(ctx: Lwjgl3Context) {
        val rp = renderPass
        val loadedColorTex = parentPass.colorTexture.loadedTexture
        val loadedDepthTex = parentPass.depthTexture.loadedTexture

        isCreated = false
        renderPass = null

        if (!parentPass.isExtColorTexture) {
            parentPass.colorTexture.clear()
        }
        if (!parentPass.isExtDepthTexture) {
            parentPass.depthTexture.clear()
        }
        colorAttachments = null
        depthAttachment = null

        ctx.runDelayed(3) {
            rp?.destroyNow()
            if (!parentPass.isExtColorTexture) {
                loadedColorTex?.dispose()
            }
            if (!parentPass.isExtDepthTexture) {
                loadedDepthTex?.dispose()
            }
        }
    }

    private fun Texture.clear() {
        loadedTexture = null
        loadingState = Texture.LoadingState.NOT_LOADED
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)

        if (!parentPass.isExtColorTexture && !parentPass.isExtDepthTexture) {
            create(ctx)

        } else {
            blockCreation = true
            ctx.runDelayed(3) {
                blockCreation = false
                create(ctx)
            }
        }
    }

    private fun create(ctx: Lwjgl3Context) {
        val sys = (ctx.renderBackend as VkRenderBackend).vkSystem
        parentPass.apply {
            if (colorAttachments == null) {
                colorAttachments = if (isExtColorTexture) {
                    val vkTex = colorTexture.loadedTexture as LoadedTextureVk?
                    if (vkTex != null) {
                        VkOffscreenRenderPass.ProvidedColorAttachments(false, listOf(vkTex.textureImage), listOf(vkTex.textureImageView), listOf(vkTex.sampler))
                    } else {
                        null
                    }
                } else {
                    VkOffscreenRenderPass.CreatedColorAttachments(sys, offscreenPass.texWidth, offscreenPass.texHeight,
                            false, listOf(offscreenPass.colorFormat.vkFormat), VK_FILTER_LINEAR)
                }
            }

            if (depthAttachment == null) {
                depthAttachment = if (isExtDepthTexture) {
                    val vkTex = depthTexture.loadedTexture as LoadedTextureVk?
                    if (vkTex != null) {
                        VkOffscreenRenderPass.ProvidedDepthAttachment(false, vkTex.textureImage, vkTex.textureImageView, vkTex.sampler)
                    } else {
                        null
                    }
                } else {
                    val filterMethod = if (parentPass.offscreenPass.setup.isUsedAsShadowMap) VK_FILTER_LINEAR else VK_FILTER_NEAREST
                    val depthCompareOp = if (filterMethod == VK_FILTER_LINEAR) VK_COMPARE_OP_LESS else VK_COMPARE_OP_NEVER
                    VkOffscreenRenderPass.CreatedDepthAttachment(sys, offscreenPass.texWidth, offscreenPass.texHeight,
                            false, filterMethod, depthCompareOp)
                }
            }

            val ca = colorAttachments
            val da = depthAttachment
            if (ca != null && da != null) {
                val rp = VkOffscreenRenderPass(sys, offscreenPass.texWidth, offscreenPass.texHeight, ca, isExtColorTexture, da, isExtDepthTexture)

                if (!isExtColorTexture) {
                    createTex(colorTexture, true, rp, sys)
                }
                if (!isExtDepthTexture) {
                    createTex(depthTexture, false, rp, sys)
                }
                renderPass = rp
                isCreated = true
            }
        }
    }

    private fun createTex(tex: Texture, isColor: Boolean, rp: VkOffscreenRenderPass, sys: VkSystem) {
        tex.apply {
            loadedTexture = if (isColor) {
                LoadedTextureVk(sys, rp.texFormat, rp.image, rp.imageView, rp.sampler, true)
            } else {
                LoadedTextureVk(sys, rp.texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
            }
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

        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight
        val tex = TextureLoader.createTexture(vkBackend.vkSystem, props, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
        vkBackend.vkSystem.device.addDependingResource(tex)
    }
}