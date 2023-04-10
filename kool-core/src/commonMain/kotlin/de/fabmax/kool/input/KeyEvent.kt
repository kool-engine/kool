package de.fabmax.kool.input

class KeyEvent(keyCode: KeyCode, localKeyCode: KeyCode, event: Int, modifiers: Int) {
    /**
     * Key code for US keyboard layout
     */
    var keyCode = keyCode
        internal set

    /**
     * Key code for local keyboard layout
     */
    var localKeyCode = localKeyCode
        internal set

    var modifiers = modifiers
        internal set
    var event = event
        internal set
    var typedChar: Char = 0.toChar()
        internal set

    constructor(keyCode: KeyCode, event: Int, modifiers: Int) : this(keyCode, keyCode, event, modifiers)

    val isPressed: Boolean get() = (event and KeyboardInput.KEY_EV_DOWN) != 0
    val isRepeated: Boolean get() = (event and KeyboardInput.KEY_EV_REPEATED) != 0
    val isReleased: Boolean get() = (event and KeyboardInput.KEY_EV_UP) != 0
    val isCharTyped: Boolean get() = (event and KeyboardInput.KEY_EV_CHAR_TYPED) != 0

    val isShiftDown: Boolean get() = (modifiers and KeyboardInput.KEY_MOD_SHIFT) != 0
    val isCtrlDown: Boolean get() = (modifiers and KeyboardInput.KEY_MOD_CTRL) != 0
    val isAltDown: Boolean get() = (modifiers and KeyboardInput.KEY_MOD_ALT) != 0
    val isSuperDown: Boolean get() = (modifiers and KeyboardInput.KEY_MOD_SUPER) != 0
}