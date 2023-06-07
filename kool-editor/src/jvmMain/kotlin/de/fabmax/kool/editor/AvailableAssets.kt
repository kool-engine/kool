package de.fabmax.kool.editor

import de.fabmax.kool.LoadableFile
import de.fabmax.kool.modules.ui2.MutableStateList
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.*

actual class AvailableAssets actual constructor(assetsBaseDir: String) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()

    actual val rootAssets = mutableStateListOf<AssetItem>()
    actual val modelAssets = mutableStateListOf<AssetItem>()
    actual val textureAssets = mutableStateListOf<AssetItem>()
    actual val hdriTextureAssets = mutableStateListOf<AssetItem>()

    private val assetsDir = Path.of(assetsBaseDir)
    private val assetsByPath = mutableMapOf<String, AssetItem>()

    private val assetsNameComparator = Comparator<AssetItem> { a, b ->
        if (a.type == AppAssetType.Directory && b.type != AppAssetType.Directory) {
            -1
        } else if (a.type != AppAssetType.Directory && b.type == AppAssetType.Directory) {
            1
        } else {
            a.path.compareTo(b.path)
        }
    }

    init {
        if (!assetsDir.exists()) {
            assetsDir.createDirectories()
        }
        updateAssets()
    }

    actual fun createAssetDir(createPath: String) {
        val path = Path(assetsDir.pathString, createPath)
        path.createDirectories()
    }

    actual fun deleteAssetDir(deletePath: String) {
        val path = Path(assetsDir.pathString, deletePath)
        path.deleteRecursively()
    }

    actual fun importAssets(targetPath: String, assetFiles: List<LoadableFile>) {
        assetFiles.forEach { importAsset(targetPath, it) }
    }

    private fun importAsset(targetPath: String, assetFile: LoadableFile) {
        logD { "Importing asset file: ${assetFile.selectionPath}" }

        val dest = Path(assetsDir.pathString, targetPath, assetFile.selectionPath)
        try {
            dest.parent.createDirectories()
            Files.copy(assetFile.file.toPath(), dest)
        } catch (e: Exception) {
            logE { "Failed importing asset file: $e" }
        }
    }

    private val assetsWatcher = DirectoryWatcher(setOf(assetsBaseDir))

    private val loader = launch {
        while (true) {
            assetsWatcher.changes.receive()
            updateAssets()
        }
    }

    private fun updateAssets() {
        val rootAssets = mutableListOf<AssetItem>()
        val assetPaths = mutableSetOf<String>()
        val pathPrefix = assetsDir.pathString.replace('\\', '/')
        assetsByPath.values.forEach { it.children.clear() }

        assetsDir.walk(PathWalkOption.INCLUDE_DIRECTORIES).filter { it.name != "kool-project.json" }.forEach { path ->
            val assetType = when {
                path.isDirectory() -> AppAssetType.Directory
                path.isTexture() -> AppAssetType.Texture
                path.isModel() -> AppAssetType.Model
                else -> AppAssetType.Unknown
            }

            val pathString = path.pathString.replace('\\', '/').removeSuffix("/")
            val parentPath = pathString.replaceAfterLast('/', "").removeSuffix("/")
            val parent = assetsByPath[parentPath]

            // assetPath: asset path relative to top-level asset dir, so that it is found by asset loader
            val assetPath = pathString.removePrefix(pathPrefix).removePrefix("/")
            val assetItem = assetsByPath.getOrPut(pathString) {
                AssetItem(path.name, assetPath, assetType)
            }

            if (parent != null) {
                parent.children += assetItem
            } else {
                rootAssets += assetItem
            }
            assetPaths += pathString
        }
        assetsByPath.keys.retainAll(assetPaths)
        assetsByPath.values.forEach { it.children.sortWith(assetsNameComparator) }

        filterAssetsByType(AppAssetType.Model, modelAssets)
        filterAssetsByType(AppAssetType.Texture, textureAssets) { !it.name.lowercase().endsWith(".rgbe.png") }
        filterAssetsByType(AppAssetType.Texture, hdriTextureAssets) { it.name.lowercase().endsWith(".rgbe.png") }

        this.rootAssets.atomic {
            clear()
            addAll(rootAssets)
        }
    }

    private fun filterAssetsByType(type: AppAssetType, result: MutableStateList<AssetItem>, filter: (AssetItem) -> Boolean = { true }) {
        result.atomic {
            clear()
            assetsByPath.values
                .filter { it.type == type }
                .filter(filter)
                .sortedBy { it.name }
                .forEach { add(it) }
        }
    }

    private fun Path.isTexture(): Boolean {
        return name
            .replaceBeforeLast(".", "")
            .lowercase() in imageFileExtensions
    }

    private fun Path.isModel(): Boolean {
        return name
            .removeSuffix(".gz")
            .replaceBeforeLast(".", "")
            .lowercase() in modelFileExtensions
    }

    companion object {
        val imageFileExtensions = setOf(".jpg", ".jpeg", ".png")
        val modelFileExtensions = setOf(".gltf", ".glb", ".glbz")
    }
}