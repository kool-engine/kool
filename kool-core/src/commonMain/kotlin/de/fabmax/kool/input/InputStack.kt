package de.fabmax.kool.input

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW

object InputStack {

    val defaultInputHandler = InputHandler("InputStack.defaultInputHandler")
    val defaultKeyboardListener = SimpleKeyboardListener()

    private val handlerStack = mutableListOf(defaultInputHandler)
    private val processableKeyEvents = mutableListOf<KeyEvent>()

    init {
        defaultInputHandler.keyboardListeners += defaultKeyboardListener
    }

    fun pushTop(inputHandler: InputHandler) {
        remove(inputHandler)
        handlerStack += inputHandler
    }

    fun pushBottom(inputHandler: InputHandler) {
        remove(inputHandler)
        handlerStack.add(0, inputHandler)
    }

    fun remove(inputHandler: InputHandler) {
        handlerStack -= inputHandler
    }

    fun popAboveAndIncluding(inputHandler: InputHandler) {
        while (handlerStack.isNotEmpty()) {
            val removed = handlerStack.removeLast()
            if (removed === inputHandler) {
                break
            }
        }
    }

    internal fun handleInput(keyEvents: MutableList<KeyEvent>, ctx: KoolContext) {
        processableKeyEvents.clear()
        processableKeyEvents.addAll(keyEvents)

        var pointerBlocked = false

        for (i in handlerStack.lastIndex downTo 0) {
            val handler = handlerStack[i]

            if (!pointerBlocked) {
                handler.handlePointer(PointerInput.pointerState, ctx)
                if (handler.blockAllPointerInput) {
                    pointerBlocked = true
                }
            }

            if (processableKeyEvents.isNotEmpty()) {
                handler.handleKeyEvents(processableKeyEvents, ctx)
                if (handler.blockAllKeyboardInput) {
                    processableKeyEvents.clear()
                }
            }

            if (pointerBlocked && processableKeyEvents.isEmpty()) {
                break
            }
        }
    }

    open class InputHandler(val name: String) {
        var blockAllPointerInput = false
        var blockAllKeyboardInput = false

        val pointerListeners = mutableListOf<PointerListener>()
        val keyboardListeners = mutableListOf<KeyboardListener>()

        open fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
            pointerListeners.forEach { it.handlePointer(pointerState, ctx) }
        }

        open fun handleKeyEvents(keyEvents: MutableList<KeyEvent>, ctx: KoolContext) {
            keyboardListeners.forEach { it.handleKeyboard(keyEvents, ctx) }
        }
    }

    interface PointerListener {
        fun handlePointer(pointerState: PointerState, ctx: KoolContext)
    }

    interface KeyboardListener {
        fun handleKeyboard(keyEvents: MutableList<KeyEvent>, ctx: KoolContext)
    }

    class SimpleKeyboardListener : KeyboardListener {
        private val keyListeners = mutableMapOf<KeyCode, MutableList<SimpleKeyListener>>()

        fun registerKeyListener(
            keyCode: KeyCode,
            name: String,
            filter: (KeyEvent) -> Boolean = { true },
            callback: (KeyEvent) -> Unit
        ): SimpleKeyListener {
            return registerKeyListener(SimpleKeyListener(keyCode, name, filter, callback))
        }

        fun registerKeyListener(handler: SimpleKeyListener): SimpleKeyListener {
            val listeners = keyListeners.getOrPut(handler.keyCode) { mutableListOf() }
            if (listeners.isNotEmpty()) {
                logW { "Multiple bindings for key ${handler.keyCode}: ${listeners.map { it.name }}" }
            }

            listeners += handler
            logD { "Registered key handler: \"${handler.name}\" [keyCode=${handler.keyCode}]" }
            return handler
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
                    for (j in listeners.indices) {
                        if (listeners[j].filter(ev)) {
                            listeners[j](ev)
                            isHandled = true
                        }
                    }
                }
                if (!isHandled) {
                    keyListeners[ev.localKeyCode]?.let { listeners ->
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
        val filter: (KeyEvent) -> Boolean = { true },
        val callback: (KeyEvent) -> Unit
    ) {
        operator fun invoke(evt: KeyEvent) = callback.invoke(evt)
    }
}