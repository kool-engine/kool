package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_GPU_ONLY
import org.lwjgl.vulkan.KHRSurface.*
import org.lwjgl.vulkan.KHRSwapchain.*
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkExtent2D
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR
import org.lwjgl.vulkan.VkSurfaceFormatKHR
import kotlin.math.max

class SwapChain(val sys: VkSystem) : VkResource() {

    val vkSwapChain: Long
    val imageFormat: Int
    val extent = VkExtent2D.malloc()
    val images = mutableListOf<Long>()
    val imageViews = mutableListOf<ImageView>()
    val framebuffers = mutableListOf<Long>()

    val nImages: Int
        get() = images.size

    private val colorImage: Image
    private val colorImageView: ImageView

    private val depthImage: Image
    private val depthImageView: ImageView

    val renderPass: OnScreenRenderPass

    init {
        memStack {
            val swapChainSupport = querySwapChainSupport()
            val surfaceFormat = swapChainSupport.chooseBestFormat()
            val presentMode = swapChainSupport.chooseBestPresentMode()
            val extent = swapChainSupport.chooseSwapExtent(this)
            var imageCount = swapChainSupport.capabilities.minImageCount() + 1
            if (swapChainSupport.capabilities.maxImageCount() in 1 until imageCount) {
                imageCount = swapChainSupport.capabilities.maxImageCount()
            }

            val createInfo = callocVkSwapchainCreateInfoKHR {
                sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                surface(sys.window.surface.surfaceHandle)
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

                val indices = sys.physicalDevice.queueFamiliyIndices
                if (indices.graphicsFamily != indices.presentFamily) {
                    imageSharingMode(VK_SHARING_MODE_CONCURRENT)
                    pQueueFamilyIndices(ints(indices.graphicsFamily!!, indices.presentFamily!!))
                } else {
                    imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                }
            }

            vkSwapChain = checkCreatePointer { vkCreateSwapchainKHR(sys.device.vkDevice, createInfo, null, it) }
            imageFormat = surfaceFormat.format()
            this@SwapChain.extent.set(extent)

            val ip = mallocInt(1)
            vkGetSwapchainImagesKHR(sys.device.vkDevice, vkSwapChain, ip, null)
            val imgs = mallocLong(ip[0])
            vkGetSwapchainImagesKHR(sys.device.vkDevice, vkSwapChain, ip, imgs)
            for (i in 0 until ip[0]) {
                images += imgs[i]
                imageViews += ImageView(sys, imgs[i], imageFormat, VK_IMAGE_ASPECT_COLOR_BIT, 1, VK_IMAGE_VIEW_TYPE_2D, 1)
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

        sys.device.addDependingResource(this)
        logD { "Created swap chain" }
    }

    private fun createColorResources(): Pair<Image, ImageView> {
        val imgConfig = Image.Config()
        imgConfig.width = extent.width()
        imgConfig.height = extent.height()
        imgConfig.mipLevels = 1
        imgConfig.numSamples = sys.physicalDevice.msaaSamples
        imgConfig.format = imageFormat
        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
        imgConfig.usage = VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT or VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT
        imgConfig.allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
        val image = Image(sys, imgConfig)

        val imageView = ImageView(sys, image, VK_IMAGE_ASPECT_COLOR_BIT)
        image.transitionLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
        return image to imageView
    }

    private fun createDepthResources(): Pair<Image, ImageView> {
        val imgConfig = Image.Config()
        imgConfig.width = extent.width()
        imgConfig.height = extent.height()
        imgConfig.mipLevels = 1
        imgConfig.numSamples = sys.physicalDevice.msaaSamples
        imgConfig.format = sys.physicalDevice.depthFormat
        imgConfig.tiling = VK_IMAGE_TILING_OPTIMAL
        imgConfig.usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT
        imgConfig.allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
        val image = Image(sys, imgConfig)

        val imageView = ImageView(sys, image, VK_IMAGE_ASPECT_DEPTH_BIT)
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
            framebuffers += checkCreatePointer { vkCreateFramebuffer(sys.device.vkDevice, framebufferInfo, null, it) }
        }
    }

    private fun MemoryStack.querySwapChainSupport(): SwapChainSupportDetails {
        val ip = mallocInt(1)
        val physicalDevice = sys.physicalDevice.vkPhysicalDevice
        val surface = sys.window.surface.surfaceHandle
        val formatList = mutableListOf<VkSurfaceFormatKHR>()
        val presentModeList = mutableListOf<Int>()
        val capabilities = VkSurfaceCapabilitiesKHR.malloc(this)

        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, capabilities)

        vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, ip, null)
        if (ip[0] > 0) {
            val formats = VkSurfaceFormatKHR.malloc(ip[0], this)
            vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, ip, formats)
            formatList.addAll(formats)
        }

        vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, ip, null)
        if (ip[0] > 0) {
            val presentModes = mallocInt(ip[0])
            vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, ip, presentModes)
            for (i in 0 until ip[0]) {
                presentModeList += presentModes[i]
            }
        }

        return SwapChainSupportDetails(capabilities, formatList, presentModeList)
    }

    override fun freeResources() {
        sys.device.removeDependingResource(this)

        framebuffers.forEach { fb ->
            vkDestroyFramebuffer(sys.device.vkDevice, fb, null)
        }
        framebuffers.clear()

        vkDestroySwapchainKHR(sys.device.vkDevice, vkSwapChain, null)
        images.clear()
        imageViews.clear()
        extent.free()

        logD { "Destroyed swap chain" }
    }

    private inner class SwapChainSupportDetails(val capabilities: VkSurfaceCapabilitiesKHR, val formats: List<VkSurfaceFormatKHR>, val presentModes: List<Int>) {
        fun chooseBestFormat(): VkSurfaceFormatKHR {
            return formats.find { it.format() == VK_FORMAT_B8G8R8A8_UNORM && it.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR } ?: formats[0]
        }

        fun chooseBestPresentMode(): Int {
            // VK_PRESENT_MODE_FIFO_KHR is guaranteed to be available, VK_PRESENT_MODE_MAILBOX_KHR is nicer...
            //return presentModes.find { it == VK_PRESENT_MODE_MAILBOX_KHR } ?: VK_PRESENT_MODE_FIFO_KHR
            return VK_PRESENT_MODE_FIFO_KHR
        }

        fun chooseSwapExtent(stack: MemoryStack): VkExtent2D {
            return if (capabilities.currentExtent().width() != -1) {
                capabilities.currentExtent()
            } else {
                val fbWidth = stack.mallocInt(1)
                val fbHeight = stack.mallocInt(1)
                GLFW.glfwGetFramebufferSize(sys.window.windowPtr, fbWidth, fbHeight)
                VkExtent2D.malloc(stack)
                    .width(max(capabilities.minImageExtent().width(), max(capabilities.maxImageExtent().width(), fbWidth[0])))
                    .height(max(capabilities.minImageExtent().height(), max(capabilities.maxImageExtent().height(), fbHeight[0])))
            }
        }
    }
}