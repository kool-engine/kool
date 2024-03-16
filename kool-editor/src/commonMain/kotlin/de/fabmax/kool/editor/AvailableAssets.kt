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

    private val assetsByPath = mutableMapOf<String, AssetItem>()

    private val fsWatcher = object : FileSystemWatcher {
        override fun onFileCreated(file: FileSystemFile) = addAssetItem(file)
        override fun onFileDeleted(file: FileSystemFile) = deleteAssetItem(file)

        override fun onDirectoryCreated(directory: FileSystemDirectory) = addAssetItem(directory)
        override fun onDirectoryDeleted(directory: FileSystemDirectory) = deleteAssetItem(directory)

        private fun addAssetItem(fileItem: FileSystemItem) {
            if (fileItem.path.startsWith(projectFiles.assets.path)) {
                val parent = assetsByPath[fileItem.parent?.path]
                if (parent == null) {
                    refreshAllAssets()
                } else {
                    val assetItem = AssetItem(fileItem)
                    assetsByPath[fileItem.path] = assetItem
                    parent.children += assetItem

                    when (assetItem.type) {
                        AppAssetType.Texture -> textureAssets += assetItem
                        AppAssetType.Hdri -> hdriAssets += assetItem
                        AppAssetType.Model -> textureAssets += assetItem
                        else -> { }
                    }
                }
            }
        }

        private fun deleteAssetItem(fileItem: FileSystemItem) {
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
                    AppAssetType.Model -> textureAssets -= deletedAsset
                    else -> { }
                }
            }
        }
    }

    init {
        projectFiles.fileSystem.addFileSystemWatcher(fsWatcher)
        refreshAllAssets()
    }

    fun createAssetDir(createPath: String) {
        val parentPath = FileSystem.sanitizeDirPath(createPath.removeSuffix("/").substringBeforeLast('/'))
        val parentItem = assetsByPath[parentPath]
        if (parentItem == null) {
            logE { "Unable to create directory: ${createPath}. Parent path not found (${parentPath})" }
            return
        }

        // Only create actual directory. No need to create the corresponding AssetItem, is handled by fsWatcher
        val parentDir = parentItem.fileItem as WritableFileSystemDirectory
        parentDir.createDirectory(createPath.removePrefix(parentPath))
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
        val deleteItem = assetsByPath[deletePath]
        if (deleteItem == null) {
            logE { "Unable to delete asset: ${deletePath}. Path not found" }
            return
        }

        // Only delete file. No need to modify asset items: is handled by fsWatcher
        when (deleteItem.fileItem) {
            is WritableFileSystemFile -> deleteItem.fileItem.delete()
            is WritableFileSystemDirectory -> deleteItem.fileItem.delete()
            else -> logE { "Unable to delete asset: ${deletePath}. Not a writable file item" }
        }
    }

    suspend fun importAssets(targetPath: String, assetFiles: List<LoadableFile>) {
        val targetDir = assetsByPath[FileSystem.sanitizeDirPath(targetPath)]?.fileItem as WritableFileSystemDirectory?
        if (targetDir == null) {
            logE { "Unable to import assets into target directory: ${targetPath}. Path not found" }
            return
        }

        assetFiles.forEach { assetFile ->
            val data = assetFile.read()
            targetDir.createFile(assetFile.name, data)
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
                AssetItem(file)
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
        assetsByPath.values.filterAssetsByType(AppAssetType.Texture, textureAssets) { !it.name.lowercase().endsWith(".rgbe.png") }
        assetsByPath.values.filterAssetsByType(AppAssetType.Texture, hdriAssets) { it.name.lowercase().endsWith(".rgbe.png") }

        rootAssets.atomic {
            clear()
            addAll(newRootAssets)
        }
    }
}

class AssetItem(val fileItem: FileSystemItem, val type: AppAssetType = AppAssetType.fromFileItem(fileItem)) {
    val name: String
        get() = fileItem.name
    val path: String
        get() = fileItem.path

    val children = mutableStateListOf<AssetItem>()

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
    Hdri,
    Model;

    companion object {
        fun fromFileItem(fileItem: FileSystemItem): AppAssetType {
            return when {
                fileItem.isDirectory -> Directory
                fileItem.path.isTexture() -> Texture
                fileItem.path.isHdri() -> Hdri
                fileItem.path.isModel() -> Model
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

        private val imageFileExtensions = setOf("jpg", "jpeg", "png")
        private val modelFileExtensions = setOf("gltf", "glb", "glbz")
    }
}
