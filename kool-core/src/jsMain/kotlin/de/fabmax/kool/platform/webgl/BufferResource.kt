package de.fabmax.kool.platform.webgl

import de.fabmax.kool.pipeline.backend.gl.GlImpl
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.*

class BufferResource(val target: Int, ctx: JsContext) {

    val bufferId = nextBufferId++
    val buffer = GlImpl.gl.createBuffer()

    fun delete(ctx: JsContext) {
        ctx.engineStats.bufferDeleted(bufferId)
        GlImpl.gl.deleteBuffer(buffer)
    }

    fun bind(ctx: JsContext) {
        GlImpl.gl.bindBuffer(target, buffer)
    }

    fun setData(data: Float32Buffer, usage: Int, ctx: JsContext) {
        data as Float32BufferImpl
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        GlImpl.gl.bufferData(target, data.buffer, usage, 0, data.len)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
    }

    fun setData(data: Uint8Buffer, usage: Int, ctx: JsContext) {
        data as Uint8BufferImpl
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        GlImpl.gl.bufferData(target, data.buffer, usage, 0, data.len)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
    }

    fun setData(data: MixedBuffer, usage: Int, ctx: JsContext) {
        data as MixedBufferImpl
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        GlImpl.gl.bufferData(target, data.buffer, usage, 0, data.len)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
    }

    fun setData(data: Uint16Buffer, usage: Int, ctx: JsContext) {
        data as Uint16BufferImpl
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        GlImpl.gl.bufferData(target, data.buffer, usage, 0, data.len)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 2)
    }

    fun setData(data: Int32Buffer, usage: Int, ctx: JsContext) {
        data as Int32BufferImpl
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        GlImpl.gl.bufferData(target, data.buffer, usage, 0, data.len)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
    }

    fun unbind(ctx: JsContext) {
        GlImpl.gl.bindBuffer(target, null)
    }

    companion object {
        private var nextBufferId = 1L
    }
}