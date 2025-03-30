package de.fabmax.kool.util

import de.fabmax.kool.pipeline.BufferUsage
import de.fabmax.kool.pipeline.GpuBuffer

class StructBuffer<T: Struct>(val capacity: Int, val struct: T) {
    @PublishedApi
    internal val bufferAccess: StructBufferAccessIndexed = struct.viewBuffer(this)

    val strideBytes: Int = struct.structSize
    val buffer = MixedBuffer(capacity * strideBytes)

    @PublishedApi
    internal var position = 0
    val size: Int get() = position

    init {
        require(struct.layout != MemoryLayout.DontCare) {
            "StructBuffer requires the memory layout of the struct to be other than MemoryLayout.DontCare"
        }
    }

    fun clear() {
        position = 0
    }

    operator fun get(index: Int) = struct.also { bufferAccess.index = index }

    inline fun set(index: Int, block: T.() -> Unit) {
        check(index < capacity) { "StructBuffer capacity exceeded" }
        bufferAccess.index = index
        struct.block()
    }

    inline fun put(block: T.() -> Unit): Int {
        check(position < capacity) { "StructBuffer capacity exceeded" }
        val index = position++
        bufferAccess.index = index
        struct.block()
        return index
    }

    inline fun forEach(block: T.() -> Unit) {
        bufferAccess.index = 0
        for (i in 0 until position) {
            bufferAccess.index = i
            struct.block()
        }
    }
}

fun <T: Struct> T.viewBuffer(buffer: StructBuffer<T>): StructBufferAccessIndexed {
    val accessor = StructBufferAccessIndexed(buffer)
    setupBufferAccess(accessor)
    return accessor
}

fun StructBuffer<*>.asStorageBuffer(): GpuBuffer = asGpuBuffer(BufferUsage.makeUsage(storage = true))

fun StructBuffer<*>.asGpuBuffer(usage: BufferUsage): GpuBuffer {
    val buffer = GpuBuffer(struct.type, usage, capacity)
    buffer.uploadData(this)
    return buffer
}
