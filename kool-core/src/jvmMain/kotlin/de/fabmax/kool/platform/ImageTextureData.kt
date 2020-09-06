package de.fabmax.kool.platform

import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.BufferedTextureData
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.createUint8Buffer
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

class ImageTextureData(image: BufferedImage, val dstFormat: TexFormat?) :
        BufferedTextureData(image.toBuffer(dstFormat), image.width, image.height, chooseDstFormat(image.format, dstFormat)) {

    companion object {
        private val BufferedImage.format: TexFormat get() {
            val alpha = transparency == Transparency.TRANSLUCENT || transparency == Transparency.BITMASK
            return if (alpha) TexFormat.RGBA else TexFormat.RGB
        }

        private fun chooseDstFormat(srcFormat: TexFormat, preferredFormat: TexFormat?): TexFormat {
            return when {
                preferredFormat == null -> srcFormat
                preferredFormat.hasAlpha && !srcFormat.hasAlpha -> srcFormat

                // fixme: loading f16 formats is not yet implemented
                preferredFormat == TexFormat.R_F16 -> TexFormat.R
                preferredFormat == TexFormat.RG_F16 -> TexFormat.RG
                preferredFormat == TexFormat.RGB_F16 -> TexFormat.RGB
                preferredFormat == TexFormat.RGBA_F16 -> TexFormat.RGBA

                else -> preferredFormat
            }
        }


        private fun BufferedImage.toBuffer(dstFormat: TexFormat?): Uint8Buffer {
            return bufferedImageToBuffer(this, dstFormat, 0, 0)
        }

        fun bufferedImageToBuffer(image: BufferedImage, dstFmt: TexFormat?, width: Int, height: Int): Uint8Buffer {
            val srcFormat = image.format
            val dstFormat = chooseDstFormat(srcFormat, dstFmt)

            val w = if (width == 0) { image.width } else { width }
            val h = if (height == 0) { image.height} else { height }
            val stride = when (srcFormat) {
                TexFormat.R -> 1
                TexFormat.RG -> 2
                TexFormat.RGB -> 3
                TexFormat.RGBA -> 4
                else -> throw KoolException("Invalid output format $srcFormat")
            }

            val buffer = createUint8Buffer(w * h * stride)

            var copied = false
            if (w == image.width && h == image.height) {
                // Images loaded via ImageIO usually are of type 4BYTE_ABGR or 3BYTE_BGR, we can load them in a optimized way...
                when {
                    image.type == BufferedImage.TYPE_4BYTE_ABGR && dstFormat == TexFormat.RGBA -> {
                        copied = fastCopyImage(image, buffer, dstFormat)
                    }
                    image.type == BufferedImage.TYPE_3BYTE_BGR && dstFormat == TexFormat.RGB -> {
                        copied = fastCopyImage(image, buffer, dstFormat)
                    }
                    image.type == BufferedImage.TYPE_BYTE_GRAY && dstFormat == TexFormat.R -> {
                        copied = fastCopyImage(image, buffer, dstFormat)
                    }
                }
            }

            if (!copied) {
                // fallback to slow copy
                slowCopyImage(image, buffer, dstFormat, w, h)
            }

            buffer.flip()
            return buffer
        }

        private fun fastCopyImage(image: BufferedImage, target: Uint8Buffer, dstFormat: TexFormat): Boolean {
            val imgBuf = image.data.dataBuffer as? DataBufferByte ?: return false
            val bytes = imgBuf.bankData[0]
            val nPixels = image.width * image.height * dstFormat.channels

            if (dstFormat == TexFormat.RGBA && bytes.size == nPixels) {
                for (i in 0 until nPixels step 4) {
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

            } else if (dstFormat == TexFormat.RGB && bytes.size == nPixels) {
                for (i in 0 until nPixels step 3) {
                    // swap byte order (bgr -> rgb)
                    val b = bytes[i]
                    bytes[i] = bytes[i+2]
                    bytes[i+2] = b
                }
                target.put(bytes)
                return true

            } else if (dstFormat == TexFormat.R && bytes.size == nPixels) {
                target.put(bytes)
                return true
            }
            return false
        }

        private fun slowCopyImage(image: BufferedImage, target: Uint8Buffer, dstFormat: TexFormat, width: Int, height: Int) {
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

                    val r = pixel[0] / sizes[0].toFloat()
                    val g = pixel[1] / sizes[1].toFloat()
                    val b = pixel[2] / sizes[2].toFloat()
                    val a = pixel[3] / sizes[3].toFloat()

                    // copy bytes to target buf
                    when (dstFormat) {
                        TexFormat.R -> {
                            target.put((r * 255f).toInt().toByte())
                        }
                        TexFormat.RG -> {
                            target.put((r * 255f).toInt().toByte())
                            target.put((g * 255f).toInt().toByte())
                        }
                        TexFormat.RGB -> {
                            target.put((r * 255f).toInt().toByte())
                            target.put((g * 255f).toInt().toByte())
                            target.put((b * 255f).toInt().toByte())
                        }
                        TexFormat.RGBA -> {
                            target.put((r * 255f).toInt().toByte())
                            target.put((g * 255f).toInt().toByte())
                            target.put((b * 255f).toInt().toByte())
                            target.put((a * 255f).toInt().toByte())
                        }
                        else -> throw IllegalArgumentException("TexFormat not yet implemented: $dstFormat")
                    }
                }
            }
        }
    }
}
