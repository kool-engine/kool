package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.util.memStack
import physxandroid.physics.PxRigidStatic

// GENERATED CODE BELOW:
// Transformed from desktop source

actual fun RigidStatic(pose: Mat4f): RigidStatic = RigidStaticImpl(pose)

class RigidStaticImpl(pose: Mat4f) : RigidActorImpl(), RigidStatic {
    private val pxRigidStatic: PxRigidStatic
    override val holder: RigidActorHolder

    init {
        memStack {
            val pxPose = pose.toPxTransform(createPxTransform())
            pxRigidStatic = PhysicsImpl.physics.createRigidStatic(pxPose)
            holder = RigidActorHolder(pxRigidStatic)
        }
        transform.setMatrix(pose)
        syncSimulationData()
    }
}