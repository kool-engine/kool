package de.fabmax.kool.util

/**
 * Super class for platform-dependent buffers. In the JVM these buffers directly map to the corresponding NIO buffers.
 * However, not all operations of NIO buffers are supported.
 *
 * @author fabmax
 */
interface Buffer {
    var limit: Int
    var position: Int
    val remaining: Int
    val capacity: Int

    fun flip()
    fun clear()
}

/**
 * Represents a buffer for bytes.
 *
 * @author fabmax
 */
interface Uint8Buffer : Buffer {
    operator fun get(i: Int): Byte
    operator fun set(i: Int, value: Byte)
    operator fun plusAssign(value: Byte) { put(value) }

    fun put(value: Byte): Uint8Buffer
    fun put(data: ByteArray): Uint8Buffer = put(data, 0, data.size)
    fun put(data: ByteArray, offset: Int, len: Int): Uint8Buffer
    fun put(data: Uint8Buffer): Uint8Buffer
}

/**
 * Represents a buffer for shorts.
 *
 * @author fabmax
 */
interface Uint16Buffer : Buffer {
    operator fun get(i: Int): Short
    operator fun set(i: Int, value: Short)
    operator fun plusAssign(value: Short) { put(value) }

    fun put(value: Short): Uint16Buffer
    fun put(data: ShortArray): Uint16Buffer = put(data, 0, data.size)
    fun put(data: ShortArray, offset: Int, len: Int): Uint16Buffer
    fun put(data: Uint16Buffer): Uint16Buffer
}

/**
 * Represents a buffer for ints.
 *
 * @author fabmax
 */
interface Uint32Buffer : Buffer {
    operator fun get(i: Int): Int
    operator fun set(i: Int, value: Int)
    operator fun plusAssign(value: Int) { put(value) }

    fun put(value: Int): Uint32Buffer
    fun put(data: IntArray): Uint32Buffer = put(data, 0, data.size)
    fun put(data: IntArray, offset: Int, len: Int): Uint32Buffer
    fun put(data: Uint32Buffer): Uint32Buffer
}

/**
 * Represents a buffer for floats.
 *
 * @author fabmax
 */
interface Float32Buffer : Buffer {
    operator fun get(i: Int): Float
    operator fun set(i: Int, value: Float)
    operator fun plusAssign(value: Float) { put(value) }

    fun put(value: Float): Float32Buffer
    fun put(data: FloatArray): Float32Buffer = put(data, 0, data.size)
    fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer
    fun put(data: Float32Buffer): Float32Buffer
}

expect fun createUint8Buffer(capacity: Int): Uint8Buffer

expect fun createUint16Buffer(capacity: Int): Uint16Buffer

expect fun createUint32Buffer(capacity: Int): Uint32Buffer

expect fun createFloat32Buffer(capacity: Int): Float32Buffer
