package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.GpuBufferImpl
import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.*

class GpuBufferWgpu(val buffer: GPUBuffer, size: Long, info: String?) :
    BaseReleasable(), GpuBufferImpl
{

    private val bufferInfo = BufferInfo(buffer.label, info ?: "<none>").apply {
        allocated(size)
    }

    override fun release() {
        super.release()
        buffer.destroy()
        bufferInfo.deleted()
    }
}

internal class WgpuGrowingBuffer(
    val backend: RenderBackendWebGpu,
    val label: String,
    size: Long,
    val usage: Int = GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
) : BaseReleasable() {
    private val device: GPUDevice get() = backend.device

    var size: Long = size
        private set
    var buffer: GpuBufferWgpu = makeBuffer(size)
        private set

    fun writeData(data: Float32Buffer) {
        checkSize(data.limit * 4L)
        device.queue.writeBuffer(
            buffer = buffer.buffer,
            bufferOffset = 0L,
            data = (data as Float32BufferImpl).buffer,
            dataOffset = 0L,
            size = data.limit.toLong()
        )
    }

    fun writeData(data: Int32Buffer) {
        checkSize(data.limit * 4L)
        device.queue.writeBuffer(
            buffer = buffer.buffer,
            bufferOffset = 0L,
            data = (data as Int32BufferImpl).buffer,
            dataOffset = 0L,
            size = data.limit.toLong()
        )
    }

    private fun checkSize(required: Long) {
        if (required > size) {
            buffer.release()
            size = required
            buffer = makeBuffer(required)
        }
    }

    private fun makeBuffer(size: Long) = backend.createBuffer(
        GPUBufferDescriptor(
            label = label,
            size = size,
            usage = usage
        ),
        label
    )
}