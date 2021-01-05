package de.fabmax.kool.physics.vehicle

import ammo.*
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidBody

actual class Vehicle actual constructor(chassis: RigidBody, vehicleProperties: VehicleProperties, world: PhysicsWorld): CommonVehicle(chassis, vehicleProperties) {

    val btVehicle: btRaycastVehicle

    init {
        Physics.checkIsLoaded()

        val tuning = Ammo.btVehicleTuning()
        btVehicle = Ammo.btRaycastVehicle(tuning, chassis.btRigidBody, world.vehicleRaycaster)
        btVehicle.setCoordinateSystem(0, 1, 2)

        vehicleProperties.wheels.forEach {
            addWheel(it, tuning)
        }

        chassis.btRigidBody.setActivationState(Ammo.DISABLE_DEACTIVATION)
    }

    override fun setSteerAngle(wheelIndex: Int, value: Float) {
        btVehicle.setSteeringValue(value.toRad(), wheelIndex)
    }

    override fun setEngineForce(wheelIndex: Int, value: Float) {
        btVehicle.applyEngineForce(value, wheelIndex)
    }

    override fun setBrake(wheelIndex: Int, value: Float) {
        btVehicle.setBrake(value, wheelIndex)
    }

    actual fun updateWheelTransform(wheelIndex: Int, result: Mat4f): Mat4f {
        btVehicle.updateWheelTransform(wheelIndex, false)
        btVehicle.getWheelTransformWS(wheelIndex).toMat4f(result)
        return result
    }

    private fun addWheel(props: WheelProperties, tuning: btVehicleTuning): btWheelInfo {
        val wheelDirectionCS0 = Ammo.btVector3(0f, -1f, 0f)
        val wheelAxleCS = Ammo.btVector3(-1f, 0f, 0f)

        tuning.m_suspensionCompression = props.suspensionCompression
        tuning.m_suspensionDamping = props.suspensionDamping

        val wheelInfo = btVehicle.addWheel(props.position.toBtVector3(), wheelDirectionCS0, wheelAxleCS,
            props.suspensionRestLength, props.radius, tuning, props.isSteering)

        wheelInfo.m_suspensionStiffness = props.suspensionStiffness
        wheelInfo.m_maxSuspensionTravelCm = props.maxSuspensionTravelCm
        wheelInfo.m_maxSuspensionForce = props.maxSuspensionForce
        wheelInfo.m_frictionSlip = props.friction

        return wheelInfo
    }
}