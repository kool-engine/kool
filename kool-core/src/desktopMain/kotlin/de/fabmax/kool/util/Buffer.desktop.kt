package de.fabmax.kool.util

import de.fabmax.kool.util.MixedBuffer.Companion.SIZEOF_FLOAT
import de.fabmax.kool.util.MixedBuffer.Companion.SIZEOF_INT
import de.fabmax.kool.util.MixedBuffer.Companion.SIZEOF_SHORT
import java.nio.*

private typealias NioBuffer = java.nio.Buffer

actual fun Uint8Buffer(capacity: Int, isAutoLimit: Boolean): Uint8Buffer = Uint8BufferImpl(capacity, isAutoLimit)
actual fun Uint16Buffer(capacity: Int, isAutoLimit: Boolean): Uint16Buffer = Uint16BufferImpl(capacity, isAutoLimit)
actual fun Int32Buffer(capacity: Int, isAutoLimit: Boolean): Int32Buffer = Int32BufferImpl(capacity, isAutoLimit)
actual fun Float32Buffer(capacity: Int, isAutoLimit: Boolean): Float32Buffer = Float32BufferImpl(capacity, isAutoLimit)
actual fun MixedBuffer(capacity: Int, isAutoLimit: Boolean): MixedBuffer = MixedBufferImpl(capacity, isAutoLimit)

inline fun <R> Uint8Buffer.useRaw(block: (ByteBuffer) -> R): R = (this as Uint8BufferImpl).useRaw(block)
inline fun <R> Uint16Buffer.useRaw(block: (ShortBuffer) -> R): R = (this as Uint16BufferImpl).useRaw(block)
inline fun <R> Int32Buffer.useRaw(block: (IntBuffer) -> R): R = (this as Int32BufferImpl).useRaw(block)
inline fun <R> Float32Buffer.useRaw(block: (FloatBuffer) -> R): R = (this as Float32BufferImpl).useRaw(block)
inline fun <R> MixedBuffer.useRaw(block: (ByteBuffer) -> R): R = (this as MixedBufferImpl).useRaw(block)


fun Uint8BufferImpl(capacity: Int, isAutoLimit: Boolean = false) = Uint8BufferImpl(
    buffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()),
    isAutoLimit = isAutoLimit
)

fun Uint8BufferImpl(data: ByteArray): Uint8BufferImpl {
    val buf = Uint8BufferImpl(ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder()), false)
    buf.put(data)
    return buf
}

fun Uint16BufferImpl(capacity: Int, isAutoLimit: Boolean = false) = Uint16BufferImpl(
    buffer = ByteBuffer.allocateDirect(capacity * 2).order(ByteOrder.nativeOrder()).asShortBuffer(),
    isAutoLimit = isAutoLimit
)

fun Int32BufferImpl(capacity: Int, isAutoLimit: Boolean = false) = Int32BufferImpl(
    buffer = ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder()).asIntBuffer(),
    isAutoLimit = isAutoLimit
)

fun Float32BufferImpl(capacity: Int, isAutoLimit: Boolean = false) = Float32BufferImpl(
    buffer = ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(),
    isAutoLimit = isAutoLimit
)

fun MixedBufferImpl(capacity: Int, isAutoLimit: Boolean = false) = MixedBufferImpl(
    buffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()),
    isAutoLimit = isAutoLimit
)

abstract class GenericBuffer<B: NioBuffer>(
    override val capacity: Int,
    protected val buffer: B,
    isAutoLimit: Boolean
) : Buffer {
    @PublishedApi
    internal var modCount = 0

    override var isAutoLimit: Boolean = isAutoLimit
        set(value) {
            field = value
            if (value) {
                buffer.limit(capacity)
            }
        }

    override var limit: Int
        get() = if (isAutoLimit) pos else buffer.limit()
        set(value) {
            modCount++
            buffer.limit(value)
            isAutoLimit = false
        }

    override var position: Int
        get() = pos
        set(value) {
            modCount++
            buffer.position(value)
            pos = value
        }

    protected var pos = 0

    override fun clear() {
        modCount++
        buffer.clear()
        position = 0
    }

    fun getRawBuffer(): B {
        buffer.position(0)
        if (isAutoLimit) {
            buffer.limit(pos)
        }
        return buffer
    }

    fun finishRawBuffer() {
        if (isAutoLimit) {
            buffer.limit(capacity)
        }
        buffer.position(pos)
    }

    inline fun <R> useRaw(block: (B) -> R): R {
        val modBefore = modCount
        val result = block(getRawBuffer())
        finishRawBuffer()
        val modAfter = modCount
        if (modBefore != modAfter) {
            logE { "Buffer was modified externally while used raw" }
        }
        return result
    }
}

