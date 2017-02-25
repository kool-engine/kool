package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.platform.lwjgl3.*
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.GlslGenerator
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Desktop LWJGL3 platform implementation
 *
 * @author fabmax
 */
class PlatformImpl private constructor() : Platform() {

    companion object {
        private val monitors: MutableList<MonitorSpec> = mutableListOf()
        val primaryMonitor: MonitorSpec

        init {
            // setup an error callback
            GLFWErrorCallback.createPrint(System.err).set()

            // initialize GLFW
            if (!GLFW.glfwInit()) {
                throw IllegalStateException("Unable to initialize GLFW")
            }

            var primMon: MonitorSpec? = null
            val primMonId = GLFW.glfwGetPrimaryMonitor()
            val mons = GLFW.glfwGetMonitors()
            for (i in (0..mons.limit()-1)) {
                val spec = MonitorSpec(mons[i])
                monitors += spec
                if (mons[i] == primMonId) {
                    primMon = spec
                }
            }
            primaryMonitor = primMon!!
        }

        fun init() {
            Platform.initPlatform(PlatformImpl())
        }

        fun getMonitorSpecAt(x: Int, y: Int): MonitorSpec {
            var nearestMon: MonitorSpec? = null
            var dist = Double.POSITIVE_INFINITY
            for (i in monitors.indices) {
                val d = monitors[i].distance(x, y)
                if (d < dist) {
                    dist = d
                    nearestMon = monitors[i]
                }
            }
            return nearestMon!!
        }

        fun getResolutionAt(x: Int, y: Int): Float {
            return getMonitorSpecAt(x, y).dpi
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

    override fun loadTextureAsset(assetPath: String): BufferedTextureData {
        try {
            val image = ImageIO.read(File(assetPath))
            val alpha = image.transparency == Transparency.TRANSLUCENT || image.transparency == Transparency.BITMASK
            val format = if (alpha) GL.RGBA else GL.RGB
            val buffer = bufferedImageToBuffer(image, format, 0, 0)
            return BufferedTextureData(buffer, image.width, image.height, format)
        } catch (e: IOException) {
            throw KoolException("Failed to load texture asset: \"$assetPath\"")
        }
    }

    override fun createCharMap(font: Font, chars: String): CharMap {
        return fontGenerator.createCharMap(font, chars)
    }
}

class MonitorSpec(val monitor: Long) {
    val widthMm: Int
    val heightMm: Int
    val widthPx: Int
    val heightPx: Int
    val posX: Int
    val posY: Int
    val dpi: Float

    val vidmode: GLFWVidMode = GLFW.glfwGetVideoMode(monitor)

    init {
        val x = IntArray(1)
        val y = IntArray(1)

        GLFW.glfwGetMonitorPhysicalSize(monitor, x, y)
        widthMm = x[0]
        heightMm = y[0]
        GLFW.glfwGetMonitorPos(monitor, x, y)
        posX = x[0]
        posY = y[0]

        widthPx = vidmode.width()
        heightPx = vidmode.height()

        dpi = widthPx.toFloat() / (widthMm / 25.4f)
    }

    fun isOnMonitor(x: Int, y: Int): Boolean = (x >= posX && x < posX + widthPx && y >= posY && y < posY + heightPx)

    internal fun distance(x: Int, y: Int): Double {
        if (isOnMonitor(x, y)) {
            return -1.0
        } else {
            var dx = 0.0
            var dy = 0.0
            if (x < posX) {
                dx = (posX - x).toDouble()
            } else if (x > posX + widthPx) {
                dx = (x - posX - widthPx).toDouble()
            }
            if (y < posY) {
                dy = (posY - y).toDouble()
            } else if (y > posY + heightPx) {
                dy = (y - posY - heightPx).toDouble()
            }
            return Math.sqrt(dx * dx + dy * dy)
        }
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
