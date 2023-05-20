package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AppReloadListener
import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.AddNodeAction
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.RemoveNodeAction
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.MeshShapeData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.editor.data.SceneNodeData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logI

class SceneObjectTree(val sceneBrowser: SceneBrowser) : Composable {

    private val modelTreeItemMap = mutableMapOf<EditorNodeModel, SceneObjectItem>()
    private val nodeTreeItemMap = mutableMapOf<Node, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    private val itemPopupMenu = ContextPopupMenu<SceneObjectItem>()

    init {
        sceneBrowser.editor.appLoader.appReloadListeners += AppReloadListener {
            nodeTreeItemMap.clear()
        }
    }

    fun refreshSceneTree() {
        isTreeValid.set(false)
    }

    private fun addNewMesh(parent: SceneObjectItem, meshShape: MeshShapeData) {
        val parentScene = EditorState.selectedScene.value ?: return
        val id = EditorState.projectModel.nextId()
        val nodeData = SceneNodeData("${meshShape.name}-$id", id)
        nodeData.components += MeshComponentData(meshShape)
        val mesh = SceneNodeModel(nodeData, parentScene)
        EditorActions.applyAction(AddNodeAction(mesh, parent.nodeModel, parentScene, this))
    }

    private fun addNewModel(parent: SceneObjectItem, modelAsset: AssetItem) {
        val parentScene = EditorState.selectedScene.value ?: return
        val id = EditorState.projectModel.nextId()
        val nodeData = SceneNodeData(modelAsset.name, id)
        nodeData.components += ModelComponentData(modelAsset.path)
        val mesh = SceneNodeModel(nodeData, parentScene)
        EditorActions.applyAction(AddNodeAction(mesh, parent.nodeModel, parentScene, this))
    }

    private fun deleteNode(node: SceneObjectItem) {
        val parentScene = EditorState.selectedScene.value ?: return
        val parentNode = node.parentModel ?: return
        val removeNode = node.nodeModel as? SceneNodeModel ?: return
        EditorActions.applyAction(RemoveNodeAction(removeNode, parentNode, parentScene, this))
    }

    override fun UiScope.compose() {
        EditorState.selectedScene.use()

        if (!isTreeValid.use()) {
            treeItems.clear()
            EditorState.projectModel.getCreatedScenes().forEach {
                treeItems.appendNode(it, null, it.node, it, 0)
            }
            isTreeValid.set(true)
        }

        LazyList(
            containerModifier = { it.backgroundColor(null) }
        ) {
            var hoveredIndex by remember(-1)
            itemsIndexed(treeItems) { i, item ->
                sceneObjectItem(item).apply {
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
                    if (hoveredIndex == i) {
                        modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                    }

                }
            }

            itemPopupMenu()
        }
    }

    private fun makeMenu() = SubMenuItem {
        item("Delete object") {
            deleteNode(it)
        }
        subMenu("Add child object") {
            subMenu("Mesh") {
                item("Box") {
                    addNewMesh(it, MeshShapeData.defaultBox)
                }
                item("Rect") {
                    addNewMesh(it, MeshShapeData.defaultRect)
                }
                item("Ico-Sphere") {
                    addNewMesh(it, MeshShapeData.defaultIcoSphere)
                }
                item("UV-Sphere") {
                    addNewMesh(it, MeshShapeData.defaultUvSphere)
                }
                item("Cylinder") {
                    addNewMesh(it, MeshShapeData.defaultCylinder)
                }
                item("Empty") {
                    addNewMesh(it, MeshShapeData.Empty)
                }
            }
            subMenu("glTF Model") {
                item("Import Model") {
                    logI { "Not yet implemented" }
                }
                divider()
                sceneBrowser.editor.availableAssets.modelAssets.forEach { modelAsset ->
                    item(modelAsset.name) {
                        addNewModel(it, modelAsset)
                    }
                }

            }
            item("Group") { }
        }
        divider()
        item("Focus object") { }
    }

    private fun UiScope.sceneObjectItem(item: SceneObjectItem) = Row(width = Grow.Std) {
        modifier
            .margin(horizontal = sizes.smallGap)
            .height(sizes.lineHeight)
            .onClick {
                if (it.pointer.isLeftButtonClicked) {
                    EditorState.selectedNode.set(item.nodeModel)
                    if (it.pointer.leftButtonRepeatedClickCount == 2 && item.isExpandable) {
                        item.toggleExpanded()
                    }
                }
            }

        // tree-depth based indentation
        if (item.depth > 0) {
            Box(width = sizes.gap * item.depth) { }
        }

        // expand / collapse arrow
        Box {
            modifier
                .size(sizes.lineHeight, FitContent)
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

        Text(item.name) {
            modifier
                .alignY(AlignmentY.Center)
            val textColor = if (item.nodeModel === EditorState.selectedNode.use()) {
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
            modifier.textColor(textColor)
        }
    }

    private fun MutableList<SceneObjectItem>.appendNode(scene: SceneModel, parent: EditorNodeModel?, node: Node, selectModel: EditorNodeModel, depth: Int) {
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
                    else -> SceneObjectType.GROUP
                }
                SceneObjectItem(node, nodeModel, parent, type, depth)
            }
        } else {
            nodeTreeItemMap.getOrPut(node) {
                SceneObjectItem(node, selectModel, null, SceneObjectType.NON_MODEL_NODE, depth)
            }
        }

        // update item node, it can change when model / app is reloaded
        item.node = node

        add(item)
        if (item.isExpanded.value) {
            node.children.forEach {
                if (it.isVisible && !it.tags.hasTag(KoolEditor.TAG_EDITOR_SUPPORT_CONTENT)) {
                    val childNodeModel = scene.nodesToNodeModels[it]
                    appendNode(scene, nodeModel, it, childNodeModel ?: selectModel, depth + 1)
                }
            }
        }
    }

    private inner class SceneObjectItem(
        var node: Node,
        val nodeModel: EditorNodeModel,
        val parentModel: EditorNodeModel?,
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

    private enum class SceneObjectType(val startExpanded: Boolean = false) {
        NON_MODEL_NODE,
        CAMERA,
        GROUP(true),
        MESH,
        MODEL,
        SCENE(true)
    }
}