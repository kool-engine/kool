package de.fabmax.kool.modules.physics

import de.fabmax.kool.math.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class RigidBody(val shape: Box, val mass: Float, val inertiaVec: Vec3f) {

    var name = "RigidBody-${nInstances++}"

    val isStaticOrKinematic: Boolean
        get() = mass == 0f

    val worldTransform: Mat4f
        get() = shape.transform
    val centerOfMass: MutableVec3f
        get() = shape.center
    private val unpredictedWorldTransform = Mat4f()

    val invInertiaTensor = Mat3f()
    val invMass = if (!isStaticOrKinematic) 1f / mass else 0f

    val velocity = MutableVec3f()
    val acceleration: Vec3f
        get() = mutAcceleration
    private val mutAcceleration = MutableVec3f()

    val angularVelocity = MutableVec3f()
    val angularAcceleration: Vec3f
        get() = mutAngularAcceleration
    private val mutAngularAcceleration = MutableVec3f()

    private val force = MutableVec3f()
    private val torque = MutableVec3f()
    private val prevForce = MutableVec3f()
    private val prevTorque = MutableVec3f()

    private val tmpVec = MutableVec3f()
    private val tmpPosLocal = MutableVec3f()
    private val tmpQuat1 = MutableVec4f()
    private val tmpQuat2 = MutableVec4f()
    private val tmpMat3 = Mat3f()

    init {
        updateInertiaTensor()
    }

    fun applyGravity(dt: Float, world: CollisionWorld) {
        if (!isStaticOrKinematic) {
            world.gravity.scale(dt, tmpVec)
            velocity += tmpVec
        }
    }

    fun stepSimulation(dt: Float, world: CollisionWorld) {
        if (!isStaticOrKinematic) {
            worldTransform.set(unpredictedWorldTransform)

            // compute linear acceleration caused by applied force and gravity
            tmpVec.set(force).subtract(prevForce).scale(0.5f).add(prevForce)
            prevForce.set(force)
            tmpVec.scale(invMass)
            mutAcceleration.set(tmpVec)

            // update linear velocity
            tmpVec.scale(dt).add(velocity)
            velocity.set(tmpVec)

            // update position based on velocity and time step
            // don't use worldTransform.translate() here because velocity vector always is in global orientation
            tmpVec.scale(dt)
            centerOfMass.add(tmpVec)
            tmpPosLocal.set(centerOfMass)

            var fAngle = angularVelocity.length()
            // limit the angular motion
            if (fAngle * dt > ANGULAR_MOTION_THRESHOLD) {
                fAngle = ANGULAR_MOTION_THRESHOLD / dt
            }

            // determine rotation axis, tmoVec1
            if (fAngle < 0.001f) {
                // use Taylor's expansions of sync function
                tmpVec.set(angularVelocity).scale(0.5f * dt - dt * dt * dt * 0.020833333333f * fAngle * fAngle)
            } else {
                tmpVec.set(angularVelocity).scale(sin(0.5f * fAngle * dt) / fAngle)
            }
            tmpQuat1.set(tmpVec, cos(fAngle * dt * 0.5f))
            worldTransform.getRotation(tmpQuat2)
            tmpQuat1.quatProduct(tmpQuat2).norm()

            worldTransform.setRotate(tmpQuat1)
            centerOfMass.set(tmpPosLocal)

            updateInertiaTensor()
        }

        force.set(Vec3f.ZERO)
        torque.set(Vec3f.ZERO)
    }

    fun predictIntegratedTransform(dt: Float) {
        if (!isStaticOrKinematic) {
            unpredictedWorldTransform.set(worldTransform)

            tmpPosLocal.set(velocity).scale(dt).add(centerOfMass)

            var fAngle = angularVelocity.length()
            // limit the angular motion
            if (fAngle * dt > ANGULAR_MOTION_THRESHOLD) {
                fAngle = ANGULAR_MOTION_THRESHOLD / dt
            }

            // determine rotation axis, tmoVec1
            if (fAngle < 0.001f) {
                // use Taylor's expansions of sync function
                tmpVec.set(angularVelocity).scale(0.5f * dt - dt * dt * dt * 0.020833333333f * fAngle * fAngle)
            } else {
                tmpVec.set(angularVelocity).scale(sin(0.5f * fAngle * dt) / fAngle)
            }
            tmpQuat1.set(tmpVec, cos(fAngle * dt * 0.5f))
            worldTransform.getRotation(tmpQuat2)
            tmpQuat1.quatProduct(tmpQuat2).norm()

            worldTransform.setRotate(tmpQuat1)
            centerOfMass.set(tmpPosLocal)
        }
    }

    /**
     * Computes the inertia tensor for the current orientation.
     */
    fun updateInertiaTensor() {
//        worldTransform.getOrientationTransposed(invInertiaTensor).scale(inertiaVec)
        worldTransform.getOrientation(tmpMat3)
        invInertiaTensor.set(tmpMat3).scale(inertiaVec).mul(tmpMat3.transpose())
    }

    fun getVelocityInLocalPoint(pos: Vec3f, result: MutableVec3f): MutableVec3f =
        result.set(angularVelocity.cross(pos, tmpVec)).add(velocity)

    /**
     * Applies the given force vector (global orientation) at the specified position relative to center of mass.
     */
    fun applyForceRelative(position: Vec3f, force: Vec3f) {
        this.torque.add(position.cross(force, tmpVec))
        this.force.add(force)
    }

    /**
     * Applies the given force vector at the specified position in global coordinates.
     */
    fun applyForceGlobal(position: Vec3f, force: Vec3f) {
        tmpPosLocal.set(position).subtract(centerOfMass)
        applyForceRelative(tmpPosLocal, force)
    }

    /**
     * Applies the given impulse vector (global orientation) at the specified position relative to center of mass.
     */
    fun applyImpulseRelative(position: Vec3f, impulse: Vec3f) {
        // impulse immediately changes (angular) velocity
        velocity.add(tmpVec.set(impulse).scale(invMass))

        invInertiaTensor.transform(position.cross(impulse, tmpVec))
        angularVelocity.add(tmpVec)
    }

    /**
     * Applies the given impulse vector at the specified position in global coordinates.
     */
    fun applyImpulseGlobal(position: Vec3f, impulse: Vec3f) {
        tmpPosLocal.set(position).subtract(centerOfMass)
        applyImpulseRelative(tmpPosLocal, impulse)
    }

    override fun toString(): String {
        return name
    }

    companion object {
        const val ANGULAR_DAMPING = 0.98f
        const val ANGULAR_MOTION_THRESHOLD = PI.toFloat() / 4f

        private var nInstances = 1
    }
}

fun staticBox(size: Vec3f): RigidBody {
    return staticBox(size.x, size.y, size.z)
}

fun staticBox(sizeX: Float, sizeY: Float, sizeZ: Float): RigidBody {
    return RigidBody(Box(sizeX, sizeY, sizeZ), 0f, Vec3f.ZERO)
}

fun uniformMassBox(size: Vec3f, mass: Float): RigidBody {
    return uniformMassBox(size.x, size.y, size.z, mass)
}

fun uniformMassBox(sizeX: Float, sizeY: Float, sizeZ: Float, mass: Float): RigidBody {
    val inertia = MutableVec3f()
    val i = mass / 12f
    inertia.x = 1f / (i * (sizeY * sizeY + sizeZ * sizeZ))
    inertia.y = 1f / (i * (sizeX * sizeX + sizeZ * sizeZ))
    inertia.z = 1f / (i * (sizeX * sizeX + sizeY * sizeY))
    return RigidBody(Box(sizeX, sizeY, sizeZ), mass, inertia)
}
