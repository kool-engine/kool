package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.UniqueId
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.VK10.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Buffer(val backend: RenderBackendVk, val bufferSize: Long, val usage: Int, val allocUsage: Int) : BaseReleasable() {
    val vkBuffer: Long
    val allocation: Long

    private val allocInfo = BufferInfo(UniqueId.nextId("VkBuffer"), "<none>")

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
            checkVk(backend.memManager.createBuffer(bufferInfo, allocUsage, pBuffer, pAllocation))
            vkBuffer = pBuffer[0]
            allocation = pAllocation[0]

            allocInfo.allocated(bufferSize)
        }
    }

    inline fun mapped(block: ByteBuffer.() -> Unit) {
        val addr = backend.memManager.mapMemory(allocation)
        MemoryUtil.memByteBuffer(addr, bufferSize.toInt()).block()
        backend.memManager.unmapMemory(allocation)
    }

    inline fun mappedFloats(block: FloatBuffer.() -> Unit) {
        val addr = backend.memManager.mapMemory(allocation)
        MemoryUtil.memFloatBuffer(addr, bufferSize.toInt()).block()
        backend.memManager.unmapMemory(allocation)
    }

    inline fun mappedInts(block: IntBuffer.() -> Unit) {
        val addr = backend.memManager.mapMemory(allocation)
        MemoryUtil.memIntBuffer(addr, bufferSize.toInt()).block()
        backend.memManager.unmapMemory(allocation)
    }

    fun put(srcBuffer: Buffer) {
        backend.transferCommandPool.singleShotCommands { commandBuffer ->
            val copyRegion = callocVkBufferCopyN(1) { size(srcBuffer.bufferSize) }
            vkCmdCopyBuffer(commandBuffer, srcBuffer.vkBuffer, vkBuffer, copyRegion)
        }
    }

    override fun release() {
        super.release()
        backend.memManager.freeBuffer(vkBuffer, allocation)
        allocInfo.deleted()
    }
}