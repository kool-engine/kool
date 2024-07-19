package de.fabmax.kool.modules.ui2.docking

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.Dimension
import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.MutableStateValue

interface Dockable {
    val name: String
    val isHidden: Boolean

    val preferredWidth: Dp?
    val preferredHeight: Dp?

    val dockedTo: MutableStateValue<DockNodeLeaf?>
    val isDocked: MutableStateValue<Boolean>

    val floatingX: MutableStateValue<Dp>
    val floatingY: MutableStateValue<Dp>
    val floatingWidth: MutableStateValue<Dimension>
    val floatingHeight: MutableStateValue<Dimension>

    fun isInBounds(screenPosPx: Vec2f): Boolean
}