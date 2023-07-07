import de.fabmax.kool.KoolApplication
import de.fabmax.kool.app.App
import de.fabmax.kool.editor.api.ScriptLoader

fun main() = KoolApplication { ctx ->
    ScriptLoader.appScriptLoader = ScriptBindings
    App().launchStandalone(ctx)
}