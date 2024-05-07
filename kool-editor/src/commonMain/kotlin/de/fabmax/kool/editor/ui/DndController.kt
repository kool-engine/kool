package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene

class DndController(uiScene: Scene) {

    val dndContext = DragAndDropContext<EditorDndItem<*>>()

    private val surfaceHandlers = mutableMapOf<UiSurface, MutableSet<DragAndDropHandler<EditorDndItem<*>>>>()
    private val dndCancelHandler = object : DragAndDropHandler<EditorDndItem<*>> {
        val cancelListener = EditorKeyListener.cancelListener("Drag & drop") { dndContext.cancelDrag() }
        override val dropTarget: UiNode? = null

        override fun receive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) = false

        override fun onDragStart(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) = cancelListener.push()

        override fun onDragEnd(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?,
            target: DragAndDropHandler<EditorDndItem<*>>?,
            success: Boolean
        ) = cancelListener.pop()
    }

    init {
        uiScene.onRenderScene += {
            dndContext.clearHandlers()
            dndContext.registerHandler(dndCancelHandler)
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

    data object DndAssetItem : DndItemFlavor<AssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (AssetItem) -> Any> = mapOf(this to { it })

        override fun getTyped(item: Any): AssetItem = item as AssetItem
    }

    data object DndItemModel : DndItemFlavor<AssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (AssetItem) -> Any> = mapOf(
            this to { it },
            DndAssetItem to { it }
        )

        override fun getTyped(item: Any): AssetItem = item as AssetItem
    }

    data object DndItemTexture : DndItemFlavor<AssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (AssetItem) -> Any> = mapOf(
            this to { it },
            DndAssetItem to { it }
        )

        override fun getTyped(item: Any): AssetItem = item as AssetItem
    }

    data object DndBrowserItem : DndItemFlavor<BrowserPanel.BrowserItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (BrowserPanel.BrowserItem) -> Any> = mapOf(this to { it })

        override fun getTyped(item: Any): BrowserPanel.BrowserItem = item as BrowserPanel.BrowserItem
    }

    data object DndBrowserItemAsset : DndItemFlavor<BrowserPanel.BrowserAssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (BrowserPanel.BrowserAssetItem) -> Any> = mapOf(
            this to { it },
            DndAssetItem to { it.asset }
        )

        override fun getTyped(item: Any): BrowserPanel.BrowserAssetItem = item as BrowserPanel.BrowserAssetItem
    }

    data object DndBrowserItemTexture : DndItemFlavor<BrowserPanel.BrowserAssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (BrowserPanel.BrowserAssetItem) -> Any> = mapOf(
            this to { it },
            DndAssetItem to { it.asset },
            DndItemTexture to { it.asset }
        )

        override fun getTyped(item: Any): BrowserPanel.BrowserAssetItem = item as BrowserPanel.BrowserAssetItem
    }

    data object DndBrowserItemHdri : DndItemFlavor<BrowserPanel.BrowserAssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (BrowserPanel.BrowserAssetItem) -> Any> = mapOf(
            this to { it },
            DndAssetItem to { it.asset },
            DndItemTexture to { it.asset }
        )

        override fun getTyped(item: Any): BrowserPanel.BrowserAssetItem = item as BrowserPanel.BrowserAssetItem
    }

    data object DndBrowserItemModel : DndItemFlavor<BrowserPanel.BrowserAssetItem>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (BrowserPanel.BrowserAssetItem) -> Any> = mapOf(
            this to { it },
            DndAssetItem to { it.asset },
            DndItemModel to { it.asset }
        )

        override fun getTyped(item: Any): BrowserPanel.BrowserAssetItem = item as BrowserPanel.BrowserAssetItem
    }

    data object DndNodeModel : DndItemFlavor<NodeModel>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (NodeModel) -> Any> = mapOf(this to { it })

        override fun getTyped(item: Any): NodeModel = item as NodeModel
    }

    data object DndSceneNodeModel : DndItemFlavor<SceneNodeModel>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (SceneNodeModel) -> Any> = mapOf(
            this to { it },
            DndNodeModel to { it },
            DndSceneNodeModels to { listOf(it) }
        )

        override fun getTyped(item: Any): SceneNodeModel = item as SceneNodeModel
    }

    data object DndSceneNodeModels : DndItemFlavor<List<SceneNodeModel>>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (List<SceneNodeModel>) -> Any> = mapOf(this to { it })

        @Suppress("UNCHECKED_CAST")
        override fun getTyped(item: Any): List<SceneNodeModel> = item as List<SceneNodeModel>
    }

    data object DndSceneModel : DndItemFlavor<SceneModel>() {
        override val flavorMappings: Map<DndItemFlavor<*>, (SceneModel) -> Any> = mapOf(
            this to { it },
            DndNodeModel to { it }
        )

        override fun getTyped(item: Any): SceneModel = item as SceneModel
    }

}
