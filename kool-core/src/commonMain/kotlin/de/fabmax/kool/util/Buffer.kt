package de.fabmax.kool.util

expect fun Uint8Buffer(capacity: Int, isAutoLimit: Boolean = false): Uint8Buffer
expect fun Uint16Buffer(capacity: Int, isAutoLimit: Boolean = false): Uint16Buffer
expect fun Int32Buffer(capacity: Int, isAutoLimit: Boolean = false): Int32Buffer
expect fun Float32Buffer(capacity: Int, isAutoLimit: Boolean = false): Float32Buffer
expect fun MixedBuffer(capacity: Int, isAutoLimit: Boolean = false): MixedBuffer
fun Uint32Buffer(capacity: Int, isAutoLimit: Boolean = false): Uint32Buffer = Int32Buffer(capacity, isAutoLimit)

/**
 * Super class for platform-dependent buffers. In the JVM these buffers directly map to the corresponding NIO buffers.
 * However, not all operations of NIO buffers are supported.
 *
 * Notice that Buffer is not generic, so that concrete types remain primitive.
 *
 * @author fabmax
 */
interface Buffer {
    val capacity: Int
    var position: Int
    var limit: Int
    var isAutoLimit: Boolean

    val remaining: Int
        get() = capacity - position

    fun clear()

    fun checkCapacity(requiredSize: Int) = check(requiredSize <= remaining) {
        RuntimeException("Insufficient remaining size. Requested: $requiredSize, remaining: $remaining")
    }
}

/**
 * Represents a buffer for bytes.
 *
 * @author fabmax
 */
interface Uint8Buffer : Buffer {
    operator fun get(i: Int): UByte
    operator fun set(i: Int, value: UByte)
    fun put(value: UByte): Uint8Buffer
    fun put(data: ByteArray, offset: Int, len: Int): Uint8Buffer
    fun put(data: Uint8Buffer): Uint8Buffer

    operator fun plusAssign(value: UByte) { put(value) }
    operator fun plusAssign(value: Byte) { put(value) }
    fun put(value: Byte): Uint8Buffer = put(value.toUByte())
    fun put(data: ByteArray): Uint8Buffer = put(data, 0, data.size)

    fun toArray(): ByteArray = ByteArray(capacity) { get(it).toByte() }
}

/**
 * Represents a buffer for shorts.
 *
 * @author fabmax
 */
interface Uint16Buffer : Buffer {
    operator fun get(i: Int): UShort
    operator fun set(i: Int, value: UShort)
    fun put(value: UShort): Uint16Buffer
    fun put(data: ShortArray, offset: Int, len: Int): Uint16Buffer
    fun put(data: Uint16Buffer): Uint16Buffer

    operator fun plusAssign(value: UShort) { put(value) }
    operator fun plusAssign(value: Short) { put(value) }
    fun put(value: Short): Uint16Buffer = put(value.toUShort())
    fun put(data: ShortArray): Uint16Buffer = put(data, 0, data.size)

    fun toArray(): ShortArray = ShortArray(capacity) { get(it).toShort() }
}

/**
 * Represents a buffer for ints.
 *
 * @author fabmax
 */
interface Int32Buffer : Buffer {
    operator fun get(i: Int): Int
    operator fun set(i: Int, value: Int)
    fun put(value: Int): Int32Buffer
    fun put(data: IntArray, offset: Int, len: Int): Int32Buffer
    fun put(data: Int32Buffer): Int32Buffer

    operator fun plusAssign(value: UInt) { put(value.toInt()) }
    operator fun plusAssign(value: Int) { put(value) }
    fun put(value: UInt): Int32Buffer = put(value.toInt())
    fun put(data: IntArray): Int32Buffer = put(data, 0, data.size)

    fun toArray(): IntArray = IntArray(capacity) { get(it) }
}

typealias Uint32Buffer = Int32Buffer

