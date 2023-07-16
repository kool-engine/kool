package de.fabmax.kool.editor

import de.fabmax.kool.KeyValueStore
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.platform.Lwjgl3Context

actual object PlatformCallbacks {

    actual fun onEditorStarted(ctx: KoolContext) {
        ctx as Lwjgl3Context
        val wnd = ctx.renderBackend.glfwWindow

        val posX = KeyValueStore.getInt("editor.window.posX", -1)
        val posY = KeyValueStore.getInt("editor.window.posY", -1)
        if (posX != -1 && posY != -1) {
            wnd.setWindowPos(posX, posY)
        }

        val width = KeyValueStore.getInt("editor.window.width", KoolSystem.config.windowSize.x)
        val height = KeyValueStore.getInt("editor.window.height", KoolSystem.config.windowSize.y)
        wnd.setWindowSize(width, height)

        val isMaximized = KeyValueStore.getBoolean("editor.window.isMaximized", false)
        if (isMaximized) {
            wnd.isMaximized = true
        }
        wnd.isVisible = true
    }

    actual fun onWindowCloseRequest(ctx: KoolContext): Boolean {
        ctx as Lwjgl3Context
        val wnd = ctx.renderBackend.glfwWindow

        if (wnd.isMaximized) {
            KeyValueStore.setBoolean("editor.window.isMaximized", true)
        } else {
            KeyValueStore.setBoolean("editor.window.isMaximized", false)
            KeyValueStore.setInt("editor.window.posX", wnd.windowPosX)
            KeyValueStore.setInt("editor.window.posY", wnd.windowPosY)
            KeyValueStore.setInt("editor.window.width", wnd.windowWidth)
            KeyValueStore.setInt("editor.window.height", wnd.windowHeight)
        }
        return true
    }

}