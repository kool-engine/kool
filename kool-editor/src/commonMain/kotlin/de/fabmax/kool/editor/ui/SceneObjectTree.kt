package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AppReloadListener
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.EditorDefaults
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.AddNodeAction
import de.fabmax.kool.editor.actions.DeleteNodeAction
import de.fabmax.kool.editor.actions.MoveSceneNodeAction
import de.fabmax.kool.editor.actions.SetVisibilityAction
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE

class SceneObjectTree(val sceneBrowser: SceneBrowser) : Composable {

    private val modelTreeItemMap = mutableMapOf<NodeModel, SceneObjectItem>()
    private val nodeTreeItemMap = mutableMapOf<Node, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    private val dndCtx: DragAndDropContext<EditorDndItem<*>> get() = sceneBrowser.dnd.dndContext

    private val editor: KoolEditor
        get() = KoolEditor.instance

    init {
        sceneBrowser.editor.appLoader.appReloadListeners += AppReloadListener {
            nodeTreeItemMap.clear()
        }
    }

    fun refreshSceneTree() {
        isTreeValid.set(false)
    }

    private fun addNewMesh(parent: SceneObjectItem, meshShape: MeshShapeData) {
        val parentScene = editor.activeScene.value ?: return
        val id = editor.projectModel.nextId()
        val name = editor.projectModel.uniquifyName(meshShape.name)
        val nodeData = SceneNodeData(name, id)
        nodeData.components += MeshComponentData(meshShape)
        nodeData.components += MaterialComponentData(-1)
        val mesh = SceneNodeModel(nodeData, parent.nodeModel, parentScene)
        AddNodeAction(mesh).apply()
    }

    private fun addNewModel(parent: SceneObjectItem, modelAsset: AssetItem) {
        val parentScene = editor.activeScene.value ?: return
        val id = editor.projectModel.nextId()
        val name = editor.projectModel.uniquifyName(modelAsset.name)
        val nodeData = SceneNodeData(name, id)
        nodeData.components += ModelComponentData(modelAsset.path)
        val mesh = SceneNodeModel(nodeData, parent.nodeModel, parentScene)
        AddNodeAction(mesh).apply()
    }

    private fun addNewLight(parent: SceneObjectItem, lightType: LightTypeData) {
        val parentScene = editor.activeScene.value ?: return
        val id = editor.projectModel.nextId()
        val name = editor.projectModel.uniquifyName(lightType.name)
        val nodeData = SceneNodeData(name, id)
        nodeData.components += DiscreteLightComponentData(lightType)
        val light = SceneNodeModel(nodeData, parent.nodeModel, parentScene)

        val transform = MutableMat4d().translate(EditorDefaults.DEFAULT_LIGHT_POSITION)
        if (lightType !is LightTypeData.Point) {
            transform.mul(MutableMat4d().rotate(EditorDefaults.DEFAULT_LIGHT_ROTATION))
        }
        light.transform.componentData.transform = TransformData.fromMatrix(transform)

        AddNodeAction(light).apply()
    }

    private fun addEmptyNode(parent: SceneObjectItem) {
        val parentScene = editor.activeScene.value ?: return
        val id = editor.projectModel.nextId()
        val name = editor.projectModel.uniquifyName("Empty")
        val nodeData = SceneNodeData(name, id)
        val empty = SceneNodeModel(nodeData, parent.nodeModel, parentScene)
        AddNodeAction(empty).apply()
    }

    private fun deleteNode(node: SceneObjectItem) {
        val removeNode = node.nodeModel as? SceneNodeModel ?: return
        DeleteNodeAction(removeNode).apply()
    }

    private fun focusNode(node: SceneObjectItem) {
        (node.nodeModel as? SceneNodeModel)?.let { nodeModel ->
            sceneBrowser.editor.editorCameraTransform.focusObject(nodeModel)
        }
    }

