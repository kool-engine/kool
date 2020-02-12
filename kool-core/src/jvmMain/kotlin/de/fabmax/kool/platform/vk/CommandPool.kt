package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logD
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkQueue

class CommandPool(val sys: VkSystem, val queue: VkQueue) : VkResource() {

    val vkCommandPool: Long
    val queueIndex = when {
        queue === sys.device.graphicsQueue -> sys.physicalDevice.queueFamiliyIndices.graphicsFamily!!
        queue === sys.device.transferQueue -> sys.physicalDevice.queueFamiliyIndices.transferFamily!!
        else -> throw IllegalArgumentException("Invalid queue (neither graphics nor transfer)")
    }

    init {
        memStack {
            val poolInfo = callocVkCommandPoolCreateInfo {
                sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                queueFamilyIndex(queueIndex)
            }
            vkCommandPool = checkCreatePointer { vkCreateCommandPool(sys.device.vkDevice, poolInfo, null, it) }
        }

        sys.device.addDependingResource(this)
        logD { "Created command pool (queue index: $queueIndex)" }
    }

    fun reset() {
        vkResetCommandPool(sys.device.vkDevice, vkCommandPool, 0)
    }

    fun createCommandBuffers(nBuffers: Int): CommandBuffers = CommandBuffers(this, nBuffers)

    inline fun singleTimeCommands(block: MemoryStack.(VkCommandBuffer) -> Unit) {
        memStack {
            val allocInfo = callocVkCommandBufferAllocateInfo {
                sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                commandPool(vkCommandPool)
                level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                commandBufferCount(1)
            }

            val pCommandBuffer = mallocPointer(1)
            checkVk(vkAllocateCommandBuffers(sys.device.vkDevice, allocInfo, pCommandBuffer))
            val commandBuffer = VkCommandBuffer(pCommandBuffer[0], sys.device.vkDevice)

            val beginInfo = callocVkCommandBufferBeginInfo {
                sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT)
            }
            vkBeginCommandBuffer(commandBuffer, beginInfo)
            block(commandBuffer)
            vkEndCommandBuffer(commandBuffer)

            val submitInfo = callocVkSubmitInfoN(1) {
                sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                pCommandBuffers(pCommandBuffer)
            }
            vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE)
            vkQueueWaitIdle(queue)
            vkFreeCommandBuffers(sys.device.vkDevice, vkCommandPool, pCommandBuffer)
        }
    }

    override fun freeResources() {
        vkDestroyCommandPool(sys.device.vkDevice, vkCommandPool, null)
        logD { "Destroyed command pool" }
    }
}