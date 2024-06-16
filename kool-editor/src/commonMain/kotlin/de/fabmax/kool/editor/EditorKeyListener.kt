package de.fabmax.kool.editor

import de.fabmax.kool.input.*
import de.fabmax.kool.modules.ui2.toggle

open class EditorKeyListener(name: String) : InputStack.InputHandler(name) {
    private val _registeredKeys = mutableMapOf<Key, InputStack.SimpleKeyListener>()
    val registeredKeys: List<Key> get() = _registeredKeys.keys.toList()

    init {
        blockAllKeyboardInput = true
        addKeyListener(Key.Help) { KoolEditor.instance.ui.sceneView.isShowKeyInfo.toggle() }
    }

    fun push() {
        InputStack.pushTop(this)
    }

    fun pop() {
        InputStack.remove(this)
    }

    fun addKeyListener(key: Key, block: (KeyEvent) -> Unit) {
        check(key !in _registeredKeys) { "$key already registered" }
        val binding = key.binding
        _registeredKeys[key] = addKeyListener(binding.keyCode, key.name, binding.keyMod.keyEventFilter, block)
        key.binding.extraKeyCodes.forEach { (keyCode, mod) ->
            addKeyListener(keyCode, key.name, mod.keyEventFilter, block)
        }
    }

    fun removeKeyListener(key: Key) {
        _registeredKeys.remove(key)?.let { removeKeyListener(it) }
    }

    companion object {
        fun cancelListener(name: String, block: (KeyEvent) -> Unit): EditorKeyListener {
            return EditorKeyListener(name).apply {
                addKeyListener(Key.Cancel, block)
            }
        }
    }
}

enum class Key(val group: KeyGroup) {
    Help(KeyGroup.General),
    Copy(KeyGroup.General),
    Paste(KeyGroup.General),
    Duplicate(KeyGroup.General),

    Undo(KeyGroup.General),
    Redo(KeyGroup.General),

    Enter(KeyGroup.General),
    Cancel(KeyGroup.General),
    DeleteSelected(KeyGroup.General),
    HideSelected(KeyGroup.General),
    UnhideHidden(KeyGroup.General),
    FocusSelected(KeyGroup.General),

    ToggleBoxSelectMode(KeyGroup.General),
    ToggleMoveMode(KeyGroup.General),
    ToggleRotateMode(KeyGroup.General),
    ToggleScaleMode(KeyGroup.General),
    ToggleImmediateMoveMode(KeyGroup.General),
    ToggleImmediateRotateMode(KeyGroup.General),
    ToggleImmediateScaleMode(KeyGroup.General),


    LimitToXAxis(KeyGroup.ImmediateTransform),
    LimitToYAxis(KeyGroup.ImmediateTransform),
    LimitToZAxis(KeyGroup.ImmediateTransform),
    LimitToXPlane(KeyGroup.ImmediateTransform),
    LimitToYPlane(KeyGroup.ImmediateTransform),
    LimitToZPlane(KeyGroup.ImmediateTransform),

    TickIncrement(KeyGroup.ImmediateTransform),
    MinorTickIncrement(KeyGroup.ImmediateTransform),
    TickDecrement(KeyGroup.ImmediateTransform),
    MinorTickDecrement(KeyGroup.ImmediateTransform),
    ;

    val binding: KeyBinding get() = getBinding(this)
    val description: String get() = getDescription(this)

