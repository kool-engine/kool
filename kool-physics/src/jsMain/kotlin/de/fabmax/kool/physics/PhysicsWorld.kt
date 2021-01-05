package de.fabmax.kool.physics

import ammo.Ammo
import ammo.btDiscreteDynamicsWorld
import ammo.toBtVector3
import ammo.toVec3f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.constraints.Constraint

actual class PhysicsWorld : CommonPhysicsWorld() {
    val physicsWorld: btDiscreteDynamicsWorld

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
    }

    override fun singleStepPhysicsImpl(timeStep: Float) {
        physicsWorld.stepSimulation(timeStep)
    }

    override fun addRigidBodyImpl(rigidBody: RigidBody) {
        physicsWorld.addRigidBody(rigidBody.btRigidBody)
    }

    override fun removeRigidBodyImpl(rigidBody: RigidBody) {
        physicsWorld.removeRigidBody(rigidBody.btRigidBody)
    }

    override fun addConstraintImpl(constraint: Constraint, disableCollisionBetweenBodies: Boolean) {
        physicsWorld.addConstraint(constraint.btConstraint, disableCollisionBetweenBodies)
    }

    override fun removeConstraintImpl(constraint: Constraint) {
        physicsWorld.removeConstraint(constraint.btConstraint)
    }
}