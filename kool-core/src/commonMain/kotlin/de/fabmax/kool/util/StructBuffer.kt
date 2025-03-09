package de.fabmax.kool.util

class StructBuffer<T: Struct<T>>(val initCapacity: Int, val struct: T) {
    @PublishedApi
    internal val bufferAccess: StructBufferAccessIndexed = struct.viewBuffer(this)

    val strideBytes: Int = struct.structSize
    val buffer = MixedBuffer(initCapacity * strideBytes)

    var size = 0

    fun clear() {
        size = 0
    }

    operator fun get(index: Int) = struct.also { bufferAccess.index = index }

    inline fun put(block: T.() -> Unit) {
        check(size < initCapacity) { "StructBuffer capacity exceeded" }
        bufferAccess.index = size++
        struct.block()
    }

    inline fun forEach(block: T.() -> Unit) {
        bufferAccess.index = 0
        for (i in 0 until size) {
            bufferAccess.index = i
            struct.block()
        }
    }
}