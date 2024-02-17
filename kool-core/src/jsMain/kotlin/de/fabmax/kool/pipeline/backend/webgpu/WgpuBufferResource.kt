package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.backend.GpuBuffer
import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.BaseReleasable

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