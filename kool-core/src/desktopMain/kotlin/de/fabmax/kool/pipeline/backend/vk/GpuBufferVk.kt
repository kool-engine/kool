package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.GpuBufferImpl
import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.useRaw
import org.lwjgl.vulkan.VK10.vkCmdCopyBuffer
import org.lwjgl.vulkan.VkBufferCopy
import org.lwjgl.vulkan.VkCommandBuffer

class GpuBufferVk(
    val backend: RenderBackendVk,
    bufferInfo: MemoryInfo
) : BaseReleasable(), GpuBufferImpl {

    val vkBuffer: VkBuffer = backend.memManager.createBuffer(bufferInfo)
    val bufferSize: Long get() = vkBuffer.bufferSize

    private val allocInfo = BufferInfo(bufferInfo.label, "<none>").apply { allocated(bufferSize) }

    fun copyFrom(srcBuffer: VkBuffer, commandBuffer: VkCommandBuffer, size: Long = srcBuffer.bufferSize) {
        bufferCopy[0].set(0L, 0L, size)
        vkCmdCopyBuffer(commandBuffer, srcBuffer.handle, vkBuffer.handle, bufferCopy)
    }

    override fun release() {
        val wasReleased = isReleased
        super.release()
        if (!wasReleased) {
            backend.memManager.freeBuffer(vkBuffer)
            allocInfo.deleted()
        }
    }

    companion object {
        private val bufferCopy = VkBufferCopy.malloc(1)
    }
}

class GrowingBufferVk(
    val backend: RenderBackendVk,
    bufferInfo: MemoryInfo
) : BaseReleasable() {

    var bufferInfo = bufferInfo
        private set
    val size: Long get() = bufferInfo.size
    var buffer: GpuBufferVk = makeBuffer(bufferInfo)

    fun writeData(data: Float32Buffer, commandBuffer: VkCommandBuffer) {
        if (data.limit == 0) return

        val bufSize = data.limit * 4L
        checkSize(bufSize)
        backend.memManager.stagingBuffer(bufSize) { stagingBuf ->
            data.useRaw { stagingBuf.mapped!!.asFloatBuffer().put(it) }
            buffer.copyFrom(stagingBuf, commandBuffer)
        }
    }

    fun writeData(data: Int32Buffer, commandBuffer: VkCommandBuffer) {
        if (data.limit == 0) return

        val bufSize = data.limit * 4L
        checkSize(bufSize)
        backend.memManager.stagingBuffer(bufSize) { stagingBuf ->
            data.useRaw { stagingBuf.mapped!!.asIntBuffer().put(it) }
            buffer.copyFrom(stagingBuf, commandBuffer)
        }
    }

    private fun checkSize(required: Long) {
        if (required > size) {
            buffer.release()
            bufferInfo = bufferInfo.copy(size = required)
            buffer = makeBuffer(bufferInfo)
        }
    }

    private fun makeBuffer(bufferInfo: MemoryInfo) = GpuBufferVk(backend, bufferInfo)

    override fun release() {
        super.release()
        buffer.release()
    }
}