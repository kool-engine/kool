import de.fabmax.kool.Assets
import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolContext
import de.fabmax.kool.app.App
import de.fabmax.kool.editor.*
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
        PlatformFunctions.initPlatform("kool-project.json")

        BehaviorLoader.appBehaviorLoader = BehaviorBindings
        LoadedAppProxy.loadedApp = LoadedApp(App(), emptyMap())

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