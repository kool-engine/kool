package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT
import org.lwjgl.vulkan.VK10.vkCmdCopyBuffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Buffer(
    val backend: RenderBackendVk,
    bufferInfo: MemoryInfo,
    label: String = UniqueId.nextId("VkBuffer")
) : BaseReleasable() {

    val vkBuffer: VkBuffer
    val bufferSize: Long get() = vkBuffer.bufferSize

    private val allocInfo = BufferInfo(label, "<none>")

    init {
        vkBuffer = backend.memManager.createBuffer(bufferInfo)
        allocInfo.allocated(bufferSize)
    }

    inline fun mappedBytes(block: (ByteBuffer) -> Unit) = backend.memManager.mappedBytes(vkBuffer, block)
    inline fun mappedFloats(block: (FloatBuffer) -> Unit) = backend.memManager.mappedFloats(vkBuffer, block)
    inline fun mappedInts(block: (IntBuffer) -> Unit) = backend.memManager.mappedInts(vkBuffer, block)

    fun copyFrom(srcBuffer: Buffer) {
        backend.transferCommandPool.singleShotCommands { commandBuffer ->
            val copyRegion = callocVkBufferCopyN(1) { size(srcBuffer.bufferSize) }
            vkCmdCopyBuffer(commandBuffer, srcBuffer.vkBuffer.handle, vkBuffer.handle, copyRegion)
        }
    }

    override fun release() {
        super.release()
        backend.memManager.freeBuffer(vkBuffer)
        allocInfo.deleted()
    }
}

class GrowingBuffer(
    val backend: RenderBackendVk,
    bufferInfo: MemoryInfo,
    val label: String
) {
    var bufferInfo = bufferInfo
        private set
    val size: Long get() = bufferInfo.size
    var buffer: Buffer = makeBuffer(bufferInfo)

    fun writeData(data: Float32Buffer) {
        val bufSize = data.limit * 4L
        checkSize(bufSize)

        val stagingInfo = MemoryInfo(bufSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, createMapped = true)
        val stagingBuffer = Buffer(backend, stagingInfo, "vertex staging float buffer")
        stagingBuffer.mappedFloats { floats ->
            data.useRaw { floats.put(it) }
        }
        buffer.copyFrom(stagingBuffer)
        stagingBuffer.release()
    }

    fun writeData(data: Int32Buffer) {
        val bufSize = data.limit * 4L
        checkSize(bufSize)

        val stagingInfo = MemoryInfo(bufSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, createMapped = true)
        val stagingBuffer = Buffer(backend, stagingInfo, "vertex staging int buffer")
        stagingBuffer.mappedInts { ints ->
            data.useRaw { ints.put(it) }
        }
        buffer.copyFrom(stagingBuffer)
        stagingBuffer.release()
    }

    private fun checkSize(required: Long) {
        if (required > size) {
            buffer.release()
            bufferInfo = bufferInfo.copy(size = required)
            buffer = makeBuffer(bufferInfo)
        }
    }

    private fun makeBuffer(bufferInfo: MemoryInfo) = Buffer(backend, bufferInfo, label)
}