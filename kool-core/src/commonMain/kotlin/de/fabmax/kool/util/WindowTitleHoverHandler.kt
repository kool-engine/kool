package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.mutableStateOf

open class WindowTitleHoverHandler {
    val titleBarPos = MutableVec2i()
    val titleBarSize = MutableVec2i()

    val minButtonPos = MutableVec2i()
    val minButtonSize = MutableVec2i()

    val maxButtonPos = MutableVec2i()
    val maxButtonSize = MutableVec2i()

    val closeButtonPos = MutableVec2i()
    val closeButtonSize = MutableVec2i()

    val isTitleBarHovered = mutableStateOf(false)
    val isMinButtonHovered = mutableStateOf(false)
    val isMaxButtonHovered = mutableStateOf(false)
    val isCloseButtonHovered = mutableStateOf(false)

    var hoverState = HoverState.NONE
        private set

    var onClickMinimize: (() -> Unit)? = null
    var onClickMaximize: (() -> Unit)? = null
    var onClickClose: (() -> Unit)? = null

    fun setMaxButtonBounds(button: UiScope) {
        maxButtonPos.set(button.uiNode.leftPx.toInt(), button.uiNode.topPx.toInt())
        maxButtonSize.set(button.uiNode.widthPx.toInt(), button.uiNode.heightPx.toInt())
    }

    fun setMinButtonBounds(button: UiScope) {
        minButtonPos.set(button.uiNode.leftPx.toInt(), button.uiNode.topPx.toInt())
        minButtonSize.set(button.uiNode.widthPx.toInt(), button.uiNode.heightPx.toInt())
    }

    fun setCloseButtonBounds(button: UiScope) {
        closeButtonPos.set(button.uiNode.leftPx.toInt(), button.uiNode.topPx.toInt())
        closeButtonSize.set(button.uiNode.widthPx.toInt(), button.uiNode.heightPx.toInt())
    }

    fun setTitleBarBounds(button: UiScope) {
        titleBarPos.set(button.uiNode.leftPx.toInt(), button.uiNode.topPx.toInt())
        titleBarSize.set(button.uiNode.widthPx.toInt(), button.uiNode.heightPx.toInt())
    }

    open fun checkHover(x: Int, y: Int): HoverState {
        isMinButtonHovered.set(isInBounds(x, y, minButtonPos, minButtonSize))
        isMaxButtonHovered.set(isInBounds(x, y, maxButtonPos, maxButtonSize))
        isCloseButtonHovered.set(isInBounds(x, y, closeButtonPos, closeButtonSize))
        isTitleBarHovered.set(isInBounds(x, y, titleBarPos, titleBarSize))
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

    private fun isInBounds(x: Int, y: Int, pos: Vec2i, size: Vec2i): Boolean {
        return x >= pos.x && x < pos.x + size.x && y >= pos.y && y < pos.y + size.y
    }

    enum class HoverState {
        NONE,
        TITLE_BAR,
        MIN_BUTTON,
        MAX_BUTTON,
        CLOSE_BUTTON
    }
}