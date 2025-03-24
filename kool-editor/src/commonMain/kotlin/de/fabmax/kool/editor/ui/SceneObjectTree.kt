package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AppReloadListener
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.ChangeEntityHierarchyAction
import de.fabmax.kool.editor.actions.SetVisibilityAction
import de.fabmax.kool.editor.actions.deleteNode
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.globalLookAt
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import kotlin.math.max
import kotlin.math.min

class SceneObjectTree(val sceneBrowser: SceneBrowser) : Composable {

    private val modelTreeItemMap = mutableMapOf<GameEntity, SceneObjectItem>()
    private val nodeTreeItemMap = mutableMapOf<Node, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    private var lastSelectionIndex = -1

    private val dndCtx: DragAndDropContext<EditorDndItem<*>> get() = sceneBrowser.dnd.dndContext

    private val editor: KoolEditor
        get() = sceneBrowser.editor

    init {
        editor.appLoader.appReloadListeners += AppReloadListener {
            nodeTreeItemMap.clear()
        }

        editor.selectionOverlay.onSelectionChanged += { selItems ->
            if (selItems.isEmpty()) {
                lastSelectionIndex = -1
            } else {
                val lastSelected = selItems.last()
                val idx = treeItems.indexOfFirst { it.gameEntity == lastSelected }
                if (idx >= 0) {
                    lastSelectionIndex = idx
                }
            }
        }
    }

    fun refreshSceneTree() {
        isTreeValid.set(false)
    }

    override fun UiScope.compose() {
        editor.activeScene.use()

        if (!isTreeValid.use()) {
            treeItems.clear()
            editor.projectModel.createdScenes.values.forEach { sceneModel ->
                treeItems.appendNode(sceneModel, sceneModel.sceneEntity, 0)
            }
            isTreeValid.set(true)
        }

        LazyColumn(
            containerModifier = { it.backgroundColor(null) },
            vScrollbarModifier = defaultScrollbarModifierV()
        ) {
            var hoveredIndex by remember(-1)
            val itemPopupMenu = remember { ContextPopupMenu<GameEntity?>("scene-item-popup") }

            itemsIndexed(treeItems) { i, item ->
                sceneObjectItem(item, hoveredIndex == i).apply {
                    modifier
                        .onEnter { hoveredIndex = i }
                        .onExit { hoveredIndex = -1 }
                        .onClick {
                            if (it.pointer.isRightButtonClicked) {
                                itemPopupMenu.show(it.screenPosition, makeMenu(item), item.gameEntity)
                            }
                        }

                    if (i == 0) {
                        modifier.margin(top = sizes.smallGap)
                    }
                }
            }

            itemPopupMenu()
        }
    }

    private fun makeMenu(item: SceneObjectItem) = SubMenuItem {
        menuItems += addSceneObjectMenu("Add child object", item.gameEntity, editor.globalLookAt)
        if (item.type != SceneObjectType.SCENE) {
            divider()
            item("Focus object", Icons.small.circleCrosshair) { editor.focusObject(it) }
            item("Delete object", Icons.small.trash) { deleteNode(it) }
        }
    }

    private fun UiScope.sceneObjectItem(item: SceneObjectItem, isHovered: Boolean) {
        modifier
            .margin(horizontal = sizes.smallGap)
            .onClick { evt ->
                if (evt.pointer.isLeftButtonClicked) {
                    if (evt.pointer.leftButtonRepeatedClickCount == 2 && item.isExpandable) {
                        item.toggleExpanded()
                    } else {
                        if (KeyboardInput.isShiftDown && lastSelectionIndex >= 0) {
                            // select range
                            val clickIndex = treeItems.indexOf(item)
                            if (clickIndex >= 0) {
                                val selection = treeItems.subList(min(lastSelectionIndex, clickIndex), max(lastSelectionIndex, clickIndex) + 1)
                                    .map { it.gameEntity }
                                editor.selectionOverlay.expandSelection(selection)
                            }

                        } else {
                            editor.selectionOverlay.selectSingle(item.gameEntity)
                        }
                    }
                } else if (evt.pointer.isMiddleButtonClicked && item.isExpandable) {
                    item.toggleExpanded()
                }
            }
        if (isHovered || item.gameEntity in editor.selectionOverlay.selectionState.use()) {
            modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
        }

        sceneObjectDndHandler(item)
        sceneObjectLabel(item, isHovered)
    }

