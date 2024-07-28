package de.fabmax.kool.editor

import de.fabmax.kool.LoadableFile
import de.fabmax.kool.modules.filesystem.*
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.util.logE

class AvailableAssets(private val projectFiles: ProjectFiles) {
    val rootAssets = mutableStateListOf<AssetItem>()
    val modelAssets = mutableStateListOf<AssetItem>()
    val textureAssets = mutableStateListOf<AssetItem>()
    val hdriAssets = mutableStateListOf<AssetItem>()
    val heightmapAssets = mutableStateListOf<AssetItem>()

    private val assetPathPrefix = projectFiles.assets.path
    private val assetsByPath = mutableMapOf<String, AssetItem>()

    private val fsWatcher = object : FileSystemWatcher {
        override fun onFileCreated(file: FileSystemFile) = addAssetItem(file)
        override fun onFileDeleted(file: FileSystemFile) = removeAssetItem(file)
        override fun onFileChanged(file: FileSystemFile) = updateAssetItem(file)

        override fun onDirectoryCreated(directory: FileSystemDirectory) = addAssetItem(directory)
        override fun onDirectoryDeleted(directory: FileSystemDirectory) = removeAssetItem(directory)

        private fun addAssetItem(fileItem: FileSystemItem) {
            if (fileItem.path.startsWith(projectFiles.assets.path)) {
                val parent = assetsByPath[fileItem.parent?.path]
                if (parent == null) {
                    refreshAllAssets()
                } else {
                    val assetItem = AssetItem(fileItem, fileItem.assetPath)
                    assetsByPath[fileItem.path] = assetItem
                    parent.children += assetItem
                    parent.sortChildrenByName()

                    when (assetItem.type) {
                        AppAssetType.Texture -> textureAssets += assetItem
                        AppAssetType.Hdri -> hdriAssets += assetItem
                        AppAssetType.Model -> modelAssets += assetItem
                        AppAssetType.Heightmap -> heightmapAssets += assetItem
                        AppAssetType.Directory -> { }
                        AppAssetType.Unknown -> { }
                    }
                }
            }
        }

        private fun removeAssetItem(fileItem: FileSystemItem) {
            val deletedAsset = assetsByPath[fileItem.path] ?: return
            val parent = assetsByPath[fileItem.parent?.path]
            if (parent == null) {
                refreshAllAssets()
            } else {
                assetsByPath -= fileItem.path
                parent.children -= deletedAsset

                when (deletedAsset.type) {
                    AppAssetType.Texture -> textureAssets -= deletedAsset
                    AppAssetType.Hdri -> hdriAssets -= deletedAsset
                    AppAssetType.Model -> modelAssets -= deletedAsset
                    AppAssetType.Heightmap -> heightmapAssets -= deletedAsset
                    AppAssetType.Directory -> { }
                    AppAssetType.Unknown -> { }
                }
            }
        }

        private fun updateAssetItem(fileItem: FileSystemItem) {
            val changedAsset = assetsByPath[fileItem.path] ?: return
            KoolEditor.instance.cachedAppAssets.reloadAsset(changedAsset)
        }
    }

    init {
        projectFiles.fileSystem.addFileSystemWatcher(fsWatcher)
        refreshAllAssets()
    }

    fun createAssetDir(createPath: String) {
        val prefixedPath = FileSystem.sanitizePath("${assetPathPrefix}/${createPath}")
        val parentPath = FileSystem.parentPath(prefixedPath)
        val parentItem = assetsByPath[parentPath]?.fileItem as? WritableFileSystemDirectory?
        if (parentItem == null) {
            logE { "Unable to create directory: ${createPath}. Parent directory not found or not writable" }
            return
        }
        parentItem.createDirectory(prefixedPath.removePrefix(parentPath))
    }

    suspend fun renameAsset(sourcePath: String, destPath: String) {
        val prefixedSrc = FileSystem.sanitizePath("${assetPathPrefix}/${sourcePath}")
        val prefixedDst = FileSystem.sanitizePath("${assetPathPrefix}/${destPath}")
        projectFiles.fileSystem.move(prefixedSrc, prefixedDst)
    }

