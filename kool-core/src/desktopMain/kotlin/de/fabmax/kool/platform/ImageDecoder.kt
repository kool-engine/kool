package de.fabmax.kool.platform

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryUtil
import java.awt.RenderingHints
import java.awt.Transparency
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.imageio.ImageIO
import kotlin.math.roundToInt

object ImageDecoder {
    private val imageIoLock = Any()

    fun loadImage(inputStream: InputStream, props: TextureProps?): TextureData2d {
        return if (props?.resolveSize != null) {
            // load and rescale image -> use ImageIO and BufferedImage to re-render image at desired size
            loadImageImageIO(inputStream, props)
        } else {
            // load image at native resolution -> use stb_image (>10x faster than ImageIO...)
            loadImageStb(inputStream, props)
        }
    }

    private fun loadImageStb(inputStream: InputStream, props: TextureProps?): TextureData2d {
        val imageData = inputStream.readAllBytes().toBuffer()

        return memStack {
            val w: IntBuffer = mallocInt(1)
            val h: IntBuffer = mallocInt(1)
            val channels: IntBuffer = mallocInt(1)
            val desiredChannels = props?.format?.channels ?: 4

            val rawBuffer = if (props?.format?.isF16 == true || props?.format?.isF32 == true) {
                imageData.useRaw { raw ->
                    stbi_loadf_from_memory(raw, w, h, channels, desiredChannels)
                }
            } else {
                imageData.useRaw { raw ->
                    stbi_load_from_memory(raw, w, h, channels, desiredChannels)
                }
            }
            checkNotNull(rawBuffer) { "Failed to load image: ${stbi_failure_reason()}" }

            val managedBuffer = when (rawBuffer) {
                is ByteBuffer -> {
                    val managed = Uint8Buffer(rawBuffer.capacity())
                    managed.useRaw { it.put(rawBuffer) }
                    managed
                }
                is FloatBuffer -> {
                    val managed = Float32Buffer(rawBuffer.capacity())
                    managed.useRaw { it.put(rawBuffer) }
                    managed
                }
                else -> error("unreachable")
            }
            MemoryUtil.memFree(rawBuffer)
            TextureData2d(managedBuffer, w[0], h[0], props?.format ?: TexFormat.RGBA)
        }
    }

    private fun loadImageImageIO(inputStream: InputStream, props: TextureProps?): TextureData2d {
        var img = synchronized(imageIoLock) {
            // ImageIO is not thread-safe, making loading multiple images even slower
            ImageIO.read(inputStream)
        }
        if (props?.resolveSize != null && props.resolveSize != Vec2i(img.width, img.height)) {
            img = resizeImage(img, props.resolveSize)
        }
        return loadBufferedImage(img, props)
    }

    fun loadBufferedImage(image: BufferedImage, props: TextureProps?): TextureData2d {
        return TextureData2d(image.toBuffer(props?.format), image.width, image.height, props?.format ?: image.preferredFormat)
    }

    private fun resizeImage(img: BufferedImage, size: Vec2i): BufferedImage {
        val scaleX = size.x.toDouble() / img.width
        val scaleY = size.y.toDouble() / img.height
        val resized = BufferedImage((img.width * scaleX).roundToInt(), (img.height * scaleY).roundToInt(), BufferedImage.TYPE_4BYTE_ABGR)
        val g = resized.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        g.drawImage(img, AffineTransform().apply { scale(scaleX, scaleY) }, null)
        g.dispose()
        return resized
    }

    private val BufferedImage.preferredFormat: TexFormat
        get() {
        val isFloat = colorModel.componentSize.any { it > 8 }
        return if (isFloat) TexFormat.RGBA_F16 else TexFormat.RGBA
    }

    private fun BufferedImage.toBuffer(dstFormat: TexFormat?): Buffer {
        val dstFmt = dstFormat ?: this.preferredFormat
        return if (dstFmt.isF16 || dstFmt.isF32) {
            bufferedImageToFloat32Buffer(this, dstFmt)
        } else {
            bufferedImageToUint8Buffer(this, dstFmt)
        }
    }

