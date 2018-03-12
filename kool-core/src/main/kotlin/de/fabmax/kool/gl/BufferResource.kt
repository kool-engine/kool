package de.fabmax.kool.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Uint16Buffer
import de.fabmax.kool.util.Uint32Buffer
import de.fabmax.kool.util.Uint8Buffer

class BufferResource private constructor(glRef: Any, val target: Int, ctx: KoolContext) :
        GlResource(glRef, Type.BUFFER, ctx) {

    companion object {
        fun create(target: Int, ctx: KoolContext): BufferResource {
            return BufferResource(glCreateBuffer(), target, ctx)
        }
    }

    override fun delete(ctx: KoolContext) {
        glDeleteBuffer(this)
        super.delete(ctx)
    }

    fun bind(ctx: KoolContext) {
        if (ctx.boundBuffers[target] != this) {
            glBindBuffer(target, this)
            ctx.boundBuffers[target] = this
        }
    }

    fun setData(data: Float32Buffer, usage: Int, ctx: KoolContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        glBufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint8Buffer, usage: Int, ctx: KoolContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        glBufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint16Buffer, usage: Int, ctx: KoolContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        glBufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 2)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint32Buffer, usage: Int, ctx: KoolContext) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind(ctx)
        glBufferData(target, data, usage)
        ctx.memoryMgr.memoryAllocated(this, pos * 4)
        data.limit = limit
        data.position = pos
    }

    fun unbind(ctx: KoolContext) {
        glBindBuffer(target, null)
        ctx.boundBuffers[target] = null
    }
}