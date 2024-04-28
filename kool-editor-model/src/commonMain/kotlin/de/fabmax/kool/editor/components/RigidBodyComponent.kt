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
import de.fabmax.kool.util.logW

fun RigidBodyComponent(nodeModel: SceneNodeModel): RigidBodyComponent {
    return RigidBodyComponent(nodeModel, RigidBodyComponentData())
}

class RigidBodyComponent(nodeModel: SceneNodeModel, override val componentData: RigidBodyComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<RigidBodyComponentData>,
    PhysicsComponent
{
    val bodyState = mutableStateOf(componentData.properties).onChange {
        if (AppState.isEditMode) {
            componentData.properties = it
        }
        updateRigidBody()
    }

    var rigidActor: RigidActor? = null

    override suspend fun createComponent() {
        super.createComponent()

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
        val isActorOk = when (componentData.properties.bodyType) {
            RigidBodyType.DYNAMIC -> actor is RigidDynamic && !actor.isKinematic
            RigidBodyType.KINEMATIC -> actor is RigidDynamic && actor.isKinematic
            RigidBodyType.STATIC -> actor is RigidStatic
        }

        if (!isActorOk) {
            createRigidBody()

        } else if (actor is RigidDynamic) {
            actor.mass = componentData.properties.mass
        }
    }

    private fun createRigidBody() {
        val physicsWorldComponent = nodeModel.sceneModel.getOrPutComponent<PhysicsWorldComponent> {
            logW { "Failed to find a PhysicsWorldComponent in parent scene, creating default one" }
            PhysicsWorldComponent(nodeModel.sceneModel)
        }
        val physicsWorld = physicsWorldComponent.physicsWorld
        if (physicsWorld == null) {
            logW { "Unable to create rigid body: parent physics world was not yet created" }
        }

        rigidActor?.let {
            physicsWorld?.removeActor(it)
            it.release()
        }

        rigidActor = when (componentData.properties.bodyType) {
            RigidBodyType.DYNAMIC -> RigidDynamic(componentData.properties.mass)
            RigidBodyType.KINEMATIC -> RigidDynamic(componentData.properties.mass, isKinematic = true)
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