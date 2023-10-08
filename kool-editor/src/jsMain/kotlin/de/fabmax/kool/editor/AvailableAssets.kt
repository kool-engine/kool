package de.fabmax.kool.editor

import de.fabmax.kool.Assets
import de.fabmax.kool.LoadableFile
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.util.logE

actual class AvailableAssets actual constructor(assetsBaseDir: String, browserSubDir: String) {
    actual val rootAssets = mutableStateListOf<AssetItem>()
    actual val modelAssets = mutableStateListOf<AssetItem>()
    actual val textureAssets = mutableStateListOf<AssetItem>()
    actual val hdriTextureAssets = mutableStateListOf<AssetItem>()

    init {
        Assets.launch {
            try {
                val assetJson = loadBlobAsset("available-assets.json").toArray().decodeToString()
                val assets = JSON.parse<Array<String>>(assetJson).toList()
                addAvailableAssets(assets)
            } catch (e: Exception) {
                logE { "Failed to load asset index" }
            }
        }
    }

    private fun addAvailableAssets(assets: List<String>) {
        val rootAssets = mutableListOf<AssetItem>()
        val assetPaths = mutableSetOf<String>()
        val assetsByPath = mutableMapOf<String, AssetItem>()

        assets.forEach { pathString ->
            val parentPath = pathString.replaceAfterLast('/', "").removeSuffix("/")
            val parent = assetsByPath[parentPath]

            // assetPath: asset path relative to top-level asset dir, so that it is found by asset loader
            val assetPath = pathString.removePrefix("/")
            val assetItem = assetsByPath.getOrPut(pathString) {
                val assetName = pathString.replaceBeforeLast('/', "").removePrefix("/")
                AssetItem(assetName, assetPath)
            }

            if (parent != null) {
                parent.children += assetItem
            } else {
                rootAssets += assetItem
            }
            assetPaths += pathString
        }
        assetsByPath.values.forEach { it.sortChildrenByName() }
        assetsByPath.values.filterAssetsByType(AppAssetType.Model, modelAssets)
        assetsByPath.values.filterAssetsByType(AppAssetType.Texture, textureAssets) { !it.name.lowercase().endsWith(".rgbe.png") }
        assetsByPath.values.filterAssetsByType(AppAssetType.Texture, hdriTextureAssets) { it.name.lowercase().endsWith(".rgbe.png") }

        this.rootAssets.atomic {
            clear()
            addAll(rootAssets)
        }
    }

    actual fun createAssetDir(createPath: String) {
        browserDoesNotSupportFileManipulation()
    }

    actual fun renameAsset(sourcePath: String, destPath: String) {
        browserDoesNotSupportFileManipulation()
    }

    actual fun deleteAsset(deletePath: String) {
        browserDoesNotSupportFileManipulation()
    }

    actual fun importAssets(targetPath: String, assetFiles: List<LoadableFile>) {
        browserDoesNotSupportFileManipulation()
    }

    private fun browserDoesNotSupportFileManipulation() {
        logE { "Browser version currently does not support asset file manipulation" }
    }
}