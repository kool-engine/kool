package de.fabmax.kool.util

import de.fabmax.kool.Input
import de.fabmax.kool.KoolContext

object InputStack {

    val defaultInputHandler = InputHandler("InputStack.defaultInputHandler")

    private val handlerStack = mutableListOf(defaultInputHandler)
    private val processableKeyEvents = mutableListOf<Input.KeyEvent>()

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

    internal fun handleInput(inputManager: Input, ctx: KoolContext) {
        processableKeyEvents.clear()
        processableKeyEvents.addAll(inputManager.keyEvents)

        var pointerBlocked = false

        for (i in handlerStack.lastIndex downTo 0) {
            val handler = handlerStack[i]

            if (!pointerBlocked) {
                handler.handlePointer(inputManager.pointerState, ctx)
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

        open fun handlePointer(pointerState: Input.PointerState, ctx: KoolContext) {
            pointerListeners.forEach { it.handlePointer(pointerState, ctx) }
        }

        open fun handleKeyEvents(keyEvents: MutableList<Input.KeyEvent>, ctx: KoolContext) {
            keyboardListeners.forEach { it.handleKeyboard(keyEvents, ctx) }
        }
    }

    interface PointerListener {
        fun handlePointer(pointerState: Input.PointerState, ctx: KoolContext)
    }

    interface KeyboardListener {
        fun handleKeyboard(keyEvents: MutableList<Input.KeyEvent>, ctx: KoolContext)
    }
}