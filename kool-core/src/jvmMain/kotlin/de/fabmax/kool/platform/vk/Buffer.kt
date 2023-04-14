package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.VK10.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Buffer(val sys: VkSystem, val bufferSize: Long, val usage: Int, val allocUsage: Int) : VkResource() {
    val vkBuffer: Long
    val allocation: Long

    init {
        memStack {
            val bufferInfo = callocVkBufferCreateInfo {
                sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                size(bufferSize)
                usage(usage)
                sharingMode(VK_SHARING_MODE_EXCLUSIVE)
            }

            val pBuffer = mallocLong(1)
            val pAllocation = mallocPointer(1)
            checkVk(sys.memManager.createBuffer(bufferInfo, allocUsage, pBuffer, pAllocation))
            vkBuffer = pBuffer[0]
            allocation = pAllocation[0]

            sys.ctx.engineStats.bufferAllocated(vkBuffer, bufferSize.toInt())
        }
    }

    inline fun mapped(block: ByteBuffer.() -> Unit) {
        val addr = sys.memManager.mapMemory(allocation)
        MemoryUtil.memByteBuffer(addr, bufferSize.toInt()).block()
        sys.memManager.unmapMemory(allocation)
    }

    inline fun mappedFloats(block: FloatBuffer.() -> Unit) {
        val addr = sys.memManager.mapMemory(allocation)
        MemoryUtil.memFloatBuffer(addr, bufferSize.toInt()).block()
        sys.memManager.unmapMemory(allocation)
    }

    inline fun mappedInts(block: IntBuffer.() -> Unit) {
        val addr = sys.memManager.mapMemory(allocation)
        MemoryUtil.memIntBuffer(addr, bufferSize.toInt()).block()
        sys.memManager.unmapMemory(allocation)
    }

    fun put(srcBuffer: Buffer) {
        sys.transferCommandPool.singleTimeCommands { commandBuffer ->
            val copyRegion = callocVkBufferCopyN(1) { size(srcBuffer.bufferSize) }
            vkCmdCopyBuffer(commandBuffer, srcBuffer.vkBuffer, vkBuffer, copyRegion)
        }
    }

    override fun freeResources() {
        sys.memManager.freeBuffer(vkBuffer, allocation)
        sys.ctx.engineStats.bufferDeleted(vkBuffer)
    }
}