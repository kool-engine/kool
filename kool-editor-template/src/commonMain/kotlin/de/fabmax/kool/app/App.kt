package de.fabmax.kool.app

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.model.EditorProject
import de.fabmax.kool.scene.defaultOrbitCamera

class App : EditorAwareApp {
    override suspend fun loadApp(projectModel: EditorProject, ctx: KoolContext) {
        projectModel.create()

        if (!AppState.isInEditor) {
            // fixme: camera not yet included in project model, add a default one
            projectModel.getCreatedScenes().forEach {
                it.drawNode.camera.setClipRange(0.1f, 1000f)
                it.drawNode.defaultOrbitCamera()
            }
        }
    }
}