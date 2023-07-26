package de.fabmax.kool.app

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.editor.model.EditorProject

class App : EditorAwareApp {
    override suspend fun loadApp(projectModel: EditorProject, ctx: KoolContext) {
        projectModel.create()
    }
}