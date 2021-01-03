package de.fabmax.kool.physics.shapes

import de.fabmax.kool.physics.BtSphereShape

@Suppress("CanBeParameter")
actual class SphereShape actual constructor(radius: Float) : CommonSphereShape(radius), CollisionShape {

    override val btShape: BtSphereShape = BtSphereShape(radius)

}