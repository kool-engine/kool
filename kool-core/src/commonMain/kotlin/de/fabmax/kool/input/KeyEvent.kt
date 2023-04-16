package de.fabmax.kool.input

data class KeyEvent(
    /**
     * Keyboard-layout independent keycode.
     */
    val keyCode: KeyCode,

    /**
     * Keyboard-layout specific keycode.
     */
    val localKeyCode: KeyCode,

    /**
     * Key event type: Bit mask with possible values
     * - [KeyboardInput.KEY_EV_UP]
     * - [KeyboardInput.KEY_EV_DOWN]
     * - [KeyboardInput.KEY_EV_REPEATED]
     * - [KeyboardInput.KEY_EV_CHAR_TYPED]
     */
    val event: Int,

    /**
     * Key event modifiers: Bit mask with possible values
     * - [KeyboardInput.KEY_MOD_SHIFT]
     * - [KeyboardInput.KEY_MOD_CTRL]
     * - [KeyboardInput.KEY_MOD_ALT]
     * - [KeyboardInput.KEY_MOD_SUPER]
     */
    val modifiers: Int,

    /**
     * Typed character, only valid if [event] is [KeyboardInput.KEY_EV_CHAR_TYPED].
     */
    val typedChar: Char = 0.toChar()
) {
    val isPressed: Boolean get() = (event and KeyboardInput.KEY_EV_DOWN) != 0
    val isRepeated: Boolean get() = (event and KeyboardInput.KEY_EV_REPEATED) != 0
    val isReleased: Boolean get() = (event and KeyboardInput.KEY_EV_UP) != 0
    val isCharTyped: Boolean get() = (event and KeyboardInput.KEY_EV_CHAR_TYPED) != 0

    val isShiftDown: Boolean get() = (modifiers and KeyboardInput.KEY_MOD_SHIFT) != 0
    val isCtrlDown: Boolean get() = (modifiers and KeyboardInput.KEY_MOD_CTRL) != 0
    val isAltDown: Boolean get() = (modifiers and KeyboardInput.KEY_MOD_ALT) != 0
    val isSuperDown: Boolean get() = (modifiers and KeyboardInput.KEY_MOD_SUPER) != 0
}