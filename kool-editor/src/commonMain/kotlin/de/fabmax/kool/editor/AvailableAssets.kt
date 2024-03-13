package de.fabmax.kool.editor

import de.fabmax.kool.LoadableFile
import de.fabmax.kool.modules.filesystem.FileSystemFile
import de.fabmax.kool.modules.filesystem.FileSystemItem
import de.fabmax.kool.modules.filesystem.FileSystemWatcher
import de.fabmax.kool.modules.filesystem.collect
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.modules.ui2.mutableStateListOf

class AvailableAssets(private val projectFiles: ProjectFiles) {
    val rootAssets = mutableStateListOf<AssetItem>()
    val modelAssets = mutableStateListOf<AssetItem>()
    val textureAssets = mutableStateListOf<AssetItem>()
    val hdriTextureAssets = mutableStateListOf<AssetItem>()

    private val assetsByPath = mutableMapOf<String, AssetItem>()

    val fsWatcher = object : FileSystemWatcher {
        override fun onFileCreated(file: FileSystemFile) {
        }

        override fun onFileDeleted(file: FileSystemFile) {
        }
    }

    init {
        projectFiles.fileSystem.addFileSystemWatcher(fsWatcher)
        updateAssets()
    }

    fun createAssetDir(createPath: String) {
        TODO()
//        val path = Path(assetsDir.pathString, createPath.removePrefix(browserSubDir))
//        logD { "Create asset directory: $path" }
//        path.createDirectories()
    }

    fun renameAsset(sourcePath: String, destPath: String) {
        TODO()
//        val source = Path(assetsDir.pathString, sourcePath.removePrefix(browserSubDir))
//        val dest = Path(assetsDir.pathString, destPath.removePrefix(browserSubDir))
//        logD { "Rename asset: $source -> $dest" }
//        dest.parent.createDirectories()
//        source.moveTo(dest, StandardCopyOption.ATOMIC_MOVE)
    }

    fun deleteAsset(deletePath: String) {
        TODO()
//        val path = Path(assetsDir.pathString, deletePath.removePrefix(browserSubDir))
//        logD { "Delete asset path: $path" }
//        if (path.isDirectory()) {
//            path.deleteRecursively()
//        } else {
//            path.deleteIfExists()
//        }
    }

    fun importAssets(targetPath: String, assetFiles: List<LoadableFile>) {
        TODO()
//        assetFiles.forEach { importAsset(targetPath, it as LoadableFileImpl) }
    }

//    private fun importAsset(targetPath: String, assetFile: LoadableFileImpl) {
//        logD { "Importing asset file: ${assetFile.selectionPath}" }
//
//        val dest = Path(assetsDir.pathString, targetPath.removePrefix(browserSubDir), assetFile.selectionPath)
//        try {
//            dest.parent.createDirectories()
//            Files.copy(assetFile.file.toPath(), dest)
//        } catch (e: Exception) {
//            logE { "Failed importing asset file: $e" }
//        }
//    }

    private fun updateAssets() {
        val assetPaths = mutableSetOf<String>()
        val rootAssets = mutableListOf<AssetItem>()
        assetsByPath.values.forEach { it.children.clear() }

        with(projectFiles.fileSystem) {
            projectFiles.assets.collect().forEach { file ->
                val pathString = file.path
                val parentPath = file.parent.path
                val parent = assetsByPath[parentPath]

                val assetItem = assetsByPath.getOrPut(file.path) {
                    AssetItem(file)
                }

                if (parent != null) {
                    parent.children += assetItem
                } else {
                    rootAssets += assetItem
                }
                assetPaths += pathString
            }
        }

        assetsByPath.keys.retainAll(assetPaths)
        assetsByPath.values.forEach { it.sortChildrenByName() }
        assetsByPath.values.filterAssetsByType(AppAssetType.Model, modelAssets)
        assetsByPath.values.filterAssetsByType(AppAssetType.Texture, textureAssets) { !it.name.lowercase().endsWith(".rgbe.png") }
        assetsByPath.values.filterAssetsByType(AppAssetType.Texture, hdriTextureAssets) { it.name.lowercase().endsWith(".rgbe.png") }

        this.rootAssets.atomic {
            clear()
            addAll(rootAssets)
        }
    }
}

class AssetItem(val fileItem: FileSystemItem, val type: AppAssetType = AppAssetType.fromFileItem(fileItem)) {
    val name: String
        get() = fileItem.name
    val path: String
        get() = fileItem.path

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
        fun fromFileItem(fileItem: FileSystemItem): AppAssetType {
            return when {
                fileItem.isDirectory -> Directory
                fileItem.path.isTexture() -> Texture
                fileItem.path.isModel() -> Model
                else -> Unknown
            }
        }

        private fun String.isTexture(): Boolean {
            return this
                .substringAfterLast(".")
                .lowercase() in imageFileExtensions
        }

        private fun String.isModel(): Boolean {
            return this
                .removeSuffix(".gz")
                .substringAfterLast(".")
                .lowercase() in modelFileExtensions
        }

        private val imageFileExtensions = setOf("jpg", "jpeg", "png")
        private val modelFileExtensions = setOf("gltf", "glb", "glbz")
    }
}
