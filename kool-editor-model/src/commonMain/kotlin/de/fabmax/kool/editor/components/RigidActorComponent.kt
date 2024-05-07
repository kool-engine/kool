package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.RigidActorComponentData
import de.fabmax.kool.editor.data.RigidActorType
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.*
import de.fabmax.kool.util.HeightMap
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

fun RigidActorComponent(nodeModel: SceneNodeModel): RigidActorComponent {
    return RigidActorComponent(nodeModel, RigidActorComponentData())
}

class RigidActorComponent(nodeModel: SceneNodeModel, override val componentData: RigidActorComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<RigidActorComponentData>,
    PhysicsComponent,
    UpdateMeshComponent
{
    val actorState = mutableStateOf(componentData.properties).onChange {
        if (AppState.isEditMode) {
            componentData.properties = it
        }
        launchOnMainThread {
            updateRigidActor()
        }
    }

    private var rigidActor: RigidActor? = null

    private var geometry: List<CollisionGeometry> = emptyList()
    private var bodyShapes: List<ShapeData> = emptyList()

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
        geometry.forEach { it.release() }
        geometry = emptyList()
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

    private suspend fun updateRigidActor() {
        val actor = rigidActor
        val isActorOk = when (componentData.properties.type) {
            RigidActorType.DYNAMIC -> actor is RigidDynamic && !actor.isKinematic
            RigidActorType.KINEMATIC -> actor is RigidDynamic && actor.isKinematic
            RigidActorType.STATIC -> actor is RigidStatic
        }

        if (!isActorOk || componentData.properties.shapes != bodyShapes) {
            createRigidBody()

        } else if (actor is RigidDynamic) {
            actor.mass = componentData.properties.mass
        }
    }

    private suspend fun createRigidBody() {
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
        geometry.forEach { it.release() }

        rigidActor = when (componentData.properties.type) {
            RigidActorType.DYNAMIC -> RigidDynamic(componentData.properties.mass)
            RigidActorType.KINEMATIC -> RigidDynamic(componentData.properties.mass, isKinematic = true)
            RigidActorType.STATIC -> RigidStatic()
        }

        rigidActor?.apply {
            bodyShapes = componentData.properties.shapes
            geometry = if (bodyShapes.isEmpty()) {
                makeMeshGeometry()
            } else {
                bodyShapes.mapNotNull { shape ->
                    when (shape) {
                        is ShapeData.Box -> BoxGeometry(shape.size.toVec3f())
                        is ShapeData.Capsule -> CapsuleGeometry(shape.length.toFloat(), shape.radius.toFloat())
                        is ShapeData.Cylinder -> CylinderGeometry(shape.length.toFloat(), shape.topRadius.toFloat())
                        is ShapeData.Sphere -> SphereGeometry(shape.radius.toFloat())
                        is ShapeData.Heightmap -> loadHeightmapGeometry(shape)
                        is ShapeData.Rect -> null
                        is ShapeData.Empty -> null
                    }
                }
            }
            geometry.forEach { attachShape(Shape(it)) }
            physicsWorld?.addActor(this)
        }
        setPhysicsTransformFromModel()
    }

    private suspend fun loadHeightmapGeometry(shapeData: ShapeData.Heightmap): CollisionGeometry? {
        if (shapeData.mapPath.isBlank()) {
            return null
        }
        val heightData = AppAssets.loadBlob(shapeData.mapPath) ?: return null
        val heightMap = HeightMap.fromRawData(heightData, shapeData.heightScale, heightOffset = shapeData.heightOffset)
        val heightField = HeightField(heightMap, shapeData.rowScale, shapeData.colScale)
        return HeightFieldGeometry(heightField)
    }

    private fun makeMeshGeometry(): List<CollisionGeometry> {
        val mesh = nodeModel.getComponent<MeshComponent>()
        if (mesh == null) {
            logE { "Node ${nodeModel.name}: Failed attaching mesh shape to rigid actor: has no attached MeshComponent" }
            return emptyList()
        }

        return mesh.componentData.shapes.mapNotNull { meshShape ->
            when (meshShape) {
                is ShapeData.Box -> BoxGeometry(meshShape.size.toVec3f())
                is ShapeData.Capsule -> CapsuleGeometry(meshShape.length.toFloat(), meshShape.radius.toFloat())
                is ShapeData.Cylinder -> CylinderGeometry(meshShape.length.toFloat(), meshShape.topRadius.toFloat())
                is ShapeData.Sphere -> SphereGeometry(meshShape.radius.toFloat())
                else -> {
                    logE { "Node ${nodeModel.name}: Mesh shape is not supported as rigid actor shape: $meshShape" }
                    null
                }
            }
        }
    }

    private fun setPhysicsTransformFromModel() {
        rigidActor?.apply {
            position = nodeModel.transform.componentData.transform.position.toVec3f()
            rotation = nodeModel.transform.componentData.transform.rotation.toQuatF()
        }
    }

    override fun updateMesh(mesh: MeshComponentData) {
        if (componentData.properties.shapes.isEmpty()) {
            launchOnMainThread {
                createRigidBody()
            }
        }
    }
}