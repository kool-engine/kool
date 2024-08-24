package de.fabmax.kool.util

import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Heightmap(val heightData: FloatArray, val rows: Int, val columns: Int) {

    val minHeight: Float
    val maxHeight: Float

    init {
        check(heightData.size == columns * rows) {
            "Supplied height data has not the correct size: ${heightData.size} elements != $columns x $rows"
        }

        var min = Float.POSITIVE_INFINITY
        var max = Float.NEGATIVE_INFINITY
        for (i in heightData.indices) {
            min = min(min, heightData[i])
            max = max(max, heightData[i])
        }
        minHeight = min
        maxHeight = max
    }

    fun getHeightLinear(x: Float, y: Float): Float {
        val x0 = floor(x).toInt()
        val x1 = x0 + 1
        val xf = x - x0
        val y0 = floor(y).toInt()
        val y1 = y0 + 1
        val yf = y - y0

        val h00 = getHeight(x0, y0)
        val h01 = getHeight(x0, y1)
        val h10 = getHeight(x1, y0)
        val h11 = getHeight(x1, y1)

        val ha = h00 * (1f - yf) + h01 * yf
        val hb = h10 * (1f - yf) + h11 * yf
        return ha * (1f - xf) + hb * xf
    }

    fun getHeight(x: Int, y: Int): Float {
        val cx = x.clamp(0, columns - 1)
        val cy = y.clamp(0, rows - 1)
        return heightData[cy * columns + cx]
    }

    companion object {
        /**
         * Constructs a [Heightmap] from the provided [BufferedImageData2d] object. Format must be either [TexFormat.R_F16]
         * or [TexFormat.R]
         */
        fun fromTextureData2d(bufferedImageData2D: BufferedImageData2d, heightScale: Float, heightOffset: Float = 0f): Heightmap {
            check(bufferedImageData2D.format == TexFormat.R_F16 || bufferedImageData2D.format == TexFormat.R) {
                "Texture format must be either TexFormat.R_F16 or TexFormat.R"
            }

            val heightData = FloatArray(bufferedImageData2D.width * bufferedImageData2D.height)
            if (bufferedImageData2D.data is Float32Buffer) {
                val float32Buf = bufferedImageData2D.data
                for (i in 0 until bufferedImageData2D.width * bufferedImageData2D.height) {
                    heightData[i] = float32Buf[i] * heightScale + heightOffset
                }
            } else {
                logW { "Constructing height map from 8-bit texture data, consider using raw data for higher height resolution" }
                val uint8Buf = bufferedImageData2D.data as Uint8Buffer
                for (i in 0 until bufferedImageData2D.width * bufferedImageData2D.height) {
                    heightData[i] = ((uint8Buf[i].toInt() and 0xff) / 255f) * heightScale + heightOffset
                }
            }
            return Heightmap(heightData, bufferedImageData2D.height, bufferedImageData2D.width)
        }

        /**
         * Constructs a [Heightmap] from the provided binary buffer. Buffer content is expected to be a plain array of
         * 16-bit integers with dimension [columns] x [rows]. If [columns] and / or [rows] are not specified, a square
         * map is assumed.
         */
        fun fromRawData(rawData: Uint8Buffer, heightScale: Float, rows: Int = 0, columns: Int = 0, heightOffset: Float = 0f): Heightmap {
            val elems = rawData.capacity / 2

            // if width and / or height are not supplied, a square map is assumed
            val w = if (columns > 0) columns else sqrt(elems.toDouble()).toInt()
            val h = if (rows > 0) rows else w
            check (w * h == elems) {
                "Raw data size ($elems) does not match anticipated map size: $w x $h"
            }

            val heightData = FloatArray(elems)
            for (i in 0 until elems) {
                val ia = rawData[i*2].toInt() and 0xff
                val ib = rawData[i*2+1].toInt() and 0xff
                heightData[i] = ((ia or (ib shl 8)) / 65535f) * heightScale + heightOffset
            }
            return Heightmap(heightData, h, w)
        }
    }
}