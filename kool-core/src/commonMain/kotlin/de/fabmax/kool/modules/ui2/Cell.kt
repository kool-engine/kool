package de.fabmax.kool.modules.ui2

inline fun UiScope.Cell(block: UiScope.() -> Unit) {
    val childCell = CellNode(uiNode, uiCtx)
    uiNode.children += childCell
    childCell.block()
}

class CellNode(parent: UiNode?, uiCtx: UiContext) : UiNode(parent, uiCtx), UiScope {
    override val modifier = UiModifier()
}