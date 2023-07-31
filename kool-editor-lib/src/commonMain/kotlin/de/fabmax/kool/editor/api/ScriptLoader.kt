package de.fabmax.kool.editor.api

expect object ScriptLoader {
    fun newScriptInstance(scriptClassName: String): KoolScript
    fun getScriptProperty(script: KoolScript, propertyName: String): Any?
    fun setScriptProperty(script: KoolScript, propertyName: String, value: Any?)
}