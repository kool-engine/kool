package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.DriveAxes
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.geometry.ConvexMesh
import de.fabmax.kool.physics.geometry.ConvexMeshGeometry
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.physics.vehicle.VehicleProperties
import de.fabmax.kool.pipeline.deferred.DeferredKslPbrShader
import de.fabmax.kool.pipeline.deferred.DeferredPointLights
import de.fabmax.kool.pipeline.deferred.DeferredSpotLights
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max

class DemoVehicle(val demo: VehicleDemo, private val vehicleModel: Model, ctx: KoolContext) {
    private val world: VehicleWorld get() = demo.vehicleWorld

    val vehicle: Vehicle
    val vehicleGroup = Node()
    private val vehicleGroupInner = Node()

    val vehicleAudio = VehicleAudio(world.physics)

    private lateinit var recoverListener: InputStack.SimpleKeyListener
    private val inputAxes = DriveAxes(ctx)
    private val throttleBrakeHandler = ThrottleBrakeHandler()

    private var previousGear = 0

    private val brakeLightShader: DeferredKslPbrShader
    private val reverseLightShader: DeferredKslPbrShader
    private val rearLightLt: DeferredPointLights.PointLight
    private val rearLightRt: DeferredPointLights.PointLight
    private val headLightLt: DeferredSpotLights.SpotLight
    private val headLightRt: DeferredSpotLights.SpotLight

    private val rearLightColorBrake = Color(1f, 0.01f, 0.01f)
    private val rearLightColorReverse = Color(1f, 1f, 1f)
    private val rearLightColorBrakeReverse = Color(2f, 1f, 1f)

    private var desiredHeadLightRange = 70f
    var isHeadlightsOn: Boolean
        get() = desiredHeadLightRange > 0f
        set(value) {
            desiredHeadLightRange = if (value) 70f else 0f
        }

    init {
        vehicleModel.meshes.values.forEach { it.disableShadowCastingAboveLevel(1) }

        vehicleGroup += vehicleGroupInner
        vehicleGroupInner += vehicleModel

        vehicle = makeRaycastVehicle(world)
        registerKeyHandlers()

        resetVehiclePos()

        vehicleModel.meshes["mesh_head_lights_0"]?.shader = deferredKslPbrShader {
            emission { constColor(Color(25f, 25f, 25f)) }
        }
        brakeLightShader = deferredKslPbrShader {
            color { constColor(Color(0.5f, 0.0f, 0.0f)) }
            emission { uniformColor(Color.BLACK) }
        }
        vehicleModel.meshes["mesh_brake_lights_0"]?.shader = brakeLightShader
        reverseLightShader = deferredKslPbrShader {
            color { constColor(Color(0.6f, 0.6f, 0.6f)) }
            emission { uniformColor(Color.BLACK) }
        }
        vehicleModel.meshes["mesh_reverse_lights_0"]?.shader = reverseLightShader

        headLightLt = DeferredSpotLights.SpotLight().apply {
            spotAngle = 30f.deg
            coreRatio = 0.5f
            radius = 0f
            intensity = 500f
        }
        headLightRt = DeferredSpotLights.SpotLight().apply {
            spotAngle = 30f.deg
            coreRatio = 0.5f
            radius = 0f
            intensity = 500f
        }
        val headLights = world.deferredPipeline.createSpotLights(30f.deg)
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

        val steerScale = 1f - (abs(vehicle.forwardSpeed) / 100f).clamp(0f, 0.9f)
        vehicle.steerInput = inputAxes.leftRight * steerScale

        vehicleAudio.rpm = vehicle.engineSpeedRpm
        vehicleAudio.throttle = throttleBrakeHandler.throttle
        vehicleAudio.brake = throttleBrakeHandler.brake
        vehicleAudio.speed = vehicle.linearVelocity.length()

        val lightRadius: Float
        val lightColor: Color
        when {
            vehicle.isReverse && vehicle.brakeInput > 0f -> {
                lightRadius = 2.5f
                lightColor = rearLightColorBrakeReverse
            }
            vehicle.isReverse && vehicle.brakeInput == 0f -> {
                lightRadius = 2.5f
                lightColor = rearLightColorReverse
            }
            !vehicle.isReverse && vehicle.brakeInput > 0f -> {
                lightRadius = 2.5f
                lightColor = rearLightColorBrake
            }
            else -> {
                lightRadius = 0f
                lightColor = Color.BLACK
            }
        }
        rearLightLt.radius = lightRadius
        rearLightRt.radius = lightRadius
        rearLightLt.color.set(lightColor)
        rearLightRt.color.set(lightColor)

        if (vehicle.brakeInput > 0f) {
            brakeLightShader.emission = Color(25f, 0.25f, 0.125f)
        } else {
            brakeLightShader.emission = Color.BLACK
        }
        if (vehicle.isReverse) {
            reverseLightShader.emission = Color(25f, 25f, 25f)
        } else {
            reverseLightShader.emission = Color.BLACK
        }

        var maxSlip = 0f
        for (i in 0..3) {
            val slip = max(abs(vehicle.wheelInfos[i].lateralSlip) * 2f, abs(vehicle.wheelInfos[i].longitudinalSlip) * 1.5f)
            if (slip > maxSlip) {
                maxSlip = slip
            }
        }
        vehicleAudio.slip = smoothStep(0f, 1f, maxSlip)

        val gear = vehicle.currentGear
        if (gear != previousGear) {
            vehicleAudio.gearOut = gear == 0
            vehicleAudio.gearIn = gear != 0
        }
        previousGear = gear

        rearLightLt.position.set(0.4f, 0.6f, -2.5f)
        vehicle.transform.transform(rearLightLt.position)
        rearLightRt.position.set(-0.4f, 0.6f, -2.5f)
        vehicle.transform.transform(rearLightRt.position)

        headLightLt.rotation.set(QuatF.IDENTITY).mul(vehicle.transform.rotation).rotate((-85f).deg, Vec3f.Y_AXIS).rotate((-7f).deg, Vec3f.Z_AXIS)
        headLightRt.rotation.set(QuatF.IDENTITY).mul(vehicle.transform.rotation).rotate((-95f).deg, Vec3f.Y_AXIS).rotate((-7f).deg, Vec3f.Z_AXIS)
        headLightLt.position.set(0.65f, 0.3f, 2.7f)
        vehicle.transform.transform(headLightLt.position)
        headLightRt.position.set(-0.65f, 0.3f, 2.7f)
        vehicle.transform.transform(headLightRt.position)

        headLightLt.radius = desiredHeadLightRange * 0.1f + headLightLt.radius * 0.9f
        headLightRt.radius = headLightLt.radius
    }

