package de.fabmax.kool.app

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.ClassFactory
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.model.MProject
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.defaultOrbitCamera

expect object AppClassFactory : ClassFactory

class App : EditorAwareApp {
    override fun startApp(project: MProject, isInEditor: Boolean, ctx: KoolContext): List<Scene> {
        val scene = project.scenes[0].createScene(AppClassFactory)
        scene.defaultOrbitCamera()
        return listOf(scene)
    }
}