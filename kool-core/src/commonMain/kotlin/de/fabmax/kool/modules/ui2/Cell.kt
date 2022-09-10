package de.fabmax.kool.modules.ui2

inline fun UiScope.Cell(block: UiScope.() -> Unit): UiScope {
    val cell = uiNode.createChild(CellNode::class, CellNode.factory)
    cell.block()
    return cell
}

class CellNode(parent: UiNode?, uiCtx: UiContext) : UiNode(parent, uiCtx), UiScope {
    override val modifier = UiModifier()

    companion object {
        val factory: (UiNode, UiContext) -> CellNode = { parent, uiCtx -> CellNode(parent, uiCtx) }
    }
}