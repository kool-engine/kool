package de.fabmax.kool.modules.audio

import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Uint8Buffer


class WavFile(fileData: Uint8Buffer) {
    var wFormatTag: Int = 0
        private set
    var wChannels: Int = 0
        private set
    var dwSamplesPerSec: Int = 0
        private set
    var dwAvgBytesPerSec: Int = 0
        private set
    var wBlockAlign: Int = 0
        private set
    var wBitsPerSample: Int = 0
        private set
    var numSamples = 0
        private set

    lateinit var channels: Array<FloatArray>
        private set

    init {
        val inStream = ByteArrayInputStream(fileData)
        while (inStream.available() > 0) {
            inStream.nextChunk()
        }
    }

    private fun ByteArrayInputStream.nextChunk() {
        val headerData = ByteArray(8)
        if (read(headerData) != 8) {
            throw IllegalStateException("Unexpected end of file")
        }
        val chunkId = headerData.toCharArray().concatToString(0, 4)
        val chunkSize = byteToU32(headerData, 4)
        when (chunkId) {
            "RIFF" -> readRiffHeaderChunk()
            "fmt " -> readFormatChunk(chunkSize)
            "data" -> readDataChunk(chunkSize)
            else -> skip(chunkSize.toInt())
        }
    }

    private fun ByteArrayInputStream.readRiffHeaderChunk() {
        val riffTypeData = ByteArray(4)
        if (read(riffTypeData) != 4) {
            throw IllegalStateException("Unexpected end of file")
        }
        if (riffTypeData.toCharArray().concatToString() != "WAVE") {
            throw IllegalStateException("Unexpected data type: $riffTypeData")
        }
    }

    private fun ByteArrayInputStream.readFormatChunk(size: UInt) {
        val fmtData = ByteArray(size.toInt())
        if (read(fmtData) != size.toInt()) {
            throw IllegalStateException("Unexpected end of file")
        }

        wFormatTag = byteToU16(fmtData, 0).toInt()
        wChannels = byteToU16(fmtData, 2).toInt()
        dwSamplesPerSec = byteToU32(fmtData, 4).toInt()
        dwAvgBytesPerSec = byteToU32(fmtData, 8).toInt()
        wBlockAlign = byteToU16(fmtData, 12).toInt()

        if (wFormatTag == 1 && size >= 16U) {
            wBitsPerSample = byteToU16(fmtData, 14).toInt()
        } else {
            throw IllegalStateException("Unsupported format: $wFormatTag, fmt size: $size")
        }
    }

    private fun ByteArrayInputStream.readDataChunk(size: UInt) {
        numSamples = (size / wBlockAlign.toUInt()).toInt()
        channels = Array(wChannels) { FloatArray(numSamples) }

        val frameData = ByteArray(wBlockAlign)
        for (iFrame in 0 until numSamples) {
            if (read(frameData) != wBlockAlign) {
                throw IllegalStateException("Unexpected end of file")
            }

            for (i in 0 until wChannels) {
                channels[i][iFrame] = when (wBitsPerSample) {
                    8 -> frameData[i] / 128f
                    16 -> byteToU16(frameData, i * 2).toShort() / 32768f
                    24 -> byteToI24(frameData, i * 3) / 8388608f
                    32 -> byteToU32(frameData, i * 4).toInt() / 2147483648f
                    else -> throw IllegalStateException("Unsupported bits per sample: $wBitsPerSample")
                }
            }
        }
    }

    private fun byteToU32(buf: ByteArray, off: Int) = byteToU32(buf[off].toUByte(), buf[off + 1].toUByte(), buf[off + 2].toUByte(), buf[off + 3].toUByte())

    private fun byteToU32(b0: UByte, b1: UByte, b2: UByte, b3: UByte): UInt {
        return (b3.toUInt() shl 24) or
                (b2.toUInt() shl 16) or
                (b1.toUInt() shl 8) or
                (b0.toUInt() shl 0)
    }

    private fun byteToI24(buf: ByteArray, off: Int): Int {
        val u24 = byteToU24(buf[off].toUByte(), buf[off + 1].toUByte(), buf[off + 2].toUByte()).toInt()
        return if (u24 and 0x800000 != 0) {
            -(u24 and 0x7fffff)
        } else {
            u24
        }
    }

    private fun byteToU24(b0: UByte, b1: UByte, b2: UByte): UInt {
        return (b2.toUInt() shl 16) or (b1.toUInt() shl 8) or b0.toUInt()
    }

    private fun byteToU16(buf: ByteArray, off: Int) = byteToU16(buf[off].toUByte(), buf[off + 1].toUByte())

    private fun byteToU16(b0: UByte, b1: UByte): UInt {
        return (b1.toUInt() shl 8) or b0.toUInt()
    }

    private fun ByteArray.toCharArray(): CharArray {
        val arr = CharArray(size)
        for (i in indices) {
            arr[i] = this[i].toInt().toChar()
        }
        return arr
    }

    private class ByteArrayInputStream(val data: Uint8Buffer) {
        private var pos = 0

        fun read(buf: ByteArray): Int {
            val len = buf.size.clamp(0, available())
            for (i in 0 until len) {
                buf[i] = data[pos++].toByte()
            }
            return len
        }

        fun available() = data.capacity - pos

        fun skip(n: Int): Int {
            val skipN = n.clamp(0, available())
            pos += skipN
            return skipN
        }
    }
}