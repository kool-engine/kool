package de.fabmax.kool.physics.joints

import physx.extensions.PxJoint

actual interface Joint {

    val pxJoint: PxJoint

}