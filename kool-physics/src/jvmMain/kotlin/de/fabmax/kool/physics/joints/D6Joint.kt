package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.*

actual enum class D6JointMotion(val pxVal: Int) {
    Free(PxD6MotionEnum.eFREE),
    Limited(PxD6MotionEnum.eLIMITED),
    Locked(PxD6MotionEnum.eLOCKED);

    companion object {
        fun fromPx(pxVal: Int) = when (pxVal) {
            PxD6MotionEnum.eFREE -> Free
            PxD6MotionEnum.eLIMITED -> Limited
            PxD6MotionEnum.eLOCKED -> Locked
            else -> throw RuntimeException()
        }
    }
}

actual class D6Joint actual constructor(
    actual val bodyA: RigidActor,
    actual val bodyB: RigidActor,
    posA: Mat4f,
    posB: Mat4f
) : Joint() {

    actual val frameA = Mat4f().set(posA)
    actual val frameB = Mat4f().set(posB)

    override val pxJoint: PxD6Joint

    init {
        Physics.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.D6JointCreate(Physics.physics, bodyA.pxRigidActor, frmA, bodyB.pxRigidActor, frmB)
        }
    }

    actual var motionX: D6JointMotion
        get() = D6JointMotion.fromPx(pxJoint.getMotion(PxD6AxisEnum.eX))
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eX, value.pxVal)

    actual var motionY: D6JointMotion
        get() = D6JointMotion.fromPx(pxJoint.getMotion(PxD6AxisEnum.eY))
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eY, value.pxVal)

    actual var motionZ: D6JointMotion
        get() = D6JointMotion.fromPx(pxJoint.getMotion(PxD6AxisEnum.eZ))
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eZ, value.pxVal)

    actual fun setDistanceLimit(extend: Float, stiffness: Float, damping: Float) {
        pxJoint.setDistanceLimit(PxJointLinearLimit(extend, PxSpring(stiffness, damping)))
    }

    actual fun setXLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        pxJoint.setLinearLimit(PxD6AxisEnum.eX,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionX = D6JointMotion.Limited
    }

    actual fun setYLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        pxJoint.setLinearLimit(PxD6AxisEnum.eY,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionY = D6JointMotion.Limited
    }

    actual fun setZLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        pxJoint.setLinearLimit(PxD6AxisEnum.eZ,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionZ = D6JointMotion.Limited
    }
}