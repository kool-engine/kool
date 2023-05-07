package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.DragAndDropContext
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.UniqueId

class Dock(name: String? = null) : Node(name = name ?: UniqueId.nextId("Dock")) {

    private val dockableNodes = Node(name = "${name}.dockableNodes")
    private val dockables = mutableMapOf<UiSurface, Dockable>()
    val dockingSurface = UiSurface(name = "${name}.dockingSurface")

    internal var root: DockNode = DockNodeLeaf(this, null)

    val dndContext = DragAndDropContext<Dockable>()

    init {
        addNode(dockableNodes)
        addNode(dockingSurface)

        dockingSurface.inputMode = UiSurface.InputCaptureMode.CaptureDisabled
        dockingSurface.content = {
            dndContext.clearHandlers()
            root()
        }

        onUpdate {
            sortDockablesDrawOrder()
        }
    }

    private fun sortDockablesDrawOrder() {
        dockableNodes.sortChildrenBy { drawNode ->
            drawNode as UiSurface
            val dockable = dockables[drawNode]
            when {
                dockable == null -> 0.0
                dockable.isDocked -> drawNode.lastInputTime - 1e9
                else -> drawNode.lastInputTime
            }
        }
    }

    fun addDockableSurface(dockable: Dockable, drawNode: UiSurface) {
        dockableNodes += drawNode
        dockables[drawNode] = dockable
    }

    fun removeDockableSurface(drawNode: UiSurface) {
        dockables[drawNode]?.let { root.undock(it) }
        dockableNodes -= drawNode
        dockables -= drawNode
    }

    fun getLeafNodeAt(screenPosPx: Vec2f): DockNode? {
        return root.getLeafNodeAt(screenPosPx)
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