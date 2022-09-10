package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext

interface BoxScope : UiScope {
    override val modifier: BoxModifier

    fun Row(block: BoxScope.() -> Unit)
    fun Column(block: BoxScope.() -> Unit)
    fun Box(layoutDirection: LayoutDirection, block: BoxScope.() -> Unit)
}

open class BoxModifier : UiModifier() {
    var layoutDirection = LayoutDirection.TopToBottom
}

fun <T: BoxModifier> T.layoutDirection(direction: LayoutDirection): T { layoutDirection = direction; return this }

enum class LayoutDirection(val isVertical: Boolean) {
    StartToEnd(false),
    EndToStart(false),
    TopToBottom(true),
    BottomToTop(true),
}

class BoxNode(parent: UiNode?, uiCtx: UiContext) : UiNode(parent, uiCtx), BoxScope {
    override val modifier = BoxModifier()

    override fun Row(block: BoxScope.() -> Unit) = Box(LayoutDirection.StartToEnd, block)

    override fun Column(block: BoxScope.() -> Unit) = Box(LayoutDirection.TopToBottom, block)

    override fun Box(layoutDirection: LayoutDirection, block: BoxScope.() -> Unit) {
        val childBox = BoxNode(this, uiCtx)
        children += childBox
        childBox.modifier.layoutDirection(layoutDirection)
        childBox.block()
    }

    override fun measureContentSize(ctx: KoolContext) {
        BoxLayout.measureContentSize(this, modifier.layoutDirection)
    }

    override fun layoutChildren(ctx: KoolContext) {
        BoxLayout.layoutChildren(this, modifier.layoutDirection)
    }
}
