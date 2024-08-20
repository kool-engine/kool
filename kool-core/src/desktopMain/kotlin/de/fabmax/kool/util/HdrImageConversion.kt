package de.fabmax.kool.util

import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_loadf_from_memory
import java.awt.image.BufferedImage
import java.io.File
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow

object HdrImageConversion {

    fun loadHdrImage(hdrImage: File): BufferedImageData2d {
        val imageData = Uint8BufferImpl(hdrImage.readBytes())
        return loadHdrImage(imageData)
    }

    fun loadHdrImage(imageData: Uint8Buffer): BufferedImageData2d {
        return memStack {
            val w: IntBuffer = mallocInt(1)
            val h: IntBuffer = mallocInt(1)
            val ch: IntBuffer = mallocInt(1)
            val desiredCh = 4

            val image: FloatBuffer? = imageData.useRaw { raw -> stbi_loadf_from_memory(raw, w, h, ch, desiredCh) }
            checkNotNull(image) { "Failed to load image: ${stbi_failure_reason()}" }
            BufferedImageData2d(Float32BufferImpl(image), w[0], h[0], TexFormat.RGBA_F16)
        }
    }

    fun convertHdrImageToRgbe(hdrImage: File): BufferedImage {
        return convertHdrImageToRgbe(loadHdrImage(hdrImage))
    }

    fun convertHdrImageToRgbe(hdrImage: BufferedImageData2d): BufferedImage {
        val hdrData = (hdrImage.data as? Float32BufferImpl) ?: error("Supplied HDR image data needs to be in float format")

        val c = hdrImage.format.channels
        val img = BufferedImage(hdrImage.width, hdrImage.height, BufferedImage.TYPE_INT_ARGB)
        var i = 0
        var maxVal = 0f
        for (y in 0 until hdrImage.height) {
            for (x in 0 until hdrImage.width) {
                val rf = hdrData[i++]
                val gf = if (c > 1) hdrData[i++] else 0f
                val bf = if (c > 2) hdrData[i++] else 0f
                // ignore alpha channel
                if (c == 4) hdrData[i++]

                val v = max(rf, max(gf, bf))
                val e = ceil(log2(v)).toInt()
                val s = 2f.pow(e-8)
                if (v > maxVal) { maxVal = v }

                val r = (rf / s).toInt().clamp(0, 255)
                val g = (gf / s).toInt().clamp(0, 255)
                val b = (bf / s).toInt().clamp(0, 255)
                val a = e + 128

                val rgb = (a shl 24) or (r shl 16) or (g shl 8) or b
                img.setRGB(x, y, rgb)
            }
        }
        return img
    }

}