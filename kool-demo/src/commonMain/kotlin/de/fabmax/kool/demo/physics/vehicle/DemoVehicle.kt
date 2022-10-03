package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.LocalKeyCode
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.geometry.ConvexMesh
import de.fabmax.kool.physics.geometry.ConvexMeshGeometry
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.physics.vehicle.VehicleProperties
import de.fabmax.kool.physics.vehicle.VehicleUtils
import de.fabmax.kool.pipeline.deferred.DeferredPbrShader
import de.fabmax.kool.pipeline.deferred.DeferredPointLights
import de.fabmax.kool.pipeline.deferred.DeferredSpotLights
import de.fabmax.kool.pipeline.deferred.deferredPbrShader
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Model
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.DriveAxes
import de.fabmax.kool.util.Time
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max

class DemoVehicle(world: VehicleWorld, private val vehicleModel: Model, ctx: KoolContext) {

    val vehicle: Vehicle
    val vehicleGroup = Group()

    val vehicleAudio = VehicleAudio(world.physics)

    private lateinit var vehicleGeometry: ConvexMeshGeometry

    private lateinit var recoverListener: InputManager.KeyEventListener
    private val inputAxes = DriveAxes(ctx)
    private val throttleBrakeHandler = ThrottleBrakeHandler()

    private var previousGear = 0

    private val brakeLightShader: DeferredPbrShader
    private val reverseLightShader: DeferredPbrShader
    private val rearLightLt: DeferredPointLights.PointLight
    private val rearLightRt: DeferredPointLights.PointLight
    private val headLightLt: DeferredSpotLights.SpotLight
    private val headLightRt: DeferredSpotLights.SpotLight

    private val rearLightColorBrake = Color(1f, 0.01f, 0.01f)
    private val rearLightColorReverse = Color(1f, 1f, 1f)
    private val rearLightColorBrakeReverse = Color(2f, 1f, 1f)

    private var desiredHeadLightPower = 5000f
    var isHeadlightsOn: Boolean
        get() = desiredHeadLightPower > 0f
        set(value) {
            desiredHeadLightPower = if (value) 5000f else 0f
        }

    init {
        vehicleModel.meshes.values.forEach { it.disableShadowCastingAboveLevel(1) }

        vehicleGroup += vehicleModel
        vehicle = makeRaycastVehicle(world)
        registerKeyHandlers(ctx)

        resetVehiclePos()

        vehicleModel.meshes["mesh_head_lights_0"]?.shader = deferredPbrShader {
            albedoSource = Albedo.STATIC_ALBEDO
            albedo = Color.WHITE
            emissive = Color(25f, 25f, 25f)
        }
        brakeLightShader = deferredPbrShader {
            albedoSource = Albedo.STATIC_ALBEDO
            albedo = Color(0.5f, 0.0f, 0.0f)
        }
        vehicleModel.meshes["mesh_brake_lights_0"]?.shader = brakeLightShader
        reverseLightShader = deferredPbrShader {
            albedoSource = Albedo.STATIC_ALBEDO
            albedo = Color(0.6f, 0.6f, 0.6f)
        }
        vehicleModel.meshes["mesh_reverse_lights_0"]?.shader = reverseLightShader

        headLightLt = DeferredSpotLights.SpotLight().apply {
            spotAngle = 30f
            coreRatio = 0.5f
            power = 0f
            maxIntensity = 50f
        }
        headLightRt = DeferredSpotLights.SpotLight().apply {
            spotAngle = 30f
            coreRatio = 0.5f
            power = 0f
            maxIntensity = 50f
        }
        val headLights = world.deferredPipeline.createSpotLights(30f)
        headLights.addSpotLight(headLightLt)
        headLights.addSpotLight(headLightRt)

        rearLightLt = world.deferredPipeline.dynamicPointLights.addPointLight { }
        rearLightRt = world.deferredPipeline.dynamicPointLights.addPointLight { }

        vehicleModel.onUpdate += {
            updateVehicle()
        }
    }

