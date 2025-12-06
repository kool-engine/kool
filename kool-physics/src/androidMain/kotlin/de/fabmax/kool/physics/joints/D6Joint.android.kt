package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.util.logW
import de.fabmax.kool.util.memStack
import physxandroid.PxTopLevelFunctions
import physxandroid.extensions.*
import kotlin.math.max

// GENERATED CODE BELOW:
// Transformed from desktop source

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

    private var yAngularLimitMin = 0f.rad
    private var yAngularLimitMax = 0f.rad
    private var zAngularLimitMin = 0f.rad
    private var zAngularLimitMax = 0f.rad
    private var limitBehaviorAngularY: LimitBehavior? = null
    private var limitBehaviorAngularZ: LimitBehavior? = null
    private val targetDriveVelLinear = MutableVec3f()
    private val targetDriveVelAngular = MutableVec3f()

    init {
        memStack {
            val frmA = frameA.toPxTransform(createPxTransform())
            val frmB = frameB.toPxTransform(createPxTransform())
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
        memStack {
            val spring = createPxSpring(limitBehavior.stiffness, limitBehavior.damping)
            val limit = createPxJointLinearLimit(extend, spring)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setDistanceLimit(limit)
        }
    }

    override fun enableLinearLimitX(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        memStack {
            val spring = createPxSpring(limitBehavior.stiffness, limitBehavior.damping)
            val limit = createPxJointLinearLimitPair(lowerLimit, upperLimit, spring)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eX, limit)
            linearMotionX = D6JointMotion.Limited
        }
    }

    override fun enableLinearLimitY(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        memStack {
            val spring = createPxSpring(limitBehavior.stiffness, limitBehavior.damping)
            val limit = createPxJointLinearLimitPair(lowerLimit, upperLimit, spring)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eY, limit)
            linearMotionY = D6JointMotion.Limited
        }
    }

    override fun enableLinearLimitZ(lowerLimit: Float, upperLimit: Float, limitBehavior: LimitBehavior) {
        memStack {
            val spring = createPxSpring(limitBehavior.stiffness, limitBehavior.damping)
            val limit = createPxJointLinearLimitPair(lowerLimit, upperLimit, spring)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLinearLimit(PxD6AxisEnum.eZ, limit)
            linearMotionZ = D6JointMotion.Limited
        }
    }

    override fun enableAngularLimitX(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        memStack {
            val spring = createPxSpring(limitBehavior.stiffness, limitBehavior.damping)
            val limit = createPxJointAngularLimitPair(lowerLimit, upperLimit, spring)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setTwistLimit(limit)
            angularMotionX = D6JointMotion.Limited
        }
    }

    override fun enableAngularLimitY(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        limitBehaviorAngularY = limitBehavior
        yAngularLimitMin = lowerLimit
        yAngularLimitMax = upperLimit
        val mergedLimit = getLimitBehaviorAngularYZ()
        updateAngularLimitYZ(mergedLimit)
        if (mergedLimit != limitBehavior) {
            logW { "Conflicting limit behaviors for angular y and angular z limit, merged limit behavior: $mergedLimit" }
        }
    }

    override fun enableAngularLimitZ(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        limitBehaviorAngularZ = limitBehavior
        zAngularLimitMin = lowerLimit
        zAngularLimitMax = upperLimit
        val mergedLimit = getLimitBehaviorAngularYZ()
        updateAngularLimitYZ(mergedLimit)
        if (mergedLimit != limitBehavior) {
            logW { "Conflicting limit behaviors for angular y and angular z limit, merged limit behavior: $mergedLimit" }
        }
    }

    override fun disableDistanceLimit() {
        memStack {
            val spring = createPxSpring(0f, 0f)
            val limit = createPxJointLinearLimit(Float.MAX_VALUE, spring)
            pxJoint.setDistanceLimit(limit)
        }
    }

    override fun disableLinearLimitX() {
        memStack {
            val spring = createPxSpring(0f, 0f)
            val limit = createPxJointLinearLimitPair(0f, Float.MAX_VALUE, spring)
            pxJoint.setLinearLimit(PxD6AxisEnum.eX, limit)
            linearMotionX = D6JointMotion.Free
        }
    }

    override fun disableLinearLimitY() {
        memStack {
            val spring = createPxSpring(0f, 0f)
            val limit = createPxJointLinearLimitPair(0f, Float.MAX_VALUE, spring)
            pxJoint.setLinearLimit(PxD6AxisEnum.eY, limit)
            linearMotionY = D6JointMotion.Free
        }

    }

    override fun disableLinearLimitZ() {
        memStack {
            val spring = createPxSpring(0f, 0f)
            val limit = createPxJointLinearLimitPair(0f, Float.MAX_VALUE, spring)
            pxJoint.setLinearLimit(PxD6AxisEnum.eZ, limit)
            linearMotionZ = D6JointMotion.Free
        }
    }

    override fun disableAngularLimitX() {
        memStack {
            val spring = createPxSpring(0f, 0f)
            val limit = createPxJointAngularLimitPair(PI_F.rad * -2f, PI_F.rad * 2f, spring)
            pxJoint.setTwistLimit(limit)
            angularMotionX = D6JointMotion.Free
        }
    }

    override fun disableAngularLimitY() {
        limitBehaviorAngularY = null
        yAngularLimitMin = -PI_F.rad
        yAngularLimitMax = PI_F.rad
        updateAngularLimitYZ(getLimitBehaviorAngularYZ())
    }

    override fun disableAngularLimitZ() {
        limitBehaviorAngularZ = null
        zAngularLimitMin = -PI_F.rad
        zAngularLimitMax = PI_F.rad
        updateAngularLimitYZ(getLimitBehaviorAngularYZ())
    }

    private fun updateAngularLimitYZ(limitBehavior: LimitBehavior) {
        memStack {
            val spring = createPxSpring(limitBehavior.stiffness, limitBehavior.damping)
            val limit = createPxJointLimitPyramid(yAngularLimitMin.rad, yAngularLimitMax.rad, zAngularLimitMin.rad, zAngularLimitMax.rad, spring)
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setPyramidSwingLimit(limit)
            angularMotionZ = D6JointMotion.Limited
        }
    }

    override fun setDriveTargetPose(target: PoseF) {
        memStack {
            pxJoint.setDrivePosition(target.toPxTransform(createPxTransform()), true)
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

    private fun setDrive(index: PxD6DriveEnum, drive: D6JointDrive) {
        memStack {
            val pxDrive = PxD6JointDrive(drive.stiffness, drive.damping, drive.forceLimit, drive.isAcceleration)
            pxJoint.setDrive(index, pxDrive)
            pxDrive.destroy()

            val linearVel = targetDriveVelLinear.toPxVec3(createPxVec3())
            val angularVel = targetDriveVelAngular.toPxVec3(createPxVec3())
            pxJoint.setDriveVelocity(linearVel, angularVel)
        }
    }

    private fun getLimitBehaviorAngularYZ(): LimitBehavior {
        val limitY = limitBehaviorAngularY
        val limitZ = limitBehaviorAngularZ

        val stiffness = max(limitY?.stiffness ?: 0f, limitZ?.stiffness ?: 0f)
        val damping = max(limitY?.damping ?: 0f, limitZ?.damping ?: 0f)
        val restitution = max(limitY?.restitution ?: 0f, limitZ?.restitution ?: 0f)
        val bounceThreshold = max(limitY?.bounceThreshold ?: 0f, limitZ?.bounceThreshold ?: 0f)
        return LimitBehavior(stiffness, damping, restitution, bounceThreshold)
    }

    companion object {
        private fun PxD6MotionEnum.toD6JointMotion(): D6JointMotion = when (this) {
            PxD6MotionEnum.eFREE -> D6JointMotion.Free
            PxD6MotionEnum.eLIMITED -> D6JointMotion.Limited
            PxD6MotionEnum.eLOCKED -> D6JointMotion.Locked
            //@js: else -> error("Invalid value $this")
        }

        private fun D6JointMotion.toPxD6MotionEnum(): PxD6MotionEnum = when (this) {
            D6JointMotion.Free -> PxD6MotionEnum.eFREE
            D6JointMotion.Limited -> PxD6MotionEnum.eLIMITED
            D6JointMotion.Locked -> PxD6MotionEnum.eLOCKED
        }
    }
}