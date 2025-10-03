package de.fabmax.kool.util

import de.fabmax.kool.pipeline.BufferUsage
import de.fabmax.kool.pipeline.GpuBuffer

class StructBuffer<T: Struct>(val struct: T, val capacity: Int) {
    val strideBytes: Int = struct.structSize
    val buffer = MixedBuffer(capacity * strideBytes)

    @PublishedApi
    internal val defaultView: MutableStructBufferView<T> = MutableStructBufferView(buffer, 0, strideBytes, capacity)

    @PublishedApi
    internal var position = 0
    val size: Int get() = position

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
        defaultView.index = index
        defaultView.block(struct)
    }

    inline fun put(block: MutableStructBufferView<T>.(T) -> Unit): Int {
        check(position < capacity) { "StructBuffer capacity exceeded, capacity: $capacity" }
        val index = position++
        defaultView.index = index
        defaultView.block(struct)
        return index
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
