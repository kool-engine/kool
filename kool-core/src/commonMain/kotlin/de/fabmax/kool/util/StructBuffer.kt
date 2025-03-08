package de.fabmax.kool.util

class StructBuffer<T: Struct<T>>(val initCapacity: Int, /*isResizable: Boolean = true, */val structProvider: () -> T) {
    @PublishedApi
    internal val bufferAccess = StructBufferAccessIndexed(this)
    @PublishedApi
    internal val sharedAccessor = createAccessor(bufferAccess)
    val strideBytes: Int = sharedAccessor.structSize
    val buffer = MixedBuffer(initCapacity * strideBytes)

    var size = 0

    fun clear() {
        size = 0
    }

    fun createAccessor(bufferAccess: StructBufferAccess = StructBufferAccessIndexed(this)): T {
        return structProvider().also { it.setupBufferAccess(bufferAccess) }
    }

    operator fun get(index: Int) = sharedAccessor.also { bufferAccess.index = index }

    inline fun put(block: T.() -> Unit) {
        check(size < initCapacity) { "StructBuffer capacity exceeded" }
        bufferAccess.index = size++
        sharedAccessor.block()
    }

    inline fun forEach(block: T.() -> Unit) {
        bufferAccess.index = 0
        for (i in 0 until size) {
            bufferAccess.index = i
            sharedAccessor.block()
        }
    }
}