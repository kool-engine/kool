package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
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

        // if width / height is Grow, content size will remain 0
        var measuredWidth = if (modWidth is Dp) modWidth.px else 0f
        var measuredHeight = if (modHeight is Dp) modHeight.px else 0f
        val isDynamicWidth = modWidth === WrapContent
        val isDynamicHeight = modHeight === WrapContent

        if (isDynamicWidth || isDynamicHeight) {
            // determine content size based on child sizes
            var prevMargin = paddingTopPx
            val indices = if (isStartToEnd) children.indices else children.indices.reversed()
            for (i in indices) {
                val child = children[i]
                if (isDynamicWidth) {
                    measuredWidth += child.contentWidthPx + max(prevMargin, child.marginStartPx)
                    prevMargin = child.marginEndPx
                }
                if (isDynamicHeight) {
                    val pTop = max(paddingTopPx, child.marginTopPx)
                    val pBottom = max(paddingBottomPx, child.marginBottomPx)
                    measuredHeight = max(measuredHeight, child.contentHeightPx + pTop + pBottom)
                }
                if (i == children.lastIndex && isDynamicWidth) {
                    measuredWidth += max(prevMargin, paddingEndPx)
                }
            }
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    fun layout(uiNode: UiNode, isStartToEnd: Boolean) = uiNode.run {
        val growSpace = determineAvailableGrowSpace(isStartToEnd)
        var x = if (isStartToEnd) leftPx else rightPx
        var prevMargin = if (isStartToEnd) paddingStartPx else paddingEndPx

        for (i in children.indices) {
            val child = children[i]

            val layoutW = when (val childW = child.modifier.width) {
                is Dp -> childW.px
                is Grow -> widthPx - max(paddingStartPx, child.marginStartPx) - max(paddingEndPx, child.marginEndPx)
                WrapContent -> child.contentWidthPx
            }
            val layoutH = when (val childH = child.modifier.height) {
                is Dp -> childH.px
                is Grow -> growSpace * childH.weight
                WrapContent -> child.contentHeightPx
            }
            val layoutY = when (child.modifier.alignY) {
                AlignmentY.Top -> topPx + max(paddingTopPx, child.marginTopPx)
                AlignmentY.Center -> topPx + (heightPx - layoutH) * 0.5f
                AlignmentY.Bottom -> bottomPx - layoutH - max(paddingBottomPx, child.marginBottomPx)
            }

            val layoutX: Float
            if (isStartToEnd) {
                x += max(prevMargin, child.marginStartPx)
                prevMargin = child.marginEndPx
                layoutX = x
                x += layoutW
            } else {
                x -= max(prevMargin, child.marginEndPx)
                prevMargin = child.marginStartPx
                x -= layoutW
                layoutX = x
            }

            child.setBounds(round(layoutX), round(layoutY), round(layoutX + layoutW), round(layoutY + layoutH))
        }
    }

    private fun UiNode.determineAvailableGrowSpace(isStartToEnd: Boolean): Float {
        var prevMargin = paddingStartPx
        var remainingSpace = widthPx
        var totalWeight = 0f
        val indices = if (isStartToEnd) children.indices else children.indices.reversed()
        for (i in indices) {
            val child = children[i]
            when (val childW = child.modifier.width) {
                is Dp -> remainingSpace -= childW.px
                is Grow -> totalWeight += childW.weight
                WrapContent -> remainingSpace -= child.contentHeightPx
            }
            remainingSpace -= max(prevMargin, child.marginStartPx)
            prevMargin = child.marginEndPx
            if (i == uiNode.children.lastIndex) {
                remainingSpace -= max(prevMargin, uiNode.paddingEndPx)
            }
        }
        if (totalWeight == 0f) totalWeight = 1f
        return remainingSpace / totalWeight
    }
}
