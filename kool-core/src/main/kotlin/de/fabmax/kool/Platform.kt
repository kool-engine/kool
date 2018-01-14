package de.fabmax.kool

import de.fabmax.kool.shading.GlslGenerator
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps

/**
 * @author fabmax
 */

expect val supportsMultiContext: Boolean

expect val supportsUint32Indices: Boolean

expect fun createContext(props: RenderContext.InitProps): RenderContext

expect fun createCharMap(fontProps: FontProps): CharMap

expect fun currentTimeMillis(): Long

expect fun defaultGlslInjector(): GlslGenerator.GlslInjector

expect fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit)

expect fun loadTextureAsset(assetPath: String): TextureData

expect fun openUrl(url: String)
