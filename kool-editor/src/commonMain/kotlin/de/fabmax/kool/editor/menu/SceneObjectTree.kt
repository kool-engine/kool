package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene

class SceneObjectTree(val editor: KoolEditor) : Composable, KoolEditor.AppReloadListener {

    private val treeItemMap = mutableMapOf<Node, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    private var selectedItem = mutableStateOf<Node?>(null)

    init {
        editor.appReloadListeners += this
    }

    override fun onAppReload(oldApp: KoolEditor.AppContext?, newApp: KoolEditor.AppContext) {
        selectedItem.set(null)
        treeItemMap.clear()
        isTreeValid.set(false)
    }

    override fun UiScope.compose() {
        val app = editor.loadedApp.use() ?: return

        if (!isTreeValid.use()) {
            treeItems.clear()
            app.appScenes.forEach { treeItems.appendNode(it, 0) }
            isTreeValid.set(true)
        }

        LazyList {
            items(treeItems) {
                sceneObjectItem(it)
            }
        }
    }

    private fun UiScope.sceneObjectItem(item: SceneObjectItem) = Row(width = Grow.Std) {
        modifier
            .height(sizes.largeGap * 1.3f)
            .onClick {
                selectedItem.set(item.node)
            }

        if (item.node === selectedItem.use()) {
            modifier.backgroundColor(colors.secondaryVariant.withAlpha(0.4f))
        }

        // tree-depth based indentation
        if (item.depth > 0) {
            Box(width = sizes.largeGap * item.depth.toFloat()) { }
        }

        // expand / collapse arrow
        if (item.isExpandable) {
            Box {
                modifier
                    .size(sizes.largeGap, FitContent)
                    .alignY(AlignmentY.Center)
                Arrow {
                    modifier
                        .rotation(if (item.isExpanded.use()) ROTATION_DOWN else ROTATION_RIGHT)
                        .align(AlignmentX.Center, AlignmentY.Center)
                        .onClick {
                            item.isExpanded.set(!item.isExpanded.value)
                            isTreeValid.set(false)
                        }
                }
            }
        } else {
            Box(width = sizes.largeGap) { }
        }

        Text(item.name) {
            modifier
                .alignY(AlignmentY.Center)
            if (item.node === selectedItem.use()) {
                modifier.textColor(colors.primary)
            }
        }
    }

    private fun MutableList<SceneObjectItem>.appendNode(node: Node, depth: Int) {
        val item = treeItemMap.getOrPut(node) {
            val type = when (node) {
                is Scene -> SceneObjectType.SCENE
                is Mesh -> SceneObjectType.MESH
                is Camera -> SceneObjectType.CAMERA
                else -> SceneObjectType.NODE
            }
            SceneObjectItem(node, type, depth)
        }

        add(item)
        if (item.isExpanded.value) {
            node.children.forEach {
                appendNode(it, depth + 1)
            }
        }
    }

    private class SceneObjectItem(
        val node: Node,
        val type: SceneObjectType,
        val depth: Int
    ) {
        val name: String get() = node.name
        val isExpandable: Boolean get() = node.children.isNotEmpty()
        val isExpanded = mutableStateOf(false)
    }

    private enum class SceneObjectType {
        CAMERA,
        MESH,
        NODE,
        SCENE
    }
}