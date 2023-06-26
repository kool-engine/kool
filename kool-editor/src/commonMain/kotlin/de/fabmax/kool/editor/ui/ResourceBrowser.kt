package de.fabmax.kool.editor.ui

import de.fabmax.kool.Assets
import de.fabmax.kool.FileFilterItem
import de.fabmax.kool.editor.AppAssetType
import de.fabmax.kool.editor.AppScript
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.DeleteMaterialAction
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class ResourceBrowser(editorUi: EditorUi) : EditorPanel(
    "Resource Browser",
    editorUi,
    defaultWidth = Dp(600f),
    defaultHeight = Dp(300f)
) {

    private val browserItems = mutableMapOf<String, BrowserItem>()
    private val expandedDirTree = mutableListOf<BrowserDir>()

    val selectedDirectory = mutableStateOf<BrowserDir?>(null)

    override val windowSurface = EditorPanelWindow {
        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable)
            Row(Grow.Std, Grow.Std) {
                refreshBrowserItems()

                directoryTree()
                directoryContent()
            }
        }
    }

    private fun UiScope.refreshBrowserItems() {
        val travPaths = mutableSetOf<String>()
        expandedDirTree.clear()

        val dirSpacer = browserItems.getOrPut("/!spacer") { BrowserDir(0, "", "/!spacer", BrowserCategory.SPACER) } as BrowserDir

        editor.availableAssets.rootAssets.use().forEach { traverseAssetItem(0, it, travPaths, true) }

        expandedDirTree.add(dirSpacer)
        refreshMaterials(travPaths)

        expandedDirTree.add(dirSpacer)
        refreshScripts(travPaths)

        browserItems.keys.retainAll(travPaths)
        selectedDirectory.value?.path?.let { selectedDir ->
            if (selectedDir !in browserItems.keys) {
                selectedDirectory.set(browserItems.values.firstOrNull { it is BrowserDir && it.level == 0 } as BrowserDir?)
            }
        }
    }

    private fun UiScope.refreshMaterials(traversedPaths: MutableSet<String>) {
        val materialDir = browserItems.getOrPut("/materials") {
            BrowserDir(0, "Materials", "/materials", BrowserCategory.MATERIALS)
        } as BrowserDir
        expandedDirTree += materialDir
        traversedPaths += "/materials"

        materialDir.children.clear()
        EditorState.projectModel.materials.use().forEach {
            val materialItem = browserItems.getOrPut(it.path) { BrowserMaterialItem(1, it) }
            materialDir.children += materialItem
            traversedPaths += materialItem.path
        }
    }

    private fun refreshScripts(traversedPaths: MutableSet<String>) {
        val scriptDir = browserItems.getOrPut("/scripts") {
            BrowserDir(0, "Scripts", "/scripts", BrowserCategory.SCRIPTS)
        } as BrowserDir
        expandedDirTree += scriptDir
        traversedPaths += "/scripts"

        scriptDir.children.clear()
        EditorState.loadedApp.value?.scriptClasses?.values?.forEach {
            val scriptItem = browserItems.getOrPut(it.path) { BrowserScriptItem(1, it) }
            scriptDir.children += scriptItem
            traversedPaths += scriptItem.path
        }
    }

    private fun UiScope.traverseAssetItem(level: Int, assetItem: AssetItem, traversedPaths: MutableSet<String>, addToDirTree: Boolean): BrowserItem {
        traversedPaths += assetItem.path
        var item = browserItems[assetItem.path]
        if (item == null || !assetItem.type.matchesBrowserItemType(item)) {
            item = if (assetItem.type == AppAssetType.Directory) {
                val name = if (level == 0) "Assets" else assetItem.name
                BrowserDir(level, name, assetItem.path, BrowserCategory.ASSETS)
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
                item.children += traverseAssetItem(level + 1, it, traversedPaths, addToDirTree && item.isExpanded.use())
            }
        }
        return item
    }

    private fun AppAssetType.matchesBrowserItemType(browserItem: BrowserItem?): Boolean {
        return this == AppAssetType.Directory && browserItem is BrowserDir
                || browserItem is BrowserAssetItem
    }

    private fun UiScope.directoryTree() = Box(width = sizes.baseSize * 6, height = Grow.Std) {
        if (selectedDirectory.value == null) {
            selectedDirectory.set(browserItems.values.firstOrNull { it is BrowserDir && it.level == 0 } as BrowserDir?)
        }

        val dirPopupMenu = remember { ContextPopupMenu<BrowserItem>() }
        dirPopupMenu()

        LazyList(
            containerModifier = { it.backgroundColor(colors.background) }
        ) {
            var hoveredIndex by remember(-1)
            itemsIndexed(expandedDirTree) { i, dir ->
                if (dir.category == BrowserCategory.SPACER) {
                    Box(height = sizes.gap) { }

                } else {
                    Row(width = Grow.Std, height = sizes.lineHeight) {
                        modifier
                            .onEnter { hoveredIndex = i }
                            .onExit { hoveredIndex = -1 }
                            .onClick { evt ->
                                if (evt.pointer.isLeftButtonClicked) {
                                    selectedDirectory.set(dir)
                                    if (evt.pointer.leftButtonRepeatedClickCount == 2 && dir.level > 0) {
                                        dir.isExpanded.set(!dir.isExpanded.value)
                                    }
                                } else if (evt.pointer.isRightButtonClicked) {
                                    makeAssetDirPopupMenu(dir, dir.level > 0)?.let { dirPopupMenu.show(evt.screenPosition, it, dir) }
                                }
                            }
                            .margin(horizontal = sizes.smallGap)

                        if (i == 0) {
                            modifier.margin(top = sizes.smallGap)
                        }

                        if (hoveredIndex == i) {
                            modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                        }

                        if (dir.level > 0) {
                            // tree-depth based indentation
                            Box(width = sizes.gap * dir.level) { }

                            // expand / collapse arrow
                            Box {
                                modifier
                                    .size(sizes.lineHeight, FitContent)
                                    .alignY(AlignmentY.Center)
                                if (dir.isExpandable.use()) {
                                    Arrow {
                                        modifier
                                            .rotation(if (dir.isExpanded.use()) ArrowScope.ROTATION_DOWN else ArrowScope.ROTATION_RIGHT)
                                            .align(AlignmentX.Center, AlignmentY.Center)
                                            .onClick { dir.isExpanded.set(!dir.isExpanded.value) }
                                            .onEnter { hoveredIndex = i }
                                            .onExit { hoveredIndex = -1 }
                                    }
                                }
                            }
                        }

                        Text(dir.name) {
                            modifier
                                .alignY(AlignmentY.Center)
                                .width(Grow.Std)
                            if (dir == selectedDirectory.value) {
                                modifier.textColor(colors.primary)
                            }
                            if (dir.level == 0) {
                                modifier
                                    .margin(start = sizes.gap)
                                    .font(sizes.boldText)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun makeAssetDirPopupMenu(
        dir: BrowserDir,
        withRenameItem: Boolean,
        withDeleteItem: Boolean = withRenameItem
    ): SubMenuItem<BrowserItem>? {
        return when (dir.category) {
            BrowserCategory.ASSETS -> SubMenuItem {
                createDirectoryItem()
                if (withRenameItem) {
                    renameDirectoryItem()
                }
                if (withDeleteItem) {
                    deleteDirectoryItem()
                }
                divider()
                importAssetsMenu()
            }
            BrowserCategory.MATERIALS -> SubMenuItem {
                item("New material") { }
            }
            else -> null
        }
    }

    private fun UiScope.directoryContent() = Box(width = Grow.Std, height = Grow.Std) {
        var areaWidth by remember(0f)
        val gridSize = sizes.baseSize * 3f

        val dir = selectedDirectory.use()
        val dirItems = dir?.children ?: emptyList()

        val popupMenu = remember { ContextPopupMenu<BrowserItem>() }
        popupMenu()

        if (dir?.category == BrowserCategory.ASSETS) {
            modifier.onClick { evt ->
                if (evt.pointer.isRightButtonClicked) {
                    makeAssetDirPopupMenu(dir, false)?.let { popupMenu.show(evt.screenPosition, it, dir) }
                }
            }
        }

        ScrollArea(containerModifier = {
            it.onPositioned { nd ->
                areaWidth = nd.widthPx - sizes.largeGap.px
            }
        }) {
            modifier.margin(sizes.gap)
            if (areaWidth > 0f) {
                val cols = max(1, floor(areaWidth / gridSize.px).toInt())

                Column {
                    for (i in dirItems.indices step cols) {
                        Row {
                            for (j in i until min(i + cols, dirItems.size)) {
                                browserItem(dirItems[j], gridSize, popupMenu)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun UiScope.browserItem(item: BrowserItem, gridSize: Dp, itemPopupMenu: ContextPopupMenu<BrowserItem>) {
        Column(width = gridSize) {
            val color = when (item) {
                is BrowserDir -> MdColor.AMBER
                is BrowserAssetItem -> item.itemColor
                is BrowserMaterialItem -> MdColor.GREY
                is BrowserScriptItem -> MdColor.PURPLE
            }
            var isHovered by remember(false)
            if (isHovered) {
                modifier.background(RoundRectBackground(color.withAlpha(0.25f), sizes.smallGap))
            }

            modifier
                .onClick { evt ->
                    if (evt.pointer.isLeftButtonClicked) {
                        if (item is BrowserDir && evt.pointer.leftButtonRepeatedClickCount == 2) {
                            selectedDirectory.value?.isExpanded?.set(true)
                            selectedDirectory.set(item)
                        }
                    } else if (evt.pointer.isRightButtonClicked) {
                        makeAssetItemPopupMenu(item)?.let { itemPopupMenu.show(evt.screenPosition, it, item) }
                    }
                }

            Box {
                modifier
                    .size(sizes.baseSize * 2, sizes.baseSize * 2)
                    .alignX(AlignmentX.Center)
                    .margin(sizes.smallGap)
                    .background(RoundRectBackground(color, sizes.gap))
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }
            }

            Text(item.name) {
                modifier
                    .width(Grow.Std)
                    .isWrapText(true)
                    .textAlignX(AlignmentX.Center)
                    .margin(sizes.smallGap)
            }
        }
    }

    private fun makeAssetItemPopupMenu(item: BrowserItem): SubMenuItem<BrowserItem>? {
        return when (item) {
            is BrowserAssetItem -> SubMenuItem {
                renameAssetItem()
                deleteAssetItem()
            }
            is BrowserMaterialItem -> SubMenuItem {
                item("Delete material") {
                    OkCancelTextDialog("Delete Material", "Delete material \"${item.name}\"?") {
                        EditorActions.applyAction(DeleteMaterialAction(item.material))
                    }
                }
            }
            is BrowserDir -> SubMenuItem {
                renameDirectoryItem()
                deleteDirectoryItem()
            }
            else -> null
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
        OkCancelEnterTextDialog("Rename Directory", item.path, hint = "New directory name") {
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
        OkCancelEnterTextDialog("Rename Asset File", item.path, hint = "New asset file name") {
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
        private val MaterialData.path: String
            get() = "/materials/$name"

        private val AppScript.path: String
            get() = "/scripts/$qualifiedName"

        private val filterListTextures = listOf(
            FileFilterItem("Images", ".jpg, .png, .hdr")
        )

        private val filterListModels = listOf(
            FileFilterItem("glTF Models", ".glb, .glb.gz, .gltf, .gltf.gz")
        )
    }

    enum class BrowserCategory {
        ASSETS,
        MATERIALS,
        SCRIPTS,
        SPACER
    }

    sealed class BrowserItem(val level: Int, val name: String, val path: String, val category: BrowserCategory)

    class BrowserDir(level: Int, name: String, path: String, category: BrowserCategory) : BrowserItem(level, name, path, category) {
        val isExpanded = mutableStateOf(level == 0)
        val isExpandable = mutableStateOf(false)
        val children = mutableListOf<BrowserItem>()
    }

    class BrowserAssetItem(level: Int, asset: AssetItem) : BrowserItem(level, asset.name, asset.path, BrowserCategory.ASSETS) {
        val itemColor: Color = when (asset.type) {
            AppAssetType.Unknown -> MdColor.PINK
            AppAssetType.Directory -> MdColor.AMBER
            AppAssetType.Texture -> MdColor.LIGHT_GREEN
            AppAssetType.Model -> MdColor.LIGHT_BLUE
        }
    }

    class BrowserMaterialItem(level: Int, val material: MaterialData) : BrowserItem(level, material.name, material.path, BrowserCategory.MATERIALS)

    class BrowserScriptItem(level: Int, script: AppScript) : BrowserItem(level, script.prettyName, script.path, BrowserCategory.SCRIPTS)
}