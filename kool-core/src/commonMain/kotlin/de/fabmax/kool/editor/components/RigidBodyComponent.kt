package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.RigidBodyComponentData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf

fun RigidBodyComponent(nodeModel: SceneNodeModel): RigidBodyComponent {
    return RigidBodyComponent(nodeModel, RigidBodyComponentData(1f))
}

class RigidBodyComponent(nodeModel: SceneNodeModel, override val componentData: RigidBodyComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<RigidBodyComponentData>
{
    val massState = mutableStateOf(componentData.mass).onChange {
        if (AppState.isEditMode) {
            componentData.mass = it
        }
        updateRigidBody()
    }

    private fun updateRigidBody() {

    }
}