package de.fabmax.kool.physics.joints

import de.fabmax.kool.physics.Releasable
import physx.PxJoint

actual interface Joint : Releasable {

    val pxJoint: PxJoint

    actual val isBroken: Boolean

    actual fun setBreakForce(force: Float, torque: Float)

    override fun release() {
        pxJoint.release()
    }
}