    private fun updateVehicle() {
        throttleBrakeHandler.update(inputAxes.throttle, inputAxes.brake, vehicle.forwardSpeed, Time.deltaT)
        vehicle.isReverse = throttleBrakeHandler.isReverse
        vehicle.throttleInput = throttleBrakeHandler.throttle
        vehicle.brakeInput = throttleBrakeHandler.brake
        vehicle.steerInput = inputAxes.leftRight

        vehicleAudio.rpm = vehicle.engineSpeedRpm
        vehicleAudio.throttle = throttleBrakeHandler.throttle
        vehicleAudio.brake = throttleBrakeHandler.brake
        vehicleAudio.speed = vehicle.linearVelocity.length()

        val lightIntensity: Float
        val lightColor: Color
        when {
            vehicle.isReverse && vehicle.brakeInput > 0f -> {
                lightIntensity = 5f
                lightColor = rearLightColorBrakeReverse
            }
            vehicle.isReverse && vehicle.brakeInput == 0f -> {
                lightIntensity = 5f
                lightColor = rearLightColorReverse
            }
            !vehicle.isReverse && vehicle.brakeInput > 0f -> {
                lightIntensity = 5f
                lightColor = rearLightColorBrake
            }
            else -> {
                lightIntensity = 0f
                lightColor = Color.BLACK
            }
        }
        rearLightLt.power = lightIntensity
        rearLightRt.power = lightIntensity
        rearLightLt.color.set(lightColor)
        rearLightRt.color.set(lightColor)

        if (vehicle.brakeInput > 0f) {
            brakeLightShader.emissive(Color(25f, 0.25f, 0.125f))
        } else {
            brakeLightShader.emissive(Color.BLACK)
        }
        if (vehicle.isReverse) {
            reverseLightShader.emissive(Color(25f, 25f, 25f))
        } else {
            reverseLightShader.emissive(Color.BLACK)
        }

        vehicleAudio.slip = 0f
        for (i in 0..3) {
            val slip = max(abs(vehicle.wheelInfos[i].lateralSlip), (abs(vehicle.wheelInfos[i].longitudinalSlip) - 0.3f) / 0.7f)
            if (slip > vehicleAudio.slip) {
                vehicleAudio.slip = slip
            }
        }

        val gear = vehicle.currentGear
        if (gear != previousGear) {
            vehicleAudio.gearOut = gear == 0
            vehicleAudio.gearIn = gear != 0
        }
        previousGear = gear

        rearLightLt.position.set(0.4f, -0.1f, -2.5f)
        vehicle.transform.transform(rearLightLt.position)
        rearLightRt.position.set(-0.4f, -0.1f, -2.5f)
        vehicle.transform.transform(rearLightRt.position)

        vehicle.transform.getRotation(headLightLt.orientation).rotate(-85f, Vec3f.Y_AXIS).rotate(-7f, Vec3f.Z_AXIS)
        vehicle.transform.getRotation(headLightRt.orientation).rotate(-95f, Vec3f.Y_AXIS).rotate(-7f, Vec3f.Z_AXIS)
        headLightLt.position.set(0.65f, -0.45f, 2.7f)
        vehicle.transform.transform(headLightLt.position)
        headLightRt.position.set(-0.65f, -0.45f, 2.7f)
        vehicle.transform.transform(headLightRt.position)

        headLightLt.power = desiredHeadLightPower * 0.1f + headLightLt.power * 0.9f
        headLightRt.power = headLightLt.power
    }

    fun resetVehiclePos() {
        vehicle.position = START_POS
        vehicle.setRotation(Mat3f().rotate(START_HEAD, Vec3f.Y_AXIS))
    }

