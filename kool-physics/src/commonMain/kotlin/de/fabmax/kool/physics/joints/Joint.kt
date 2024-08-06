package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.util.Releasable

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class JointHolder

interface Joint : Releasable {

    val joint: JointHolder

    val bodyA: RigidActor?
    val bodyB: RigidActor

    val frameA: PoseF
    val frameB: PoseF

    val isBroken: Boolean
    var debugVisualize: Boolean

    fun setBreakForce(force: Float, torque: Float)
}