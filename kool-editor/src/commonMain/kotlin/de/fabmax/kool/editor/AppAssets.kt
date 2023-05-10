package de.fabmax.kool.editor

import de.fabmax.kool.modules.ui2.MutableStateValue

expect class AppAssets(assetsBaseDir: String) {
    val assets: MutableStateValue<List<AppAsset>>
}

data class AppAsset(val name: String, val path: String, val type: AppAssetType)

enum class AppAssetType {
    Unknown,
    Directory,
    Texture,
    Model
}
