package de.fabmax.kool.app

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.ClassFactory
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.model.MProject
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.launchOnMainThread

expect object AppClassFactory : ClassFactory

class App : EditorAwareApp {
    override suspend fun startApp(projectModel: MProject, isInEditor: Boolean, ctx: KoolContext) {
        val scenes = projectModel.create()

        if (!isInEditor) {
            // fixme: camera not yet included in project model, add a default one
            scenes.forEach {
                it.defaultOrbitCamera()
            }
        }
    }

    fun launchStandalone(ctx: KoolContext) {
        val app = App()

        launchOnMainThread {
            val projModel = MProject.loadFromAssets() ?: throw IllegalStateException("kool-project.json not found")
            app.startApp(projModel, false, ctx)
            val createdScenes = projModel.getOrNull() ?: throw IllegalStateException("Scene creation failed")
            createdScenes.forEach {
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
}