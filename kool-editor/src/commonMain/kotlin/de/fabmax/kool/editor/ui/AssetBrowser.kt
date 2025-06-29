package de.fabmax.kool.editor.ui

import de.fabmax.kool.Assets
import de.fabmax.kool.FileFilterItem
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.MimeType
import de.fabmax.kool.editor.AppAssetType
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.util.ThumbnailRenderer
import de.fabmax.kool.editor.util.ThumbnailState
import de.fabmax.kool.editor.util.hdriThumbnail
import de.fabmax.kool.editor.util.textureThumbnail
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.launchOnMainThread
import kotlin.math.roundToInt

class AssetBrowser(ui: EditorUi) : BrowserPanel("Asset Browser", Icons.medium.picture, ui) {

    private val thumbnailRenderer = ThumbnailRenderer("asset-thumbnails")
    private val textureThumbnails = BrowserThumbnails<String>(thumbnailRenderer) { thumbnailRenderer.textureThumbnail(it) }
    private val hdriThumbnails = BrowserThumbnails<String>(thumbnailRenderer) { thumbnailRenderer.hdriThumbnail(it) }

    init {
        KoolSystem.requireContext().backgroundPasses += thumbnailRenderer
    }

    override fun UiScope.titleBar() {
        thumbnailRenderer.updateTileSize(sizes.browserItemSize.px.roundToInt())

        Row(height = Grow.Std) {
            val popup = remember { ContextPopupMenu<BrowserItem>("import-assets-popup") }
            var popupPos by remember(Vec2f.ZERO)

            // register drag callbacks, to block window drag
            modifier
                .onDragStart {  }
                .alignY(AlignmentY.Center)

            divider(colors.strongDividerColor, marginStart = sizes.largeGap, marginEnd = sizes.largeGap, verticalMargin = sizes.gap)

            val button = iconTextButton(
                icon = Icons.small.plus,
                text = "Import Assets",
                bgColor = colors.componentBg,
                bgColorHovered = colors.componentBgHovered,
                bgColorClicked = colors.elevatedComponentBgHovered,
                width = sizes.baseSize * 4
            ) {
                if (!popup.isVisible.use()) {
                    selectedDirectory.use()?.let {
                        popup.show(popupPos, makeImportAssetsMenu(), it)
                    }
                } else {
                    popup.hide()
                }
            }

            popupPos = Vec2f(button.uiNode.leftPx, button.uiNode.bottomPx)
            popup()
        }
    }

    private fun makeImportAssetsMenu(): SubMenuItem<BrowserItem> = SubMenuItem {
        importAssetsItem("Textures", filterListTextures)
        importAssetsItem("Models", filterListModels)
        importAssetsItem("Other", emptyList())
    }

    override fun UiScope.collectBrowserDirs(traversedPaths: MutableSet<String>) {
        editor.availableAssets.rootAssets.use().forEach {
            traverseAssetItem(0, it, traversedPaths, true)
        }
    }

    private fun UiScope.traverseAssetItem(
        level: Int,
        assetItem: AssetItem,
        traversedPaths: MutableSet<String>,
        addToDirTree: Boolean
    ): BrowserItem {
        traversedPaths += assetItem.path
        var item = browserItems[assetItem.path]
        if (item == null || !assetItem.type.matchesBrowserItemType(item)) {
            item = if (assetItem.type == AppAssetType.Directory) {
                val name = if (level == 0) "Assets" else assetItem.name
                BrowserDir(level, name, assetItem.path)
            } else {
                BrowserAssetItem(level, assetItem).apply {
                    assetItem.getThumbnailComposable()?.let { composable = it }
                }
            }
            browserItems[assetItem.path] = item
        }
        if (item is BrowserDir) {
            if (addToDirTree) {
                expandedDirTree.add(item)
            }
            item.isExpandable.set(assetItem.children.any { it.type == AppAssetType.Directory })
            item.children.clear()
            assetItem.children.use().forEach {
                item.children += traverseAssetItem(
                    level = level + 1,
                    assetItem = it,
                    traversedPaths = traversedPaths,
                    addToDirTree = addToDirTree && item.isExpanded.use()
                )
            }
        }
        return item
    }

    private fun AssetItem.getThumbnailComposable(): BrowserItemComposable? {
        return when (type) {
            AppAssetType.Texture -> textureThumbnails.getThumbnailComposable(path)
            AppAssetType.Hdri -> hdriThumbnails.getThumbnailComposable(path)
            else -> null
        }
    }

