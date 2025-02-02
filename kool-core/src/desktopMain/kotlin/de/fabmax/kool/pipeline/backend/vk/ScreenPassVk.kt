package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.KHRCopyCommands2.vkCmdBlitImage2KHR
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdBeginRenderingKHR
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdEndRenderingKHR
import org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VK12.*
import org.lwjgl.vulkan.VkCommandBuffer

class ScreenPassVk(backend: RenderBackendVk) :
    RenderPassVk(true, backend.swapchain.numSamples, backend)
{
    override val colorTargetFormats: List<Int> = listOf(backend.physicalDevice.swapChainSupport.chooseSurfaceFormat().format())
    private var isStore = false

    private lateinit var colorImage: ImageVk
    lateinit var depthImage: ImageVk
    private lateinit var resolveImage: ImageVk
    private var colorImageView: VkImageView = VkImageView(0)
    private var depthImageView: VkImageView = VkImageView(0)
    private var resolveImageView: VkImageView = VkImageView(0)

    init {
        releaseWith(backend.device)
        onSwapchainRecreated()
    }

    fun onSwapchainRecreated() {
        backend.commandPool.singleShotCommands { commandBuffer ->
            val (cImage, cImageView) = createColorResources(numSamples, commandBuffer)
            colorImage = cImage.also { it.releaseWith(backend.swapchain) }
            colorImageView = cImageView

            val (dImage, dImageView) = createDepthResources(commandBuffer)
            depthImage = dImage.also { it.releaseWith(backend.swapchain) }
            depthImageView = dImageView

            val (cResolve, cResolveView) = createColorResources(1, commandBuffer)
            resolveImage = cResolve.also { it.releaseWith(backend.swapchain) }
            resolveImageView = cResolveView
        }
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean) {
        val isLoad = forceLoad || passEncoderState.renderPass.clearColor == null
        val loadOp = if (isLoad) VK_ATTACHMENT_LOAD_OP_LOAD else VK_ATTACHMENT_LOAD_OP_CLEAR
        val storeOp = if (isStore) VK_ATTACHMENT_STORE_OP_STORE else VK_ATTACHMENT_STORE_OP_DONT_CARE

        resolveImage.layout = VK_IMAGE_LAYOUT_UNDEFINED
        resolveImage.transitionLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, passEncoderState.commandBuffer, passEncoderState.stack)

        memStack {
            val colorAttachmentInfo = callocVkRenderingAttachmentInfoN(1) {
                imageView(colorImageView.handle)
                imageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                resolveMode(VK_RESOLVE_MODE_AVERAGE_BIT)
                resolveImageView(resolveImageView.handle)
                resolveImageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                loadOp(loadOp)
                storeOp(storeOp)
                clearValue { it.setColor(passEncoderState.renderPass.clearColor ?: Color.BLACK) }
            }
            val depthAttachmentInfo = callocVkRenderingAttachmentInfo {
                imageView(depthImageView.handle)
                imageLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                resolveMode(VK_RESOLVE_MODE_NONE)
                loadOp(loadOp)
                storeOp(storeOp)
                clearValue { cv -> cv.depthStencil { it.depth(if (passEncoderState.renderPass.isReverseDepth) 0f else 1f) } }
            }
            val renderingInfo = callocVkRenderingInfo {
                renderArea { ra ->
                    ra.offset { it.set(0, 0) }
                    ra.extent { it.set(colorImage.width, colorImage.height) }
                }
                layerCount(1)
                pColorAttachments(colorAttachmentInfo)
                pDepthAttachment(depthAttachmentInfo)
            }
            vkCmdBeginRenderingKHR(passEncoderState.commandBuffer, renderingInfo)
        }
    }

    fun blitOutputImage(passEncoderState: PassEncoderState) {
        val swapchain = backend.swapchain
        val dstImage = swapchain.images[backend.swapchain.nextSwapImage]

        resolveImage.layout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL
        resolveImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, passEncoderState.commandBuffer, passEncoderState.stack)
        dstImage.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_ASPECT_COLOR_BIT, 1, 1, passEncoderState.commandBuffer, passEncoderState.stack)

        val blit = passEncoderState.stack.callocVkBlitImageInfo2 {
            val region = passEncoderState.stack.callocVkImageBlit2N(1) {
                srcOffsets(1).set(resolveImage.width, resolveImage.height, 1)
                dstOffsets(1).set(swapchain.width, swapchain.height, 1)
                srcSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.layerCount(1)
                }
                dstSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.layerCount(1)
                }
            }
            srcImage(resolveImage.vkImage.handle)
            srcImageLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
            dstImage(dstImage.handle)
            dstImageLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
            filter(VK_FILTER_LINEAR)
            pRegions(region)
        }
        vkCmdBlitImage2KHR(passEncoderState.commandBuffer, blit)

        dstImage.transitionLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR, VK_IMAGE_ASPECT_COLOR_BIT, 1, 1, passEncoderState.commandBuffer, passEncoderState.stack)
    }

    fun renderScene(scenePass: Scene.ScreenPass, passEncoderState: PassEncoderState) {
        render(scenePass, passEncoderState)
    }

    override fun generateMipLevels(passEncoderState: PassEncoderState) { }

    override fun copy(frameCopy: FrameCopy, passEncoderState: PassEncoderState) {
        if (!isStore) {
            isStore = true
            logI { "Screen copy requested. Enabling screen copy feature. First capture might be corrupted." }
        }

        var screenCopy = frameCopy.gpuFrameCopy as ScreenCopy?
        if (screenCopy == null) {
            screenCopy = ScreenCopy(frameCopy)
            frameCopy.gpuFrameCopy = screenCopy
        }
        screenCopy.copy(passEncoderState)
    }

    private fun createColorResources(samples: Int, commandBuffer: VkCommandBuffer): Pair<ImageVk, VkImageView> {
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = backend.swapchain.imageFormat,
            width = backend.swapchain.width,
            height = backend.swapchain.height,
            depth = 1,
            arrayLayers = 1,
            mipLevels = 1,
            samples = samples,
            tiling = VK_IMAGE_TILING_OPTIMAL,
            usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or VK_IMAGE_USAGE_TRANSFER_SRC_BIT,
            aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
            label = "screen-color"
        )
        val image = ImageVk(backend, imgInfo)
        val imageView = image.imageView2d(device)
        image.transitionLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, commandBuffer)
        image.onRelease { device.destroyImageView(imageView) }
        return image to imageView
    }

    private fun createDepthResources(commandBuffer: VkCommandBuffer): Pair<ImageVk, VkImageView> {
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = backend.physicalDevice.depthFormat,
            width = backend.swapchain.width,
            height = backend.swapchain.height,
            depth = 1,
            arrayLayers = 1,
            mipLevels = 1,
            samples = numSamples,
            tiling = VK_IMAGE_TILING_OPTIMAL,
            usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT or VK_IMAGE_USAGE_SAMPLED_BIT,
            aspectMask = VK_IMAGE_ASPECT_DEPTH_BIT,
            label = "screen-depth"
        )
        val image = ImageVk(backend, imgInfo)
        val imageView = image.imageView2d(device)
        image.transitionLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, commandBuffer)
        image.onRelease { device.destroyImageView(imageView) }
        return image to imageView
    }

    private inner class ScreenCopy(val frameCopy: FrameCopy) : BaseReleasable() {
        var colorCopyView: VkImageView = VkImageView(0L)
        var depthCopyView: VkImageView = VkImageView(0L)

        fun getOrCreateColorCopy(): ImageVk? {
            if (!frameCopy.isCopyColor) return null

            val colorDst = frameCopy.colorCopy2d
            var colorDstVk = colorDst.gpuTexture as ImageVk?
            if (colorDstVk == null || colorDstVk.width != colorImage.width || colorDstVk.height != colorImage.height) {
                if (colorCopyView.handle != 0L) {
                    backend.device.destroyImageView(colorCopyView)
                }
                colorDstVk?.release()

                val imgInfo = ImageInfo(
                    imageType = VK_IMAGE_TYPE_2D,
                    format = backend.physicalDevice.swapChainSupport.chooseSurfaceFormat().format(),
                    width = colorImage.width,
                    height = colorImage.height,
                    depth = 1,
                    arrayLayers = 1,
                    mipLevels = 1,
                    samples = VK_SAMPLE_COUNT_1_BIT,
                    usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or VK_IMAGE_USAGE_SAMPLED_BIT,
                    aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
                    label = colorDst.name
                )
                colorDstVk = ImageVk(backend, imgInfo)
                colorCopyView = colorDstVk.imageView2d(backend.device)
                colorDst.gpuTexture = colorDstVk
                colorDst.loadingState = Texture.LoadingState.LOADED
            }
            return colorDstVk
        }

        fun getOrCreateDepthCopy(): ImageVk? {
            if (!frameCopy.isCopyDepth) return null

            val depthDst = frameCopy.depthCopy2d
            var depthDstVk = depthDst.gpuTexture as ImageVk?
            if (depthDstVk == null || depthDstVk.width != colorImage.width || depthDstVk.height != colorImage.height) {
                depthDstVk?.release()

                val imgInfo = ImageInfo(
                    imageType = VK_IMAGE_TYPE_2D,
                    format = backend.physicalDevice.depthFormat,
                    width = colorImage.width,
                    height = colorImage.height,
                    depth = 1,
                    arrayLayers = 1,
                    mipLevels = 1,
                    samples = VK_SAMPLE_COUNT_1_BIT,
                    usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT or VK_IMAGE_USAGE_SAMPLED_BIT,
                    aspectMask = VK_IMAGE_ASPECT_DEPTH_BIT,
                    label = depthDst.name
                )
                depthDstVk = ImageVk(backend, imgInfo)
                depthCopyView = depthDstVk.imageView2d(backend.device)
                depthDst.gpuTexture = depthDstVk
                depthDst.loadingState = Texture.LoadingState.LOADED
            }
            return depthDstVk
        }

        fun copy(passEncoderState: PassEncoderState) {
            val colorCopyImg = getOrCreateColorCopy()
            val depthCopyImg = getOrCreateDepthCopy()

            // launch an empty render pass, this resolves the multi-sampled color texture into the resolve target
            passEncoderState.ensureRenderPassInactive()
            memStack(passEncoderState.stack) {
                val colorAttachmentInfo = if (!frameCopy.isCopyColor) null else callocVkRenderingAttachmentInfoN(1) {
                    imageView(colorImageView.handle)
                    imageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    resolveMode(VK_RESOLVE_MODE_AVERAGE_BIT)
                    resolveImageView(colorCopyView.handle)
                    resolveImageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    loadOp(VK_ATTACHMENT_LOAD_OP_LOAD)
                    storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                }
                val depthAttachmentInfo = if (!frameCopy.isCopyDepth) null else callocVkRenderingAttachmentInfo {
                    imageView(depthImageView.handle)
                    imageLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                    resolveMode(VK_RESOLVE_MODE_SAMPLE_ZERO_BIT)
                    resolveImageView(depthCopyView.handle)
                    resolveImageLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                    loadOp(VK_ATTACHMENT_LOAD_OP_LOAD)
                    storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                }
                val renderingInfo = callocVkRenderingInfo {
                    renderArea { ra ->
                        ra.offset { it.set(0, 0) }
                        ra.extent { it.set(colorImage.width, colorImage.height) }
                    }
                    layerCount(1)
                    pColorAttachments(colorAttachmentInfo)
                    pDepthAttachment(depthAttachmentInfo)
                }

                vkCmdBeginRenderingKHR(passEncoderState.commandBuffer, renderingInfo)
                vkCmdEndRenderingKHR(passEncoderState.commandBuffer)

                colorCopyImg?.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, passEncoderState.commandBuffer, this)
                depthCopyImg?.transitionLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, passEncoderState.commandBuffer, this)
            }
        }

        override fun release() {
            super.release()
            if (colorCopyView.handle != 0L) {
                backend.device.destroyImageView(colorCopyView)
            }
            if (depthCopyView.handle != 0L) {
                backend.device.destroyImageView(depthCopyView)
            }
        }
    }
}
