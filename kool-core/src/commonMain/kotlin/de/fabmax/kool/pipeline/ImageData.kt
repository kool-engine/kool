package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.ImageData.Companion.checkBufferFormat
import de.fabmax.kool.util.*
import kotlin.math.roundToInt

/**
 * Base interface for all types of loaded image data. Image data is typically used as source to load textures.
 * However, the *buffered* image data variants can be directly accessed and used to load arbitrary sampled data,
 * as, e.g., height fields.
 */
sealed interface ImageData {
    val format: TexFormat

    companion object {
        fun createBuffer(format: TexFormat, width: Int, height: Int = 1, depth: Int = 1): Buffer {
            return when {
                format.isByte -> Uint8Buffer(format.channels * width * height * depth)
                format.isF16 || format.isF32 -> Float32Buffer(format.channels * width * height * depth)
                format.isI32 || format.isU32 -> Int32Buffer(format.channels * width * height * depth)
                else -> error("Unsupported texture format: $format")
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

sealed interface BufferedImageData {
    val data: Buffer
}

interface ImageData1d : ImageData {
    val width: Int
}

interface ImageData2d : ImageData {
    val width: Int
    val height: Int
    val size: Vec2i get() = Vec2i(width, height)
}

interface ImageData3d : ImageData {
    val width: Int
    val height: Int
    val depth: Int
    val size: Vec3i get() = Vec3i(width, height, depth)
}

class BufferedImageData1d(
    override val data: Buffer,
    override val width: Int,
    override val format: TexFormat
) : ImageData1d, BufferedImageData {

    init {
        checkBufferFormat(data, format)
        val dataSize = width * format.channels
        check(data.capacity == dataSize) {
            "Invalid buffer size: ${data.capacity} does not match image dimensions $width x ${format.channels} channels ($dataSize)"
        }
    }

    companion object {
        fun gradient(gradient: ColorGradient, size: Int): BufferedImageData1d {
            val buf = Uint8Buffer(4 * size)
            val color = MutableColor()
            for (i in 0 until size) {
                gradient.getColorInterpolated(i / (size - 1f), color)
                buf[i * 4 + 0] = (color.r * 255f).roundToInt().toUByte()
                buf[i * 4 + 1] = (color.g * 255f).roundToInt().toUByte()
                buf[i * 4 + 2] = (color.b * 255f).roundToInt().toUByte()
                buf[i * 4 + 3] = (color.a * 255f).roundToInt().toUByte()
            }
            return BufferedImageData1d(buf, size, TexFormat.RGBA)
        }

        fun gradientF16(gradient: ColorGradient, size: Int): BufferedImageData1d {
            val buf = Float32Buffer(4 * size)
            val color = MutableColor()
            for (i in 0 until size) {
                gradient.getColorInterpolated(i / (size - 1f), color)
                buf[i * 4 + 0] = color.r
                buf[i * 4 + 1] = color.g
                buf[i * 4 + 2] = color.b
                buf[i * 4 + 3] = color.a
            }
            return BufferedImageData1d(buf, size, TexFormat.RGBA_F16)
        }
    }
}

/**
 * Buffer based 2d image data. Image data can be generated and edited procedurally. Layout and format of data
 * is specified by the format parameter. The buffer size must match the texture size and data format.
 *
 * @param data   texture data buffer, must have a size of width * height * bytes-per-pixel
 * @param width  width of texture in pixels
 * @param height height of texture in pixels
 * @param format texture data format
 */
class BufferedImageData2d(
    override val data: Buffer,
    override val width: Int,
    override val height: Int,
    override val format: TexFormat
) : ImageData2d, BufferedImageData {

    init {
        checkBufferFormat(data, format)
        val dataSize = width * height * format.channels
        check(data.capacity == dataSize) {
            "Invalid buffer size: ${data.capacity} does not match image dimensions $width x $height x ${format.channels} channels ($dataSize)"
        }
    }

    companion object {
        fun singleColor(color: Color): BufferedImageData2d {
            val buf = Uint8Buffer(4)
            buf[0] = (color.r * 255f).roundToInt().toUByte()
            buf[1] = (color.g * 255f).roundToInt().toUByte()
            buf[2] = (color.b * 255f).roundToInt().toUByte()
            buf[3] = (color.a * 255f).roundToInt().toUByte()
            return BufferedImageData2d(buf, 1, 1, TexFormat.RGBA)
        }
    }
}

class BufferedImageData3d(
    override val data: Buffer,
    override val width: Int,
    override val height: Int,
    override val depth: Int,
    override val format: TexFormat
) : ImageData3d, BufferedImageData {

    init {
        checkBufferFormat(data, format)
        val dataSize = width * height * depth * format.channels
        check(data.capacity == dataSize) {
            "Invalid buffer size: ${data.capacity} does not match image dimensions $width x $height x $depth x ${format.channels} channels ($dataSize)"
        }
    }
}

class ImageDataCube(
    val front: ImageData2d,
    val back: ImageData2d,
    val left: ImageData2d,
    val right: ImageData2d,
    val up: ImageData2d,
    val down: ImageData2d
) : ImageData {

    val posX: ImageData2d get() = right
    val negX: ImageData2d get() = left
    val posY: ImageData2d get() = up
    val negY: ImageData2d get() = down
    val posZ: ImageData2d get() = back
    val negZ: ImageData2d get() = front

    val width = front.width
    val height = front.height
    val size: Vec2i get() = Vec2i(width, height)
    override val format = front.format

    init {
        check(
            front.size == back.size &&
            front.size == left.size &&
            front.size == right.size &&
            front.size == up.size &&
            front.size == down.size
        )
        check(
            front.format == back.format &&
            front.format == left.format &&
            front.format == right.format &&
            front.format == up.format &&
            front.format == down.format
        )
    }
}

class ImageDataCubeArray(val cubes: List<ImageDataCube>) : ImageData {
    val width: Int = cubes[0].width
    val height: Int = cubes[0].height
    val slices: Int = cubes.size
    override val format: TexFormat = cubes[0].format

    init {
        val ref = cubes[0]
        for (i in 1 until cubes.size) {
            val map = cubes[i]
            check(ref.width == map.width && ref.height == map.height && ref.format == map.format) {
                "All cube maps must have the same dimensions and format"
            }
        }
    }
}


fun ImageData1d.toLazyTexture(props: TextureProps = TextureProps(), name: String = UniqueId.nextId("Texture1d")) =
    Texture1d(this, props, name)

fun ImageData2d.toLazyTexture(props: TextureProps = TextureProps(), name: String = UniqueId.nextId("Texture2d")) =
    Texture2d(this, props, name)

fun ImageData3d.toLazyTexture(props: TextureProps = TextureProps(), name: String = UniqueId.nextId("Texture3d")) =
    Texture3d(this, props, name)

fun ImageDataCube.toLazyTexture(props: TextureProps = TextureProps(), name: String = UniqueId.nextId("TextureCube")) =
    TextureCube(this, props, name)


suspend fun ImageData1d.toTexture(props: TextureProps = TextureProps(), name: String = UniqueId.nextId("Texture1d")) =
    Texture1d(props, name).apply { upload(this@toTexture) }

suspend fun ImageData2d.toTexture(props: TextureProps = TextureProps(), name: String = UniqueId.nextId("Texture2d")) =
    Texture2d(props, name).apply { upload(this@toTexture) }

suspend fun ImageData3d.toTexture(props: TextureProps = TextureProps(), name: String = UniqueId.nextId("Texture3d")) =
    Texture3d(props, name).apply { upload(this@toTexture) }

suspend fun ImageDataCube.toTexture(props: TextureProps = TextureProps(), name: String = UniqueId.nextId("TextureCube")) =
    TextureCube(props, name).apply { upload(this@toTexture) }
