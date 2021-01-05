package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Vec3f

class VehicleProperties(numWheels: Int) {
    val wheels = List(numWheels) { WheelProperties() }

    fun forEachWheel(block: (WheelProperties) -> Unit) {
        wheels.forEach(block)
    }

    companion object {
        fun defaultVehicleProperties(trackWidth: Float, wheelBase: Float, axleHeight: Float, wheelRadius: Float = 0.4f): VehicleProperties {
            return VehicleProperties(4).apply {
                val w = trackWidth * 0.5f
                val l = wheelBase * 0.5f
                wheels[CommonVehicle.FRONT_LEFT].apply {
                    position = Vec3f(w, axleHeight, l)
                    radius = wheelRadius
                    isSteering = true
                    isMotor = false
                }
                wheels[CommonVehicle.FRONT_RIGHT].apply {
                    position = Vec3f(-w, axleHeight, l)
                    radius = wheelRadius
                    isSteering = true
                    isMotor = false
                }
                wheels[CommonVehicle.REAR_LEFT].apply {
                    position = Vec3f(w, axleHeight, -l)
                    radius = wheelRadius
                    isSteering = false
                    isMotor = true
                }
                wheels[CommonVehicle.REAR_RIGHT].apply {
                    position = Vec3f(-w, axleHeight, -l)
                    radius = wheelRadius
                    isSteering = false
                    isMotor = true
                }
            }
        }
    }
}

class WheelProperties {
    var position = Vec3f(0f)
    var radius = 0.4f

    var isSteering = false
    var isMotor = false
    var isBrake = true

    var suspensionStiffness = 20.0f
    var suspensionCompression = 4.4f
    var suspensionDamping = 2.3f
    var maxSuspensionTravelCm = 50f
    var suspensionRestLength = 0.6f
    var friction = 1.25f
    var maxSuspensionForce = 6000f
}
