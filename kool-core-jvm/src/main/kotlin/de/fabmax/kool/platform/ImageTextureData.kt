package de.fabmax.kool.platform

import de.fabmax.kool.KoolException
import de.fabmax.kool.RenderContext
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.gl.*
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.createUint8Buffer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class ImageTextureData(val assetPath: String) : TextureData() {
    private var buffer: Uint8Buffer? = null
    private var format = 0

    init {
        launch(CommonPool) {
            try {
                // todo: use a less naive distinction between http and local textures
                val file = if (assetPath.startsWith("http")) {
                    HttpCache.loadHttpResource(assetPath)
                } else {
                    File(assetPath)
                }

                val image = ImageIO.read(file)
                val alpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
                format = if (alpha) GL_RGBA else GL_RGB
                width = image.width
                height = image.height
                buffer = bufferedImageToBuffer(image, format, 0, 0)
                isAvailable = true
            } catch (e: IOException) {
                throw KoolException("Failed to load texture asset: \"$assetPath\"")
            }
        }
    }

    override fun onLoad(texture: Texture, ctx: RenderContext) {
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
    val alpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
    val w = if (width == 0) { image.width } else { width }
    val h = if (height == 0) { image.height} else { height }
    val raster = image.data
    val pixel = IntArray(4)
    val indexed = image.type == BufferedImage.TYPE_BYTE_BINARY || image.type == BufferedImage.TYPE_BYTE_INDEXED
    val model = image.colorModel
    val stride = when (format) {
        GL_RGB -> 3
        GL_RGBA -> 4
        GL_ALPHA -> 1
        else -> throw KoolException("Invalid output format $format")
    }

    val buffer = createUint8Buffer(w * h * stride)
    for (y in 0..h-1) {
        for (x in 0..w-1) {
            raster.getPixel(x, y, pixel)

            if (indexed) {
                if (alpha) {
                    pixel[3] = model.getAlpha(pixel[0])
                }
                pixel[2] = model.getBlue(pixel[0])
                pixel[1] = model.getGreen(pixel[0])
                pixel[0] = model.getRed(pixel[0])
            }
            if (!alpha) {
                pixel[3] = 255
            }

            // pre-multiply alpha
            if (format == GL_RGBA && pixel[3] < 255) {
                pixel[0] = Math.round(pixel[0] * pixel[3] / 255f)
                pixel[1] = Math.round(pixel[1] * pixel[3] / 255f)
                pixel[2] = Math.round(pixel[2] * pixel[3] / 255f)
            }

            if (format == GL_RGBA || format == GL_RGB) {
                buffer.put(pixel[0].toByte()).put(pixel[1].toByte()).put(pixel[2].toByte())
            }
            if (format == GL_RGBA || format == GL_ALPHA) {
                buffer.put(pixel[3].toByte())
            }
        }
    }
    return buffer
}
