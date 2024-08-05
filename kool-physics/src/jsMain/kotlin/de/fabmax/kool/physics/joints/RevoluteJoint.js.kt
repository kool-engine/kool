package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import physx.PxRevoluteJoint
import physx.PxRevoluteJointFlagEnum
import physx.driveForceLimit
import physx.driveVelocity

actual fun RevoluteJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: PoseF, frameB: PoseF): RevoluteJoint {
    return RevoluteJointImpl(bodyA, bodyB, frameA, frameB)
}

actual fun RevoluteJoint(bodyA: RigidActor, bodyB: RigidActor, pivotA: Vec3f, pivotB: Vec3f, axisA: Vec3f, axisB: Vec3f): RevoluteJoint {
    return RevoluteJointImpl(bodyA, bodyB, pivotA, pivotB, axisA, axisB)
}

class RevoluteJointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: PoseF,
    frameB: PoseF
) : JointImpl(frameA, frameB), RevoluteJoint {

    constructor(
        bodyA: RigidActor, bodyB: RigidActor,
        pivotA: Vec3f, pivotB: Vec3f,
        axisA: Vec3f, axisB: Vec3f
    ) : this(bodyA, bodyB, RevoluteJoint.computeFrame(pivotA, axisA), RevoluteJoint.computeFrame(pivotB, axisB))

    override val pxJoint: PxRevoluteJoint

    init {
        PhysicsImpl.checkIsLoaded()
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            pxJoint = PxTopLevelFunctions.RevoluteJointCreate(PhysicsImpl.physics, bodyA.holder.px, frmA, bodyB.holder.px, frmB)
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
}