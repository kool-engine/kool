package de.fabmax.kool.util

class StructBuffer<T: Struct<T>>(val size: Int, val struct: T) {
    @PublishedApi
    internal val bufferAccess: StructBufferAccessIndexed = struct.viewBuffer(this)

    val strideBytes: Int = struct.structSize
    val buffer = MixedBuffer(size * strideBytes)

    @PublishedApi
    internal var position = 0

    fun clear() {
        position = 0
    }

    operator fun get(index: Int) = struct.also { bufferAccess.index = index }

    inline fun put(block: T.() -> Unit) {
        check(position < size) { "StructBuffer capacity exceeded" }
        bufferAccess.index = position++
        struct.block()
    }

    inline fun forEach(block: T.() -> Unit) {
        bufferAccess.index = 0
        for (i in 0 until position) {
            bufferAccess.index = i
            struct.block()
        }
    }
}