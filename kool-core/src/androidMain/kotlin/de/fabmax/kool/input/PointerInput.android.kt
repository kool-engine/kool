package de.fabmax.kool.input

internal actual fun PlatformInput(): PlatformInput = PlatformInputAndroid

object PlatformInputAndroid : PlatformInput {
    override fun setCursorMode(cursorMode: CursorMode) { }

    override fun applyCursorShape(cursorShape: CursorShape) { }
}