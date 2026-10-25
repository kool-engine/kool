package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.util.cancelReleaseWith
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.releaseWith
import de.fabmax.kool.util.scopedMem
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
import org.lwjgl.vulkan.KHRSwapchain.*
import org.lwjgl.vulkan.VK10.*

class Swapchain(val backend: RenderBackendVk, surface: Surface) : FrameOutput() {

    private val physicalDevice: PhysicalDevice get() = backend.physicalDevice
    private val device: Device get() = backend.device
    private val frameIndex: Int get() = backend.frameSync.currentFrameIndex

    private val nextImage = BufferUtils.createIntBuffer(1)
    val nextSwapImage: Int get() = nextImage[0]

    val vkSwapchain: VkSwapchain
    val imageFormat: Int
    val images: List<VkImage>
    val imageViews: List<VkImageView>

    val extent: Vec2i
    override val width: Int get() = extent.x
    override val height: Int get() = extent.y
    override val renderFormat: Int get() = imageFormat

    private val imageAvailableSemas: List<VkSemaphore>
    private val renderFinishedSemas: List<VkSemaphore>

    override val waitSemaphore: VkSemaphore get() = imageAvailableSemas[frameIndex]
    override val signalSemaphore: VkSemaphore get() = renderFinishedSemas[frameIndex]

    init {
        scopedMem {
            val swapChainSupport = physicalDevice.querySwapchainSupport(this)
            val surfaceFormat = swapChainSupport.chooseSurfaceFormat()
            val presentMode = swapChainSupport.choosePresentationMode()
            extent = swapChainSupport.chooseSwapExtent(backend.window)
            var imageCount = swapChainSupport.capabilities.minImageCount() + 1
            if (swapChainSupport.capabilities.maxImageCount() > 0) {
                imageCount = imageCount.coerceAtMost(swapChainSupport.capabilities.maxImageCount())
            }

            vkSwapchain = device.createSwapchain(this) {
                surface(surface.surfaceHandle)
                minImageCount(imageCount)
                imageFormat(surfaceFormat.format())
                imageColorSpace(surfaceFormat.colorSpace())
                imageExtent { it.set(extent.x, extent.y) }
                imageArrayLayers(1)
                imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or VK_IMAGE_USAGE_TRANSFER_DST_BIT)
                preTransform(swapChainSupport.capabilities.currentTransform())
                compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                presentMode(presentMode)
                clipped(true)

                val graphicsFamily = physicalDevice.queueFamiliyIndices.graphicsFamily!!
                val presentFamily = physicalDevice.queueFamiliyIndices.presentFamily!!
                if (graphicsFamily != presentFamily) {
                    imageSharingMode(VK_SHARING_MODE_CONCURRENT)
                    pQueueFamilyIndices(ints(graphicsFamily, presentFamily))
                } else {
                    imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                }
            }

            imageFormat = surfaceFormat.format()

            val imgs = enumerateLongs { cnt, imgs ->
                vkGetSwapchainImagesKHR(device.vkDevice, vkSwapchain.handle, cnt, imgs)
            }
            images = buildList {
                for (i in 0 until imgs.capacity()) {
                    add(VkImage(imgs[i], 0))
                }
            }
            imageViews = images.map { img ->
                device.createImageView(
                    image = img,
                    viewType = VK_IMAGE_VIEW_TYPE_2D,
                    format = imageFormat,
                    aspectMask = VK_IMAGE_ASPECT_COLOR_BIT,
                    levelCount = 1,
                    layerCount = 1
                )
            }

            imageAvailableSemas = buildList {
                repeat(FrameSync.MAX_FRAMES_IN_FLIGHT) { add(device.createSemaphore(this@scopedMem)) }
            }
            renderFinishedSemas = buildList {
                repeat(FrameSync.MAX_FRAMES_IN_FLIGHT) { add(device.createSemaphore(this@scopedMem)) }
            }
        }

        releaseWith(backend.device)
        logT { "Created swap chain" }
    }

    override fun acquireNextImage(): Boolean {
        return when (vkAcquireNextImageKHR(device.vkDevice, vkSwapchain.handle, -1, waitSemaphore.handle, 0, nextImage)) {
            VK_SUCCESS -> true
            VK_SUBOPTIMAL_KHR -> true   // also considered OK
            VK_ERROR_OUT_OF_DATE_KHR -> false
            else -> error("failed to acquire swap chain image")
        }
    }

    override fun recordOutput(screenPass: ScreenPassVk, passEncoderState: PassEncoderState) {
        screenPass.blitOutputImage(
            dstImage = images[nextSwapImage],
            dstWidth = width,
            dstHeight = height,
            dstLayout = VK_IMAGE_LAYOUT_PRESENT_SRC_KHR,
            passEncoderState = passEncoderState
        )
    }

    override fun present(stack: MemoryStack): Boolean {
        stack.apply {
            val presentInfo = callocVkPresentInfoKHR {
                pWaitSemaphores(longs(signalSemaphore.handle))
                pSwapchains(longs(vkSwapchain.handle))
                swapchainCount(1)
                pImageIndices(nextImage)
            }
            return when (vkQueuePresentKHR(device.presentQueue!!, presentInfo)) {
                VK_SUCCESS -> true
                VK_SUBOPTIMAL_KHR -> true   // also considered OK
                VK_ERROR_OUT_OF_DATE_KHR -> false
                else -> error("failed to present swap chain image")
            }
        }
    }

    override fun doRelease() {
        cancelReleaseWith(backend.device)
        device.destroySwapchain(vkSwapchain)

        imageViews.forEach { device.destroyImageView(it) }

        imageAvailableSemas.forEach { device.destroySemaphore(it) }
        renderFinishedSemas.forEach { device.destroySemaphore(it) }

        logT { "Destroyed swap chain" }
    }
}
