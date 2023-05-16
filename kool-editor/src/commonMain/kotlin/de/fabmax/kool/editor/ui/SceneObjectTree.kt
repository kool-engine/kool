package de.fabmax.kool.editor.ui

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.AppAsset
import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.AddObjectAction
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.RemoveObjectAction
import de.fabmax.kool.editor.model.*
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.RenderLoop
import de.fabmax.kool.util.logI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SceneObjectTree(val sceneBrowser: SceneBrowser) : Composable {

    private val treeItemMap = mutableMapOf<MSceneNode, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    private val itemPopupMenu = ContextPopupMenu<SceneObjectItem>()

    fun refreshSceneTree() {
        isTreeValid.set(false)
    }

    private fun addNewMesh(parent: SceneObjectItem, meshShape: MMeshShape) {
        val parentScene = EditorState.selectedScene.value ?: return
        val id = EditorState.projectModel.nextId()
        val mesh = MMesh(id).apply {
            name = "${meshShape.name}-$id"
            transform = MTransform.IDENTITY
            shape = meshShape
        }
        mesh.parentId = parent.nodeModel.nodeId
        EditorActions.applyAction(AddObjectAction(mesh, parentScene, this))
    }

    private fun addNewModel(parent: SceneObjectItem, modelAsset: AppAsset) {
        val parentScene = EditorState.selectedScene.value ?: return
        val id = EditorState.projectModel.nextId()
        logI { "Adding model ${modelAsset.name}" }

        Assets.launch {
            val model = loadGltfModel(modelAsset.path)
            withContext(Dispatchers.RenderLoop) {
                parentScene.created?.addNode(model)
            }
        }
    }

    private fun deleteNode(node: SceneObjectItem) {
        val parentScene = EditorState.selectedScene.value ?: return
        EditorActions.applyAction(RemoveObjectAction(node.nodeModel, parentScene, this))
    }

    override fun UiScope.compose() {
        EditorState.selectedScene.use()

        if (!isTreeValid.use()) {
            treeItems.clear()
            EditorState.projectModel.scenes.forEach {
                treeItems.appendNode(it, it.created, 0)
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
                    addNewMesh(it, MMeshShape.defaultBox)
                }
                item("Rect") {
                    addNewMesh(it, MMeshShape.defaultRect)
                }
                item("Ico-Sphere") {
                    addNewMesh(it, MMeshShape.defaultIcoSphere)
                }
                item("UV-Sphere") {
                    addNewMesh(it, MMeshShape.defaultUvSphere)
                }
                item("Cylinder") {
                    addNewMesh(it, MMeshShape.defaultCylinder)
                }
                item("Empty") {
                    addNewMesh(it, MMeshShape.Empty)
                }
            }
            subMenu("glTF Model") {
                item("Import Model") {
                    logI { "Not yet implemented" }
                }
                divider()
                sceneBrowser.editor.appAssets.modelAssets.forEach { modelAsset ->
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
                    EditorState.selectedObject.set(item.nodeModel)
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
            if (item.nodeModel === EditorState.selectedObject.use()) {
                modifier.textColor(colors.primary)
            }
        }
    }

    private fun MutableList<SceneObjectItem>.appendNode(scene: MScene, node: Node?, depth: Int) {
        node ?: return
        val nodeModel = scene.nodesToNodeModels[node] ?: return

        val item = treeItemMap.getOrPut(nodeModel) {
            val type = when (node) {
                is Scene -> SceneObjectType.SCENE
                is Mesh -> SceneObjectType.MESH
                is Camera -> SceneObjectType.CAMERA
                else -> SceneObjectType.NODE
            }
            SceneObjectItem(nodeModel, type, depth)
        }

        add(item)
        if (item.isExpanded.value) {
            node.children.forEach {
                // fixme: somewhat hacky way to hide editor objects in the scene graph
                if (!it.tags.hasTag("hidden")) {
                    appendNode(scene, it, depth + 1)
                }
            }
        }
    }

    private inner class SceneObjectItem(
        val nodeModel: MSceneNode,
        val type: SceneObjectType,
        val depth: Int
    ) {
        val name: String get() = nodeModel.name
        val isExpandable: Boolean get() = nodeModel.created?.children?.isNotEmpty() == true
        val isExpanded = mutableStateOf(true)

        fun toggleExpanded() {
            if (isExpandable) {
                isExpanded.set(!isExpanded.value)
                isTreeValid.set(false)
            }
        }
    }

    private enum class SceneObjectType {
        CAMERA,
        MESH,
        NODE,
        SCENE
    }
}