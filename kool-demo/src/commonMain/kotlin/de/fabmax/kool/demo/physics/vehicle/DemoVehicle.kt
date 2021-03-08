package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.physics.vehicle.VehicleProperties
import de.fabmax.kool.physics.vehicle.VehicleUtils
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.group
import de.fabmax.kool.util.Color
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.sign

class DemoVehicle(world: VehicleWorld, ctx: KoolContext) {

    val vehicle: Vehicle
    val vehicleMesh = colorMesh {  }
    val vehicleGroup = Group()

    val vehicleAudio = VehicleAudio(ctx)

    private val steerAnimator = ValueAnimator()
    private val throttleBrakeHandler = ThrottleBrakeHandler()
    private val keyListeners = mutableListOf<InputManager.KeyEventListener>()

    private var previousGear = 0

    init {
        vehicle = makeRaycastVehicle(world)
        registerKeyHandlers(ctx)

        vehicleGroup.onUpdate += { ev ->
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
    }

    private fun makeRaycastVehicle(world: VehicleWorld): Vehicle {
        val vehicleProps = VehicleProperties().apply {
            groundMaterialFrictions = mapOf(world.defaultMaterial to 1.5f)
            chassisDims = Vec3f(2f, 1f, 5f)
            wheelFrontZ = 1.6f
            wheelRearZ = -1.5f
            trackWidth = 2.45f
            maxBrakeTorqueFront = 2400f
            maxBrakeTorqueRear = 1200f

            updateChassisMoiFromDimensionsAndMass()
        }

        val wheelBumperDims = Vec3f(vehicleProps.trackWidth + vehicleProps.wheelWidth, 0.2f, vehicleProps.wheelRadius * 2f)

        val chassisBox = VehicleUtils.defaultChassisShape(vehicleProps.chassisDims)
        vehicleProps.chassisShapes = listOf(
            chassisBox,
            // add additional shapes which act as collision dummys for wheel vs. drivable object collisions
            Shape(
                BoxGeometry(wheelBumperDims), chassisBox.material,
                Mat4f().translate(0f, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelFrontZ),
                simFilterData = chassisBox.simFilterData, queryFilterData = chassisBox.queryFilterData),
            Shape(
                BoxGeometry(wheelBumperDims), chassisBox.material,
                Mat4f().translate(0f, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelRearZ),
                simFilterData = chassisBox.simFilterData, queryFilterData = chassisBox.queryFilterData)
        )

        val pose = Mat4f().translate(0f, 1.5f, -40f)
        val vehicle = Vehicle(vehicleProps, world.physics, pose)
        vehicle.setRotation(Mat3f().rotate(-90f, Vec3f.Y_AXIS))
        world.physics.addActor(vehicle)

        vehicleGroup.apply {
            val wheelMeshes = mutableListOf<Group>()
            for (i in 0..3) {
                wheelMeshes += group {
                    +colorMesh {
                        generate {
                            color = Color.DARK_GRAY.toLinear()
                            rotate(90f, Vec3f.Z_AXIS)
                            cylinder {
                                steps = 32
                                radius = vehicleProps.wheelRadius
                                height = vehicleProps.wheelWidth
                                origin.set(0f, -height * 0.5f, 0f)
                            }
                        }
                        shader = pbrShader {
                            useImageBasedLighting(world.envMaps)
                            useScreenSpaceAmbientOcclusion(world.aoMap)
                            shadowMaps += world.shadows
                        }
                    }
                }.also { +it }
            }

            vehicleMesh.apply {
                generate {
                    color = VehicleDemo.color(600f)
                    cube {
                        size.set(vehicleProps.chassisDims)
                        centered()
                    }
                }
                shader = pbrShader {
                    useImageBasedLighting(world.envMaps)
                    useScreenSpaceAmbientOcclusion(world.aoMap)
                    shadowMaps += world.shadows
                }
            }
            +vehicleMesh

            onUpdate += {
                transform.set(vehicle.transform)
                setDirty()
                for (i in 0..3) {
                    wheelMeshes[i].transform.set(vehicle.wheelInfos[i].transform)
                    wheelMeshes[i].setDirty()
                }
            }
        }

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

        keyListeners += ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "steer left", callback = steerLeft)
        keyListeners += ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "steer right", callback = steerRight)
        keyListeners += ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_UP, "accelerate", callback = accelerate)
        keyListeners += ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_DOWN, "brake", callback = brake)
        keyListeners += ctx.inputMgr.registerKeyListener('A', "steer left", filter = { true }, callback = steerLeft)
        keyListeners += ctx.inputMgr.registerKeyListener('D', "steer right", filter = { true }, callback = steerRight)
        keyListeners += ctx.inputMgr.registerKeyListener('W', "accelerate", filter = { true }, callback = accelerate)
        keyListeners += ctx.inputMgr.registerKeyListener('S', "brake", filter = { true }, callback = brake)
        keyListeners += ctx.inputMgr.registerKeyListener('R', "recover", filter = { it.isPressed }) {
            val pos = vehicle.position
            vehicle.position = Vec3f(pos.x, pos.y + 2f, pos.z)

            val head = vehicle.transform.transform(MutableVec3f(0f, 0f, 1f), 0f)
            val headDeg = atan2(head.x, head.z).toDeg()
            val ori = Mat3f().rotate(headDeg, Vec3f.Y_AXIS)
            vehicle.setRotation(ori)
            vehicle.linearVelocity = Vec3f.ZERO
            vehicle.angularVelocity = Vec3f.ZERO
        }
    }

    fun cleanUp(ctx: KoolContext) {
        keyListeners.forEach { ctx.inputMgr.removeKeyListener(it) }
        vehicleAudio.stop()
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

}