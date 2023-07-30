package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene

class DndController(uiScene: Scene) {

    val dndContext = DragAndDropContext<EditorDndItem<*>>()

    private val surfaceHandlers = mutableMapOf<UiSurface, MutableSet<DragAndDropHandler<EditorDndItem<*>>>>()

    init {
        uiScene.onRenderScene += {
            dndContext.clearHandlers()
            surfaceHandlers.values.forEach {
                dndContext.handlers += it
            }
        }
    }

    fun registerHandler(dndHandler: DragAndDropHandler<EditorDndItem<*>>, surface: UiSurface) {
        surfaceHandlers.getOrPut(surface) {
            val surfaceHandlers = mutableSetOf<DragAndDropHandler<EditorDndItem<*>>>()
            surface.onCompose { surfaceHandlers.clear() }
            surfaceHandlers
        }.add(dndHandler)
    }
}

class EditorDndItem<T: Any>(val item: T, val flavors: Map<DndItemFlavor<*>, (T) -> Any>) {
    fun <T: Any> get(flavor: DndItemFlavor<T>): T {
        val getter = flavors[flavor] ?: throw NoSuchElementException("EditorDndItem does not have requested flavor $flavor")
        return flavor.getTyped(getter(item))
    }
}

open class DndHandler(
    override val dropTarget: UiNode,
    val acceptedFlavors: Set<DndItemFlavor<*>> = emptySet()
) : DragAndDropHandler<EditorDndItem<*>> {

    val isHovered = mutableStateOf(false)
    val isDrag = mutableStateOf(false)

    override fun onDragStart(
        dragItem: EditorDndItem<*>,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<EditorDndItem<*>>?
    ) {
        if (isMatchingFlavor(dragItem)) {
            onMatchingDragStart(dragItem, dragPointer, source)
        }
    }

    override fun onDrag(
        dragItem: EditorDndItem<*>,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<EditorDndItem<*>>?,
        isHovered: Boolean
    ) {
        if (isHovered && isMatchingFlavor(dragItem)) {
            onMatchingHover(dragItem, dragPointer, source, true)
        } else {
            onMatchingHover(dragItem, dragPointer, source, false)
        }
    }

    override fun onDragEnd(
        dragItem: EditorDndItem<*>,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<EditorDndItem<*>>?,
        target: DragAndDropHandler<EditorDndItem<*>>?,
        success: Boolean
    ) {
        isHovered.set(false)
        isDrag.set(false)
    }

    override fun receive(dragItem: EditorDndItem<*>, dragPointer: PointerEvent, source: DragAndDropHandler<EditorDndItem<*>>?): Boolean {
        if (isMatchingFlavor(dragItem)) {
            onMatchingReceive(dragItem, dragPointer, source)
            return true
        }
        return false
    }

    protected fun isMatchingFlavor(dragItem: EditorDndItem<*>): Boolean {
        return acceptedFlavors.isEmpty() || dragItem.flavors.keys.any { it in acceptedFlavors }
    }

    protected open fun onMatchingReceive(
        dragItem: EditorDndItem<*>,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<EditorDndItem<*>>?
    ) { }

    protected open fun onMatchingDragStart(
        dragItem: EditorDndItem<*>,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<EditorDndItem<*>>?
    ) {
        isDrag.set(true)
    }

    protected open fun onMatchingHover(
        dragItem: EditorDndItem<*>,
        dragPointer: PointerEvent,
        source: DragAndDropHandler<EditorDndItem<*>>?,
        isHovered: Boolean
    ) {
        this.isHovered.set(isHovered)
    }
}

abstract class DndItemFlavor<T: Any> {

    abstract val flavorMappings: Map<DndItemFlavor<*>, (T) -> Any>

    abstract fun getTyped(item: Any): T

    fun itemOf(value: T): EditorDndItem<T> {
        return EditorDndItem(value, flavorMappings)
    }

    data object ASSET_ITEM : DndItemFlavor<AssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (AssetItem) -> Any> = mapOf(this to { it })

        override fun getTyped(item: Any): AssetItem = item as AssetItem
    }

    data object ASSET_ITEM_MODEL : DndItemFlavor<AssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (AssetItem) -> Any> = mapOf(
            this to { it },
            ASSET_ITEM to { it }
        )

        override fun getTyped(item: Any): AssetItem = item as AssetItem
    }

    data object ASSET_ITEM_TEXTURE : DndItemFlavor<AssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (AssetItem) -> Any> = mapOf(
            this to { it },
            ASSET_ITEM to { it }
        )

        override fun getTyped(item: Any): AssetItem = item as AssetItem
    }

    data object BROWSER_ITEM : DndItemFlavor<ResourceBrowser.BrowserItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (ResourceBrowser.BrowserItem) -> Any> = mapOf(this to { it })

        override fun getTyped(item: Any): ResourceBrowser.BrowserItem = item as ResourceBrowser.BrowserItem
    }

    data object BROWSER_ITEM_ASSET : DndItemFlavor<ResourceBrowser.BrowserAssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (ResourceBrowser.BrowserAssetItem) -> Any> = mapOf(
            this to { it },
            ASSET_ITEM to { it.asset }
        )

        override fun getTyped(item: Any): ResourceBrowser.BrowserAssetItem = item as ResourceBrowser.BrowserAssetItem
    }

    data object BROWSER_ITEM_TEXTURE : DndItemFlavor<ResourceBrowser.BrowserAssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (ResourceBrowser.BrowserAssetItem) -> Any> = mapOf(
            this to { it },
            ASSET_ITEM to { it.asset },
            ASSET_ITEM_TEXTURE to { it.asset }
        )

        override fun getTyped(item: Any): ResourceBrowser.BrowserAssetItem = item as ResourceBrowser.BrowserAssetItem
    }

    data object BROWSER_ITEM_MODEL : DndItemFlavor<ResourceBrowser.BrowserAssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (ResourceBrowser.BrowserAssetItem) -> Any> = mapOf(
            this to { it },
            ASSET_ITEM to { it.asset },
            ASSET_ITEM_MODEL to { it.asset }
        )

        override fun getTyped(item: Any): ResourceBrowser.BrowserAssetItem = item as ResourceBrowser.BrowserAssetItem
    }

    data object NODE_MODEL : DndItemFlavor<NodeModel>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (NodeModel) -> Any> = mapOf(this to { it })

        override fun getTyped(item: Any): NodeModel = item as NodeModel
    }

    data object SCENE_NODE_MODEL : DndItemFlavor<SceneNodeModel>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (SceneNodeModel) -> Any> = mapOf(
            this to { it },
            NODE_MODEL to { it }
        )

        override fun getTyped(item: Any): SceneNodeModel = item as SceneNodeModel
    }

    data object SCENE_MODEL : DndItemFlavor<SceneModel>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (SceneModel) -> Any> = mapOf(
            this to { it },
            NODE_MODEL to { it }
        )

        override fun getTyped(item: Any): SceneModel = item as SceneModel
    }

}
