package de.fabmax.kool.modules.ui2

interface ScrollPaneScope : UiScope {
    override val modifier: ScrollPaneModifier
}

class ScrollPaneModifier : UiModifier() {
    var scrollPosX = Dp.ZERO
    var scrollPosY = Dp.ZERO

    fun scrollPos(x: Dp = scrollPosX, y: Dp = scrollPosY): ScrollPaneModifier {
        scrollPosX = x
        scrollPosY = y
        return this
    }

    override fun resetDefaults() {
        super.resetDefaults()
        scrollPosX = Dp.ZERO
        scrollPosY = Dp.ZERO
    }
}

inline fun UiScope.ScrollPane(block: ScrollPaneScope.() -> Unit): ScrollPaneScope {
    val scrollPane = uiNode.createChild(ScrollPaneNode::class, ScrollPaneNode.factory)
    scrollPane.block()
    return scrollPane
}

class ScrollPaneNode(parent: UiNode?, uiCtx: UiContext) : UiNode(parent, uiCtx), ScrollPaneScope {
    override val modifier = ScrollPaneModifier()

    override fun setBounds(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        val scrollX = modifier.scrollPosX.value * uiCtx.measuredScale
        val scrollY = modifier.scrollPosY.value * uiCtx.measuredScale
        super.setBounds(minX - scrollX, minY - scrollY, maxX - scrollX, maxY - scrollY)
    }

    companion object {
        val factory: (UiNode, UiContext) -> ScrollPaneNode = { parent, uiCtx -> ScrollPaneNode(parent, uiCtx) }
    }
}