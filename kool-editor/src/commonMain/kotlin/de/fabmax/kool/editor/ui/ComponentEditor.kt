package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.Composable

abstract class ComponentEditor<T: EditorModelComponent>(var component: T ) : Composable {

    open val nodeModel: EditorNodeModel
        get() = component.nodeModel

    open val sceneModel: SceneModel
        get() = requireNotNull(EditorState.activeScene.value)

}