    private fun bufferedImageToFloat32Buffer(image: BufferedImage, dstFormat: TexFormat): Float32Buffer {
        val buffer = Float32Buffer(image.width * image.height * dstFormat.channels)
        slowCopyImage(image, buffer, dstFormat)
        return buffer
    }

    private fun bufferedImageToUint8Buffer(image: BufferedImage, dstFormat: TexFormat): Uint8Buffer {
        val buffer = Uint8Buffer(image.width * image.height * dstFormat.channels)
        var copied = false

        if ((image.type == BufferedImage.TYPE_4BYTE_ABGR && dstFormat == TexFormat.RGBA)
            || (image.type == BufferedImage.TYPE_BYTE_GRAY && dstFormat == TexFormat.R)) {
            // Images loaded via ImageIO usually are of a byte type. We can load them in an optimized
            // way if the requested destination format matches
            copied = fastCopyImage(image, buffer, dstFormat)
        }
        if (!copied) {
            // fallback to slow copy
            slowCopyImage(image, buffer, dstFormat)
        }
        return buffer
    }

    private fun fastCopyImage(image: BufferedImage, target: Uint8Buffer, dstFormat: TexFormat): Boolean {
        val imgBuf = image.data.dataBuffer as? DataBufferByte ?: return false
        val bytes = imgBuf.bankData[0]
        val nBytes = image.width * image.height * dstFormat.channels

        if (dstFormat == TexFormat.RGBA && bytes.size == nBytes) {
            for (i in 0 until nBytes step 4) {
                // swap byte order (abgr -> rgba)
                val a = bytes[i]
                val b = bytes[i+1]
                bytes[i] = bytes[i+3]
                bytes[i+1] = bytes[i+2]
                bytes[i+2] = b
                bytes[i+3] = a
            }
            target.put(bytes)
            return true

        } else if (dstFormat == TexFormat.R && bytes.size == nBytes) {
            target.put(bytes)
            return true
        }
        return false
    }

    private fun slowCopyImage(image: BufferedImage, target: Buffer, dstFormat: TexFormat) {
        val pixel = IntArray(4)
        val model = image.colorModel
        val sizes = FloatArray(4) { i -> ((1 shl model.componentSize[i % model.componentSize.size]) - 1).toFloat() }
        val raster = image.data
        val hasSrcAlpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
        val indexed = image.type == BufferedImage.TYPE_BYTE_BINARY || image.type == BufferedImage.TYPE_BYTE_INDEXED

        val targetBufUint8: Uint8Buffer? = target as? Uint8Buffer
        val targetBufFloat: Float32Buffer? = target as? Float32Buffer

        check(targetBufUint8 != null || targetBufFloat != null) { "Supplied buffer is neither Uint8Buffer nor Float32Buffer" }

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                raster.getPixel(x, y, pixel)
                if (indexed) {
                    val p = pixel[0]
                    pixel[0] = model.getRed(p)
                    pixel[1] = model.getGreen(p)
                    pixel[2] = model.getBlue(p)
                    pixel[3] = model.getAlpha(p)
                }
                if (!hasSrcAlpha) {
                    pixel[3] = 255
                }

                val r = pixel[0] / sizes[0]
                val g = pixel[1] / sizes[1]
                val b = pixel[2] / sizes[2]
                val a = pixel[3] / sizes[3]

                targetBufUint8?.let { buf ->
                    buf.put((r * 255f).toInt().toByte())
                    if (dstFormat.channels > 1) buf.put((g * 255f).toInt().toByte())
                    if (dstFormat.channels > 2) buf.put((b * 255f).toInt().toByte())
                    if (dstFormat.channels > 3) buf.put((a * 255f).toInt().toByte())
                }
                targetBufFloat?.let { buf ->
                    buf.put(r)
                    if (dstFormat.channels > 1) buf.put(g)
                    if (dstFormat.channels > 2) buf.put(b)
                    if (dstFormat.channels > 3) buf.put(a)
                }
            }
        }
    }
}