    private fun UiScope.sceneObjectDndHandler(item: SceneObjectItem) {
        if (item.type != SceneObjectType.NON_MODEL_NODE) {
            // create dnd handler (handles reception of dropped dnd items)
            val dndHandler = rememberItemDndHandler(item)

            // drag-and-drop hover is not covered by regular hover callbacks, instead we have to handle
            // it separately here...
            if (dndHandler.isHovered.use()) {
                when (dndHandler.insertPos.use()) {
                    -1 -> {
                        // top border hovered, dropped item will be inserted before this node
                        Box(width = Grow.Std, height = 1.5f.dp) {
                            modifier
                                .margin(start = sizes.gap * item.depth)
                                .backgroundColor(colors.elevatedComponentBgHovered)
                        }
                    }
                    0 -> {
                        // center hovered, dropped item will be inserted as a child of this node
                        modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                    }
                    1 -> {
                        // bottom border hovered, dropped item will be inserted after this node
                        Box(width = Grow.Std, height = 1.5f.dp) {
                            modifier
                                .margin(start = sizes.gap * item.depth)
                                .alignY(AlignmentY.Bottom)
                                .backgroundColor(colors.elevatedComponentBgHovered)
                        }
                    }
                }
            }

            // install drag and drop handler (handles dragging / sending this item to somewhere else)
            if (item.gameEntity.isSceneChild) {
                modifier.installDragAndDropHandler(dndCtx, dndHandler) {
                    if (editor.selectionOverlay.isSelected(item.gameEntity)) {
                        val selectedSceneNodes = editor.selectionOverlay.selection.filter { it.isSceneChild }
                        DndItemFlavor.DndGameEntities.itemOf(selectedSceneNodes)
                    } else {
                        DndItemFlavor.DndGameEntity.itemOf(item.gameEntity)
                    }
                }
            } else if (item.gameEntity.isSceneRoot) {
                modifier.installDragAndDropHandler(dndCtx, dndHandler) {
                    DndItemFlavor.DndGameEntity.itemOf(item.gameEntity)
                }
            }
        }
    }

    private fun UiScope.sceneObjectLabel(item: SceneObjectItem, isHovered: Boolean) = Row(width = Grow.Std, height = sizes.lineHeight) {
        // tree-depth based indentation
        if (item.depth > 0) {
            Box(width = sizes.treeIndentation * item.depth) { }
        }

        // expand / collapse arrow
        Box {
            modifier
                .size(sizes.lineHeight * 0.8f, FitContent)
                .alignY(AlignmentY.Center)
            if (item.isExpandable) {
                Arrow(isHoverable = false) {
                    modifier
                        .rotation(if (item.isExpanded.use()) ROTATION_DOWN else ROTATION_RIGHT)
                        .align(AlignmentX.Center, AlignmentY.Center)
                        .onClick { item.toggleExpanded() }
                }
            }
        }

        val fgColor = if (item.gameEntity in editor.selectionOverlay.selectionState.use()) {
            if (item.type != SceneObjectType.NON_MODEL_NODE) {
                colors.primary
            } else {
                colors.primary.mix(Color.BLACK, 0.3f)
            }
        } else {
            if (item.type != SceneObjectType.NON_MODEL_NODE) {
                colors.onBackground
            } else {
                colors.onBackground.mix(Color.BLACK, 0.3f)
            }
        }

        val isVisible = item.gameEntity.isVisible
        val fgColorVis = if (isVisible) fgColor else fgColor.withAlpha(0.5f)

        // type icon
        Image {
            val icon = when (item.type) {
                SceneObjectType.NON_MODEL_NODE -> Icons.small.nodeCircle
                SceneObjectType.CAMERA -> Icons.small.camera
                SceneObjectType.LIGHT -> Icons.small.light
                SceneObjectType.GROUP -> Icons.small.emptyObject
                SceneObjectType.MESH -> Icons.small.cube
                SceneObjectType.SCENE -> Icons.small.world
                SceneObjectType.PHYSICS -> Icons.small.physics
                SceneObjectType.PHYSICS_CHARACTER -> Icons.small.character
                SceneObjectType.PHYSICS_JOINT -> Icons.small.joint
            }
            modifier
                .alignX(AlignmentX.End)
                .alignY(AlignmentY.Center)
                .margin(end = sizes.smallGap)
                .iconImage(icon, fgColorVis)
        }

        Box(width = Grow.Std, height = Grow.Std) {
            Text(item.name) {
                modifier
                    .alignY(AlignmentY.Center)
                    .textColor(fgColorVis)
            }

            if (item.type.isHideable) {
                val eyeColor = when {
                    isHovered -> fgColor
                    !isVisible -> fgColor
                    else -> fgColor.withAlpha(0.4f)
                }
                Image {
                    modifier
                        .alignX(AlignmentX.End)
                        .alignY(AlignmentY.Center)
                        .margin(end = sizes.smallGap)
                        .iconImage(if (isVisible) Icons.small.eye else Icons.small.eyeOff, eyeColor)
                        .onClick { SetVisibilityAction(item.gameEntity, !isVisible).apply() }
                }
            }
        }
    }