    private fun AppAssetType.matchesBrowserItemType(browserItem: BrowserItem?): Boolean {
        return this == AppAssetType.Directory &&
                (browserItem is BrowserDir || browserItem is BrowserAssetItem)
    }

    override fun makeDirPopupMenu(item: BrowserDir, isInTree: Boolean): SubMenuItem<BrowserItem> {
        return SubMenuItem {
            if (isInTree) {
                // tree view directory popup menu
                createDirectoryItem()
                if (item.level > 0) {
                    // directory popup menu inside tree view -> here, directory can also be renamed and deleted
                    renameDirectoryItem()
                    deleteDirectoryItem()
                }
                divider()
                importAssetsMenu()

            } else if (item == selectedDirectory.value) {
                // item view empty space popup menu
                createDirectoryItem()
                importAssetsMenu()

            } else {
                // item view child directory popup menu
                renameDirectoryItem()
                deleteDirectoryItem()
            }
        }
    }

    override fun onItemClick(item: BrowserItem, ev: PointerEvent): SubMenuItem<BrowserItem>? {
        val assetItem = item as? BrowserAssetItem
        return when {
            assetItem != null && ev.isRightClick -> SubMenuItem {
                renameAssetItem()
                deleteAssetItem()
            }
            else -> super.onItemClick(item, ev)
        }
    }

    private fun SubMenuItem<BrowserItem>.createDirectoryItem() = item("Create directory") { item ->
        OkCancelEnterTextDialog("Create New Directory", hint = "Directory name") {
            if (it.isNotBlank()) {
                editor.availableAssets.createAssetDir("${item.path}/${it.trim()}")
            }
        }
    }

    private fun SubMenuItem<BrowserItem>.renameDirectoryItem() = item("Rename directory") { item ->
        OkCancelEnterTextDialog("Rename / Move Directory", item.path, hint = "New directory name") {
            if (it.isNotBlank()) {
                launchOnMainThread {
                    editor.availableAssets.renameAsset(item.path, it.trim())
                }
            }
        }
    }

    private fun SubMenuItem<BrowserItem>.deleteDirectoryItem() = item("Delete directory") { item ->
        OkCancelTextDialog("Delete Directory", "Delete directory \"${item.name}\" including all its contents?\nThis cannot be undone.") {
            editor.availableAssets.deleteAsset(item.path)
        }
    }

    private fun SubMenuItem<BrowserItem>.renameAssetItem() = item("Rename asset file") { item ->
        OkCancelEnterTextDialog("Rename / Move Asset File", item.path, hint = "New asset file name") {
            if (it.isNotBlank()) {
                launchOnMainThread {
                    editor.availableAssets.renameAsset(item.path, it.trim())
                }
            }
        }
    }

    private fun SubMenuItem<BrowserItem>.deleteAssetItem() = item("Delete asset file") { item ->
        OkCancelTextDialog("Delete Asset File", "Delete asset file \"${item.name}\"? This cannot be undone.") {
            editor.availableAssets.deleteAsset(item.path)
        }
    }

    private fun SubMenuItem<BrowserItem>.importAssetsMenu() = subMenu("Import") {
        importAssetsItem("Textures", filterListTextures)
        importAssetsItem("Models", filterListModels)
        importAssetsItem("Other", emptyList())
    }

    private fun SubMenuItem<BrowserItem>.importAssetsItem(label: String, filterList: List<FileFilterItem>) = item(label) { item ->
        launchOnMainThread {
            val importFiles = Assets.loadFileByUser(filterList, true)
            editor.availableAssets.importAssets(item.path, importFiles)
        }
    }

    fun onAssetItemChanged(item: AssetItem) {
        when (item.type) {
            AppAssetType.Texture -> textureThumbnails.getThumbnail(item.path)?.state?.set(ThumbnailState.USABLE_OUTDATED)
            AppAssetType.Hdri -> hdriThumbnails.getThumbnail(item.path)?.state?.set(ThumbnailState.USABLE_OUTDATED)
            else -> { }
        }
    }

    companion object {
        private val filterListTextures = listOf(
            FileFilterItem("Images", MimeType("image/*"), listOf(".jpg", ".png", ".hdr"))
        )

        private val filterListModels = listOf(
            FileFilterItem("glTF Models", MimeType.BINARY_DATA, listOf(".glb", ".glb.gz", ".gltf", ".gltf.gz"))
        )
    }
}