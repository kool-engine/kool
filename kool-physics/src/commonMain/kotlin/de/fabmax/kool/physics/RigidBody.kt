package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f

interface RigidBody : RigidActor {

    var mass: Float
    var inertia: Vec3f

    var linearVelocity: Vec3f
    var angularVelocity: Vec3f

    var maxLinearVelocity: Float
    var maxAngularVelocity: Float

    var linearDamping: Float
    var angularDamping: Float

    /**
     * Updates this bodies inertia tensor based on the mass and attached shapes.
     *
     * Must be called before the body is added to the simulation / [PhysicsWorld] or from
     * [PhysicsStepListener.onPhysicsUpdate] to make sure that the values don't change while the simulation
     * is running.
     */
    fun updateInertiaFromShapesAndMass()

    /**
     * Adds the given force to this body for the next simulation step.
     *
     * Must be called before the body is added to the simulation / [PhysicsWorld] or from
     * [PhysicsStepListener.onPhysicsUpdate] to make sure that the values don't change while the simulation
     * is running.
     */
    fun addForceAtPos(force: Vec3f, pos: Vec3f, isLocalForce: Boolean = false, isLocalPos: Boolean = false)

    /**
     * Adds the given impulse to this body for the next simulation step.
     *
     * Must be called before the body is added to the simulation / [PhysicsWorld] or from
     * [PhysicsStepListener.onPhysicsUpdate] to make sure that the values don't change while the simulation
     * is running.
     */
    fun addImpulseAtPos(impulse: Vec3f, pos: Vec3f, isLocalImpulse: Boolean = false, isLocalPos: Boolean = false)

    /**
     * Adds the given torque to this body for the next simulation step.
     *
     * Must be called before the body is added to the simulation / [PhysicsWorld] or from
     * [PhysicsStepListener.onPhysicsUpdate] to make sure that the values don't change while the simulation
     * is running.
     */
    fun addTorque(torque: Vec3f, isLocalTorque: Boolean = false)
}