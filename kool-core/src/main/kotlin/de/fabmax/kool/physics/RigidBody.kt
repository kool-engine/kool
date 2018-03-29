package de.fabmax.kool.physics

import de.fabmax.kool.math.*

class RigidBody(val shape: Box, var mass: Float, inertiaVec: Vec3f) {

    val transform: Mat4f
        get() = shape.transform
    val centerOfMass: MutableVec3f
        get() = shape.center

    private val inertiaT = Mat3f()
    private val invOrientation = Mat3f()

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
    private val tmpForceLocal = MutableVec3f()

    var isInCollision = false

    init {
        inertiaT.scale(inertiaVec)
    }

    fun stepSimulation(dt: Float, world: CollisionWorld) {
        if (mass > 0) {
            // apply gravity (acts on center of mass, applies no torque)
            tmpVec.set(world.gravity).scale(mass)
            force.add(tmpVec)

            // compute linear acceleration caused by applied force
            tmpVec.set(force).subtract(prevForce).scale(0.5f).add(prevForce)
            prevForce.set(force)
            tmpVec.scale(1f / mass)
            mutAcceleration.set(tmpVec)

            // update linear velocity
            tmpVec.scale(dt).add(velocity)
            velocity.set(tmpVec)

            // update position based on velocity and time step
            // don't use transform.translate() here because velocity vector always is in global orientation
            tmpVec.scale(dt)
            //transform.translate(tmpVec)
            centerOfMass.add(tmpVec)

            // compute angular acceleration caused by applied torque
            tmpVec.set(torque).subtract(prevTorque).scale(0.5f).add(prevTorque)
            prevTorque.set(torque)
            inertiaT.transform(tmpVec)
            mutAngularAcceleration.set(tmpVec)

            // update angular velocity (and apply some constant damping factor)
            tmpVec.scale(dt).add(angularVelocity).scale(0.98f)
            angularVelocity.set(tmpVec)

            // update rotation based on angular velocity and time step
            tmpVec.scale(dt * RAD_2_DEG.toFloat())
            transform.rotate(tmpVec.x, Vec3f.X_AXIS)
            transform.rotate(tmpVec.y, Vec3f.Y_AXIS)
            transform.rotate(tmpVec.z, Vec3f.Z_AXIS)

            // get inverse orientation (orientation is a orthonormal rotation matrix -> transpose == inverse)
            transform.getOrientation(invOrientation).transpose()
        }

        force.set(Vec3f.ZERO)
        torque.set(Vec3f.ZERO)
    }

    fun getVelocityInLocalPoint(pos: MutableVec3f): MutableVec3f =
        pos.set(angularVelocity.cross(pos, tmpVec)).add(velocity)

    /**
     * Applies the given force vector (global orientation) at the specified position relative to center of mass.
     */
    fun applyForceRelative(position: Vec3f, force: Vec3f) {
        invOrientation.transform(tmpForceLocal.set(force))
        this.torque.add(position.cross(tmpForceLocal, tmpVec))
        this.force.add(force)
    }

    /**
     * Applies the given force vector at the specified position in global coordinates.
     */
    fun applyForceGlobal(position: Vec3f, force: Vec3f) {
        tmpPosLocal.set(position).subtract(centerOfMass)
        invOrientation.transform(tmpPosLocal)
        applyForceRelative(tmpPosLocal, force)
    }

    /**
     * Applies the given impulse vector (global orientation) at the specified position relative to center of mass.
     */
    fun applyImpulseRelative(position: Vec3f, impulse: Vec3f) {
        // impulse immediately changes (angular) velocity
        velocity.add(tmpVec.set(impulse).scale(1f / mass))

        invOrientation.transform(tmpForceLocal.set(impulse))
        inertiaT.transform(position.cross(tmpForceLocal, tmpVec))
        angularVelocity.add(tmpVec)
    }

    /**
     * Applies the given impulse vector at the specified position in global coordinates.
     */
    fun applyImpulseGlobal(position: Vec3f, impulse: Vec3f) {
        tmpPosLocal.set(position).subtract(centerOfMass)
        invOrientation.transform(tmpPosLocal)
        applyImpulseRelative(tmpPosLocal, impulse)
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
