import de.fabmax.kool.KoolApplication
import de.fabmax.kool.app.App
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.LoadedApp
import de.fabmax.kool.editor.LoadedAppProxy
import de.fabmax.kool.editor.ProjectPaths
import de.fabmax.kool.editor.api.BehaviorLoader

fun main() = KoolApplication { ctx ->
    BehaviorLoader.appBehaviorLoader = BehaviorBindings
    //App().launchStandalone(ctx)

    val paths = ProjectPaths(".", gradleRootDir = "", gradleBuildTask = "")
    LoadedAppProxy.loadedApp = LoadedApp(App(), emptyMap())
    KoolEditor(ctx, paths)
}