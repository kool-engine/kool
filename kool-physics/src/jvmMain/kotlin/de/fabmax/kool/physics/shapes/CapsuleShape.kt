package de.fabmax.kool.physics.shapes

import de.fabmax.kool.physics.btCapsuleShape

@Suppress("CanBeParameter")
actual class CapsuleShape actual constructor(actual val height: Float, actual val radius: Float) : CollisionShape() {

    override val shape: btCapsuleShape = btCapsuleShape(radius, height)

}