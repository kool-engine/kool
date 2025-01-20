package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR
import org.lwjgl.vulkan.VK10.*

class ScreenRenderPassVk(backend: RenderBackendVk) :
    RenderPassVk(backend.physicalDevice.depthFormat, backend.physicalDevice.maxSamples, backend)
{
    override val colorTargetFormats: List<Int> = listOf(backend.physicalDevice.swapChainSupport.chooseSurfaceFormat().format())
    private val vkRenderPasses = Array<RenderPassWrapper?>(2) { null }

    init {
        releaseWith(backend.device)
    }

    private fun getOrCreateRenderPass(forceLoad: Boolean): RenderPassWrapper {
        val idx = if (forceLoad) 1 else 0
        vkRenderPasses[idx]?.let { return it }

        val rp = RenderPassWrapper(forceLoad)
        vkRenderPasses[idx] = rp
        return rp
    }

    fun onSwapchainRecreated() {
        vkRenderPasses.forEach { it?.recreateFramebuffers() }
    }

    override fun beginRenderPass(passEncoderState: RenderPassEncoderState, forceLoad: Boolean): VkRenderPass {
        val rp = getOrCreateRenderPass(forceLoad)
        rp.begin(passEncoderState)
        return rp.vkRenderPass
    }

    fun renderScene(scenePass: Scene.SceneRenderPass, passEncoderState: RenderPassEncoderState) {
        render(scenePass, passEncoderState)
    }

    override fun generateMipLevels(passEncoderState: RenderPassEncoderState) { }

    override fun copy(frameCopy: FrameCopy, passEncoderState: RenderPassEncoderState) {
        val colorDst = frameCopy.colorCopy2d

        val swapchain = backend.swapchain
        val colorTexture = swapchain.colorImage

        val colorSrc = colorTexture
        val width = colorSrc.width
        val height = colorSrc.height

        var colorDstVk: ImageVk? = null
        //var depthDstVk: ImageVk? = null

        colorDst.let { dst ->
            var copyDstC = (dst.gpuTexture as ImageVk?)
            if (copyDstC == null || copyDstC.width != width || copyDstC.height != height) {
                copyDstC?.let {
                    launchDelayed(1) { it.release() }
                }

                val imgInfo = ImageInfo(
                    imageType = VK_IMAGE_TYPE_2D,
                    format = colorSrc.format,
                    width = width,
                    height = height,
                    depth = 1,
                    arrayLayers = 1,
                    mipLevels = 1,
                    samples = VK_SAMPLE_COUNT_1_BIT,
                    usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT /*or VK_IMAGE_USAGE_TRANSFER_SRC_BIT*/ or VK_IMAGE_USAGE_SAMPLED_BIT
                )
                val texResource = ImageVk(backend, imgInfo)

                copyDstC = texResource
                dst.gpuTexture = copyDstC
                dst.loadingState = Texture.LoadingState.LOADED
            }
            colorDstVk = copyDstC
        }

        memStack {
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
                    it.mipLevel(0)
                    it.baseArrayLayer(0)
                    it.layerCount(1)
                }
                dstOffset { it.set(0, 0, 0) }
                extent { it.set(width, height, 1) }
            }
            // todo: layout transitions
            vkCmdCopyImage(passEncoderState.commandBuffer, colorSrc.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, colorDstVk!!.vkImage.handle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
        }
    }

    private inner class RenderPassWrapper(forceLoad: Boolean) : BaseReleasable() {
        val vkRenderPass: VkRenderPass
        var framebuffers: List<VkFramebuffer>

        init {
            if (forceLoad) {
                TODO()
            }

            memStack {
                val imageFormat = colorTargetFormats[0]
                val attachments = callocVkAttachmentDescriptionN(3) {
                    this[0]
                        .format(imageFormat)
                        .samples(numSamples)
                        .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                        .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                        .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                        .finalLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    this[1]
                        .format(depthFormat)
                        .samples(numSamples)
                        .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                        .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                        .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                        .finalLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                    this[2]
                        .format(imageFormat)
                        .samples(VK_SAMPLE_COUNT_1_BIT)
                        .loadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                        .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                        .finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
                }

                val colorAttachmentRef = callocVkAttachmentReferenceN(1) {
                    attachment(0)
                    layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                }
                val depthAttachmentRef = callocVkAttachmentReferenceN(1) {
                    attachment(1)
                    layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                }
                val colorAttachmentResolveRef  = callocVkAttachmentReferenceN(1) {
                    attachment(2)
                    layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                }

                val subpass = callocVkSubpassDescriptionN(1) {
                    pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                    colorAttachmentCount(1)
                    pColorAttachments(colorAttachmentRef)
                    pDepthStencilAttachment(depthAttachmentRef[0])
                    pResolveAttachments(colorAttachmentResolveRef)
                }

                val dependency = callocVkSubpassDependencyN(1) {
                    srcSubpass(VK_SUBPASS_EXTERNAL)
                    dstSubpass(0)
                    srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    srcAccessMask(0)
                    dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                }

                vkRenderPass = device.createRenderPass {
                    pAttachments(attachments)
                    pSubpasses(subpass)
                    pDependencies(dependency)
                }
            }
            framebuffers = createFramebuffers()

            releaseWith(this@ScreenRenderPassVk)
            logD { "Created screen render pass (loading = $forceLoad)" }
        }

        fun begin(passEncoderState: RenderPassEncoderState) = with(passEncoderState.stack) {
            val beginInfo = callocVkRenderPassBeginInfo {
                renderPass(vkRenderPass.handle)

                val swapchain = backend.swapchain
                framebuffer(framebuffers[swapchain.nextSwapImage].handle)
                renderArea().extent(swapchain.extent)

                val clearValues = callocVkClearValueN(2) {
                    this[0].setColor(passEncoderState.renderPass.clearColor ?: Color.BLACK)
                    this[1].depthStencil {
                        it.depth(if (passEncoderState.renderPass.isReverseDepth) 0f else 1f)
                        it.stencil(0)
                    }
                }
                pClearValues(clearValues)
            }
            vkCmdBeginRenderPass(passEncoderState.commandBuffer, beginInfo, VK_SUBPASS_CONTENTS_INLINE)
        }

        override fun release() {
            super.release()
            device.destroyRenderPass(vkRenderPass)
        }

        fun recreateFramebuffers() {
            framebuffers = createFramebuffers()
        }

        private fun createFramebuffers(): List<VkFramebuffer> = buildList {
            val swapchain = backend.swapchain
            memStack {
                swapchain.imageViews.forEach { imgView ->
                    val fb = device.createFramebuffer(this@memStack) {
                        renderPass(vkRenderPass.handle)
                        pAttachments(longs(swapchain.colorImageView.handle, swapchain.depthImageView.handle, imgView.handle))
                        width(swapchain.extent.width())
                        height(swapchain.extent.height())
                        layers(1)
                    }
                    add(fb)
                    swapchain.onRelease { device.destroyFramebuffer(fb) }
                }
            }
        }
    }
}