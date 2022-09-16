package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

interface UiRenderer<in T: UiNode> {
    fun renderUi(node: T)
}

class RectBackground(val backgroundColor: Color) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            surface.defaultPrimitives.localRect(0f, 0f, widthPx, heightPx, backgroundColor)
        }
    }
}

class RoundRectBackground(val backgroundColor: Color, val cornerRadius: Dp) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            surface.defaultPrimitives.localRoundRect(0f, 0f, widthPx, heightPx, cornerRadius.px, backgroundColor)
        }
    }
}

class RectBorder(val borderColor: Color, val borderWidth: Dp, val inset: Dp = Dp.ZERO) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val inPx = inset.px
            surface.defaultPrimitives.localRectBorder(
                inPx, inPx, widthPx - inPx * 2f, heightPx - inPx * 2f, borderWidth.px, borderColor
            )
        }
    }
}

class RoundRectBorder(val borderColor: Color, val cornerRadius: Dp, val borderWidth: Dp, val inset: Dp = Dp.ZERO) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val inPx = inset.px
            surface.defaultPrimitives.localRoundRectBorder(
                inPx, inPx, widthPx - inPx * 2f, heightPx - inPx * 2f, cornerRadius.px, borderWidth.px, borderColor
            )
        }
    }
}
