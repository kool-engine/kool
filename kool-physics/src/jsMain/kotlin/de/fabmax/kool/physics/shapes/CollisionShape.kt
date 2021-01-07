package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder
import physx.*

actual interface CollisionShape {

    actual fun generateGeometry(target: MeshBuilder)

    actual fun getAabb(result: BoundingBox): BoundingBox

    actual fun getBoundingSphere(result: MutableVec4f): MutableVec4f

    fun attachTo(actor: PxRigidActor, material: PxMaterial, flags: PxShapeFlags, collisionFilter: PxFilterData): PxShape?

}