    private fun MutableList<SceneObjectItem>.appendNode(scene: EditorScene, selectEntity: GameEntity, depth: Int) {
        val item = modelTreeItemMap.getOrPut(selectEntity) { SceneObjectItem(selectEntity, depth) }
        modelTreeItemMap[selectEntity.parent]?.isExpandable = true

        add(item)
        if (item.isExpanded.value) {
            selectEntity.children.forEach { child ->
                appendNode(scene, child, depth + 1)
            }
        }
    }

    private inner class SceneObjectItem(
        val gameEntity: GameEntity,
        val depth: Int,
        val forcedType: SceneObjectType? = null
    ) {
        val name: String get() = gameEntity.name
        var type: SceneObjectType = getNodeType()
        var isExpandable = false
        val isExpanded = mutableStateOf(type.startExpanded)

        private fun getNodeType(): SceneObjectType {
            if (forcedType != null) {
                return forcedType
            }

            if (gameEntity.isSceneRoot) {
                return SceneObjectType.SCENE
            }

            return when {
                gameEntity.hasComponent<MeshComponent>() -> SceneObjectType.MESH
                gameEntity.hasComponent<DiscreteLightComponent>() -> SceneObjectType.LIGHT
                gameEntity.hasComponent<CameraComponent>() -> SceneObjectType.CAMERA
                gameEntity.hasComponent<CharacterControllerComponent>() -> SceneObjectType.PHYSICS_CHARACTER
                gameEntity.hasComponent<JointComponent>() -> SceneObjectType.PHYSICS_JOINT
                gameEntity.hasComponent<PhysicsComponent>() -> SceneObjectType.PHYSICS
                else -> SceneObjectType.GROUP
            }
        }

        fun toggleExpanded() {
            if (isExpandable) {
                isExpanded.set(!isExpanded.value)
                isTreeValid.set(false)
            }
        }
    }

    private fun UiScope.rememberItemDndHandler(treeItem: SceneObjectItem): TreeItemDndHandler {
        val handler = remember { TreeItemDndHandler(treeItem, uiNode) }
        handler.treeItem = treeItem
        KoolEditor.instance.ui.dndController.registerHandler(handler, surface)
        return handler
    }

    private inner class TreeItemDndHandler(var treeItem: SceneObjectItem, dropTarget: UiNode) :
        DndHandler(dropTarget, setOf(DndItemFlavor.DndGameEntities))
    {
        // insert pos: -1 if top border is hovered, 0 if center is hovered, +1 if bottom border is hovered
        val insertPos = mutableStateOf(0)

        override fun onMatchingHover(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?,
            isHovered: Boolean
        ) {
            super.onMatchingHover(dragItem, dragPointer, source, isHovered)

            val h = dropTarget.heightPx
            val hoverPtrPos = dropTarget.toLocal(dragPointer.screenPosition)

            when {
                hoverPtrPos.y < h * 0.25f -> insertPos.set(-1)
                hoverPtrPos.y > h * 0.75f -> insertPos.set(1)
                else -> insertPos.set(0)
            }
        }

        override fun onMatchingReceive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) {
            val nodeModels = dragItem.get(DndItemFlavor.DndGameEntities)
            val self = treeItem.gameEntity
            if (self !in nodeModels) {
                when {
                    insertPos.value == -1 && self.isSceneChild -> {
                        ChangeEntityHierarchyAction(nodeModels, self.parent!!.id, GameEntity.InsertionPos.Before(self.id)).apply()
                    }
                    insertPos.value == 1 && self.isSceneChild -> {
                        ChangeEntityHierarchyAction(nodeModels, self.parent!!.id, GameEntity.InsertionPos.After(self.id)).apply()
                    }
                    else -> ChangeEntityHierarchyAction(nodeModels, self.id, GameEntity.InsertionPos.End).apply()
                }
            }
        }
    }

    private enum class SceneObjectType(val startExpanded: Boolean = true, val isHideable: Boolean = true) {
        NON_MODEL_NODE(isHideable = false),
        CAMERA,
        LIGHT,
        GROUP,
        MESH,
        PHYSICS,
        PHYSICS_CHARACTER,
        PHYSICS_JOINT,
        SCENE(isHideable = false)
    }
}