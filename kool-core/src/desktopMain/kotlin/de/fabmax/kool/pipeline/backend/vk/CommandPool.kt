package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkQueue

class CommandPool(val backendVk: RenderBackendVk, val queue: VkQueue) : VkResource() {

    val physicalDevice: PhysicalDevice get() = backendVk.physicalDevice
    val logicalDevice: LogicalDevice get() = backendVk.logicalDevice

    val vkCommandPool: Long
    val queueIndex = when {
        queue === logicalDevice.graphicsQueue -> physicalDevice.queueFamiliyIndices.graphicsFamily!!
        queue === logicalDevice.transferQueue -> physicalDevice.queueFamiliyIndices.transferFamily!!
        else -> throw IllegalArgumentException("Invalid queue (neither graphics nor transfer)")
    }

    init {
        memStack {
            val poolInfo = callocVkCommandPoolCreateInfo {
                flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
                queueFamilyIndex(queueIndex)
            }
            vkCommandPool = checkCreateLongPtr { vkCreateCommandPool(logicalDevice.vkDevice, poolInfo, null, it) }
        }

        logicalDevice.addDependingResource(this)
        logD { "Created command pool (queue index: $queueIndex)" }
    }

    fun reset() {
        vkResetCommandPool(logicalDevice.vkDevice, vkCommandPool, 0)
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
            checkVk(vkAllocateCommandBuffers(logicalDevice.vkDevice, allocInfo, pCommandBuffer))
            val commandBuffer = VkCommandBuffer(pCommandBuffer[0], logicalDevice.vkDevice)

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
            vkFreeCommandBuffers(logicalDevice.vkDevice, vkCommandPool, pCommandBuffer)
        }
    }

    override fun freeResources() {
        vkDestroyCommandPool(logicalDevice.vkDevice, vkCommandPool, null)
        logD { "Destroyed command pool" }
    }
}