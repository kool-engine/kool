package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class CommandBuffers(val commandPool: CommandPool, nBuffers: Int) : VkResource() {

    val vkCommandBuffers: List<VkCommandBuffer>

    private var index = 0

    init {
        memStack {
            val cmdBuffers = mallocPointer(nBuffers)
            val allocInfo = callocVkCommandBufferAllocateInfo {
                commandPool(commandPool.vkCommandPool)
                level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                commandBufferCount(cmdBuffers.capacity())
            }

            checkVk(vkAllocateCommandBuffers(commandPool.logicalDevice.vkDevice, allocInfo, cmdBuffers))
            vkCommandBuffers = buildList {
                for (i in 0 until nBuffers) {
                    add(VkCommandBuffer(cmdBuffers[i], commandPool.logicalDevice.vkDevice))
                }
            }
        }
    }

    fun nextCommandBuffer(): VkCommandBuffer {
        return vkCommandBuffers.getOrNull(index) ?: error("Command buffer pool exhausted")
    }

    override fun freeResources() {
        vkCommandBuffers.forEach {
            vkFreeCommandBuffers(commandPool.logicalDevice.vkDevice, commandPool.vkCommandPool, it)
        }
    }
}