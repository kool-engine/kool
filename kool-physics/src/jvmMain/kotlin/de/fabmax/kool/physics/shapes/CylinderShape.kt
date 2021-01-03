package de.fabmax.kool.physics.shapes

import de.fabmax.kool.physics.BtCylinderShape
import javax.vecmath.Vector3f

actual class CylinderShape actual constructor(height: Float, radius: Float) : CommonCylinderShape(height, radius), CollisionShape {

    override val btShape: BtCylinderShape

    init {
        val halfExtents = Vector3f(radius, height / 2, radius)
        btShape = BtCylinderShape(halfExtents)
    }

}