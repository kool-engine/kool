package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object PlatformFunctions {
    fun onEditorStarted(ctx: KoolContext)

    fun onWindowCloseRequest(ctx: KoolContext): Boolean

    fun editBehavior(behaviorClassName: String)

    suspend fun chooseFilePath(): String?
}