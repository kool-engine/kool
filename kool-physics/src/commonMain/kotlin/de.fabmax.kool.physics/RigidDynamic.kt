package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f

expect open class RigidDynamic(mass: Float = 1f, pose: Mat4f = Mat4f()) : RigidActor {

    var mass: Float
    var inertia: Vec3f

    var linearVelocity: Vec3f
    var angularVelocity: Vec3f

    var maxLinearVelocity: Float
    var maxAngularVelocity: Float

    var linearDamping: Float
    var angularDamping: Float

}
