package de.fabmax.kool.input

import de.fabmax.kool.KoolContext

object Input {
    val keyboard: KeyboardInput get() = KeyboardInput
    val pointer: PointerInput get() = PointerInput

    internal val platformInput = PlatformInput()

    fun poll(ctx: KoolContext) {
        platformInput.poll()
        KeyboardInput.poll(ctx)
        PointerInput.poll(ctx)
        ControllerInput.poll()
    }
}

internal expect fun PlatformInput(): PlatformInput

internal interface PlatformInput {
    fun setCursorMode(cursorMode: CursorMode)
    fun applyCursorShape(cursorShape: CursorShape)

    fun poll() { }

    fun requestKeyboard() { }
    fun hideKeyboard() { }
}
