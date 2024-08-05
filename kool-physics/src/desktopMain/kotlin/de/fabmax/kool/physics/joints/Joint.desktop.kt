package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.util.BaseReleasable
import physx.extensions.PxJoint
import physx.physics.PxConstraintFlagEnum

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual typealias JointHolder = PxJoint

abstract class JointImpl(frameA: PoseF, frameB: PoseF) : BaseReleasable(), Joint {
    override val frameA = PoseF(frameA)
    override val frameB = PoseF(frameB)

    override val isBroken: Boolean
        get() = joint.constraintFlags.isSet(PxConstraintFlagEnum.eBROKEN)

    override var debugVisualize: Boolean = false
        set(value) = if (value) {
            joint.constraintFlags.raise(PxConstraintFlagEnum.eVISUALIZATION)
        } else {
            joint.constraintFlags.clear(PxConstraintFlagEnum.eVISUALIZATION)
        }

    override fun setBreakForce(force: Float, torque: Float) = joint.setBreakForce(force, torque)

    override fun release() {
        super.release()
        joint.release()
    }
}