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
            var prevMargin = paddingTop
            val indices = if (isStartToEnd) children.indices else children.indices.reversed()
            for (i in indices) {
                val child = children[i]
                if (isDynamicWidth) {
                    measuredWidth += child.contentWidth + max(prevMargin, child.marginStart)
                    prevMargin = child.marginEnd
                }
                if (isDynamicHeight) {
                    val pTop = max(paddingTop, child.marginTop)
                    val pBottom = max(paddingBottom, child.marginBottom)
                    measuredHeight = max(measuredHeight, child.contentHeight + pTop + pBottom)
                }
                if (i == children.lastIndex && isDynamicWidth) {
                    measuredWidth += max(prevMargin, paddingEnd)
                }
            }
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    fun layout(uiNode: UiNode, isStartToEnd: Boolean) = uiNode.run {
        val growSpace = determineAvailableGrowSpace(isStartToEnd)
        var x = if (isStartToEnd) minX else maxX
        var prevMargin = if (isStartToEnd) paddingStart else paddingEnd

        for (i in children.indices) {
            val child = children[i]

            val layoutW = when (val childW = child.modifier.width) {
                is Dp -> childW.px
                is Grow -> width - max(paddingStart, child.marginStart) - max(paddingEnd, child.marginEnd)
                WrapContent -> child.contentWidth
            }
            val layoutH = when (val childH = child.modifier.height) {
                is Dp -> childH.px
                is Grow -> growSpace * childH.weight
                WrapContent -> child.contentHeight
            }
            val layoutY = when (child.modifier.alignY) {
                AlignmentY.Top -> minY + max(paddingTop, child.marginTop)
                AlignmentY.Center -> minY + (height - layoutH) * 0.5f
                AlignmentY.Bottom -> maxY - layoutH - max(paddingBottom, child.marginBottom)
            }

            val layoutX: Float
            if (isStartToEnd) {
                x += max(prevMargin, child.marginStart)
                prevMargin = child.marginEnd
                layoutX = x
                x += layoutW
            } else {
                x -= max(prevMargin, child.marginEnd)
                prevMargin = child.marginStart
                x -= layoutW
                layoutX = x
            }

            child.setBounds(round(layoutX), round(layoutY), round(layoutX + layoutW), round(layoutY + layoutH))
        }
    }

    private fun UiNode.determineAvailableGrowSpace(isStartToEnd: Boolean): Float {
        var prevMargin = paddingStart
        var remainingSpace = width
        var totalWeight = 0f
        val indices = if (isStartToEnd) children.indices else children.indices.reversed()
        for (i in indices) {
            val child = children[i]
            when (val childW = child.modifier.width) {
                is Dp -> remainingSpace -= childW.px
                is Grow -> totalWeight += childW.weight
                WrapContent -> remainingSpace -= child.contentHeight
            }
            remainingSpace -= max(prevMargin, child.marginStart)
            prevMargin = child.marginEnd
            if (i == uiNode.children.lastIndex) {
                remainingSpace -= max(prevMargin, uiNode.paddingEnd)
            }
        }
        if (totalWeight == 0f) totalWeight = 1f
        return remainingSpace / totalWeight
    }
}
