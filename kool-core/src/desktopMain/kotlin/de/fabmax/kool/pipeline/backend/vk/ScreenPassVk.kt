package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ClearColorLoad
import de.fabmax.kool.pipeline.ClearDepthLoad
import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.releaseWith
import org.lwjgl.vulkan.KHRCopyCommands2.vkCmdBlitImage2KHR
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdBeginRenderingKHR
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdEndRenderingKHR
import org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class ScreenPassVk(backend: RenderBackendVk) :
    RenderPassVk(true, backend.swapchain.numSamples, backend)
{
    override val colorTargetFormats: List<Int> = listOf(backend.physicalDevice.swapChainSupport.chooseSurfaceFormat().format())
    private var isStore = false

    private lateinit var colorImage: ImageVk
    private lateinit var depthImage: ImageVk
    private lateinit var resolveImage: ImageVk
    private lateinit var colorImageView: VkImageView
    private lateinit var depthImageView: VkImageView
    private lateinit var resolveImageView: VkImageView

    private var colorImageViews: List<VkImageView> = emptyList()
    private var resolveImageViews: List<VkImageView> = emptyList()

    init {
        releaseWith(backend.device)
        onSwapchainRecreated()
    }

    fun onSwapchainRecreated() {
        backend.commandPool.singleShotCommands { commandBuffer ->
            val (cImage, cImageView) = createColorResources(numSamples, commandBuffer)
            colorImage = cImage.also { it.releaseWith(backend.swapchain) }
            colorImageView = cImageView
            colorImageViews = listOf(cImageView)

            val (dImage, dImageView) = createDepthResources(commandBuffer)
            depthImage = dImage.also { it.releaseWith(backend.swapchain) }
            depthImageView = dImageView

            val (rImage, rImageView) = createColorResources(1, commandBuffer)
            resolveImage = rImage.also { it.releaseWith(backend.swapchain) }
            resolveImageView = rImageView
            resolveImageViews = listOf(rImageView)
        }
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean) {
        val isLoad = forceLoad || passEncoderState.renderPass.clearColors[0] == ClearColorLoad
        val storeOp = if (isStore) VK_ATTACHMENT_STORE_OP_STORE else VK_ATTACHMENT_STORE_OP_DONT_CARE

        val srcLayout = if (isLoad) resolveImage.lastKnownLayout else VK_IMAGE_LAYOUT_UNDEFINED
        resolveImage.transitionLayout(
            oldLayout = srcLayout,
            newLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
            commandBuffer = passEncoderState.commandBuffer,
            stack = passEncoderState.stack
        )

        val renderingInfo = setupRenderingInfo(
            width = colorImage.width,
            height = colorImage.height,
            forceLoad = forceLoad,
            colorImageViews = colorImageViews,
            colorClearModes = passEncoderState.renderPass.clearColors,
            colorStoreOp = storeOp,
            resolveColorViews = resolveImageViews,
            depthImageView = depthImageView,
            depthClearMode = passEncoderState.renderPass.clearDepth,
            isReverseDepth = passEncoderState.renderPass.isReverseDepth
        )
        vkCmdBeginRenderingKHR(passEncoderState.commandBuffer, renderingInfo)
    }

    fun blitOutputImage(passEncoderState: PassEncoderState) {
        val swapchain = backend.swapchain
        val dstImage = swapchain.images[backend.swapchain.nextSwapImage]

        resolveImage.transitionLayout(
            oldLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
            newLayout = VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
            commandBuffer = passEncoderState.commandBuffer,
            stack = passEncoderState.stack
        )
        dstImage.transitionLayout(
            oldLayout = VK_IMAGE_LAYOUT_UNDEFINED,
            newLayout = VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
            aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
            mipLevels = 1,
            arrayLayers = 1,
            commandBuffer = passEncoderState.commandBuffer,
            stack = passEncoderState.stack
        )

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
        image.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, commandBuffer)
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
        image.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, commandBuffer)
        image.onRelease { device.destroyImageView(imageView) }
        return image to imageView
    }

    private inner class ScreenCopy(val frameCopy: FrameCopy) : BaseReleasable() {
        var colorCopyView: VkImageView? = null
        var depthCopyView: VkImageView? = null

        fun getOrCreateColorCopy(): ImageVk? {
            if (!frameCopy.isCopyColor) return null

            val colorDst = frameCopy.colorCopy2d
            var colorDstVk = colorDst.gpuTexture as ImageVk?
            if (colorDstVk == null || colorDstVk.width != colorImage.width || colorDstVk.height != colorImage.height) {
                colorCopyView?.let { backend.device.destroyImageView(it) }
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
                depthCopyView?.let { backend.device.destroyImageView(it) }
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

            passEncoderState.ensureRenderPassInactive()

            colorCopyImg?.transitionLayout(
                oldLayout = VK_IMAGE_LAYOUT_UNDEFINED,
                newLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
                commandBuffer = passEncoderState.commandBuffer,
                stack = passEncoderState.stack
            )
            depthCopyImg?.transitionLayout(
                oldLayout = VK_IMAGE_LAYOUT_UNDEFINED,
                newLayout = VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
                commandBuffer = passEncoderState.commandBuffer,
                stack = passEncoderState.stack
            )

            val colorSrc = if (frameCopy.isCopyColor) colorImageViews else emptyList()
            val depthSrc = if (frameCopy.isCopyDepth) depthImageView else null

            // launch an empty render pass, this resolves the multi-sampled color texture into the resolve target
            val renderingInfo = setupRenderingInfo(
                width = colorImage.width,
                height = colorImage.height,
                colorImageViews = colorSrc,
                colorClearModes = listOf(ClearColorLoad),
                colorStoreOp = VK_ATTACHMENT_STORE_OP_STORE,
                resolveColorViews = colorCopyView?.let { listOf(it) } ?: emptyList(),
                depthImageView = depthSrc,
                depthClearMode = ClearDepthLoad,
                resolveDepthView = depthCopyView
            )
            vkCmdBeginRenderingKHR(passEncoderState.commandBuffer, renderingInfo)
            vkCmdEndRenderingKHR(passEncoderState.commandBuffer)

            colorCopyImg?.transitionLayout(
                oldLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
                newLayout = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
                commandBuffer = passEncoderState.commandBuffer,
                stack = passEncoderState.stack
            )
            depthCopyImg?.transitionLayout(
                oldLayout = VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
                newLayout = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
                commandBuffer = passEncoderState.commandBuffer,
                stack = passEncoderState.stack
            )
        }

        override fun release() {
            super.release()
            colorCopyView?.let { backend.device.destroyImageView(it) }
            depthCopyView?.let { backend.device.destroyImageView(it) }
        }
    }
}
