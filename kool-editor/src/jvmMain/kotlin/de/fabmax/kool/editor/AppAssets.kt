package de.fabmax.kool.editor

import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.*

actual class AppAssets actual constructor(assetsBaseDir: String) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()

    actual val rootAssets = mutableStateOf<List<AppAsset>>(emptyList())
    actual val modelAssets = mutableStateListOf<AppAsset>()

    private val assetsDir = Path.of(assetsBaseDir)
    private val assetsByPath = mutableMapOf<String, AppAsset>()

    private val assetsNameComparator = Comparator<AppAsset> { a, b ->
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

    private val assetsWatcher = DirectoryWatcher(setOf(assetsBaseDir))

    private val loader = launch {
        while (true) {
            assetsWatcher.changes.receive()
            updateAssets()
        }
    }

    private fun updateAssets() {
        val rootAssets = mutableListOf<AppAsset>()
        val assetPaths = mutableSetOf<String>()
        assetsByPath.values.forEach { it.children.clear() }

        assetsDir.walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach { path ->
            val assetType = when {
                path.isDirectory() -> AppAssetType.Directory
                path.isTexture() -> AppAssetType.Texture
                path.isModel() -> AppAssetType.Model
                else -> AppAssetType.Unknown
            }
            val pathString = path.pathString
                .removePrefix(assetsDir.pathString)
                .replace('\\', '/').removeSuffix("/")
            val parentPath = pathString.replaceAfterLast('/', "").removeSuffix("/")
            val parent = assetsByPath[parentPath]
            val appAsset = assetsByPath.getOrPut(pathString) {
                AppAsset(path.name, pathString, assetType).apply { if (parent == null) isExpanded.set(true) }
            }

            if (parent != null) {
                parent.children += appAsset
            } else {
                rootAssets += appAsset
            }
            assetPaths += pathString
        }
        assetsByPath.keys.retainAll(assetPaths)
        assetsByPath.values.forEach { it.children.sortWith(assetsNameComparator) }
        this.rootAssets.set(rootAssets)

        modelAssets.clear()
        assetsByPath.values
            .filter { it.type == AppAssetType.Model }
            .sortedBy { it.name }
            .forEach { modelAssets += it }
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