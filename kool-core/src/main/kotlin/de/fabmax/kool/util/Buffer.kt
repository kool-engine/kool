package de.fabmax.kool.util

/**
 * Super class for platform-dependent buffers. In the JVM these buffers directly map to the corresponding NIO buffers.
 * However, not all operations of NIO buffers are supported.
 *
 * @author fabmax
 */
interface Buffer<T> {
    var limit: Int
    var position: Int
    val remaining: Int
    val capacity: Int

    fun flip()
    fun clear()

    fun put(value: T): Buffer<T>
    operator fun get(i: Int): T
    operator fun set(i: Int, value: T)
    operator fun plusAssign(value: T) { put(value) }

}

/**
 * Represents a buffer for bytes.
 *
 * @author fabmax
 */
interface Uint8Buffer : Buffer<Byte> {
    fun put(data: ByteArray): Uint8Buffer = put(data, 0, data.size)
    fun put(data: ByteArray, offset: Int, len: Int): Uint8Buffer
    override fun put(value: Byte): Uint8Buffer
}

/**
 * Represents a buffer for shorts.
 *
 * @author fabmax
 */
interface Uint16Buffer : Buffer<Short> {
    fun put(data: ShortArray): Uint16Buffer = put(data, 0, data.size)
    fun put(data: ShortArray, offset: Int, len: Int): Uint16Buffer
    override fun put(value: Short): Uint16Buffer
}

/**
 * Represents a buffer for ints.
 *
 * @author fabmax
 */
interface Uint32Buffer : Buffer<Int> {
    fun put(data: IntArray): Uint32Buffer = put(data, 0, data.size)
    fun put(data: IntArray, offset: Int, len: Int): Uint32Buffer
    override fun put(value: Int): Uint32Buffer
}

/**
 * Represents a buffer for floats.
 *
 * @author fabmax
 */
interface Float32Buffer : Buffer<Float> {
    fun put(data: FloatArray): Float32Buffer = put(data, 0, data.size)
    fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer
    override fun put(value: Float): Float32Buffer
}

expect fun createUint8Buffer(capacity: Int): Uint8Buffer

expect fun createUint16Buffer(capacity: Int): Uint16Buffer

expect fun createUint32Buffer(capacity: Int): Uint32Buffer

expect fun createFloat32Buffer(capacity: Int): Float32Buffer
