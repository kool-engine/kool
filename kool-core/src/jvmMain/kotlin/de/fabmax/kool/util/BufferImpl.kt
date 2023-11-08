package de.fabmax.kool.util

import java.nio.*


actual fun Uint8Buffer(capacity: Int): Uint8Buffer = Uint8BufferImpl(capacity)
actual fun Uint16Buffer(capacity: Int): Uint16Buffer = Uint16BufferImpl(capacity)
actual fun Int32Buffer(capacity: Int): Int32Buffer = Int32BufferImpl(capacity)
actual fun Float32Buffer(capacity: Int): Float32Buffer = Float32BufferImpl(capacity)
actual fun MixedBuffer(capacity: Int): MixedBuffer = MixedBufferImpl(capacity)

private typealias NioBuffer = java.nio.Buffer

abstract class GenericBuffer<B: NioBuffer>(override val capacity: Int, val buffer: B) : Buffer {
    override var limit: Int
        get() = buffer.limit()
        set(value) { buffer.limit(value) }

    override var position: Int = 0
        set(value) {
            field = value
            bufferPos = value
        }

    protected var bufferPos: Int
        get() = buffer.position()
        set(value) { buffer.position(value) }

    override fun flip() {
        buffer.flip()
        position = bufferPos
    }

    override fun clear() {
        buffer.clear()
        position = bufferPos
    }
}

class Uint8BufferImpl(buffer: ByteBuffer) : Uint8Buffer, GenericBuffer<ByteBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()))

    constructor(data: ByteArray) : this(ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())) {
        put(data)
    }

    override fun get(i: Int): UByte {
        return buffer[i].toUByte()
    }

    override fun set(i: Int, value: UByte) {
        buffer.put(i, value.toByte())
        //position = max(i + 1, position)
    }

    override fun put(value: UByte): Uint8Buffer {
        buffer.put(value.toByte())
        position++
        return this
    }

    override fun put(data: ByteArray, offset: Int, len: Int): Uint8Buffer {
        buffer.put(data, offset, len)
        position += len
        return this
    }

    override fun put(data: Uint8Buffer): Uint8Buffer {
        data as Uint8BufferImpl
        val len = data.limit - data.position
        val dataPos = data.position
        buffer.put(data.buffer)
        position += len
        data.position = dataPos
        return this
    }
}

class Uint16BufferImpl(buffer: ShortBuffer) : Uint16Buffer, GenericBuffer<ShortBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity * 2).order(ByteOrder.nativeOrder()).asShortBuffer())

    override fun get(i: Int): UShort {
        return buffer[i].toUShort()
    }

    override fun set(i: Int, value: UShort) {
        buffer.put(i, value.toShort())
        //position = max(i + 1, position)
    }

    override fun put(value: UShort): Uint16Buffer {
        buffer.put(value.toShort())
        position++
        return this
    }

    override fun put(data: ShortArray, offset: Int, len: Int): Uint16Buffer {
        buffer.put(data, offset, len)
        position += len
        return this
    }

    override fun put(data: Uint16Buffer): Uint16Buffer {
        data as Uint16BufferImpl
        val len = data.limit - data.position
        val dataPos = data.position
        buffer.put(data.buffer)
        position += len
        data.position = dataPos
        return this
    }
}

class Int32BufferImpl(buffer: IntBuffer) : Int32Buffer, GenericBuffer<IntBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder()).asIntBuffer())

    override fun get(i: Int): Int {
        return buffer[i]
    }

    override fun set(i: Int, value: Int) {
        buffer.put(i, value)
        //position = max(i + 1, position)
    }

    override fun put(value: Int): Int32Buffer {
        buffer.put(value)
        position++
        return this
    }

    override fun put(data: IntArray, offset: Int, len: Int): Int32Buffer {
        buffer.put(data, offset, len)
        position += len
        return this
    }

    override fun put(data: Int32Buffer): Int32Buffer {
        data as Int32BufferImpl
        val len = data.limit - data.position
        val dataPos = data.position
        buffer.put(data.buffer)
        position += len
        data.position = dataPos
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
        //position = max(i + 1, position)
    }

    override fun put(value: Float): Float32Buffer {
        buffer.put(value)
        position++
        return this
    }

    override fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer {
        buffer.put(data, offset, len)
        position += len
        return this
    }

    override fun put(data: Float32Buffer): Float32Buffer {
        data as Float32BufferImpl
        val len = data.limit - data.position
        val dataPos = data.position
        buffer.put(data.buffer)
        position += len
        data.position = dataPos
        return this
    }
}

