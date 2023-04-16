package de.fabmax.kool.input

import de.fabmax.kool.KoolContext

object KeyboardInput {
    private val queuedKeyEvents: MutableList<KeyEvent> = mutableListOf()

    private var currentKeyMods = 0
    private var currentKeyRepeated = 0

    val isShiftDown: Boolean get() = (currentKeyMods and KEY_MOD_SHIFT) != 0
    val isCtrlDown: Boolean get() = (currentKeyMods and KEY_MOD_CTRL) != 0
    val isAltDown: Boolean get() = (currentKeyMods and KEY_MOD_ALT) != 0
    val isSuperDown: Boolean get() = (currentKeyMods and KEY_MOD_SUPER) != 0

    fun addKeyListener(
        keyCode: KeyCode,
        name: String,
        filter: (KeyEvent) -> Boolean = { true },
        callback: (KeyEvent) -> Unit
    ): InputStack.SimpleKeyListener {
        return InputStack.defaultInputHandler.addKeyListener(keyCode, name, filter, callback)
    }

    fun removeKeyListener(listener: InputStack.SimpleKeyListener) {
        InputStack.defaultInputHandler.removeKeyListener(listener)
    }

    fun getKeyCodeForChar(char: Char) = char.uppercaseChar().code

    internal fun onNewFrame(ctx: KoolContext) {
        InputStack.handleInput(queuedKeyEvents, ctx)
        queuedKeyEvents.clear()
    }

    //
    // key handler functions to be called by platform code
    //

    fun handleKeyEvent(ev: KeyEvent) {
        currentKeyMods = ev.modifiers
        currentKeyRepeated = ev.event and KEY_EV_REPEATED
        queuedKeyEvents.add(ev)
    }

    fun handleCharTyped(typedChar: Char) {
        val ev = KeyEvent(
            UniversalKeyCode(typedChar.code),
            LocalKeyCode(typedChar.code),
            KEY_EV_CHAR_TYPED or currentKeyRepeated,
            currentKeyMods,
            typedChar
        )
        queuedKeyEvents.add(ev)
    }

    const val KEY_EV_UP = 1
    const val KEY_EV_DOWN = 2
    const val KEY_EV_REPEATED = 4
    const val KEY_EV_CHAR_TYPED = 8

    const val KEY_MOD_SHIFT = 1
    const val KEY_MOD_CTRL = 2
    const val KEY_MOD_ALT = 4
    const val KEY_MOD_SUPER = 8

    val KEY_CTRL_LEFT = UniversalKeyCode(-1, "CTRL_LEFT")
    val KEY_CTRL_RIGHT = UniversalKeyCode(-2, "CTRL_RIGHT")
    val KEY_SHIFT_LEFT = UniversalKeyCode(-3, "SHIFT_LEFT")
    val KEY_SHIFT_RIGHT = UniversalKeyCode(-4, "SHIFT_RIGHT")
    val KEY_ALT_LEFT = UniversalKeyCode(-5, "ALT_LEFT")
    val KEY_ALT_RIGHT = UniversalKeyCode(-6, "ALT_RIGHT")
    val KEY_SUPER_LEFT = UniversalKeyCode(-7, "SUPER_LEFT")
    val KEY_SUPER_RIGHT = UniversalKeyCode(-8, "SUPER_RIGHT")
    val KEY_ESC = UniversalKeyCode(-9, "ESC")
    val KEY_MENU = UniversalKeyCode(-10, "MENU")
    val KEY_ENTER = UniversalKeyCode(-11, "ENTER")
    val KEY_NP_ENTER = UniversalKeyCode(-12, "NP_ENTER")
    val KEY_NP_DIV = UniversalKeyCode(-13, "NP_DIV")
    val KEY_NP_MUL = UniversalKeyCode(-14, "NP_MUL")
    val KEY_NP_PLUS = UniversalKeyCode(-15, "NP_PLUS")
    val KEY_NP_MINUS = UniversalKeyCode(-16, "NP_MINUS")
    val KEY_BACKSPACE = UniversalKeyCode(-17, "BACKSPACE")
    val KEY_TAB = UniversalKeyCode(-18, "TAB")
    val KEY_DEL = UniversalKeyCode(-19, "DEL")
    val KEY_INSERT = UniversalKeyCode(-20, "INSERT")
    val KEY_HOME = UniversalKeyCode(-21, "HOME")
    val KEY_END = UniversalKeyCode(-22, "END")
    val KEY_PAGE_UP = UniversalKeyCode(-23, "PAGE_UP")
    val KEY_PAGE_DOWN = UniversalKeyCode(-24, "PAGE_DOWN")
    val KEY_CURSOR_LEFT = UniversalKeyCode(-25, "CURSOR_LEFT")
    val KEY_CURSOR_RIGHT = UniversalKeyCode(-26, "CURSOR_RIGHT")
    val KEY_CURSOR_UP = UniversalKeyCode(-27, "CURSOR_UP")
    val KEY_CURSOR_DOWN = UniversalKeyCode(-28, "CURSOR_DOWN")
    val KEY_F1 = UniversalKeyCode(-29, "F1")
    val KEY_F2 = UniversalKeyCode(-30, "F2")
    val KEY_F3 = UniversalKeyCode(-31, "F3")
    val KEY_F4 = UniversalKeyCode(-32, "F4")
    val KEY_F5 = UniversalKeyCode(-33, "F5")
    val KEY_F6 = UniversalKeyCode(-34, "F6")
    val KEY_F7 = UniversalKeyCode(-35, "F7")
    val KEY_F8 = UniversalKeyCode(-36, "F8")
    val KEY_F9 = UniversalKeyCode(-37, "F9")
    val KEY_F10 = UniversalKeyCode(-38, "F10")
    val KEY_F11 = UniversalKeyCode(-39, "F11")
    val KEY_F12 = UniversalKeyCode(-40, "F12")
}
