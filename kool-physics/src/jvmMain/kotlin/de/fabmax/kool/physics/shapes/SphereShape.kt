package de.fabmax.kool.physics.shapes

import de.fabmax.kool.physics.btSphereShape

@Suppress("CanBeParameter")
actual class SphereShape actual constructor(actual val radius: Float) : CollisionShape() {

    override val shape: btSphereShape = btSphereShape(radius)

}