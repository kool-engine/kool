package de.fabmax.kool

import de.fabmax.kool.util.MsdfFontInfo
import de.fabmax.kool.util.MsdfMeta
import kotlinx.serialization.json.Json

data class KoolConfigAndroid(
    /**
     * Default asset loader used by [Assets] to load textures, models, etc.
     */
    override val defaultAssetLoader: AssetLoader = NativeAssetLoader("./assets"),

    /**
     * Default font used by UI elements.
     */
    override val defaultFont: MsdfFontInfo = DEFAULT_MSDF_FONT_INFO,

    val msaaSamples: Int = 4,

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