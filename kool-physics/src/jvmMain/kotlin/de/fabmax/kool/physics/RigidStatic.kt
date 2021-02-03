package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import physx.physics.PxRigidStatic

actual class RigidStatic actual constructor(pose: Mat4f) : RigidActor() {

    private val pxRigidStatic: PxRigidStatic

    init {
        pose.toPxTransform(pxPose)
        pxRigidStatic = Physics.physics.createRigidStatic(pxPose)
        pxRigidActor = pxRigidStatic
    }
}