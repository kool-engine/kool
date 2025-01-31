package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkClearValue
import org.lwjgl.vulkan.VkRenderPassBeginInfo

class ScreenPassVk(backend: RenderBackendVk) :
    RenderPassVk(backend.physicalDevice.depthFormat, backend.swapchain.numSamples, backend)
{
    override val colorTargetFormats: List<Int> = listOf(backend.physicalDevice.swapChainSupport.chooseSurfaceFormat().format())
    private val vkRenderPasses = Array<RenderPassWrapper?>(4) { null }
    private var isStore = false

    init {
        releaseWith(backend.device)
    }

    fun onSwapchainRecreated() {
        vkRenderPasses.forEach { it?.recreateFramebuffers(backend.swapchain.imageViews) }
    }

    private fun getOrCreateRenderPass(isLoad: Boolean): RenderPassWrapper {
        if (isLoad && !isStore) {
            isStore = true
            logI { "Screen copy requested. Enabling screen copy feature. First capture might be corrupted." }
        }

        var idx = 0
        if (isLoad) idx = idx or 1
        if (isStore) idx = idx or 2
        vkRenderPasses[idx]?.let { return it }

        val rp = RenderPassWrapper(
            isLoad = isLoad,
            isStore = isStore,
            resolveViews = backend.swapchain.imageViews,
            finalLayout = VK_IMAGE_LAYOUT_PRESENT_SRC_KHR,
            destoryResolveViews = false
        )
        vkRenderPasses[idx] = rp
        return rp
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean): VkRenderPass {
        val isLoad = forceLoad || passEncoderState.renderPass.clearColor == null
        val rp = getOrCreateRenderPass(isLoad)
        rp.begin(passEncoderState, backend.swapchain.nextSwapImage)
        return rp.vkRenderPass
    }

    fun renderScene(scenePass: Scene.ScreenPass, passEncoderState: PassEncoderState) {
        render(scenePass, passEncoderState)
    }

    override fun generateMipLevels(passEncoderState: PassEncoderState) { }

    override fun copy(frameCopy: FrameCopy, passEncoderState: PassEncoderState) {
        var screenCopy = frameCopy.gpuFrameCopy as ScreenCopy?
        if (screenCopy == null) {
            screenCopy = ScreenCopy(frameCopy)
            frameCopy.gpuFrameCopy = screenCopy
        }
        screenCopy.copy(passEncoderState)
    }

    override fun release() {
        super.release()
        vkRenderPasses.filterNotNull().forEach { it.release() }
    }

    private inner class RenderPassWrapper(
        val isLoad: Boolean,
        isStore: Boolean,
        resolveViews: List<VkImageView>,
        finalLayout: Int,
        val destoryResolveViews: Boolean
    ) : BaseReleasable() {
        val vkRenderPass: VkRenderPass
        var framebuffers: List<VkFramebuffer>
        var framebufferWidth = 0; private set
        var framebufferHeight = 0; private set

        init {
            val loadOp = if (isLoad) VK_ATTACHMENT_LOAD_OP_LOAD else VK_ATTACHMENT_LOAD_OP_CLEAR
            val storeOp = if (isStore) VK_ATTACHMENT_STORE_OP_STORE else VK_ATTACHMENT_STORE_OP_DONT_CARE

            val initialColorLayout = if (isLoad) VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL else VK_IMAGE_LAYOUT_UNDEFINED
            val initialDepthLayout = if (isLoad) VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL else VK_IMAGE_LAYOUT_UNDEFINED

            memStack {
                val imageFormat = colorTargetFormats[0]
                val attachments = callocVkAttachmentDescriptionN(3) {
                    this[0]
                        .format(imageFormat)
                        .samples(numSamples)
                        .loadOp(loadOp)
                        .storeOp(storeOp)
                        .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(initialColorLayout)
                        .finalLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    this[1]
                        .format(depthFormat)
                        .samples(numSamples)
                        .loadOp(loadOp)
                        .storeOp(storeOp)
                        .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(initialDepthLayout)
                        .finalLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                    this[2]
                        .format(imageFormat)
                        .samples(VK_SAMPLE_COUNT_1_BIT)
                        .loadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                        .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                        .finalLayout(finalLayout)
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

                framebuffers = createFramebuffers(resolveViews)
            }
            logD("ScreenRenderPassVk") { "Created screen render pass (isLoad: $isLoad)" }
        }

        fun begin(passEncoderState: PassEncoderState, viewIndex: Int) {
            renderPassBeginInfo.apply {
                renderPass(vkRenderPass.handle)
                framebuffer(framebuffers[viewIndex].handle)
                renderArea().extent(backend.swapchain.extent)

                val rp = if (passEncoderState.isPassActive) passEncoderState.renderPass else null
                clearValues[0].setColor(rp?.clearColor ?: Color.BLACK)
                clearValues[1].depthStencil {
                    it.depth(if (rp?.isReverseDepth == true) 0f else 1f)
                    it.stencil(0)
                }
                pClearValues(clearValues)
            }
            vkCmdBeginRenderPass(passEncoderState.commandBuffer, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE)
        }

        override fun release() {
            super.release()
            device.destroyRenderPass(vkRenderPass)
        }

        fun recreateFramebuffers(resolveViews: List<VkImageView>) {
            memStack {
                framebuffers = createFramebuffers(resolveViews)
            }
        }

        private fun MemoryStack.createFramebuffers(resolveViews: List<VkImageView>): List<VkFramebuffer> = buildList {
            val swapchain = backend.swapchain
            framebufferWidth = swapchain.extent.width()
            framebufferHeight = swapchain.extent.height()
            resolveViews.forEach { imgView ->
                val fb = device.createFramebuffer(this@createFramebuffers) {
                    renderPass(vkRenderPass.handle)
                    pAttachments(longs(swapchain.colorImageView.handle, swapchain.depthImageView.handle, imgView.handle))
                    width(framebufferWidth)
                    height(framebufferHeight)
                    layers(1)
                }
                add(fb)
            }
            swapchain.onRelease {
                framebuffers.forEach { device.destroyFramebuffer(it) }
                framebuffers = emptyList()
                if (destoryResolveViews) {
                    resolveViews.forEach { device.destroyImageView(it) }
                }
            }
        }
    }

    private inner class ScreenCopy(val frameCopy: FrameCopy) : BaseReleasable() {
        var colorCopy: RenderPassWrapper? = null
        var depthCopy: MultiSampledTexCopyPass? = null

        fun copy(passEncoderState: PassEncoderState) {
            if (frameCopy.isCopyColor) {
                copyColor(passEncoderState)
            }
            if (frameCopy.isCopyDepth) {
                copyDepth(passEncoderState)
            }
        }

        private fun copyColor(passEncoderState: PassEncoderState) {
            val width = backend.swapchain.colorImage.width
            val height = backend.swapchain.colorImage.height
            val colorDst = frameCopy.colorCopy2d
            var colorDstVk = colorDst.gpuTexture as ImageVk?

            if (colorDstVk == null || colorDstVk.width != width || colorDstVk.height != height) {
                colorDstVk?.release()

                val imgInfo = ImageInfo(
                    imageType = VK_IMAGE_TYPE_2D,
                    format = backend.physicalDevice.swapChainSupport.chooseSurfaceFormat().format(),
                    width = width,
                    height = height,
                    depth = 1,
                    arrayLayers = 1,
                    mipLevels = 1,
                    samples = VK_SAMPLE_COUNT_1_BIT,
                    usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT,
                    aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
                    label = colorDst.name
                )
                colorDstVk = ImageVk(backend, imgInfo)
                colorDst.gpuTexture = colorDstVk
                colorDst.loadingState = Texture.LoadingState.LOADED
            }

            if (colorCopy == null) {
                colorCopy = RenderPassWrapper(
                    isLoad = true,
                    isStore = true,
                    resolveViews = listOf(ImageVk.imageView2d(backend.device, colorDstVk)),
                    finalLayout = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
                    destoryResolveViews = true
                )
            }
            val copyPass = colorCopy!!

            if (copyPass.framebuffers.isEmpty() || copyPass.framebufferWidth != width ||copyPass.framebufferHeight != height) {
                copyPass.recreateFramebuffers(listOf(ImageVk.imageView2d(backend.device, colorDstVk)))
            }

            // launch an empty render pass, this resolves the multi-sampled color texture into the resolve target
            passEncoderState.ensureRenderPassInactive()
            copyPass.begin(passEncoderState, 0)
            vkCmdEndRenderPass(passEncoderState.commandBuffer)
        }

        private fun copyDepth(passEncoderState: PassEncoderState) {
            if (depthCopy == null) {
                depthCopy = MultiSampledTexCopyPass(backend, TexFormat.R_F32, FilterMethod.NEAREST)
            }

            backend.swapchain.depthImage.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, passEncoderState.commandBuffer)
            depthCopy!!.doCopyPass(backend.swapchain.depthImage, passEncoderState)
            backend.swapchain.depthImage.transitionLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, passEncoderState.commandBuffer)

            val copiedTex = depthCopy!!.colorTexture!!.gpuTexture
            frameCopy.depthCopy2d.gpuTexture = copiedTex
            frameCopy.depthCopy2d.loadingState = Texture.LoadingState.LOADED
        }

        override fun release() {
            super.release()
            colorCopy?.release()
            depthCopy?.release()
            // set depth copy gpuTexture to null to avoid double free when frame copy is released
            // not needed for color copy, which is correctly handled by OffscreenPass2d
            frameCopy.depthCopy?.let { it.gpuTexture = null }
        }
    }

    companion object {
        private val renderPassBeginInfo = VkRenderPassBeginInfo.calloc().apply { `sType$Default`() }
        private val clearValues = VkClearValue.calloc(2)
    }
}
