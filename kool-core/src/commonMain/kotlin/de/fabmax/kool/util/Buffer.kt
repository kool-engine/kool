package de.fabmax.kool.util

import de.fabmax.kool.math.*

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
 * Notice that Buffer is not generic, so that concrete types remain primitive (instead of getting boxed).
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

    fun getInt8(byteIndex: Int): Byte = getUint8(byteIndex).toByte()
    fun setInt8(byteIndex: Int, value: Byte): MixedBuffer = setUint8(byteIndex, value.toUByte())

    fun putInt16(value: Short): MixedBuffer = putUint16(value.toUShort())
    fun putInt16(data: ShortArray): MixedBuffer = putUint16(data)
    fun putInt16(data: ShortArray, offset: Int, len: Int): MixedBuffer = putUint16(data, offset, len)
    fun putInt16(data: Uint16Buffer): MixedBuffer = putUint16(data)

    fun getInt16(byteIndex: Int): Short = getUint16(byteIndex).toShort()
    fun setInt16(byteIndex: Int, value: Short): MixedBuffer = setUint16(byteIndex, value.toUShort())

    fun putInt32(value: Int): MixedBuffer
    fun putInt32(data: IntArray): MixedBuffer = putInt32(data, 0, data.size)
    fun putInt32(data: IntArray, offset: Int, len: Int): MixedBuffer
    fun putInt32(data: Int32Buffer): MixedBuffer

    fun getInt32(byteIndex: Int): Int
    fun setInt32(byteIndex: Int, value: Int): MixedBuffer

    fun putUint8(value: UByte): MixedBuffer
    fun putUint8(data: ByteArray): MixedBuffer = putUint8(data, 0, data.size)
    fun putUint8(data: ByteArray, offset: Int, len: Int): MixedBuffer
    fun putUint8(data: Uint8Buffer): MixedBuffer

    fun getUint8(byteIndex: Int): UByte
    fun setUint8(byteIndex: Int, value: UByte): MixedBuffer

    fun putUint16(value: UShort): MixedBuffer
    fun putUint16(data: ShortArray): MixedBuffer = putUint16(data, 0, data.size)
    fun putUint16(data: ShortArray, offset: Int, len: Int): MixedBuffer
    fun putUint16(data: Uint16Buffer): MixedBuffer

    fun getUint16(byteIndex: Int): UShort
    fun setUint16(byteIndex: Int, value: UShort): MixedBuffer

    fun putUint32(value: UInt): MixedBuffer = putInt32(value.toInt())
    fun putUint32(data: IntArray): MixedBuffer = putInt32(data, 0, data.size)
    fun putUint32(data: IntArray, offset: Int, len: Int): MixedBuffer = putInt32(data, 0, data.size)
    fun putUint32(data: Uint32Buffer): MixedBuffer = putInt32(data)

    fun getUint32(byteIndex: Int): UInt = getInt32(byteIndex).toUInt()
    fun setUint32(byteIndex: Int, value: UInt): MixedBuffer = setInt32(byteIndex, value.toInt())

    fun putFloat32(value: Float): MixedBuffer
    fun putFloat32(data: FloatArray): MixedBuffer = putFloat32(data, 0, data.size)
    fun putFloat32(data: FloatArray, offset: Int, len: Int): MixedBuffer
    fun putFloat32(data: Float32Buffer): MixedBuffer

    fun getFloat32(byteIndex: Int): Float
    fun setFloat32(byteIndex: Int, value: Float): MixedBuffer

    fun putFloat32(value: Double): MixedBuffer = putFloat32(value.toFloat())
    fun putFloat32(data: DoubleArray): MixedBuffer = putFloat32(data, 0, data.size)
    fun putFloat32(data: DoubleArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset ..< (offset + len)) {
            putFloat32(data[i].toFloat())
        }
        return this
    }

    fun putPadding(nBytes: Int): MixedBuffer

    companion object {
        const val SIZEOF_SHORT = 2
        const val SIZEOF_INT = 4
        const val SIZEOF_FLOAT = 4
    }
}

/**
 * Returns the buffer content at the given index. The index is based on the buffer size, i.e. calling this method
 * with [i] = 1 will return a vector with its components set to the two floats at buffer positions 2 and 3.
 */
fun Float32Buffer.getVec2f(i: Int, result: MutableVec2f = MutableVec2f()): MutableVec2f {
    val pos = i * 2
    result.x = get(pos)
    result.y = get(pos + 1)
    return result
}

/**
 * Returns the buffer content at the given index. If [withPadding] is true (the default), the vector is padded with an
 * additional 4th float value, which is typically needed in order to use it on the GPU.
 * The index is based on the buffer size, i.e. calling this method with [i] = 1 will return a vector with its
 * components set to the three floats at buffer positions 4, 5, and 6 (if [withPadding] is true) or at buffer
 * positions 3, 4, and 5 (if it is false).
 */
fun Float32Buffer.getVec3f(i: Int, result: MutableVec3f = MutableVec3f(), withPadding: Boolean = true): MutableVec3f {
    val pos = i * if (withPadding) 4 else 3
    result.x = get(pos)
    result.y = get(pos + 1)
    result.z = get(pos + 2)
    return result
}

