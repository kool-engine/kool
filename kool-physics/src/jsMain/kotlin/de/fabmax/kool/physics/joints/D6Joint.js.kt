package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.physics.*
import physx.*

actual fun D6Joint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): D6Joint {
    return D6JointImpl(bodyA, bodyB, frameA, frameB)
}

class D6JointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), D6Joint {

    override val pxJoint: PxD6Joint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.D6JointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
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

    override var motionTwist: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eTWIST).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eTWIST, value.toPxD6MotionEnum())

    override var motionSwing1: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eSWING1).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eSWING1, value.toPxD6MotionEnum())

    override var motionSwing2: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eSWING2).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eSWING2, value.toPxD6MotionEnum())

    override fun setDistanceLimit(extend: Float, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLinearLimit(extend, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setDistanceLimit(limit)
        }
    }

    override fun setXLinearLimit(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLinearLimitPair(lowerLimit, upperLimit, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eX, limit)
            motionX = D6JointMotion.Limited
        }
    }

    override fun setYLinearLimit(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLinearLimitPair(lowerLimit, upperLimit, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eY, limit)
            motionY = D6JointMotion.Limited
        }
    }

    override fun setZLinearLimit(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLinearLimitPair(lowerLimit, upperLimit, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eZ, limit)
            motionZ = D6JointMotion.Limited
        }
    }

    override fun setTwistLimit(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointAngularLimitPair(lowerLimit.rad, upperLimit.rad, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setTwistLimit(limit)
            motionTwist = D6JointMotion.Limited
        }
    }

    override fun setSwingLimit(yLimit: AngleF, zLimit: AngleF, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLimitCone(yLimit.rad, zLimit.rad, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setSwingLimit(limit)
            motionSwing1 = D6JointMotion.Limited
            motionSwing2 = D6JointMotion.Limited
        }
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