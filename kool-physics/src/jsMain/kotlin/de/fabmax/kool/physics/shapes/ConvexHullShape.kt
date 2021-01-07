package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.IndexedVertexList
import physx.*

actual class ConvexHullShape actual constructor(points: List<Vec3f>) : CommonConvexHullShape(points), CollisionShape {

    init {
        Physics.checkIsLoaded()

    }

    override fun getAabb(result: BoundingBox) = result
    override fun getBoundingSphere(result: MutableVec4f) = result
    override fun attachTo(
        actor: PxRigidActor,
        material: PxMaterial,
        flags: PxShapeFlags,
        collisionFilter: PxFilterData
    ): PxShape? {
        return null
    }

    override val geometry = IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)
}