package de.fabmax.kool.physics

import com.bulletphysics.collision.broadphase.DbvtBroadphase
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import de.fabmax.kool.math.Vec3f
import javax.vecmath.Vector3f

actual class PhysicsWorld  : CommonPhysicsWorld() {
    val physicsWorld: DiscreteDynamicsWorld

    private val bufGravity = Vector3f()

    actual var gravity : Vec3f
        get() {
            physicsWorld.getGravity(bufGravity)
            return bufGravity.toVec3f()
        }
        set(value) {
            physicsWorld.setGravity(bufGravity.set(value))
        }

    init {
        val collisionConfiguration = DefaultCollisionConfiguration()
        val dispatcher = CollisionDispatcher(collisionConfiguration)
        val pairCache = DbvtBroadphase()
        val solver = SequentialImpulseConstraintSolver()

        physicsWorld = DiscreteDynamicsWorld(dispatcher, pairCache, solver, collisionConfiguration)
        physicsWorld.setGravity(Vec3f(0f, -9.81f, 0f).toBtVector3f())
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
}