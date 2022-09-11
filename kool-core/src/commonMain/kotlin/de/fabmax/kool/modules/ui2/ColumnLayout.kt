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
        var measuredWidth = if (modWidth is Dp) modWidth.value * surface.measuredScale else 0f
        var measuredHeight = if (modHeight is Dp) modHeight.value * surface.measuredScale else 0f
        val isDynamicWidth = modWidth === WrapContent
        val isDynamicHeight = modHeight === WrapContent

        if (isDynamicWidth || isDynamicHeight) {
            // determine content size based on child sizes
            var prevMargin = paddingTop
            val indices = if (isTopToBottom) children.indices else children.indices.reversed()
            for (i in indices) {
                val child = children[i]
                if (isDynamicWidth) {
                    val pStart = max(paddingStart, child.marginStart)
                    val pEnd = max(paddingEnd, child.marginEnd)
                    measuredWidth = max(measuredWidth, child.contentWidth + pStart + pEnd)
                }
                if (isDynamicHeight) {
                    measuredHeight += child.contentHeight + max(prevMargin, child.marginTop)
                    prevMargin = child.marginBottom
                }
                if (i == children.lastIndex && isDynamicHeight) {
                    measuredHeight += max(prevMargin, paddingBottom)
                }
            }
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    fun layout(uiNode: UiNode, isTopToBottom: Boolean) = uiNode.run {
        val growSpace = determineAvailableGrowSpace(isTopToBottom)
        var y = if (isTopToBottom) minY else maxY
        var prevMargin = if (isTopToBottom) paddingTop else paddingBottom

        for (i in children.indices) {
            val child = children[i]

            val layoutW = when (val childW = child.modifier.width) {
                is Dp -> childW.value * surface.measuredScale
                is Grow -> width - max(paddingStart, child.marginStart) - max(paddingEnd, child.marginEnd)
                WrapContent -> child.contentWidth
            }
            val layoutH = when (val childH = child.modifier.height) {
                is Dp -> childH.value * surface.measuredScale
                is Grow -> growSpace * childH.weight
                WrapContent -> child.contentHeight
            }
            val layoutX = when (child.modifier.alignX) {
                AlignmentX.Start -> minX + max(paddingStart, child.marginStart)
                AlignmentX.Center -> minX + (width - layoutW) * 0.5f
                AlignmentX.End -> maxX - layoutW - max(paddingEnd, child.marginEnd)
            }

            val layoutY: Float
            if (isTopToBottom) {
                y += max(prevMargin, child.marginTop)
                prevMargin = child.marginBottom
                layoutY = y
                y += layoutH
            } else {
                y -= max(prevMargin, child.marginBottom)
                prevMargin = child.marginTop
                y -= layoutH
                layoutY = y
            }

            child.setBounds(round(layoutX), round(layoutY), round(layoutX + layoutW), round(layoutY + layoutH))
        }
    }

    private fun UiNode.determineAvailableGrowSpace(isTopToBottom: Boolean): Float {
        var prevMargin = paddingTop
        var remainingSpace = height
        var totalWeight = 0f
        val indices = if (isTopToBottom) children.indices else children.indices.reversed()
        for (i in indices) {
            val child = children[i]
            when (val childH = child.modifier.height) {
                is Dp -> remainingSpace -= childH.value * uiNode.surface.measuredScale
                is Grow -> totalWeight += childH.weight
                WrapContent -> remainingSpace -= child.contentHeight
            }
            remainingSpace -= max(prevMargin, child.marginTop)
            prevMargin = child.marginBottom
            if (i == uiNode.children.lastIndex) {
                remainingSpace -= max(prevMargin, uiNode.paddingBottom)
            }
        }
        if (totalWeight == 0f) totalWeight = 1f
        return remainingSpace / totalWeight
    }
}
