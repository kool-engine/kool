package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.GpuBufferImpl
import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.*
import io.ygdrasil.webgpu.BufferDescriptor
import io.ygdrasil.webgpu.GPUBuffer
import io.ygdrasil.webgpu.GPUBufferUsage
import io.ygdrasil.webgpu.GPUDevice

class GpuBufferWgpu(val buffer: GPUBuffer, size: Long, info: String?) :
    BaseReleasable(), GpuBufferImpl
{

    private val bufferInfo = BufferInfo(buffer.label, info ?: "<none>").apply {
        allocated(size)
    }

    override fun release() {
        super.release()
        buffer.close()
        bufferInfo.deleted()
    }
}

internal class WgpuGrowingBuffer(
    val backend: GPUBackend,
    val label: String,
    size: Long,
    val usage: Set<GPUBufferUsage> = setOf(GPUBufferUsage.Vertex, GPUBufferUsage.CopyDst)
) : BaseReleasable() {
    private val device: GPUDevice get() = backend.device

    var size: Long = size
        private set
    var buffer: GpuBufferWgpu = makeBuffer(size)
        private set

    fun writeData(data: Float32Buffer) {
        checkSize(data.limit * 4L)
        data.asArrayBuffer { arrayBuffer ->
            device.queue.writeBuffer(
                buffer = buffer.buffer,
                bufferOffset = 0uL,
                data = arrayBuffer,
                dataOffset = 0uL,
                size = (data.limit * 4L).toULong()
            )
        }
    }

    fun writeData(data: Int32Buffer) {
        checkSize(data.limit * 4L)
        data.asArrayBuffer { arrayBuffer ->
            device.queue.writeBuffer(
                buffer = buffer.buffer,
                bufferOffset = 0uL,
                data = arrayBuffer,
                dataOffset = 0uL,
                size = (data.limit * 4L).toULong()
            )
        }
    }

    private fun checkSize(required: Long) {
        if (required > size) {
            buffer.release()
            size = required
            buffer = makeBuffer(required)
        }
    }

    private fun makeBuffer(size: Long) = backend.createBuffer(
        BufferDescriptor(
            label = label,
            size = size.toULong(),
            usage = usage
        ),
        label
    )
}