class MixedBufferImpl(buffer: ByteBuffer) : MixedBuffer, GenericBuffer<ByteBuffer>(buffer.capacity(), buffer) {

    constructor(capacity: Int) : this(ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()))

    override fun putUint8(value: UByte): MixedBuffer {
        buffer.put(value.toByte())
        position++
        return this
    }

    override fun putUint8(data: ByteArray, offset: Int, len: Int): MixedBuffer {
        buffer.put(data, offset, len)
        position += len
        return this
    }

    override fun putUint8(data: Uint8Buffer): MixedBuffer {
        data as Uint8BufferImpl
        val len = data.limit - data.position
        val dataPos = data.position
        buffer.put(data.buffer)
        position += len
        data.position = dataPos
        return this
    }

    override fun putUint16(value: UShort): MixedBuffer {
        buffer.putShort(value.toShort())
        position += SIZEOF_SHORT
        return this
    }

    override fun putUint16(data: ShortArray, offset: Int, len: Int): MixedBuffer {
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putShort(data[offset + i])
            }
        } else {
            buffer.asShortBuffer().put(data, offset, len)
        }
        position += SIZEOF_SHORT * len
        return this
    }

    override fun putUint16(data: Uint16Buffer): MixedBuffer {
        data as Uint16BufferImpl
        val len = data.limit - data.position
        if (len <= BUFFER_CONV_THRESH) {
            for (i in data.position until data.limit) {
                buffer.putShort(data[i].toShort())
            }
        } else {
            val dataPos = data.position
            buffer.asShortBuffer().put(data.buffer)
            data.position = dataPos
        }
        position += SIZEOF_SHORT * len
        return this
    }

    override fun putInt32(value: Int): MixedBuffer {
        buffer.putInt(value)
        position += SIZEOF_INT
        return this
    }

    override fun putInt32(data: IntArray, offset: Int, len: Int): MixedBuffer {
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putInt(data[offset + i])
            }
        } else {
            buffer.asIntBuffer().put(data, offset, len)
        }
        position += SIZEOF_INT * len
        return this
    }

    override fun putInt32(data: Int32Buffer): MixedBuffer {
        data as Int32BufferImpl
        val len = data.limit - data.position
        if (len <= BUFFER_CONV_THRESH) {
            for (i in data.position until data.limit) {
                buffer.putInt(data[i])
            }
        } else {
            val dataPos = data.position
            buffer.asIntBuffer().put(data.buffer)
            data.position = dataPos
        }
        position += SIZEOF_INT * len
        return this
    }

    override fun putFloat32(value: Float): MixedBuffer {
        buffer.putFloat(value)
        position += SIZEOF_FLOAT
        return this
    }

    override fun putFloat32(data: FloatArray, offset: Int, len: Int): MixedBuffer {
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putFloat(data[offset + i])
            }
        } else {
            buffer.asFloatBuffer().put(data, offset, len)
        }
        position += SIZEOF_FLOAT * len
        return this
    }

    override fun putFloat32(data: Float32Buffer): MixedBuffer {
        data as Float32BufferImpl
        val len = data.limit - data.position
        if (len <= BUFFER_CONV_THRESH) {
            for (i in data.position until data.limit) {
                buffer.putFloat(data[i])
            }
        } else {
            val dataPos = data.position
            buffer.asFloatBuffer().put(data.buffer)
            data.position = dataPos
        }
        position += SIZEOF_FLOAT * len
        return this
    }

    override fun putPadding(nBytes: Int): MixedBuffer {
        position += nBytes
        return this
    }

    companion object {
        private const val BUFFER_CONV_THRESH = 16

        private const val SIZEOF_SHORT = 2
        private const val SIZEOF_INT = 4
        private const val SIZEOF_FLOAT = 4
    }
}
