package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.backend.GpuBuffer
import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.*

class WgpuBufferResource(val buffer: GPUBuffer, size: Long, info: String?) :
    BaseReleasable(), GpuBuffer
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

internal class WgpuVertexBuffer(
    val backend: RenderBackendWebGpu,
    val label: String,
    var size: Int,
    val usage: Int = GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
) {
    private val device: GPUDevice get() = backend.device

    var buffer: WgpuBufferResource = makeBuffer()

    fun writeData(data: Float32Buffer) {
        checkSize(data.limit * 4)
        device.queue.writeBuffer(
            buffer = buffer.buffer,
            bufferOffset = 0L,
            data = (data as Float32BufferImpl).buffer,
            dataOffset = 0L,
            size = data.limit.toLong()
        )
    }

    fun writeData(data: Int32Buffer) {
        checkSize(data.limit * 4)
        device.queue.writeBuffer(
            buffer = buffer.buffer,
            bufferOffset = 0L,
            data = (data as Int32BufferImpl).buffer,
            dataOffset = 0L,
            size = data.limit.toLong()
        )
    }

    private fun checkSize(required: Int) {
        if (required > size) {
            buffer.release()
            size = required
            buffer = makeBuffer()
        }
    }

    private fun makeBuffer() = backend.createBuffer(
        GPUBufferDescriptor(
            label = label,
            size = size.toLong(),
            usage = usage
        ),
        label
    )
}