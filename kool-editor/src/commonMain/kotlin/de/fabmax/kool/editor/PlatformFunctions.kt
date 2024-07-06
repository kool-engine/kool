package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object PlatformFunctions {

    val windowButtonStyle: WindowButtonStyle
    val isWindowMaximized: Boolean

    fun onEditorStarted(ctx: KoolContext)

    fun onExit(ctx: KoolContext)

    fun editBehavior(behaviorClassName: String)

    suspend fun chooseFilePath(): String?

    fun saveProjectBlocking()

    fun toggleMaximizeWindow()
    fun minimizeWindow()
    fun closeWindow()
}

enum class WindowButtonStyle {
    NONE,
    WINDOWS
}