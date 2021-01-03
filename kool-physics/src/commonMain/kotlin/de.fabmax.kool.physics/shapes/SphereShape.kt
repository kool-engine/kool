package de.fabmax.kool.physics.shapes

import de.fabmax.kool.util.MeshBuilder

expect class SphereShape(radius: Float) : CommonSphereShape, CollisionShape

abstract class CommonSphereShape(val radius: Float) {

    open fun generateGeometry(target: MeshBuilder) {
        target.icoSphere {
            radius = this@CommonSphereShape.radius
            steps = 2
        }
    }

}
