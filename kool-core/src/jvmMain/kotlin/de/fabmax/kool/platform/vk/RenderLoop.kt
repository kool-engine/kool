package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logI
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
                imageAvailableSemaphore += checkCreatePointer { vkCreateSemaphore(sys.device.vkDevice, semaphoreInfo, null, it) }
                renderFinishedSemaphore += checkCreatePointer { vkCreateSemaphore(sys.device.vkDevice, semaphoreInfo, null, it) }
                inFlightFences += checkCreatePointer { vkCreateFence(sys.device.vkDevice, fenceInfo, null, it) }
            }
        }

        sys.device.addDependingResource(this)

        sys.window.onResize += object : GlfwWindow.OnWindowResizeListener {
            override fun onResize(window: GlfwWindow, newWidth: Int, newHeight: Int) {
                framebufferResized = true
            }
        }
    }

    fun run() {
        logI { "Entering render loop" }
        while (!glfwWindowShouldClose(sys.window.glfwWindow)) {
            glfwPollEvents()
            drawFrame()
        }
        vkDeviceWaitIdle(sys.device.vkDevice)
    }

    fun drawFrame() {
        val swapChain = sys.swapChain ?: return

        memStack {
            val fence = longs(inFlightFences[currentFrame])
            vkWaitForFences(sys.device.vkDevice, fence, true, -1L)

            val ip = mallocInt(1)
            var result = vkAcquireNextImageKHR(sys.device.vkDevice, swapChain.vkSwapChain, -1L, imageAvailableSemaphore[currentFrame], VK_NULL_HANDLE, ip)
            if (result == VK_ERROR_OUT_OF_DATE_KHR) {
                sys.recreateSwapChain()
                return
            }
            check(result == VK_SUCCESS || result == VK_SUBOPTIMAL_KHR)
            val imageIndex = ip[0]

            val waitSemaphores = longs(imageAvailableSemaphore[currentFrame])
            val signalSemaphores = longs(renderFinishedSemaphore[currentFrame])

            val commandBuffer = sys.scene.onDrawFrame(swapChain, imageIndex)

            val submitInfo = callocVkSubmitInfo {
                sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                waitSemaphoreCount(1)
                pWaitSemaphores(waitSemaphores)
                pWaitDstStageMask(ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                pCommandBuffers(pointers(commandBuffer))
                pSignalSemaphores(signalSemaphores)
            }

            vkResetFences(sys.device.vkDevice, fence)
            checkVk(vkQueueSubmit(sys.device.graphicsQueue, submitInfo, inFlightFences[currentFrame]))

            val presentInfo = callocVkPresentInfoKHR {
                sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                pWaitSemaphores(signalSemaphores)
                swapchainCount(1)
                pSwapchains(longs(swapChain.vkSwapChain))
                pImageIndices(ints(imageIndex))
            }
            result = vkQueuePresentKHR(sys.device.presentQueue, presentInfo)
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
            vkDestroySemaphore(sys.device.vkDevice, imageAvailableSemaphore[i], null)
            vkDestroySemaphore(sys.device.vkDevice, renderFinishedSemaphore[i], null)
            vkDestroyFence(sys.device.vkDevice, inFlightFences[i], null)
        }
    }

    companion object {
        const val MAX_FRAMES_IN_FLIGHT = 2
    }
}