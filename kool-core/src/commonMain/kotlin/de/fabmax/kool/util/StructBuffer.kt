package de.fabmax.kool.util

import de.fabmax.kool.pipeline.BufferUsage
import de.fabmax.kool.pipeline.GpuBuffer
import kotlin.math.max

class StructBuffer<T: Struct>(val struct: T, val capacity: Int) {
    val strideBytes: Int = struct.structSize
    val buffer = MixedBuffer(capacity * strideBytes)

    @PublishedApi
    internal val defaultView: MutableStructBufferView<T> = MutableStructBufferView(buffer, 0, strideBytes, capacity)

    var position = 0

    var limit = capacity
        set(value) {
            if (value != field) {
                field = value
                buffer.limit = value * strideBytes
            }
        }

    val remaining: Int
        get() = capacity - position

    fun clear() {
        position = 0
        buffer.clear()
    }

    inline fun get(index: Int, block: StructBufferView<T>.(T) -> Unit) {
        require(index >= 0 && index < capacity) { "Out-of-bounds index: $index, capacity: $capacity" }
        defaultView.index = index
        defaultView.block(struct)
    }

    inline fun set(index: Int, block: MutableStructBufferView<T>.(T) -> Unit) {
        require(index >= 0 && index < capacity) { "Out-of-bounds index: $index, capacity: $capacity" }
        limit = max(index + 1, limit)
        defaultView.index = index
        defaultView.block(struct)
    }

    inline fun put(block: MutableStructBufferView<T>.(T) -> Unit): Int {
        check(position < capacity) { "StructBuffer capacity exceeded, capacity: $capacity" }
        val index = position++
        limit = max(position, limit)
        defaultView.index = index
        defaultView.block(struct)
        return index
    }

    fun putAll(other: StructBuffer<T>) {
        check(struct.hash == other.struct.hash) { "Can only put buffers with matching structs" }
        check(remaining >= other.limit) { "Insufficient size: $remaining < ${other.limit}" }
        check(other.limit == other.buffer.limit / other.strideBytes) {
            "Buffer limit mismatch: ${other.limit} != ${other.buffer.limit / other.strideBytes}"
        }

        limit = max(limit, position + other.limit)
        buffer.put(other.buffer)
        position += other.limit
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
