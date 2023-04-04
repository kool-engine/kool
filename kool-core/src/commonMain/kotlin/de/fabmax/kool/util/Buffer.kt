package de.fabmax.kool.util

import de.fabmax.kool.KoolException

/**
 * Super class for platform-dependent buffers. In the JVM these buffers directly map to the corresponding NIO buffers.
 * However, not all operations of NIO buffers are supported.
 *
 * Notice that Buffer is not generic, so that concrete types remain primitive.
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

    fun removeAt(index: Int) {
        if (position > index) {
            position--
        }
        if (limit > index) {
            limit--
        }
    }
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

    fun toArray(): ByteArray {
        val array = ByteArray(capacity)
        for (i in 0 until capacity) {
            array[i] = get(i)
        }
        return array
    }

    override fun removeAt(index: Int) {
        for (i in index until position) {
            this[i] = this[i+1]
        }
        super.removeAt(index)
    }
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

    override fun removeAt(index: Int) {
        for (i in index until position) {
            this[i] = this[i+1]
        }
        super.removeAt(index)
    }
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

    override fun removeAt(index: Int) {
        for (i in index until position) {
            this[i] = this[i+1]
        }
        super.removeAt(index)
    }
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

    override fun removeAt(index: Int) {
        for (i in index until position) {
            this[i] = this[i+1]
        }
        super.removeAt(index)
    }
}

/**
 * Represents a buffer containing mixed type data. All buffer positions are in bytes.
 *
 * @author fabmax
 */
interface MixedBuffer : Buffer {
    fun putInt8(value: Byte) = putUint8(value)
    fun putInt8(data: ByteArray) = putUint8(data)
    fun putInt8(data: ByteArray, offset: Int, len: Int) = putUint8(data, offset, len)
    fun putInt8(data: Uint8Buffer) = putUint8(data)

    fun putInt16(value: Short) = putUint16(value)
    fun putInt16(data: ShortArray) = putUint16(data)
    fun putInt16(data: ShortArray, offset: Int, len: Int) = putUint16(data, offset, len)
    fun putInt16(data: Uint16Buffer) = putUint16(data)

    fun putInt32(value: Int) = putUint32(value)
    fun putInt32(data: IntArray) = putUint32(data)
    fun putInt32(data: IntArray, offset: Int, len: Int) = putUint32(data, offset, len)
    fun putInt32(data: Uint32Buffer) = putUint32(data)

    fun putUint8(value: Byte): MixedBuffer
    fun putUint8(data: ByteArray): MixedBuffer = putUint8(data, 0, data.size)
    fun putUint8(data: ByteArray, offset: Int, len: Int): MixedBuffer
    fun putUint8(data: Uint8Buffer): MixedBuffer

    fun putUint16(value: Short): MixedBuffer
    fun putUint16(data: ShortArray): MixedBuffer = putUint16(data, 0, data.size)
    fun putUint16(data: ShortArray, offset: Int, len: Int): MixedBuffer
    fun putUint16(data: Uint16Buffer): MixedBuffer

    fun putUint32(value: Int): MixedBuffer
    fun putUint32(data: IntArray): MixedBuffer = putUint32(data, 0, data.size)
    fun putUint32(data: IntArray, offset: Int, len: Int): MixedBuffer
    fun putUint32(data: Uint32Buffer): MixedBuffer

    fun putFloat32(value: Float): MixedBuffer
    fun putFloat32(data: FloatArray): MixedBuffer = putFloat32(data, 0, data.size)
    fun putFloat32(data: FloatArray, offset: Int, len: Int): MixedBuffer
    fun putFloat32(data: Float32Buffer): MixedBuffer

    fun padding(nBytes: Int): MixedBuffer

    override fun removeAt(index: Int) {
        throw KoolException("MixedBuffer does not support element removal")
    }
}

expect fun createUint8Buffer(capacity: Int): Uint8Buffer

expect fun createUint16Buffer(capacity: Int): Uint16Buffer

expect fun createUint32Buffer(capacity: Int): Uint32Buffer

expect fun createFloat32Buffer(capacity: Int): Float32Buffer

expect fun createMixedBuffer(capacity: Int): MixedBuffer

