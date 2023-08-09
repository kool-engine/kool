package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.ScenePropertiesComponentData
import de.fabmax.kool.editor.model.SceneModel

class ScenePropertiesComponent(override val nodeModel: SceneModel, override val componentData: ScenePropertiesComponentData) :
    EditorModelComponent(nodeModel),
    EditorDataComponent<ScenePropertiesComponentData>
{
    init {
        componentOrder = COMPONENT_ORDER_EARLY
    }
}