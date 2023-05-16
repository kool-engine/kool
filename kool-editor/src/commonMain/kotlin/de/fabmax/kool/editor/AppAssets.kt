package de.fabmax.kool.editor

import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf

expect class AppAssets(assetsBaseDir: String) {
    val rootAssets: MutableStateValue<List<AppAsset>>

    val modelAssets: MutableStateList<AppAsset>
}

class AppAsset(val name: String, val path: String, val type: AppAssetType) {
    val isExpanded = mutableStateOf(false)
    val children = mutableListOf<AppAsset>()
}

enum class AppAssetType {
    Unknown,
    Directory,
    Texture,
    Model
}
