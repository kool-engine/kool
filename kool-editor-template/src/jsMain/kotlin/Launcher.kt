import de.fabmax.kool.KoolApplication
import de.fabmax.kool.app.App

fun main() = KoolApplication { ctx ->
    ctx.scenes += App().startApp(ctx, false)
}