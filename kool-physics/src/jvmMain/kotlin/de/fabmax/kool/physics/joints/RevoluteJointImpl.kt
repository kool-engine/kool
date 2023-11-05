package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.PhysicsImpl
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.createPxTransform
import de.fabmax.kool.physics.joints.RevoluteJoint.Companion.computeFrame
import de.fabmax.kool.physics.toPxTransform
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.extensions.PxRevoluteJoint
import physx.extensions.PxRevoluteJointFlagEnum

class RevoluteJointImpl(
    override val bodyA: RigidActor,
    override val bodyB: RigidActor,
    frameA: Mat4f,
    frameB: Mat4f
) : JointImpl(frameA, frameB), RevoluteJoint {

    constructor(
        bodyA: RigidActor, bodyB: RigidActor,
        pivotA: Vec3f, pivotB: Vec3f,
        axisA: Vec3f, axisB: Vec3f
    ) : this(bodyA, bodyB, computeFrame(pivotA, axisA), computeFrame(pivotB, axisB))

    override val joint: PxRevoluteJoint

    init {
        MemoryStack.stackPush().use { mem ->
            val frmA = frameA.toPxTransform(mem.createPxTransform())
            val frmB = frameB.toPxTransform(mem.createPxTransform())
            joint = PxTopLevelFunctions.RevoluteJointCreate(PhysicsImpl.physics, bodyA.holder, frmA, bodyB.holder, frmB)
        }
    }

    override fun disableAngularMotor() {
        joint.driveVelocity = 0f
        joint.driveForceLimit = 0f
        joint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, false)
    }

    override fun enableAngularMotor(angularVelocity: Float, forceLimit: Float) {
        joint.driveVelocity = angularVelocity
        joint.driveForceLimit = forceLimit
        joint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, true)
    }
}