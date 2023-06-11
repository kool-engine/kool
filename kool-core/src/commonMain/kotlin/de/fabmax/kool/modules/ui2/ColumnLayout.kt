package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.modules.ui2.Layout.Companion.LAYOUT_EPS
import kotlin.math.max
import kotlin.math.round

object ColumnLayout : Layout {
    override fun measureContentSize(uiNode: UiNode, ctx: KoolContext) {
        VerticalLayout.measure(uiNode, true)
    }

    override fun layoutChildren(uiNode: UiNode, ctx: KoolContext) {
        VerticalLayout.layout(uiNode, true)
    }
}

object ReverseColumnLayout : Layout {
    override fun measureContentSize(uiNode: UiNode, ctx: KoolContext) {
        VerticalLayout.measure(uiNode, false)
    }

    override fun layoutChildren(uiNode: UiNode, ctx: KoolContext) {
        VerticalLayout.layout(uiNode, false)
    }
}

private object VerticalLayout {
    fun measure(uiNode: UiNode, isTopToBottom: Boolean) = uiNode.run {
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
            val indices = if (isTopToBottom) children.indices else children.indices.reversed()
            for (i in indices) {
                val child = children[i]
                if (isDynamicWidth) {
                    val pStart = max(paddingStartPx, child.marginStartPx)
                    val pEnd = max(paddingEndPx, child.marginEndPx)
                    measuredWidth = max(measuredWidth, round(child.contentWidthPx + pStart + pEnd))
                }
                if (isDynamicHeight) {
                    measuredHeight += round(child.contentHeightPx) + round(max(prevMargin, child.marginTopPx))
                    prevMargin = child.marginBottomPx
                }
                if (i == children.lastIndex && isDynamicHeight) {
                    measuredHeight += round(max(prevMargin, paddingBottomPx))
                }
            }

            if (modWidth is Grow) measuredWidth = modWidth.clampPx(measuredWidth, measuredWidth)
            if (modHeight is Grow) measuredHeight = modHeight.clampPx(measuredHeight, measuredHeight)
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    fun layout(uiNode: UiNode, isTopToBottom: Boolean) = uiNode.run {
        val growSpace = determineAvailableGrowSpace(isTopToBottom)
        var y = if (isTopToBottom) topPx else bottomPx
        var prevMargin = if (isTopToBottom) paddingTopPx else paddingBottomPx

        for (i in children.indices) {
            val child = children[i]

            val growSpaceH = growSpace.x / growSpace.y
            val growSpaceW = widthPx - max(paddingStartPx, child.marginStartPx) - max(paddingEndPx, child.marginEndPx)
            val layoutW = round(child.computeWidthFromDimension(growSpaceW) + LAYOUT_EPS)
            val layoutH = round(child.computeHeightFromDimension(growSpaceH) + LAYOUT_EPS)
            val layoutX = round(uiNode.computeChildLocationX(child, layoutW) + LAYOUT_EPS)

            val ch = child.modifier.height
            if (ch is Grow) {
                growSpace.x -= layoutH
                growSpace.y -= ch.weight
            }

            val layoutY: Float
            if (isTopToBottom) {
                y += round(max(prevMargin, child.marginTopPx))
                prevMargin = child.marginBottomPx
                layoutY = round(y + LAYOUT_EPS)
                y += layoutH
            } else {
                y -= round(max(prevMargin, child.marginBottomPx))
                prevMargin = child.marginTopPx
                y -= layoutH
                layoutY = round(y + LAYOUT_EPS)
            }

            child.setBounds(layoutX, layoutY, layoutX + layoutW, layoutY + layoutH)
        }
    }

    private fun UiNode.determineAvailableGrowSpace(isTopToBottom: Boolean): MutableVec2f {
        var prevMargin = paddingTopPx
        var remainingSpace = heightPx
        var totalWeight = 0f
        val indices = if (isTopToBottom) children.indices else children.indices.reversed()
        for (i in indices) {
            val child = children[i]
            when (val childH = child.modifier.height) {
                is Dp -> remainingSpace -= childH.px
                is Grow -> totalWeight += childH.weight
                FitContent -> remainingSpace -= child.contentHeightPx
            }
            remainingSpace -= max(prevMargin, child.marginTopPx)
            prevMargin = child.marginBottomPx
            if (i == uiNode.children.lastIndex) {
                remainingSpace -= max(prevMargin, uiNode.paddingBottomPx)
            }
        }
        if (totalWeight == 0f) totalWeight = 1f
        return MutableVec2f(remainingSpace, totalWeight)
    }
}
