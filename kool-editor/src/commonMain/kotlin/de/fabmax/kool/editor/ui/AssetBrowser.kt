package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AppAssetType
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class AssetBrowser(editorUi: EditorUi) : EditorPanel(
    "Asset Browser",
    editorUi,
    defaultWidth = Dp(600f),
    defaultHeight = Dp(350f)
) {

    private var rootAssets: List<AssetItem> = emptyList()
    private val selectedDirectory = mutableStateOf<AssetItem?>(null)

    override val windowSurface = EditorPanelWindow {
        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable)
            Row(Grow.Std, Grow.Std) {
                rootAssets = editor.availableAssets.rootAssets.use()

                directoryTree()
                directoryContent()
            }
        }
    }

    private fun UiScope.directoryTree() = Box(width = sizes.baseSize * 6, height = Grow.Std) {
        if (selectedDirectory.value == null) {
            selectedDirectory.set(rootAssets.find { it.type == AppAssetType.Directory })
        }

        LazyList(
            containerModifier = { it.backgroundColor(colors.background) }
        ) {
            var hoveredIndex by remember(-1)
            itemsIndexed(flattenedDirectoryTree()) { i, (lvl, asset) ->
                Row(width = Grow.Std, height = sizes.lineHeight) {
                    modifier
                        .onEnter { hoveredIndex = i }
                        .onExit { hoveredIndex = -1 }
                        .onClick {
                            selectedDirectory.set(asset)
                            if (it.pointer.leftButtonRepeatedClickCount == 2) {
                                asset.isExpanded.set(!asset.isExpanded.value)
                            }
                        }
                        .margin(horizontal = sizes.smallGap)

                    if (i == 0) {
                        modifier.margin(top = sizes.smallGap)
                    }
                    if (hoveredIndex == i) {
                        modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                    }

                    // tree-depth based indentation
                    Box(width = sizes.gap * lvl) { }

                    // expand / collapse arrow
                    Box {
                        modifier
                            .size(sizes.lineHeight, FitContent)
                            .alignY(AlignmentY.Center)
                        if (asset.isExpandable) {
                            Arrow {
                                modifier
                                    .rotation(if (asset.isExpanded.use()) ArrowScope.ROTATION_DOWN else ArrowScope.ROTATION_RIGHT)
                                    .align(AlignmentX.Center, AlignmentY.Center)
                                    .onClick { asset.isExpanded.set(!asset.isExpanded.value) }
                                    .onEnter { hoveredIndex = i }
                                    .onExit { hoveredIndex = -1 }
                            }
                        }
                    }

                    Text(asset.name) {
                        modifier
                            .alignY(AlignmentY.Center)
                            .width(Grow.Std)
                            if (asset == selectedDirectory.value) {
                                modifier.textColor(colors.primary)
                            }
                    }
                }
            }
        }
    }

    private val AssetItem.isExpandable: Boolean get() {
        return type == AppAssetType.Directory && children.any { it.type == AppAssetType.Directory }
    }

    private fun UiScope.flattenedDirectoryTree(): List<Pair<Int, AssetItem>> {
        val result = mutableListOf<Pair<Int, AssetItem>>()
        rootAssets.forEach { appendDirectories(it, 0, result) }
        return result
    }

    private fun UiScope.appendDirectories(asset: AssetItem, level: Int, result: MutableList<Pair<Int, AssetItem>>) {
        if (asset.type == AppAssetType.Directory) {
            result += level to asset
            if (asset.isExpanded.use()) {
                asset.children.forEach {
                    appendDirectories(it, level + 1, result)
                }
            }
        }
    }

    private fun UiScope.directoryContent() = Box(width = Grow.Std, height = Grow.Std) {
        var areaWidth by remember(0f)
        val gridSize = sizes.baseSize * 3f

        val dirAssets = selectedDirectory.use()?.children ?: emptyList()

        ScrollArea(containerModifier = {
            it.onPositioned { nd ->
                areaWidth = nd.widthPx
            }
        }) {
            if (areaWidth > 0f) {
                val cols = max(1, floor(areaWidth / gridSize.px).toInt())

                Column {
                    for (i in dirAssets.indices step cols) {
                        Row {
                            for (j in i until min(i + cols, dirAssets.size)) {
                                AssetItem(dirAssets[j], gridSize)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun UiScope.AssetItem(item: AssetItem, gridSize: Dp) {
        Column(width = gridSize) {
            val color = when (item.type) {
                AppAssetType.Unknown -> MdColor.PINK
                AppAssetType.Directory -> MdColor.AMBER
                AppAssetType.Texture -> MdColor.LIGHT_GREEN
                AppAssetType.Model -> MdColor.LIGHT_BLUE
            }
            var isHovered by remember(false)
            if (isHovered) {
                modifier.background(RoundRectBackground(color.withAlpha(0.25f), sizes.smallGap))
            }

            modifier
                .margin(sizes.gap)
                .onClick {
                    if (item.type == AppAssetType.Directory && it.pointer.leftButtonRepeatedClickCount == 2) {
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

}