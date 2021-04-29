package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.physics.geometry.ConvexMesh
import de.fabmax.kool.physics.geometry.ConvexMeshGeometry
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.physics.vehicle.VehicleProperties
import de.fabmax.kool.physics.vehicle.VehicleUtils
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Model
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.deferred.DeferredPbrShader
import de.fabmax.kool.util.deferred.DeferredPointLights
import de.fabmax.kool.util.deferred.deferredPbrShader
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.sign

class DemoVehicle(world: VehicleWorld, private val vehicleModel: Model, ctx: KoolContext) {

    val vehicle: Vehicle
    val vehicleGroup = Group()

    val vehicleAudio = VehicleAudio()

    private lateinit var vehicleGeometry: ConvexMeshGeometry

    private val steerAnimator = ValueAnimator()
    private val throttleBrakeHandler = ThrottleBrakeHandler()
    private val keyListeners = mutableListOf<InputManager.KeyEventListener>()

    private var previousGear = 0

    private val brakeLightShader: DeferredPbrShader
    private val reverseLightShader: DeferredPbrShader
//    private val headLightLt: DeferredSpotLights.SpotLight
//    private val headLightRt: DeferredSpotLights.SpotLight
    private val rearLightLt: DeferredPointLights.PointLight
    private val rearLightRt: DeferredPointLights.PointLight

    private val rearLightColorBrake = Color(1f, 0.01f, 0.01f)
    private val rearLightColorReverse = Color(1f, 1f, 1f)
    private val rearLightColorBrakeReverse = Color(2f, 1f, 1f)

    init {
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

//        headLightLt = DeferredSpotLights.SpotLight().apply {
//            spotAngle = 30f
//            intensity = 10000f
//        }
//        headLightRt = DeferredSpotLights.SpotLight().apply {
//            spotAngle = 30f
//            intensity = 10000f
//        }
//        val headLights = world.deferredPipeline.pbrPass.addSpotLights(30f)
//        headLights.addSpotLight(headLightLt)
//        headLights.addSpotLight(headLightRt)

        rearLightLt = world.deferredPipeline.pbrPass.dynamicPointLights.addPointLight { }
        rearLightRt = world.deferredPipeline.pbrPass.dynamicPointLights.addPointLight { }

        vehicleModel.onUpdate += { ev ->
            updateVehicle(ev)
        }
    }

