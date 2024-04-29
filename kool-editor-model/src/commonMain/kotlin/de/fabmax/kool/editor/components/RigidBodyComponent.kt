package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.CapsuleGeometry
import de.fabmax.kool.physics.geometry.CylinderGeometry
import de.fabmax.kool.physics.geometry.SphereGeometry
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlin.math.max

fun RigidBodyComponent(nodeModel: SceneNodeModel): RigidBodyComponent {
    return RigidBodyComponent(nodeModel, RigidBodyComponentData())
}

class RigidBodyComponent(nodeModel: SceneNodeModel, override val componentData: RigidBodyComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<RigidBodyComponentData>,
    PhysicsComponent,
    UpdateMeshComponent
{
    val bodyState = mutableStateOf(componentData.properties).onChange {
        if (AppState.isEditMode) {
            componentData.properties = it
        }
        updateRigidBody()
    }

    var rigidActor: RigidActor? = null
    private var bodyShape: RigidBodyShape? = null

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

    override fun onStart() {
        super.onStart()

        // make sure the draw node uses the physics transform
        rigidActor?.let { actor ->
            if (nodeModel.drawNode.transform != actor.transform) {
                nodeModel.drawNode.transform = actor.transform
            }
        }
    }

    private fun updateRigidBody() {
        val actor = rigidActor
        val isActorOk = when (componentData.properties.type) {
            RigidBodyType.DYNAMIC -> actor is RigidDynamic && !actor.isKinematic
            RigidBodyType.KINEMATIC -> actor is RigidDynamic && actor.isKinematic
            RigidBodyType.STATIC -> actor is RigidStatic
        }

        if (!isActorOk || componentData.properties.shape != bodyShape) {
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

        rigidActor = when (componentData.properties.type) {
            RigidBodyType.DYNAMIC -> RigidDynamic(componentData.properties.mass)
            RigidBodyType.KINEMATIC -> RigidDynamic(componentData.properties.mass, isKinematic = true)
            RigidBodyType.STATIC -> RigidStatic()
        }

        rigidActor?.apply {
            when (val shape = componentData.properties.shape) {
                is RigidBodyShape.Box -> attachShape(Shape(BoxGeometry(shape.size.toVec3f())))
                is RigidBodyShape.Capsule -> attachShape(Shape(CapsuleGeometry(shape.length, shape.radius)))
                is RigidBodyShape.Cylinder -> attachShape(Shape(CylinderGeometry(shape.length, shape.radius)))
                is RigidBodyShape.Sphere -> attachShape(Shape(SphereGeometry(shape.radius)))
                RigidBodyShape.UseMesh -> attachMeshShapes(this)
            }

            physicsWorld?.addActor(this)
        }
        setPhysicsTransformFromModel()
    }

    private fun attachMeshShapes(actor: RigidActor) {
        val mesh = nodeModel.getComponent<MeshComponent>()
        if (mesh == null) {
            logE { "Node ${nodeModel.name}: Failed attaching mesh shape to rigid actor: has no attached MeshComponent" }
            actor.attachShape(Shape(BoxGeometry(Vec3f.ONES)))
            return
        }

        mesh.componentData.shapes.forEach { meshShape ->
            val geom = when (meshShape) {
                is MeshShapeData.Box -> BoxGeometry(meshShape.size.toVec3f())
                is MeshShapeData.Capsule -> CapsuleGeometry(meshShape.length.toFloat(), meshShape.radius.toFloat())
                is MeshShapeData.Cylinder -> CylinderGeometry(meshShape.length.toFloat(), max(meshShape.topRadius, meshShape.bottomRadius).toFloat())
                is MeshShapeData.IcoSphere -> SphereGeometry(meshShape.radius.toFloat())
                is MeshShapeData.UvSphere -> SphereGeometry(meshShape.radius.toFloat())
                else -> {
                    logE { "Node ${nodeModel.name}: Mesh shape is not supported as rigid actor shape: $meshShape" }
                    BoxGeometry(Vec3f.ONES)
                }
            }
            actor.attachShape(Shape(geom))
        }
    }

    private fun setPhysicsTransformFromModel() {
        rigidActor?.apply {
            position = nodeModel.transform.componentData.transform.position.toVec3f()
            rotation = nodeModel.transform.componentData.transform.rotation.toQuatF()
        }
    }

    override fun updateMesh(mesh: MeshComponentData) {
        if (componentData.properties.shape == RigidBodyShape.UseMesh) {
            createRigidBody()
        }
    }
}