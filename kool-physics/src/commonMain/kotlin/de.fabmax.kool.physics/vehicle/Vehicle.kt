package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.PhysicsWorld

expect class Vehicle(world: PhysicsWorld) : CommonVehicle {

    val chassisTransform: Mat4f
    val wheelTransforms: List<Mat4f>

    fun updateWheelTransform(wheelIndex: Int, result: Mat4f): Mat4f

}

abstract class CommonVehicle {

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    abstract fun setSteerAngle(wheelIndex: Int, value: Float)

    abstract fun setEngineForce(wheelIndex: Int, value: Float)

    abstract fun setBrake(wheelIndex: Int, value: Float)

    abstract fun setSteerAngle(value: Float)

    abstract fun setEngineForce(value: Float)

    abstract fun setBrake(value: Float)

    internal open fun fixedUpdate(timeStep: Float) {
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }

//    fun setSteerAngle(value: Float) {
//        for (i in vehicleProperties.wheels.indices) {
//            if (vehicleProperties.wheels[i].isSteering) {
//                setSteerAngle(i, value)
//            }
//        }
//    }
//
//    fun setEngineForce(value: Float) {
//        for (i in vehicleProperties.wheels.indices) {
//            if (vehicleProperties.wheels[i].isMotor) {
//                setEngineForce(i, value)
//            }
//        }
//    }
//
//    fun setBrake(value: Float) {
//        for (i in vehicleProperties.wheels.indices) {
//            if (vehicleProperties.wheels[i].isBrake) {
//                setBrake(i, value)
//            }
//        }
//    }

    companion object {
        const val FRONT_LEFT = 0
        const val FRONT_RIGHT = 1
        const val REAR_LEFT = 2
        const val REAR_RIGHT = 3
    }
}
