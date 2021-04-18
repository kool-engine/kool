package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Material
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.Releasable

interface VehicleUpdater : Releasable {
    fun updateVehicle(vehicle: Vehicle, timeStep: Float)

    fun setSurfaceFrictions(frictionPairs: Map<Material, Float>)
}

expect class SingleVehicleUpdater(vehicle: Vehicle, world: PhysicsWorld) : VehicleUpdater {
    var vehicleGravity: Vec3f?
}

expect class BatchVehicleUpdater(maxVehicles: Int, physicsWorld: PhysicsWorld) : VehicleUpdater {
    fun addVehicle(vehicle: Vehicle): BatchVehicleUpdater
    fun removeVehicle(vehicle: Vehicle): BatchVehicleUpdater
}
