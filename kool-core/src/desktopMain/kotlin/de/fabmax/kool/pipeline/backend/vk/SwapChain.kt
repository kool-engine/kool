package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_GPU_ONLY
import org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
import org.lwjgl.vulkan.KHRSwapchain.*
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkExtent2D

class SwapChain(val backend: VkRenderBackend) : VkResource() {
    val vkSwapChain: Long
    val imageFormat: Int
    val extent = VkExtent2D.malloc()
    val images = mutableListOf<Long>()
    val imageViews = mutableListOf<ImageView>()
    val framebuffers = mutableListOf<Long>()

    val nImages: Int
        get() = images.size

    val colorImage: Image
    private val colorImageView: ImageView

    private val depthImage: Image
    private val depthImageView: ImageView

    val renderPass: OnScreenRenderPass

    private val physicalDevice: PhysicalDevice get() = backend.physicalDevice
    private val logicalDevice: LogicalDevice get() = backend.logicalDevice

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

            val createInfo = callocVkSwapchainCreateInfoKHR {
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

            vkSwapChain = checkCreateLongPtr { vkCreateSwapchainKHR(logicalDevice.vkDevice, createInfo, null, it) }
            imageFormat = surfaceFormat.format()
            this@SwapChain.extent.set(extent)

            val (cnt, imgs) = getLongs { cnt, imgs -> vkGetSwapchainImagesKHR(logicalDevice.vkDevice, vkSwapChain, cnt, imgs) }
            for (i in 0 until cnt) {
                images += imgs[i]
                imageViews += ImageView(logicalDevice, imgs[i], imageFormat, VK_IMAGE_ASPECT_COLOR_BIT, 1, VK_IMAGE_VIEW_TYPE_2D, 1)
                    .also { addDependingResource(it) }
            }

            renderPass = OnScreenRenderPass(this@SwapChain)

            val (cImage, cImageView) = createColorResources()
            colorImage = cImage.also { addDependingResource(it) }
            colorImageView = cImageView.also { addDependingResource(it) }

            val (dImage, dImageView) = createDepthResources()
            depthImage = dImage.also { addDependingResource(it) }
            depthImageView = dImageView.also { addDependingResource(it) }

            createFramebuffers()
        }

        logicalDevice.addDependingResource(this)
        logD { "Created swap chain" }
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

    private fun MemoryStack.createFramebuffers() {
        imageViews.forEach { imgView ->
            val framebufferInfo = callocVkFramebufferCreateInfo {
                sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                renderPass(renderPass.vkRenderPass)
                pAttachments(longs(colorImageView.vkImageView, depthImageView.vkImageView, imgView.vkImageView))
                width(extent.width())
                height(extent.height())
                layers(1)
            }
            framebuffers += checkCreateLongPtr { vkCreateFramebuffer(logicalDevice.vkDevice, framebufferInfo, null, it) }
        }
    }

    override fun freeResources() {
        logicalDevice.removeDependingResource(this)

        framebuffers.forEach { fb ->
            vkDestroyFramebuffer(logicalDevice.vkDevice, fb, null)
        }
        framebuffers.clear()

        vkDestroySwapchainKHR(logicalDevice.vkDevice, vkSwapChain, null)
        images.clear()
        imageViews.clear()
        extent.free()

        logD { "Destroyed swap chain" }
    }
}