package de.fabmax.kool.modules.ui2

import kotlin.math.max
import kotlin.math.round

object CellLayout {
    fun measureContentSize(uiNode: UiNode) {
        uiNode.apply {
            var measuredWidth = 0f
            var measuredHeight = 0f

            val modWidth = modifier.width
            val modHeight = modifier.height
            val isFixedWidth: Boolean
            val isFixedHeight: Boolean

            if (modWidth is Dp) {
                measuredWidth = modWidth.value * surface.measuredScale
                isFixedWidth = true
            } else {
                isFixedWidth = false
            }
            if (modHeight is Dp) {
                measuredHeight = modHeight.value * surface.measuredScale
                isFixedHeight = true
            } else {
                isFixedHeight = false
            }

            if (!isFixedWidth || !isFixedHeight) {
                for (i in children.indices) {
                    val child = children[i]
                    if (!isFixedWidth) {
                        val pStart = max(paddingStart, child.marginStart)
                        val pEnd = max(paddingEnd, child.marginEnd)
                        measuredWidth = max(measuredWidth, child.contentWidth + pStart + pEnd)
                    }
                    if (!isFixedHeight) {
                        val pTop = max(paddingTop, child.marginTop)
                        val pBottom = max(paddingBottom, child.marginBottom)
                        measuredHeight = max(measuredHeight, child.contentHeight + pTop + pBottom)
                    }
                }
            }
            setContentSize(measuredWidth, measuredHeight)
        }
    }

    fun layoutChildren(uiNode: UiNode) {
        uiNode.apply {
            children.forEach { child ->
                val childWidth = when (val w = child.modifier.width) {
                    is Dp -> w.value * surface.measuredScale
                    is Grow -> width - max(paddingStart, child.marginStart) - max(paddingEnd, child.marginEnd)
                    WrapContent -> child.contentWidth
                }
                val childHeight = when (val h = child.modifier.height) {
                    is Dp -> h.value * surface.measuredScale
                    is Grow -> height - max(paddingTop, child.marginTop) - max(paddingBottom, child.marginBottom)
                    WrapContent -> child.contentHeight
                }

                val childX = minX + when (child.modifier.alignX) {
                    AlignmentX.Start -> max(paddingStart, child.marginStart)
                    AlignmentX.Center -> (width - childWidth) * 0.5f
                    AlignmentX.End -> width - childWidth - max(paddingEnd, child.marginEnd)
                }
                val childY = minY + when (child.modifier.alignY) {
                    AlignmentY.Top -> max(paddingTop, child.marginTop)
                    AlignmentY.Center -> (height - childHeight) * 0.5f
                    AlignmentY.Bottom -> height - childHeight - max(paddingBottom, child.marginBottom)
                }

                child.setBounds(round(childX), round(childY), round(childX + childWidth), round(childY + childHeight))
            }
        }
    }
}