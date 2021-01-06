package de.fabmax.kool.physics

import ammo.*
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.joints.Joint
import de.fabmax.kool.physics.vehicle.Vehicle

actual class PhysicsWorld : CommonPhysicsWorld() {
    val physicsWorld: btDiscreteDynamicsWorld
    val vehicleRaycaster: btVehicleRaycaster

    private val bufGravity = MutableVec3f()

    actual var gravity: Vec3f
        get() = physicsWorld.getGravity().toVec3f(bufGravity)
        set(value) {
            physicsWorld.setGravity(value.toBtVector3())
        }

    init {
        Physics.checkIsLoaded()

        val collisionConfiguration = Ammo.btDefaultCollisionConfiguration()
        val dispatcher = Ammo.btCollisionDispatcher(collisionConfiguration)
        val pairCache = Ammo.btDbvtBroadphase()
        val solver = Ammo.btSequentialImpulseConstraintSolver()

        physicsWorld = Ammo.btDiscreteDynamicsWorld(dispatcher, pairCache, solver, collisionConfiguration)
        physicsWorld.setGravity(Vec3f(0f, -9.81f, 0f).toBtVector3())
        vehicleRaycaster = Ammo.btDefaultVehicleRaycaster(physicsWorld)
    }

    override fun singleStepPhysicsImpl(timeStep: Float) {
        physicsWorld.stepSimulation(timeStep)
    }

    override fun addRigidBodyImpl(rigidBody: RigidBody) {
        physicsWorld.addRigidBody(rigidBody.btRigidBody, rigidBody.collisionGroup.toShort(), rigidBody.collisionMask.toShort())
    }

    override fun removeRigidBodyImpl(rigidBody: RigidBody) {
        physicsWorld.removeRigidBody(rigidBody.btRigidBody)
    }

    override fun addJointImpl(joint: Joint, disableCollisionBetweenBodies: Boolean) {
        physicsWorld.addConstraint(joint.btConstraint, disableCollisionBetweenBodies)
    }

    override fun removeJointImpl(joint: Joint) {
        physicsWorld.removeConstraint(joint.btConstraint)
    }

    override fun addVehicleImpl(vehicle: Vehicle) {
        physicsWorld.addAction(vehicle.btVehicle)
    }

    override fun removeVehicleImpl(vehicle: Vehicle) {
        physicsWorld.removeAction(vehicle.btVehicle)
    }
}