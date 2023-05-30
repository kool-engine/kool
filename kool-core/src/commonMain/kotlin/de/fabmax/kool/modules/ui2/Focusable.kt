package de.fabmax.kool.modules.ui2

import de.fabmax.kool.input.KeyEvent

interface Focusable {
    val isFocused: MutableStateValue<Boolean>

    fun UiScope.requestFocus() {
        surface.requestFocus(this@Focusable)
    }

    fun onFocusGain() {
        isFocused.set(true)
    }

    fun onFocusLost() {
        isFocused.set(false)
    }

    fun onKeyEvent(keyEvent: KeyEvent)
}