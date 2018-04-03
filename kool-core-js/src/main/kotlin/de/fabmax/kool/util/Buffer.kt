package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import org.khronos.webgl.*

/**
 * @author fabmax
 */
internal abstract class GenericBuffer<out B: ArrayBufferView>(capacity: Int, create: () -> B) : Buffer {
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
internal class Uint8BufferImpl(capacity: Int) : Uint8Buffer, GenericBuffer<Uint8Array>(capacity, {
    Uint8Array(capacity)
}) {
    override fun get(i: Int): Byte {
        return buffer[i]
    }

    override fun set(i: Int, value: Byte) {
        buffer[i] = value
    }

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
internal class Uint16BufferImpl(capacity: Int) : Uint16Buffer, GenericBuffer<Uint16Array>(capacity, {
    Uint16Array(capacity)
}) {
    override fun get(i: Int): Short {
        return buffer[i]
    }

    override fun set(i: Int, value: Short) {
        buffer[i] = value
    }

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
internal class Uint32BufferImpl(capacity: Int) : Uint32Buffer, GenericBuffer<Uint32Array>(capacity, {
    Uint32Array(capacity)
}) {
    override fun get(i: Int): Int {
        return buffer[i]
    }

    override fun set(i: Int, value: Int) {
        buffer[i] = value
    }

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

    override fun put(data: Uint32Buffer): Uint32Buffer {
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
    }
}

/**
 * FloatBuffer buffer implementation
 */
internal class Float32BufferImpl(capacity: Int) : Float32Buffer, GenericBuffer<Float32Array>(capacity, {
    Float32Array(capacity)
}) {
    override fun get(i: Int): Float {
        return buffer[i]
    }

    override fun set(i: Int, value: Float) {
        buffer[i] = value
    }

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

    override fun put(data: Float32Buffer): Float32Buffer {
        for (i in data.position until data.limit) {
            put(data[i])
        }
        return this
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
