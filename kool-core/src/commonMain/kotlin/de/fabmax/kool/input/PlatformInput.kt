package de.fabmax.kool.input

internal expect object PlatformInput {
    fun setCursorMode(cursorMode: CursorMode)
    fun applyCursorShape(cursorShape: CursorShape)
}