package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import org.khronos.webgl.*


actual fun Uint8Buffer(capacity: Int): Uint8Buffer = Uint8BufferImpl(capacity)
actual fun Uint16Buffer(capacity: Int): Uint16Buffer = Uint16BufferImpl(capacity)
actual fun Int32Buffer(capacity: Int): Int32Buffer = Int32BufferImpl(capacity)
actual fun Float32Buffer(capacity: Int): Float32Buffer = Float32BufferImpl(capacity)
actual fun MixedBuffer(capacity: Int): MixedBuffer = MixedBufferImpl(capacity)

/**
 * @author fabmax
 */
abstract class GenericBuffer<out B: ArrayBufferView>(override val capacity: Int, create: () -> B) : Buffer {
    val buffer = create()

    override var limit = capacity
        set(value) {
            if (value < 0 || value > capacity) {
                throw KoolException("Limit is out of bounds: $value (capacity: $capacity)")
            }
            field = value
            if (position > value) {
                position = value
            }
        }

    override var position = 0

//    override val remaining: Int
//        get() = limit - position

    override fun flip() {
        limit = position
        position = 0
    }

    override fun clear() {
        limit = capacity
        position = 0
    }
}

/**
 * ByteBuffer buffer implementation
 */
class Uint8BufferImpl(array: Uint8Array) : Uint8Buffer, GenericBuffer<Uint8Array>(array.length, { array }) {

    constructor(capacity: Int) : this(Uint8Array(capacity))

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
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }
}

/**
 * ShortBuffer buffer implementation
 */
class Uint16BufferImpl(capacity: Int) : Uint16Buffer, GenericBuffer<Uint16Array>(capacity, {
    Uint16Array(capacity)
}) {
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
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }
}

/**
 * IntBuffer buffer implementation
 */
class Int32BufferImpl(capacity: Int) : Int32Buffer, GenericBuffer<Uint32Array>(capacity, {
    Uint32Array(capacity)
}) {
    override fun get(i: Int): Int {
        return buffer[i]
    }

    override fun set(i: Int, value: Int) {
        buffer[i] = value
    }

    override fun put(data: IntArray, offset: Int, len: Int): Int32Buffer {
        for (i in offset..(offset + len - 1)) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(value: Int): Int32Buffer {
        buffer[position++] = value
        return this
    }

    override fun put(data: Int32Buffer): Int32Buffer {
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }
}

/**
 * FloatBuffer buffer implementation
 */
class Float32BufferImpl(capacity: Int) : Float32Buffer, GenericBuffer<Float32Array>(capacity, {
    Float32Array(capacity)
}) {
    override fun get(i: Int): Float {
        return buffer[i]
    }

    override fun set(i: Int, value: Float) {
        buffer[i] = value
    }

    override fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer {
        for (i in offset until offset + len) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(value: Float): Float32Buffer {
        buffer[position++] = value
        return this
    }

    override fun put(value: Double): Float32Buffer {
        buffer[position++] = value.toFloat()
        return this
    }

    override fun put(data: Float32Buffer): Float32Buffer {
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }
}

class MixedBufferImpl(capacity: Int) : MixedBuffer, GenericBuffer<DataView>(capacity, {
    DataView(ArrayBuffer(capacity))
}) {
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

    override fun putUint16(value: UShort): MixedBuffer {
        buffer.setUint16(position, value.toShort(), true)
        position += 2
        return this
    }

    override fun putUint16(data: ShortArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset until offset + len) {
            buffer.setUint16(position, data[i], true)
            position += 2
        }
        return this
    }

    override fun putUint16(data: Uint16Buffer): MixedBuffer {
        for (i in data.position until data.limit) {
            buffer.setUint16(position, data[i].toShort(), true)
            position += 2
        }
        return this
    }

    override fun putInt32(value: Int): MixedBuffer {
        buffer.setUint32(position, value, true)
        position += 4
        return this
    }

    override fun putInt32(data: IntArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset until offset + len) {
            buffer.setUint32(position, data[i], true)
            position += 4
        }
        return this
    }

    override fun putInt32(data: Int32Buffer): MixedBuffer {
        for (i in data.position until data.limit) {
            buffer.setUint32(position, data[i], true)
            position += 4
        }
        return this
    }

    override fun putFloat32(value: Float): MixedBuffer {
        buffer.setFloat32(position, value, true)
        position += 4
        return this
    }

    override fun putFloat32(data: FloatArray, offset: Int, len: Int): MixedBuffer {
        for (i in offset until offset + len) {
            buffer.setFloat32(position, data[i], true)
            position += 4
        }
        return this
    }

    override fun putFloat32(data: Float32Buffer): MixedBuffer {
        for (i in data.position until data.limit) {
            buffer.setFloat32(position, data[i], true)
            position += 4
        }
        return this
    }

    override fun putPadding(nBytes: Int): MixedBuffer {
        position += nBytes
        return this
    }
}
