package de.fabmax.kool.util

/**
 * Utility class to read arbitrary numbers from a Uint8Buffer. Buffer is expected to be little endian (low byte first).
 */
class DataStream(val data: Uint8Buffer, val byteOffset: Int = 0) {
    var index = 0

    fun hasRemaining() = index < data.capacity

    fun readByte(): Int {
        return data[byteOffset + index++].toInt()
    }

    fun readUByte(): Int {
        return data[byteOffset + index++].toInt() and 0xff
    }

    fun readShort(): Int {
        var s = readUShort()
        if (s > 32767) {
            s -= 65536
        }
        return s
    }

    fun readUShort(): Int {
        var d = 0
        for (i in 0..1) {
            d = d or (readUByte() shl (i * 8))
        }
        return d
    }

    fun readInt(): Int {
        return readUInt()
    }

    fun readUInt(): Int {
        var d = 0
        for (i in 0..3) {
            d = d or (readUByte() shl (i * 8))
        }
        return d
    }

    fun readFloat(): Float {
        return Float.fromBits(readUInt())
    }

    fun readData(len: Int): Uint8Buffer {
        val buf = Uint8Buffer(len)
        for (i in 0 until len) {
            buf[i] = data[index++]
        }
        return buf
    }

    fun skipBytes(nBytes: Int) {
        index += nBytes
    }
}