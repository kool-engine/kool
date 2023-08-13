package de.fabmax.kool.editor

import de.fabmax.kool.LoadableFile
import de.fabmax.kool.modules.ui2.mutableStateListOf

actual class AvailableAssets actual constructor(assetsBaseDir: String, val browserSubDir: String) {
    actual val rootAssets = mutableStateListOf<AssetItem>()
    actual val modelAssets = mutableStateListOf<AssetItem>()
    actual val textureAssets = mutableStateListOf<AssetItem>()
    actual val hdriTextureAssets = mutableStateListOf<AssetItem>()

    actual fun createAssetDir(createPath: String) {
    }

    actual fun renameAsset(sourcePath: String, destPath: String) {
    }

    actual fun deleteAsset(deletePath: String) {
    }

    actual fun importAssets(
        targetPath: String,
        assetFiles: List<LoadableFile>
    ) {
    }

}