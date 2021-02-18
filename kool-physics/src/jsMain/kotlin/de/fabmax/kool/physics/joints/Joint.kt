package de.fabmax.kool.physics.joints

import de.fabmax.kool.physics.Releasable
import physx.PxJoint

actual interface Joint : Releasable {

    val pxJoint: PxJoint

    override fun release() {
        pxJoint.release()
    }
}