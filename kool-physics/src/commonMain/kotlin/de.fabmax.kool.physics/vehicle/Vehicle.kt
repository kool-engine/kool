package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidDynamic

expect class Vehicle(vehicleProps: VehicleProperties, world: PhysicsWorld, pose: Mat4f = Mat4f()) : CommonVehicle {

    val wheelTransforms: List<Mat4f>

    val forwardSpeed: Float

    var isReverse: Boolean

}

abstract class CommonVehicle(vehicleProps: VehicleProperties, pose: Mat4f) : RigidDynamic(vehicleProps.chassisMass, pose) {

    abstract fun setSteerInput(value: Float)

    abstract fun setThrottleInput(value: Float)

    abstract fun setBrakeInput(value: Float)

    companion object {
        const val FRONT_LEFT = 0
        const val FRONT_RIGHT = 1
        const val REAR_LEFT = 2
        const val REAR_RIGHT = 3
    }
}
