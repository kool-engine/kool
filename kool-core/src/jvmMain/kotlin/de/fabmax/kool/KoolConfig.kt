package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.platform.Lwjgl3Context
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

actual data class KoolConfig(
    actual val assetPath: String = "./assets",
    val storageDir: String = "./.storage",
    val httpCacheDir: String = "./.httpCache",

    val renderBackend: Lwjgl3Context.Backend = Lwjgl3Context.Backend.OPEN_GL,
    val windowTitle: String = "Kool App",
    val windowSize: Vec2i = Vec2i(1600, 900),
    val isFullscreen: Boolean = false,
    val showWindowOnStart: Boolean = true,
    val monitor: Int = -1,
    val windowIcon: List<BufferedImage> = DEFAULT_ICON?.let { listOf(it) } ?: emptyList(),

    val isVsync: Boolean = true,
    val maxFrameRate: Int = 0,
    val windowNotFocusedFrameRate: Int = 0,
    val msaaSamples: Int = 8,
    val customTtfFonts: Map<String, String> = emptyMap(),
) {
    companion object {
        val DEFAULT_ICON: BufferedImage?

        init {
            DEFAULT_ICON = try {
                ImageIO.read(ClassLoader.getSystemResourceAsStream("icon.png"))
            } catch (e: Exception) {
                null
            }
        }
    }
}