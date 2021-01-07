package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.joints.Joint
import de.fabmax.kool.physics.vehicle.Vehicle
import physx.PhysX
import physx.PxScene
import physx.toPxVec3
import physx.toVec3f

actual class PhysicsWorld : CommonPhysicsWorld() {
    val scene: PxScene

    private val bufGravity = MutableVec3f()
    actual var gravity: Vec3f
        get() = scene.getGravity().toVec3f(bufGravity)
        set(value) {
            scene.setGravity(value.toPxVec3())
        }

    init {
        Physics.checkIsLoaded()

        val sceneDesc = PhysX.PxSceneDesc(PhysX.physics.getTolerancesScale())
        scene = PhysX.physics.createScene(sceneDesc)
    }

    override fun singleStepPhysicsImpl(timeStep: Float) {
        scene.simulate(timeStep, true)
        scene.fetchResults(true)
    }

    override fun addRigidBodyImpl(rigidBody: RigidBody) {
        scene.addActor(rigidBody.pxActor, null)
    }

    override fun removeRigidBodyImpl(rigidBody: RigidBody) {
        scene.removeActor(rigidBody.pxActor, true)
    }

    override fun addJointImpl(joint: Joint, disableCollisionBetweenBodies: Boolean) {
        //scene.addConstraint(joint.btConstraint, disableCollisionBetweenBodies)
    }

    override fun removeJointImpl(joint: Joint) {
        //scene.removeConstraint(joint.btConstraint)
    }

    override fun addVehicleImpl(vehicle: Vehicle) {
        //scene.addAction(vehicle.btVehicle)
    }

    override fun removeVehicleImpl(vehicle: Vehicle) {
        //scene.removeAction(vehicle.btVehicle)
    }
}