package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.UniqueId

class SceneObjectTree(val editor: KoolEditor, val sceneBrowser: SceneBrowser) : Composable {

    private val treeItemMap = mutableMapOf<MSceneNode<*>, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    private val contextMenuItems = SubMenuItem {
        item("Delete object") {
            deleteNode(it)
        }
        subMenu("Add child object") {
            subMenu("Mesh") {
                item("Box") {
                    addNewMesh(it, MMeshType.Box(MVec3(1.0, 1.0, 1.0)))
                }
                item("Rect") {
                    addNewMesh(it, MMeshType.Rect(MVec2(1.0, 1.0)))
                }
                item("Ico-Sphere") {
                    addNewMesh(it, MMeshType.IcoSphere(1f, 3))
                }
                item("UV-Sphere") {
                    addNewMesh(it, MMeshType.UvSphere(1f, 20))
                }
                item("Cylinder") {
                    addNewMesh(it, MMeshType.Cylinder(1f, 1f, 1f, 20))
                }
                item("Empty Mesh") {
                    addNewMesh(it, MMeshType.Empty)
                }
            }
            item("glTF Model") { }
            item("Group") { }
        }
        divider()
        item("Focus object") { }
    }

    private val itemPopupMenu = ContextPopupMenu(contextMenuItems)

    fun refreshSceneTree() {
        isTreeValid.set(false)
    }

    private fun deleteNode(node: SceneObjectItem) {
        val parentScene = EditorState.selectedScene.value ?: return
        parentScene.removeNode(node.node)
        refreshSceneTree()

        if (EditorState.selectedObject.value == node.node) {
            EditorState.selectedObject.set(null)
        }
    }

    private fun addNewMesh(parent: SceneObjectItem, meshType: MMeshType) {
        val parentScene = EditorState.selectedScene.value ?: return
        val hierarchyPath = parent.node.nodeProperties.hierarchyPath + UniqueId.nextId(meshType.name)
        val meshProps = MCommonNodeProperties(hierarchyPath.toMutableList(), MTransform.IDENTITY)
        parentScene.addNode(MMesh(meshProps, meshType))
        refreshSceneTree()
    }

    override fun UiScope.compose() {
        EditorState.selectedScene.use()

        if (!isTreeValid.use()) {
            println("refresh tree")
            treeItems.clear()
            EditorState.projectModel.scenes.forEach {
                treeItems.appendNode(it, it.created, 0)
            }
            isTreeValid.set(true)
        }

        LazyList(
            containerModifier = { it.backgroundColor(null) }
        ) {
            items(treeItems) { item ->
                sceneObjectItem(item).apply {
                    modifier.onClick {
                        if (it.pointer.isRightButtonClicked) {
                            itemPopupMenu.show(it.screenPosition, item)
                        }
                    }
                }
            }

            itemPopupMenu()
        }
    }

    private fun UiScope.sceneObjectItem(item: SceneObjectItem) = Row(width = Grow.Std) {
        modifier
            .height(sizes.lineHeight)
            .padding(horizontal = sizes.gap)
            .onClick {
                if (it.pointer.isLeftButtonClicked) {
                    EditorState.selectedObject.set(item.node)
                    if (it.pointer.leftButtonRepeatedClickCount == 2 && item.isExpandable) {
                        item.toggleExpanded()
                    }
                }
            }

        if (item.node === EditorState.selectedObject.use()) {
            modifier.backgroundColor(colors.selectionBg)
        }

        // tree-depth based indentation
        if (item.depth > 0) {
            Box(width = sizes.largeGap * item.depth.toFloat()) { }
        }

        // expand / collapse arrow
        if (item.isExpandable) {
            Box {
                modifier
                    .size(sizes.lineHeight, FitContent)
                    .alignY(AlignmentY.Center)
                Arrow {
                    modifier
                        .rotation(if (item.isExpanded.use()) ROTATION_DOWN else ROTATION_RIGHT)
                        .align(AlignmentX.Center, AlignmentY.Center)
                        .onClick { item.toggleExpanded() }
                }
            }
        } else {
            Box(width = sizes.largeGap) { }
        }

        Text(item.name) {
            modifier
                .margin(horizontal = sizes.smallGap)
                .alignY(AlignmentY.Center)
            if (item.node === EditorState.selectedObject.use()) {
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
        val node: MSceneNode<*>,
        val type: SceneObjectType,
        val depth: Int
    ) {
        val name: String get() = node.nodeProperties.name
        val isExpandable: Boolean get() = node.created?.children?.isNotEmpty() == true
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