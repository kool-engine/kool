package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBodyProperties
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList
import physx.PxMaterial
import physx.PxRigidActor
import physx.PxShape
import physx.PxShapeFlags

actual class TriangleMeshShape actual constructor(geometry: IndexedVertexList) : CommonTriangleMeshShape(geometry), CollisionShape {

    init {
        Physics.checkIsLoaded()

        TODO()
    }

    override fun getAabb(result: BoundingBox): BoundingBox = TODO()
    override fun getBoundingSphere(result: MutableVec4f): MutableVec4f = TODO()
    override fun attachTo(
        actor: PxRigidActor,
        material: PxMaterial,
        flags: PxShapeFlags,
        bodyProps: RigidBodyProperties
    ): PxShape? {
        return null
    }

    override fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f {
        // tri mesh does not have a meaningful inertia
        return result.set(1f, 1f, 1f)
    }
}