    private fun updateVehicle(ev: RenderPass.UpdateEvent) {
        throttleBrakeHandler.update(vehicle.forwardSpeed, ev.deltaT)
        vehicle.isReverse = throttleBrakeHandler.isReverse
        vehicle.steerInput = steerAnimator.tick(ev.deltaT)
        vehicle.throttleInput = throttleBrakeHandler.throttle.value
        vehicle.brakeInput = throttleBrakeHandler.brake.value

        vehicleAudio.rpm = vehicle.engineSpeedRpm
        vehicleAudio.throttle = throttleBrakeHandler.throttle.value
        vehicleAudio.brake = throttleBrakeHandler.brake.value
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
        rearLightLt.intensity = lightIntensity
        rearLightRt.intensity = lightIntensity
        rearLightLt.color.set(lightColor)
        rearLightRt.color.set(lightColor)

        if (vehicle.brakeInput > 0f) {
            brakeLightShader.emissive = Color(75f, 0.25f, 0.125f)
            println("brake light: ${brakeLightShader.emissive}")
        } else {
            brakeLightShader.emissive = Color.BLACK
        }
        if (vehicle.isReverse) {
            reverseLightShader.emissive = Color(75f, 75f, 75f)
        } else {
            reverseLightShader.emissive = Color.BLACK
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

//        vehicle.transform.getRotation(headLightLt.orientation).rotate(-90f, Vec3f.Y_AXIS)
//        headLightRt.orientation.set(headLightLt.orientation)
//        headLightLt.position.set(0.65f, -0.55f, 2.7f)
//        vehicle.transform.transform(headLightLt.position)
//        headLightRt.position.set(-0.65f, -0.55f, 2.7f)
//        vehicle.transform.transform(headLightRt.position)
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
        val chassisBox = VehicleUtils.defaultChassisShape(vehicleGeometry)
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

//            +colorMesh {
//                generate { geometry.addGeometry(vehicleMesh.convexHull) }
//                shader = deferredPbrShader {  }
//            }

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
        val steerLeft: (InputManager.KeyEvent) -> Unit = {
            if (it.isPressed) { steerAnimator.target = 1f } else { steerAnimator.target = 0f }
        }
        val steerRight: (InputManager.KeyEvent) -> Unit = {
            if (it.isPressed) { steerAnimator.target = -1f } else { steerAnimator.target = 0f }
        }
        val accelerate: (InputManager.KeyEvent) -> Unit = {
            throttleBrakeHandler.upKeyPressed = it.isPressed
        }
        val brake: (InputManager.KeyEvent) -> Unit = {
            throttleBrakeHandler.downKeyPressed = it.isPressed
        }

        var prevRecoverTime = 0.0

        keyListeners += ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "steer left", callback = steerLeft)
        keyListeners += ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "steer right", callback = steerRight)
        keyListeners += ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_UP, "accelerate", callback = accelerate)
        keyListeners += ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_DOWN, "brake", callback = brake)
        keyListeners += ctx.inputMgr.registerKeyListener('A', "steer left", filter = { true }, callback = steerLeft)
        keyListeners += ctx.inputMgr.registerKeyListener('D', "steer right", filter = { true }, callback = steerRight)
        keyListeners += ctx.inputMgr.registerKeyListener('W', "accelerate", filter = { true }, callback = accelerate)
        keyListeners += ctx.inputMgr.registerKeyListener('S', "brake", filter = { true }, callback = brake)
        keyListeners += ctx.inputMgr.registerKeyListener('R', "recover", filter = { it.isPressed }) {
            val time = ctx.time
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
        }
    }

    fun cleanUp(ctx: KoolContext) {
        keyListeners.forEach { ctx.inputMgr.removeKeyListener(it) }
        vehicleAudio.stop()
        vehicleGeometry.release()
    }

    fun toggleSound(enabled: Boolean) {
        if (enabled && !vehicleAudio.isStarted) {
            vehicleAudio.start()
        } else if (!enabled && vehicleAudio.isStarted) {
            vehicleAudio.stop()
        }
    }

    class ValueAnimator {
        var target = 0f
        var value = 0f
        var speed = 2f

        fun tick(deltaT: Float): Float {
            var dv = target - value
            if (abs(dv) > speed * deltaT) {
                dv = sign(dv) * speed * deltaT
            }
            value += dv
            return value
        }
    }

    class ThrottleBrakeHandler {
        var upKeyPressed = false
        var downKeyPressed = false

        var reverseTriggerTime = 0f
        var isReverse = false

        val throttle = ValueAnimator()
        val brake = ValueAnimator()

        init {
            throttle.speed = 5f
            brake.speed = 5f
        }

        fun update(forwardSpeed: Float, deltaT: Float) {
            if (abs(forwardSpeed) < 0.1f && downKeyPressed) {
                reverseTriggerTime += deltaT
                if (reverseTriggerTime > 0.2f) {
                    isReverse = true
                }
            } else {
                reverseTriggerTime = 0f
            }

            if (isReverse && !downKeyPressed && forwardSpeed > -0.1f) {
                isReverse = false
            }

            if (!isReverse) {
                throttle.target = if (upKeyPressed) 1f else 0f
                brake.target = if (downKeyPressed) 1f else 0f
            } else {
                // invert throttle / brake buttons while reverse is engaged
                brake.target = if (upKeyPressed) 1f else 0f
                throttle.target = if (downKeyPressed) 1f else 0f
            }
            throttle.tick(deltaT)
            brake.tick(deltaT)
        }
    }

    companion object {
        private val START_POS = Vec3f(0f, 1.5f, -40f)
        private val START_HEAD = 270f
    }
}