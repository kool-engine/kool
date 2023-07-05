package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.platform.Lwjgl3Context
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

actual data class KoolConfig(
    /**
     * Base path used by [Assets] to look for assets to be loaded (textures, models, etc.).
     */
    actual val assetPath: String = "./assets",

    /**
     * Alternative base path used by [Assets] to look for assets using the classloader. This can be used to place
     * assets into the resources directory under /src/commonMain/resources/[classloaderAssetPath]. The classloader
     * asset path is preferred by the [Assets] loader. If a requested asset is not found in the classloader resources
     * the regular file-system based [assetPath] is used.
     */
    val classloaderAssetPath: String = "",

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
                ImageIO.read(KoolConfig::class.java.classLoader.getResourceAsStream("icon.png"))
            } catch (e: Exception) {
                null
            }
        }
    }
}