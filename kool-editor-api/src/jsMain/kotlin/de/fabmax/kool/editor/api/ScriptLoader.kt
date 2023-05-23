package de.fabmax.kool.editor.api

actual object ScriptLoader {

    var appScriptLoader: AppScriptLoader? = null

    private val loader: AppScriptLoader
        get() = appScriptLoader ?: throw IllegalStateException("ScriptLoader.appScriptLoader not initialized")

    actual fun newScriptInstance(scriptClassName: String): KoolScript {
        return loader.newScriptInstance(scriptClassName)
    }

    actual fun getScriptProperty(script: KoolScript, propertyName: String): Any? {
        return loader.getScriptProperty(script, propertyName)
    }

    actual fun setScriptProperty(script: KoolScript, propertyName: String, value: Any?) {
        loader.setScriptProperty(script, propertyName, value)
    }

    interface AppScriptLoader {
        fun newScriptInstance(scriptClassName: String): KoolScript
        fun getScriptProperty(script: KoolScript, propertyName: String): Any?
        fun setScriptProperty(script: KoolScript, propertyName: String, value: Any?)
    }
}