package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.CommonPhysicsWorld
import de.fabmax.kool.physics.Material
import de.fabmax.kool.physics.Releasable

expect class VehicleManager(maxVehicles: Int, physicsWorld: CommonPhysicsWorld, surfaceFrictions: Map<Material, Float>) : Releasable {

    fun addVehicle(vehicle: Vehicle)

    fun removeVehicle(vehicle: Vehicle)

    fun onFixedUpdate(timeStep: Float)

}