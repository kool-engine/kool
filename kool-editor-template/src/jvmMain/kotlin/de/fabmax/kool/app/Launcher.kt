package de.fabmax.kool.app

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolContext

fun main() = KoolApplication { ctx ->
    val app = App()
    ctx.scenes += app.startApp(ctx, false)
    ctx.applicationCallbacks = object : ApplicationCallbacks {
        override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
            app.onDispose(ctx, false)
            return true
        }
    }
}