package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.*
import de.fabmax.kool.physics.joints.RevoluteJoint
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.physics.vehicle.VehicleProperties
import de.fabmax.kool.physics.vehicle.VehicleUtils
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_DRIVABLE_OBSTACLE
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_DRIVABLE_OBSTACLE_AGAINST
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_GROUND
import de.fabmax.kool.physics.vehicle.VehicleUtils.COLLISION_FLAG_GROUND_AGAINST
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sign

class VehicleDemo : DemoScene("Vehicle") {

    private lateinit var ibl: EnvironmentMaps
    private lateinit var aoPipeline: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()

    private lateinit var ground: RigidStatic
    private lateinit var vehicle: Vehicle

    private val groundMaterial = Material(0.5f)

    private val groundSimFilterData = FilterData(COLLISION_FLAG_GROUND, COLLISION_FLAG_GROUND_AGAINST)
    private val groundQryFilterData = FilterData().apply { VehicleUtils.setupDrivableSurface(this) }

    private val obstacleSimFilterData = FilterData(COLLISION_FLAG_DRIVABLE_OBSTACLE, COLLISION_FLAG_DRIVABLE_OBSTACLE_AGAINST)
    private val obstacleQryFilterData = FilterData().apply { VehicleUtils.setupDrivableSurface(this) }

    private var dashboard: VehicleUi? = null

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

            makeGround(world)
            makeBoxes(Mat4f().translate(-20f, 0f, 30f), world)
            makeRocker(Mat4f().translate(-20f, 0f, 90f), world)

            +colorMesh {
                generate {
                    makeRamp(Mat4f().translate(0f, 0f, 30f))
                    makeBumps(Mat4f().translate(20f, 0f, 0f))
                    makeHalfPipe(Mat4f().translate(-40f, 0f, 30f).rotate(90f, 0f, -1f, 0f))
                }
                shader = pbrShader {
                    albedoSource = Albedo.VERTEX_ALBEDO
                    shadowMaps += shadows
                    useImageBasedLighting(ibl)
                    useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                }

                makeStaticCollisionBody(geometry, world)
            }

            makeRaycastVehicle(world)

            (camera as PerspectiveCamera).apply {
                clipNear = 1f
                clipFar = 1000f
            }

