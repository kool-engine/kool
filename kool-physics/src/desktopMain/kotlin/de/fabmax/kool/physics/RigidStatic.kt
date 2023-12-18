package de.fabmax.kool.physics

import de.fabmax.kool.math.Mat4f
import org.lwjgl.system.MemoryStack
import physx.physics.PxRigidActor
import physx.physics.PxRigidStatic

actual fun RigidStatic(pose: Mat4f): RigidStatic = RigidStaticImpl(pose)

class RigidStaticImpl(pose: Mat4f) : RigidActorImpl(), RigidStatic {

    private val pxRigidStatic: PxRigidStatic

    override val holder: PxRigidActor

    init {
        MemoryStack.stackPush().use { mem ->
            val pxPose = pose.toPxTransform(mem.createPxTransform())
            pxRigidStatic = PhysicsImpl.physics.createRigidStatic(pxPose)
            holder = pxRigidStatic
        }
        transform.setMatrix(pose)
    }
}