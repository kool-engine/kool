package de.fabmax.kool.demo.physics

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.joints.RevoluteJoint
import de.fabmax.kool.physics.shapes.*
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.physics.vehicle.VehicleProperties
import de.fabmax.kool.physics.vehicle.VehicleUtils
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_DRIVABLE_OBSTACLE
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_DRIVABLE_OBSTACLE_AGAINST
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_GROUND
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_GROUND_AGAINST
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.abs
import kotlin.math.sign

class VehicleDemo : DemoScene("Vehicle") {

    private lateinit var ibl: EnvironmentMaps
    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()

    private lateinit var ground: RigidBody
    private lateinit var vehicle: Vehicle

    private val groundMaterial = Material(0.5f)
    private val gndProps = RigidBodyProperties().apply {
        material = groundMaterial
        simFilterData.set(COLLISION_FLAG_GROUND, COLLISION_FLAG_GROUND_AGAINST)
        VehicleUtils.setupDrivableSurface(this)
    }

    private val obstacleProps = RigidBodyProperties().apply {
        material = groundMaterial
        simFilterData.set(COLLISION_FLAG_DRIVABLE_OBSTACLE, COLLISION_FLAG_DRIVABLE_OBSTACLE_AGAINST)
        VehicleUtils.setupDrivableSurface(this)
    }

