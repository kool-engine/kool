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

    override var isChildCollisionEnabled: Boolean = false
        set(value) {
            field = value
            if (value) {
                joint.setConstraintFlag(PxConstraintFlagEnum.eCOLLISION_ENABLED, true)
            } else {
                joint.setConstraintFlag(PxConstraintFlagEnum.eCOLLISION_ENABLED, false)
            }
        }

    override var debugVisualize: Boolean = false
        set(value) {
            field = value
            if (value) {
                joint.setConstraintFlag(PxConstraintFlagEnum.eVISUALIZATION, true)
            } else {
                joint.setConstraintFlag(PxConstraintFlagEnum.eVISUALIZATION, false)
            }
        }

    override fun enableBreakage(breakForce: Float, breakTorque: Float) = joint.setBreakForce(breakForce, breakTorque)

    override fun release() {
        super.release()
        joint.release()
    }
}