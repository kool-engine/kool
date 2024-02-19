package de.fabmax.kool.pipeline

import de.fabmax.kool.util.*
import kotlin.math.roundToInt

abstract class TextureData {
    var width = 0
        protected set
    var height = 0
        protected set
    var depth = 0
        protected set
    var format = TexFormat.RGBA
        protected set

    abstract val data: Any

    companion object {
        fun createBuffer(format: TexFormat, width: Int, height: Int = 1, depth: Int = 1): Buffer {
            return when {
                format.isByte -> Uint8Buffer(format.channels * width * height * depth)
                format.isF16 || format.isF32 -> Float32Buffer(format.channels * width * height * depth)
                else -> Int32Buffer(format.channels * width * height * depth)
            }
        }

        fun checkBufferFormat(buffer: Buffer, format: TexFormat) {
            when (buffer) {
                is Uint8Buffer -> check(format.isByte) { "Buffer is a Uint8Buffer, format needs to be a byte format but is $format" }
                is Float32Buffer -> check(format.isF16 || format.isF32) { "Buffer is a Float32Buffer, format needs to be a float format but is $format" }
                is Int32Buffer -> check(format.isI32 || format.isU32) { "Buffer is a Int32Buffer, format needs to be an int format but is $format" }
                else -> error("Unsupported buffer format: ${buffer::class.simpleName}, format is $format")
            }
        }

    }
}

class TextureData1d(override val data: Buffer, width: Int, format: TexFormat) : TextureData() {
    init {
        this.width = width
        this.height = 1
        this.depth = 1
        this.format = format
        checkBufferFormat(data, format)
    }

    companion object {
        fun gradient(gradient: ColorGradient, size: Int): TextureData1d {
            val buf = Uint8Buffer(4 * size)
            val color = MutableColor()
            for (i in 0 until size) {
                gradient.getColorInterpolated(i / (size - 1f), color)
                buf[i * 4 + 0] = (color.r * 255f).roundToInt().toUByte()
                buf[i * 4 + 1] = (color.g * 255f).roundToInt().toUByte()
                buf[i * 4 + 2] = (color.b * 255f).roundToInt().toUByte()
                buf[i * 4 + 3] = (color.a * 255f).roundToInt().toUByte()
            }
            return TextureData1d(buf, size, TexFormat.RGBA)
        }

        fun gradientF16(gradient: ColorGradient, size: Int): TextureData1d {
            val buf = Float32Buffer(4 * size)
            val color = MutableColor()
            for (i in 0 until size) {
                gradient.getColorInterpolated(i / (size - 1f), color)
                buf[i * 4 + 0] = color.r
                buf[i * 4 + 1] = color.g
                buf[i * 4 + 2] = color.b
                buf[i * 4 + 3] = color.a
            }
            return TextureData1d(buf, size, TexFormat.RGBA_F16)
        }
    }
}

/**
 * Buffer based 2d texture data. Texture data can be generated and edited procedurally. Layout and format of data
 * is specified by the format parameter. The buffer size must match the texture size and data format.
 *
 * @param data   texture data buffer, must have a size of width * height * bytes-per-pixel
 * @param width  width of texture in pixels
 * @param height height of texture in pixels
 * @param format texture data format
 */
class TextureData2d(override val data: Buffer, width: Int, height: Int, format: TexFormat) : TextureData() {
    init {
        this.width = width
        this.height = height
        this.depth = 1
        this.format = format
        checkBufferFormat(data, format)
    }

    companion object {
        fun singleColor(color: Color): TextureData2d {
            val buf = Uint8Buffer(4)
            buf[0] = (color.r * 255f).roundToInt().toUByte()
            buf[1] = (color.g * 255f).roundToInt().toUByte()
            buf[2] = (color.b * 255f).roundToInt().toUByte()
            buf[3] = (color.a * 255f).roundToInt().toUByte()
            return TextureData2d(buf, 1, 1, TexFormat.RGBA)
        }
    }
}

class TextureData3d(override val data: Buffer, width: Int, height: Int, depth: Int, format: TexFormat) : TextureData() {
    init {
        this.width = width
        this.height = height
        this.depth = depth
        this.format = format
        checkBufferFormat(data, format)
    }
}

class TextureDataCube(
    val front: TextureData,
    val back: TextureData,
    val left: TextureData,
    val right: TextureData,
    val up: TextureData,
    val down: TextureData
) : TextureData() {

    val posX: TextureData get() = right
    val negX: TextureData get() = left
    val posY: TextureData get() = up
    val negY: TextureData get() = down
    val posZ: TextureData get() = back
    val negZ: TextureData get() = front

    init {
        width = front.width
        height = front.height
        depth = 1
        format = front.format
    }

    override val data: Any
        get() = front.data
}