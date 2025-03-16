package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.GpuBufferImpl
import de.fabmax.kool.pipeline.backend.stats.BufferInfo
import de.fabmax.kool.util.*

class GpuBufferGl(val target: Int, val backend: RenderBackendGl, creationInfo: BufferCreationInfo) :
    BaseReleasable(), GpuBufferImpl
{

    private val gl: GlApi = backend.gl

    val bufferId = nextBufferId++
    val buffer = gl.createBuffer()

    private val resInfo = BufferInfo(creationInfo.bufferName, "scene: ${creationInfo.sceneName}, render-pass: ${creationInfo.renderPassName}")

    fun bind() {
        gl.bindBuffer(target, buffer)
    }

    fun setData(size: Int, usage: Int) {
        bind()
        gl.bufferData(target, size, usage)
        resInfo.allocated(size.toLong())
    }

    fun setData(data: Uint8Buffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
        resInfo.allocated(data.limit.toLong())
    }

    fun setData(data: Uint16Buffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
        resInfo.allocated(data.limit * 2L)
    }

    fun setData(data: Int32Buffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
        resInfo.allocated(data.limit * 4L)
    }

    fun setData(data: Float32Buffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
        resInfo.allocated(data.limit * 4L)
    }

    fun setData(data: MixedBuffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
        resInfo.allocated(data.limit.toLong())
    }

    fun unbind() {
        gl.bindBuffer(target, gl.NULL_BUFFER)
    }

    override fun release() {
        super.release()
        gl.deleteBuffer(buffer)
        resInfo.deleted()
    }

    companion object {
        private var nextBufferId = 1L
    }
}

fun BufferCreationInfo(cmd: DrawCommand): BufferCreationInfo {
    return BufferCreationInfo(
        bufferName = cmd.mesh.name,
        renderPassName = cmd.queue.renderPass.name,
        sceneName = cmd.queue.renderPass.parentScene?.name ?: "scene:<null>"
    )
}

data class BufferCreationInfo(
    val bufferName: String,
    val renderPassName: String,
    val sceneName: String
)
