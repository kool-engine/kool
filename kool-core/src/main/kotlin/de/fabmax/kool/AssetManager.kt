package de.fabmax.kool

import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps

abstract class AssetManager {

    abstract fun createCharMap(fontProps: FontProps): CharMap

    abstract fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit)

    abstract fun loadTextureAsset(assetPath: String): TextureData

}