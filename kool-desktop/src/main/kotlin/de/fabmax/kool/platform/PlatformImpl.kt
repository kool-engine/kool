package de.fabmax.kool.platform

import de.fabmax.kool.BufferedTexture2d
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
    }

    internal val fontGenerator = FontMapGenerator()

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
        val buffer = bufferedImageToBuffer(image)
        return BufferedTexture2d(buffer, image.width, image.height, if (alpha) GL.RGBA else GL.RGB, props)
    }

    override fun createCharMap(font: Font, chars: String): CharMap {
        return fontGenerator.createCharMap(font, chars)
    }
}

internal fun bufferedImageToBuffer(image: BufferedImage): Uint8Buffer {
    val alpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
    val stride = if (alpha) 4 else 3
    val width = image.width
    val height = image.height
    val raster = image.data
    val pixel = IntArray(4)
    val indexed = image.type == BufferedImage.TYPE_BYTE_BINARY || image.type == BufferedImage.TYPE_BYTE_INDEXED
    val model = image.colorModel

    val buffer = Platform.createUint8Buffer(width * height * stride)
    for (y in 0..height-1) {
        for (x in 0..width-1) {
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
            if (pixel[3] < 255) {
                pixel[0] = Math.round(pixel[0] * pixel[3] / 255f)
                pixel[1] = Math.round(pixel[1] * pixel[3] / 255f)
                pixel[2] = Math.round(pixel[2] * pixel[3] / 255f)
            }

            buffer.put(pixel[0].toByte()).put(pixel[1].toByte()).put(pixel[2].toByte())
            if (alpha) {
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
