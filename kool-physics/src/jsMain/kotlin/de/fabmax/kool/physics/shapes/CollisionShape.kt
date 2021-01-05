package de.fabmax.kool.physics.shapes

import ammo.btCollisionShape
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder

actual interface CollisionShape {

    val btShape: btCollisionShape

    actual fun generateGeometry(target: MeshBuilder)

    actual fun getAabb(result: BoundingBox): BoundingBox

    actual fun getBoundingSphere(result: MutableVec4f): MutableVec4f

}