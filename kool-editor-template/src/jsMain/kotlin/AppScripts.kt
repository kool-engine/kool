import de.fabmax.kool.app.SampleScript
import de.fabmax.kool.editor.api.KoolScript
import de.fabmax.kool.editor.api.ScriptLoader

object AppScripts : ScriptLoader.AppScriptLoader {
    override fun newScriptInstance(scriptClassName: String): KoolScript {
        // todo: generate this
        return when (scriptClassName) {
            "de.fabmax.kool.app.SampleScript" -> SampleScript()
            else -> throw IllegalArgumentException("$scriptClassName not mapped. Add corresponding entry to AppScripts class")
        }
    }
}