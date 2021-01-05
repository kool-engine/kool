package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.BtCollisionShape
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder

actual interface CollisionShape {

    val btShape: BtCollisionShape

    actual fun generateGeometry(target: MeshBuilder)

    actual fun getAabb(result: BoundingBox): BoundingBox

    actual fun getBoundingSphere(result: MutableVec4f): MutableVec4f

}
