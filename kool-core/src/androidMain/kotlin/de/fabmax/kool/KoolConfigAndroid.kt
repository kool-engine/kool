package de.fabmax.kool

import android.content.Context
import android.opengl.GLSurfaceView
import de.fabmax.kool.util.MsdfFontInfo
import de.fabmax.kool.util.MsdfMeta
import kotlinx.serialization.json.Json

data class KoolConfigAndroid(
    val appContext: Context,

    /**
     * Default asset loader used by [Assets] to load textures, models, etc.
     */
    override val defaultAssetLoader: AssetLoader = NativeAssetLoader(),

    /**
     * Default font used by UI elements.
     */
    override val defaultFont: MsdfFontInfo = DEFAULT_MSDF_FONT_INFO,

    override val numSamples: Int = 1,

    val forceFloatDepthBuffer: Boolean = false,

    /**
     * With scaleModifier = 1, screen scale is computed to match Android's device independent pixels
     * (i.e. scale = physical screen resolution / 160 dpi). scaleModifier can be used to easily adjust this scale
     * for the entire app. This only affects the size of UI elements, render resolution remains unchanged.
     */
    val scaleModifier: Float = 1f,

    val surfaceView: GLSurfaceView? = null,
) : KoolConfig {
    companion object {
        val DEFAULT_MSDF_FONT_INFO: MsdfFontInfo by lazy {
            checkNotNull(KoolConfigAndroid::class.java.classLoader)
                .getResourceAsStream("fonts/font-roboto-regular.json").use {
                    checkNotNull(it) { "Failed to load \"fonts/font-roboto-regular.json\" from resources" }
                    val meta = Json.Default.decodeFromString<MsdfMeta>(it.readBytes().decodeToString())
                    MsdfFontInfo(meta, "fonts/font-roboto-regular.png")
                }
        }
    }
}