    override fun setupMainScene(ctx: KoolContext) = scene {
        ctx.assetMgr.launch {
            lighting.singleLight {
                setDirectional(Vec3f(1f, -1.2f, -0.8f))
                setColor(Color.WHITE, 1f)
            }
            ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/colorful_studio_1k.rgbe.png", this)
            aoPipeline = AoPipeline.createForward(this@scene).apply { mapSize = 0.75f }
            shadows += CascadedShadowMap(this@scene, 0).apply {
                mapRanges[0].set(0f, 0.15f)
                mapRanges[1].set(0.15f, 0.4f)
                mapRanges[2].set(0.4f, 1f)
            }
            +Skybox.cube(ibl.reflectionMap, 1f)

            Physics.awaitLoaded()
            val world = PhysicsWorld()
            val steerAnimator = ValueAnimator()
            val throttleBrakeHandler = ThrottleBrakeHandler()

            gndProps.material = groundMaterial
            obstacleProps.material = groundMaterial

            makeGround(world)
            makeRamp(Mat4f().translate(0f, 0f, 30f), world)
            makeBumps(Mat4f().translate(20f, 0f, 0f), world)
            makeBoxes(Mat4f().translate(-20f, 0f, 30f), world)
            makeRocker(Mat4f().translate(-20f, 0f, 90f), world)
            makeRaycastVehicle(world)

            (camera as PerspectiveCamera).apply {
                clipNear = 1f
                clipFar = 1000f
            }

            onUpdate += {
                throttleBrakeHandler.update(vehicle.forwardSpeed, it.deltaT)
                vehicle.isReverse = throttleBrakeHandler.isReverse
                vehicle.setSteerInput(steerAnimator.tick(it.deltaT))
                vehicle.setThrottleInput(throttleBrakeHandler.throttle.value)
                vehicle.setBrakeInput(throttleBrakeHandler.brake.value)
                world.stepPhysics(it.deltaT)
            }

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

            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "steer left", callback = steerLeft)
            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "steer right", callback = steerRight)
            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_UP, "accelerate", callback = accelerate)
            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_DOWN, "brake", callback = brake)
            ctx.inputMgr.registerKeyListener('a', "steer left", callback = steerLeft)
            ctx.inputMgr.registerKeyListener('d', "steer left", callback = steerRight)
            ctx.inputMgr.registerKeyListener('w', "steer left", callback = accelerate)
            ctx.inputMgr.registerKeyListener('s', "steer left", callback = brake)
        }
    }

    private fun Scene.makeRaycastVehicle(world: PhysicsWorld) {
        val vehicleProps = VehicleProperties()
        vehicleProps.groundMaterialFrictions = mapOf(groundMaterial to 1.5f)
        vehicleProps.chassisDims = Vec3f(2f, 1f, 5f)
        vehicleProps.wheelFrontZ = 1.25f
        vehicleProps.wheelRearZ = -1.75f
        vehicleProps.trackWidth = 2.45f

        val wheelBumperDims = Vec3f(vehicleProps.trackWidth + vehicleProps.wheelWidth, 0.2f, vehicleProps.wheelRadius * 2f)

        vehicleProps.chassisShapes = listOf(
            BoxShape(vehicleProps.chassisDims),
            // add additional shapes which act as collision dummys for wheel vs. drivable object collisions
            BoxShape(wheelBumperDims, Mat4f().translate(0f, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelFrontZ)),
            BoxShape(wheelBumperDims, Mat4f().translate(0f, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelRearZ))
        )
        vehicle = Vehicle(vehicleProps, world)
        world.addVehicle(vehicle)

        +group {
            val wheelMeshes = mutableListOf<Group>()
            for (i in 0..3) {
                wheelMeshes += group {
                    +colorMesh {
                        generate {
                            color = Color.DARK_GRAY.toLinear()
                            rotate(90f, Vec3f.Z_AXIS)
                            cylinder {
                                radius = vehicleProps.wheelRadius
                                height = vehicleProps.wheelWidth
                                origin.set(0f, -height * 0.5f, 0f)
                            }
                        }
                        shader = pbrShader {
                            useImageBasedLighting(ibl)
                            useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                            shadowMaps += shadows
                        }
                    }
                }.also { +it }
            }

            +colorMesh {
                generate {
                    color = Color.MD_ORANGE.toLinear()
                    cube {
                        size.set(vehicleProps.chassisDims)
                        centered()
                    }
                }
                shader = pbrShader {
                    useImageBasedLighting(ibl)
                    useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                    shadowMaps += shadows
                }
            }

            +orbitInputTransform {
                setMouseRotation(180f, -30f)
                +camera
            }

            onUpdate += {
                transform.set(vehicle.chassisTransform)
                setDirty()
                for (i in 0..3) {
                    wheelMeshes[i].transform.set(vehicle.wheelTransforms[i])
                    wheelMeshes[i].setDirty()
                }
            }
        }
    }

    private fun Scene.makeBoxes(frame: Mat4f, world: PhysicsWorld) {
        val n = 6
        val size = 2f
        val stepX = size * 1.2f
        for (r in 0 until n) {
            val c = n - r
            val x = (c - 1) * stepX * -0.5f

            for (i in 0 until c) {
                val boxShape = BoxShape(Vec3f(size))
                val body = RigidBody(boxShape, 250f, obstacleProps)
                val pos = MutableVec3f(x + i * stepX, size * 0.5f + r * size, 0f)
                frame.transform(pos)
                body.origin = pos
                world.addRigidBody(body)

                val color = if (i % 2 == 0) Color.MD_ORANGE_300 else  Color.MD_ORANGE_100
                +body.toPrettyMesh(color.toLinear())
            }
        }
    }

    private fun Scene.makeRocker(frame: Mat4f, world: PhysicsWorld) {
        val anchor = RigidBody(BoxShape(Vec3f(7.5f, 1.5f, 0.3f)), 0f, obstacleProps)
        anchor.origin = frame.transform(MutableVec3f(0f, 0.75f, 0f))
        val rocker = RigidBody(BoxShape(Vec3f(7.5f, 0.15f, 15f)), 500f, obstacleProps)
        rocker.origin = frame.transform(MutableVec3f(0f, 1.7f, 0f))
        world.addRigidBody(anchor)
        world.addRigidBody(rocker)
        +anchor.toPrettyMesh(Color.MD_ORANGE.toLinear())
        +rocker.toPrettyMesh(Color.MD_ORANGE_100.toLinear())

        val joint = RevoluteJoint(anchor, rocker, Mat4f().translate(0f, 0.85f, 0f), Mat4f().translate(0f, 0f, 0.2f))
        world.addJoint(joint)
    }

    private fun Scene.makeRamp(frame: Mat4f, world: PhysicsWorld) {
        val ramp = RigidBody(BoxShape(Vec3f(10f, 2f, 10f)), 0f, obstacleProps)
        ramp.origin = frame.transform(MutableVec3f(0f, 0f, 0f))
        ramp.setRotation(Mat3f().rotate(-11f, 0f, 0f))
        world.addRigidBody(ramp)
        +ramp.toPrettyMesh(Color.MD_ORANGE_100.toLinear())
    }

    private fun Scene.makeBumps(frame: Mat4f, world: PhysicsWorld) {
        for (i in 0 until 30) {
            val c = if (i % 2 == 0) Color.MD_ORANGE_300 else Color.MD_ORANGE_100
            for (s in -1 .. 1 step 2) {
                val bump = RigidBody(CylinderShape(4f, 0.5f), 0f, obstacleProps)
                bump.origin = frame.transform(MutableVec3f(2f * s, -0.3f, i * 3.1f + s * 0.4f))
                //bump.setRotation(Mat3f().rotate(0f, 0f, 90f))
                world.addRigidBody(bump)
                +bump.toPrettyMesh(c.toLinear())
            }
        }
    }

    private fun Scene.makeGround(world: PhysicsWorld) {
        val gndMesh = textureMesh(isNormalMapped = true) {
            generate {
                isCastingShadow = false
                vertexModFun = {
                    texCoord.set(x / 10f, z / 10f)
                }
                grid {
                    sizeX = 500f
                    sizeY = 500f
                    stepsX = sizeX.toInt() / 10
                    stepsY = sizeY.toInt() / 10
                }
            }
            shader = pbrShader {
                useAlbedoMap("${Demo.pbrBasePath}/tile_flat/tiles_flat_gray.png", true)
                useNormalMap("${Demo.pbrBasePath}/tile_flat/tiles_flat_normal.png")
                useImageBasedLighting(ibl)
                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                albedo = Color.MD_ORANGE_100.mix(Color.WHITE, 0.5f).toLinear()
                shadowMaps += shadows
            }
        }
        +gndMesh

        ground = RigidBody(PlaneShape(), 0f, gndProps)
        ground.setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
        world.addRigidBody(ground)
    }

    private fun RigidBody.toPrettyMesh(color: Color, rough: Float = 0.8f, metal: Float = 0f) = toMesh(color) {
        roughness = rough
        metallic = metal
        useImageBasedLighting(ibl)
        useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
        shadowMaps += shadows
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
                if (reverseTriggerTime > 0.5f) {
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