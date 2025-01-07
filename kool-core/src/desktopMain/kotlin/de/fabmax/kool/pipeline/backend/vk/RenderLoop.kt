package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logI
import de.fabmax.kool.util.memStack
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.vulkan.KHRSwapchain.*
import org.lwjgl.vulkan.VK10.*

class RenderLoop(val sys: VkSystem) : VkResource() {

    private val imageAvailableSemaphore = mutableListOf<Long>()
    private val renderFinishedSemaphore = mutableListOf<Long>()
    private val inFlightFences = mutableListOf<Long>()

    private var currentFrame = 0
    private var framebufferResized = false

    private var frameCnt = 0

    init {
        memStack {
            val semaphoreInfo = callocVkSemaphoreCreateInfo { sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO) }
            val fenceInfo = callocVkFenceCreateInfo {
                sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                flags(VK_FENCE_CREATE_SIGNALED_BIT)
            }

            for (i in 0 until MAX_FRAMES_IN_FLIGHT) {
                imageAvailableSemaphore += checkCreateLongPtr { vkCreateSemaphore(sys.logicalDevice.vkDevice, semaphoreInfo, null, it) }
                renderFinishedSemaphore += checkCreateLongPtr { vkCreateSemaphore(sys.logicalDevice.vkDevice, semaphoreInfo, null, it) }
                inFlightFences += checkCreateLongPtr { vkCreateFence(sys.logicalDevice.vkDevice, fenceInfo, null, it) }
            }
        }

        sys.logicalDevice.addDependingResource(this)

        sys.window.onResize += GlfwVkWindow.OnWindowResizeListener { _, _ -> framebufferResized = true }
    }

    fun run() {
        logI { "Entering render loop" }
        while (!glfwWindowShouldClose(sys.window.windowPtr)) {
            glfwPollEvents()
            drawFrame()
        }
        vkDeviceWaitIdle(sys.logicalDevice.vkDevice)
    }

    fun drawFrame() {
        val swapChain = sys.swapChain ?: return

        memStack {
            val fence = longs(inFlightFences[currentFrame])
            vkWaitForFences(sys.logicalDevice.vkDevice, fence, true, -1L)

            val ip = mallocInt(1)
            var result = vkAcquireNextImageKHR(sys.logicalDevice.vkDevice, swapChain.vkSwapchain.handle, -1L, imageAvailableSemaphore[currentFrame], VK_NULL_HANDLE, ip)
            if (result == VK_ERROR_OUT_OF_DATE_KHR) {
                sys.recreateSwapChain()
                return
            }
            check(result == VK_SUCCESS || result == VK_SUBOPTIMAL_KHR)
            val imageIndex = ip[0]

            val waitSemaphores = longs(imageAvailableSemaphore[currentFrame])
            val signalSemaphores = longs(renderFinishedSemaphore[currentFrame])

            sys.scene.onDrawFrame(swapChain, imageIndex, fence, waitSemaphores, signalSemaphores)

            val presentInfo = callocVkPresentInfoKHR {
                sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                pWaitSemaphores(signalSemaphores)
                swapchainCount(1)
                pSwapchains(longs(swapChain.vkSwapchain.handle))
                pImageIndices(ints(imageIndex))
            }
            result = vkQueuePresentKHR(sys.logicalDevice.presentQueue, presentInfo)
            if (result == VK_ERROR_OUT_OF_DATE_KHR || result == VK_SUBOPTIMAL_KHR || framebufferResized) {
                framebufferResized = false
                sys.recreateSwapChain()
            } else {
                checkVk(result) { "Failed to present swap chain image: $result" }
            }
        }

        currentFrame = (currentFrame + 1) % MAX_FRAMES_IN_FLIGHT
        ++frameCnt
    }

    override fun freeResources() {
        for (i in 0 until MAX_FRAMES_IN_FLIGHT) {
            vkDestroySemaphore(sys.logicalDevice.vkDevice, imageAvailableSemaphore[i], null)
            vkDestroySemaphore(sys.logicalDevice.vkDevice, renderFinishedSemaphore[i], null)
            vkDestroyFence(sys.logicalDevice.vkDevice, inFlightFences[i], null)
        }
    }

    companion object {
        const val MAX_FRAMES_IN_FLIGHT = 2
    }
}