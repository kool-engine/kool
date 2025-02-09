package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.util.*
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
import org.lwjgl.vulkan.KHRSwapchain.*
import org.lwjgl.vulkan.VK10.*

class Swapchain(val backend: RenderBackendVk) : BaseReleasable() {

    private val physicalDevice: PhysicalDevice get() = backend.physicalDevice
    private val device: Device get() = backend.device

    private val nextImage = BufferUtils.createIntBuffer(1)
    val nextSwapImage: Int get() = nextImage[0]
    var currentFrameIndex = 0
        private set

    val vkSwapchain: VkSwapchain
    val imageFormat: Int
    val images: List<VkImage>
    val imageViews: List<VkImageView>
    val numSamples = KoolSystem.configJvm.numSamples.coerceAtMost(backend.features.maxSamples)

    val extent: Vec2i
    val width: Int get() = extent.x
    val height: Int get() = extent.y

    private val imageAvailableSemas: List<VkSemaphore>
    private val renderFinishedSemas: List<VkSemaphore>
    private val inFlightFences: List<VkFence>

    val imageAvailableSema: VkSemaphore get() = imageAvailableSemas[currentFrameIndex]
    val renderFinishedSema: VkSemaphore get() = renderFinishedSemas[currentFrameIndex]
    val inFlightFence: VkFence get() = inFlightFences[currentFrameIndex]

    init {
        memStack {
            val swapChainSupport = physicalDevice.querySwapchainSupport(this)
            val surfaceFormat = swapChainSupport.chooseSurfaceFormat()
            val presentMode = swapChainSupport.choosePresentationMode()
            extent = swapChainSupport.chooseSwapExtent(backend.glfwWindow)
            var imageCount = swapChainSupport.capabilities.minImageCount() + 1
            if (swapChainSupport.capabilities.maxImageCount() > 0) {
                imageCount = imageCount.coerceAtMost(swapChainSupport.capabilities.maxImageCount())
            }

            vkSwapchain = device.createSwapchain(this) {
                surface(backend.glfwWindow.surface.surfaceHandle)
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

                val indices = physicalDevice.queueFamiliyIndices
                if (indices.graphicsFamily != indices.presentFamily) {
                    imageSharingMode(VK_SHARING_MODE_CONCURRENT)
                    pQueueFamilyIndices(ints(indices.graphicsFamily!!, indices.presentFamily!!))
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
                repeat(MAX_FRAMES_IN_FLIGHT) { add(device.createSemaphore(this@memStack)) }
            }
            renderFinishedSemas = buildList {
                repeat(MAX_FRAMES_IN_FLIGHT) { add(device.createSemaphore(this@memStack)) }
            }
            inFlightFences = buildList {
                repeat(MAX_FRAMES_IN_FLIGHT) {
                    add(device.createFence(this@memStack) { flags(VK_FENCE_CREATE_SIGNALED_BIT) })
                }
            }
        }

        releaseWith(backend.device)
        logT { "Created swap chain" }
    }

    fun acquireNextImage(): Boolean {
        vkWaitForFences(device.vkDevice, inFlightFence.handle, true, -1)
        vkResetFences(device.vkDevice, inFlightFence.handle)
        return when (vkAcquireNextImageKHR(device.vkDevice, vkSwapchain.handle, -1, imageAvailableSema.handle, 0, nextImage)) {
            VK_SUCCESS -> true
            VK_SUBOPTIMAL_KHR -> true   // also considered OK
            VK_ERROR_OUT_OF_DATE_KHR -> false
            else -> error("failed to acquire swap chain image")
        }
    }

    fun presentNextImage(stack: MemoryStack): Boolean{
        stack.apply {
            val presentInfo = callocVkPresentInfoKHR {
                pWaitSemaphores(longs(renderFinishedSema.handle))
                pSwapchains(longs(vkSwapchain.handle))
                swapchainCount(1)
                pImageIndices(nextImage)
            }
            currentFrameIndex = (currentFrameIndex + 1) % MAX_FRAMES_IN_FLIGHT
            return when (vkQueuePresentKHR(device.presentQueue, presentInfo)) {
                VK_SUCCESS -> true
                VK_SUBOPTIMAL_KHR -> false   // not considered OK
                VK_ERROR_OUT_OF_DATE_KHR -> false
                else -> error("failed to acquire swap chain image")
            }
        }
    }

    override fun release() {
        super.release()
        cancelReleaseWith(backend.device)
        device.destroySwapchain(vkSwapchain)

        imageViews.forEach { device.destroyImageView(it) }

        imageAvailableSemas.forEach { device.destroySemaphore(it) }
        renderFinishedSemas.forEach { device.destroySemaphore(it) }
        inFlightFences.forEach { device.destroyFence(it) }

        logT { "Destroyed swap chain" }
    }

    companion object {
        const val MAX_FRAMES_IN_FLIGHT = 2
    }
}
