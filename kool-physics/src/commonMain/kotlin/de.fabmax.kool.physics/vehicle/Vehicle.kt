package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.PhysicsWorld

expect class Vehicle(vehicleProps: VehicleProperties, world: PhysicsWorld) : CommonVehicle {

    val chassisTransform: Mat4f
    val wheelTransforms: List<Mat4f>

    val velocity: Vec3f
    val forwardSpeed: Float

    var isReverse: Boolean

}

abstract class CommonVehicle {

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    abstract fun setSteerInput(value: Float)

    abstract fun setThrottleInput(value: Float)

    abstract fun setBrakeInput(value: Float)

    internal open fun fixedUpdate(timeStep: Float) {
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }

    companion object {
        const val FRONT_LEFT = 0
        const val FRONT_RIGHT = 1
        const val REAR_LEFT = 2
        const val REAR_RIGHT = 3
    }
}
