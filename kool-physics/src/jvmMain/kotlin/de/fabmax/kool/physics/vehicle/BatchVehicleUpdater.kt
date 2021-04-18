package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.Material
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.toMat4f
import physx.PxTopLevelFunctions
import physx.extensions.BatchVehicleUpdate
import physx.extensions.BatchVehicleUpdateDesc
import kotlin.math.min

actual class BatchVehicleUpdater actual constructor(maxVehicles: Int, private val physicsWorld: PhysicsWorld) : VehicleUpdater {

    private val batchVehicleUpdater: BatchVehicleUpdate
    private val desc = BatchVehicleUpdateDesc()

    private var frictionPairs = Physics.defaultSurfaceFrictions
    private val vehicles = mutableListOf<Vehicle>()

    private val onFixedUpdate: (Float) -> Unit

    init {
        desc.foundation = Physics.foundation
        desc.scene = physicsWorld.scene
        desc.numWorkers = min(maxVehicles, physicsWorld.numWorkers)
        desc.batchSize = min(maxVehicles / desc.numWorkers, 64)
        desc.frictionPairs = frictionPairs.frictionPairs
        desc.maxNbVehicles = maxVehicles
        desc.preFilterShader = PxTopLevelFunctions.DefaultWheelSceneQueryPreFilterBlocking()
        batchVehicleUpdater = BatchVehicleUpdate(desc)

        onFixedUpdate = { timeStep ->
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
        physicsWorld.onFixedUpdate += onFixedUpdate
    }

    actual fun addVehicle(vehicle: Vehicle): BatchVehicleUpdater {
        vehicles.add(vehicle)
        batchVehicleUpdater.addVehicle(vehicle.pxVehicle)
        return this
    }

    actual fun removeVehicle(vehicle: Vehicle): BatchVehicleUpdater {
        vehicles.remove(vehicle)
        batchVehicleUpdater.removeVehicle(vehicle.pxVehicle)
        return this
    }

    override fun updateVehicle(vehicle: Vehicle, timeStep: Float) {
        // do nothing here: this method is called on every vehicle individually but we update
        // them all in a single batch call
    }

    override fun setSurfaceFrictions(frictionPairs: Map<Material, Float>) {
        this.frictionPairs = FrictionPairs(frictionPairs)
    }

    override fun release() {
        physicsWorld.onFixedUpdate -= onFixedUpdate

        batchVehicleUpdater.destroy()
        desc.destroy()

        if (frictionPairs != Physics.defaultSurfaceFrictions) {
            frictionPairs.release()
        }
    }
}