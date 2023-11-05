package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.physics.RigidActor

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class JointHolder

interface Joint : Releasable {

    val joint: JointHolder

    val bodyA: RigidActor
    val bodyB: RigidActor

    val frameA: Mat4f
    val frameB: Mat4f

    val isBroken: Boolean
    var debugVisualize: Boolean

    fun setBreakForce(force: Float, torque: Float)

    override fun release()
}