/**
 * Represents a buffer for floats.
 *
 * @author fabmax
 */
interface Float32Buffer : Buffer {
    operator fun get(i: Int): Float
    operator fun set(i: Int, value: Float)
    fun put(value: Float): Float32Buffer
    fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer
    fun put(data: Float32Buffer): Float32Buffer

    operator fun plusAssign(value: Float) { put(value) }
    operator fun plusAssign(value: Double) { put(value) }

    fun put(value: Double): Float32Buffer = put(value.toFloat())
    fun put(data: FloatArray): Float32Buffer = put(data, 0, data.size)
    fun put(data: DoubleArray): Float32Buffer = put(data, 0, data.size)
    fun put(data: DoubleArray, offset: Int, len: Int): Float32Buffer {
        for (i in offset ..< (offset + len)) {
            put(data[i].toFloat())
        }
        return this
    }

    fun toArray(): FloatArray = FloatArray(capacity) { get(it) }
}

/**
 * Represents a buffer containing mixed type data. All buffer positions are in bytes.
 *
 * @author fabmax
 */
interface MixedBuffer : Buffer {
    fun putInt8(value: Byte): MixedBuffer = putUint8(value.toUByte())
    fun putInt8(data: ByteArray): MixedBuffer = putUint8(data)
    fun putInt8(data: ByteArray, offset: Int, len: Int): MixedBuffer = putUint8(data, offset, len)
    fun putInt8(data: Uint8Buffer): MixedBuffer = putUint8(data)

    fun putInt16(value: Short): MixedBuffer = putUint16(value.toUShort())
    fun putInt16(data: ShortArray): MixedBuffer = putUint16(data)
    fun putInt16(data: ShortArray, offset: Int, len: Int): MixedBuffer = putUint16(data, offset, len)
    fun putInt16(data: Uint16Buffer): MixedBuffer = putUint16(data)

    fun putInt32(value: Int): MixedBuffer
    fun putInt32(data: IntArray) : MixedBuffer= putInt32(data, 0, data.size)
    fun putInt32(data: IntArray, offset: Int, len: Int): MixedBuffer
    fun putInt32(data: Int32Buffer): MixedBuffer

    fun putUint8(value: UByte): MixedBuffer
    fun putUint8(data: ByteArray): MixedBuffer = putUint8(data, 0, data.size)
    fun putUint8(data: ByteArray, offset: Int, len: Int): MixedBuffer
    fun putUint8(data: Uint8Buffer): MixedBuffer

    fun putUint16(value: UShort): MixedBuffer
    fun putUint16(data: ShortArray): MixedBuffer = putUint16(data, 0, data.size)
    fun putUint16(data: ShortArray, offset: Int, len: Int): MixedBuffer
    fun putUint16(data: Uint16Buffer): MixedBuffer

    fun putUint32(value: Int): MixedBuffer = putInt32(value)
    fun putUint32(data: IntArray): MixedBuffer = putInt32(data, 0, data.size)
    fun putUint32(data: IntArray, offset: Int, len: Int): MixedBuffer = putInt32(data, 0, data.size)
    fun putUint32(data: Uint32Buffer): MixedBuffer = putInt32(data)

    fun putFloat32(value: Float): MixedBuffer
    fun putFloat32(data: FloatArray): MixedBuffer = putFloat32(data, 0, data.size)
    fun putFloat32(data: FloatArray, offset: Int, len: Int): MixedBuffer
    fun putFloat32(data: Float32Buffer): MixedBuffer
    fun putFloat32(value: Double): MixedBuffer = putFloat32(value.toFloat())
    fun putFloat32(data: DoubleArray): MixedBuffer = putFloat32(data, 0, data.size)
    fun putFloat32(data: DoubleArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset ..< (offset + len)) {
            putFloat32(data[i].toFloat())
        }
        return this
    }

    fun putPadding(nBytes: Int): MixedBuffer
}
