package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.modules.ui2.Layout.Companion.LAYOUT_EPS
import kotlin.math.max
import kotlin.math.round

object RowLayout : Layout {
    override fun measureContentSize(uiNode: UiNode, ctx: KoolContext) {
        HorizontalLayout.measure(uiNode, true)
    }

    override fun layoutChildren(uiNode: UiNode, ctx: KoolContext) {
        HorizontalLayout.layout(uiNode, true)
    }
}

object ReverseRowLayout : Layout {
    override fun measureContentSize(uiNode: UiNode, ctx: KoolContext) {
        HorizontalLayout.measure(uiNode, false)
    }

    override fun layoutChildren(uiNode: UiNode, ctx: KoolContext) {
        HorizontalLayout.layout(uiNode, false)
    }
}

private object HorizontalLayout {
    fun measure(uiNode: UiNode, isStartToEnd: Boolean) = uiNode.run {
        val modWidth = modifier.width
        val modHeight = modifier.height

        var measuredWidth = 0f
        var measuredHeight = 0f
        var isDynamicWidth = true
        var isDynamicHeight = true

        if (modWidth is Dp) {
            measuredWidth = modWidth.px
            isDynamicWidth = false
        }
        if (modHeight is Dp) {
            measuredHeight = modHeight.px
            isDynamicHeight = false
        }

        if (isDynamicWidth || isDynamicHeight) {
            // determine content size based on child sizes
            var prevMargin = paddingTopPx
            val indices = if (isStartToEnd) children.indices else children.indices.reversed()
            for (i in indices) {
                val child = children[i]
                if (isDynamicWidth) {
                    measuredWidth += round(child.contentWidthPx) + round(max(prevMargin, child.marginStartPx))
                    prevMargin = child.marginEndPx
                }
                if (isDynamicHeight) {
                    val pTop = max(paddingTopPx, child.marginTopPx)
                    val pBottom = max(paddingBottomPx, child.marginBottomPx)
                    measuredHeight = max(measuredHeight, round(child.contentHeightPx + pTop + pBottom))
                }
                if (i == children.lastIndex && isDynamicWidth) {
                    measuredWidth += round(max(prevMargin, paddingEndPx))
                }
            }

            if (modWidth is Grow) measuredWidth = modWidth.clampPx(measuredWidth, measuredWidth)
            if (modHeight is Grow) measuredHeight = modHeight.clampPx(measuredHeight, measuredHeight)
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    fun layout(uiNode: UiNode, isStartToEnd: Boolean) = uiNode.run {
        val growSpace = determineAvailableGrowSpace(isStartToEnd)
        var x = if (isStartToEnd) leftPx else rightPx
        var prevMargin = if (isStartToEnd) paddingStartPx else paddingEndPx

        for (i in children.indices) {
            val child = children[i]

            val growSpaceW = growSpace.x / growSpace.y
            val growSpaceH = heightPx - max(paddingTopPx, child.marginTopPx) - max(paddingBottomPx, child.marginBottomPx)
            val layoutW = round(child.computeWidthFromDimension(growSpaceW) + LAYOUT_EPS)
            val layoutH = round(child.computeHeightFromDimension(growSpaceH) + LAYOUT_EPS)
            val layoutY = round(uiNode.computeChildLocationY(child, layoutH) + LAYOUT_EPS)

            val cw = child.modifier.width
            if (cw is Grow) {
                growSpace.x -= layoutW
                growSpace.y -= cw.weight
            }

            val layoutX: Float
            if (isStartToEnd) {
                x += round(max(prevMargin, child.marginStartPx))
                prevMargin = child.marginEndPx
                layoutX = round(x + LAYOUT_EPS)
                x += layoutW
            } else {
                x -= round(max(prevMargin, child.marginEndPx))
                prevMargin = child.marginStartPx
                x -= layoutW
                layoutX = round(x + LAYOUT_EPS)
            }

            child.setBounds(layoutX, layoutY, layoutX + layoutW, layoutY + layoutH)
        }
    }

    private fun UiNode.determineAvailableGrowSpace(isStartToEnd: Boolean): MutableVec2f {
        var prevMargin = paddingStartPx
        var remainingSpace = widthPx
        var totalWeight = 0f
        val indices = if (isStartToEnd) children.indices else children.indices.reversed()
        for (i in indices) {
            val child = children[i]
            when (val childW = child.modifier.width) {
                is Dp -> remainingSpace -= childW.px
                is Grow -> totalWeight += childW.weight
                FitContent -> remainingSpace -= child.contentWidthPx
            }
            remainingSpace -= max(prevMargin, child.marginStartPx)
            prevMargin = child.marginEndPx
            if (i == uiNode.children.lastIndex) {
                remainingSpace -= max(prevMargin, uiNode.paddingEndPx)
            }
        }
        if (totalWeight == 0f) totalWeight = 1f
        return MutableVec2f(remainingSpace, totalWeight)
    }
}
