package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.*

actual class PrismaticJoint actual constructor(
    val bodyA: RigidActor,
    val bodyB: RigidActor,
    posA: Mat4f,
    posB: Mat4f
) : Joint() {
    actual val frameA = Mat4f().set(posA)
    actual val frameB = Mat4f().set(posB)
    override val pxJoint: PxPrismaticJoint

    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.PrismaticJointCreate(Physics.physics, bodyA.pxRigidActor, frmA, bodyB.pxRigidActor, frmB)
        }
    }

    actual fun setLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        pxJoint.setLimit(PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, true)
    }

    actual fun removeLimit() {
        pxJoint.setPrismaticJointFlag(PxPrismaticJointFlagEnum.eLIMIT_ENABLED, false)
    }
}