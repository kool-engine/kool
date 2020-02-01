package de.fabmax.kool.platform.webgl

import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.*

class BufferResource(val target: Int, ctx: JsContext) {

    val buffer = ctx.gl.createBuffer()

    fun delete(ctx: JsContext) {
        ctx.gl.deleteBuffer(buffer)
    }

    fun bind(ctx: JsContext) {
        ctx.gl.bindBuffer(target, buffer)
    }

    fun setData(data: Float32Buffer, usage: Int, ctx: JsContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        ctx.gl.bufferData(target, (data as Float32BufferImpl).buffer, usage)
        //ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint8Buffer, usage: Int, ctx: JsContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        ctx.gl.bufferData(target, (data as Uint8BufferImpl).buffer, usage)
        //ctx.memoryMgr.memoryAllocated(this, pos)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint16Buffer, usage: Int, ctx: JsContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        ctx.gl.bufferData(target, (data as Uint16BufferImpl).buffer, usage)
        //ctx.memoryMgr.memoryAllocated(this, pos * 2)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint32Buffer, usage: Int, ctx: JsContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        ctx.gl.bufferData(target, (data as Uint32BufferImpl).buffer, usage)
        //ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun unbind(ctx: JsContext) {
        ctx.gl.bindBuffer(target, null)
    }
}