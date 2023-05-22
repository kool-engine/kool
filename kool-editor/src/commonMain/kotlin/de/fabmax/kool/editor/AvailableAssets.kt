package de.fabmax.kool.editor

import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.modules.ui2.mutableStateOf

expect class AvailableAssets(assetsBaseDir: String) {
    val rootAssets: MutableStateList<AssetItem>

    val modelAssets: MutableStateList<AssetItem>
    val textureAssets: MutableStateList<AssetItem>
}

class AssetItem(val name: String, val path: String, val type: AppAssetType) {
    val isExpanded = mutableStateOf(false)
    val children = mutableListOf<AssetItem>()

    override fun toString(): String {
        return name
    }
}

enum class AppAssetType {
    Unknown,
    Directory,
    Texture,
    Model
}
