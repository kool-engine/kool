package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.vehicle.Vehicle
import physx.PxTopLevelFunctions
import physx.physics.PxScene
import physx.physics.PxSceneDesc
import physx.physics.PxSceneFlagEnum
import physx.vehicle.PxVehicleTopLevelFunctions
import physx.vehicle.PxVehicleUpdateModeEnum

actual class PhysicsWorld actual constructor(gravity: Vec3f, numWorkers: Int) : CommonPhysicsWorld() {
    val scene: PxScene

    private val bufPxGravity = gravity.toPxVec3()
    private val bufGravity = MutableVec3f()
    actual var gravity: Vec3f
        get() = scene.gravity.toVec3f(bufGravity)
        set(value) {
            scene.gravity = value.toPxVec3(bufPxGravity)
        }

    init {
        val sceneDesc = PxSceneDesc(Physics.physics.tolerancesScale)
        sceneDesc.gravity = bufPxGravity
        sceneDesc.cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(8)
        sceneDesc.filterShader = PxTopLevelFunctions.DefaultFilterShader()
        sceneDesc.flags.set(PxSceneFlagEnum.eENABLE_CCD)
        scene = Physics.physics.createScene(sceneDesc)

        // init vehicle simulation framework
        PxVehicleTopLevelFunctions.InitVehicleSDK(Physics.physics)
        PxVehicleTopLevelFunctions.VehicleSetBasisVectors(Vec3f.Y_AXIS.toPxVec3(), Vec3f.Z_AXIS.toPxVec3())
        PxVehicleTopLevelFunctions.VehicleSetUpdateMode(PxVehicleUpdateModeEnum.eVELOCITY_CHANGE)
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
        scene.addActor(vehicle.vehicle.rigidDynamicActor)
    }

    override fun removeVehicleImpl(vehicle: Vehicle) {
        scene.removeActor(vehicle.vehicle.rigidDynamicActor)
    }
}