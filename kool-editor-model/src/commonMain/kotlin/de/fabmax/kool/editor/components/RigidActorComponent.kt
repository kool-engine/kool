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
import de.fabmax.kool.util.launchOnMainThread
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

    init {
        dependsOn(MeshComponent::class, isOptional = true)
        dependsOn(ModelComponent::class, isOptional = true)

        componentData.properties.shapes
            .filterIsInstance<ShapeData.Heightmap>()
            .filter{ it.mapPath.isNotBlank() }
            .forEach { requiredAssets += it.toAssetReference() }
    }

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
            actor.mass = componentData.properties.mass.toFloat()
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
            RigidActorType.DYNAMIC -> RigidDynamic(componentData.properties.mass.toFloat())
            RigidActorType.KINEMATIC -> RigidDynamic(componentData.properties.mass.toFloat(), isKinematic = true)
            RigidActorType.STATIC -> RigidStatic()
        }

        requiredAssets.clear()
        rigidActor?.apply {
            bodyShapes = componentData.properties.shapes
            geometry = if (bodyShapes.isEmpty()) {
                nodeModel.getComponent<MeshComponent>()?.componentData?.shapes
                    ?.mapNotNull { shape -> shape.makeCollisionGeometry() }
                    ?: emptyList()
            } else {
                bodyShapes.mapNotNull { shape -> shape.makeCollisionGeometry() }
            }
            geometry.forEach { attachShape(Shape(it)) }
            physicsWorld?.addActor(this)
        }
        setPhysicsTransformFromModel()
    }

    private suspend fun ShapeData.makeCollisionGeometry(): CollisionGeometry? {
        return when (this) {
            is ShapeData.Box -> BoxGeometry(size.toVec3f())
            is ShapeData.Capsule -> CapsuleGeometry(length.toFloat(), radius.toFloat())
            is ShapeData.Cylinder -> CylinderGeometry(length.toFloat(), bottomRadius.toFloat())
            is ShapeData.Sphere -> SphereGeometry(radius.toFloat())
            is ShapeData.Heightmap -> loadHeightmapGeometry(this)
            is ShapeData.Rect -> null
            is ShapeData.Empty -> null
        }
    }

    private suspend fun loadHeightmapGeometry(shapeData: ShapeData.Heightmap): CollisionGeometry? {
        if (shapeData.mapPath.isBlank()) {
            return null
        }
        val heightmapRef = shapeData.toAssetReference()
        requiredAssets += heightmapRef
        val heightmap = AppAssets.loadHeightmap(heightmapRef) ?: return null
        val heightField = HeightField(heightmap, shapeData.rowScale.toFloat(), shapeData.colScale.toFloat())
        return HeightFieldGeometry(heightField)
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