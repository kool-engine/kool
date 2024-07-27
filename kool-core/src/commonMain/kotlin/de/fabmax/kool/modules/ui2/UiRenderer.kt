package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

interface UiRenderer<in T: UiNode> {
    fun renderUi(node: T)
}

fun UiRenderer(renderUi: (UiNode) -> Unit) = object : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        renderUi(node)
    }
}

class RectBackground(val backgroundColor: Color) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val lt = max(leftPx, clipLeftPx)
            val rt = min(rightPx, clipRightPx)
            val tp = max(topPx, clipTopPx)
            val bt = min(bottomPx, clipBottomPx)

            node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                .rect(lt, tp, rt - lt, bt - tp, clipBoundsPx, backgroundColor)
        }
    }
}

class RoundRectBackground(val backgroundColor: Color, val cornerRadius: Dp) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val c = cornerRadius.px
            val lt = max(leftPx, clipLeftPx - c)
            val rt = min(rightPx, clipRightPx + c)
            val tp = max(topPx, clipTopPx - c)
            val bt = min(bottomPx, clipBottomPx + c)

            node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                .roundRect(lt, tp, rt - lt, bt - tp, c, clipBoundsPx, backgroundColor)
        }
    }
}

class CircularBackground(val backgroundColor: Color) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                .localCircle(widthPx * 0.5f, heightPx * 0.5f, min(widthPx, heightPx) * 0.5f, backgroundColor)
        }
    }
}

class RectGradientBackground(
    val colorA: Color, val colorB: Color,
    val gradientCx: Dp, val gradientCy: Dp,
    val gradientRx: Dp, val gradientRy: Dp
) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                .localRectGradient(0f, 0f, widthPx, heightPx, colorA, colorB, gradientCx.px, gradientCy.px, gradientRx.px, gradientRy.px)
        }
    }
}

class RoundRectGradientBackground(
    val cornerRadius: Dp, val colorA: Color, val colorB: Color,
    val gradientCx: Dp, val gradientCy: Dp,
    val gradientRx: Dp, val gradientRy: Dp
) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                .localRoundRectGradient(0f, 0f, widthPx, heightPx, cornerRadius.px, colorA, colorB, gradientCx.px, gradientCy.px, gradientRx.px, gradientRy.px)
        }
    }
}

open class RectBorder(val borderColor: Color, val borderWidth: Dp, val inset: Dp = Dp.ZERO) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val inPx = inset.px
            node.getUiPrimitives().localRectBorder(
                inPx, inPx, widthPx - inPx * 2f, heightPx - inPx * 2f, borderWidth.px, borderColor
            )
        }
    }
}

object DebugBorder : RectBorder(Color.RED, Dp(1f))

class RoundRectBorder(val borderColor: Color, val cornerRadius: Dp, val borderWidth: Dp, val inset: Dp = Dp.ZERO) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val inPx = round(inset.px)
            val bw = round(borderWidth.px)
            node.getUiPrimitives().localRoundRectBorder(
                inPx, inPx, widthPx - inPx * 2f, heightPx - inPx * 2f, cornerRadius.px, bw, borderColor
            )
        }
    }
}

class CircularBorder(val borderColor: Color, val borderWidth: Dp, val inset: Dp = Dp.ZERO) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val bw = round(borderWidth.px)
            val x = widthPx * 0.5f
            val y = heightPx * 0.5f
            val r = min(x, y) - round(inset.px)
            node.getUiPrimitives().localCircleBorder(x, y, r, bw, borderColor)
        }
    }
}
