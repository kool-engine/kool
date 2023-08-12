import de.fabmax.kool.KoolApplication
import de.fabmax.kool.app.App
import de.fabmax.kool.editor.api.BehaviorLoader

fun main() = KoolApplication { ctx ->
    BehaviorLoader.appBehaviorLoader = BehaviorBindings
    App().launchStandalone(ctx)
}