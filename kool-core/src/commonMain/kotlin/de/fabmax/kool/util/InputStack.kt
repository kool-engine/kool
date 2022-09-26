package de.fabmax.kool.util

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext

object InputStack {

    val defaultInputHandler = InputHandler("InputStack.defaultInputHandler", false, false)

    private val handlerStack = mutableListOf(defaultInputHandler)
    private val processableKeyEvents = mutableListOf<InputManager.KeyEvent>()

    fun pushTop(inputHandler: InputHandler) {
        handlerStack += inputHandler
    }

    fun pushBottom(inputHandler: InputHandler) {
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

    internal fun handleInput(inputManager: InputManager, ctx: KoolContext) {
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

    open class InputHandler(val name: String, var blockAllPointerInput: Boolean, var blockAllKeyboardInput: Boolean) {
        val pointerListeners = mutableListOf<(InputManager.PointerState) -> Unit>()
        val keyboardListeners = mutableListOf<(MutableList<InputManager.KeyEvent>) -> Unit>()

        open fun handlePointer(pointerState: InputManager.PointerState, ctx: KoolContext) {
            pointerListeners.forEach { it(pointerState) }
        }

        open fun handleKeyEvents(keyEvents: MutableList<InputManager.KeyEvent>, ctx: KoolContext) {
            keyboardListeners.forEach { it(keyEvents) }
        }
    }
}