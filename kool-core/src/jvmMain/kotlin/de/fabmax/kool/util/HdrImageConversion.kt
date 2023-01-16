package de.fabmax.kool.util

import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.TextureData2d
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_loadf_from_memory
import org.lwjgl.system.MemoryStack
import java.awt.image.BufferedImage
import java.io.File
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow

object HdrImageConversion {

    fun loadHdrImage(hdrImage: File): TextureData2d {
        val imageData = Uint8BufferImpl(hdrImage.readBytes())
        imageData.flip()
        return loadHdrImage(imageData)
    }

    fun loadHdrImage(imageData: Uint8Buffer): TextureData2d {
        return MemoryStack.stackPush().use { stack ->
            val imageBuffer = (imageData as Uint8BufferImpl).buffer

            val w: IntBuffer = stack.mallocInt(1)
            val h: IntBuffer = stack.mallocInt(1)
            val components: IntBuffer = stack.mallocInt(1)
            val image: FloatBuffer = stbi_loadf_from_memory(imageBuffer, w, h, components, 0) ?:
                throw RuntimeException("Failed to load image: " + stbi_failure_reason())

            val texFormat = when (components[0]) {
                1 -> TexFormat.R_F16
                2 -> TexFormat.RG_F16
                3 -> TexFormat.RGB_F16
                4 -> TexFormat.RGBA_F16
                else -> throw IllegalStateException("Invalid number of image components: ${components[0]}")
            }

            TextureData2d(Float32BufferImpl(image), w[0], h[0], texFormat)
        }
    }

    fun convertHdrImageToRgbe(hdrImage: File): BufferedImage {
        return convertHdrImageToRgbe(loadHdrImage(hdrImage))
    }

    fun convertHdrImageToRgbe(hdrImage: TextureData2d): BufferedImage {
        val hdrData = (hdrImage.data as? Float32BufferImpl)
            ?: throw IllegalArgumentException("Supplied HDR image data needs to be in float format")

        val c = hdrImage.format.channels
        if (c !in 1..3) {
            throw IllegalArgumentException("Supplied HDR image has an invalid number of components: ${hdrImage.format.channels}, must be in [1..3]")
        }

        val img = BufferedImage(hdrImage.width, hdrImage.height, BufferedImage.TYPE_INT_ARGB)
        var i = 0
        var maxVal = 0f
        for (y in 0 until hdrImage.height) {
            for (x in 0 until hdrImage.width) {
                val rf = hdrData[i++]
                val gf = if (c > 1) hdrData[i++] else 0f
                val bf = if (c > 2) hdrData[i++] else 0f

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