package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.data.RigidActorComponentData
import de.fabmax.kool.editor.data.RigidActorType
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.scene.geometry.IndexedVertexList
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

    var rigidActor: RigidActor? = null
        private set

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
            actor.updateInertiaFromShapesAndMass()
            actor.characterControllerHitBehavior = componentData.properties.characterControllerHitBehavior
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

        scale.set(Vec3d.ONES)

        requiredAssets.clear()
        rigidActor?.apply {
            bodyShapes = componentData.properties.shapes

            val meshComp = nodeModel.getComponent<MeshComponent>()
            val modelComp = nodeModel.getComponent<ModelComponent>()

            val shapes = when {
                bodyShapes.isNotEmpty() -> bodyShapes.mapNotNull { shape -> shape.makeCollisionGeometry() }
                meshComp != null -> meshComp.makeCollisionShapes()
                modelComp != null -> modelComp.makeCollisionShapes()
                else -> emptyList()
            }

            shapes.forEach { (shape, pose) -> attachShape(Shape(shape, localPose = pose)) }
            geometry = shapes.map { it.first }
            characterControllerHitBehavior = componentData.properties.characterControllerHitBehavior

            if (this is RigidDynamic) {
                updateInertiaFromShapesAndMass()
            }
            physicsWorld?.addActor(this)
        }

        setPhysicsTransformFromDrawNode()
    }

    private suspend fun MeshComponent.makeCollisionShapes(): List<Pair<CollisionGeometry, Mat4f>> {
        return componentData.shapes.mapNotNull { shape -> shape.makeCollisionGeometry(mesh) }
    }

    private fun ModelComponent.makeCollisionShapes(): List<Pair<CollisionGeometry, Mat4f>> {
        val model = this.model ?: return emptyList()

        model.transform.decompose(scale = scale)

        val collisionGeom = IndexedVertexList(Attribute.POSITIONS)
        val globalToModel = model.invModelMatD
        model.meshes.values.forEach { mesh ->
            val meshToModel = mesh.modelMatD * globalToModel
            collisionGeom.addGeometry(mesh.geometry) {
                meshToModel.transform(position, 1f)
            }
        }
        return listOf(collisionGeom.makeTriMeshGeometry(scale.toVec3f()) to Mat4f.IDENTITY)
    }

    private suspend fun ShapeData.makeCollisionGeometry(mesh: Mesh? = null): Pair<CollisionGeometry, Mat4f>? {
        return when (this) {
            is ShapeData.Box -> BoxGeometry(size.toVec3f()) to Mat4f.IDENTITY
            is ShapeData.Capsule -> CapsuleGeometry(length.toFloat(), radius.toFloat()) to Mat4f.IDENTITY
            is ShapeData.Cylinder -> CylinderGeometry(length.toFloat(), bottomRadius.toFloat()) to Mat4f.IDENTITY
            is ShapeData.Sphere -> SphereGeometry(radius.toFloat()) to Mat4f.IDENTITY
            is ShapeData.Heightmap -> loadHeightmapGeometry(this)?.let { it to Mat4f.IDENTITY }
            is ShapeData.Plane -> PlaneGeometry() to Mat4f.rotation(90f.deg, Vec3f.Z_AXIS)
            is ShapeData.Rect -> mesh?.let { it.geometry.makeTriMeshGeometry(Vec3f.ONES) to Mat4f.IDENTITY }
            is ShapeData.Custom -> null
        }
    }

    private fun IndexedVertexList.makeTriMeshGeometry(scale: Vec3f): TriangleMeshGeometry {
        return TriangleMeshGeometry(this, scale)
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