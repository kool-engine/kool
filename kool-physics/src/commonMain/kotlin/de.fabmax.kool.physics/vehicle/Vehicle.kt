package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidBody

expect class Vehicle(chassis: RigidBody, vehicleProperties: VehicleProperties, world: PhysicsWorld) : CommonVehicle {

    fun updateWheelTransform(wheelIndex: Int, result: Mat4f): Mat4f

}

abstract class CommonVehicle(val chassis: RigidBody, val vehicleProperties: VehicleProperties) {

    abstract fun setSteerAngle(wheelIndex: Int, value: Float)

    abstract fun setEngineForce(wheelIndex: Int, value: Float)

    abstract fun setBrake(wheelIndex: Int, value: Float)

    fun setSteerAngle(value: Float) {
        for (i in vehicleProperties.wheels.indices) {
            if (vehicleProperties.wheels[i].isSteering) {
                setSteerAngle(i, value)
            }
        }
    }

    fun setEngineForce(value: Float) {
        for (i in vehicleProperties.wheels.indices) {
            if (vehicleProperties.wheels[i].isMotor) {
                setEngineForce(i, value)
            }
        }
    }

    fun setBrake(value: Float) {
        for (i in vehicleProperties.wheels.indices) {
            if (vehicleProperties.wheels[i].isBrake) {
                setBrake(i, value)
            }
        }
    }

    companion object {
        const val FRONT_LEFT = 0
        const val FRONT_RIGHT = 1
        const val REAR_LEFT = 2
        const val REAR_RIGHT = 3
    }
}
