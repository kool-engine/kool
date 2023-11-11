package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.util.*

class BufferResource(val target: Int, val backend: RenderBackendGl) {

    private val gl: GlApi = backend.gl

    val bufferId = nextBufferId++
    val buffer = gl.createBuffer()

    fun delete() {
//        ctx.engineStats.bufferDeleted(bufferId)
        gl.deleteBuffer(buffer)
    }

    fun bind() {
        gl.bindBuffer(target, buffer)
    }

    fun setData(data: Uint8Buffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
//        ctx.engineStats.bufferDeleted(bufferId)
//        data.useRaw {
//            glBufferData(target, it, usage)
//        }
//        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
    }

    fun setData(data: Uint16Buffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
//        ctx.engineStats.bufferDeleted(bufferId)
//        data.useRaw {
//            glBufferData(target, it, usage)
//        }
//        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 2)
    }

    fun setData(data: Int32Buffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
//        ctx.engineStats.bufferDeleted(bufferId)
//        data.useRaw {
//            glBufferData(target, it, usage)
//        }
//        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
    }

    fun setData(data: Float32Buffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
//        ctx.engineStats.bufferDeleted(bufferId)
//        data.useRaw {
//            glBufferData(target, it, usage)
//        }
//        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
    }

    fun setData(data: MixedBuffer, usage: Int) {
        bind()
        gl.bufferData(target, data, usage)
//        ctx.engineStats.bufferDeleted(bufferId)
//        data.useRaw {
//            glBufferData(target, it, usage)
//        }
//        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
    }

    fun unbind() {
        gl.bindBuffer(target, gl.NULL_BUFFER)
    }

    companion object {
        private var nextBufferId = 1L
    }
}