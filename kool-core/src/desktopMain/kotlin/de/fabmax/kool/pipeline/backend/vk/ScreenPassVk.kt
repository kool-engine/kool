package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.PassData
import de.fabmax.kool.configJvm
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.releaseWith
import org.lwjgl.vulkan.KHRCopyCommands2.vkCmdBlitImage2KHR
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdBeginRenderingKHR
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdEndRenderingKHR
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class ScreenPassVk(backend: RenderBackendVk) :
    RenderPassVk(true, KoolSystem.configJvm.numSamples, backend)
{
    override val colorTargetFormats: List<Int>
        field = mutableListOf(backend.output.renderFormat)
    private var isStore = false

    private lateinit var colorImage: ImageVk
    private lateinit var depthImage: ImageVk
    private var resolveImage: ImageVk? = null
    private lateinit var colorImageView: VkImageView
    private lateinit var depthImageView: VkImageView

    private var colorImageViews: List<VkImageView> = emptyList()
    private var resolveImageViews: List<VkImageView> = emptyList()

    /**
     * Single-sampled final output image of the screen pass: The resolve image if multi-sampling is enabled, the
     * color image otherwise.
     */
    val outputImage: ImageVk get() = resolveImage ?: colorImage

    private val frameCopyPasses = mutableMapOf<FrameCopy, ScreenCopy>()

    init {
        releaseWith(backend.device)
        onOutputRecreated()
    }

    fun onOutputRecreated() {
        // todo: if this would really change, draw pipelines would also need to be recreated...
        colorTargetFormats[0] = backend.output.renderFormat
        backend.commandPool.singleShotCommands { commandBuffer ->
            val (cImage, cImageView) = createColorResources(numSamples, commandBuffer)
            colorImage = cImage.also { it.releaseWith(backend.output) }
            colorImageView = cImageView
            colorImageViews = listOf(cImageView)

            val (dImage, dImageView) = createDepthResources(commandBuffer)
            depthImage = dImage.also { it.releaseWith(backend.output) }
            depthImageView = dImageView

            if (isMultiSampled) {
                val (rImage, rImageView) = createColorResources(1, commandBuffer)
                resolveImage = rImage.also { it.releaseWith(backend.output) }
                resolveImageViews = listOf(rImageView)
            } else {
                // without multi-sampling, the color image is directly used as output, no need to resolve anything
                resolveImage = null
                resolveImageViews = emptyList()
            }
        }
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean) {
        val isLoad = forceLoad || passEncoderState.renderPass.colorAttachments[0].clearColor == ClearColorLoad
        // without multi-sampling the color image is the output image and needs to be stored in any case
        val storeOp = if (isStore || !isMultiSampled) VK_ATTACHMENT_STORE_OP_STORE else VK_ATTACHMENT_STORE_OP_DONT_CARE

        val output = outputImage
        val srcLayout = if (isLoad) output.lastKnownLayout else VK_IMAGE_LAYOUT_UNDEFINED
        output.transitionLayout(
            oldLayout = srcLayout,
            newLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
            commandBuffer = passEncoderState.commandBuffer,
            stack = passEncoderState.stack
        )

        val renderingInfo = setupRenderingInfo(
            width = colorImage.width,
            height = colorImage.height,
            renderPass = passEncoderState.renderPass,
            forceLoad = forceLoad,
            colorImageViews = colorImageViews,
            colorStoreOp = storeOp,
            resolveColorViews = resolveImageViews,
            depthImageView = depthImageView,
        )
        vkCmdBeginRenderingKHR(passEncoderState.commandBuffer, renderingInfo)
    }

    /**
     * Blits the screen pass output into the given destination image (e.g. a swapchain image). The previous content
     * of [dstImage] is discarded, after the blit [dstImage] is transitioned into [dstLayout].
     */
    fun blitOutputImage(dstImage: VkImage, dstWidth: Int, dstHeight: Int, dstLayout: Int, passEncoderState: PassEncoderState) {
        val srcImage = outputImage
        srcImage.transitionLayout(
            oldLayout = srcImage.lastKnownLayout,
            newLayout = VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
            commandBuffer = passEncoderState.commandBuffer,
            stack = passEncoderState.stack
        )
        dstImage.transitionLayout(
            oldLayout = VK_IMAGE_LAYOUT_UNDEFINED,
            newLayout = VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
            aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
            baseMipLevel = 0,
            mipLevels = 1,
            baseArrayLayer = 0,
            arrayLayers = 1,
            commandBuffer = passEncoderState.commandBuffer,
            stack = passEncoderState.stack
        )

        val blit = passEncoderState.stack.callocVkBlitImageInfo2 {
            val region = passEncoderState.stack.callocVkImageBlit2N(1) {
                srcOffsets(1).set(srcImage.width, srcImage.height, 1)
                dstOffsets(1).set(dstWidth, dstHeight, 1)
                srcSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.layerCount(1)
                }
                dstSubresource {
                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    it.layerCount(1)
                }
            }
            srcImage(srcImage.vkImage.handle)
            srcImageLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
            dstImage(dstImage.handle)
            dstImageLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
            filter(VK_FILTER_LINEAR)
            pRegions(region)
        }
        vkCmdBlitImage2KHR(passEncoderState.commandBuffer, blit)

        dstImage.transitionLayout(
            oldLayout = VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
            newLayout = dstLayout,
            aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
            baseMipLevel = 0,
            mipLevels = 1,
            baseArrayLayer = 0,
            arrayLayers = 1,
            commandBuffer = passEncoderState.commandBuffer,
            stack = passEncoderState.stack)
    }

    fun renderScene(passData: PassData, passEncoderState: PassEncoderState) {
        render(passData, passEncoderState)
    }

    override fun generateMipLevels(passEncoderState: PassEncoderState) { }

    override fun copy(frameCopy: FrameCopy, passEncoderState: PassEncoderState) {
        if (!isStore && isMultiSampled) {
            isStore = true
            logI { "Screen copy requested. Enabling screen copy feature. First capture might be corrupted." }
        }

        frameCopyPasses.getOrPut(frameCopy) { ScreenCopy(frameCopy) }.copy(passEncoderState)
    }

    private fun createColorResources(samples: Int, commandBuffer: VkCommandBuffer): Pair<ImageVk, VkImageView> {
        val renderScale = backend.window.renderResolutionFactor
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = backend.output.renderFormat,
            width = (backend.output.width * renderScale).toInt(),
            height = (backend.output.height * renderScale).toInt(),
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
        val renderScale = backend.window.renderResolutionFactor
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = backend.physicalDevice.depthFormat,
            width = (backend.output.width * renderScale).toInt(),
            height = (backend.output.height * renderScale).toInt(),
            depth = 1,
            arrayLayers = 1,
            mipLevels = 1,
            samples = numSamples,
            tiling = VK_IMAGE_TILING_OPTIMAL,
            usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT or VK_IMAGE_USAGE_SAMPLED_BIT or VK_IMAGE_USAGE_TRANSFER_SRC_BIT,
            aspectMask = VK_IMAGE_ASPECT_DEPTH_BIT,
            label = "screen-depth"
        )
        val image = ImageVk(backend, imgInfo)
        val imageView = image.imageView2d(device)
        image.transitionLayout(VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, commandBuffer)
        image.onRelease { device.destroyImageView(imageView) }
        return image to imageView
    }

    override fun doRelease() { }

    private inner class ScreenCopy(val frameCopy: FrameCopy) {
        var colorCopyView: VkImageView? = null
        var depthCopyView: VkImageView? = null

        private val copyRenderPass  = object : RenderPass(1, MipMode.Single, "screen-copy-pass") {
            override val colorAttachments: List<RenderPassColorAttachment> = listOf(object : RenderPassColorAttachment {
                override var clearColor: ClearColor = ClearColorLoad
            })
            override val depthAttachment: RenderPassDepthAttachment = object : RenderPassDepthAttachment {
                override var clearDepth: ClearDepth = ClearDepthLoad
            }
            override val dimensions: Vec3i = Vec3i.ZERO
            override val views: List<View> = emptyList()
            override fun doRelease() { }
        }

        fun getOrCreateColorCopy(): ImageVk? {
            if (!frameCopy.isCopyColor) return null

            val colorDst = frameCopy.colorCopy2d
            var colorDstVk = colorDst.gpuTexture as ImageVk?
            if (colorDstVk == null || colorDstVk.width != colorImage.width || colorDstVk.height != colorImage.height) {
                colorCopyView?.let { backend.device.destroyImageView(it) }
                colorDstVk?.release()

                val imgInfo = ImageInfo(
                    imageType = VK_IMAGE_TYPE_2D,
                    format = colorTargetFormats[0],
                    width = colorImage.width,
                    height = colorImage.height,
                    depth = 1,
                    arrayLayers = 1,
                    mipLevels = 1,
                    samples = VK_SAMPLE_COUNT_1_BIT,
                    usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or VK_IMAGE_USAGE_SAMPLED_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT,
                    aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
                    label = colorDst.name
                )
                colorDstVk = ImageVk(backend, imgInfo)
                colorCopyView = colorDstVk.imageView2d(backend.device).also { view ->
                    colorDstVk.onRelease { backend.device.destroyImageView(view) }
                }
                colorDst.gpuTexture = colorDstVk
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
                    usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT or VK_IMAGE_USAGE_SAMPLED_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT,
                    aspectMask = VK_IMAGE_ASPECT_DEPTH_BIT,
                    label = depthDst.name
                )
                depthDstVk = ImageVk(backend, imgInfo)
                depthCopyView = depthDstVk.imageView2d(backend.device).also { view ->
                    depthDstVk.onRelease { backend.device.destroyImageView(view) }
                }
                depthDst.gpuTexture = depthDstVk
            }
            return depthDstVk
        }

        fun copy(passEncoderState: PassEncoderState) {
            val colorCopyImg = getOrCreateColorCopy()
            val depthCopyImg = getOrCreateDepthCopy()

            passEncoderState.ensureRenderPassInactive()
            if (isMultiSampled) {
                resolveCopy(colorCopyImg, depthCopyImg, passEncoderState)
            } else {
                // single-sampled images can't be resolved -> copy them instead
                colorCopyImg?.copyFromImage(colorImage, passEncoderState.commandBuffer, stack = passEncoderState.stack)
                depthCopyImg?.copyFromImage(depthImage, passEncoderState.commandBuffer, stack = passEncoderState.stack)
            }
        }

        private fun resolveCopy(colorCopyImg: ImageVk?, depthCopyImg: ImageVk?, passEncoderState: PassEncoderState) {
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
                renderPass = copyRenderPass,
                colorImageViews = colorSrc,
                colorStoreOp = VK_ATTACHMENT_STORE_OP_STORE,
                resolveColorViews = colorCopyView?.let { listOf(it) } ?: emptyList(),
                depthImageView = depthSrc,
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
    }
}
