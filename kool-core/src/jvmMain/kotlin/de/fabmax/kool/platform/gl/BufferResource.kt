package de.fabmax.kool.platform.gl

import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.*
import org.lwjgl.opengl.GL15.*

class BufferResource(val target: Int) {

    val bufferId = nextBufferId++
    val buffer = glGenBuffers()

    fun delete(ctx: Lwjgl3Context) {
        ctx.engineStats.bufferDeleted(bufferId)
        glDeleteBuffers(buffer)
    }

    fun bind() {
        glBindBuffer(target, buffer)
    }

    fun setData(data: Float32Buffer, usage: Int, ctx: Lwjgl3Context) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        glBufferData(target, (data as Float32BufferImpl).buffer, usage)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint8Buffer, usage: Int, ctx: Lwjgl3Context) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        glBufferData(target, (data as Uint8BufferImpl).buffer, usage)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: MixedBuffer, usage: Int, ctx: Lwjgl3Context) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        glBufferData(target, (data as MixedBufferImpl).buffer, usage)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint16Buffer, usage: Int, ctx: Lwjgl3Context) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        glBufferData(target, (data as Uint16BufferImpl).buffer, usage)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 2)
        data.limit = limit
        data.position = pos
    }

    fun setData(data: Uint32Buffer, usage: Int, ctx: Lwjgl3Context) {
        val limit = data.limit
        val pos = data.position
        data.flip()
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        glBufferData(target, (data as Uint32BufferImpl).buffer, usage)
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
        data.limit = limit
        data.position = pos
    }

    fun unbind() {
        glBindBuffer(target, GL_NONE)
    }

    companion object {
        private var nextBufferId = 1L
    }
}