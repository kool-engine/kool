package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.PxFixedJoint
import physx.physics.PxConstraintFlagEnum

actual class FixedJoint actual constructor(actual val bodyA: RigidActor, actual val bodyB: RigidActor,
                                           frameA: Mat4f, frameB: Mat4f) : Joint() {

    actual val frameA = Mat4f().set(frameA)
    actual val frameB = Mat4f().set(frameB)

    override val pxJoint: PxFixedJoint

    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.FixedJointCreate(Physics.physics, bodyA.pxRigidActor, frmA, bodyB.pxRigidActor, frmB)
        }
    }
}