    override fun UiScope.compose() {
        editor.activeScene.use()

        if (!isTreeValid.use()) {
            treeItems.clear()
            editor.projectModel.getCreatedScenes().forEach { sceneModel ->
                sceneModel.drawNode.let {
                    treeItems.appendNode(sceneModel, it, sceneModel, 0)
                }
            }
            isTreeValid.set(true)
        }

        LazyList(
            containerModifier = { it.backgroundColor(null) }
        ) {
            var hoveredIndex by remember(-1)
            val itemPopupMenu = remember { ContextPopupMenu<SceneObjectItem>() }

            itemsIndexed(treeItems) { i, item ->
                if (item.type != SceneObjectType.NON_MODEL_NODE && item.node != item.nodeModel.drawNode) {
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
                item("Box") { addNewMesh(it, MeshShapeData.defaultBox) }
                item("Rect") { addNewMesh(it, MeshShapeData.defaultRect) }
                item("Ico-Sphere") { addNewMesh(it, MeshShapeData.defaultIcoSphere) }
                item("UV-Sphere") { addNewMesh(it, MeshShapeData.defaultUvSphere) }
                item("Cylinder") { addNewMesh(it, MeshShapeData.defaultCylinder) }
                item("Capsule") { addNewMesh(it, MeshShapeData.defaultCapsule) }
                item("Empty") { addNewMesh(it, MeshShapeData.defaultEmpty) }
            }
            subMenu("glTF model") {
                item("Import model") { logE { "Not yet implemented" } }
                divider()
                sceneBrowser.editor.availableAssets.modelAssets.forEach { modelAsset ->
                    item(modelAsset.name) { addNewModel(it, modelAsset) }
                }
            }
            editor.activeScene.value?.let { sceneModel ->
                if (sceneModel.drawNode.lighting.lights.size < sceneModel.maxNumLightsState.value) {
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
            .onClick {
                if (it.pointer.isLeftButtonClicked) {
                    editor.selectionOverlay.selectSingle(item.nodeModel)
                    if (it.pointer.leftButtonRepeatedClickCount == 2 && item.isExpandable) {
                        item.toggleExpanded()
                    }
                }
            }
        if (isHovered) {
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
            if (item.nodeModel is SceneNodeModel) {
                modifier.installDragAndDropHandler(dndCtx, dndHandler) { DndItemFlavor.SCENE_NODE_MODEL.itemOf(item.nodeModel) }
            } else if (item.nodeModel is SceneModel) {
                modifier.installDragAndDropHandler(dndCtx, dndHandler) { DndItemFlavor.SCENE_MODEL.itemOf(item.nodeModel) }
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

        val fgColor = if (item.nodeModel in editor.selectionOverlay.selection.use()) {
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

        val isVisible = item.nodeModel.isVisibleState.use()
        val fgColorVis = if (isVisible) fgColor else fgColor.withAlpha(0.5f)

        // type icon
        Image {
            val icon = when (item.type) {
                SceneObjectType.NON_MODEL_NODE -> IconMap.small.nodeCircle
                SceneObjectType.CAMERA -> IconMap.small.camera
                SceneObjectType.LIGHT -> IconMap.small.light
                SceneObjectType.GROUP -> IconMap.small.quadBox
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
                        .onClick { SetVisibilityAction(item.nodeModel as SceneNodeModel, !isVisible).apply() }
                }
            }
        }
    }

    private fun MutableList<SceneObjectItem>.appendNode(scene: SceneModel, node: Node, selectModel: NodeModel, depth: Int) {
        // get nodeModel for node, this should be equal to [selectModel] for regular objects but can be null if node
        // does not correspond to a scene model item (e.g. child meshes of a gltf model)
        val nodeModel = scene.nodesToNodeModels[node]

        val item = if (nodeModel != null) {
            modelTreeItemMap.getOrPut(nodeModel) {
                SceneObjectItem(node, nodeModel)
            }
        } else {
            nodeTreeItemMap.getOrPut(node) {
                SceneObjectItem(node, selectModel, SceneObjectType.NON_MODEL_NODE)
            }
        }

        // update item node, it can change when model / app is reloaded
        item.node = node
        item.depth = depth

        add(item)
        if (item.isExpanded.value) {
            node.children.forEach {
                if (!it.tags.hasTag(KoolEditor.TAG_EDITOR_SUPPORT_CONTENT)) {
                    val childNodeModel = scene.nodesToNodeModels[it]
                    appendNode(scene, it, childNodeModel ?: selectModel, depth + 1)
                }
            }
        }
    }

    private inner class SceneObjectItem(
        node: Node,
        val nodeModel: NodeModel,
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

            if (nodeModel is SceneModel) {
                return SceneObjectType.SCENE
            }

            return when (nodeModel.getComponent<ContentComponent>()) {
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
        DndHandler(dropTarget, setOf(DndItemFlavor.SCENE_NODE_MODEL))
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
            val dragTreeItem = dragItem.get(DndItemFlavor.SCENE_NODE_MODEL)
            val self = treeItem.nodeModel
            if (dragTreeItem != self) {
                when {
                    insertPos.value == -1 && self is SceneNodeModel -> {
                        MoveSceneNodeAction(dragTreeItem, self.parent, NodeModel.InsertionPos.Before(self)).apply()
                    }
                    insertPos.value == 1 && self is SceneNodeModel -> {
                        MoveSceneNodeAction(dragTreeItem, self.parent, NodeModel.InsertionPos.After(self)).apply()
                    }
                    else -> MoveSceneNodeAction(dragTreeItem, self, NodeModel.InsertionPos.End).apply()
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