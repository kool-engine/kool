package de.fabmax.kool.physics.joints

import de.fabmax.kool.physics.Releasable
import physx.PxConstraintFlagEnum
import physx.PxJoint
import physx.constraintFlags

actual abstract class Joint : Releasable {

    abstract val pxJoint: PxJoint

    actual fun setBreakForce(force: Float, torque: Float) = pxJoint.setBreakForce(force, torque)

    override fun release() {
        pxJoint.release()
    }

    actual val isBroken: Boolean
        get() = pxJoint.constraintFlags.isSet(PxConstraintFlagEnum.eBROKEN)

    actual var debugVisualize: Boolean = false
        set(value) = if ( value ) {
            pxJoint.constraintFlags.set(PxConstraintFlagEnum.eVISUALIZATION)
        } else {
            pxJoint.constraintFlags.clear(PxConstraintFlagEnum.eVISUALIZATION)
        }
}