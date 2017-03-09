package de.fabmax.kool.gl

import de.fabmax.kool.platform.*

class BufferResource private constructor(glRef: Any, val target: Int, ctx: RenderContext) :
        GlResource(glRef, Type.BUFFER) {

    companion object {
        fun create(target: Int, ctx: RenderContext): BufferResource {
            return BufferResource(GL.createBuffer(), target, ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        GL.deleteBuffer(this)
        super.delete(ctx)
    }

    fun bind(ctx: RenderContext) {
        if (ctx.boundBuffers[target] != this) {
            GL.bindBuffer(target, this)
            ctx.boundBuffers[target] = this
        }
    }

    fun setData(data: Float32Buffer, usage: Int, ctx: RenderContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        GL.bufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint8Buffer, usage: Int, ctx: RenderContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        GL.bufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint16Buffer, usage: Int, ctx: RenderContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        GL.bufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 2)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint32Buffer, usage: Int, ctx: RenderContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        GL.bufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun unbind(ctx: RenderContext) {
        GL.bindBuffer(target, null)
        ctx.boundBuffers[target] = null
    }
}