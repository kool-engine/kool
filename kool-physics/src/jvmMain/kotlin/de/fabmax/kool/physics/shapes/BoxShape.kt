package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.BtBoxShape
import de.fabmax.kool.physics.toBtVector3f

actual class BoxShape actual constructor(size: Vec3f) : CommonBoxShape(size), CollisionShape {

    override val btShape: BtBoxShape = BtBoxShape(size.scale(0.5f, MutableVec3f()).toBtVector3f())

}