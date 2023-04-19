package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.editor.model.MSceneNode
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene

class SceneObjectTree(val editor: KoolEditor, val sceneBrowser: SceneBrowser) : Composable {

    private val treeItemMap = mutableMapOf<MSceneNode<*>, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    fun refreshSceneTree() {
        isTreeValid.set(false)
    }

    override fun UiScope.compose() {
        editor.selectedScene.use()

        if (!isTreeValid.use()) {
            treeItems.clear()
            editor.projectModel.scenes.forEach {
                treeItems.appendNode(it, it.created, 0)
            }
            isTreeValid.set(true)
        }

        LazyList(
            containerModifier = { it.backgroundColor(null) }
        ) {
            items(treeItems) {
                sceneObjectItem(it)
            }
        }
    }

    private fun UiScope.sceneObjectItem(item: SceneObjectItem) = Row(width = Grow.Std) {
        modifier
            .height(sizes.lineHeight)
            .padding(horizontal = sizes.gap)
            .onClick {
                if (it.pointer.isLeftButtonClicked) {
                    sceneBrowser.selectedObject.set(item.node)
                    if (it.pointer.leftButtonRepeatedClickCount == 2 && item.isExpandable) {
                        item.toggleExpanded()
                    }
                }
            }

        if (item.node === sceneBrowser.selectedObject.use()) {
            modifier.backgroundColor(colors.secondaryVariantAlpha(0.5f))
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
            if (item.node === sceneBrowser.selectedObject.use()) {
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