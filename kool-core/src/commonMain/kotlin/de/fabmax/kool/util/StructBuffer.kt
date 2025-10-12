package de.fabmax.kool.util

import de.fabmax.kool.pipeline.BufferUsage
import de.fabmax.kool.pipeline.GpuBuffer
import kotlin.math.max

class StructBuffer<T: Struct>(
    val struct: T,
    val capacity: Int,
) {
    val strideBytes: Int = struct.structSize
    val buffer = MixedBuffer(capacity * strideBytes).also { it.limit = 0 }

    @PublishedApi
    internal val defaultView: MutableStructBufferView<T> = MutableStructBufferView(buffer, 0, strideBytes, capacity)

    var limit = 0
        set(value) {
            field = value
            buffer.limit = value * strideBytes
        }

    val remaining: Int get() = capacity - limit

    fun clear() {
        buffer.clear()
        limit = 0
    }

    inline fun <R> get(index: Int, block: StructBufferView<T>.(T) -> R): R {
        require(index >= 0 && index < capacity) { "Out-of-bounds index: $index, capacity: $capacity" }
        defaultView.index = index
        return defaultView.block(struct)
    }

    inline fun set(index: Int, block: MutableStructBufferView<T>.(T) -> Unit) {
        require(index >= 0 && index < capacity) { "Out-of-bounds index: $index, capacity: $capacity" }
        limit = max(index + 1, limit)
        defaultView.index = index
        defaultView.block(struct)
    }

    fun set(dstIndex: Int, srcIndex: Int, src: StructBuffer<*>) {
        check(struct.hash == src.struct.hash) { "Can only put buffers with matching structs" }
        if (strideBytes == 0) {
            return
        }
        val srcOffset = srcIndex * strideBytes
        val dstOffset = dstIndex * strideBytes
        if (strideBytes % 4 == 0) {
            for (i in 0 until strideBytes step 4) {
                buffer.setInt32(dstOffset + i, src.buffer.getInt32(srcOffset + i))
            }
        } else {
            for (i in 0 until strideBytes) {
                buffer.setInt8(dstOffset + i, src.buffer.getInt8(srcOffset + i))
            }
        }
    }

    inline fun put(block: MutableStructBufferView<T>.(T) -> Unit): Int {
        check(limit < capacity) { "StructBuffer capacity exceeded, capacity: $capacity" }
        val index = limit++
        defaultView.index = index
        defaultView.block(struct)
        return index
    }

    fun put(other: StructBuffer<*>) {
        check(struct.hash == other.struct.hash) { "Can only put buffers with matching structs" }
        if (strideBytes == 0) {
            return
        }
        check(remaining >= other.limit) { "Insufficient size: $remaining < ${other.limit}" }
        check(other.limit == other.buffer.limit / other.strideBytes) {
            "Buffer limit mismatch: ${other.limit} != ${other.buffer.limit / other.strideBytes} (${other.buffer.limit})"
        }

        buffer.position = limit * strideBytes
        limit += other.limit
        buffer.put(other.buffer)
        buffer.position = 0
    }

    fun view(index: Int = 0): StructBufferView<T> = mutableView(index)

    fun mutableView(index: Int = 0): MutableStructBufferView<T> {
        val view = MutableStructBufferView<T>(buffer, 0, strideBytes, capacity)
        view.index = index
        return view
    }
}

fun StructBuffer<*>.asStorageBuffer(): GpuBuffer = asGpuBuffer(BufferUsage.makeUsage(storage = true))

fun StructBuffer<*>.asGpuBuffer(usage: BufferUsage): GpuBuffer {
    val buffer = GpuBuffer(struct.type, usage, capacity)
    buffer.uploadData(this)
    return buffer
}
