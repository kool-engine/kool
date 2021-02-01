package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.RigidBody
import de.fabmax.kool.physics.toPxTransform
import physx.PxRevoluteJoint
import physx.PxRevoluteJointFlagEnum

@Suppress("CanBeParameter")
actual class RevoluteJoint actual constructor(actual val bodyA: RigidBody, actual val bodyB: RigidBody,
                                              frameA: Mat4f, frameB: Mat4f) : CommonRevoluteJoint(), Joint {


    actual val frameA = Mat4f().set(frameA)
    actual val frameB = Mat4f().set(frameB)

    actual constructor(bodyA: RigidBody, bodyB: RigidBody,
                       pivotA: Vec3f, pivotB: Vec3f,
                       axisA: Vec3f, axisB: Vec3f)
            : this(bodyA, bodyB, computeFrame(pivotA, axisA), computeFrame(pivotB, axisB))

    override val pxJoint: PxRevoluteJoint

    init {
        Physics.checkIsLoaded()

        pxJoint = Physics.Px.RevoluteJointCreate(Physics.physics, bodyA.pxActor, frameA.toPxTransform(), bodyB.pxActor, frameB.toPxTransform())
    }

    actual fun disableAngularMotor() {
        pxJoint.setDriveVelocity(0f, true)
        pxJoint.setDriveForceLimit(0f)

        val flags = pxJoint.getRevoluteJointFlags()
        flags.clear(PxRevoluteJointFlagEnum.eDRIVE_ENABLED)
        pxJoint.setRevoluteJointFlags(flags)
    }

    actual fun enableAngularMotor(angularVelocity: Float, forceLimit: Float) {
        pxJoint.setDriveVelocity(angularVelocity, true)
        pxJoint.setDriveForceLimit(forceLimit)

        val flags = pxJoint.getRevoluteJointFlags()
        flags.set(PxRevoluteJointFlagEnum.eDRIVE_ENABLED)
        pxJoint.setRevoluteJointFlags(flags)
    }
}