            onUpdate += {
                throttleBrakeHandler.update(vehicle.forwardSpeed, it.deltaT)
                vehicle.isReverse = throttleBrakeHandler.isReverse
                vehicle.steerInput = steerAnimator.tick(it.deltaT)
                vehicle.throttleInput = throttleBrakeHandler.throttle.value
                vehicle.brakeInput = throttleBrakeHandler.brake.value
                updateDashboard()
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
            ctx.inputMgr.registerKeyListener('A', "steer left", filter = { true }, callback = steerLeft)
            ctx.inputMgr.registerKeyListener('D', "steer right", filter = { true }, callback = steerRight)
            ctx.inputMgr.registerKeyListener('W', "accelerate", filter = { true }, callback = accelerate)
            ctx.inputMgr.registerKeyListener('S', "brake", filter = { true }, callback = brake)
            ctx.inputMgr.registerKeyListener('R', "recover", filter = { it.isPressed }) {
                val pos = vehicle.position
                vehicle.position = Vec3f(pos.x, pos.y + 2f, pos.z)

                val head = vehicle.transform.transform(MutableVec3f(0f, 0f, 1f), 0f)
                val headDeg = atan2(head.x, head.z).toDeg()
                val ori = Mat3f().rotate(headDeg, Vec3f.Y_AXIS)
                vehicle.setRotation(ori)
                vehicle.linearVelocity = Vec3f.ZERO
                vehicle.angularVelocity = Vec3f.ZERO
            }

            world.registerHandlers(this@scene)
        }
    }

    override fun setupMenu(ctx: KoolContext): Scene {
        dashboard = VehicleUi(ctx)
        return dashboard!!.uiScene
    }

    private fun updateDashboard() {
        dashboard?.apply {
            speedKph = vehicle.forwardSpeed * 3.6f
            rpm = vehicle.engineSpeedRpm
            torqueNm = vehicle.engineTorqueNm
            powerKW = vehicle.enginePowerW / 1000f
            gear = vehicle.currentGear
            steering = -vehicle.steerInput
            throttle = vehicle.throttleInput
            brake = vehicle.brakeInput
            longitudinalAcceleration = vehicle.longitudinalAcceleration
            lateralAcceleration = vehicle.lateralAcceleration
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
            BoxGeometry(vehicleProps.chassisDims) to Mat4f(),
            // add additional shapes which act as collision dummys for wheel vs. drivable object collisions
            BoxGeometry(wheelBumperDims) to Mat4f().translate(0f, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelFrontZ),
            BoxGeometry(wheelBumperDims) to Mat4f().translate(0f, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelRearZ)
        )

        val pose = Mat4f().translate(0f, 3f, 0f)
        vehicle = Vehicle(vehicleProps, world, pose)
        world.addActor(vehicle)

        +group {
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
                transform.set(vehicle.transform)
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
                val boxShape = BoxGeometry(Vec3f(size))
                val body = RigidDynamic(250f)
                body.attachShape(Shape(boxShape, groundMaterial))
                body.setSimulationFilterData(obstacleSimFilterData)
                body.setQueryFilterData(obstacleQryFilterData)
                val pos = MutableVec3f(x + i * stepX, size * 0.5f + r * size, 0f)
                frame.transform(pos)
                body.position = pos
                world.addActor(body)

                val color = if (i % 2 == 0) Color.MD_ORANGE_300 else  Color.MD_ORANGE_100
                +body.toPrettyMesh(color.toLinear())
            }
        }
    }

    private fun Scene.makeRocker(frame: Mat4f, world: PhysicsWorld) {
        val anchor = RigidStatic().apply {
            setSimulationFilterData(obstacleSimFilterData)
            setQueryFilterData(obstacleQryFilterData)
            attachShape(Shape(BoxGeometry(Vec3f(7.5f, 1.5f, 0.3f)), groundMaterial))
            position = frame.transform(MutableVec3f(0f, 0.75f, 0f))
        }
        val rocker = RigidDynamic(500f).apply {
            setSimulationFilterData(obstacleSimFilterData)
            setQueryFilterData(obstacleQryFilterData)
            attachShape(Shape(BoxGeometry(Vec3f(7.5f, 0.15f, 15f)), groundMaterial))
            position = frame.transform(MutableVec3f(0f, 1.7f, 0f))
        }
        world.addActor(anchor)
        world.addActor(rocker)
        +anchor.toPrettyMesh(Color.MD_ORANGE.toLinear())
        +rocker.toPrettyMesh(Color.MD_ORANGE_100.toLinear())

        RevoluteJoint(anchor, rocker, Mat4f().translate(0f, 0.85f, 0f), Mat4f().translate(0f, 0f, 0.2f))
    }

    private fun MeshBuilder.makeRamp(frame: Mat4f) {
        color = Color.MD_ORANGE_100.toLinear()
        withTransform {
            transform.mul(frame)
            rotate(-11f, 0f, 0f)
            cube {
                size.set(10f, 2f, 10f)
                centered()
            }
        }
    }

    private fun MeshBuilder.makeBumps(frame: Mat4f) {
        for (i in 0 until 30) {
            val c = if (i % 2 == 0) Color.MD_ORANGE_300 else Color.MD_ORANGE_100
            for (s in -1 .. 1 step 2) {
                withTransform {
                    transform.mul(frame)
                    translate(2f * s, -0.3f, i * 3.1f + s * 0.4f)
                    rotate(90f, Vec3f.Z_AXIS)
                    translate(0f, -2f, 0f)
                    color = c.toLinear()
                    cylinder {
                        radius = 0.5f
                        height = 4f
                        steps = 32
                    }
                }
            }
        }
    }

    private fun MeshBuilder.makeHalfPipe(frame: Mat4f) {
        withTransform {
            transform.mul(frame)
            profile {
                val multiShape = multiShape {
                    simpleShape(false) {
                        xy(24f, 0f)
                        xy(24f, 10f)
                    }
                    simpleShape(false) {
                        xy(24f, 10f)
                        xy(20f, 10f)
                    }
                    simpleShape(false) {
                        xyArc(Vec2f(20f, 10f), Vec2f(10f, 10f), -90f, 20)
                    }
                }

                color = Color.MD_ORANGE_100.toLinear()
                sample()
                val inds = mutableListOf<Int>()
                inds += multiShape.shapes[0].sampledVertIndices
                inds += multiShape.shapes[1].sampledVertIndices
                inds += multiShape.shapes[2].sampledVertIndices
                fillPolygon(inds.reversed())

                sample(false)
                for (i in 0 until 5) {
                    translate(0f, 0f, 5f)
                    sample()
                }
                for (i in 0 until 50) {
                    rotate(180f / 50f, 0f, -1f, 0f)
                    sample()
                }
                for (i in 0 until 5) {
                    translate(0f, 0f, 5f)
                    sample()
                }
                sample(false)
                inds.clear()
                inds += multiShape.shapes[0].sampledVertIndices
                inds += multiShape.shapes[1].sampledVertIndices
                inds += multiShape.shapes[2].sampledVertIndices
                fillPolygon(inds)

                geometry.generateNormals()
            }
        }
    }

    private fun makeStaticCollisionBody(mesh: IndexedVertexList, world: PhysicsWorld) {
        val meshBody = RigidStatic().apply {
            setSimulationFilterData(obstacleSimFilterData)
            setQueryFilterData(obstacleQryFilterData)
            attachShape(Shape(TriangleMeshGeometry(mesh), groundMaterial))
        }
        world.addActor(meshBody)
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
                useAlbedoMap("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png", true)
                useNormalMap("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")
                useImageBasedLighting(ibl)
                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                albedo = Color.MD_ORANGE_100.mix(Color.WHITE, 0.5f).toLinear()
                shadowMaps += shadows
            }
        }
        +gndMesh

        ground = RigidStatic().apply {
            setSimulationFilterData(groundSimFilterData)
            setQueryFilterData(groundQryFilterData)
            attachShape(Shape(PlaneGeometry(), groundMaterial))
            setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
        }
        world.addActor(ground)
    }

    private fun RigidActor.toPrettyMesh(color: Color, rough: Float = 0.8f, metal: Float = 0f) = toMesh(color) {
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