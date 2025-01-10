package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.*
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_CPU_ONLY
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_GPU_ONLY
import org.lwjgl.vulkan.VK10.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Buffer(
    val backend: RenderBackendVk,
    bufferSize: Long,
    val usage: Int,
    val allocUsage: Int,
    label: String = UniqueId.nextId("VkBuffer")
) : BaseReleasable() {

    val vkBuffer: VkBuffer
    val bufferSize: Long get() = vkBuffer.bufferSize

    private val allocInfo = BufferInfo(label, "<none>")

    init {
        memStack {
            val bufferInfo = callocVkBufferCreateInfo {
                sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                size(bufferSize)
                usage(usage)
                sharingMode(VK_SHARING_MODE_EXCLUSIVE)
            }
            vkBuffer = backend.memManager.createBuffer(bufferInfo, allocUsage)
            allocInfo.allocated(bufferSize)
        }
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

class VertexBuffer(
    val backend: RenderBackendVk,
    val label: String,
    size: Long,
    val usage: Int = VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
    val allocUsage: Int = VMA_MEMORY_USAGE_GPU_ONLY
) {
    var size: Long = size
        private set
    var buffer: Buffer = makeBuffer(size)

    fun writeData(data: Float32Buffer) {
        val bufSize = data.limit * 4L
        checkSize(bufSize)

        val stagingBuffer = Buffer(backend, bufSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VMA_MEMORY_USAGE_CPU_ONLY)
        stagingBuffer.mappedFloats { floats ->
            data.useRaw { floats.put(it) }
        }
        buffer.copyFrom(stagingBuffer)
    }

    fun writeData(data: Int32Buffer) {
        val bufSize = data.limit * 4L
        checkSize(bufSize)

        val stagingBuffer = Buffer(backend, bufSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VMA_MEMORY_USAGE_CPU_ONLY)
        stagingBuffer.mappedInts { ints ->
            data.useRaw { ints.put(it) }
        }
        buffer.copyFrom(stagingBuffer)
    }

    private fun checkSize(required: Long) {
        if (required > size) {
            buffer.release()
            size = required
            buffer = makeBuffer(required)
        }
    }

    private fun makeBuffer(size: Long) = Buffer(backend, size, usage, allocUsage, label)
}