    private fun makeRaycastVehicle(world: VehicleWorld): Vehicle {
        val vehicleProps = VehicleProperties().apply {
            chassisDims = Vec3f(2.1f, 0.98f, 5.4f)
            trackWidth = 1.6f
            maxBrakeTorqueFront = 2400f
            maxBrakeTorqueRear = 1200f
            gearFinalRatio = 3f
            maxCompression = 0.1f
            maxDroop = 0.1f
            springStrength = 75000f
            springDamperRate = 9000f

            wheelRadiusFront = 0.36f
            wheelWidthFront = 0.3f
            wheelMassFront = 25f
            wheelPosFront = 1.7f

            wheelRadiusRear = 0.4f
            wheelWidthRear = 0.333f
            wheelMassRear = 30f
            wheelPosRear = -1.7f

            updateChassisMoiFromDimensionsAndMass()
            updateWheelMoiFromRadiusAndMass()
        }

        val vehicleMesh = ConvexMesh(listOf(
            Vec3f(-1.05f, -0.65f,  2.5f), Vec3f(-1.05f, -0.4f,  2.75f),
            Vec3f( 1.05f, -0.65f,  2.5f), Vec3f( 1.05f, -0.4f,  2.75f),
            Vec3f(-0.95f, -0.65f, -2.5f), Vec3f(-0.95f, 0.25f, -2.6f),
            Vec3f( 0.95f, -0.65f, -2.5f), Vec3f( 0.95f, 0.25f, -2.6f),

            Vec3f(-1.05f, -0.55f,  2.75f), Vec3f(1.05f, -0.55f,  2.75f),
            Vec3f( -0.95f, 0.2f, 0f), Vec3f( 0.95f, 0.2f, 0f)
        ))

        vehicleGeometry = ConvexMeshGeometry(vehicleMesh)
        val chassisBox = VehicleUtils.defaultChassisShape(vehicleGeometry, Physics.NOTIFY_TOUCH_FOUND or Physics.NOTIFY_CONTACT_POINTS)
        vehicleProps.chassisShapes = listOf(chassisBox)

        val vehicle = Vehicle(vehicleProps, world.physics)
        world.physics.addActor(vehicle)

        vehicleGroup.apply {
            val wheelTransforms = mutableListOf<Group>()
            wheelTransforms += vehicleModel.findNode("Wheel_fl")!! as Group
            wheelTransforms += vehicleModel.findNode("Wheel_fr")!! as Group
            wheelTransforms += vehicleModel.findNode("Wheel_rl")!! as Group
            wheelTransforms += vehicleModel.findNode("Wheel_rr")!! as Group

            wheelTransforms.forEach {
                vehicleModel -= it
                vehicleGroup += it
            }

            world.scene.onRenderScene += {
                transform.set(vehicle.transform)
                setDirty()
                for (i in 0..3) {
                    wheelTransforms[i].transform.set(vehicle.wheelInfos[i].transform)
                    wheelTransforms[i].setDirty()
                }
            }
        }

        vehicleModel.translate(0f, -0.86f, 0f)

        return vehicle
    }

    private fun registerKeyHandlers(ctx: KoolContext) {
        // throttle and brake are used in a digital fashion, set low r

        var prevRecoverTime = 0.0
        recoverListener = ctx.inputMgr.registerKeyListener(LocalKeyCode('r'), "recover", filter = { it.isPressed }) {
            val time = Time.gameTime
            val recoverHard = time - prevRecoverTime < 0.3
            prevRecoverTime = time

            if (recoverHard) {
                // reset vehicle position to spawn point on double tap
                resetVehiclePos()
            } else {
                // move vehicle up and reset rotation to recover from a flipped orientation
                val pos = vehicle.position
                vehicle.position = Vec3f(pos.x, pos.y + 2f, pos.z)

                val head = vehicle.transform.transform(MutableVec3f(0f, 0f, 1f), 0f)
                val headDeg = atan2(head.x, head.z).toDeg()
                val ori = Mat3f().rotate(headDeg, Vec3f.Y_AXIS)
                vehicle.setRotation(ori)
            }
            vehicle.linearVelocity = Vec3f.ZERO
            vehicle.angularVelocity = Vec3f.ZERO
            vehicle.setToRestState()
        }
    }

    fun cleanUp(ctx: KoolContext) {
        inputAxes.dispose(ctx)
        vehicleAudio.stop()
        vehicleGeometry.release()
        ctx.inputMgr.removeKeyListener(recoverListener)
    }

    fun toggleSound(enabled: Boolean) {
        if (enabled && !vehicleAudio.isStarted) {
            vehicleAudio.start()
        } else if (!enabled && vehicleAudio.isStarted) {
            vehicleAudio.stop()
        }
    }

    class ThrottleBrakeHandler {
        var reverseTriggerTime = 0f
        var isReverse = false

        var throttle = 0f
        var brake = 0f

        fun update(throttleIn: Float, brakeIn: Float, forwardSpeed: Float, deltaT: Float) {
            if (abs(forwardSpeed) < 0.1f && brakeIn > 0f) {
                reverseTriggerTime += deltaT
                if (reverseTriggerTime > 0.2f) {
                    isReverse = true
                }
            } else {
                reverseTriggerTime = 0f
            }

            if (isReverse && brakeIn == 0f && forwardSpeed > -0.1f) {
                isReverse = false
            }

            if (!isReverse) {
                throttle = throttleIn
                brake = brakeIn
            } else {
                // invert throttle / brake buttons while reverse is engaged
                brake = throttleIn
                throttle = brakeIn
            }
        }
    }

    companion object {
        private val START_POS = Vec3f(0f, 1.5f, -40f)
        private val START_HEAD = 270f
    }
}