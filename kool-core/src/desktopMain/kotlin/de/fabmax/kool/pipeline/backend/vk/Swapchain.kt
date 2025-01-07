package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_GPU_ONLY
import org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
import org.lwjgl.vulkan.KHRSwapchain.*
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkExtent2D

class Swapchain(val backend: RenderBackendVk) : VkResource() {

    private val physicalDevice: PhysicalDevice get() = backend.physicalDevice
    private val logicalDevice: LogicalDevice get() = backend.logicalDevice

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

            vkSwapchain = logicalDevice.createSwapchain(this) {
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
                vkGetSwapchainImagesKHR(logicalDevice.vkDevice, vkSwapchain.handle, cnt, imgs)
            }
            images = buildList {
                for (i in 0 until imgs.capacity()) {
                    add(VkImage(imgs[i], 0))
                }
            }
            imageViews = images.map {
                ImageView(logicalDevice, it, imageFormat, VK_IMAGE_ASPECT_COLOR_BIT, 1, VK_IMAGE_VIEW_TYPE_2D, 1).also {
                    addDependingResource(it)
                }
            }

            val (cImage, cImageView) = createColorResources()
            colorImage = cImage.also { addDependingResource(it) }
            colorImageView = cImageView.also { addDependingResource(it) }

            val (dImage, dImageView) = createDepthResources()
            depthImage = dImage.also { addDependingResource(it) }
            depthImageView = dImageView.also { addDependingResource(it) }

            framebuffers = createFramebuffers()

            imageAvailableSemas = buildList {
                repeat(MAX_FRAMES_IN_FLIGHT) { add(logicalDevice.createSemaphore(this@memStack)) }
            }
            renderFinishedSemas = buildList {
                repeat(MAX_FRAMES_IN_FLIGHT) { add(logicalDevice.createSemaphore(this@memStack)) }
            }
            inFlightFences = buildList {
                repeat(MAX_FRAMES_IN_FLIGHT) {
                    add(logicalDevice.createFence(this@memStack) { flags(VK_FENCE_CREATE_SIGNALED_BIT) })
                }
            }
        }

        logicalDevice.addDependingResource(this)
        logD { "Created swap chain" }
    }

    fun acquireNextImage(): Boolean {
        vkWaitForFences(logicalDevice.vkDevice, inFlightFence.handle, true, -1)
        vkResetFences(logicalDevice.vkDevice, inFlightFence.handle)
        return when (vkAcquireNextImageKHR(logicalDevice.vkDevice, vkSwapchain.handle, -1, imageAvailableSema.handle, 0, nextImage)) {
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
            return when (vkQueuePresentKHR(logicalDevice.presentQueue, presentInfo)) {
                VK_SUCCESS -> true
                VK_SUBOPTIMAL_KHR -> false   // not considered OK
                VK_ERROR_OUT_OF_DATE_KHR -> false
                else -> error("failed to acquire swap chain image")
            }
        }
    }

    private fun createColorResources(): Pair<Image, ImageView> {
        val imgConfig = Image.Config()
        imgConfig.width = extent.width()
        imgConfig.height = extent.height()
        imgConfig.mipLevels = 1
        imgConfig.numSamples = physicalDevice.msaaSamples
        imgConfig.format = imageFormat
        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
        imgConfig.usage = VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT or VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT //or VK_IMAGE_USAGE_TRANSFER_DST_BIT // does not work because of VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT flag
        imgConfig.allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
        val image = Image(backend, imgConfig)

        val imageView = ImageView.imageView2d(logicalDevice, image, VK_IMAGE_ASPECT_COLOR_BIT)
        image.transitionLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
        return image to imageView
    }

    private fun createDepthResources(): Pair<Image, ImageView> {
        val imgConfig = Image.Config()
        imgConfig.width = extent.width()
        imgConfig.height = extent.height()
        imgConfig.mipLevels = 1
        imgConfig.numSamples = physicalDevice.msaaSamples
        imgConfig.format = physicalDevice.depthFormat
        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
        imgConfig.usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT
        imgConfig.allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
        val image = Image(backend, imgConfig)

        val imageView = ImageView.imageView2d(logicalDevice, image, VK_IMAGE_ASPECT_DEPTH_BIT)
        image.transitionLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
        return image to imageView
    }

    private fun MemoryStack.createFramebuffers(): List<VkFramebuffer> = buildList {
        imageViews.forEach { imgView ->
            add(logicalDevice.createFramebuffer(this@createFramebuffers) {
                renderPass(backend.screenRenderPass.vkRenderPass.handle)
                pAttachments(longs(colorImageView.vkImageView.handle, depthImageView.vkImageView.handle, imgView.vkImageView.handle))
                width(extent.width())
                height(extent.height())
                layers(1)
            })
        }
    }

    override fun freeResources() {
        logicalDevice.removeDependingResource(this)
        framebuffers.forEach { fb -> logicalDevice.destroyFramebuffer(fb) }
        logicalDevice.destroySwapchain(vkSwapchain)
        extent.free()

        imageAvailableSemas.forEach { logicalDevice.destroySemaphore(it) }
        renderFinishedSemas.forEach { logicalDevice.destroySemaphore(it) }
        inFlightFences.forEach { logicalDevice.destroyFence(it) }

        logD { "Destroyed swap chain" }
    }

    companion object {
        const val MAX_FRAMES_IN_FLIGHT = 2
    }
}
