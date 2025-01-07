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

    val vkCommandPool: VkCommandPool
    val queueIndex = when {
        queue === logicalDevice.graphicsQueue -> physicalDevice.queueFamiliyIndices.graphicsFamily!!
        queue === logicalDevice.transferQueue -> physicalDevice.queueFamiliyIndices.transferFamily!!
        else -> throw IllegalArgumentException("Invalid queue (neither graphics nor transfer)")
    }

    init {
        vkCommandPool = logicalDevice.createCommandPool {
            flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
            queueFamilyIndex(queueIndex)
        }
        logicalDevice.addDependingResource(this)
        logD { "Created command pool (queue index: $queueIndex)" }
    }

    fun reset() {
        logicalDevice.resetCommandPool(vkCommandPool)
    }

    inline fun singleShotCommands(block: MemoryStack.(VkCommandBuffer) -> Unit) {
        memStack {
            val commandBuffer = allocateCommandBuffers(1, stack = this).first()

            val beginInfo = callocVkCommandBufferBeginInfo {
                flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT)
            }
            vkBeginCommandBuffer(commandBuffer, beginInfo)
            block(commandBuffer)
            vkEndCommandBuffer(commandBuffer)

            val submitInfo = callocVkSubmitInfoN(1) {
                pCommandBuffers(pointers(commandBuffer))
            }
            vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE)
            vkQueueWaitIdle(queue)
            vkFreeCommandBuffers(logicalDevice.vkDevice, vkCommandPool.handle, commandBuffer)
        }
    }

    override fun freeResources() {
        logicalDevice.destroyCommandPool(vkCommandPool)
        logD { "Destroyed command pool" }
    }

    fun allocateCommandBuffers(
        numBuffers: Int,
        level: Int = VK_COMMAND_BUFFER_LEVEL_PRIMARY,
        stack: MemoryStack? = null
    ): List<VkCommandBuffer> {
        memStack(stack) {
            val allocateInfo = callocVkCommandBufferAllocateInfo {
                commandPool(vkCommandPool.handle)
                level(level)
                commandBufferCount(numBuffers)
            }
            val handles = mallocPointer(numBuffers)
            checkVk(vkAllocateCommandBuffers(logicalDevice.vkDevice, allocateInfo, handles)) { "Failed creating command buffers" }
            return buildList {
                for (i in 0 until numBuffers) {
                    add(VkCommandBuffer(handles[i], logicalDevice.vkDevice))
                }
            }
        }
    }

}