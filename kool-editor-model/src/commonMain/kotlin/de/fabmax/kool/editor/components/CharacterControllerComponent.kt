package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.editor.model.SceneNodeModel

class CharacterControllerComponent(
    nodeModel: SceneNodeModel,
    override val componentData: CharacterControllerComponentData = CharacterControllerComponentData()
) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<CharacterControllerComponentData>,
    PhysicsComponent
{
}