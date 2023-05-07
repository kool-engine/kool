package de.fabmax.kool.modules.ui2.docking

interface Dockable {
    val name: String
    var dockOrderIndex: Int

    val isDocked: Boolean
    fun onDocked(dockNode: DockNodeLeaf)
    fun onUndocked(dockNode: DockNodeLeaf)
}