package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.RigidBodyComponentData
import de.fabmax.kool.editor.data.RigidBodyType
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.util.logE

fun RigidBodyComponent(nodeModel: SceneNodeModel): RigidBodyComponent {
    return RigidBodyComponent(nodeModel, RigidBodyComponentData())
}

class RigidBodyComponent(nodeModel: SceneNodeModel, override val componentData: RigidBodyComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<RigidBodyComponentData>,
    PhysicsComponent
{
    val bodyState = mutableStateOf(componentData.settings).onChange {
        if (AppState.isEditMode) {
            componentData.settings = it
        }
        updateRigidBody()
    }

    var rigidActor: RigidActor? = null

    override suspend fun createComponent() {
        super.createComponent()

        val physicsWorld = nodeModel.sceneModel.getComponent<PhysicsWorldComponent>()?.physicsWorld
        if (physicsWorld == null) {
            logE { "Unable to create RigidBodyComponent: parent scene has no PhysicsWorldComponent" }
            return
        }

        createRigidBody()
        nodeModel.transform.onTransformEdited += { setPhysicsTransformFromModel() }
    }

    override fun destroyComponent() {
        super.destroyComponent()
        rigidActor?.let {
            nodeModel.sceneModel.getComponent<PhysicsWorldComponent>()?.physicsWorld?.removeActor(it)
            it.release()
        }
        rigidActor = null
    }

    private fun updateRigidBody() {
        val actor = rigidActor
        val isActorOk = when (componentData.settings.bodyType) {
            RigidBodyType.DYNAMIC -> actor is RigidDynamic && !actor.isKinematic
            RigidBodyType.KINEMATIC -> actor is RigidDynamic && actor.isKinematic
            RigidBodyType.STATIC -> actor is RigidStatic
        }

        if (!isActorOk) {
            createRigidBody()

        } else if (actor is RigidDynamic) {
            actor.mass = componentData.settings.mass
        }
    }

    private fun createRigidBody() {
        val physicsWorld = nodeModel.sceneModel.getComponent<PhysicsWorldComponent>()?.physicsWorld
        rigidActor?.let {
            physicsWorld?.removeActor(it)
            it.release()
        }

        rigidActor = when (componentData.settings.bodyType) {
            RigidBodyType.DYNAMIC -> RigidDynamic(componentData.settings.mass)
            RigidBodyType.KINEMATIC -> RigidDynamic(componentData.settings.mass, isKinematic = true)
            RigidBodyType.STATIC -> RigidStatic()
        }

        rigidActor?.apply {
            attachShape(Shape(BoxGeometry(Vec3f.ONES)))
            physicsWorld?.addActor(this)
            nodeModel.drawNode.transform = transform
        }
        setPhysicsTransformFromModel()
    }

    private fun setPhysicsTransformFromModel() {
        rigidActor?.apply {
            position = nodeModel.transform.componentData.transform.position.toVec3f()
            rotation = nodeModel.transform.componentData.transform.rotation.toQuatF()
        }
    }
}