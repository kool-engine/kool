package de.fabmax.kool.util

import de.fabmax.kool.util.MixedBuffer.Companion.SIZEOF_FLOAT
import de.fabmax.kool.util.MixedBuffer.Companion.SIZEOF_INT
import de.fabmax.kool.util.MixedBuffer.Companion.SIZEOF_SHORT
import org.khronos.webgl.*

actual fun Uint8Buffer(capacity: Int, isAutoLimit: Boolean): Uint8Buffer = Uint8BufferImpl(capacity, isAutoLimit)
actual fun Uint16Buffer(capacity: Int, isAutoLimit: Boolean): Uint16Buffer = Uint16BufferImpl(capacity, isAutoLimit)
actual fun Int32Buffer(capacity: Int, isAutoLimit: Boolean): Int32Buffer = Int32BufferImpl(capacity, isAutoLimit)
actual fun Float32Buffer(capacity: Int, isAutoLimit: Boolean): Float32Buffer = Float32BufferImpl(capacity, isAutoLimit)
actual fun MixedBuffer(capacity: Int, isAutoLimit: Boolean): MixedBuffer = MixedBufferImpl(capacity, isAutoLimit)

abstract class GenericBuffer<B: ArrayBufferView>(
    final override val capacity: Int,
    val buffer: B,
    override var isAutoLimit: Boolean
) : Buffer {

    override var limit = capacity
        get() = if (isAutoLimit) position else field
        set(value) {
            check(value in 0 .. capacity) { "Limit is out of bounds: $value (capacity: $capacity)" }
            field = value
            isAutoLimit = false
        }

    override var position = 0

    override fun clear() {
        position = 0
    }
}

/**
 * ByteBuffer buffer implementation
 */
