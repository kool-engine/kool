package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidActor

actual fun RevoluteJoint(
    bodyA: RigidActor?,
    bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
): RevoluteJoint {
    TODO("Not yet implemented")
}

actual fun RevoluteJoint(
    bodyA: RigidActor?,
    bodyB: RigidActor,
    pivotA: Vec3f,
    pivotB: Vec3f,
    axisA: Vec3f,
    axisB: Vec3f
): RevoluteJoint {
    TODO("Not yet implemented")
}