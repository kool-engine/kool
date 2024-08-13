package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.*
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

    private var ySwingLimitMin = 0f.rad
    private var ySwingLimitMax = 0f.rad
    private var zSwingLimitMin = 0f.rad
    private var zSwingLimitMax = 0f.rad
    private val targetDriveVelLinear = MutableVec3f()
    private val targetDriveVelAngular = MutableVec3f()

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.D6JointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override var linearMotionX: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eX).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eX, value.toPxD6MotionEnum())

    override var linearMotionY: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eY).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eY, value.toPxD6MotionEnum())

    override var linearMotionZ: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eZ).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eZ, value.toPxD6MotionEnum())

    override var angularMotionX: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eTWIST).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eTWIST, value.toPxD6MotionEnum())

    override var angularMotionY: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eSWING1).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eSWING1, value.toPxD6MotionEnum())

    override var angularMotionZ: D6JointMotion
        get() = pxJoint.getMotion(PxD6AxisEnum.eSWING2).toD6JointMotion()
        set(value) = pxJoint.setMotion(PxD6AxisEnum.eSWING2, value.toPxD6MotionEnum())

    override fun enableDistanceLimit(extend: Float, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLinearLimit(extend, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setDistanceLimit(limit)
        }
    }

    override fun enableLinearLimitX(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLinearLimitPair(lowerLimit, upperLimit, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eX, limit)
            linearMotionX = D6JointMotion.Limited
        }
    }

    override fun enableLinearLimitY(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLinearLimitPair(lowerLimit, upperLimit, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eY, limit)
            linearMotionY = D6JointMotion.Limited
        }
    }

    override fun enableLinearLimitZ(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointLinearLimitPair(lowerLimit, upperLimit, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eZ, limit)
            linearMotionZ = D6JointMotion.Limited
        }
    }

    override fun enableAngularLimitX(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointAngularLimitPair(lowerLimit.rad, upperLimit.rad, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setTwistLimit(limit)
            angularMotionX = D6JointMotion.Limited
        }
    }

    override fun enableAngularLimitY(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            ySwingLimitMin = lowerLimit
            ySwingLimitMax = upperLimit
            val limit = mem.autoDelete(
                PxJointLimitPyramid(ySwingLimitMin.rad, ySwingLimitMax.rad, zSwingLimitMin.rad, zSwingLimitMax.rad, spring)
            )
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setPyramidSwingLimit(limit)
            angularMotionY = D6JointMotion.Limited
        }
    }

    override fun enableAngularLimitZ(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            ySwingLimitMin = lowerLimit
            ySwingLimitMax = upperLimit
            val limit = mem.autoDelete(
                PxJointLimitPyramid(ySwingLimitMin.rad, ySwingLimitMax.rad, zSwingLimitMin.rad, zSwingLimitMax.rad, spring)
            )
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setPyramidSwingLimit(limit)
            angularMotionZ = D6JointMotion.Limited
        }
    }

    override fun disableDistanceLimit() {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            val limit = mem.autoDelete(PxJointLinearLimit(Float.MAX_VALUE, spring))
            pxJoint.setDistanceLimit(limit)
        }
    }

    override fun disableLinearLimitX() {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            val limit = mem.autoDelete(PxJointLinearLimitPair(0f, Float.MAX_VALUE, spring))
            pxJoint.setLinearLimit(PxD6AxisEnum.eX, limit)
            linearMotionX = D6JointMotion.Free
        }
    }

    override fun disableLinearLimitY() {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            val limit = mem.autoDelete(PxJointLinearLimitPair(0f, Float.MAX_VALUE, spring))
            pxJoint.setLinearLimit(PxD6AxisEnum.eY, limit)
            linearMotionY = D6JointMotion.Free
        }

    }

    override fun disableLinearLimitZ() {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            val limit = mem.autoDelete(PxJointLinearLimitPair(0f, Float.MAX_VALUE, spring))
            pxJoint.setLinearLimit(PxD6AxisEnum.eZ, limit)
            linearMotionZ = D6JointMotion.Free
        }
    }

    override fun disableAngularLimitX() {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            val limit = mem.autoDelete(PxJointAngularLimitPair(PI_F * -2f, PI_F * 2f, spring))
            pxJoint.setTwistLimit(limit)
            angularMotionX = D6JointMotion.Free
        }
    }

    override fun disableAngularLimitY() {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            ySwingLimitMin = -PI_F.rad
            ySwingLimitMax = PI_F.rad
            val limit = mem.autoDelete(
                PxJointLimitPyramid(ySwingLimitMin.rad, ySwingLimitMax.rad, zSwingLimitMin.rad, zSwingLimitMax.rad, spring)
            )
            pxJoint.setPyramidSwingLimit(limit)
            angularMotionY = D6JointMotion.Free
        }
    }

    override fun disableAngularLimitZ() {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(0f, 0f))
            zSwingLimitMin = -PI_F.rad
            zSwingLimitMax = PI_F.rad
            val limit = mem.autoDelete(
                PxJointLimitPyramid(ySwingLimitMin.rad, ySwingLimitMax.rad, zSwingLimitMin.rad, zSwingLimitMax.rad, spring)
            )
            pxJoint.setPyramidSwingLimit(limit)
            angularMotionZ = D6JointMotion.Free
        }
    }

    override fun setDriveTargetPose(target: PoseF) {
        MemoryStack.stackPush().use { mem ->
            pxJoint.setDrivePosition(target.toPxTransform(mem.createPxTransform()), true)
        }
    }

    override fun enableLinearDriveX(drive: D6JointDrive) {
        targetDriveVelLinear.x = drive.targetVelocity
        setDrive(PxD6DriveEnum.eX, drive)
    }

    override fun enableLinearDriveY(drive: D6JointDrive) {
        targetDriveVelLinear.y = drive.targetVelocity
        setDrive(PxD6DriveEnum.eY, drive)
    }

    override fun enableLinearDriveZ(drive: D6JointDrive) {
        targetDriveVelLinear.z = drive.targetVelocity
        setDrive(PxD6DriveEnum.eX, drive)
    }

    override fun enableAngularDriveX(drive: D6JointDrive) {
        targetDriveVelAngular.x = drive.targetVelocity
        setDrive(PxD6DriveEnum.eTWIST, drive)
    }

    override fun enableAngularDriveY(drive: D6JointDrive) {
        targetDriveVelAngular.y = drive.targetVelocity
        setDrive(PxD6DriveEnum.eSWING, drive)
    }

    override fun enableAngularDriveZ(drive: D6JointDrive) {
        targetDriveVelAngular.z = drive.targetVelocity
        setDrive(PxD6DriveEnum.eSWING, drive)
    }

    override fun disableLinearDriveX() {
        targetDriveVelLinear.x = 0f
        setDrive(PxD6DriveEnum.eX, D6JointDrive(0f, 0f, 0f, 0f))
    }

    override fun disableLinearDriveY() {
        targetDriveVelLinear.y = 0f
        setDrive(PxD6DriveEnum.eY, D6JointDrive(0f, 0f, 0f, 0f))
    }

    override fun disableLinearDriveZ() {
        targetDriveVelLinear.z = 0f
        setDrive(PxD6DriveEnum.eZ, D6JointDrive(0f, 0f, 0f, 0f))
    }

    override fun disableAngularDriveX() {
        targetDriveVelAngular.x = 0f
        setDrive(PxD6DriveEnum.eTWIST, D6JointDrive(0f, 0f, 0f, 0f))
    }

    override fun disableAngularDriveY() {
        targetDriveVelAngular.y = 0f
        setDrive(PxD6DriveEnum.eSWING, D6JointDrive(0f, 0f, 0f, 0f))
    }

    override fun disableAngularDriveZ() {
        targetDriveVelAngular.y = 0f
        setDrive(PxD6DriveEnum.eSWING, D6JointDrive(0f, 0f, 0f, 0f))
    }

    private fun setDrive(index: Int, drive: D6JointDrive) {
        MemoryStack.stackPush().use { mem ->
            val pxDrive = PxD6JointDrive(drive.stiffness, drive.damping, drive.forceLimit, drive.isAcceleration)
            pxJoint.setDrive(index, pxDrive)
            pxDrive.destroy()

            val linearVel = targetDriveVelLinear.toPxVec3(mem.createPxVec3())
            val angularVel = targetDriveVelAngular.toPxVec3(mem.createPxVec3())
            pxJoint.setDriveVelocity(linearVel, angularVel)
        }
    }

    companion object {
        private fun Int.toD6JointMotion(): D6JointMotion = when (this) {
            PxD6MotionEnum.eFREE -> D6JointMotion.Free
            PxD6MotionEnum.eLIMITED -> D6JointMotion.Limited
            PxD6MotionEnum.eLOCKED -> D6JointMotion.Locked
            else -> error("Invalid value $this")
        }

        private fun D6JointMotion.toPxD6MotionEnum(): Int = when (this) {
            D6JointMotion.Free -> PxD6MotionEnum.eFREE
            D6JointMotion.Limited -> PxD6MotionEnum.eLIMITED
            D6JointMotion.Locked -> PxD6MotionEnum.eLOCKED
        }
    }
}