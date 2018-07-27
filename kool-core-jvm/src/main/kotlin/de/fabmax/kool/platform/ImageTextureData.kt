package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.gl.*
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.createUint8Buffer
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import kotlin.math.round

class ImageTextureData : TextureData() {
    private var buffer: Uint8Buffer? = null
    private var format = 0

    internal fun setTexImage(image: BufferedImage) {
        val alpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
        format = if (alpha) GL_RGBA else GL_RGB
        width = image.width
        height = image.height
        buffer = bufferedImageToBuffer(image, format, 0, 0)
        isAvailable = true
    }

    override fun onLoad(texture: Texture, ctx: KoolContext) {
        val res = texture.res ?: throw KoolException("Texture wasn't created")
        val limit = buffer!!.limit
        val pos = buffer!!.position
        buffer!!.flip()

//        val intFormat = if (format == GL.RGBA) {
//            GL13.GL_COMPRESSED_RGBA
//        } else {
//            GL13.GL_COMPRESSED_RGB
//        }
        val intFormat = format

        glTexImage2D(res.target, 0, intFormat, width, height, 0, format, GL_UNSIGNED_BYTE, buffer)
        buffer!!.limit = limit
        buffer!!.position = pos
        ctx.memoryMgr.memoryAllocated(res, buffer!!.position)
    }
}

fun bufferedImageToBuffer(image: BufferedImage, format: Int, width: Int, height: Int): Uint8Buffer {
    val w = if (width == 0) { image.width } else { width }
    val h = if (height == 0) { image.height} else { height }
    val stride = when (format) {
        GL_RGB -> 3
        GL_RGBA -> 4
        GL_ALPHA -> 1
        else -> throw KoolException("Invalid output format $format")
    }

    val buffer = createUint8Buffer(w * h * stride)

    var copied = false
    if (w == image.width && h == image.height) {
        // Images loaded via ImageIO usually are of type 4BYTE_ABGR or 3BYTE_BGR, we can load them in a optimized way...
        when {
            image.type == BufferedImage.TYPE_4BYTE_ABGR && format == GL_RGBA -> {
                copied = fastCopyImage(image, buffer, format)
            }
            image.type == BufferedImage.TYPE_3BYTE_BGR && format == GL_RGB -> {
                copied = fastCopyImage(image, buffer, format)
            }
        }
    }

    if (!copied) {
        // fallback to slow copy
        slowCopyImage(image, buffer, format, w, h)
    }

    return buffer
}

fun fastCopyImage(image: BufferedImage, target: Uint8Buffer, format: Int): Boolean {
    val imgBuf = image.data.dataBuffer as? DataBufferByte ?: return false
    val bytes = imgBuf.bankData[0]
    val nPixels = image.width * image.height * if (format == GL_RGB) 3 else 4

    // swap byte order and apply pre-multiplication if there is alpha
    if (format == GL_RGBA && bytes.size == nPixels) {
        for (i in 0 until nPixels step 4) {
            val alpha = bytes[i].toInt() and 0xff
            when (alpha) {
                0 -> {
                    bytes[i] = 0
                    bytes[i+1] = 0
                    bytes[i+2] = 0
                    bytes[i+3] = 0
                }
                255 -> {
                    bytes[i] = bytes[i+3]
                    val b = bytes[i+1]
                    bytes[i+1] = bytes[i+2]
                    bytes[i+2] = b
                    bytes[i+3] = 255.toByte()
                }
                else -> {
                    val f = alpha / 255f
                    bytes[i] = round((bytes[i+3].toInt() and 0xff) * f).toByte()
                    val b = bytes[i+1]
                    bytes[i+1] = round((bytes[i+2].toInt() and 0xff) * f).toByte()
                    bytes[i+2] = round((b.toInt() and 0xff) * f).toByte()
                    bytes[i+3] = alpha.toByte()
                }
            }
        }
        target.put(bytes)
        return true

    } else if (format == GL_RGB && bytes.size == nPixels) {
        for (i in 0 until nPixels step 3) {
            val b = bytes[i]
            bytes[i] = bytes[i+2]
            bytes[i+2] = b
        }
        target.put(bytes)
        return true
    }
    return false
}

fun slowCopyImage(image: BufferedImage, target: Uint8Buffer, format: Int, width: Int, height: Int) {
    val pixel = IntArray(4)
    val model = image.colorModel
    val raster = image.data
    val alpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
    val indexed = image.type == BufferedImage.TYPE_BYTE_BINARY || image.type == BufferedImage.TYPE_BYTE_INDEXED

    for (y in 0 until height) {
        for (x in 0 until width) {
            raster.getPixel(x, y, pixel)

            if (indexed) {
                val p = pixel[0]
                pixel[0] = model.getRed(p)
                pixel[1] = model.getGreen(p)
                pixel[2] = model.getBlue(p)
                pixel[3] = model.getAlpha(p)
            }
            if (!alpha) {
                pixel[3] = 255
            }

            // pre-multiply alpha
            if (format == GL_RGBA && pixel[3] < 255) {
                if (pixel[3] == 0) {
                    pixel[0] = 0
                    pixel[1] = 0
                    pixel[2] = 0
                } else {
                    val f = pixel[3] / 255f
                    pixel[0] = Math.round(pixel[0] * f)
                    pixel[1] = Math.round(pixel[1] * f)
                    pixel[2] = Math.round(pixel[2] * f)
                }
            }

            // copy bytes to target buf
            if (format == GL_RGBA || format == GL_RGB) {
                target.put(pixel[0].toByte()).put(pixel[1].toByte()).put(pixel[2].toByte())
            }
            if (format == GL_RGBA || format == GL_ALPHA) {
                target.put(pixel[3].toByte())
            }
        }
    }
}
