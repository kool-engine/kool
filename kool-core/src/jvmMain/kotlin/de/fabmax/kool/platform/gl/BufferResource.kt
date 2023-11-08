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
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        data.useRaw {
            glBufferData(target, it, usage)
        }
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
    }

    fun setData(data: Uint8Buffer, usage: Int, ctx: Lwjgl3Context) {
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        data.useRaw {
            glBufferData(target, it, usage)
        }
        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
    }

    fun setData(data: MixedBuffer, usage: Int, ctx: Lwjgl3Context) {
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        data.useRaw {
            glBufferData(target, it, usage)
        }
        ctx.engineStats.bufferAllocated(bufferId, data.capacity)
    }

    fun setData(data: Uint16Buffer, usage: Int, ctx: Lwjgl3Context) {
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        data.useRaw {
            glBufferData(target, it, usage)
        }
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 2)
    }

    fun setData(data: Int32Buffer, usage: Int, ctx: Lwjgl3Context) {
        bind()
        ctx.engineStats.bufferDeleted(bufferId)
        data.useRaw {
            glBufferData(target, it, usage)
        }
        ctx.engineStats.bufferAllocated(bufferId, data.capacity * 4)
    }

    fun unbind() {
        glBindBuffer(target, GL_NONE)
    }

    companion object {
        private var nextBufferId = 1L
    }
}