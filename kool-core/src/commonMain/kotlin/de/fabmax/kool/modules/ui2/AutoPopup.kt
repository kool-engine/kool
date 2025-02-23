package de.fabmax.kool.modules.ui2

import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.launchDelayed

fun UiScope.AutoPopup(
    hideOnEsc: Boolean = true,
    hideOnOutsideClick: Boolean = true,
    scopeName: String? = null,
    block: UiScope.() -> Unit
): AutoPopup = remember {
    val popup = AutoPopup(hideOnEsc, hideOnOutsideClick, scopeName)
    popup.popupContent = Composable(block)
    popup
}

open class AutoPopup(
    val hideOnEsc: Boolean = true,
    val hideOnOutsideClick: Boolean = true,
    private val scopeName: String? = null
) : Composable, Focusable {

    val isVisible = mutableStateOf(false)
    val screenPosPx = mutableStateOf(Vec2f.ZERO)

    var popupContent = Composable {  }
    var onShow: (() -> Unit)? = null
    var onHide: (() -> Unit)? = null

    override val isFocused = mutableStateOf(false)
    private var parentSurface: UiSurface? = null
    private var hideCnt = 0
    private var revokeHide = 0

    open fun show(pointerEvent: PointerEvent) = show(pointerEvent.screenPosition)

    open fun show(screenPosPx: Vec2f) {
        this.screenPosPx.set(screenPosPx)
        revokeHide = hideCnt
        isVisible.set(true)
        parentSurface?.requestFocus(this@AutoPopup)
        onShow?.invoke()
    }

    open fun hide() {
        isVisible.set(false)
        if (isFocused.value) {
            parentSurface?.unfocus(this)
        }
        onHide?.invoke()
    }

    open fun toggleVisibility(showScreenPosPx: Vec2f) {
        if (isVisible.value) {
            hide()
        } else {
            show(showScreenPosPx)
        }
    }

    override fun UiScope.compose() {
        parentSurface = surface

        if (isVisible.use()) {
            val pos = screenPosPx.use()
            Popup(pos.x, pos.y, scopeName = scopeName) {
                modifier
                    .onPositioned { checkPopupPos(it) }
                    .onHover { }
                    .onEnter { }
                    .onDrag { }
                    .onClick { surface.requestFocus(this@AutoPopup) }

                popupContent()

                if (hideOnOutsideClick) {
                    // somewhat hacky way to close popup menu on any button event outside popup menu
                    surface.onEachFrame {
                        val ptr = PointerInput.primaryPointer
                        if (ptr.isAnyButtonReleased && !uiNode.isInBounds(ptr.pos)) {
                            // hide AutoPopup with 1 frame delay to give outside click handlers the chance to hide
                            // the menu explicitly, also do not hide the popup if show was called in between (e.g.
                            // because the popup should reappear at another location)
                            hideCnt++
                            launchDelayed(1) {
                                if (revokeHide < hideCnt) {
                                    hide()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkPopupPos(popupNode: UiNode) {
        var movedPos = screenPosPx.value
        if (screenPosPx.value.x < 0f) {
            movedPos = Vec2f(0f, movedPos.y)
        }
        if (screenPosPx.value.y < 0f) {
            movedPos = Vec2f(movedPos.x, 0f)
        }
        if (popupNode.rightPx > popupNode.surface.viewport.rightPx) {
            movedPos = Vec2f(movedPos.x - popupNode.rightPx + popupNode.surface.viewport.rightPx, movedPos.y)
        }
        if (popupNode.bottomPx > popupNode.surface.viewport.bottomPx) {
            movedPos = Vec2f(movedPos.x, movedPos.y - popupNode.bottomPx + popupNode.surface.viewport.bottomPx)
        }
        screenPosPx.set(movedPos)
    }

    override fun onKeyEvent(keyEvent: KeyEvent) {
        if (hideOnEsc && keyEvent.keyCode == KeyboardInput.KEY_ESC && keyEvent.isPressed) {
            hide()
        }
    }
}