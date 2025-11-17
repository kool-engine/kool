package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.util.BaseReleasable
import physx.extensions.PxJoint
import physx.physics.PxConstraintFlagEnum

actual class JointHolder(val px: PxJoint)

abstract class JointImpl(frameA: PoseF, frameB: PoseF) : BaseReleasable(), Joint {
    override val frameA = PoseF(frameA)
    override val frameB = PoseF(frameB)

    abstract val pxJoint: PxJoint
    override val joint: JointHolder by lazy { JointHolder(pxJoint) }

    override val isBroken: Boolean
        get() = pxJoint.constraintFlags.isSet(PxConstraintFlagEnum.eBROKEN)

    override var isChildCollisionEnabled: Boolean = false
        set(value) {
            field = value
            if (value) {
                pxJoint.setConstraintFlag(PxConstraintFlagEnum.eCOLLISION_ENABLED, true)
            } else {
                pxJoint.setConstraintFlag(PxConstraintFlagEnum.eCOLLISION_ENABLED, false)
            }
        }

    override var debugVisualize: Boolean = false
        set(value) {
            field = value
            if (value) {
                pxJoint.setConstraintFlag(PxConstraintFlagEnum.eVISUALIZATION, true)
            } else {
                pxJoint.setConstraintFlag(PxConstraintFlagEnum.eVISUALIZATION, false)
            }
        }

    override fun enableBreakage(breakForce: Float, breakTorque: Float) = pxJoint.setBreakForce(breakForce, breakTorque)

    override fun doRelease() {
        pxJoint.release()
    }
}