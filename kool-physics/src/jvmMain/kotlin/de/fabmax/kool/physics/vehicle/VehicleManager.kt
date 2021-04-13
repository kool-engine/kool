package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.*
import physx.PxTopLevelFunctions
import physx.extensions.BatchVehicleUpdate
import physx.extensions.BatchVehicleUpdateDesc
import kotlin.math.min

actual class VehicleManager actual constructor(maxVehicles: Int, physicsWorld: CommonPhysicsWorld, surfaceFrictions: Map<Material, Float>) : Releasable{

    private val batchVehicleUpdater: BatchVehicleUpdate
    private val desc = BatchVehicleUpdateDesc()

    private val frictionPairs = FrictionPairs(surfaceFrictions)
    private val vehicles = mutableListOf<Vehicle>()

    init {
        val actualPhysicsWorld = (physicsWorld as PhysicsWorld)
        desc.foundation = Physics.foundation
        desc.scene = actualPhysicsWorld.scene
        desc.numWorkers = min(maxVehicles, actualPhysicsWorld.numWorkers)
        desc.batchSize = min(maxVehicles / desc.numWorkers, 64)
        desc.frictionPairs = frictionPairs.frictionPairs
        desc.maxNbVehicles = maxVehicles
        desc.preFilterShader = PxTopLevelFunctions.DefaultWheelSceneQueryPreFilterBlocking()
        batchVehicleUpdater = BatchVehicleUpdate(desc)
    }

    actual fun addVehicle(vehicle: Vehicle) {
        vehicles.add(vehicle)
        batchVehicleUpdater.addVehicle(vehicle.pxVehicle)
    }

    actual fun removeVehicle(vehicle: Vehicle) {
        vehicles.remove(vehicle)
        batchVehicleUpdater.removeVehicle(vehicle.pxVehicle)
    }

    actual fun onFixedUpdate(timeStep: Float) {
        batchVehicleUpdater.batchUpdate(timeStep)
        for (vi in vehicles.indices) {
            for (wi in 0 until 4) {
                val wheelQryResult = batchVehicleUpdater.getWheelQueryResult(vi, wi)
                vehicles[vi].wheelInfos[wi].apply {
                    wheelQryResult.localPose.toMat4f(transform)
                    lateralSlip = wheelQryResult.lateralSlip
                    longitudinalSlip = wheelQryResult.longitudinalSlip
                }
            }
        }
    }

    override fun release() {
        batchVehicleUpdater.destroy()
        desc.destroy()
        frictionPairs.release()
    }
}