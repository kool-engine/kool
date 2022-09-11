package de.fabmax.kool.modules.ui2

import kotlin.math.max
import kotlin.math.round

object BoxLayout {

    // caution: this class contains optimized (i.e. terrible) code!

    fun measureContentSize(uiNode: UiNode, layoutDirection: LayoutDirection) {
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
                var prevMargin = when (layoutDirection) {
                    LayoutDirection.StartToEnd -> paddingStart
                    LayoutDirection.EndToStart -> paddingTop
                    LayoutDirection.TopToBottom -> paddingEnd
                    LayoutDirection.BottomToTop -> paddingBottom
                }

                for (i in uiNode.children.indices) {
                    val child = uiNode.children[i]
                    val childW = child.contentWidth
                    val childH = child.contentHeight

                    when (layoutDirection) {
                        LayoutDirection.StartToEnd -> {
                            if (!isFixedHeight) {
                                val pTop = max(paddingTop, child.marginTop)
                                val pBottom = max(paddingBottom, child.marginBottom)
                                measuredHeight = max(measuredHeight, childH + pTop + pBottom)
                            }
                            if (!isFixedWidth) {
                                measuredWidth += childW + max(prevMargin, child.marginStart)
                                prevMargin = child.marginEnd
                            }
                        }
                        LayoutDirection.EndToStart -> {
                            if (!isFixedHeight) {
                                val pTop = max(paddingTop, child.marginTop)
                                val pBottom = max(paddingBottom, child.marginBottom)
                                measuredHeight = max(measuredHeight, childH + pTop + pBottom)
                            }
                            if (!isFixedWidth) {
                                measuredWidth += childW + max(prevMargin, child.marginEnd)
                                prevMargin = child.marginStart
                            }
                        }
                        LayoutDirection.TopToBottom -> {
                            if (!isFixedWidth) {
                                val pStart = max(paddingStart, child.marginStart)
                                val pEnd = max(paddingEnd, child.marginEnd)
                                measuredWidth = max(measuredWidth, childW + pStart + pEnd)
                            }
                            if (!isFixedHeight) {
                                measuredHeight += childH + max(prevMargin, child.marginTop)
                                prevMargin = child.marginBottom
                            }
                        }
                        LayoutDirection.BottomToTop -> {
                            if (!isFixedWidth) {
                                val pStart = max(paddingStart, child.marginStart)
                                val pEnd = max(paddingEnd, child.marginEnd)
                                measuredWidth = max(measuredWidth, childW + pStart + pEnd)
                            }
                            if (!isFixedHeight) {
                                measuredHeight += childH + max(prevMargin, child.marginBottom)
                                prevMargin = child.marginTop
                            }
                        }
                    }

                    if (i == uiNode.children.lastIndex) {
                        when {
                            layoutDirection == LayoutDirection.StartToEnd && !isFixedWidth ->
                                measuredWidth += max(prevMargin, paddingEnd)
                            layoutDirection == LayoutDirection.EndToStart && !isFixedWidth ->
                                measuredWidth += max(prevMargin, paddingStart)
                            layoutDirection == LayoutDirection.TopToBottom && !isFixedHeight ->
                                measuredHeight += max(prevMargin, paddingBottom)
                            layoutDirection == LayoutDirection.BottomToTop && !isFixedHeight ->
                                measuredHeight += max(prevMargin, paddingTop)
                        }
                    }
                }
            }
            setContentSize(measuredWidth, measuredHeight)
        }
    }

    fun layoutChildren(uiNode: UiNode, layoutDirection: LayoutDirection) {
        var prevMargin = when (layoutDirection) {
            LayoutDirection.StartToEnd -> uiNode.paddingStart
            LayoutDirection.EndToStart -> uiNode.paddingEnd
            LayoutDirection.TopToBottom -> uiNode.paddingTop
            LayoutDirection.BottomToTop -> uiNode.paddingBottom
        }

        var remainingSpace = if (layoutDirection.isVertical) uiNode.height else uiNode.width
        var totalWeight = 0f

        for (i in uiNode.children.indices) {
            val child = uiNode.children[i]
            val childW = child.modifier.width
            val childH = child.modifier.height

            if (layoutDirection.isVertical) {
                when (childH) {
                    is Dp -> remainingSpace -= childH.value * uiNode.surface.measuredScale
                    is Grow -> totalWeight += childH.weight
                    WrapContent -> remainingSpace -= child.contentHeight
                }
                if (layoutDirection == LayoutDirection.TopToBottom) {
                    remainingSpace -= max(prevMargin, child.marginTop)
                    prevMargin = child.marginBottom
                    if (i == uiNode.children.lastIndex) {
                        remainingSpace -= max(prevMargin, uiNode.paddingBottom)
                    }
                } else {
                    remainingSpace -= max(prevMargin, child.marginBottom)
                    prevMargin = child.marginTop
                    if (i == uiNode.children.lastIndex) {
                        remainingSpace -= max(prevMargin, uiNode.paddingTop)
                    }
                }

            } else if (!layoutDirection.isVertical) {
                when (childW) {
                    is Dp -> remainingSpace -= childW.value * uiNode.surface.measuredScale
                    is Grow -> totalWeight += childW.weight
                    WrapContent -> remainingSpace -= child.contentWidth
                }
                if (layoutDirection == LayoutDirection.StartToEnd) {
                    remainingSpace -= max(prevMargin, child.marginStart)
                    prevMargin = child.marginEnd
                    if (i == uiNode.children.lastIndex) {
                        remainingSpace -= max(prevMargin, uiNode.paddingEnd)
                    }
                } else {
                    remainingSpace -= max(prevMargin, child.marginEnd)
                    prevMargin = child.marginStart
                    if (i == uiNode.children.lastIndex) {
                        remainingSpace -= max(prevMargin, uiNode.paddingStart)
                    }
                }
            }
        }

        if (totalWeight == 0f) {
            totalWeight = 1f
        }

        val relSpace = remainingSpace / totalWeight
        when (layoutDirection) {
            LayoutDirection.StartToEnd -> uiNode.layoutHorizontally(relSpace, true)
            LayoutDirection.EndToStart -> uiNode.layoutHorizontally(relSpace, false)
            LayoutDirection.TopToBottom -> uiNode.layoutVertically(relSpace, true)
            LayoutDirection.BottomToTop -> uiNode.layoutVertically(relSpace, false)
        }
    }

    private fun UiNode.layoutVertically(relativeSpace: Float, topToBottom: Boolean) {
        var prevMargin = if (topToBottom) paddingTop else paddingBottom
        var y = if (topToBottom) minY else maxY

        for (i in children.indices) {
            val child = children[i]
            val childW = child.modifier.width
            val childH = child.modifier.height

            val layoutH = when (childH) {
                is Dp -> childH.value * surface.measuredScale
                is Grow -> max(relativeSpace * childH.weight, child.contentHeight)
                WrapContent -> child.contentHeight
            }

            val layoutY: Float
            if (topToBottom) {
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

            val layoutW = when (childW) {
                is Dp -> childW.value * surface.measuredScale
                is Grow -> width - max(paddingStart, child.marginStart) - max(paddingEnd, child.marginEnd)
                WrapContent -> child.contentWidth
            }
            val layoutX = when (child.modifier.alignX) {
                AlignmentX.Start -> minX + max(paddingStart, child.marginStart)
                AlignmentX.Center -> minX + (width - layoutW) * 0.5f
                AlignmentX.End -> maxX - layoutW - max(paddingEnd, child.marginEnd)
            }

            child.setBounds(round(layoutX), round(layoutY), round(layoutX + layoutW), round(layoutY + layoutH))
        }
    }

    private fun UiNode.layoutHorizontally(relativeSpace: Float, startToEnd: Boolean) {
        var prevMargin = if (startToEnd) paddingStart else paddingEnd
        var x = if (startToEnd) minX else maxX

        for (i in children.indices) {
            val child = children[i]
            val childW = child.modifier.width
            val childH = child.modifier.height

            val layoutW = when (childW) {
                is Dp -> childW.value * surface.measuredScale
                is Grow -> max(relativeSpace * childW.weight, child.contentWidth)
                WrapContent -> child.contentWidth
            }

            val layoutX: Float
            if (startToEnd) {
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

            val layoutH = when (childH) {
                is Dp -> childH.value * surface.measuredScale
                is Grow -> height - max(paddingTop, child.marginTop) - max(paddingBottom, child.marginBottom)
                WrapContent -> child.contentHeight
            }
            val layoutY: Float = when (child.modifier.alignY) {
                AlignmentY.Top -> minY + max(paddingTop, child.marginTop)
                AlignmentY.Center -> minY + (height - layoutH) * 0.5f
                AlignmentY.Bottom -> maxY - layoutH - max(paddingBottom, child.marginBottom)
            }

            child.setBounds(round(layoutX), round(layoutY), round(layoutX + layoutW), round(layoutY + layoutH))
        }
    }
}