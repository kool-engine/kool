package de.fabmax.kool.physics

import com.bulletphysics.collision.broadphase.DbvtBroadphase
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import com.bulletphysics.dynamics.vehicle.DefaultVehicleRaycaster
import com.bulletphysics.dynamics.vehicle.VehicleRaycaster
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.joints.Joint
import de.fabmax.kool.physics.vehicle.Vehicle
import javax.vecmath.Vector3f

actual class PhysicsWorld  : CommonPhysicsWorld() {
    val physicsWorld: DiscreteDynamicsWorld
    val vehicleRaycaster: VehicleRaycaster

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

        vehicleRaycaster = DefaultVehicleRaycaster(physicsWorld)
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
        physicsWorld.addVehicle(vehicle.btVehicle)
    }

    override fun removeVehicleImpl(vehicle: Vehicle) {
        physicsWorld.removeVehicle(vehicle.btVehicle)
    }
}