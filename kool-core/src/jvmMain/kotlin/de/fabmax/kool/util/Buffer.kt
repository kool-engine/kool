package de.fabmax.kool.util

import java.nio.*
import java.nio.Buffer

/**
 * @author fabmax
 */
abstract class GenericBuffer<out B: Buffer>(override val capacity: Int, val buffer: B) : de.fabmax.kool.util.Buffer {
    override var limit: Int
        get() = buffer.limit()
        set(value) { buffer.limit(value) }

    override var position: Int
        get() = buffer.position()
        set(value) { buffer.position(value) }

    override val remaining: Int
        get() = buffer.remaining()

    override fun flip() {
        buffer.flip()
    }

    override fun clear() {
        buffer.clear()
    }
}

/**
 * ByteBuffer buffer implementation
 */
class Uint8BufferImpl(buffer: ByteBuffer) : Uint8Buffer, GenericBuffer<ByteBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()))

    constructor(data: ByteArray) : this(ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())) {
        put(data)
    }

    override fun get(i: Int): Byte {
        return buffer[i]
    }

    override fun set(i: Int, value: Byte) {
        buffer.put(i, value)
    }

    override fun put(data: ByteArray, offset: Int, len: Int): Uint8Buffer {
        buffer.put(data, offset, len)
        return this
    }

    override fun put(value: Byte): Uint8Buffer {
        buffer.put(value)
        return this
    }

    override fun put(data: Uint8Buffer): Uint8Buffer {
        if (data is Uint8BufferImpl) {
            val dataPos = data.position
            buffer.put(data.buffer)
            data.position = dataPos
        } else {
            for (i in data.position until data.limit) {
                buffer.put(data[i])
            }
        }
        return this
    }
}

/**
 * ShortBuffer buffer implementation
 */
class Uint16BufferImpl(buffer: ShortBuffer) : Uint16Buffer, GenericBuffer<ShortBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity * 2).order(ByteOrder.nativeOrder()).asShortBuffer())

    override fun get(i: Int): Short {
        return buffer[i]
    }

    override fun set(i: Int, value: Short) {
        buffer.put(i, value)
    }

    override fun put(data: ShortArray, offset: Int, len: Int): Uint16Buffer {
        buffer.put(data, offset, len)
        return this
    }

    override fun put(value: Short): Uint16Buffer {
        buffer.put(value)
        return this
    }

    override fun put(data: Uint16Buffer): Uint16Buffer {
        if (data is Uint16BufferImpl) {
            val dataPos = data.position
            buffer.put(data.buffer)
            data.position = dataPos
        } else {
            for (i in data.position until data.limit) {
                buffer.put(data[i])
            }
        }
        return this
    }
}

/**
 * IntBuffer buffer implementation
 */
class Uint32BufferImpl(buffer: IntBuffer) : Uint32Buffer, GenericBuffer<IntBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder()).asIntBuffer())

    override fun get(i: Int): Int {
        return buffer[i]
    }

    override fun set(i: Int, value: Int) {
        buffer.put(i, value)
    }

    override fun put(data: IntArray, offset: Int, len: Int): Uint32Buffer {
        buffer.put(data, offset, len)
        return this
    }

    override fun put(value: Int): Uint32Buffer {
        buffer.put(value)
        return this
    }

    override fun put(data: Uint32Buffer): Uint32Buffer {
        if (data is Uint32BufferImpl) {
            val dataPos = data.position
            buffer.put(data.buffer)
            data.position = dataPos
        } else {
            for (i in data.position until data.limit) {
                buffer.put(data[i])
            }
        }
        return this
    }
}

/**
 * FloatBuffer buffer implementation
 */
class Float32BufferImpl(buffer: FloatBuffer) : Float32Buffer, GenericBuffer<FloatBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder()).asFloatBuffer())

    override fun get(i: Int): Float {
        return buffer[i]
    }

    override fun set(i: Int, value: Float) {
        buffer.put(i, value)
    }

    override fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer {
        buffer.put(data, offset, len)
        return this
    }

    override fun put(value: Float): Float32Buffer {
        buffer.put(value)
        return this
    }

    override fun put(data: Float32Buffer): Float32Buffer {
        if (data is Float32BufferImpl) {
            val dataPos = data.position
            buffer.put(data.buffer)
            data.position = dataPos
        } else {
            for (i in data.position until data.limit) {
                buffer.put(data[i])
            }
        }
        return this
    }
}

