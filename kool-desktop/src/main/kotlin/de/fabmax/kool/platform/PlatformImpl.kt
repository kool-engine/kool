package de.fabmax.kool.platform

import de.fabmax.kool.BufferedTexture2d
import de.fabmax.kool.KoolException
import de.fabmax.kool.Texture2d
import de.fabmax.kool.TextureResource
import de.fabmax.kool.platform.lwjgl3.*
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.GlslGenerator
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Desktop LWJGL3 platform implementation
 *
 * @author fabmax
 */
class PlatformImpl private constructor() : Platform() {

    companion object {
        fun init() {
            Platform.initPlatform(PlatformImpl())
        }

        val MAX_GENERATED_TEX_WIDTH = 2048
        val MAX_GENERATED_TEX_HEIGHT = 2048
    }

    internal val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    override val supportsMultiContext = true

    override val supportsUint32Indices = true

    override fun createContext(props: RenderContext.InitProps): Lwjgl3Context {
        if (props is Lwjgl3Context.InitProps) {
            return Lwjgl3Context(props)
        } else {
            throw IllegalArgumentException("Props must be of Lwjgl3Context.InitProps")
        }
    }

    override fun createDefaultShaderGenerator(): ShaderGenerator {
        return GlslGenerator()
    }

    override fun getGlImpl(): GL.Impl {
        return Lwjgl3Impl.instance
    }

    override fun createUint8Buffer(capacity: Int): Uint8Buffer {
        return Uint8BufferImpl(capacity)
    }

    override fun createUint16Buffer(capacity: Int): Uint16Buffer {
        return Uint16BufferImpl(capacity)
    }

    override fun createUint32Buffer(capacity: Int): Uint32Buffer {
        return Uint32BufferImpl(capacity)
    }

    override fun createFloat32Buffer(capacity: Int): Float32Buffer {
        return Float32BufferImpl(capacity)
    }

    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun loadTexture(path: String, props: TextureResource.Props): BufferedTexture2d {
        val image = ImageIO.read(File(path))
        val alpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
        val format = if (alpha) GL.RGBA else GL.RGB
        val buffer = bufferedImageToBuffer(image, format, 0, 0)
        return BufferedTexture2d(buffer, image.width, image.height, format, props)
    }

    override fun createCharMap(font: Font, chars: String): CharMap {
        return fontGenerator.createCharMap(font, chars)
    }
}

internal fun bufferedImageToBuffer(image: BufferedImage, format: Int, width: Int, height: Int): Uint8Buffer {
    val alpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
    val w = if (width == 0) { image.width } else { width }
    val h = if (height == 0) { image.height} else { height }
    val raster = image.data
    val pixel = IntArray(4)
    val indexed = image.type == BufferedImage.TYPE_BYTE_BINARY || image.type == BufferedImage.TYPE_BYTE_INDEXED
    val model = image.colorModel
    val stride = when (format) {
        GL.RGB -> 3
        GL.RGBA -> 4
        GL.ALPHA -> 1
        else -> throw KoolException("Invalid output format $format")
    }

    val buffer = Platform.createUint8Buffer(w * h * stride)
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
            if (format == GL.RGBA && pixel[3] < 255) {
                pixel[0] = Math.round(pixel[0] * pixel[3] / 255f)
                pixel[1] = Math.round(pixel[1] * pixel[3] / 255f)
                pixel[2] = Math.round(pixel[2] * pixel[3] / 255f)
            }

            if (format == GL.RGBA || format == GL.RGB) {
                buffer.put(pixel[0].toByte()).put(pixel[1].toByte()).put(pixel[2].toByte())
            }
            if (format == GL.RGBA || format == GL.ALPHA) {
                buffer.put(pixel[3].toByte())
            }
        }
    }
    return buffer
}

/**
 * AutoCloseable variant of the standard use extension function (which only works for Closeable).
 * This is mainly needed for MemoryStack.stackPush() to work in a try-with-resources manner.
 */
inline fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            this.close()
        } catch (closeException: Exception) {
            // eat the closeException as we are already throwing the original cause
            // and we don't want to mask the real exception

            // TODO on Java 7 we should call
            // e.addSuppressed(closeException)
            // to work like try-with-resources
            // http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html#suppressed-exceptions
        }
        throw e
    } finally {
        if (!closed) {
            this.close()
        }
    }
}
