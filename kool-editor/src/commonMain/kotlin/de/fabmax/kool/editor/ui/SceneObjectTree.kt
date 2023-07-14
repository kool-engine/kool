package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.*
import de.fabmax.kool.editor.actions.AddNodeAction
import de.fabmax.kool.editor.actions.DeleteNodeAction
import de.fabmax.kool.editor.actions.SetVisibilityAction
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE

class SceneObjectTree(val sceneBrowser: SceneBrowser) : Composable {

    private val modelTreeItemMap = mutableMapOf<EditorNodeModel, SceneObjectItem>()
    private val nodeTreeItemMap = mutableMapOf<Node, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    init {
        sceneBrowser.editor.appLoader.appReloadListeners += AppReloadListener {
            nodeTreeItemMap.clear()
        }
    }

    fun refreshSceneTree() {
        isTreeValid.set(false)
    }

    private fun addNewMesh(parent: SceneObjectItem, meshShape: MeshShapeData) {
        val parentScene = EditorState.activeScene.value ?: return
        val id = EditorState.projectModel.nextId()
        val nodeData = SceneNodeData("${meshShape.name}-$id", id)
        nodeData.components += MeshComponentData(meshShape)
        nodeData.components += MaterialComponentData(-1)
        val mesh = SceneNodeModel(nodeData, parent.nodeModel, parentScene)
        AddNodeAction(mesh).apply()
    }

    private fun addNewModel(parent: SceneObjectItem, modelAsset: AssetItem) {
        val parentScene = EditorState.activeScene.value ?: return
        val id = EditorState.projectModel.nextId()
        val nodeData = SceneNodeData(modelAsset.name, id)
        nodeData.components += ModelComponentData(modelAsset.path)
        val mesh = SceneNodeModel(nodeData, parent.nodeModel, parentScene)
        AddNodeAction(mesh).apply()
    }

    private fun addNewLight(parent: SceneObjectItem, lightType: LightTypeData) {
        val parentScene = EditorState.activeScene.value ?: return
        val id = EditorState.projectModel.nextId()
        val nodeData = SceneNodeData("${lightType.name}-$id", id)
        nodeData.components += DiscreteLightComponentData(lightType)
        val light = SceneNodeModel(nodeData, parent.nodeModel, parentScene)

        val transform = Mat4d().translate(EditorDefaults.DEFAULT_LIGHT_POSITION)
        if (lightType !is LightTypeData.Point) {
            transform.mul(Mat4d().setRotate(EditorDefaults.DEFAULT_LIGHT_ROTATION))
        }
        light.transform.componentData.transform = TransformData(transform)

        AddNodeAction(light).apply()
    }

    private fun addEmptyNode(parent: SceneObjectItem) {
        val parentScene = EditorState.activeScene.value ?: return
        val id = EditorState.projectModel.nextId()
        val nodeData = SceneNodeData("Empty-$id", id)
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
        EditorState.activeScene.use()

        if (!isTreeValid.use()) {
            treeItems.clear()
            EditorState.projectModel.getCreatedScenes().forEach {
                treeItems.appendNode(it, it.drawNode, it, 0)
            }
            isTreeValid.set(true)
        }

        LazyList(
            containerModifier = { it.backgroundColor(null) }
        ) {
            var hoveredIndex by remember(-1)
            val itemPopupMenu = remember { ContextPopupMenu<SceneObjectItem>() }

            itemsIndexed(treeItems) { i, item ->
                sceneObjectItem(item, hoveredIndex == i).apply {
                    modifier
                        .onEnter { hoveredIndex = i }
                        .onExit { hoveredIndex = -1 }
                        .onClick {
                            if (it.pointer.isRightButtonClicked) {
                                itemPopupMenu.show(it.screenPosition, makeMenu(), item)
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

    private fun makeMenu() = SubMenuItem {
        item("Delete object") { deleteNode(it) }
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
            EditorState.activeScene.value?.let { sceneModel ->
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
        divider()
        item("Focus object") { focusNode(it) }
    }

    private fun UiScope.sceneObjectItem(item: SceneObjectItem, isHovered: Boolean) = Row(width = Grow.Std) {
        modifier
            .margin(horizontal = sizes.smallGap)
            .height(sizes.lineHeight)
            .onClick {
                if (it.pointer.isLeftButtonClicked) {
                    EditorState.selectSingle(item.nodeModel)
                    if (it.pointer.leftButtonRepeatedClickCount == 2 && item.isExpandable) {
                        item.toggleExpanded()
                    }
                }
            }
        if (isHovered) {
            modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
        }

        // tree-depth based indentation
        if (item.depth > 0) {
            Box(width = sizes.gap * item.depth) { }
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

        val fgColor = if (item.nodeModel in EditorState.selection.use()) {
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
                SceneObjectType.NON_MODEL_NODE -> IconMap.NODE_CIRCLE
                SceneObjectType.CAMERA -> IconMap.CAMERA
                SceneObjectType.LIGHT -> IconMap.LIGHT
                SceneObjectType.GROUP -> IconMap.CIRCLE_DOT
                SceneObjectType.MESH -> IconMap.CUBE
                SceneObjectType.MODEL -> IconMap.TREE
                SceneObjectType.SCENE -> IconMap.WORLD
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
                    else -> fgColor.withAlpha(0.5f)
                }
                Image {
                    modifier
                        .alignX(AlignmentX.End)
                        .alignY(AlignmentY.Center)
                        .margin(end = sizes.smallGap)
                        .iconImage(if (isVisible) IconMap.EYE else IconMap.EYE_OFF, eyeColor)
                        .onClick { SetVisibilityAction(item.nodeModel as SceneNodeModel, !isVisible).apply() }
                }
            }
        }

    }

    private fun MutableList<SceneObjectItem>.appendNode(scene: SceneModel, node: Node, selectModel: EditorNodeModel, depth: Int) {
        // get nodeModel for node, this should be equal to [selectModel] for regular objects but can be null if node
        // does not correspond to a scene model item (e.g. child meshes of a gltf model)
        val nodeModel = scene.nodesToNodeModels[node]

        val item = if (nodeModel != null) {
            modelTreeItemMap.getOrPut(nodeModel) {
                val type = when (node) {
                    is Scene -> SceneObjectType.SCENE
                    is Mesh -> SceneObjectType.MESH
                    is Camera -> SceneObjectType.CAMERA
                    is Model -> SceneObjectType.MODEL
                    is Light -> SceneObjectType.LIGHT
                    else -> SceneObjectType.GROUP
                }
                SceneObjectItem(node, nodeModel, type, depth)
            }
        } else {
            nodeTreeItemMap.getOrPut(node) {
                SceneObjectItem(node, selectModel, SceneObjectType.NON_MODEL_NODE, depth)
            }
        }

        // update item node, it can change when model / app is reloaded
        item.node = node

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
        var node: Node,
        val nodeModel: EditorNodeModel,
        val type: SceneObjectType,
        val depth: Int
    ) {
        val name: String get() = node.name
        val isExpandable: Boolean get() = node.children.isNotEmpty()
        val isExpanded = mutableStateOf(type.startExpanded)

        fun toggleExpanded() {
            if (isExpandable) {
                isExpanded.set(!isExpanded.value)
                isTreeValid.set(false)
            }
        }
    }

    private enum class SceneObjectType(val startExpanded: Boolean = false, val isHideable: Boolean = true) {
        NON_MODEL_NODE(isHideable = false),
        CAMERA(isHideable = false),
        LIGHT,
        GROUP(true),
        MESH,
        MODEL,
        SCENE(true, isHideable = false)
    }
}