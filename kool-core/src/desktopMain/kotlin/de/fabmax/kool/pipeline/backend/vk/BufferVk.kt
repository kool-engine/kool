package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.useRaw
import org.lwjgl.vulkan.VK10.vkCmdCopyBuffer
import org.lwjgl.vulkan.VkCommandBuffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class BufferVk(
    val backend: RenderBackendVk,
    bufferInfo: MemoryInfo
) : BaseReleasable() {

    val vkBuffer: VkBuffer = backend.memManager.createBuffer(bufferInfo)
    val bufferSize: Long get() = vkBuffer.bufferSize

    private val allocInfo = BufferInfo(bufferInfo.label, "<none>").apply { allocated(bufferSize) }

    inline fun mappedBytes(block: (ByteBuffer) -> Unit) = backend.memManager.mappedBytes(vkBuffer, block)
    inline fun mappedFloats(block: (FloatBuffer) -> Unit) = backend.memManager.mappedFloats(vkBuffer, block)
    inline fun mappedInts(block: (IntBuffer) -> Unit) = backend.memManager.mappedInts(vkBuffer, block)

    fun copyFrom(srcBuffer: VkBuffer, commandBuffer: VkCommandBuffer, size: Long = srcBuffer.bufferSize) {
        backend.commandPool.singleShotCommands { commandBuffer ->
            val copyRegion = callocVkBufferCopyN(1) { size(size) }
            vkCmdCopyBuffer(commandBuffer, srcBuffer.handle, vkBuffer.handle, copyRegion)
        }
    }

    override fun release() {
        val wasReleased = isReleased
        super.release()
        if (!wasReleased) {
            backend.memManager.freeBuffer(vkBuffer)
            allocInfo.deleted()
        }
    }
}

class GrowingBufferVk(
    val backend: RenderBackendVk,
    bufferInfo: MemoryInfo
) : BaseReleasable() {

    var bufferInfo = bufferInfo
        private set
    val size: Long get() = bufferInfo.size
    var buffer: BufferVk = makeBuffer(bufferInfo)

    fun writeData(data: Float32Buffer, commandBuffer: VkCommandBuffer) {
        val bufSize = data.limit * 4L
        checkSize(bufSize)

        backend.memManager.stagingBuffer(bufSize) { stagingBuf ->
            data.useRaw { stagingBuf.mapped!!.asFloatBuffer().put(it) }
            buffer.copyFrom(stagingBuf, commandBuffer)
        }
    }

    fun writeData(data: Int32Buffer, commandBuffer: VkCommandBuffer) {
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

    private fun makeBuffer(bufferInfo: MemoryInfo) = BufferVk(backend, bufferInfo)

    override fun release() {
        super.release()
        buffer.release()
    }
}