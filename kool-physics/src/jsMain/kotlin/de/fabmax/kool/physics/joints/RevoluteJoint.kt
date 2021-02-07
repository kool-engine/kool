package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PxTransform
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.toPxTransform
import physx.PhysXJsLoader
import physx.PxRevoluteJoint
import physx.PxRevoluteJointFlagEnum

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
        Physics.checkIsLoaded()

        val frmA = frameA.toPxTransform(PxTransform())
        val frmB = frameB.toPxTransform(PxTransform())
        pxJoint = Physics.Px.RevoluteJointCreate(Physics.physics, bodyA.pxRigidActor, frmA, bodyB.pxRigidActor, frmB)
        PhysXJsLoader.destroy(frmA)
        PhysXJsLoader.destroy(frmB)
    }

    actual fun disableAngularMotor() {
        pxJoint.setDriveVelocity(0f, true)
        pxJoint.setDriveForceLimit(0f)
        pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, false)
    }

    actual fun enableAngularMotor(angularVelocity: Float, forceLimit: Float) {
        pxJoint.setDriveVelocity(angularVelocity, true)
        pxJoint.setDriveForceLimit(forceLimit)
        pxJoint.setRevoluteJointFlag(PxRevoluteJointFlagEnum.eDRIVE_ENABLED, true)
    }
}