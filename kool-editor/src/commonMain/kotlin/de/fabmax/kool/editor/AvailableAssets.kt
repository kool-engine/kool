package de.fabmax.kool.editor

import de.fabmax.kool.LoadableFile
import de.fabmax.kool.modules.ui2.MutableStateList

expect class AvailableAssets(assetsBaseDir: String) {
    val rootAssets: MutableStateList<AssetItem>

    val modelAssets: MutableStateList<AssetItem>
    val textureAssets: MutableStateList<AssetItem>
    val hdriTextureAssets: MutableStateList<AssetItem>

    fun createAssetDir(createPath: String)
    fun renameAsset(sourcePath: String, destPath: String)
    fun deleteAsset(deletePath: String)
    fun importAssets(targetPath: String, assetFiles: List<LoadableFile>)
}

class AssetItem(val name: String, val path: String, val type: AppAssetType) {
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
