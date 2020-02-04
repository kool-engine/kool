package de.fabmax.kool.platform

import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.BufferedTextureData
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.createUint8Buffer
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

class ImageTextureData(image: BufferedImage) :
        BufferedTextureData(image.toBuffer(), image.width, image.height, image.format) {

//    fun onLoad(texture: Texture, target: Int, ctx: KoolContext) {
//        val res = texture.res ?: throw KoolException("Texture wasn't created")
//        val limit = buffer!!.limit
//        val pos = buffer!!.position
//        buffer!!.flip()
//        glTexImage2D(target, 0, format.glFormat, width, height, 0, format.glFormat, GL_UNSIGNED_BYTE, buffer)
//        buffer!!.limit = limit
//        buffer!!.position = pos
//        ctx.memoryMgr.memoryAllocated(res, buffer!!.position)
//    }

    companion object {
        private val BufferedImage.format: TexFormat get() {
            val alpha = transparency == Transparency.TRANSLUCENT || transparency == Transparency.BITMASK
            return if (alpha) TexFormat.RGBA else TexFormat.RGB
        }

        private fun BufferedImage.toBuffer(): Uint8Buffer {
            return bufferedImageToBuffer(this, format, 0, 0)
        }

        fun bufferedImageToBuffer(image: BufferedImage, format: TexFormat, width: Int, height: Int): Uint8Buffer {
            val w = if (width == 0) { image.width } else { width }
            val h = if (height == 0) { image.height} else { height }
            val stride = when (format) {
                TexFormat.R -> 1
                TexFormat.RG -> 2
                TexFormat.RGB -> 3
                TexFormat.RGBA -> 4
                else -> throw KoolException("Invalid output format $format")
            }

            val buffer = createUint8Buffer(w * h * stride)

            var copied = false
            if (w == image.width && h == image.height) {
                // Images loaded via ImageIO usually are of type 4BYTE_ABGR or 3BYTE_BGR, we can load them in a optimized way...
                when {
                    image.type == BufferedImage.TYPE_4BYTE_ABGR && format == TexFormat.RGBA -> {
                        copied = fastCopyImage(image, buffer, format)
                    }
                    image.type == BufferedImage.TYPE_3BYTE_BGR && format == TexFormat.RGB -> {
                        copied = fastCopyImage(image, buffer, format)
                    }
                }
            }

            if (!copied) {
                // fallback to slow copy
                slowCopyImage(image, buffer, format, w, h)
            }

            buffer.flip()
            return buffer
        }

        private fun fastCopyImage(image: BufferedImage, target: Uint8Buffer, format: TexFormat): Boolean {
            val imgBuf = image.data.dataBuffer as? DataBufferByte ?: return false
            val bytes = imgBuf.bankData[0]
            val nPixels = image.width * image.height * if (format == TexFormat.RGB) 3 else 4

            // swap byte order and apply pre-multiplication if there is alpha
            if (format == TexFormat.RGBA && bytes.size == nPixels) {
                for (i in 0 until nPixels step 4) {
                    val a = bytes[i]
                    val b = bytes[i+1]
                    bytes[i] = bytes[i+3]
                    bytes[i+1] = bytes[i+2]
                    bytes[i+2] = b
                    bytes[i+3] = a
                }
                target.put(bytes)
                return true

            } else if (format == TexFormat.RGB && bytes.size == nPixels) {
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

        private fun slowCopyImage(image: BufferedImage, target: Uint8Buffer, format: TexFormat, width: Int, height: Int) {
            val pixel = IntArray(4)
            val model = image.colorModel
            val sizes = IntArray(4) { i -> (1 shl model.componentSize[i % model.componentSize.size]) - 1 }
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

                    var r = pixel[0] / sizes[0].toFloat()
                    var g = pixel[1] / sizes[1].toFloat()
                    var b = pixel[2] / sizes[2].toFloat()
                    val a = pixel[3] / sizes[3].toFloat()

                    // copy bytes to target buf
                    if (format == TexFormat.RGBA || format == TexFormat.RGB) {
                        target.put((r * 255f).toByte()).put((g * 255f).toByte()).put((b * 255f).toByte())
                    }
                    if (format == TexFormat.RGBA || format == TexFormat.R) {
                        target.put((a * 255f).toByte())
                    }
                }
            }
        }
    }
}

