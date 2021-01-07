package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList
import physx.*

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
        collisionFilter: PxFilterData
    ): PxShape? {
        return null
    }
}