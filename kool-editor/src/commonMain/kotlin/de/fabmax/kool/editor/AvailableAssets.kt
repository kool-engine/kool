package de.fabmax.kool.editor

import de.fabmax.kool.LoadableFile
import de.fabmax.kool.modules.ui2.MutableStateList

expect class AvailableAssets(assetsBaseDir: String, browserSubDir: String) {
    val rootAssets: MutableStateList<AssetItem>

    val modelAssets: MutableStateList<AssetItem>
    val textureAssets: MutableStateList<AssetItem>
    val hdriTextureAssets: MutableStateList<AssetItem>

    fun createAssetDir(createPath: String)
    fun renameAsset(sourcePath: String, destPath: String)
    fun deleteAsset(deletePath: String)
    fun importAssets(targetPath: String, assetFiles: List<LoadableFile>)
}

class AssetItem(val name: String, val path: String, val type: AppAssetType = AppAssetType.fromPath(path)) {
    val children = mutableListOf<AssetItem>()

    override fun toString(): String {
        return name
    }

    fun sortChildrenByName() {
        children.sortWith(assetsNameComparator)
    }

    companion object {
        val assetsNameComparator = Comparator<AssetItem> { a, b ->
            if (a.type == AppAssetType.Directory && b.type != AppAssetType.Directory) {
                -1
            } else if (a.type != AppAssetType.Directory && b.type == AppAssetType.Directory) {
                1
            } else {
                a.path.lowercase().compareTo(b.path.lowercase())
            }
        }
    }
}

fun Collection<AssetItem>.filterAssetsByType(type: AppAssetType, result: MutableStateList<AssetItem>, filter: (AssetItem) -> Boolean = { true }) {
    result.atomic {
        clear()
        this@filterAssetsByType
            .filter { it.type == type }
            .filter(filter)
            .sortedBy { it.name }
            .forEach { add(it) }
    }
}

enum class AppAssetType {
    Unknown,
    Directory,
    Texture,
    Model;

    companion object {
        fun fromPath(path: String): AppAssetType {
            return when {
                path.isDirectory() -> Directory
                path.isTexture() -> Texture
                path.isModel() -> Model
                else -> Unknown
            }
        }

        private fun String.isDirectory(): Boolean {
            return !this
                .replaceBeforeLast("/", "")
                .contains('.')
        }

        private fun String.isTexture(): Boolean {
            return this
                .replaceBeforeLast(".", "")
                .lowercase() in imageFileExtensions
        }

        private fun String.isModel(): Boolean {
            return this
                .removeSuffix(".gz")
                .replaceBeforeLast(".", "")
                .lowercase() in modelFileExtensions
        }

        val imageFileExtensions = setOf(".jpg", ".jpeg", ".png")
        val modelFileExtensions = setOf(".gltf", ".glb", ".glbz")
    }
}