/**
 * Returns the buffer content at the given index. The index is based on the buffer size, i.e. calling this method
 * with [i] = 1 will return a vector with its components set to the four floats at buffer positions 4, 5, 6, and 7.
 */
fun Float32Buffer.getVec4f(i: Int, result: MutableVec4f = MutableVec4f()): MutableVec4f {
    val pos = i * 4
    result.x = get(pos)
    result.y = get(pos + 1)
    result.z = get(pos + 2)
    result.w = get(pos + 3)
    return result
}

/**
 * Sets the buffer content at the given index to the provided value. The index is based on the buffer size, i.e.
 * calling this method with [i] = 1 will write to floats at buffer positions 2 and 3.
 */
fun Float32Buffer.setVec2f(i: Int, vec: Vec2f) {
    val pos = i * 2
    set(pos, vec.x)
    set(pos + 1, vec.y)
}

/**
 * Sets the buffer content at the given index to the provided value. If [withPadding] is true (the default), the
 * vector is padded with an additional 4th float value, which is typically needed in order to use it on the GPU.
 * The index is based on the buffer size, i.e. calling this method with [i] = 1 will write to floats at buffer
 * positions 4, 5, and 6 (if [withPadding] is true) or at buffer positions 3, 4, and 5 (if it is false).
 */
fun Float32Buffer.setVec3f(i: Int, vec: Vec3f, withPadding: Boolean = true) {
    val pos = i * if (withPadding) 4 else 3
    set(pos, vec.x)
    set(pos + 1, vec.y)
    set(pos + 2, vec.z)
}

/**
 * Sets the buffer content at the given index to the provided value. The index is based on the buffer size, i.e.
 * calling this method with [i] = 1 will write to floats at buffer positions 4, 5, 6, and 7.
 */
fun Float32Buffer.setVec4f(i: Int, vec: Vec4f) {
    val pos = i * 4
    set(pos, vec.x)
    set(pos + 1, vec.y)
    set(pos + 2, vec.z)
    set(pos + 3, vec.w)
}

/**
 * Returns the buffer content at the given index. The index is based on the buffer size, i.e. calling this method
 * with [i] = 1 will return a vector with its components set to the two ints at buffer positions 2 and 3.
 */
fun Int32Buffer.getVec2i(i: Int, result: MutableVec2i = MutableVec2i()): MutableVec2i {
    val pos = i * 2
    result.x = get(pos)
    result.y = get(pos + 1)
    return result
}

/**
 * Returns the buffer content at the given index. If [withPadding] is true (the default), the vector is padded with an
 * additional 4th int value, which is typically needed in order to use it on the GPU.
 * The index is based on the buffer size, i.e. calling this method with [i] = 1 will return a vector with its
 * components set to the three ints at buffer positions 4, 5, and 6 (if [withPadding] is true) or at buffer
 * positions 3, 4, and 5 (if it is false).
 */
fun Int32Buffer.getVec3i(i: Int, result: MutableVec3i = MutableVec3i(), withPadding: Boolean = true): MutableVec3i {
    val pos = i * if (withPadding) 4 else 3
    result.x = get(pos)
    result.y = get(pos + 1)
    result.z = get(pos + 2)
    return result
}

/**
 * Returns the buffer content at the given index. The index is based on the buffer size, i.e. calling this method
 * with [i] = 1 will return a vector with its components set to the four ints at buffer positions 4, 5, 6, and 7.
 */
fun Int32Buffer.getVec4i(i: Int, result: MutableVec4i = MutableVec4i()): MutableVec4i {
    val pos = i * 4
    result.x = get(pos)
    result.y = get(pos + 1)
    result.z = get(pos + 2)
    result.w = get(pos + 3)
    return result
}

/**
 * Sets the buffer content at the given index to the provided value. The index is based on the buffer size, i.e.
 * calling this method with [i] = 1 will write to ints at buffer positions 2 and 3.
 */
fun Int32Buffer.setVec2i(i: Int, vec: Vec2i) {
    val pos = i * 2
    set(pos, vec.x)
    set(pos + 1, vec.y)
}

/**
 * Sets the buffer content at the given index to the provided value. If [withPadding] is true (the default), the
 * vector is padded with an additional 4th int value, which is typically needed in order to use it on the GPU.
 * The index is based on the buffer size, i.e. calling this method with [i] = 1 will write to ints at buffer
 * positions 4, 5, and 6 (if [withPadding] is true) or at buffer positions 3, 4, and 5 (if it is false).
 */
fun Int32Buffer.setVec3i(i: Int, vec: Vec3i, withPadding: Boolean = true) {
    val pos = i * if (withPadding) 4 else 3
    set(pos, vec.x)
    set(pos + 1, vec.y)
    set(pos + 2, vec.z)
}

/**
 * Sets the buffer content at the given index to the provided value. The index is based on the buffer size, i.e.
 * calling this method with [i] = 1 will write to ints at buffer positions 4, 5, 6, and 7.
 */
fun Int32Buffer.setVec4i(i: Int, vec: Vec4i) {
    val pos = i * 4
    set(pos, vec.x)
    set(pos + 1, vec.y)
    set(pos + 2, vec.z)
    set(pos + 3, vec.w)
}
