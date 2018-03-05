package de.fabmax.kool

import de.fabmax.kool.platform.FontMapGenerator
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.MonitorSpec
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.net.URI

/**
 * Desktop LWJGL3 platform implementation
 *
 * @author fabmax
 */

actual var glCapabilities = GlCapabilities.UNKNOWN_CAPABILITIES

fun createContext() = createContext(Lwjgl3Context.InitProps())

actual fun createContext(props: RenderContext.InitProps): RenderContext = DesktopImpl.createContext(props)

actual fun createCharMap(fontProps: FontProps): CharMap = DesktopImpl.fontGenerator.createCharMap(fontProps)

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) {
    // try to load asset from resources
    //for (url in ClassLoader.getSystemResources())
    var inStream = ClassLoader.getSystemResourceAsStream(assetPath)
    if (inStream == null) {
        // if asset wasn't found in resources try to load it from file system
        inStream = FileInputStream(assetPath)
    }

    inStream.use {
        val data = ByteArrayOutputStream()
        val buf = ByteArray(1024 * 1024)
        while (it.available() > 0) {
            val len = it.read(buf)
            data.write(buf, 0, len)
        }
        onLoad(data.toByteArray())
    }
}

actual fun loadTextureAsset(assetPath: String): TextureData = ImageTextureData(assetPath)

actual fun openUrl(url: String) = Desktop.getDesktop().browse(URI(url))

actual fun getMemoryInfo(): String {
    val rt = Runtime.getRuntime()
    val freeMem = rt.freeMemory()
    val totalMem = rt.totalMemory()
    return "Heap: ${(totalMem - freeMem) / 1024 / 1024} / ${totalMem / 1024 / 1024} MB"
}

internal object DesktopImpl {
    private const val MAX_GENERATED_TEX_WIDTH = 2048
    private const val MAX_GENERATED_TEX_HEIGHT = 2048

    val monitors: MutableList<MonitorSpec> = mutableListOf()
    val primaryMonitor: MonitorSpec
    val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    init {
        // setup an error callback
        GLFWErrorCallback.createPrint(System.err).set()

        // initialize GLFW
        if (!GLFW.glfwInit()) {
            throw KoolException("Unable to initialize GLFW")
        }

        var primMon: MonitorSpec? = null
        val primMonId = GLFW.glfwGetPrimaryMonitor()
        val mons = GLFW.glfwGetMonitors()
        for (i in 0 until mons.limit()) {
            val spec = MonitorSpec(mons[i])
            monitors += spec
            if (mons[i] == primMonId) {
                primMon = spec
            }
        }
        primaryMonitor = primMon!!
    }

    fun createContext(props: RenderContext.InitProps): RenderContext {
        if (props is Lwjgl3Context.InitProps) {
            return Lwjgl3Context(props)
        } else {
            throw IllegalArgumentException("Props must be of Lwjgl3Context.InitProps")
        }
    }
}

/**
 * AutoCloseable variant of the standard use extension function (which only works for Closeable).
 * This is mainly needed for lwjgl's MemoryStack.stackPush() to work in a try-with-resources manner.
 */
inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        when {
            this == null -> {}
            exception == null -> close()
            else ->
                try {
                    close()
                } catch (closeException: Throwable) {
                    exception.addSuppressed(closeException)
                }
        }
    }
}