    companion object {
        fun getBinding(key: Key): KeyBinding {
            // todo: load these from settings
            return when (key) {
                Help -> KeyBinding(key, KeyboardInput.KEY_F1, KeyMod.none)
                Copy -> KeyBinding(key, LocalKeyCode('C'), KeyMod.ctrl)
                Paste -> KeyBinding(key, LocalKeyCode('V'), KeyMod.ctrl)
                Duplicate -> KeyBinding(key, LocalKeyCode('D'), KeyMod.ctrl)
                Undo -> KeyBinding(key, LocalKeyCode('Z'), KeyMod.ctrl)
                Redo -> KeyBinding(key, LocalKeyCode('Y'), KeyMod.ctrl)
                Cancel -> KeyBinding(key, KeyboardInput.KEY_ESC, KeyMod.none)
                Enter -> KeyBinding(key, KeyboardInput.KEY_ENTER, KeyMod.none, setOf(KeyboardInput.KEY_NP_ENTER to KeyMod.none))
                DeleteSelected -> KeyBinding(key, KeyboardInput.KEY_DEL, KeyMod.none)
                HideSelected -> KeyBinding(key, LocalKeyCode('H'), KeyMod.none)
                UnhideHidden -> KeyBinding(key, LocalKeyCode('H'), KeyMod.alt)
                FocusSelected -> KeyBinding(key, KeyboardInput.KEY_NP_DECIMAL, KeyMod.none)
                ToggleBoxSelectMode -> KeyBinding(key, LocalKeyCode('B'), KeyMod.none)
                ToggleMoveMode -> KeyBinding(key, LocalKeyCode('G'), KeyMod.shift)
                ToggleRotateMode -> KeyBinding(key, LocalKeyCode('R'), KeyMod.shift)
                ToggleScaleMode -> KeyBinding(key, LocalKeyCode('S'), KeyMod.shift)
                ToggleImmediateMoveMode -> KeyBinding(key, LocalKeyCode('G'), KeyMod.none)
                ToggleImmediateRotateMode -> KeyBinding(key, LocalKeyCode('R'), KeyMod.none)
                ToggleImmediateScaleMode -> KeyBinding(key, LocalKeyCode('S'), KeyMod.none)
                LimitToXAxis -> KeyBinding(key, LocalKeyCode('X'), KeyMod.none)
                LimitToYAxis -> KeyBinding(key, LocalKeyCode('Y'), KeyMod.none)
                LimitToZAxis -> KeyBinding(key, LocalKeyCode('Z'), KeyMod.none)
                LimitToXPlane -> KeyBinding(key, LocalKeyCode('X'), KeyMod.shift)
                LimitToYPlane -> KeyBinding(key, LocalKeyCode('Y'), KeyMod.shift)
                LimitToZPlane -> KeyBinding(key, LocalKeyCode('Z'), KeyMod.shift)
                TickIncrement -> KeyBinding(key, KeyboardInput.KEY_CURSOR_UP, KeyMod.none, setOf(KeyboardInput.KEY_CURSOR_RIGHT to KeyMod.none))
                MinorTickIncrement -> KeyBinding(key, KeyboardInput.KEY_CURSOR_UP, KeyMod.shift, setOf(KeyboardInput.KEY_CURSOR_RIGHT to KeyMod.shift))
                TickDecrement -> KeyBinding(key, KeyboardInput.KEY_CURSOR_DOWN, KeyMod.none, setOf(KeyboardInput.KEY_CURSOR_LEFT to KeyMod.none))
                MinorTickDecrement -> KeyBinding(key, KeyboardInput.KEY_CURSOR_DOWN, KeyMod.shift, setOf(KeyboardInput.KEY_CURSOR_LEFT to KeyMod.shift))
            }
        }

        fun getDescription(key: Key): String {
            return when (key) {
                ToggleBoxSelectMode -> "Box select"
                ToggleMoveMode -> "Move selected"
                ToggleRotateMode -> "Rotate selected"
                ToggleScaleMode -> "Scale selected"
                ToggleImmediateMoveMode -> "Immediate move selected"
                ToggleImmediateRotateMode -> "Immediate rotate selected"
                ToggleImmediateScaleMode -> "Immediate scale selected"
                DeleteSelected -> "Delete selected"
                HideSelected -> "Hide selected"
                UnhideHidden -> "Unhide all"
                FocusSelected -> "Focus selected"

                LimitToXAxis -> "X-axis only"
                LimitToYAxis -> "Y-axis only"
                LimitToZAxis -> "Z-axis only"
                LimitToXPlane -> "X-plane only"
                LimitToYPlane -> "Y-plane only"
                LimitToZPlane -> "Z-plane only"
                TickIncrement -> "Increase 1 tick"
                MinorTickIncrement -> "Increase 1 small tick"
                TickDecrement -> "Decrease 1 tick"
                MinorTickDecrement -> "Decrease 1 small tick"

                else -> key.toString()
            }
        }
    }
}

data class KeyBinding(val key: Key, val keyCode: KeyCode, val keyMod: KeyMod, val extraKeyCodes: Set<Pair<KeyCode, KeyMod>> = emptySet()) {
    val name: String get() = key.toString()

    val keyInfo: String get() {
        var s = keyMod.toString()
        if (s.isNotEmpty()) {
            s += " + "
        }
        return s + keyCode.name
    }
}

class KeyMod(val mask: Int) {
    val isAlt: Boolean = (mask and ALT) != 0
    val isCtrl: Boolean = (mask and CTRL) != 0
    val isShift: Boolean = (mask and SHIFT) != 0

    val keyEventFilter: (KeyEvent) -> Boolean = { ev ->
        ev.isPressed &&
                isAlt == ev.isAltDown &&
                isCtrl == ev.isCtrlDown &&
                isShift == ev.isShiftDown
    }

    override fun toString(): String {
        var s = ""
        if (isCtrl) s += "CTRL + "
        if (isAlt) s += "ALT + "
        if (isShift) s += "SHIFT"
        return s.removeSuffix(" + ")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as KeyMod
        return mask == other.mask
    }

    override fun hashCode(): Int = mask

    companion object {
        const val ALT = 1
        const val CTRL = 2
        const val SHIFT = 4

        val none = KeyMod(0)
        val alt = KeyMod(ALT)
        val ctrl = KeyMod(CTRL)
        val shift = KeyMod(SHIFT)
        val altCtrl = KeyMod(ALT + CTRL)
        val altShift = KeyMod(ALT + SHIFT)
        val ctrlShift = KeyMod(CTRL + SHIFT)
        val altCtrlShift = KeyMod(ALT + CTRL + SHIFT)
    }
}

enum class KeyGroup {
    General,
    ImmediateTransform
}
