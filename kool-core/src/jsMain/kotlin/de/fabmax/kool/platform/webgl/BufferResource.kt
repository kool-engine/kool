package de.fabmax.kool.platform.webgl

import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.*

class BufferResource(val target: Int, ctx: JsContext) {

    val bufferId = nextBufferId++
    val buffer = ctx.gl.createBuffer()

    fun delete(ctx: JsContext) {
        ctx.engineStats.bufferDeleted(bufferId)
        ctx.gl.deleteBuffer(buffer)
    }

    fun bind(ctx: JsContext) {
        ctx.gl.bindBuffer(target, buffer)
    }

    fun setData(data: Float32Buffer, usage: Int, length: Int, ctx: JsContext) {
        // todo: take length from (used) buffer size?
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        ctx.gl.bufferData(target, (data as Float32BufferImpl).buffer, usage, 0, length)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
    }

    fun setData(data: Uint8Buffer, usage: Int, length: Int, ctx: JsContext) {
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        ctx.gl.bufferData(target, (data as Uint8BufferImpl).buffer, usage, 0, length)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
    }

    fun setData(data: MixedBuffer, usage: Int, length: Int, ctx: JsContext) {
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        ctx.gl.bufferData(target, (data as MixedBufferImpl).buffer, usage, 0, length)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
    }

    fun setData(data: Uint16Buffer, usage: Int, length: Int, ctx: JsContext) {
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        ctx.gl.bufferData(target, (data as Uint16BufferImpl).buffer, usage, 0, length)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 2)
    }

    fun setData(data: Int32Buffer, usage: Int, length: Int, ctx: JsContext) {
        bind(ctx)
        ctx.engineStats.bufferDeleted(bufferId)
        ctx.gl.bufferData(target, (data as Int32BufferImpl).buffer, usage, 0, length)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
    }

    fun unbind(ctx: JsContext) {
        ctx.gl.bindBuffer(target, null)
    }

    companion object {
        private var nextBufferId = 1L
    }
}