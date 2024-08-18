package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.AngleF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.joints.RevoluteJoint.Companion.computeFrame
import physx.*

actual fun RevoluteJoint(bodyA: RigidActor?, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): RevoluteJoint {
    return RevoluteJointImpl(bodyA, bodyB, frameA, frameB)
}

actual fun RevoluteJoint(bodyA: RigidActor?, bodyB: RigidActor, pivotA: Vec3f, pivotB: Vec3f, axisA: Vec3f, axisB: Vec3f): RevoluteJoint {
    val frameA = computeFrame(pivotA, axisA)
    val frameB = computeFrame(pivotB, axisB)
    return RevoluteJointImpl(bodyA, bodyB, frameA, frameB)
}

class RevoluteJointImpl(
    override val bodyA: RigidActor?,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), RevoluteJoint {

    override val pxJoint: PxRevoluteJoint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.RevoluteJointCreate(PhysicsImpl.physics, bodyA?.holder?.px, frmA, bodyB.holder.px, frmB)
        }
    }

    override fun disableAngularMotor() {
        pxJoint.driveVelocity = 0f
        pxJoint.driveForceLimit = 0f
        pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, false)
    }

    override fun enableAngularMotor(angularVelocity: Float, forceLimit: Float) {
        pxJoint.driveVelocity = angularVelocity
        pxJoint.driveForceLimit = forceLimit
        pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, true)
    }

    override fun enableLimit(lowerLimit: AngleF, upperLimit: AngleF, limitBehavior: LimitBehavior) {
        MemoryStack.stackPush().use { mem ->
            val spring = mem.autoDelete(PxSpring(limitBehavior.stiffness, limitBehavior.damping))
            val limit = mem.autoDelete(PxJointAngularLimitPair(lowerLimit.rad, upperLimit.rad, spring))
            limit.restitution = limitBehavior.restitution
            limit.bounceThreshold = limitBehavior.bounceThreshold
            pxJoint.setLimit(limit)
            pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eLIMIT_ENABLED, true)
        }
    }

    override fun disableLimit() {
        pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eLIMIT_ENABLED, false)
    }
}