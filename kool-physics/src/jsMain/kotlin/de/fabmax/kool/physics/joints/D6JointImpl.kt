package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.*
import physx.*

class D6JointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: Mat4f,
    frameB: Mat4f
) : JointImpl(frameA, frameB), D6Joint {

    override val pxJoint: PxD6Joint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.D6JointCreate(PhysicsImpl.physics, bodyA.holder.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override var motionX: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eX).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eX, value.toPxD6MotionEnum())

    override var motionY: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eY).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eY, value.toPxD6MotionEnum())

    override var motionZ: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eZ).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eZ, value.toPxD6MotionEnum())

    override fun setDistanceLimit(extend: Float, stiffness: Float, damping: Float) {
        pxJoint.setDistanceLimit(PxJointLinearLimit(extend, PxSpring(stiffness, damping)))
    }

    override fun setXLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        pxJoint.setLinearLimit(PxD6AxisEnum.eX,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionX = D6JointMotion.Limited
    }

    override fun setYLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        pxJoint.setLinearLimit(PxD6AxisEnum.eY,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionY = D6JointMotion.Limited
    }

    override fun setZLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        pxJoint.setLinearLimit(PxD6AxisEnum.eZ,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionZ = D6JointMotion.Limited
    }

    companion object {
        private fun Int.toD6JointMotion(): D6JointMotion = when (this) {
            PxD6MotionEnum.eFREE -> D6JointMotion.Free
            PxD6MotionEnum.eLIMITED -> D6JointMotion.Limited
            PxD6MotionEnum.eLOCKED -> D6JointMotion.Locked
            else -> throw RuntimeException()
        }

        fun D6JointMotion.toPxD6MotionEnum(): Int = when (this) {
            D6JointMotion.Free -> PxD6MotionEnum.eFREE
            D6JointMotion.Limited -> PxD6MotionEnum.eLIMITED
            D6JointMotion.Locked -> PxD6MotionEnum.eLOCKED
        }
    }
}