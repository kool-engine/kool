package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.RigidActorComponentData
import de.fabmax.kool.editor.data.RigidActorType
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.QuatD
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.toQuatF
import de.fabmax.kool.math.toVec3f
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.*
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE

class RigidActorComponent(
    nodeModel: SceneNodeModel,
    override val componentData: RigidActorComponentData = RigidActorComponentData()
) :
    PhysicsNodeComponent(nodeModel),
    EditorDataComponent<RigidActorComponentData>,
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

    override val actorTransform: TrsTransformF? get() = rigidActor?.transform

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
    }

    override fun destroyComponent() {
        super.destroyComponent()
        rigidActor?.let {
            physicsWorld?.removeActor(it)
            it.release()
        }
        rigidActor = null
        geometry.forEach { it.release() }
        geometry = emptyList()
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
        val physicsWorldComponent = getOrCreatePhysicsWorldComponent()
        val physicsWorld = physicsWorldComponent.physicsWorld
        if (physicsWorld == null) {
            logE { "Unable to create rigid body: parent physics world was not yet created" }
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

        setPhysicsTransformFromDrawNode()
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

    override fun applyPose(position: Vec3d, rotation: QuatD) {
        rigidActor?.apply {
            this.position = position.toVec3f()
            this.rotation = rotation.toQuatF()
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