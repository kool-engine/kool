package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AppAsset
import de.fabmax.kool.editor.AppAssetType
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

    private var assets: List<AppAsset> = emptyList()
    private val selectedDirectory = mutableStateOf<AppAsset?>(null)

    override val windowSurface = WindowSurface(windowDockable) {
        Column(Grow.Std, Grow.Std) {
            TitleBar(windowDockable)
            Row(Grow.Std, Grow.Std) {
                assets = editor.appAssets.assets.use()

                directoryTree()
                directoryContent()
            }
        }
    }

    private fun UiScope.directoryTree() = Box(width = 350.dp, height = Grow.Std) {
        modifier
            .margin(sizes.gap)

        if (selectedDirectory.value == null) {
            selectedDirectory.set(assets.find { it.type == AppAssetType.Directory })
        }

        LazyList {
            itemsIndexed(editor.appAssets.assets.use().filter { it.type == AppAssetType.Directory }) { i, asset ->
                var hoveredIndex by remember(-1)
                Text(asset.name) {
                    modifier
                        .width(Grow.Std)
                        .margin(sizes.smallGap)
                        .padding(sizes.smallGap)
                        .onEnter { hoveredIndex = i }
                        .onExit { hoveredIndex = -1 }
                        .onClick { selectedDirectory.set(asset) }

                    if (hoveredIndex == i) {
                        modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                    } else if (asset == selectedDirectory.value) {
                        modifier.background(RoundRectBackground(colors.selectionBg, sizes.smallGap))
                    }
                }
            }
        }
    }

    private fun UiScope.directoryContent() = Box(width = Grow.Std, height = Grow.Std) {
        modifier.margin(sizes.gap)

        var areaWidth by remember(0f)
        val itemW = sizes.largeGap * 4f
        val itemH = sizes.largeGap * 4f

        val dirAssets = selectedDirectory.use()?.let { dir ->
            assets.filter { it.type != AppAssetType.Directory && it.path.startsWith(dir.path) }
        } ?: emptyList()

        ScrollArea(containerModifier = {
            it
                .onPositioned { nd ->
                    areaWidth = nd.widthPx
                }
        }) {
            if (areaWidth > 0f) {
                val cols = max(1, floor((areaWidth - sizes.gap.px) / (itemW.px + sizes.gap.px)).toInt())

                Column {
                    for (i in dirAssets.indices step cols) {
                        Row {
                            for (j in i until min(i + cols, dirAssets.size)) {
                                AssetItem(dirAssets[j], itemW, itemH)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun UiScope.AssetItem(item: AppAsset, itemW: Dp, itemH: Dp) {
        Column(width = itemW) {
            val color = when (item.type) {
                AppAssetType.Unknown -> MdColor.PINK
                AppAssetType.Directory -> MdColor.PURPLE
                AppAssetType.Texture -> MdColor.LIGHT_GREEN
                AppAssetType.Model -> MdColor.LIGHT_BLUE
            }
            var isHovered by remember(false)
            if (isHovered) {
                modifier.background(RoundRectBackground(color.withAlpha(0.25f), sizes.smallGap))
            }

            modifier.margin(sizes.gap)

            Box {
                modifier
                    .size(itemW - sizes.smallGap * 2, itemH - sizes.smallGap * 2)
                    .alignX(AlignmentX.Center)
                    .margin(sizes.smallGap)
                    .backgroundColor(color)
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }
            }

            Text(item.name) {
                modifier
                    .margin(sizes.smallGap)
            }
        }
    }

}