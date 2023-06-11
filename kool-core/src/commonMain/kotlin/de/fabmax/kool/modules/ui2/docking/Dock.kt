package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.UniqueId
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE

class Dock(name: String? = null) : Node(name = name ?: UniqueId.nextId("Dock")) {

    private val dockedNodes = Node(name = "${name}.dockableNodes")
    private val floatingNodes = Node(name = "${name}.floatingNodes")
    private val dockablesBySurface = mutableMapOf<UiSurface, Dockable>()
    private val surfacesByDockable = mutableMapOf<Dockable, UiSurface>()
    val dockingSurface = UiSurface(name = "${name}.dockingSurface")

    val borderWidth = mutableStateOf(Dp.ZERO)
    val borderColor = mutableStateOf(Color.BLACK)

    var root: DockNode = DockNodeLeaf(this, null)
        internal set

    val dndContext = DragAndDropContext<Dockable>()

    var dockingPaneComposable = Composable { root() }

    init {
        addNode(dockedNodes)
        addNode(dockingSurface)
        addNode(floatingNodes)

        dockingSurface.inputMode = UiSurface.InputCaptureMode.CaptureDisabled
        dockingSurface.content = {
            dndContext.clearHandlers()
            dockingPaneComposable()
        }

        onUpdate {
            sortDockablesDrawOrder()
        }
    }

    private fun sortDockablesDrawOrder() {
        dockedNodes.sortChildrenBy { (it as UiSurface).order }
        floatingNodes.sortChildrenBy { (it as UiSurface).order }
    }

    private val UiSurface.order: Double
        get() = lastInputTime
//        get() {
//            val dockable = dockables[this]
//            return when {
//                dockable == null -> 0.0
//                dockable.isDocked.value -> {
//                    val isOnTop = dockable.dockedTo.value?.isOnTop(dockable) != false
//                    lastInputTime - if (isOnTop) 1e9 else 1e10
//                }
//                else -> lastInputTime
//            }
//        }

    fun addDockableSurface(dockable: Dockable, drawNode: UiSurface) {
        if (dockable.isDocked.value) {
            dockedNodes += drawNode
        } else {
            floatingNodes += drawNode
        }
        dockablesBySurface[drawNode] = dockable
        surfacesByDockable[dockable] = drawNode
    }

    fun removeDockableSurface(drawNode: UiSurface) {
        if (dockedNodes.removeNode(drawNode)) {
            val dockable = dockablesBySurface.remove(drawNode)
            if (dockable == null) {
                logE { "dockable for UiSurface ${drawNode.name} not found" }
            } else {
                surfacesByDockable.remove(dockable)
                dockable.dockedTo.value?.undock(dockable)
            }
        }
    }

    internal fun onDocked(dockable: Dockable) {
        launchOnMainThread {
            surfacesByDockable[dockable]?.let { drawNode ->
                floatingNodes -= drawNode
                dockedNodes += drawNode
            }
        }
    }

    internal fun onUndocked(dockable: Dockable) {
        launchOnMainThread {
            surfacesByDockable[dockable]?.let { drawNode ->
                floatingNodes += drawNode
                dockedNodes -= drawNode
            }
        }
    }

    fun getLeafNodeAt(screenPosPx: Vec2f): DockNodeLeaf? {
        return root.getLeafNodeAt(screenPosPx)
    }

    fun getNodeAtPath(path: String): DockNode? {
        return try {
            var it = root
            path.split('/').drop(1).forEach { name ->
                val pos = name.replaceAfter(':', "").removeSuffix(":")
                val index = pos.toInt()
                it = (it as DockNodeInter).childNodes[index]
            }
            it
        } catch (e: Exception) {
            logE { "Failed to find DockNode at path $path" }
            null
        }
    }

    fun getLeafAtPath(path: String): DockNodeLeaf? {
        return try {
            getNodeAtPath(path) as DockNodeLeaf?
        } catch (e: Exception) {
            logE { "DockNode at path $path is not a leaf" }
            null
        }
    }

    fun createNodeLayout(nodePaths: List<String>) {
        // undock any existing dockable
        dockablesBySurface.values.forEach { it.dockedTo.value?.undock(it) }

        // create new node hierarchy
        nodePaths.forEach { path ->
            try {
                val name = path.replaceBeforeLast('/', "").removePrefix("/")
                val type = name.replaceBefore(':', "").removePrefix(":")
                val parentPath = path.replaceAfterLast("/", "", "").removeSuffix("/")
                val parent = if (parentPath.isEmpty()) null else getNodeAtPath(parentPath) as DockNodeInter

                val node = when (type) {
                    "row" -> DockNodeRow(this, parent)
                    "col" -> DockNodeColumn(this, parent)
                    "leaf" -> DockNodeLeaf(this, parent)
                    else -> throw IllegalArgumentException("Unknown node type $type in node path $path")
                }

                if (parent == null) {
                    root = node
                } else {
                    parent.childNodes.add(node)
                }
            } catch (e: Exception) {
                logE { "Failed to create node layout for path $path: $e" }
            }
        }
    }

    fun isSurfaceOnTop(surface: UiSurface, screenPosPx: Vec2f): Boolean {
        return if (surface == dockingSurface) {
            true
        } else {
            dockedNodes.children
                .map { it as UiSurface }
                .filter { dockablesBySurface[it]?.isInBounds(screenPosPx) == true }
                .maxByOrNull { it.order } == surface
        }
    }

    fun printHierarchy() {
        fun DockNode.printH(indent: String) {
            println("$indent${getPath()}, parent: ${parent?.getPath()}, w: $width, h: $height")
            if (this is DockNodeInter) {
                childNodes.forEach { it.printH("$indent  ") }
            }
        }
        root.printH("")
    }
}