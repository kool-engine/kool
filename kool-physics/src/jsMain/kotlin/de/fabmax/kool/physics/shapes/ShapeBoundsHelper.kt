package de.fabmax.kool.physics.shapes

import ammo.Ammo
import ammo.btCollisionShape
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.util.BoundingBox

class ShapeBoundsHelper(val btShape: btCollisionShape) {
    private val tmpV1 = Ammo.btVector3(0f, 0f, 0f)
    private val tmpV2 = Ammo.btVector3(0f, 0f, 0f)

    fun getAabb(result: BoundingBox): BoundingBox {
        btShape.getAabb(Ammo.IDENTITY, tmpV1, tmpV2)
        return result.set(tmpV1.x(), tmpV1.y(), tmpV1.z(), tmpV2.x(), tmpV2.y(), tmpV2.z())
    }

    fun getBoundingSphere(result: MutableVec4f): MutableVec4f {
        val r = btShape.getBoundingSphereAndRadius(tmpV1)
        return result.set(tmpV1.x(), tmpV1.y(), tmpV1.z(), r)
    }
}