package de.fabmax.kool.app

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.model.MProject
import de.fabmax.kool.util.launchOnMainThread

fun main() = KoolApplication { ctx ->
    val app = App()

    launchOnMainThread {
        val projModel = MProject.loadFromAssets() ?: throw IllegalStateException("kool-project.json not found")
        app.startApp(projModel, false, ctx)
        projModel.created?.let {
            ctx.scenes += it
        }
    }

    ctx.applicationCallbacks = object : ApplicationCallbacks {
        override fun onWindowCloseRequest(ctx: KoolContext): Boolean {
            app.onDispose(false, ctx)
            return true
        }
    }
}