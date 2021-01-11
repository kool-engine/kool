package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.joints.Joint
import de.fabmax.kool.physics.vehicle.Vehicle
import physx.*

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
        sceneDesc.gravity = PhysX.PxVec3(0f, -9.81f, 0f)
        sceneDesc.cpuDispatcher = PhysX.Px.DefaultCpuDispatcherCreate(0)
        sceneDesc.filterShader = PhysX.Px.DefaultFilterShader()
        sceneDesc.flags.set(PxSceneFlag.eENABLE_CCD)
        scene = PhysX.physics.createScene(sceneDesc)
    }

    override fun singleStepPhysicsImpl(timeStep: Float) {
        scene.simulate(timeStep)
        scene.fetchResults(true)
    }

    override fun addRigidBodyImpl(rigidBody: RigidBody) {
        scene.addActor(rigidBody.pxActor, null)
    }

    override fun removeRigidBodyImpl(rigidBody: RigidBody) {
        scene.removeActor(rigidBody.pxActor, true)
    }

    override fun addJointImpl(joint: Joint) {
        // nothing to do here
    }

    override fun removeJointImpl(joint: Joint) {
        // nothing to do here
    }

    override fun addVehicleImpl(vehicle: Vehicle) {
        // nothing to do here
    }

    override fun removeVehicleImpl(vehicle: Vehicle) {
        // nothing to do here
    }
}