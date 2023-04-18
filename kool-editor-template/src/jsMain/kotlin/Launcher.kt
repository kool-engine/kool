import de.fabmax.kool.KoolApplication
import de.fabmax.kool.app.App
import de.fabmax.kool.editor.model.MProject
import de.fabmax.kool.util.launchOnMainThread

fun main() = KoolApplication { ctx ->
    launchOnMainThread {
        val projModel = MProject.loadFromAssets() ?: throw IllegalStateException("kool-project.json missing in assets")
        ctx.scenes += App().startApp(projModel, false, ctx)
    }
}