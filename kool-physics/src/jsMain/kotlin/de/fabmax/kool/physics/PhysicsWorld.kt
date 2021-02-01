package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.vehicle.Vehicle
import physx.PxScene
import physx.PxSceneDesc
import physx.PxSceneFlagEnum
import physx.PxVehicleUpdateModeEnum

actual class PhysicsWorld actual constructor(gravity: Vec3f, numWorkers: Int) : CommonPhysicsWorld() {
    val scene: PxScene

    private val bufPxGravity = gravity.toPxVec3()
    private val bufGravity = MutableVec3f()
    actual var gravity: Vec3f
        get() = scene.getGravity().toVec3f(bufGravity)
        set(value) {
            scene.setGravity(value.toPxVec3(bufPxGravity))
        }

    init {
        val sceneDesc = PxSceneDesc(Physics.physics.getTolerancesScale())
        sceneDesc.gravity = bufPxGravity
        // ignore numWorkers parameter and set numThreads to 0, since multi-threading is disabled for wasm
        sceneDesc.cpuDispatcher = Physics.Px.DefaultCpuDispatcherCreate(0)
        sceneDesc.filterShader = Physics.Px.DefaultFilterShader()
        sceneDesc.flags.set(PxSceneFlagEnum.eENABLE_CCD)
        scene = Physics.physics.createScene(sceneDesc)

        Physics.PxVehicle.InitVehicleSDK(Physics.physics)
        Physics.PxVehicle.VehicleSetBasisVectors(Vec3f.Y_AXIS.toPxVec3(), Vec3f.Z_AXIS.toPxVec3())
        Physics.PxVehicle.VehicleSetUpdateMode(PxVehicleUpdateModeEnum.eVELOCITY_CHANGE)
    }

    override fun singleStepPhysicsImpl(timeStep: Float) {
        scene.simulate(timeStep)
        scene.fetchResults(true)
    }

    override fun addRigidBodyImpl(rigidBody: RigidBody) {
        scene.addActor(rigidBody.pxActor)
    }

    override fun removeRigidBodyImpl(rigidBody: RigidBody) {
        scene.removeActor(rigidBody.pxActor, true)
    }

    override fun addVehicleImpl(vehicle: Vehicle) {
        scene.addActor(vehicle.vehicle.getRigidDynamicActor())
    }

    override fun removeVehicleImpl(vehicle: Vehicle) {
        scene.removeActor(vehicle.vehicle.getRigidDynamicActor())
    }
}