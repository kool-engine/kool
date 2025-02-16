package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.pipeline.ImageData.Companion.checkBufferFormat
import de.fabmax.kool.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Base interface for all types of loaded image data. Image data is typically used as source to load textures.
 * However, the *buffered* image data variants can be directly accessed and used to load arbitrary sampled data,
 * as, e.g., height fields.
 */
sealed interface ImageData {
    /**
     * Unique name / ID of this image data. It is used during texture load to check if a texture with the same
     * content already exists (and if so omits loading it again). This means that, if two image data objects with
     * different content share the same ID, loading the second one will yield a wrong texture.
     */
    val id: String

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

        fun idForImageData(typeName: String, data: Buffer): String {
            val hash = LongHash {
                when (data) {
                    is Float32Buffer -> for (i in 0 until data.capacity) this += data[i]
                    is Int32Buffer -> for (i in 0 until data.capacity) this += data[i]
                    is MixedBuffer -> for (i in 0 until data.capacity) this += data.getInt8(i)
                    is Uint16Buffer -> for (i in 0 until data.capacity) this += data[i].toInt()
                    is Uint8Buffer -> for (i in 0 until data.capacity) this += data[i].toByte()
                    else -> this += UniqueId.nextId()
                }
            }
            return "$typeName-${abs(hash.hash)}"
        }

        fun idForImageIds(typeName: String, images: List<ImageData>): String {
            val hash = LongHash {
                images.forEach { this += it.id }
            }
            return "$typeName-${abs(hash.hash)}"
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
    override val format: TexFormat,
    override val id: String = ImageData.idForImageData("BufferedImageData1d", data)
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
    override val format: TexFormat,
    override val id: String = ImageData.idForImageData("BufferedImageData2d", data)
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
            return BufferedImageData2d(buf, 1, 1, TexFormat.RGBA, id = "SingleColorData[$color]")
        }
    }
}

class BufferedImageData3d(
    override val data: Buffer,
    override val width: Int,
    override val height: Int,
    override val depth: Int,
    override val format: TexFormat,
    override val id: String = ImageData.idForImageData("BufferedImageData3d", data)
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
    val negX: ImageData2d,
    val posX: ImageData2d,
    val negY: ImageData2d,
    val posY: ImageData2d,
    val negZ: ImageData2d,
    val posZ: ImageData2d,
    override val id: String = "ImageDataCube[x+:${posX.id},x-:${negX.id},y+:${posY.id},y-:${negY.id},z+:${posZ.id},z-:${negZ.id}]"
) : ImageData {

    val width = negZ.width
    val height = negZ.height
    val size: Vec2i get() = Vec2i(width, height)
    override val format = negZ.format

    init {
        check(
            negZ.size == posZ.size &&
            negZ.size == negX.size &&
            negZ.size == posX.size &&
            negZ.size == posY.size &&
            negZ.size == negY.size
        )
        check(
            negZ.format == posZ.format &&
            negZ.format == negX.format &&
            negZ.format == posX.format &&
            negZ.format == posY.format &&
            negZ.format == negY.format
        )
    }
}

class ImageData2dArray(
    val images: List<ImageData2d>,
    override val id: String = ImageData.idForImageIds("ImageData2dArray", images)
) : ImageData3d {
    override val width: Int = images[0].width
    override val height: Int = images[0].height
    override val depth: Int = images.size
    override val format: TexFormat = images[0].format

    init {
        val ref = images[0]
        for (i in 1 until images.size) {
            val map = images[i]
            check(ref.width == map.width && ref.height == map.height && ref.format == map.format) {
                "All images must have the same dimensions and format"
            }
        }
    }
}

class ImageDataCubeArray(
    val cubes: List<ImageDataCube>,
    override val id: String = ImageData.idForImageIds("ImageDataCubeArray", cubes)
) : ImageData {
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


fun ImageData1d.toLazyTexture(
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture1d")
) = Texture1d(this, mipMapping, samplerSettings, name)

fun ImageData2d.toLazyTexture(
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture2d")
) = Texture2d(this, mipMapping, samplerSettings, name)

fun ImageData3d.toLazyTexture(
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture3d")
) = Texture3d(this, mipMapping, samplerSettings, name)

fun ImageDataCube.toLazyTexture(
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("TextureCube")
) = TextureCube(this, mipMapping, samplerSettings, name)


suspend fun ImageData1d.toTexture(
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture1d")
) = Texture1d(format, mipMapping, samplerSettings, name).apply { upload(this@toTexture) }

suspend fun ImageData2d.toTexture(
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture2d")
) = Texture2d(format, mipMapping, samplerSettings, name).apply { upload(this@toTexture) }

suspend fun ImageData3d.toTexture(
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("Texture3d")
) = Texture3d(format, mipMapping, samplerSettings, name).apply { upload(this@toTexture) }

suspend fun ImageDataCube.toTexture(
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    name: String = UniqueId.nextId("TextureCube")
) = TextureCube(format, mipMapping, samplerSettings, name).apply { upload(this@toTexture) }
