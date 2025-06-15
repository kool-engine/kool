package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.pipeline.backend.vk.VkSetup
import de.fabmax.kool.util.MsdfFontInfo
import de.fabmax.kool.util.MsdfMeta
import de.fabmax.kool.util.logD
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

data class KoolConfigJvm(
    /**
     * Default asset loader used by [Assets] to load textures, models, etc.
     */
    override val defaultAssetLoader: AssetLoader = NativeAssetLoader("./assets"),

    /**
     * Default font used by UI elements.
     */
    override val defaultFont: MsdfFontInfo = DEFAULT_MSDF_FONT_INFO,

    override val numSamples: Int = 4,

    /**
     * Alternative base path used by [Assets] to look for assets using the classloader. This can be used to place
     * assets into the resources directory under /src/commonMain/resources/[classloaderAssetPath]. The classloader
     * asset path is preferred by the [Assets] loader. If a requested asset is not found in the classloader resources
     * the regular file-system based asset path is used.
     */
    val classloaderAssetPath: String = "",

    val storageDir: String = "./.storage",
    val httpCacheDir: String = "./.httpCache",

    val renderBackend: BackendProvider = RenderBackendVk,
    val vkSetup: VkSetup? = null,
    val windowTitle: String = "Kool App",
    val windowSize: Vec2i = Vec2i(1600, 900),
    val renderScale: Float = 1f,
    val isFullscreen: Boolean = false,
    val showWindowOnStart: Boolean = true,
    val updateOnWindowResize: Boolean = true,
    val useOpenGlFallback: Boolean = true,
    val monitor: Int = -1,
    val windowIcon: List<BufferedImage> = DEFAULT_ICON?.let { listOf(it) } ?: emptyList(),

    /**
     * Windows only for now: Disable the window title bar to allow custom window themes.
     */
    val isNoTitleBar: Boolean = false,

    val isVsync: Boolean = true,
    val maxFrameRate: Int = 0,
    val windowNotFocusedFrameRate: Int = 0,
    val customTtfFonts: Map<String, String> = emptyMap(),
) : KoolConfig {

    companion object {
        init {
            // on macOS, AWT clashes with GLFW, because GLFW also has to run on the first thread enabling AWT
            // headless-mode somewhat mitigates this problem.
            // The headless property is set here because it has to happen before any AWT class (as e.g. BufferedImage)
            // is loaded.
            val osName = System.getProperty("os.name", "unknown").lowercase()
            if ("mac os" in osName || "darwin" in osName || "osx" in osName) {
                logD("KoolConfig") { "Detected macOS. Enabling AWT headless mode to mitigate AWT / GLFW compatibility issues" }
                System.setProperty("java.awt.headless", "true")
            }
        }

        val DEFAULT_ICON: BufferedImage? = try {
            KoolConfigJvm::class.java.classLoader.getResourceAsStream("icon.png").use {
                ImageIO.read(it)
            }
        } catch (e: Exception) { null }

        val DEFAULT_MSDF_FONT_INFO: MsdfFontInfo by lazy {
            KoolConfigJvm::class.java.classLoader
                .getResourceAsStream("fonts/font-roboto-regular.json").use {
                    checkNotNull(it) { "Failed to load \"fonts/font-roboto-regular.json\" from resources" }
                    val meta = Json.Default.decodeFromString<MsdfMeta>(it.readBytes().decodeToString())
                    MsdfFontInfo(meta, "fonts/font-roboto-regular.png")
                }
        }
    }
}