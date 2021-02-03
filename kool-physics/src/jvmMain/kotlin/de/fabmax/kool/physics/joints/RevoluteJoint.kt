package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PxTransform
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.toPxTransform
import physx.PxTopLevelFunctions
import physx.extensions.PxRevoluteJoint
import physx.extensions.PxRevoluteJointFlagEnum

@Suppress("CanBeParameter")
actual class RevoluteJoint actual constructor(actual val bodyA: RigidActor, actual val bodyB: RigidActor,
                                              frameA: Mat4f, frameB: Mat4f) : CommonRevoluteJoint(), Joint {

    actual val frameA = Mat4f().set(frameA)
    actual val frameB = Mat4f().set(frameB)

    actual constructor(bodyA: RigidActor, bodyB: RigidActor,
                       pivotA: Vec3f, pivotB: Vec3f,
                       axisA: Vec3f, axisB: Vec3f)
            : this(bodyA, bodyB, computeFrame(pivotA, axisA), computeFrame(pivotB, axisB))

    override val pxJoint: PxRevoluteJoint

    init {
        val frmA = frameA.toPxTransform(PxTransform())
        val frmB = frameB.toPxTransform(PxTransform())
        pxJoint = PxTopLevelFunctions.RevoluteJointCreate(Physics.physics, bodyA.pxRigidActor, frmA, bodyB.pxRigidActor, frmB)
        frmA.destroy()
        frmB.destroy()
    }

    actual fun disableAngularMotor() {
        pxJoint.driveVelocity = 0f
        pxJoint.driveForceLimit = 0f

        val flags = pxJoint.revoluteJointFlags
        flags.clear(PxRevoluteJointFlagEnum.eDRIVE_ENABLED)
        pxJoint.revoluteJointFlags = flags
    }

    actual fun enableAngularMotor(angularVelocity: Float, forceLimit: Float) {
        pxJoint.driveVelocity = angularVelocity
        pxJoint.driveForceLimit = forceLimit

        val flags = pxJoint.revoluteJointFlags
        flags.set(PxRevoluteJointFlagEnum.eDRIVE_ENABLED)
        pxJoint.revoluteJointFlags = flags
    }
}