class Uint8BufferImpl(
    buffer: ByteBuffer,
    isAutoLimit: Boolean = false
) : GenericBuffer<ByteBuffer>(buffer.capacity(), buffer, isAutoLimit), Uint8Buffer {
    override fun get(i: Int): UByte {
        return buffer[i].toUByte()
    }

    override fun set(i: Int, value: UByte) {
        modCount++
        buffer.put(i, value.toByte())
    }

    override fun put(value: UByte): Uint8Buffer {
        modCount++
        buffer.put(value.toByte())
        pos++
        return this
    }

    override fun put(data: ByteArray, offset: Int, len: Int): Uint8Buffer {
        modCount++
        buffer.put(data, offset, len)
        pos += len
        return this
    }

    override fun put(data: Uint8Buffer): Uint8Buffer {
        modCount++
        data.useRaw {
            buffer.put(it)
            pos += data.limit
        }
        return this
    }
}

class Uint16BufferImpl(
    buffer: ShortBuffer,
    isAutoLimit: Boolean = false
) : GenericBuffer<ShortBuffer>(buffer.capacity(), buffer, isAutoLimit), Uint16Buffer {
    override fun get(i: Int): UShort {
        return buffer[i].toUShort()
    }

    override fun set(i: Int, value: UShort) {
        modCount++
        buffer.put(i, value.toShort())
    }

    override fun put(value: UShort): Uint16Buffer {
        modCount++
        buffer.put(value.toShort())
        pos++
        return this
    }

    override fun put(data: ShortArray, offset: Int, len: Int): Uint16Buffer {
        modCount++
        buffer.put(data, offset, len)
        pos += len
        return this
    }

    override fun put(data: Uint16Buffer): Uint16Buffer {
        modCount++
        data.useRaw {
            buffer.put(it)
            pos += data.limit
        }
        return this
    }
}

class Int32BufferImpl(buffer: IntBuffer, isAutoLimit: Boolean = false) :
    GenericBuffer<IntBuffer>(buffer.capacity(), buffer, isAutoLimit), Int32Buffer
{
    override fun get(i: Int): Int {
        return buffer[i]
    }

    override fun set(i: Int, value: Int) {
        modCount++
        buffer.put(i, value)
    }

    override fun put(value: Int): Int32Buffer {
        modCount++
        buffer.put(value)
        pos++
        return this
    }

    override fun put(data: IntArray, offset: Int, len: Int): Int32Buffer {
        modCount++
        buffer.put(data, offset, len)
        pos += len
        return this
    }

    override fun put(data: Int32Buffer): Int32Buffer {
        modCount++
        data.useRaw {
            buffer.put(it)
            pos += data.limit
        }
        return this
    }
}

/**
 * FloatBuffer buffer implementation
 */
class Float32BufferImpl(buffer: FloatBuffer, isAutoLimit: Boolean = false) :
    GenericBuffer<FloatBuffer>(buffer.capacity(), buffer, isAutoLimit), Float32Buffer
{
    override fun get(i: Int): Float {
        return buffer[i]
    }

    override fun set(i: Int, value: Float) {
        modCount++
        buffer.put(i, value)
    }

    override fun put(value: Float): Float32Buffer {
        modCount++
        buffer.put(value)
        pos++
        return this
    }

    override fun put(data: FloatArray, offset: Int, len: Int): Float32Buffer {
        modCount++
        buffer.put(data, offset, len)
        pos += len
        return this
    }

    override fun put(data: Float32Buffer): Float32Buffer {
        modCount++
        data.useRaw {
            buffer.put(it)
            pos += data.limit
        }
        return this
    }
}

