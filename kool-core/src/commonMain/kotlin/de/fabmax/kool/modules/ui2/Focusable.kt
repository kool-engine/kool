package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager

interface Focusable : UiScope {
    fun requestFocus() {
        surface.requestFocus(this)
    }

    fun onFocusGain() { }

    fun onFocusLost() { }

    fun onKeyEvent(keyEvent: InputManager.KeyEvent)
}