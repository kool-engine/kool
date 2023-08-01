package de.fabmax.kool.editor.ui

import de.fabmax.kool.Assets
import de.fabmax.kool.FileFilterItem
import de.fabmax.kool.editor.AppAssetType
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.modules.ui2.UiScope

class AssetBrowser(ui: EditorUi) : BrowserPanel("Asset Browser", IconMap.medium.PICTURE, ui) {
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
                BrowserAssetItem(level, assetItem)
            }
            browserItems[assetItem.path] = item
        }
        if (item is BrowserDir) {
            if (addToDirTree) {
                expandedDirTree.add(item)
            }
            item.isExpandable.set(assetItem.children.any { it.type == AppAssetType.Directory })
            item.children.clear()
            assetItem.children.forEach {
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

    private fun AppAssetType.matchesBrowserItemType(browserItem: BrowserItem?): Boolean {
        return this == AppAssetType.Directory &&
                (browserItem is BrowserDir || browserItem is BrowserAssetItem)
    }

    override fun makeItemPopupMenu(item: BrowserItem, isTreeItem: Boolean): SubMenuItem<BrowserItem> {
        return SubMenuItem {
            if (isTreeItem) {
                // tree view directory popup menu
                createDirectoryItem()
                if (item.level > 0) {
                    // directory popup menu inside tree view -> here, directory can also be renamed and deleted
                    renameDirectoryItem()
                    deleteDirectoryItem()
                }
                divider()
                importAssetsMenu()

            } else if (item is BrowserDir && item == selectedDirectory.value) {
                // item view empty space popup menu
                createDirectoryItem()
                importAssetsMenu()

            } else if (item is BrowserDir) {
                // item view directory popup menu
                renameDirectoryItem()
                deleteDirectoryItem()

            } else {
                // item view non-directory popup menu
                renameAssetItem()
                deleteAssetItem()
            }
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
                editor.availableAssets.renameAsset(item.path, it.trim())
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
                editor.availableAssets.renameAsset(item.path, it.trim())
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
        Assets.launch {
            val importFiles = loadFileByUser(filterList, true)
            editor.availableAssets.importAssets(item.path, importFiles)
        }
    }

    companion object {
        private val filterListTextures = listOf(
            FileFilterItem("Images", ".jpg, .png, .hdr")
        )

        private val filterListModels = listOf(
            FileFilterItem("glTF Models", ".glb, .glb.gz, .gltf, .gltf.gz")
        )
    }
}