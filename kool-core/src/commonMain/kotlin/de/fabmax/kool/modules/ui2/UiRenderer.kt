package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

interface UiRenderer<in T: UiNode> {
    fun renderUi(node: T)
}

class RectBackground(val backgroundColor: Color) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            surface.defaultPrimitives.localRect(0f, 0f, width, height, backgroundColor)
        }
    }
}

class RoundRectBackground(val backgroundColor: Color, val cornerRadius: Dp) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            surface.defaultPrimitives.localRoundRect(0f, 0f, width, height, cornerRadius.px, backgroundColor)
        }
    }
}

class RectBorder(val borderColor: Color, val inset: Dp, val borderWidth: Dp) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val inPx = inset.px
            surface.defaultPrimitives.localRectBorder(
                inPx, inPx, width - inPx * 2f, height - inPx * 2f, borderWidth.px, borderColor
            )
        }
    }
}

class RoundRectBorder(val borderColor: Color, val inset: Dp, val cornerRadius: Dp, val borderWidth: Dp) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val inPx = inset.px
            surface.defaultPrimitives.localRoundRectBorder(
                inPx, inPx, width - inPx * 2f, height - inPx * 2f, cornerRadius.px, borderWidth.px, borderColor
            )
        }
    }
}
