package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidDynamic
import kotlin.math.PI

expect class Vehicle(vehicleProps: VehicleProperties, world: PhysicsWorld, pose: Mat4f = Mat4f()) : CommonVehicle {

    val wheelTransforms: List<Mat4f>

    val forwardSpeed: Float
    val sidewaysSpeed: Float
    val longitudinalAcceleration: Float
    val lateralAcceleration: Float
    val engineSpeedRpm: Float
    val engineTorqueNm: Float
    val enginePowerW: Float
    val currentGear: Int

    var isReverse: Boolean

}

abstract class CommonVehicle(vehicleProps: VehicleProperties, pose: Mat4f) : RigidDynamic(vehicleProps.chassisMass, pose) {

    abstract var steerInput: Float
    abstract var throttleInput: Float
    abstract var brakeInput: Float

    companion object {
        const val FRONT_LEFT = 0
        const val FRONT_RIGHT = 1
        const val REAR_LEFT = 2
        const val REAR_RIGHT = 3

        const val OMEGA_TO_RPM = 60f / (2f * PI.toFloat())
    }
}
