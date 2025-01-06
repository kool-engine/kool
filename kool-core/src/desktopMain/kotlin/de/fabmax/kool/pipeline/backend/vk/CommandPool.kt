package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkQueue

class CommandPool(val sys: VkSystem, val queue: VkQueue) : VkResource() {

    val vkCommandPool: Long
    val queueIndex = when {
        queue === sys.logicalDevice.graphicsQueue -> sys.physicalDevice.queueFamiliyIndices.graphicsFamily!!
        queue === sys.logicalDevice.transferQueue -> sys.physicalDevice.queueFamiliyIndices.transferFamily!!
        else -> throw IllegalArgumentException("Invalid queue (neither graphics nor transfer)")
    }

    init {
        memStack {
            val poolInfo = callocVkCommandPoolCreateInfo {
                queueFamilyIndex(queueIndex)
            }
            vkCommandPool = checkCreatePointer { vkCreateCommandPool(sys.logicalDevice.vkDevice, poolInfo, null, it) }
        }

        sys.logicalDevice.addDependingResource(this)
        logD { "Created command pool (queue index: $queueIndex)" }
    }

    fun reset() {
        vkResetCommandPool(sys.logicalDevice.vkDevice, vkCommandPool, 0)
    }

    fun createCommandBuffers(nBuffers: Int): CommandBuffers = CommandBuffers(this, nBuffers)

    inline fun singleTimeCommands(block: MemoryStack.(VkCommandBuffer) -> Unit) {
        memStack {
            val allocInfo = callocVkCommandBufferAllocateInfo {
                commandPool(vkCommandPool)
                level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                commandBufferCount(1)
            }

            val pCommandBuffer = mallocPointer(1)
            checkVk(vkAllocateCommandBuffers(sys.logicalDevice.vkDevice, allocInfo, pCommandBuffer))
            val commandBuffer = VkCommandBuffer(pCommandBuffer[0], sys.logicalDevice.vkDevice)

            val beginInfo = callocVkCommandBufferBeginInfo {
                flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT)
            }
            vkBeginCommandBuffer(commandBuffer, beginInfo)
            block(commandBuffer)
            vkEndCommandBuffer(commandBuffer)

            val submitInfo = callocVkSubmitInfoN(1) {
                pCommandBuffers(pCommandBuffer)
            }
            vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE)
            vkQueueWaitIdle(queue)
            vkFreeCommandBuffers(sys.logicalDevice.vkDevice, vkCommandPool, pCommandBuffer)
        }
    }

    override fun freeResources() {
        vkDestroyCommandPool(sys.logicalDevice.vkDevice, vkCommandPool, null)
        logD { "Destroyed command pool" }
    }
}