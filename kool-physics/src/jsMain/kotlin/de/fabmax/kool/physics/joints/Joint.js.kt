package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.util.BaseReleasable
import physx.PxConstraintFlagEnum
import physx.PxJoint
import physx.constraintFlags

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class JointHolder(val px: PxJoint)

abstract class JointImpl(frameA: PoseF, frameB: PoseF) : BaseReleasable(), Joint {
    override val frameA = PoseF(frameA)
    override val frameB = PoseF(frameB)

    abstract val pxJoint: PxJoint

    override val joint: JointHolder by lazy { JointHolder(pxJoint) }

    override val isBroken: Boolean
        get() = pxJoint.constraintFlags.isSet(PxConstraintFlagEnum.eBROKEN)

    override var debugVisualize: Boolean = false
        set(value) = if (value) {
            pxJoint.constraintFlags.raise(PxConstraintFlagEnum.eVISUALIZATION)
        } else {
            pxJoint.constraintFlags.clear(PxConstraintFlagEnum.eVISUALIZATION)
        }

    override fun setBreakForce(force: Float, torque: Float) = pxJoint.setBreakForce(force, torque)

    override fun release() {
        super.release()
        pxJoint.release()
    }
}