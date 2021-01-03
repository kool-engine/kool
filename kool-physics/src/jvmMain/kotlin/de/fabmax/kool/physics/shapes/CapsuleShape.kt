package de.fabmax.kool.physics.shapes

import de.fabmax.kool.physics.BtCapsuleShape

actual class CapsuleShape actual constructor(height: Float, radius: Float) : CommonCapsuleShape(height, radius), CollisionShape {

    override val btShape: BtCapsuleShape = BtCapsuleShape(radius, height)

}