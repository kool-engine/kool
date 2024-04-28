package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.PhysicsWorldComponentData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.util.logW

fun PhysicsWorldComponent(nodeModel: SceneModel): PhysicsWorldComponent {
    return PhysicsWorldComponent(nodeModel, PhysicsWorldComponentData())
}

class PhysicsWorldComponent(override val nodeModel: SceneModel, override val componentData: PhysicsWorldComponentData) :
    EditorModelComponent(nodeModel),
    EditorDataComponent<PhysicsWorldComponentData>,
    PhysicsComponent
{

    val physicsWorldState = mutableStateOf(componentData.settings).onChange {
        if (AppState.isEditMode) {
            componentData.settings = it
        } else {
            logW { "Physics world properties can only be changed in edit mode" }
        }
    }

    var physicsWorld: PhysicsWorld? = null

    override suspend fun createComponent() {
        super.createComponent()
        physicsWorld = PhysicsWorld(null, componentData.settings.isContinuousCollisionDetection)
    }

    override fun destroyComponent() {
        super.destroyComponent()
        physicsWorld?.let {
            it.unregisterHandlers()
            it.release()
        }
        physicsWorld = null
    }

    override fun onStart() {
        super.onStart()
        physicsWorld?.registerHandlers(nodeModel.drawNode)
    }
}