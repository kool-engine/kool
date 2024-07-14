package de.fabmax.kool.util

import de.fabmax.kool.modules.ui2.UiNode
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.mutableStateOf

open class WindowTitleHoverHandler {
    val minButtonBounds = HitBounds()
    val maxButtonBounds = HitBounds()
    val closeButtonBounds = HitBounds()
    val titleBarBounds = HitBounds()
    val titleBarExcludeBounds = mutableListOf<HitBounds>()

    val isTitleBarHovered = mutableStateOf(false)
    val isMinButtonHovered = mutableStateOf(false)
    val isMaxButtonHovered = mutableStateOf(false)
    val isCloseButtonHovered = mutableStateOf(false)

    var hoverState = HoverState.NONE
        private set

    var onClickMinimize: (() -> Unit)? = null
    var onClickMaximize: (() -> Unit)? = null
    var onClickClose: (() -> Unit)? = null

    fun setTitleBarBounds(uiScope: UiScope) = titleBarBounds.set(uiScope.uiNode)
    fun setMinButtonBounds(uiScope: UiScope) = minButtonBounds.set(uiScope.uiNode)
    fun setMaxButtonBounds(uiScope: UiScope) = maxButtonBounds.set(uiScope.uiNode)
    fun setCloseButtonBounds(uiScope: UiScope) = closeButtonBounds.set(uiScope.uiNode)

    open fun checkHover(x: Int, y: Int): HoverState {
        isMinButtonHovered.set(minButtonBounds.contains(x, y))
        isMaxButtonHovered.set(maxButtonBounds.contains(x, y))
        isCloseButtonHovered.set(closeButtonBounds.contains(x, y))

        var isTitleHovered = titleBarBounds.contains(x, y)
        if (isTitleHovered) {
            for (i in titleBarExcludeBounds.indices) {
                isTitleHovered = isTitleHovered && !titleBarExcludeBounds[i].contains(x, y)
            }
        }
        isTitleBarHovered.set(isTitleHovered)

        hoverState = when {
            isMinButtonHovered.value -> HoverState.MIN_BUTTON
            isMaxButtonHovered.value -> HoverState.MAX_BUTTON
            isCloseButtonHovered.value -> HoverState.CLOSE_BUTTON
            isTitleBarHovered.value -> HoverState.TITLE_BAR
            else -> HoverState.NONE
        }
        return hoverState
    }

    open fun leaveHover() {
        isMinButtonHovered.set(false)
        isMaxButtonHovered.set(false)
        isCloseButtonHovered.set(false)
        isTitleBarHovered.set(false)
        hoverState = HoverState.NONE
    }

    open fun handleClick() {
        when (hoverState) {
            HoverState.MIN_BUTTON -> onClickMinimize?.invoke()
            HoverState.MAX_BUTTON -> onClickMaximize?.invoke()
            HoverState.CLOSE_BUTTON -> onClickClose?.invoke()
            else -> { }
        }
    }

    class HitBounds {
        var left: Int = 0
        var right: Int = 0
        var top: Int = 0
        var bottom: Int = 0

        fun set(uiNode: UiNode) {
            left = uiNode.leftPx.toInt()
            right = uiNode.rightPx.toInt()
            top = uiNode.topPx.toInt()
            bottom = uiNode.bottomPx.toInt()
        }

        fun contains(x: Int, y: Int): Boolean {
            return x >= left && x < right && y >= top && y < bottom
        }
    }

    enum class HoverState {
        NONE,
        TITLE_BAR,
        MIN_BUTTON,
        MAX_BUTTON,
        CLOSE_BUTTON
    }
}