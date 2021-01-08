package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.BtCollisionShape
import de.fabmax.kool.physics.toVec3f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder
import javax.vecmath.Vector3f

actual interface CollisionShape {

    val btShape: BtCollisionShape

    actual fun generateGeometry(target: MeshBuilder)

    actual fun getAabb(result: BoundingBox): BoundingBox

    actual fun getBoundingSphere(result: MutableVec4f): MutableVec4f

    actual fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f

    fun getBtInertia(mass: Float, result: MutableVec3f): MutableVec3f {
        val v = Vector3f()
        btShape.calculateLocalInertia(mass, v)
        return v.toVec3f(result)
    }
}
