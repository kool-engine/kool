package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import org.khronos.webgl.*

/**
 * @author fabmax
 */
internal abstract class GenericBuffer<out B: ArrayBufferView, T>(capacity: Int, create: () -> B) : Buffer<T> {
    val buffer = create()

    override val capacity = capacity

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

    override val remaining: Int
        get() = limit - position

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
internal class Uint8BufferImpl(capacity: Int) : Uint8Buffer, GenericBuffer<Uint8Array, Byte>(capacity, {
    Uint8Array(capacity)
}) {
    override fun put(data: ByteArray, offset: Int, len: Int): Uint8Buffer {
        for (i in offset..(offset + len - 1)) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(value: Byte): Uint8Buffer {
        buffer[position++] = value
        return this
    }

    override fun put(data: de.fabmax.kool.util.Buffer<Byte>): Uint8Buffer {
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }

    override fun get(i: Int): Byte {
        return buffer[i]
    }

    override fun set(i: Int, value: Byte) {
        buffer[i] = value
    }
}

/**
 * ShortBuffer buffer implementation
 */
internal class Uint16BufferImpl(capacity: Int) : Uint16Buffer, GenericBuffer<Uint16Array, Short>(capacity, {
    Uint16Array(capacity)
}) {
    override fun put(data: ShortArray, offset: Int, len: Int): Uint16Buffer {
        for (i in offset..(offset + len - 1)) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(value: Short): Uint16Buffer {
        buffer[position++] = value
        return this
    }

    override fun put(data: de.fabmax.kool.util.Buffer<Short>): Uint16Buffer {
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }

    override fun get(i: Int): Short {
        return buffer[i]
    }

    override fun set(i: Int, value: Short) {
        buffer[i] = value
    }
}

/**
 * IntBuffer buffer implementation
 */
internal class Uint32BufferImpl(capacity: Int) : Uint32Buffer, GenericBuffer<Uint32Array, Int>(capacity, {
    Uint32Array(capacity)
}) {
    override fun put(data: IntArray, offset: Int, len: Int): Uint32Buffer {
        for (i in offset..(offset + len - 1)) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(value: Int): Uint32Buffer {
        buffer[position++] = value
        return this
    }

    override fun put(data: de.fabmax.kool.util.Buffer<Int>): Uint32Buffer {
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }

    override fun get(i: Int): Int {
        return buffer[i]
    }

    override fun set(i: Int, value: Int) {
        buffer[i] = value
    }
}

/**
 * FloatBuffer buffer implementation
 */
internal class Float32BufferImpl(capacity: Int) : Float32Buffer, GenericBuffer<Float32Array, Float>(capacity, {
    Float32Array(capacity)
}) {
    override fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer {
        for (i in offset..(offset + len - 1)) {
            buffer[position++] = data[i]
        }
        return this
    }

    override fun put(value: Float): Float32Buffer {
        buffer[position++] = value
        return this
    }

    override fun put(data: de.fabmax.kool.util.Buffer<Float>): Float32Buffer {
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }

    override fun get(i: Int): Float {
        return buffer[i]
    }

    override fun set(i: Int, value: Float) {
        buffer[i] = value
    }
}

actual fun createUint8Buffer(capacity: Int): Uint8Buffer {
    return Uint8BufferImpl(capacity)
}

actual fun createUint16Buffer(capacity: Int): Uint16Buffer {
    return Uint16BufferImpl(capacity)
}

actual fun createUint32Buffer(capacity: Int): Uint32Buffer {
    return Uint32BufferImpl(capacity)
}

actual fun createFloat32Buffer(capacity: Int): Float32Buffer {
    return Float32BufferImpl(capacity)
}
