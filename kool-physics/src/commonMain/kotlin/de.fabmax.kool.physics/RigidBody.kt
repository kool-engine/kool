package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.physics.shapes.CollisionShape

expect class RigidBody(collisionShape: CollisionShape, mass: Float): CommonRigidBody {
    val collisionShape: CollisionShape
    val mass: Float

    val transform: Mat4f

    var origin: Vec3f
    var rotation: Vec4f

    fun setRotation(rotation: Mat3f)
}

abstract class CommonRigidBody {
    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    open fun fixedUpdate(timeStep: Float) {
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }
}