class MixedBufferImpl(buffer: ByteBuffer, isAutoLimit: Boolean = false) :
    GenericBuffer<ByteBuffer>(buffer.capacity(), buffer, isAutoLimit), MixedBuffer
{
    override fun put(data: MixedBuffer): MixedBuffer {
        modCount++
        data.useRaw { buffer.put(it) }
        pos += data.limit
        return this
    }

    override fun putUint8(value: UByte): MixedBuffer {
        modCount++
        buffer.put(value.toByte())
        pos++
        return this
    }

    override fun putUint8(data: ByteArray, offset: Int, len: Int): MixedBuffer {
        modCount++
        buffer.put(data, offset, len)
        pos += len
        return this
    }

    override fun putUint8(data: Uint8Buffer): MixedBuffer {
        modCount++
        data.useRaw { buffer.put(it) }
        pos += data.limit
        return this
    }

    override fun getUint8(byteIndex: Int): UByte {
        return buffer.get(byteIndex).toUByte()
    }

    override fun setUint8(byteIndex: Int, value: UByte): MixedBuffer {
        modCount++
        buffer.put(byteIndex, value.toByte())
        return this
    }

    override fun putUint16(value: UShort): MixedBuffer {
        modCount++
        buffer.putShort(value.toShort())
        pos += SIZEOF_SHORT
        return this
    }

    override fun putUint16(data: ShortArray, offset: Int, len: Int): MixedBuffer {
        modCount++
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putShort(data[offset + i])
            }
        } else {
            buffer.asShortBuffer().put(data, offset, len)
        }
        pos += SIZEOF_SHORT * len
        return this
    }

    override fun putUint16(data: Uint16Buffer): MixedBuffer {
        modCount++
        if (data.limit <= BUFFER_CONV_THRESH) {
            for (i in 0 until data.limit) {
                buffer.putShort(data[i].toShort())
            }
        } else {
            data.useRaw {
                buffer.asShortBuffer().put(it)
            }
        }
        pos += SIZEOF_SHORT * data.limit
        return this
    }

    override fun getUint16(byteIndex: Int): UShort {
        return buffer.getShort(byteIndex).toUShort()
    }

    override fun setUint16(byteIndex: Int, value: UShort): MixedBuffer {
        modCount++
        buffer.putShort(byteIndex, value.toShort())
        return this
    }

    override fun putInt32(value: Int): MixedBuffer {
        modCount++
        buffer.putInt(value)
        pos += SIZEOF_INT
        return this
    }

    override fun putInt32(data: IntArray, offset: Int, len: Int): MixedBuffer {
        modCount++
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putInt(data[offset + i])
            }
        } else {
            buffer.asIntBuffer().put(data, offset, len)
        }
        pos += SIZEOF_INT * len
        return this
    }

    override fun putInt32(data: Int32Buffer): MixedBuffer {
        modCount++
        if (data.limit <= BUFFER_CONV_THRESH) {
            for (i in 0 until data.limit) {
                buffer.putInt(data[i])
            }
        } else {
            data.useRaw {
                buffer.asIntBuffer().put(it)
            }
        }
        pos += SIZEOF_INT * data.limit
        return this
    }

    override fun getInt32(byteIndex: Int): Int {
        return buffer.getInt(byteIndex)
    }

    override fun setInt32(byteIndex: Int, value: Int): MixedBuffer {
        modCount++
        buffer.putInt(byteIndex, value)
        return this
    }

    override fun putFloat32(value: Float): MixedBuffer {
        modCount++
        buffer.putFloat(value)
        pos += SIZEOF_FLOAT
        return this
    }

    override fun putFloat32(data: FloatArray, offset: Int, len: Int): MixedBuffer {
        modCount++
        if (len <= BUFFER_CONV_THRESH) {
            for (i in 0 until len) {
                buffer.putFloat(data[offset + i])
            }
        } else {
            buffer.asFloatBuffer().put(data, offset, len)
        }
        pos += SIZEOF_FLOAT * len
        return this
    }

    override fun putFloat32(data: Float32Buffer): MixedBuffer {
        modCount++
        if (data.limit <= BUFFER_CONV_THRESH) {
            for (i in 0 until data.limit) {
                buffer.putFloat(data[i])
            }
        } else {
            data.useRaw {
                buffer.asFloatBuffer().put(it)
            }
        }
        pos += SIZEOF_FLOAT * data.limit
        return this
    }

    override fun getFloat32(byteIndex: Int): Float {
        return buffer.getFloat(byteIndex)
    }

    override fun setFloat32(byteIndex: Int, value: Float): MixedBuffer {
        modCount++
        buffer.putFloat(byteIndex, value)
        return this
    }

    override fun putPadding(nBytes: Int): MixedBuffer {
        modCount++
        pos += nBytes
        buffer.position(pos)
        return this
    }

    companion object {
        private const val BUFFER_CONV_THRESH = 16
    }
}
