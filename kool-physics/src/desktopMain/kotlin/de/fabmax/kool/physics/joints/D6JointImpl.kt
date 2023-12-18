package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.*

class D6JointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: Mat4f,
    frameB: Mat4f
) : JointImpl(frameA, frameB), D6Joint {

    override val joint: PxD6Joint

    init {
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            joint = PxTopLevelFunctions.D6JointCreate(PhysicsImpl.physics, bodyA.holder, frmA, bodyB.holder, frmB)
        }
    }

    override var motionX: D6JointMotion
        get() = joint.getMotion(PxD6AxisEnum.eX).toD6JointMotion()
        set(value) = joint.setMotion(PxD6AxisEnum.eX, value.toPxD6MotionEnum())

    override var motionY: D6JointMotion
        get() = joint.getMotion(PxD6AxisEnum.eY).toD6JointMotion()
        set(value) = joint.setMotion(PxD6AxisEnum.eY, value.toPxD6MotionEnum())

    override var motionZ: D6JointMotion
        get() = joint.getMotion(PxD6AxisEnum.eZ).toD6JointMotion()
        set(value) = joint.setMotion(PxD6AxisEnum.eZ, value.toPxD6MotionEnum())

    override fun setDistanceLimit(extend: Float, stiffness: Float, damping: Float) {
        joint.setDistanceLimit(PxJointLinearLimit(extend, PxSpring(stiffness, damping)))
    }

    override fun setXLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        joint.setLinearLimit(PxD6AxisEnum.eX,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionX = D6JointMotion.Limited
    }

    override fun setYLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        joint.setLinearLimit(PxD6AxisEnum.eY,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionY = D6JointMotion.Limited
    }

    override fun setZLinearLimit(lowerLimit: Float, upperLimit: Float, stiffness: Float, damping: Float) {
        joint.setLinearLimit(PxD6AxisEnum.eZ,
            PxJointLinearLimitPair(lowerLimit, upperLimit, PxSpring(stiffness, damping)))
        motionZ = D6JointMotion.Limited
    }

    companion object {
        private fun PxD6MotionEnum.toD6JointMotion(): D6JointMotion = when (this) {
            PxD6MotionEnum.eFREE -> D6JointMotion.Free
            PxD6MotionEnum.eLIMITED -> D6JointMotion.Limited
            PxD6MotionEnum.eLOCKED -> D6JointMotion.Locked
        }

        private fun D6JointMotion.toPxD6MotionEnum(): PxD6MotionEnum = when (this) {
            D6JointMotion.Free -> PxD6MotionEnum.eFREE
            D6JointMotion.Limited -> PxD6MotionEnum.eLIMITED
            D6JointMotion.Locked -> PxD6MotionEnum.eLOCKED
        }
    }
}