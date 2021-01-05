package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.BtCollisionShape
import de.fabmax.kool.physics.BulletObjects
import de.fabmax.kool.util.BoundingBox
import javax.vecmath.Vector3f

class ShapeBoundsHelper(val btShape: BtCollisionShape) {
    private val tmpV1 = Vector3f()
    private val tmpV2 = Vector3f()
    private val tmpR = FloatArray(1)

    fun getAabb(result: BoundingBox): BoundingBox {
        btShape.getAabb(BulletObjects.IDENTITY, tmpV1, tmpV2)
        return result.set(tmpV1.x, tmpV1.y, tmpV1.z, tmpV2.x, tmpV2.y, tmpV2.z)
    }

    fun getBoundingSphere(result: MutableVec4f): MutableVec4f {
        btShape.getBoundingSphere(tmpV1, tmpR)
        return result.set(tmpV1.x, tmpV1.y, tmpV1.z, tmpR[0])
    }
}