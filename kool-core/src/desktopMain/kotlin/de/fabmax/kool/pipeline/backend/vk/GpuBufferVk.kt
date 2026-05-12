package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.GpuBufferImpl
import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.KHRSynchronization2.vkCmdPipelineBarrier2KHR
import org.lwjgl.vulkan.VK10.vkCmdCopyBuffer
import org.lwjgl.vulkan.VK13.*
import org.lwjgl.vulkan.VkBufferCopy
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkMemoryBarrier2

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
        bufferTransferWriteBarrier(commandBuffer)
    }

    override fun doRelease() {
        backend.memManager.freeBuffer(vkBuffer)
        allocInfo.deleted()
    }

    companion object {
        private val bufferCopy = VkBufferCopy.malloc(1)
    }
}

internal fun bufferTransferWriteBarrier(commandBuffer: VkCommandBuffer) = scopedMem {
    val dependency = callocVkDependencyInfo {
        val barrier = VkMemoryBarrier2.calloc(1, this@scopedMem)
        barrier[0].`sType$Default`()
        barrier[0].srcAccessMask(VK_ACCESS_2_TRANSFER_WRITE_BIT)
        barrier[0].srcStageMask(VK_PIPELINE_STAGE_2_ALL_TRANSFER_BIT)
        barrier[0].dstAccessMask(VK_ACCESS_2_MEMORY_READ_BIT or VK_ACCESS_2_MEMORY_WRITE_BIT)
        barrier[0].dstStageMask(VK_PIPELINE_STAGE_2_ALL_COMMANDS_BIT)
        pMemoryBarriers(barrier)
    }
    vkCmdPipelineBarrier2KHR(commandBuffer, dependency)
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
            backend.memManager.flushBuffer(stagingBuf)
            buffer.copyFrom(stagingBuf, commandBuffer)
        }
    }

    fun writeData(data: Int32Buffer, commandBuffer: VkCommandBuffer) {
        if (data.limit == 0) return

        val bufSize = data.limit * 4L
        checkSize(bufSize)
        backend.memManager.stagingBuffer(bufSize) { stagingBuf ->
            data.useRaw { stagingBuf.mapped!!.asIntBuffer().put(it) }
            backend.memManager.flushBuffer(stagingBuf)
            buffer.copyFrom(stagingBuf, commandBuffer)
        }
    }

    fun writeData(data: MixedBuffer, commandBuffer: VkCommandBuffer) {
        if (data.limit == 0) return

        val bufSize = data.limit * 4L
        checkSize(bufSize)
        backend.memManager.stagingBuffer(bufSize) { stagingBuf ->
            data.useRaw { stagingBuf.mapped!!.put(it) }
            backend.memManager.flushBuffer(stagingBuf)
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

    override fun doRelease() {
        buffer.release()
    }
}