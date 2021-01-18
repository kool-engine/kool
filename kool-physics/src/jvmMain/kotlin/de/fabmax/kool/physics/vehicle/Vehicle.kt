package de.fabmax.kool.physics.vehicle

import com.bulletphysics.dynamics.vehicle.WheelInfo
import com.bulletphysics.linearmath.Transform
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.BtRaycastVehicle
import de.fabmax.kool.physics.PhysicsWorld
import javax.vecmath.Vector3f

actual class Vehicle actual constructor(vehicleProps: VehicleProperties, world: PhysicsWorld): CommonVehicle() {

    val btVehicle: BtRaycastVehicle
        get() = TODO()

    private val tmpTransform = Transform()
    private val tmpBtVec1 = Vector3f()
    private val tmpBtVec2 = Vector3f()

    private val wheelInfos = mutableListOf<WheelInfo>()

    actual val chassisTransform = Mat4f()
    actual val wheelTransforms = List(4) { Mat4f() }

    init {
//        val tuning = VehicleTuning()
//        btVehicle = BtRaycastVehicle(tuning, chassis.btRigidBody, world.vehicleRaycaster)
//        btVehicle.setCoordinateSystem(0, 1, 2)
//
//        vehicleProperties.wheels.forEach {
//            wheelInfos += addWheel(it, tuning)
//        }
//
//        chassis.btRigidBody.activationState = CollisionObject.DISABLE_DEACTIVATION
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

    override fun setSteerAngle(value: Float) {
//        for (i in vehicleProperties.wheels.indices) {
//            if (vehicleProperties.wheels[i].isSteering) {
//                setSteerAngle(i, value)
//            }
//        }
    }

    override fun setEngineForce(value: Float) {
//        for (i in vehicleProperties.wheels.indices) {
//            if (vehicleProperties.wheels[i].isMotor) {
//                setEngineForce(i, value)
//            }
//        }
    }

    override fun setBrake(value: Float) {
//        for (i in vehicleProperties.wheels.indices) {
//            if (vehicleProperties.wheels[i].isBrake) {
//                setBrake(i, value)
//            }
//        }
    }

//    actual fun updateWheelTransform(wheelIndex: Int, result: Mat4f): Mat4f {
//        btVehicle.updateWheelTransform(wheelIndex, false)
//        btVehicle.getWheelTransformWS(wheelIndex, tmpTransform).toMat4f(result)
//        return result
//    }

//    private fun addWheel(props: WheelProperties, tuning: VehicleTuning): WheelInfo {
//        tuning.suspensionCompression = props.suspensionCompression
//        tuning.suspensionDamping = props.suspensionDamping
//
//        val wheelInfo = btVehicle.addWheel(props.position.toBtVector3f(), wheelDirectionCS0, wheelAxleCS,
//            props.suspensionRestLength, props.radius, tuning, props.isSteering)
//
//        wheelInfo.suspensionStiffness = props.suspensionStiffness
//        wheelInfo.maxSuspensionTravelCm = props.maxSuspensionTravelCm
//        //wheelInfo.maxSuspensionForce = props.maxSuspensionForce       // not available in JBullet
//        wheelInfo.frictionSlip = props.friction
//
//        return wheelInfo
//    }

    companion object {
        private val wheelDirectionCS0 = Vector3f(0f, -1f, 0f)
        private val wheelAxleCS = Vector3f(-1f, 0f, 0f)
    }
}