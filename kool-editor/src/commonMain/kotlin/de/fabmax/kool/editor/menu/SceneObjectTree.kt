package de.fabmax.kool.editor.menu

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_DOWN
import de.fabmax.kool.modules.ui2.ArrowScope.Companion.ROTATION_RIGHT
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene

class SceneObjectTree(val editor: KoolEditor, val sceneBrowser: SceneBrowser) : Composable, KoolEditor.AppReloadListener {

    private val treeItemMap = mutableMapOf<Node, SceneObjectItem>()
    private val treeItems = mutableListOf<SceneObjectItem>()
    private val isTreeValid = mutableStateOf(false)

    init {
        editor.appReloadListeners += this
    }

    override fun onAppReload(oldApp: KoolEditor.AppContext?, newApp: KoolEditor.AppContext) {
        sceneBrowser.selectedObject.set(null)
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

    private inner class SceneObjectItem(
        val node: Node,
        val type: SceneObjectType,
        val depth: Int
    ) {
        val name: String get() = node.name
        val isExpandable: Boolean get() = node.children.isNotEmpty()
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