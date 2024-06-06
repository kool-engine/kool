package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AppReloadListener
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.EditorDefaults
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.AddSceneNodeAction
import de.fabmax.kool.editor.actions.ChangeEntityHierarchyAction
import de.fabmax.kool.editor.actions.DeleteSceneNodesAction
import de.fabmax.kool.editor.actions.SetVisibilityAction
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.scene
import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE
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

    private fun addNewMesh(parent: SceneObjectItem, meshShape: ShapeData) {
        val id = editor.projectModel.nextId()
        val name = editor.projectModel.uniquifyName(meshShape.name)
        val entityData = GameEntityData(id, name, parent.gameEntity.id)
        entityData.components += ComponentInfo(MeshComponentData(meshShape))
        entityData.components += ComponentInfo(MaterialComponentData(EntityId(-1)))
        AddSceneNodeAction(listOf(entityData)).apply()
    }

    private fun addNewModel(parent: SceneObjectItem, modelAsset: AssetItem) {
        val id = editor.projectModel.nextId()
        val name = editor.projectModel.uniquifyName(modelAsset.name)
        val nodeData = GameEntityData(id, name, parent.gameEntity.id)
        nodeData.components += ComponentInfo(ModelComponentData(modelAsset.path))
        AddSceneNodeAction(listOf(nodeData)).apply()
    }

    private fun addNewLight(parent: SceneObjectItem, lightType: LightTypeData) {
        val id = editor.projectModel.nextId()
        val name = editor.projectModel.uniquifyName(lightType.name)
        val nodeData = GameEntityData(id, name, parent.gameEntity.id)
        nodeData.components += ComponentInfo(DiscreteLightComponentData(lightType))

        val transform = MutableMat4d().translate(EditorDefaults.DEFAULT_LIGHT_POSITION)
        if (lightType !is LightTypeData.Point) {
            transform.mul(MutableMat4d().rotate(EditorDefaults.DEFAULT_LIGHT_ROTATION))
        }
        nodeData.components += ComponentInfo(TransformComponentData(TransformData.fromMatrix(transform)))

        AddSceneNodeAction(listOf(nodeData)).apply()
    }

    private fun addEmptyNode(parent: SceneObjectItem) {
        val id = editor.projectModel.nextId()
        val name = editor.projectModel.uniquifyName("Empty")
        val nodeData = GameEntityData(id, name, parent.gameEntity.id)
        AddSceneNodeAction(listOf(nodeData)).apply()
    }

    private fun deleteNode(node: SceneObjectItem) {
        val removeNode = node.gameEntity
        DeleteSceneNodesAction(listOf(removeNode)).apply()
    }

    private fun focusNode(node: SceneObjectItem) {
        sceneBrowser.editor.editorCameraTransform.focusObject(node.gameEntity)
    }

    override fun UiScope.compose() {
        editor.activeScene.use()

        if (!isTreeValid.use()) {
            treeItems.clear()
            editor.projectModel.createdScenes.values.forEach { sceneModel ->
                treeItems.appendNode(sceneModel, sceneModel.scene, sceneModel.sceneEntity, 0)
            }
            isTreeValid.set(true)
        }

        LazyList(
            containerModifier = { it.backgroundColor(null) },
            vScrollbarModifier = { it.width(sizes.scrollbarWidth) }
        ) {
            var hoveredIndex by remember(-1)
            val itemPopupMenu = remember { ContextPopupMenu<SceneObjectItem>("scene-item-popup") }

            itemsIndexed(treeItems) { i, item ->
                if (item.type != SceneObjectType.NON_MODEL_NODE && item.node != item.gameEntity.drawNode) {
                    refreshSceneTree()
                }
                sceneObjectItem(item, hoveredIndex == i).apply {
                    modifier
                        .onEnter { hoveredIndex = i }
                        .onExit { hoveredIndex = -1 }
                        .onClick {
                            if (it.pointer.isRightButtonClicked) {
                                itemPopupMenu.show(it.screenPosition, makeMenu(item), item)
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
        if (item.type != SceneObjectType.SCENE) {
            item("Delete object") { deleteNode(it) }
        }
        subMenu("Add child object") {
            subMenu("Mesh") {
                item("Box") { addNewMesh(it, ShapeData.defaultBox) }
                item("Rect") { addNewMesh(it, ShapeData.defaultRect) }
                item("Sphere") { addNewMesh(it, ShapeData.defaultSphere) }
                item("Cylinder") { addNewMesh(it, ShapeData.defaultCylinder) }
                item("Capsule") { addNewMesh(it, ShapeData.defaultCapsule) }
                item("Heightmap") { addNewMesh(it, ShapeData.defaultHeightmap) }
                item("Custom") { addNewMesh(it, ShapeData.defaultCustom) }
            }
            subMenu("glTF model") {
                item("Import model") { logE { "Not yet implemented" } }
                divider()
                sceneBrowser.editor.availableAssets.modelAssets.forEach { modelAsset ->
                    item(modelAsset.name) { addNewModel(it, modelAsset) }
                }
            }
            editor.activeScene.value?.let { sceneModel ->
                if (sceneModel.scene.lighting.lights.size < sceneModel.sceneComponent.maxNumLightsState.value) {
                    subMenu("Light") {
                        item("Directional") { addNewLight(it, LightTypeData.Directional()) }
                        item("Spot") { addNewLight(it, LightTypeData.Spot()) }
                        item("Point") { addNewLight(it, LightTypeData.Point()) }
                    }
                }
            }
            item("Empty node") { addEmptyNode(it) }
        }
        if (item.type != SceneObjectType.SCENE) {
            divider()
            item("Focus object") { focusNode(it) }
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
                Arrow {
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

        val isVisible = item.gameEntity.isVisibleState.use()
        val fgColorVis = if (isVisible) fgColor else fgColor.withAlpha(0.5f)

        // type icon
        Image {
            val icon = when (item.type) {
                SceneObjectType.NON_MODEL_NODE -> IconMap.small.nodeCircle
                SceneObjectType.CAMERA -> IconMap.small.camera
                SceneObjectType.LIGHT -> IconMap.small.light
                SceneObjectType.GROUP -> IconMap.small.rectCrosshair
                SceneObjectType.MESH -> IconMap.small.cube
                SceneObjectType.MODEL -> IconMap.small.tree
                SceneObjectType.SCENE -> IconMap.small.world
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
                        .iconImage(if (isVisible) IconMap.small.eye else IconMap.small.eyeOff, eyeColor)
                        .onClick { SetVisibilityAction(item.gameEntity, !isVisible).apply() }
                }
            }
        }
    }

    private fun MutableList<SceneObjectItem>.appendNode(scene: EditorScene, node: Node, selectEntity: GameEntity, depth: Int) {
        // get entity for node, this should be equal to [selectEntity] for regular objects but can be null if node
        // does not correspond to a GameEntity item (e.g. child meshes of a gltf model)
        val entity = scene.nodesToEntities[node]

        val item = if (entity != null) {
            modelTreeItemMap.getOrPut(entity) {
                SceneObjectItem(node, entity)
            }
        } else {
            nodeTreeItemMap.getOrPut(node) {
                SceneObjectItem(node, selectEntity, SceneObjectType.NON_MODEL_NODE)
            }
        }

        // update item node, it can change when model / app is reloaded
        item.node = node
        item.depth = depth

        add(item)
        if (item.isExpanded.value) {
            node.children.forEach {
                if (!it.tags.hasTag(KoolEditor.TAG_EDITOR_SUPPORT_CONTENT)) {
                    val childNodeModel = scene.nodesToEntities[it]
                    appendNode(scene, it, childNodeModel ?: selectEntity, depth + 1)
                }
            }
        }
    }

    private inner class SceneObjectItem(
        node: Node,
        val gameEntity: GameEntity,
        val forcedType: SceneObjectType? = null
    ) {
        var depth = 0
        var node: Node = node
            set(value) {
                field = value
                type = getNodeType()
            }

        val name: String get() = node.name

        var type: SceneObjectType = getNodeType()

        val isExpandable: Boolean get() = node.children.isNotEmpty()
        val isExpanded = mutableStateOf(type.startExpanded)

        private fun getNodeType(): SceneObjectType {
            if (forcedType != null) {
                return forcedType
            }

            if (gameEntity.isSceneRoot) {
                return SceneObjectType.SCENE
            }

            return when (gameEntity.getComponent<DrawNodeComponent>()) {
                is MeshComponent -> SceneObjectType.MESH
                is ModelComponent -> SceneObjectType.MODEL
                is DiscreteLightComponent -> SceneObjectType.LIGHT
                is CameraComponent -> SceneObjectType.CAMERA
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

    private enum class SceneObjectType(val startExpanded: Boolean = false, val isHideable: Boolean = true) {
        NON_MODEL_NODE(isHideable = false),
        CAMERA,
        LIGHT,
        GROUP(true),
        MESH,
        MODEL,
        SCENE(true, isHideable = false)
    }
}