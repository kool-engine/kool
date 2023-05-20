package de.fabmax.kool.app

import de.fabmax.kool.ApplicationCallbacks
import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.ClassFactory
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.launchOnMainThread

expect object AppClassFactory : ClassFactory

class App : EditorAwareApp {
    override suspend fun startApp(projectModel: EditorProject, isInEditor: Boolean, ctx: KoolContext) {
        projectModel.create()

        if (!isInEditor) {
            // fixme: camera not yet included in project model, add a default one
            projectModel.getCreatedScenes().forEach {
                it.node.defaultOrbitCamera()
            }
        }
    }

    fun launchStandalone(ctx: KoolContext) {
        val app = App()

        launchOnMainThread {
            val projModel = EditorProject.loadFromAssets() ?: throw IllegalStateException("kool-project.json not found")
            app.startApp(projModel, false, ctx)
            val createdScenes = projModel.getCreatedScenes()
            createdScenes.forEach {
                ctx.scenes += it.node
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