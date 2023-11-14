package de.fabmax.kool.editor

import de.fabmax.kool.LoadableFile
import de.fabmax.kool.LoadableFileImpl
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AvailableAssets actual constructor(assetsBaseDir: String, val browserSubDir: String) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()

    actual val rootAssets = mutableStateListOf<AssetItem>()
    actual val modelAssets = mutableStateListOf<AssetItem>()
    actual val textureAssets = mutableStateListOf<AssetItem>()
    actual val hdriTextureAssets = mutableStateListOf<AssetItem>()

    private val assetsDir = Path.of(assetsBaseDir, browserSubDir)
    private val assetsByPath = mutableMapOf<String, AssetItem>()

    private val assetsWatcher = DirectoryWatcher(setOf(assetsBaseDir))
    private val loader = launch {
        while (true) {
            assetsWatcher.changes.receive()
            updateAssets()
        }
    }

    init {
        if (!assetsDir.exists()) {
            assetsDir.createDirectories()
        }
        updateAssets()
    }

    actual fun createAssetDir(createPath: String) {
        val path = Path(assetsDir.pathString, createPath.removePrefix(browserSubDir))
        logD { "Create asset directory: $path" }
        path.createDirectories()
    }

    actual fun renameAsset(sourcePath: String, destPath: String) {
        val source = Path(assetsDir.pathString, sourcePath.removePrefix(browserSubDir))
        val dest = Path(assetsDir.pathString, destPath.removePrefix(browserSubDir))
        logD { "Rename asset: $source -> $dest" }
        dest.parent.createDirectories()
        source.moveTo(dest, StandardCopyOption.ATOMIC_MOVE)
    }

    @OptIn(ExperimentalPathApi::class)
    actual fun deleteAsset(deletePath: String) {
        val path = Path(assetsDir.pathString, deletePath.removePrefix(browserSubDir))
        logD { "Delete asset path: $path" }
        if (path.isDirectory()) {
            path.deleteRecursively()
        } else {
            path.deleteIfExists()
        }
    }

    actual fun importAssets(targetPath: String, assetFiles: List<LoadableFile>) {
        assetFiles.forEach { importAsset(targetPath, it as LoadableFileImpl) }
    }

    private fun importAsset(targetPath: String, assetFile: LoadableFileImpl) {
        logD { "Importing asset file: ${assetFile.selectionPath}" }

        val dest = Path(assetsDir.pathString, targetPath.removePrefix(browserSubDir), assetFile.selectionPath)
        try {
            dest.parent.createDirectories()
            Files.copy(assetFile.file.toPath(), dest)
        } catch (e: Exception) {
            logE { "Failed importing asset file: $e" }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun updateAssets() {
        val rootAssets = mutableListOf<AssetItem>()
        val assetPaths = mutableSetOf<String>()
        val pathPrefix = assetsDir.parent.pathString.replace('\\', '/')
        assetsByPath.values.forEach { it.children.clear() }

        assetsDir.walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach { path ->
            val pathString = path.pathString.replace('\\', '/').removeSuffix("/")
            val parentPath = pathString.replaceAfterLast('/', "").removeSuffix("/")
            val parent = assetsByPath[parentPath]

            // assetPath: asset path relative to top-level asset dir, so that it is found by asset loader
            val assetPath = pathString.removePrefix(pathPrefix).removePrefix("/")
            val assetItem = assetsByPath.getOrPut(pathString) {
                AssetItem(path.name, assetPath)
            }

            if (parent != null) {
                parent.children += assetItem
            } else {
                rootAssets += assetItem
            }
            assetPaths += pathString
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