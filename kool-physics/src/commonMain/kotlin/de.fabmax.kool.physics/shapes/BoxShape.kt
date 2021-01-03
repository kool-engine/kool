package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.MeshBuilder

expect class BoxShape(size: Vec3f) : CommonBoxShape, CollisionShape

abstract class CommonBoxShape(val size: Vec3f) {

    open fun generateGeometry(target: MeshBuilder) {
        target.cube {
            size.set(this@CommonBoxShape.size)
            origin.set(size).scale(-0.5f)
        }
    }

}