    fun deleteAsset(deletePath: String) {
        val prefixedPath = FileSystem.sanitizePath("${assetPathPrefix}/${deletePath}")
        val deleteItem = assetsByPath[prefixedPath]?.fileItem as? WritableFileSystemItem?
        if (deleteItem == null) {
            logE { "Unable to delete asset: ${deletePath}. Path not found or not writable" }
            return
        }
        deleteItem.delete()
    }

    suspend fun importAssets(targetPath: String, assetFiles: List<LoadableFile>) {
        val prefixedPath = FileSystem.sanitizePath("${assetPathPrefix}/${targetPath}")
        val targetDir = assetsByPath[prefixedPath]?.fileItem as? WritableFileSystemDirectory?
        if (targetDir == null) {
            logE { "Unable to import assets into target directory: ${targetPath}. Path not found or not writable" }
            return
        }

        assetFiles.forEach { assetFile ->
            val data = assetFile.read()
            val existing = targetDir.getFileOrNull(assetFile.name) as WritableFileSystemFile?
            if (existing == null) {
                targetDir.createFile(assetFile.name, data)
            } else {
                existing.write(data)
            }
        }
    }

    private fun refreshAllAssets() {
        val assetPaths = mutableSetOf<String>()
        val newRootAssets = mutableListOf<AssetItem>()
        assetsByPath.values.forEach { it.children.clear() }

        projectFiles.assets.collect().forEach { file ->
            val pathString = file.path
            val parentPath = file.parent?.path
            val parent = assetsByPath[parentPath]

            val assetItem = assetsByPath.getOrPut(file.path) {
                AssetItem(file, file.assetPath)
            }

            if (parent != null) {
                parent.children += assetItem
            } else {
                newRootAssets += assetItem
            }
            assetPaths += pathString
        }

        assetsByPath.keys.retainAll(assetPaths)
        assetsByPath.values.forEach { it.sortChildrenByName() }
        assetsByPath.values.filterAssetsByType(AppAssetType.Model, modelAssets)
        assetsByPath.values.filterAssetsByType(AppAssetType.Texture, textureAssets)
        assetsByPath.values.filterAssetsByType(AppAssetType.Hdri, hdriAssets)
        assetsByPath.values.filterAssetsByType(AppAssetType.Heightmap, heightmapAssets)

        rootAssets.atomic {
            clear()
            addAll(newRootAssets)
        }
    }

    private val FileSystemItem.assetPath: String
        get() = path.removePrefix(assetPathPrefix)
}

class AssetItem(
    val fileItem: FileSystemItem,
    val path: String,
    val type: AppAssetType = AppAssetType.fromFileItem(fileItem)
) {
    val name: String
        get() = fileItem.name

    val children = mutableStateListOf<AssetItem>()

    fun sortChildrenByName() {
        children.sortWith(assetsNameComparator)
    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AssetItem
        if (path != other.path) return false
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + type.hashCode()
        return result
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
    Hdri,
    Model,
    Heightmap;

    companion object {
        fun fromFileItem(fileItem: FileSystemItem): AppAssetType {
            return when {
                fileItem.isDirectory -> Directory
                fileItem.path.isTexture() -> Texture
                fileItem.path.isHdri() -> Hdri
                fileItem.path.isModel() -> Model
                fileItem.path.isHeightmap() -> Heightmap
                else -> Unknown
            }
        }

        private fun String.isHdri(): Boolean {
            return this.lowercase().endsWith(".rgbe.png")
        }

        private fun String.isTexture(): Boolean {
            return !isHdri() && this
                .substringAfterLast(".")
                .lowercase() in imageFileExtensions
        }

        private fun String.isModel(): Boolean {
            return this
                .removeSuffix(".gz")
                .substringAfterLast(".")
                .lowercase() in modelFileExtensions
        }

        private fun String.isHeightmap(): Boolean {
            return this
                .removeSuffix(".gz")
                .substringAfterLast(".")
                .lowercase() in heightmapFileExtensions
        }

        private val imageFileExtensions = setOf("jpg", "jpeg", "png")
        private val modelFileExtensions = setOf("gltf", "glb", "glbz")
        private val heightmapFileExtensions = setOf("hgt", "raw")
    }
}
