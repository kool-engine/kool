package de.fabmax.kool.editor

import de.fabmax.kool.modules.ui2.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.*

actual class AppAssets actual constructor(assetsBaseDir: String) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()

    actual val assets = mutableStateOf<List<AppAsset>>(emptyList())

    private val assetsDir = Path.of(assetsBaseDir)

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
        val assets = mutableListOf<AppAsset>()
        assetsDir.walk(PathWalkOption.INCLUDE_DIRECTORIES).forEach { path ->
            val assetType = when {
                path.isDirectory() -> AppAssetType.Directory
                path.isTexture() -> AppAssetType.Texture
                path.isModel() -> AppAssetType.Model
                else -> AppAssetType.Unknown
            }
            assets += AppAsset(path.name, path.pathString, assetType)
        }
        this.assets.set(assets)
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