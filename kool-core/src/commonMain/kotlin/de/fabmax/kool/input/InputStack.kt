package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.logT

object InputStack {

    val defaultInputHandler = InputHandler("InputStack.defaultInputHandler")

    val handlerStack = BufferedList<InputHandler>().apply { this += defaultInputHandler }
    val onInputStackChanged = BufferedList<() -> Unit>()

    fun pushTop(inputHandler: InputHandler) {
        if (handlerStack.lastOrNull() != inputHandler) {
            if (inputHandler in handlerStack) {
                remove(inputHandler)
            }
            handlerStack += inputHandler
        }
    }

    fun pushBottom(inputHandler: InputHandler) {
        if (handlerStack.firstOrNull() != inputHandler) {
            if (inputHandler in handlerStack) {
                remove(inputHandler)
            }
            handlerStack.stageAdd(inputHandler, 0)
        }
    }

    fun remove(inputHandler: InputHandler) {
        if (inputHandler in handlerStack) {
            handlerStack -= inputHandler
        }
    }

    fun updateHandlerStack(): Boolean {
        val hasChanged = handlerStack.update()
        if (hasChanged) {
            onInputStackChanged.updated().forEach { it() }
        }
        return hasChanged
    }

    fun popAboveAndIncluding(inputHandler: InputHandler) {
        for (i in handlerStack.indices.reversed()) {
            val it = handlerStack[i]
            handlerStack -= it
            if (it === inputHandler) {
                break
            }
        }
    }

    internal fun handleInput(keyEvents: MutableList<KeyEvent>, ctx: KoolContext) {
        var pointerBlocked = false

        updateHandlerStack()

        for (i in handlerStack.lastIndex downTo 0) {
            val handler = handlerStack[i]

            if (!pointerBlocked) {
                handler.handlePointer(PointerInput.pointerState, ctx)
                if (handler.blockAllPointerInput) {
                    pointerBlocked = true
                }
            }

            if (keyEvents.isNotEmpty()) {
                handler.handleKeyEvents(keyEvents, ctx)
                if (handler.blockAllKeyboardInput) {
                    keyEvents.clear()
                }
            }

            if (pointerBlocked && keyEvents.isEmpty()) {
                break
            }
        }
    }

    open class InputHandler(val name: String) {
        var blockAllPointerInput = false
        var blockAllKeyboardInput = false

        val pointerListeners = BufferedList<PointerListener>()
        val keyboardListeners = BufferedList<KeyboardListener>()

        val simpleKeyboardListener = SimpleKeyboardListener()

        init {
            keyboardListeners += simpleKeyboardListener
        }

        open fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
            pointerListeners.update()
            // call listeners in reversed order -> most recently added listener first
            for (i in pointerListeners.lastIndex downTo 0) {
                pointerListeners[i].handlePointer(pointerState, ctx)
            }
        }

        open fun handleKeyEvents(keyEvents: MutableList<KeyEvent>, ctx: KoolContext) {
            keyboardListeners.update()
            // call listeners in reversed order -> most recently added listener first
            for (i in keyboardListeners.lastIndex downTo 0) {
                keyboardListeners[i].handleKeyboard(keyEvents, ctx)
            }
        }

        fun addKeyListener(
            keyCode: KeyCode,
            name: String,
            filter: (KeyEvent) -> Boolean = KEY_FILTER_KEY_PRESSED,
            callback: (KeyEvent) -> Unit
        ): SimpleKeyListener = simpleKeyboardListener.addKeyListener(keyCode, name, filter, callback)

        fun addKeyListener(listener: SimpleKeyListener) = simpleKeyboardListener.addKeyListener(listener)

        fun removeKeyListener(listener: SimpleKeyListener) = simpleKeyboardListener.removeKeyListener(listener)
    }

    fun interface PointerListener {
        fun handlePointer(pointerState: PointerState, ctx: KoolContext)
    }

    fun interface KeyboardListener {
        fun handleKeyboard(keyEvents: MutableList<KeyEvent>, ctx: KoolContext)
    }

    class SimpleKeyboardListener : KeyboardListener {
        val keyListeners = mutableMapOf<KeyCode, BufferedList<SimpleKeyListener>>()

        fun addKeyListener(
            keyCode: KeyCode,
            name: String,
            filter: (KeyEvent) -> Boolean = KEY_FILTER_KEY_PRESSED,
            callback: (KeyEvent) -> Unit
        ): SimpleKeyListener {
            return addKeyListener(SimpleKeyListener(keyCode, name, filter, callback))
        }

        fun addKeyListener(listener: SimpleKeyListener): SimpleKeyListener {
            val listeners = keyListeners.getOrPut(listener.keyCode) { BufferedList() }
            listeners += listener
            logT { "Registered key handler: \"${listener.name}\" [keyCode=${listener.keyCode}]" }
            return listener
        }

        fun removeKeyListener(listener: SimpleKeyListener) {
            val listeners = keyListeners[listener.keyCode] ?: return
            listeners -= listener
        }

        override fun handleKeyboard(keyEvents: MutableList<KeyEvent>, ctx: KoolContext) {
            val it = keyEvents.iterator()
            while (it.hasNext()) {
                val ev = it.next()
                var isHandled = false

                keyListeners[ev.keyCode]?.let { listeners ->
                    listeners.update()
                    for (j in listeners.indices) {
                        if (listeners[j].filter(ev)) {
                            listeners[j](ev)
                            isHandled = true
                        }
                    }
                }
                if (!isHandled) {
                    keyListeners[ev.localKeyCode]?.let { listeners ->
                        listeners.update()
                        for (j in listeners.indices) {
                            if (listeners[j].filter(ev)) {
                                listeners[j](ev)
                                isHandled = true
                            }
                        }
                    }
                }

                if (isHandled) {
                    it.remove()
                }
            }
        }
    }

    class SimpleKeyListener(
        val keyCode: KeyCode,
        val name: String,
        val filter: (KeyEvent) -> Boolean = KEY_FILTER_KEY_PRESSED,
        val callback: (KeyEvent) -> Unit
    ) {
        operator fun invoke(evt: KeyEvent) = callback.invoke(evt)

        fun getKeyInfo(): String {
            return when (filter) {
                KEY_FILTER_CTRL_PRESSED -> "Ctrl+${keyCode.name}"
                KEY_FILTER_ALT_PRESSED -> "Alt+${keyCode.name}"
                KEY_FILTER_SHIFT_PRESSED -> "Shift+${keyCode.name}"
                KEY_FILTER_SUPER_PRESSED -> "Super+${keyCode.name}"
                else -> keyCode.name
            }
        }
    }

    val KEY_FILTER_ALL: (KeyEvent) -> Boolean = { true }
    val KEY_FILTER_KEY_PRESSED: (KeyEvent) -> Boolean = { it.isPressed }
    val KEY_FILTER_KEY_RELEASED: (KeyEvent) -> Boolean = { it.isReleased }
    val KEY_FILTER_CTRL_PRESSED: (KeyEvent) -> Boolean = { it.isPressed && it.isCtrlDown }
    val KEY_FILTER_ALT_PRESSED: (KeyEvent) -> Boolean = { it.isPressed && it.isAltDown }
    val KEY_FILTER_SHIFT_PRESSED: (KeyEvent) -> Boolean = { it.isPressed && it.isShiftDown }
    val KEY_FILTER_SUPER_PRESSED: (KeyEvent) -> Boolean = { it.isPressed && it.isSuperDown }
}