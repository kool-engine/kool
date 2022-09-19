package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color
import kotlin.math.min
import kotlin.math.round

interface UiRenderer<in T: UiNode> {
    fun renderUi(node: T)
}

class RectBackground(val backgroundColor: Color) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            surface.getUiPrimitives().localRect(0f, 0f, widthPx, heightPx, backgroundColor)
        }
    }
}

class RoundRectBackground(val backgroundColor: Color, val cornerRadius: Dp) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            surface.getUiPrimitives().localRoundRect(0f, 0f, widthPx, heightPx, cornerRadius.px, backgroundColor)
        }
    }
}

class CircularBackground(val backgroundColor: Color) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            surface.getUiPrimitives().localCircle(widthPx * 0.5f, heightPx * 0.5f, min(widthPx, heightPx) * 0.5f, backgroundColor)
        }
    }
}

class RectBorder(val borderColor: Color, val borderWidth: Dp, val inset: Dp = Dp.ZERO) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val inPx = inset.px
            surface.getUiPrimitives().localRectBorder(
                inPx, inPx, widthPx - inPx * 2f, heightPx - inPx * 2f, borderWidth.px, borderColor
            )
        }
    }
}

class RoundRectBorder(val borderColor: Color, val cornerRadius: Dp, val borderWidth: Dp, val inset: Dp = Dp.ZERO) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val inPx = round(inset.px)
            val bw = round(borderWidth.px)
            surface.getUiPrimitives().localRoundRectBorder(
                inPx, inPx, widthPx - inPx * 2f, heightPx - inPx * 2f, cornerRadius.px, bw, borderColor
            )
        }
    }
}
