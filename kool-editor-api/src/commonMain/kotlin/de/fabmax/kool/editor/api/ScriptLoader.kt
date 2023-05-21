package de.fabmax.kool.editor.api

expect object ScriptLoader {
    fun newScriptInstance(scriptClassName: String): KoolScript
}