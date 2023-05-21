package de.fabmax.kool.editor.api

actual object ScriptLoader {

    var appScriptLoader: AppScriptLoader? = null

    actual fun newScriptInstance(scriptClassName: String): KoolScript {
        val loader = appScriptLoader ?: throw IllegalStateException("ScriptLoader.appScriptLoader not initialized")
        return loader.newScriptInstance(scriptClassName)
    }

    interface AppScriptLoader {
        fun newScriptInstance(scriptClassName: String): KoolScript
    }
}