    fun resetVehiclePos() {
        vehicle.position = START_POS
        vehicle.setRotation(MutableMat3f().rotate(START_HEAD.deg, Vec3f.Y_AXIS))
    }

    private fun makeRaycastVehicle(world: VehicleWorld): Vehicle {
        val vehicleMesh = ConvexMesh(
            listOf(
                Vec3f(-1.05f, -0.65f, 2.5f), Vec3f(-1.05f, -0.4f, 2.75f),
                Vec3f(1.05f, -0.65f, 2.5f), Vec3f(1.05f, -0.4f, 2.75f),
                Vec3f(-0.95f, -0.65f, -2.5f), Vec3f(-0.95f, 0.25f, -2.6f),
                Vec3f(0.95f, -0.65f, -2.5f), Vec3f(0.95f, 0.25f, -2.6f),

                Vec3f(-1.05f, -0.55f, 2.75f), Vec3f(1.05f, -0.55f, 2.75f),
                Vec3f(-0.95f, 0.2f, 0f), Vec3f(0.95f, 0.2f, 0f)
            )
        )

        val vehicleProps = VehicleProperties().apply {
            chassisDims = Vec3f(2.1f, 0.98f, 5.4f)
            trackWidthFront = 1.6f
            trackWidthRear = 1.65f
            maxBrakeTorque = 3000f
            gearFinalRatio = 3f
            maxCompression = 0.1f
            maxDroop = 0.1f
            springStrength = 75000f
            springDamperRate = 9000f

            wheelRadiusFront = 0.36f
            wheelWidthFront = 0.3f
            wheelMassFront = 25f
            wheelPosFront = 1.7f
            brakeTorqueFrontFactor = 0.6f

            wheelRadiusRear = 0.4f
            wheelWidthRear = 0.333f
            wheelMassRear = 30f
            wheelPosRear = -1.7f
            brakeTorqueRearFactor = 0.4f

            chassisGeometry = ConvexMeshGeometry(vehicleMesh)

            updateChassisMoiFromDimensionsAndMass()
            updateWheelMoiFromRadiusAndMass()
        }

        vehicleGroupInner.transform.translate(vehicleProps.chassisCMOffset)

        val vehicle = Vehicle(vehicleProps, world.physics)
        world.physics.addActor(vehicle)

        vehicleGroup.apply {
            transform = vehicle.transform

            val wheelTransforms = mutableListOf<Node>()
            wheelTransforms += vehicleModel.findNode("Wheel_fl")!!
            wheelTransforms += vehicleModel.findNode("Wheel_fr")!!
            wheelTransforms += vehicleModel.findNode("Wheel_rl")!!
            wheelTransforms += vehicleModel.findNode("Wheel_rr")!!

            wheelTransforms.forEachIndexed { i, it ->
                vehicleModel -= it
                vehicleGroupInner += it
                it.transform = vehicle.wheelInfos[i].transform
            }
        }

        vehicleModel.transform.translate(MutableVec3f(vehicleProps.chassisCMOffset).mul(-1f))

        return vehicle
    }

    private fun registerKeyHandlers() {
        // throttle and brake are used in a digital fashion, set low r

        var prevRecoverTime = 0.0
        recoverListener = KeyboardInput.addKeyListener(LocalKeyCode('r'), "recover", filter = { it.isPressed }) {
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
                val ori = MutableMat3f().rotate(headDeg.deg, Vec3f.Y_AXIS)
                vehicle.setRotation(ori)
            }
            vehicle.linearVelocity = Vec3f.ZERO
            vehicle.angularVelocity = Vec3f.ZERO
            vehicle.setToRestState()

            demo.timer?.reset(false)
        }
    }

    fun cleanUp() {
        inputAxes.release()
        vehicleAudio.stop()
        KeyboardInput.removeKeyListener(recoverListener)
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
            if (abs(forwardSpeed) < 0.5f && brakeIn > 0f) {
                reverseTriggerTime += deltaT
                if (reverseTriggerTime > 0.2f) {
                    isReverse = true
                }
            } else {
                reverseTriggerTime = 0f
            }

            if (isReverse && brakeIn == 0f && forwardSpeed > -0.5f) {
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