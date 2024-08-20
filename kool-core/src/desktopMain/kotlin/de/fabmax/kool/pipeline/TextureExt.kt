package de.fabmax.kool.pipeline

import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import java.awt.image.BufferedImage

suspend fun Texture2d.toBufferedImage(flipY: Boolean = true): BufferedImage {
    val data = download()

    val img = BufferedImage(data.width, data.height, BufferedImage.TYPE_INT_ARGB)
    when (val buf = data.data) {
        is Uint8BufferImpl -> {
            for (i in 0 until data.width * data.height) {
                // swap byte order (rgba -> argb)
                val bi = i * 4
                val r = buf[bi].toInt()
                val g = buf[bi + 1].toInt()
                val b = buf[bi + 2].toInt()
                val a = buf[bi + 3].toInt()
                val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
                val y = if (flipY) data.height - 1 - i / data.width else i / data.width
                img.setRGB(i % data.width, y, argb)
            }
        }
        is Float32BufferImpl -> {
            for (i in 0 until data.width * data.height) {
                // swap byte order (rgba -> argb)
                val bi = i * 4
                val r = (buf[bi] * 255f).toInt()
                val g = (buf[bi + 1] * 255f).toInt()
                val b = (buf[bi + 2] * 255f).toInt()
                val a = (buf[bi + 3] * 255f).toInt()
                val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
                val y = if (flipY) data.height - 1 - i / data.width else i / data.width
                img.setRGB(i % data.width, y, argb)
            }
        }
        else -> throw IllegalStateException("Unsupported texture data format: $buf")
    }
    return img
}