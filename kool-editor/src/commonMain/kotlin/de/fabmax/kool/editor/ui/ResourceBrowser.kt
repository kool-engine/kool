package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AppAssetType
import de.fabmax.kool.editor.AppScript
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.EditorState
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
    defaultHeight = Dp(350f)
) {

    private val browserItems = mutableMapOf<String, BrowserItem>()
    private val expandedDirTree = mutableListOf<BrowserDir>()
    private val selectedDirectory = mutableStateOf<BrowserDir?>(null)

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

        val dirSpacer = browserItems.getOrPut("/!spacer") { BrowserDir(-1, "", "/!spacer") } as BrowserDir

        editor.availableAssets.rootAssets.use().forEach { traverseAssetItem(0, it, travPaths, true) }

        expandedDirTree.add(dirSpacer)
        refreshMaterials(travPaths)

        expandedDirTree.add(dirSpacer)
        refreshScripts(travPaths)

        browserItems.keys.retainAll(travPaths)
    }

    private fun refreshMaterials(traversedPaths: MutableSet<String>) {
        val materialDir = browserItems.getOrPut("/materials") {
            BrowserDir(0, "Materials", "/materials")
        } as BrowserDir
        expandedDirTree += materialDir
        traversedPaths += "/materials"

        materialDir.children.clear()
        EditorState.projectModel.materials.values.forEach {
            val materialItem = browserItems.getOrPut(it.path) { BrowserMaterialItem(1, it) }
            materialDir.children += materialItem
            traversedPaths += materialItem.path
        }
    }

    private fun refreshScripts(traversedPaths: MutableSet<String>) {
        val scriptDir = browserItems.getOrPut("/scripts") {
            BrowserDir(0, "Scripts", "/scripts")
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
                val name = if (level == 0) assetItem.name.replaceFirstChar { it.uppercase() } else assetItem.name
                BrowserDir(level, name, assetItem.path).apply {
                    isExpandable.set(assetItem.children.any { it.type == AppAssetType.Directory })
                }
            } else {
                BrowserAssetItem(level, assetItem)
            }
            browserItems[assetItem.path] = item
        }
        if (item is BrowserDir) {
            if (addToDirTree) {
                expandedDirTree.add(item)
            }
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

        LazyList(
            containerModifier = { it.backgroundColor(colors.background) }
        ) {
            var hoveredIndex by remember(-1)
            itemsIndexed(expandedDirTree) { i, dir ->
                if (dir.level < 0) {
                    // spacer
                    Box(height = sizes.gap) { }

                } else {
                    Row(width = Grow.Std, height = sizes.lineHeight) {
                        modifier
                            .onEnter { hoveredIndex = i }
                            .onExit { hoveredIndex = -1 }
                            .onClick {
                                selectedDirectory.set(dir)
                                if (it.pointer.leftButtonRepeatedClickCount == 2) {
                                    dir.isExpanded.set(!dir.isExpanded.value)
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

    private val AssetItem.isExpandable: Boolean get() {
        return type == AppAssetType.Directory && children.any { it.type == AppAssetType.Directory }
    }

    private fun UiScope.directoryContent() = Box(width = Grow.Std, height = Grow.Std) {
        var areaWidth by remember(0f)
        val gridSize = sizes.baseSize * 3f

        val dirAssets = selectedDirectory.use()?.children ?: emptyList()

        ScrollArea(containerModifier = {
            it.onPositioned { nd ->
                areaWidth = nd.widthPx - sizes.largeGap.px
            }
        }) {
            modifier.margin(sizes.gap)
            if (areaWidth > 0f) {
                val cols = max(1, floor(areaWidth / gridSize.px).toInt())

                Column {
                    for (i in dirAssets.indices step cols) {
                        Row {
                            for (j in i until min(i + cols, dirAssets.size)) {
                                browserItem(dirAssets[j], gridSize)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun UiScope.browserItem(item: BrowserItem, gridSize: Dp) {
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
                .onClick {
                    if (item is BrowserDir && it.pointer.leftButtonRepeatedClickCount == 2) {
                        selectedDirectory.value?.isExpanded?.set(true)
                        selectedDirectory.set(item)
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
                    .alignX(AlignmentX.Center)
                    .margin(sizes.smallGap)
            }
        }
    }

    companion object {
        private val MaterialData.path: String
            get() = "/materials/$name"

        private val AppScript.path: String
            get() = "/scripts/$qualifiedName"
    }

    private sealed class BrowserItem(val level: Int, val name: String, val path: String)

    private class BrowserDir(level: Int, name: String, path: String) : BrowserItem(level, name, path) {
        val isExpanded = mutableStateOf(level == 0)
        val isExpandable = mutableStateOf(false)
        val children = mutableListOf<BrowserItem>()
    }

    private class BrowserAssetItem(level: Int, asset: AssetItem) : BrowserItem(level, asset.name, asset.path) {
        val itemColor: Color = when (asset.type) {
            AppAssetType.Unknown -> MdColor.PINK
            AppAssetType.Directory -> MdColor.AMBER
            AppAssetType.Texture -> MdColor.LIGHT_GREEN
            AppAssetType.Model -> MdColor.LIGHT_BLUE
        }
    }

    private class BrowserMaterialItem(level: Int, material: MaterialData) : BrowserItem(level, material.name, material.path)

    private class BrowserScriptItem(level: Int, script: AppScript) : BrowserItem(level, script.simpleName, script.path)
}