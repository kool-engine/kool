package de.fabmax.kool.platform.vk

import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class CommandBuffers(val commandPool: CommandPool, nBuffers: Int) : VkResource() {

    val vkCommandBuffers: List<VkCommandBuffer>

    private var index = 0

    init {
        memStack {
            val cmdBuffers = mallocPointer(nBuffers)
            val allocInfo = callocVkCommandBufferAllocateInfo {
                sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                commandPool(commandPool.vkCommandPool)
                level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                commandBufferCount(cmdBuffers.capacity())
            }

            checkVk(vkAllocateCommandBuffers(commandPool.sys.device.vkDevice, allocInfo, cmdBuffers))
            val commandBuffers = mutableListOf<VkCommandBuffer>()
            for (i in 0 until nBuffers) {
                commandBuffers += VkCommandBuffer(cmdBuffers[i], commandPool.sys.device.vkDevice)
            }
            vkCommandBuffers = commandBuffers
        }
    }

    fun nextCommandBuffer(): VkCommandBuffer {
        return if (index < vkCommandBuffers.size) {
            vkCommandBuffers[index++]
        } else {
            throw IllegalStateException("Command buffer pool exhausted")
        }
    }

    override fun freeResources() {
        vkCommandBuffers.forEach {
            vkFreeCommandBuffers(commandPool.sys.device.vkDevice, commandPool.vkCommandPool, it)
        }
    }
}