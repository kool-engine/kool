package de.fabmax.kool.app

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.ClassFactory
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.model.MProject
import de.fabmax.kool.scene.defaultOrbitCamera

expect object AppClassFactory : ClassFactory

class App : EditorAwareApp {
    override fun startApp(projectModel: MProject, isInEditor: Boolean, ctx: KoolContext) {
        val scenes = projectModel.create()

        if (!isInEditor) {
            // fixme: camera not yet included in project model, add a default one
            scenes.forEach {
                it.defaultOrbitCamera()
            }
        }
    }
}