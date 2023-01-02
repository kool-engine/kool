package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.geometry.CollisionGeometry

class VehicleProperties {
    var chassisMass = 1500f
    var chassisDims = Vec3f(2f, 1f, 5f)
    var chassisCMOffset = Vec3f(0.0f, -chassisDims.y * 0.5f - 0.2f, 0.25f)

    var numWheels = 4       // for now this is kind of fixed

    var wheelMassFront = 20f
    var wheelWidthFront = 0.4f
    var wheelRadiusFront = 0.4f
    var wheelPosFront = 1.75f

    var wheelMassRear = 20f
    var wheelWidthRear = 0.4f
    var wheelRadiusRear = 0.4f
    var wheelPosRear = -1.75f

    var maxSteerAngle = 30f
    var trackWidthFront = 1.8f
    var trackWidthRear = 1.8f
    var wheelCenterHeightOffset = 0.5f

    var maxBrakeTorque = 5000f
    var brakeTorqueFrontFactor = 0.65f
    var brakeTorqueRearFactor = 0.35f
    var maxHandBrakeTorque = 5000f
    var handBrakeTorqueFrontFactor = 0f
    var handBrakeTorqueRearFactor = 1f

    var maxCompression = 0.2f
    var maxDroop = 0.2f
    var springStrength = 35000f
    var springDamperRate = 4500f
    var camberAngleAtRest = 0.0f
    var camberAngleAtMaxCompression = 0.03f
    var camberAngleAtMaxDroop = -0.03f

    var peakEngineTorque = 900f
    var peakEngineRpm = 6000f
    var gearSwitchTime = 0.35f
    var gearFinalRatio = 4f
    var clutchStrength = 50f

    var frontAntiRollBarStiffness = 10000f
    var rearAntiRollBarStiffness = 10000f

    var chassisMOI = Vec3f(0f)
    var wheelMoiFront = 0f
    var wheelMoiRear = 0f

    var chassisGeometry: CollisionGeometry? = null

    init {
        updateChassisMoiFromDimensionsAndMass()
        updateWheelMoiFromRadiusAndMass()
    }

    fun setSymmetricWheelBase(wheelBase: Float) {
        wheelPosFront = wheelBase * 0.5f
        wheelPosRear = wheelBase * -0.5f
    }

    fun updateChassisMoiFromDimensionsAndMass() {
        chassisMOI = Vec3f(
            (chassisDims.y * chassisDims.y + chassisDims.z * chassisDims.z) * chassisMass / 12.0f,
            (chassisDims.x * chassisDims.x + chassisDims.z * chassisDims.z) * chassisMass / 12.0f * 0.8f,
            (chassisDims.x * chassisDims.x + chassisDims.y * chassisDims.y) * chassisMass / 12.0f)
    }

    fun updateWheelMoiFromRadiusAndMass() {
        wheelMoiFront = 0.5f * wheelMassFront * wheelRadiusFront * wheelRadiusFront
        wheelMoiRear = 0.5f * wheelMassRear * wheelRadiusRear * wheelRadiusRear
    }
}
