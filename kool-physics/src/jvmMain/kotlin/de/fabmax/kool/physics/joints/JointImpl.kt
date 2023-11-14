package de.fabmax.kool.physics.joints

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.util.BaseReleasable
import physx.extensions.PxJoint
import physx.physics.PxConstraintFlagEnum

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual typealias JointHolder = PxJoint

actual fun D6Joint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): D6Joint {
    return D6JointImpl(bodyA, bodyB, frameA, frameB)
}

actual fun DistanceJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): DistanceJoint {
    return DistanceJointImpl(bodyA, bodyB, frameA, frameB)
}

actual fun FixedJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): FixedJoint {
    return FixedJointImpl(bodyA, bodyB, frameA, frameB)
}

actual fun PrismaticJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): PrismaticJoint {
    return PrismaticJointImpl(bodyA, bodyB, frameA, frameB)
}

actual fun RevoluteJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): RevoluteJoint {
    return RevoluteJointImpl(bodyA, bodyB, frameA, frameB)
}

actual fun RevoluteJoint(bodyA: RigidActor, bodyB: RigidActor, pivotA: Vec3f, pivotB: Vec3f, axisA: Vec3f, axisB: Vec3f): RevoluteJoint {
    return RevoluteJointImpl(bodyA, bodyB, pivotA, pivotB, axisA, axisB)
}

actual fun SphericalJoint(bodyA: RigidActor, bodyB: RigidActor, frameA: Mat4f, frameB: Mat4f): SphericalJoint {
    return SphericalJointImpl(bodyA, bodyB, frameA, frameB)
}

abstract class JointImpl(frameA: Mat4f, frameB: Mat4f) : BaseReleasable(), Joint {
    override val frameA = Mat4f(frameA)
    override val frameB = Mat4f(frameB)

    override val isBroken: Boolean
        get() = joint.constraintFlags.isSet(PxConstraintFlagEnum.eBROKEN)

    override var debugVisualize: Boolean = false
        set(value) = if (value) {
            joint.constraintFlags.raise(PxConstraintFlagEnum.eVISUALIZATION)
        } else {
            joint.constraintFlags.clear(PxConstraintFlagEnum.eVISUALIZATION)
        }

    override fun setBreakForce(force: Float, torque: Float) = joint.setBreakForce(force, torque)

    override fun release() {
        super.release()
        joint.release()
    }
}