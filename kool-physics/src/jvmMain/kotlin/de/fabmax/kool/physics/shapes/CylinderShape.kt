package de.fabmax.kool.physics.shapes

import de.fabmax.kool.physics.btCylinderShape
import javax.vecmath.Vector3f

@Suppress("CanBeParameter")
actual class CylinderShape actual constructor(actual val radius: Float, actual val height: Float) : CollisionShape() {

    override val shape: btCylinderShape

    init {
        val halfExtents = Vector3f(radius, height / 2, radius)
        shape = btCylinderShape(halfExtents)
    }

}