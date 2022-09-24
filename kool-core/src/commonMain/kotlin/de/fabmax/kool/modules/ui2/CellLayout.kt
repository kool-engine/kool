package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import kotlin.math.max
import kotlin.math.round

object CellLayout : Layout {
    override fun measureContentSize(uiNode: UiNode, ctx: KoolContext) = uiNode.run {
        val modWidth = modifier.width
        val modHeight = modifier.height

        // if width / height is Grow, content size will remain 0
        var measuredWidth = if (modWidth is Dp) modWidth.px else 0f
        var measuredHeight = if (modHeight is Dp) modHeight.px else 0f
        val isDynamicWidth = modWidth === WrapContent
        val isDynamicHeight = modHeight === WrapContent

        if (isDynamicWidth || isDynamicHeight) {
            for (i in children.indices) {
                val child = children[i]
                if (isDynamicWidth) {
                    val pStart = max(paddingStartPx, child.marginStartPx)
                    val pEnd = max(paddingEndPx, child.marginEndPx)
                    measuredWidth = max(measuredWidth, child.contentWidthPx + pStart + pEnd)
                }
                if (isDynamicHeight) {
                    val pTop = max(paddingTopPx, child.marginTopPx)
                    val pBottom = max(paddingBottomPx, child.marginBottomPx)
                    measuredHeight = max(measuredHeight, child.contentHeightPx + pTop + pBottom)
                }
            }
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun layoutChildren(uiNode: UiNode, ctx: KoolContext) {
        uiNode.apply {
            children.forEach { child ->
                val growSpaceW = widthPx - max(paddingStartPx, child.marginStartPx) - max(paddingEndPx, child.marginEndPx)
                val growSpaceH = heightPx - max(paddingTopPx, child.marginTopPx) - max(paddingBottomPx, child.marginBottomPx)

                val childWidth = child.computeWidthFromDimension(growSpaceW)
                val childHeight = child.computeHeightFromDimension(growSpaceH)
                val childX = computeChildLocationX(child, childWidth)
                val childY = computeChildLocationY(child, childHeight)

                child.setBounds(round(childX), round(childY), round(childX + childWidth), round(childY + childHeight))
            }
        }
    }
}