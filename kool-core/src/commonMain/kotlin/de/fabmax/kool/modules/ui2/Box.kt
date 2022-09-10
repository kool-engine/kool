package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext

interface BoxScope : UiScope {
    override val modifier: BoxModifier
}

open class BoxModifier : UiModifier() {
    var layoutDirection = LayoutDirection.TopToBottom

    override fun resetDefaults() {
        super.resetDefaults()
        layoutDirection = LayoutDirection.TopToBottom
    }
}

fun <T: BoxModifier> T.layoutDirection(direction: LayoutDirection): T { layoutDirection = direction; return this }

enum class LayoutDirection(val isVertical: Boolean) {
    StartToEnd(false),
    EndToStart(false),
    TopToBottom(true),
    BottomToTop(true),
}

inline fun UiScope.Row(block: BoxScope.() -> Unit) = Box(LayoutDirection.StartToEnd, block)

inline fun UiScope.Column(block: BoxScope.() -> Unit) = Box(LayoutDirection.TopToBottom, block)

inline fun UiScope.Box(layoutDirection: LayoutDirection, block: BoxScope.() -> Unit): BoxScope {
    val box = uiNode.createChild(BoxNode::class, BoxNode.factory)
    box.modifier.layoutDirection(layoutDirection)
    box.block()
    return box
}

open class BoxNode(parent: UiNode?, uiCtx: UiContext) : UiNode(parent, uiCtx), BoxScope {
    override val modifier = BoxModifier()

    override fun measureContentSize(ctx: KoolContext) {
        BoxLayout.measureContentSize(this, modifier.layoutDirection)
    }

    override fun layoutChildren(ctx: KoolContext) {
        BoxLayout.layoutChildren(this, modifier.layoutDirection)
    }

    companion object {
        val factory: (UiNode, UiContext) -> BoxNode = { parent, uiCtx -> BoxNode(parent, uiCtx) }
    }
}
