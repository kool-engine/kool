package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.util.*
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
import org.lwjgl.vulkan.KHRSwapchain.*
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkExtent2D

class Swapchain(val backend: RenderBackendVk) : BaseReleasable() {

    private val physicalDevice: PhysicalDevice get() = backend.physicalDevice
    private val device: Device get() = backend.device

    private val nextImage = BufferUtils.createIntBuffer(1)
    val nextSwapImage: Int get() = nextImage[0]
    var currentFrameIndex = 0
        private set

    val vkSwapchain: VkSwapchain
    val imageFormat: Int
    val extent: VkExtent2D = VkExtent2D.malloc()
    val images: List<VkImage>
    val imageViews: List<VkImageView>
    val numSamples = backend.physicalDevice.maxSamples.coerceAtMost(KoolSystem.configJvm.msaaSamples)

    val nImages: Int
        get() = images.size

    val colorImage: ImageVk
    val colorImageView: VkImageView
    val depthImage: ImageVk
    val depthImageView: VkImageView

    private val imageAvailableSemas: List<VkSemaphore>
    private val renderFinishedSemas: List<VkSemaphore>
    private val inFlightFences: List<VkFence>

    val imageAvailableSema: VkSemaphore get() = imageAvailableSemas[currentFrameIndex]
    val renderFinishedSema: VkSemaphore get() = renderFinishedSemas[currentFrameIndex]
    val inFlightFence: VkFence get() = inFlightFences[currentFrameIndex]

    init {
        memStack {
            val swapChainSupport = physicalDevice.swapChainSupport
            val surfaceFormat = swapChainSupport.chooseSurfaceFormat()
            val presentMode = swapChainSupport.choosePresentationMode()
            val extent = swapChainSupport.chooseSwapExtent(backend.glfwWindow, this)
            var imageCount = swapChainSupport.capabilities.minImageCount() + 1
            if (swapChainSupport.capabilities.maxImageCount() > 0) {
                imageCount = imageCount.coerceAtMost(swapChainSupport.capabilities.maxImageCount())
            }

            vkSwapchain = device.createSwapchain(this) {
                surface(backend.glfwWindow.surface.surfaceHandle)
                minImageCount(imageCount)
                imageFormat(surfaceFormat.format())
                imageColorSpace(surfaceFormat.colorSpace())
                imageExtent(extent)
                imageArrayLayers(1)
                imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
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
            this@Swapchain.extent.set(extent)

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

            backend.commandPool.singleShotCommands { commandBuffer ->
                val (cImage, cImageView) = createColorResources(commandBuffer)
                colorImage = cImage.also { addDependingReleasable(it) }
                colorImageView = cImageView

                val (dImage, dImageView) = createDepthResources(commandBuffer)
                depthImage = dImage.also { addDependingReleasable(it) }
                depthImageView = dImageView
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
        logD { "Created swap chain" }
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

    private fun createColorResources(commandBuffer: VkCommandBuffer): Pair<ImageVk, VkImageView> {
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = imageFormat,
            width = extent.width(),
            height = extent.height(),
            depth = 1,
            arrayLayers = 1,
            mipLevels = 1,
            samples = numSamples,
            tiling = VK_IMAGE_TILING_OPTIMAL,
            usage = VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT or VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT,
            label = "swapchain-color"
        )
        val image = ImageVk(backend, imgInfo)

        val imageView = ImageVk.imageView2d(device, image, VK_IMAGE_ASPECT_COLOR_BIT)
        image.transitionLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, commandBuffer)
        image.onRelease { device.destroyImageView(imageView) }
        return image to imageView
    }

    private fun createDepthResources(commandBuffer: VkCommandBuffer): Pair<ImageVk, VkImageView> {
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = physicalDevice.depthFormat,
            width = extent.width(),
            height = extent.height(),
            depth = 1,
            arrayLayers = 1,
            mipLevels = 1,
            samples = numSamples,
            tiling = VK_IMAGE_TILING_OPTIMAL,
            usage = VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT or VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT,
            label = "swapchain-depth"
        )
        val image = ImageVk(backend, imgInfo)

        val imageView = ImageVk.imageView2d(device, image, VK_IMAGE_ASPECT_DEPTH_BIT)
        image.transitionLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, commandBuffer)
        image.onRelease { device.destroyImageView(imageView) }
        return image to imageView
    }

    override fun release() {
        super.release()
        cancelReleaseWith(backend.device)
        device.destroySwapchain(vkSwapchain)
        extent.free()

        imageViews.forEach { device.destroyImageView(it) }

        imageAvailableSemas.forEach { device.destroySemaphore(it) }
        renderFinishedSemas.forEach { device.destroySemaphore(it) }
        inFlightFences.forEach { device.destroyFence(it) }

        logD { "Destroyed swap chain" }
    }

    companion object {
        const val MAX_FRAMES_IN_FLIGHT = 2
    }
}
