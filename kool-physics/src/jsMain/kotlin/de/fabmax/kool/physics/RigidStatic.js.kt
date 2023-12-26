package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import physx.PxRigidStatic

actual fun RigidStatic(pose: Mat4f): RigidStatic = RigidStaticImpl(pose)

class RigidStaticImpl(pose: Mat4f) : RigidActorImpl(), RigidStatic {

    private val pxRigidStatic: PxRigidStatic

    override val holder: RigidActorHolder

    init {
        MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            pxRigidStatic = PhysicsImpl.physics.createRigidStatic(pxPose)
            holder = RigidActorHolder(pxRigidStatic)
        }
        transform.setMatrix(pose)
    }
}