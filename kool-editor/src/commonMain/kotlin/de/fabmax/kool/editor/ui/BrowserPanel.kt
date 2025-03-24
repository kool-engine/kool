package de.fabmax.kool.editor.ui

import de.fabmax.kool.KeyValueStore
import de.fabmax.kool.editor.AppAssetType
import de.fabmax.kool.editor.AppBehavior
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.ConstColorAttribute
import de.fabmax.kool.editor.data.PbrShaderData
import de.fabmax.kool.input.CursorShape
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

abstract class BrowserPanel(name: String, icon: IconProvider, ui: EditorUi) :
    EditorPanel(name, icon, ui, defaultWidth = Dp(600f), defaultHeight = Dp(300f)
) {

    protected val browserItems = mutableMapOf<String, BrowserItem>()
    protected val expandedDirTree = mutableListOf<BrowserDir>()
    val selectedDirectory = mutableStateOf<BrowserDir?>(null)

    private val treePanelSize = mutableStateOf(Dp(280f))

    init {
        val treeW = KeyValueStore.getFloat("editor.ui.[$name].treeSize", treePanelSize.value.value)
        treePanelSize.set(Dp(treeW))
    }

    override val windowSurface = editorPanelWithPanelBar {
        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, icon) { titleBar() }
            Row(Grow.Std, Grow.Std) {
                refreshBrowserItems()

                treeView()
                treeWidthHandle()
                directoryContentView()
            }
        }
    }

    protected open fun UiScope.titleBar() { }

    private fun UiScope.treeWidthHandle() = Row(height = Grow.Std) {
        var startDragWidth by remember(treePanelSize.value)
        modifier
            .onHover {  PointerInput.cursorShape = CursorShape.RESIZE_EW }
            .onDragStart { startDragWidth = treePanelSize.value }
            .onDrag { treePanelSize.set(startDragWidth + Dp.fromPx(it.pointer.dragMovement.x)) }
            .onDragEnd { KeyValueStore.setFloat("editor.ui.[$name].treeSize", treePanelSize.value.value) }

        Box(width = sizes.smallGap, height = Grow.Std) {
            modifier.backgroundColor(colors.backgroundVariant)
        }
    }

    private fun UiScope.refreshBrowserItems() {
        val travPaths = mutableSetOf<String>()

        expandedDirTree.clear()
        collectBrowserDirs(travPaths)
        browserItems.keys.retainAll(travPaths)

        selectedDirectory.value?.path?.let { selectedDir ->
            if (selectedDir !in browserItems.keys) {
                selectDefaultDir()
            }
        }
    }

    private fun selectDefaultDir() {
        val defaultDir = browserItems.values
            .filterIsInstance<BrowserDir>()
            .firstOrNull { it.level == 0 }
        selectedDirectory.set(defaultDir)
    }

    protected abstract fun UiScope.collectBrowserDirs(traversedPaths: MutableSet<String>)

    protected open fun makeDirPopupMenu(item: BrowserDir, isInTree: Boolean): SubMenuItem<BrowserItem>? {
        return null
    }

    protected open fun onItemClick(item: BrowserItem, ev: PointerEvent): SubMenuItem<BrowserItem>? {
        val dir = item as? BrowserDir
        return when {
            dir == null -> null
            ev.isRightClick -> makeDirPopupMenu(item, false)
            ev.isLeftDoubleClick -> {
                selectedDirectory.value?.isExpanded?.set(true)
                selectedDirectory.set(item)
                null
            }
            else -> null
        }
    }

    fun UiScope.treeView() = Box(width = treePanelSize.use(), height = Grow.Std) {
        if (selectedDirectory.value == null) {
            selectDefaultDir()
        }

        val dirPopupMenu = remember { ContextPopupMenu<BrowserItem>("dir-popup") }
        dirPopupMenu()

        LazyColumn(
            containerModifier = { it.backgroundColor(colors.background) },
            vScrollbarModifier = defaultScrollbarModifierV()
        ) {
            var hoveredIndex by remember(-1)
            itemsIndexed(expandedDirTree) { i, dir ->
                Row(width = Grow.Std, height = sizes.lineHeight) {
                    modifier
                        .onEnter { hoveredIndex = i }
                        .onExit { hoveredIndex = -1 }
                        .onClick { ev ->
                            if (ev.pointer.isLeftButtonClicked) {
                                selectedDirectory.set(dir)
                                if (ev.pointer.leftButtonRepeatedClickCount == 2 && dir.level > 0) {
                                    dir.isExpanded.set(!dir.isExpanded.value)
                                }
                            } else if (ev.pointer.isRightButtonClicked) {
                                makeDirPopupMenu(dir, true)?.let { dirPopupMenu.show(ev.screenPosition, it, dir) }
                            }
                        }
                        .margin(horizontal = sizes.smallGap)

                    if (i == 0) modifier.margin(top = sizes.smallGap)
                    if (hoveredIndex == i) modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                    val fgColor = if (dir == selectedDirectory.value) colors.primary else colors.onBackground

                    // tree-depth based indentation
                    Box(width = sizes.treeIndentation * dir.level) { }

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

                    // directory icon
                    Image {
                        val ico = if (dir.isExpanded.use()) Icons.small.folderOpen else Icons.small.folder
                        modifier
                            .alignY(AlignmentY.Center)
                            .margin(end = sizes.smallGap)
                            .iconImage(ico, fgColor)
                    }

                    // directory name
                    Text(dir.name) {
                        modifier
                            .alignY(AlignmentY.Center)
                            .width(Grow.Std)
                            .textColor(fgColor)
                    }
                }
            }
        }
    }

    private fun UiScope.directoryContentView() = Box(width = Grow.Std, height = Grow.Std) {
        var areaWidth by remember(0f)
        val gridSize = sizes.baseSize * 3f

        val dir = selectedDirectory.use()
        val dirItems = dir?.children ?: emptyList()

        val popupMenu = remember { ContextPopupMenu<BrowserItem>("dir-content-popup") }
        popupMenu()

        if (dir != null) {
            modifier.onClick { evt ->
                if (evt.pointer.isRightButtonClicked) {
                    makeDirPopupMenu(dir, false)?.let { popupMenu.show(evt.screenPosition, it, dir) }
                }
            }
        }

        ScrollArea(
            containerModifier = {
                it.onPositioned { nd -> areaWidth = nd.widthPx - sizes.largeGap.px }
            },
            vScrollbarModifier = defaultScrollbarModifierV()
        ) {
            modifier.margin(sizes.gap)
            if (areaWidth > 0f) {
                val cols = max(1, floor(areaWidth / gridSize.px).toInt())

                Column {
                    modifier.margin(bottom = sizes.largeGap)
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

    private fun UiScope.browserItem(
        item: BrowserItem,
        gridSize: Dp,
        itemPopupMenu: ContextPopupMenu<BrowserItem>
    ) = Column(width = gridSize) {
        modifier.installDragAndDropHandler(dndCtx, null) { item.makeDndItem(this) }

        var isHovered by remember(false)
        if (isHovered) {
            modifier.background(RoundRectBackground(colors.onBackgroundAlpha(0.15f), sizes.smallGap))
        }

        modifier
            .onEnter { isHovered = true }
            .onExit { isHovered = false }
            .onClick { ev ->
                onItemClick(item, ev)?.let { itemPopupMenu.show(ev.screenPosition, it, item) }
            }

        item.composable()

        Text(item.name) {
            modifier
                .width(Grow.Std)
                .isWrapText(true)
                .textAlignX(AlignmentX.Center)
                .padding(sizes.smallGap)
        }
    }

    sealed class BrowserItem(val level: Int, val name: String, val path: String) {
        abstract val color: Color
        var composable: BrowserItemComposable = SimpleBrowserItemComposable()

        fun makeDndItem(uiScope: UiScope): EditorDndItem<*>? {
            return when (this) {
                is BrowserDir -> DndItemFlavor.DndBrowserItem.itemOf(this, composable.getDndComposable(uiScope))
                is BrowserAssetItem -> {
                    when (this.asset.type) {
                        AppAssetType.Unknown -> DndItemFlavor.DndBrowserItem.itemOf(this, composable.getDndComposable(uiScope))
                        AppAssetType.Directory -> DndItemFlavor.DndBrowserItem.itemOf(this, composable.getDndComposable(uiScope))
                        AppAssetType.Texture -> DndItemFlavor.DndBrowserItemTexture.itemOf(this, composable.getDndComposable(uiScope))
                        AppAssetType.Hdri -> DndItemFlavor.DndBrowserItemHdri.itemOf(this, composable.getDndComposable(uiScope))
                        AppAssetType.Model -> DndItemFlavor.DndBrowserItemModel.itemOf(this, composable.getDndComposable(uiScope))
                        AppAssetType.Heightmap -> DndItemFlavor.DndBrowserItemHeightmap.itemOf(this, composable.getDndComposable(uiScope))
                    }
                }
                is BrowserMaterialItem -> DndItemFlavor.DndBrowserItemMaterial.itemOf(this, composable.getDndComposable(uiScope))
                is BrowserBehaviorItem -> null
            }
        }

        private inner class SimpleBrowserItemComposable: BrowserItemComposable {
            override fun getComposable(sizeDp: Vec2f?, alpha: Float) = Composable {
                val width = sizeDp?.x?.dp ?: sizes.browserItemSize
                val height = sizeDp?.y?.dp ?: sizes.browserItemSize

                val icon = when (val item = this@BrowserItem) {
                    is BrowserDir -> Icons.files.folderSolid
                    is BrowserAssetItem -> {
                        when (item.asset.type) {
                            AppAssetType.Model -> Icons.files.file3d
                            AppAssetType.Heightmap -> Icons.files.file3d
                            AppAssetType.Directory -> Icons.files.folderSolid
                            else -> Icons.files.file
                        }
                    }
                    is BrowserBehaviorItem -> Icons.files.fileCode
                    else -> Icons.files.file
                }

                Box(width, height) {
                    modifier
                        .alignX(AlignmentX.Center)
                        .margin(sizes.smallGap)
                    Image {
                        modifier
                            .align(AlignmentX.Center, AlignmentY.Center)
                            .iconImage(icon, color.withAlpha(alpha))
                    }
                }
            }
        }
    }

    class BrowserDir(level: Int, name: String, path: String) : BrowserItem(level, name, path) {
        override val color: Color = MdColor.AMBER

        val isExpanded = mutableStateOf(level == 0)
        val isExpandable = mutableStateOf(false)
        val children = mutableListOf<BrowserItem>()
    }

    class BrowserAssetItem(level: Int, val asset: AssetItem) : BrowserItem(level, asset.name, asset.path) {
        override val color: Color = when (asset.type) {
            AppAssetType.Unknown -> MdColor.PINK
            AppAssetType.Directory -> MdColor.AMBER
            AppAssetType.Texture -> MdColor.GREY
            AppAssetType.Hdri -> MdColor.GREY
            AppAssetType.Model -> MdColor.LIGHT_BLUE
            AppAssetType.Heightmap -> MdColor.LIGHT_GREEN
        }
    }

    class BrowserMaterialItem(level: Int, val material: MaterialComponent) : BrowserItem(level, material.name, "/materials/${material.name}") {
        override val color: Color get() {
            val constColor = (material.data.shaderData as? PbrShaderData)?.baseColor as? ConstColorAttribute
            return constColor?.color?.toColorSrgb() ?: MdColor.GREY
        }
    }

    class BrowserBehaviorItem(level: Int, val behavior: AppBehavior) : BrowserItem(level, behavior.prettyName, "/paths/${behavior.qualifiedName}") {
        override val color: Color = MdColor.PURPLE
    }

}

interface BrowserItemComposable : Composable {
    fun getComposable(sizeDp: Vec2f? = null, alpha: Float = 1f): Composable

    fun getDndComposable(uiScope: UiScope) = getComposable(Vec2f(uiScope.sizes.baseSize.px * 1.5f), alpha = 0.7f)

    override fun UiScope.compose() {
        getComposable().invoke()
    }
}