class MixedBufferImpl(buffer: ByteBuffer) : MixedBuffer, GenericBuffer<ByteBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()))

    override fun putUint8(value: Byte): MixedBuffer {
        buffer.put(value)
        return this
    }

    override fun putUint8(data: ByteArray, offset: Int, len: Int): MixedBuffer {
        buffer.put(data, offset, len)
        return this
    }

    override fun putUint8(data: Uint8Buffer): MixedBuffer {
        if (data is Uint8BufferImpl) {
            val dataPos = data.position
            buffer.put(data.buffer)
            data.position = dataPos
        } else {
            for (i in data.position until data.limit) {
                buffer.put(data[i])
            }
        }
        return this
    }

    override fun putUint16(value: Short): MixedBuffer {
        buffer.putShort(value)
        return this
    }

    override fun putUint16(data: ShortArray, offset: Int, len: Int): MixedBuffer {
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putShort(data[offset + i])
            }
        } else {
            buffer.asShortBuffer().put(data, offset, len)
            position += len * 2
        }
        return this
    }

    override fun putUint16(data: Uint16Buffer): MixedBuffer {
        val len = data.limit - data.position
        if (data !is Uint16BufferImpl || len <= BUFFER_CONV_THRESH) {
            for (i in data.position until data.limit) {
                buffer.putShort(data[i])
            }
        } else {
            val dataPos = data.position
            buffer.asShortBuffer().put(data.buffer)
            data.position = dataPos
            position += len * 2
        }
        return this
    }

    override fun putUint32(value: Int): MixedBuffer {
        buffer.putInt(value)
        return this
    }

    override fun putUint32(data: IntArray, offset: Int, len: Int): MixedBuffer {
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putInt(data[offset + i])
            }
        } else {
            buffer.asIntBuffer().put(data, offset, len)
            position += len * 4
        }
        return this
    }

    override fun putUint32(data: Uint32Buffer): MixedBuffer {
        val len = data.limit - data.position
        if (data !is Uint32BufferImpl || len <= BUFFER_CONV_THRESH) {
            for (i in data.position until data.limit) {
                buffer.putInt(data[i])
            }
        } else {
            val dataPos = data.position
            buffer.asIntBuffer().put(data.buffer)
            data.position = dataPos
            position += len * 4
        }
        return this
    }

    override fun putFloat32(value: Float): MixedBuffer {
        buffer.putFloat(value)
        return this
    }

    override fun putFloat32(data: FloatArray, offset: Int, len: Int): MixedBuffer {
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putFloat(data[offset + i])
            }
        } else {
            buffer.asFloatBuffer().put(data, offset, len)
            position += len * 4
        }
        return this
    }

    override fun putFloat32(data: Float32Buffer): MixedBuffer {
        val len = data.limit - data.position
        if (data !is Float32BufferImpl || len <= BUFFER_CONV_THRESH) {
            for (i in data.position until data.limit) {
                buffer.putFloat(data[i])
            }
        } else {
            val dataPos = data.position
            buffer.asFloatBuffer().put(data.buffer)
            data.position = dataPos
            position += len * 4
        }
        return this
    }

    override fun padding(nBytes: Int): MixedBuffer {
        position += nBytes
        return this
    }

    companion object {
        private const val BUFFER_CONV_THRESH = 4
    }
}

actual fun createUint8Buffer(capacity: Int): Uint8Buffer = Uint8BufferImpl(capacity)

actual fun createUint16Buffer(capacity: Int): Uint16Buffer = Uint16BufferImpl(capacity)

actual fun createUint32Buffer(capacity: Int): Uint32Buffer = Uint32BufferImpl(capacity)

actual fun createFloat32Buffer(capacity: Int): Float32Buffer = Float32BufferImpl(capacity)

actual fun createMixedBuffer(capacity: Int): MixedBuffer = MixedBufferImpl(capacity)

