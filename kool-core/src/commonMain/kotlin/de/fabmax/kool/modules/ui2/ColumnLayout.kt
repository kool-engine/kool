package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
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

        // if width / height is Grow, content size will remain 0
        var measuredWidth = if (modWidth is Dp) modWidth.px else 0f
        var measuredHeight = if (modHeight is Dp) modHeight.px else 0f
        val isDynamicWidth = modWidth === WrapContent
        val isDynamicHeight = modHeight === WrapContent

        if (isDynamicWidth || isDynamicHeight) {
            // determine content size based on child sizes
            var prevMargin = paddingTopPx
            val indices = if (isTopToBottom) children.indices else children.indices.reversed()
            for (i in indices) {
                val child = children[i]
                if (isDynamicWidth) {
                    val pStart = max(paddingStartPx, child.marginStartPx)
                    val pEnd = max(paddingEndPx, child.marginEndPx)
                    measuredWidth = max(measuredWidth, child.contentWidthPx + pStart + pEnd)
                }
                if (isDynamicHeight) {
                    measuredHeight += child.contentHeightPx + max(prevMargin, child.marginTopPx)
                    prevMargin = child.marginBottomPx
                }
                if (i == children.lastIndex && isDynamicHeight) {
                    measuredHeight += max(prevMargin, paddingBottomPx)
                }
            }
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    fun layout(uiNode: UiNode, isTopToBottom: Boolean) = uiNode.run {
        val growSpace = determineAvailableGrowSpace(isTopToBottom)
        var y = if (isTopToBottom) topPx else bottomPx
        var prevMargin = if (isTopToBottom) paddingTopPx else paddingBottomPx

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
            val layoutX = when (child.modifier.alignX) {
                AlignmentX.Start -> leftPx + max(paddingStartPx, child.marginStartPx)
                AlignmentX.Center -> leftPx + (widthPx - layoutW) * 0.5f
                AlignmentX.End -> rightPx - layoutW - max(paddingEndPx, child.marginEndPx)
            }

            val layoutY: Float
            if (isTopToBottom) {
                y += max(prevMargin, child.marginTopPx)
                prevMargin = child.marginBottomPx
                layoutY = y
                y += layoutH
            } else {
                y -= max(prevMargin, child.marginBottomPx)
                prevMargin = child.marginTopPx
                y -= layoutH
                layoutY = y
            }

            child.setBounds(round(layoutX), round(layoutY), round(layoutX + layoutW), round(layoutY + layoutH))
        }
    }

    private fun UiNode.determineAvailableGrowSpace(isTopToBottom: Boolean): Float {
        var prevMargin = paddingTopPx
        var remainingSpace = heightPx
        var totalWeight = 0f
        val indices = if (isTopToBottom) children.indices else children.indices.reversed()
        for (i in indices) {
            val child = children[i]
            when (val childH = child.modifier.height) {
                is Dp -> remainingSpace -= childH.px
                is Grow -> totalWeight += childH.weight
                WrapContent -> remainingSpace -= child.contentHeightPx
            }
            remainingSpace -= max(prevMargin, child.marginTopPx)
            prevMargin = child.marginBottomPx
            if (i == uiNode.children.lastIndex) {
                remainingSpace -= max(prevMargin, uiNode.paddingBottomPx)
            }
        }
        if (totalWeight == 0f) totalWeight = 1f
        return remainingSpace / totalWeight
    }
}
