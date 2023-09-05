import de.fabmax.kool.Assets
import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolContext
import de.fabmax.kool.app.App
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.LoadedApp
import de.fabmax.kool.editor.PlatformFunctions
import de.fabmax.kool.editor.ProjectPaths
import de.fabmax.kool.editor.api.BehaviorLoader

fun main() = KoolApplication { ctx ->
    //launchApp(ctx)
    launchEditor(ctx)
}

private fun launchApp(ctx: KoolContext) {
    BehaviorLoader.appBehaviorLoader = BehaviorBindings
    App().launchStandalone(ctx)
}

private fun launchEditor(ctx: KoolContext) {
    Assets.launch {
        PlatformFunctions.initPlatform("kool-project.json", LoadedApp(App(), BehaviorBindings.behaviorClasses))
        BehaviorLoader.appBehaviorLoader = BehaviorBindings

        val paths = ProjectPaths(
            projectDir = ".",
            projectFile = "kool-project.json",
            gradleRootDir = "",
            gradleBuildTask = "",
            assetsBasePath = "."
        )
        KoolEditor(ctx, paths)
    }
}