class Uint8BufferImpl(array: Uint8Array, isAutoLimit: Boolean = false) :
    GenericBuffer<Uint8Array>(array.length, array, isAutoLimit), Uint8Buffer
{
    constructor(capacity: Int, isAutoLimit: Boolean = false) : this(Uint8Array(capacity), isAutoLimit)

    override fun get(i: Int): UByte {
        return buffer[i].toUByte()
    }

    override fun set(i: Int, value: UByte) {
        buffer[i] = value.toByte()
    }

    override fun put(value: UByte): Uint8Buffer {
        buffer[position++] = value.toByte()
        return this
    }

    override fun put(data: ByteArray, offset: Int, len: Int): Uint8Buffer {
        for (i in offset ..< offset + len) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(data: Uint8Buffer): Uint8Buffer {
        for (i in 0 ..< data.limit) {
            put(data[i])
        }
        return this
    }
}

/**
 * ShortBuffer buffer implementation
 */
class Uint16BufferImpl(capacity: Int, isAutoLimit: Boolean = false) :
    GenericBuffer<Uint16Array>(capacity, Uint16Array(capacity), isAutoLimit), Uint16Buffer
{
    override fun get(i: Int): UShort {
        return buffer[i].toUShort()
    }

    override fun set(i: Int, value: UShort) {
        buffer[i] = value.toShort()
    }

    override fun put(value: UShort): Uint16Buffer {
        buffer[position++] = value.toShort()
        return this
    }

    override fun put(data: ShortArray, offset: Int, len: Int): Uint16Buffer {
        for (i in offset ..< offset + len) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(data: Uint16Buffer): Uint16Buffer {
        for (i in 0 ..< data.limit) {
            put(data[i])
        }
        return this
    }
}

/**
 * IntBuffer buffer implementation
 */
class Int32BufferImpl(capacity: Int, isAutoLimit: Boolean = false) :
    GenericBuffer<Int32Array>(capacity, Int32Array(capacity), isAutoLimit), Int32Buffer
{
    override fun get(i: Int): Int {
        return buffer[i]
    }

    override fun set(i: Int, value: Int) {
        buffer[i] = value
    }

    override fun put(value: Int): Int32Buffer {
        buffer[position++] = value
        return this
    }

    override fun put(data: IntArray, offset: Int, len: Int): Int32Buffer {
        for (i in offset ..< offset + len) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(data: Int32Buffer): Int32Buffer {
        for (i in 0 ..< data.limit) {
            put(data[i])
        }
        return this
    }
}

/**
 * FloatBuffer buffer implementation
 */
class Float32BufferImpl(capacity: Int, isAutoLimit: Boolean = false) :
    GenericBuffer<Float32Array>(capacity, Float32Array(capacity), isAutoLimit), Float32Buffer
{
    override fun get(i: Int): Float {
        return buffer[i]
    }

    override fun set(i: Int, value: Float) {
        buffer[i] = value
    }

    override fun put(value: Float): Float32Buffer {
        buffer[position++] = value
        return this
    }

    override fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer {
        for (i in offset ..< offset + len) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(value: Double): Float32Buffer {
        buffer[position++] = value.toFloat()
        return this
    }

    override fun put(data: Float32Buffer): Float32Buffer {
        for (i in 0 ..< data.limit) {
            put(data[i])
        }
        return this
    }
}

class MixedBufferImpl(capacity: Int, isAutoLimit: Boolean = false) :
    GenericBuffer<DataView>(capacity, DataView(ArrayBuffer(capacity)), isAutoLimit), MixedBuffer
{
    override fun putUint8(value: UByte): MixedBuffer {
        buffer.setUint8(position++, value.toByte())
        return this
    }

    override fun putUint8(data: ByteArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset until offset + len) {
            buffer.setUint8(position++, data[i])
        }
        return this
    }

    override fun putUint8(data: Uint8Buffer): MixedBuffer {
        for (i in data.position until data.limit) {
            buffer.setUint8(position++, data[i].toByte())
        }
        return this
    }

    override fun getUint8(byteIndex: Int): UByte {
        return buffer.getInt8(byteIndex).toUByte()
    }

    override fun setUint8(byteIndex: Int, value: UByte): MixedBuffer {
        buffer.setUint8(byteIndex, value.toByte())
        return this
    }

    override fun putUint16(value: UShort): MixedBuffer {
        buffer.setUint16(position, value.toShort(), true)
        position += SIZEOF_SHORT
        return this
    }

    override fun putUint16(data: ShortArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset until offset + len) {
            buffer.setUint16(position, data[i], true)
            position += SIZEOF_SHORT
        }
        return this
    }

    override fun putUint16(data: Uint16Buffer): MixedBuffer {
        for (i in data.position until data.limit) {
            buffer.setUint16(position, data[i].toShort(), true)
            position += SIZEOF_SHORT
        }
        return this
    }

    override fun getUint16(byteIndex: Int): UShort {
        return buffer.getInt16(byteIndex, true).toUShort()
    }

    override fun setUint16(byteIndex: Int, value: UShort): MixedBuffer {
        buffer.setUint16(byteIndex, value.toShort(), true)
        return this
    }

    override fun putInt32(value: Int): MixedBuffer {
        buffer.setUint32(position, value, true)
        position += SIZEOF_INT
        return this
    }

    override fun putInt32(data: IntArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset until offset + len) {
            buffer.setUint32(position, data[i], true)
            position += SIZEOF_INT
        }
        return this
    }

    override fun putInt32(data: Int32Buffer): MixedBuffer {
        for (i in data.position until data.limit) {
            buffer.setUint32(position, data[i], true)
            position += SIZEOF_INT
        }
        return this
    }

    override fun getInt32(byteIndex: Int): Int {
        return buffer.getInt32(byteIndex, true)
    }

    override fun setInt32(byteIndex: Int, value: Int): MixedBuffer {
        buffer.setInt32(byteIndex, value, true)
        return this
    }

    override fun putFloat32(value: Float): MixedBuffer {
        buffer.setFloat32(position, value, true)
        position += SIZEOF_FLOAT
        return this
    }

    override fun putFloat32(data: FloatArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset until offset + len) {
            buffer.setFloat32(position, data[i], true)
            position += SIZEOF_FLOAT
        }
        return this
    }

    override fun putFloat32(data: Float32Buffer): MixedBuffer {
        for (i in data.position until data.limit) {
            buffer.setFloat32(position, data[i], true)
            position += SIZEOF_FLOAT
        }
        return this
    }

    override fun getFloat32(byteIndex: Int): Float {
        return buffer.getFloat32(byteIndex, true)
    }

    override fun setFloat32(byteIndex: Int, value: Float): MixedBuffer {
        buffer.setFloat32(byteIndex, value, true)
        return this
    }

    override fun putPadding(nBytes: Int): MixedBuffer {
        position += nBytes
        return this
    }
}
