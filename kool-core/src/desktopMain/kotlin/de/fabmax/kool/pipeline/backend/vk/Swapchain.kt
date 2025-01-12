package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.*
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
import org.lwjgl.vulkan.KHRSwapchain.*
import org.lwjgl.vulkan.VK10.*
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
    val imageViews: List<ImageView>
    val framebuffers: List<VkFramebuffer>

    val nImages: Int
        get() = images.size

    val colorImage: Image
    private val colorImageView: ImageView
    private val depthImage: Image
    private val depthImageView: ImageView

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
                imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT) // | VK_IMAGE_USAGE_TRANSFER_DST_BIT
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
            imageViews = images.map {
                ImageView(device, it, imageFormat, VK_IMAGE_ASPECT_COLOR_BIT, VK_IMAGE_VIEW_TYPE_2D, 1, 1).also {
                    addDependingReleasable(it)
                }
            }

            val (cImage, cImageView) = createColorResources()
            colorImage = cImage.also { addDependingReleasable(it) }
            colorImageView = cImageView.also { addDependingReleasable(it) }

            val (dImage, dImageView) = createDepthResources()
            depthImage = dImage.also { addDependingReleasable(it) }
            depthImageView = dImageView.also { addDependingReleasable(it) }

            framebuffers = createFramebuffers()

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

    private fun createColorResources(): Pair<Image, ImageView> {
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = imageFormat,
            width = extent.width(),
            height = extent.height(),
            depth = 1,
            arrayLayers = 1,
            mipLevels = 1,
            samples = physicalDevice.msaaSamples,
            tiling = VK_IMAGE_TILING_OPTIMAL,
            usage = VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT or VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT, //or VK_IMAGE_USAGE_TRANSFER_DST_BIT // does not work because of VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT flag
        )
        val image = Image(backend, imgInfo)

        val imageView = ImageView.imageView2d(device, image, VK_IMAGE_ASPECT_COLOR_BIT)
        image.transitionLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
        return image to imageView
    }

    private fun createDepthResources(): Pair<Image, ImageView> {
        val imgInfo = ImageInfo(
            imageType = VK_IMAGE_TYPE_2D,
            format = physicalDevice.depthFormat,
            width = extent.width(),
            height = extent.height(),
            depth = 1,
            arrayLayers = 1,
            mipLevels = 1,
            samples = physicalDevice.msaaSamples,
            tiling = VK_IMAGE_TILING_OPTIMAL,
            usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT,
        )
        val image = Image(backend, imgInfo)

        val imageView = ImageView.imageView2d(device, image, VK_IMAGE_ASPECT_DEPTH_BIT)
        image.transitionLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
        return image to imageView
    }

    private fun MemoryStack.createFramebuffers(): List<VkFramebuffer> = buildList {
        imageViews.forEach { imgView ->
            add(device.createFramebuffer(this@createFramebuffers) {
                renderPass(backend.screenRenderPass.vkRenderPass.handle)
                pAttachments(longs(colorImageView.vkImageView.handle, depthImageView.vkImageView.handle, imgView.vkImageView.handle))
                width(extent.width())
                height(extent.height())
                layers(1)
            })
        }
    }

    override fun release() {
        super.release()
        cancelReleaseWith(backend.device)
        framebuffers.forEach { fb -> device.destroyFramebuffer(fb) }
        device.destroySwapchain(vkSwapchain)
        extent.free()

        imageAvailableSemas.forEach { device.destroySemaphore(it) }
        renderFinishedSemas.forEach { device.destroySemaphore(it) }
        inFlightFences.forEach { device.destroyFence(it) }

        logD { "Destroyed swap chain" }
    }

    companion object {
        const val MAX_FRAMES_IN_FLIGHT = 2
    }
}
