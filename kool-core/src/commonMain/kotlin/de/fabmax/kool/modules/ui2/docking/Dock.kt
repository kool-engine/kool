package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.UniqueId
import de.fabmax.kool.util.logE

class Dock(name: String? = null) : Node(name = name ?: UniqueId.nextId("Dock")) {

    private val dockableNodes = Node(name = "${name}.dockableNodes")
    private val _dockables = mutableMapOf<UiSurface, Dockable>()
    val dockingSurface = UiSurface(name = "${name}.dockingSurface")
    val dockingSurfaceOverlay = UiSurface(name = "${name}.dockingSurfaceOverlay")
    val dockables: Map<UiSurface, Dockable> get() = _dockables

    val resizeMargin = mutableStateOf(Dp(4f))
    val borderWidth = mutableStateOf(Dp.ZERO)
    val borderColor = mutableStateOf(Color.BLACK)

    var root: DockNode = DockNodeLeaf(this, null)
        internal set

    val dndContext = DragAndDropContext<Dockable>()

    var dockingPaneComposable = Composable { root() }

    init {
        addNode(dockingSurface)
        addNode(dockableNodes)
        addNode(dockingSurfaceOverlay)

        dockingSurface.inputMode = UiSurface.InputCaptureMode.CaptureDisabled
        dockingSurface.content = {
            dndContext.clearHandlers()
            dockingPaneComposable()
            dndContext.registerHandler(dndOverlayVisibilityListener)
        }

        dockingSurfaceOverlay.isVisible = false
        dockingSurfaceOverlay.inputMode = UiSurface.InputCaptureMode.CaptureDisabled
        dockingSurfaceOverlay.content = {
            root.composeOverlay(this)
        }

        onUpdate {
            sortDockablesDrawOrder()
        }
    }

    private fun sortDockablesDrawOrder() {
        dockableNodes.sortChildrenBy { (it as UiSurface).order }
    }

    private val UiSurface.order: Double
        get() {
            val dockable = _dockables[this]
            return when {
                dockable == null -> 0.0
                dockable.isDocked.value -> {
                    val isOnTop = dockable.dockedTo.value?.isOnTop(dockable) != false
                    lastInputTime - if (isOnTop) 1e9 else 1e10
                }
                else -> lastInputTime
            }
        }

    fun addDockableSurface(dockable: Dockable, drawNode: UiSurface) {
        dockableNodes += drawNode
        _dockables[drawNode] = dockable
    }

    fun removeDockableSurface(drawNode: UiSurface) {
        if (dockableNodes.removeNode(drawNode)) {
            val dockable = _dockables.remove(drawNode)
            if (dockable == null) {
                logE { "dockable for UiSurface ${drawNode.name} not found" }
            } else {
                dockable.dockedTo.value?.undock(dockable)
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
        _dockables.values.forEach { it.dockedTo.value?.undock(it) }

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
            dockableNodes.children
                .map { it as UiSurface }
                .filter { _dockables[it]?.isInBounds(screenPosPx) == true }
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

    private val dndOverlayVisibilityListener = object : DragAndDropHandler<Dockable> {
        override val dropTarget: UiNode? = null

        override fun receive(dragItem: Dockable, dragPointer: PointerEvent, source: DragAndDropHandler<Dockable>?): Boolean {
            return false
        }

        override fun onDragStart(dragItem: Dockable, dragPointer: PointerEvent, source: DragAndDropHandler<Dockable>?) {
            dockingSurfaceOverlay.isVisible = true
        }

        override fun onDragEnd(
            dragItem: Dockable,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<Dockable>?,
            target: DragAndDropHandler<Dockable>?,
            success: Boolean
        ) {
            dockingSurfaceOverlay.isVisible = false
        }
    }
}