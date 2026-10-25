package de.fabmax.kool.rendertest

import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Uint8Buffer
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.max

/**
 * Tolerance used when comparing rendered images against reference images. Different GPUs / drivers produce
 * slightly different results (rasterization rules, MSAA sample positions, floating point precision), so an exact
 * comparison is not feasible.
 *
 * A pixel is considered different if any of its color channels differs by more than [maxChannelDiff]. The comparison
 * fails if the ratio of different pixels exceeds [maxDiffPixelRatio].
 */
data class ImageTolerance(
    val maxChannelDiff: Int = 16,
    val maxDiffPixelRatio: Double = 0.005,
) {
    companion object {
        val Exact = ImageTolerance(0, 0.0)
        val Default = ImageTolerance()
    }
}

class ImageComparisonResult(
    val diffPixels: Int,
    val totalPixels: Int,
    val maxChannelDiff: Int,
    val diffImage: BufferedImage,
) {
    val diffPixelRatio: Double get() = diffPixels.toDouble() / totalPixels

    fun isWithin(tolerance: ImageTolerance) = diffPixelRatio <= tolerance.maxDiffPixelRatio

    override fun toString(): String {
        return "$diffPixels of $totalPixels pixels differ (%.3f %%), max channel difference: $maxChannelDiff"
            .format(diffPixelRatio * 100.0)
    }
}

object ImageComparison {
    /**
     * Compares the two images pixel by pixel. Images must have the same size. The returned diff image highlights
     * different pixels in red (bright red = large difference), equal pixels are shown dimmed in gray scale.
     */
    fun compare(actual: BufferedImage, expected: BufferedImage, tolerance: ImageTolerance): ImageComparisonResult {
        require(actual.width == expected.width && actual.height == expected.height) {
            "Image sizes differ: actual: ${actual.width} x ${actual.height}, expected: ${expected.width} x ${expected.height}"
        }
        val width = actual.width
        val height = actual.height
        val actualPixels = actual.getRGB(0, 0, width, height, null, 0, width)
        val expectedPixels = expected.getRGB(0, 0, width, height, null, 0, width)
        val diffPixels = IntArray(width * height)

        var numDiff = 0
        var maxDiff = 0
        for (i in actualPixels.indices) {
            val a = actualPixels[i]
            val e = expectedPixels[i]
            val diff = max(
                max(abs(a.channel(16) - e.channel(16)), abs(a.channel(8) - e.channel(8))),
                max(abs(a.channel(0) - e.channel(0)), abs(a.channel(24) - e.channel(24)))
            )
            maxDiff = max(maxDiff, diff)
            diffPixels[i] = if (diff > tolerance.maxChannelDiff) {
                numDiff++
                val r = 128 + diff / 2
                (0xff shl 24) or (r shl 16)
            } else {
                val gray = (a.channel(16) * 77 + a.channel(8) * 151 + a.channel(0) * 28) shr 10
                (0xff shl 24) or (gray shl 16) or (gray shl 8) or gray
            }
        }

        val diffImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        diffImage.setRGB(0, 0, width, height, diffPixels, 0, width)
        return ImageComparisonResult(numDiff, width * height, maxDiff, diffImage)
    }

    private fun Int.channel(shift: Int): Int = (this ushr shift) and 0xff
}

fun BufferedImageData2d.toBufferedImage(): BufferedImage {
    check(format == TexFormat.RGBA) { "Unsupported image format: $format (only RGBA is supported)" }
    val src = data as Uint8Buffer
    val pixels = IntArray(width * height) { i ->
        val r = src[i * 4].toInt()
        val g = src[i * 4 + 1].toInt()
        val b = src[i * 4 + 2].toInt()
        val a = src[i * 4 + 3].toInt()
        (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
        setRGB(0, 0, width, height, pixels, 0, width)
    }
}

fun BufferedImage.writePng(file: File) {
    file.parentFile?.mkdirs()
    check(ImageIO.write(this, "png", file)) { "Failed writing image $file" }
}

fun readPng(file: File): BufferedImage = checkNotNull(ImageIO.read(file)) { "Failed reading image $file" }
