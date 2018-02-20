package de.fabmax.kool

import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps

/**
 * @author fabmax
 */

expect var glCapabilities: GlCapabilities

expect fun createContext(props: RenderContext.InitProps): RenderContext

expect fun createCharMap(fontProps: FontProps): CharMap

expect fun currentTimeMillis(): Long

expect fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit)

expect fun loadTextureAsset(assetPath: String): TextureData

expect fun openUrl(url: String)

expect fun getMemoryInfo(): String
