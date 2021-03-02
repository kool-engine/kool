package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.physics.vehicle.ui.VehicleUi
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
import de.fabmax.kool.pipeline.*
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
    private val keyListeners = mutableListOf<InputManager.KeyEventListener>()

    private lateinit var physicsWorld: PhysicsWorld
    private lateinit var vehicle: Vehicle

    private val groundMaterial = Material(0.5f)
    private val groundSimFilterData = FilterData(COLLISION_FLAG_GROUND, COLLISION_FLAG_GROUND_AGAINST)
    private val groundQryFilterData = FilterData().apply { VehicleUtils.setupDrivableSurface(this) }
    private val obstacleSimFilterData = FilterData(COLLISION_FLAG_DRIVABLE_OBSTACLE, COLLISION_FLAG_DRIVABLE_OBSTACLE_AGAINST)
    private val obstacleQryFilterData = FilterData().apply { VehicleUtils.setupDrivableSurface(this) }

    private val vehicleGroup = Group()
    private lateinit var vehicleMesh: Mesh
    private var dashboard: VehicleUi? = null
    private var track: Track? = null
    private var timer: TrackTimer? = null

    override fun setupMainScene(ctx: KoolContext) = scene {
        var inited = false
        ctx.assetMgr.launch {
            lighting.singleLight {
                setDirectional(Vec3f(1f, -1.2f, -0.8f))
                setColor(Color.WHITE, 1f)
            }
            ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/colorful_studio_1k.rgbe.png", this)
            aoPipeline = AoPipeline.createForward(this@scene).apply { mapSize = 0.75f }
            shadows += CascadedShadowMap(this@scene, 0, maxRange = 400f).apply {
                mapRanges[0].set(0f, 0.05f)
                mapRanges[1].set(0.05f, 0.2f)
                mapRanges[2].set(0.2f, 1f)
                cascades.forEach { it.directionalCamNearOffset = -40f }
            }
            +Skybox.cube(ibl.reflectionMap, 1f)

            Physics.awaitLoaded()
            inited = true
        }

        onRenderScene += {
            if (inited) {
                inited = false

                physicsWorld = PhysicsWorld()
                val steerAnimator = ValueAnimator()
                val throttleBrakeHandler = ThrottleBrakeHandler()

                makeGround(physicsWorld)
                makeBoxes(Mat4f().translate(-20f, 0f, 30f), physicsWorld)
                makeRocker(Mat4f().translate(-20f, 0f, 90f), physicsWorld)

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

                    makeStaticCollisionBody(geometry, physicsWorld)
                }

                makeRaycastVehicle(physicsWorld)
                +vehicleGroup

                +CamRig().apply {
                    trackedNode = vehicleMesh
                    +orbitInputTransform {
                        setMouseRotation(0f, -10f)
                        setMouseTranslation(0f, 1.5f, 0f)
                        +camera
                        maxZoom = 500.0
                    }
                    physicsWorld.onFixedUpdate += {
                        updateTracking()
                    }
                }

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

                physicsWorld.registerHandlers(this@scene)

                makeTrack(physicsWorld)
            }
        }

        onDispose += {
            cleanUp(it)
        }
    }

    private fun cleanUp(ctx: KoolContext) {
        physicsWorld.clear()
        physicsWorld.release()
        groundMaterial.release()

        keyListeners.forEach { ctx.inputMgr.removeKeyListener(it) }
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

            val time = timer
            if (time != null) {
                trackTime = time.trackTime
            }
        }
    }

    private fun makeRaycastVehicle(world: PhysicsWorld) {
        val vehicleProps = VehicleProperties().apply {
            groundMaterialFrictions = mapOf(groundMaterial to 1.5f)
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
            Shape(BoxGeometry(wheelBumperDims), chassisBox.material,
                Mat4f().translate(0f, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelFrontZ),
                simFilterData = chassisBox.simFilterData, queryFilterData = chassisBox.queryFilterData),
            Shape(BoxGeometry(wheelBumperDims), chassisBox.material,
                Mat4f().translate(0f, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelRearZ),
                simFilterData = chassisBox.simFilterData, queryFilterData = chassisBox.queryFilterData)
        )

        val pose = Mat4f().translate(0f, 1.5f, -40f)
        vehicle = Vehicle(vehicleProps, world, pose)
        vehicle.setRotation(Mat3f().rotate(-90f, Vec3f.Y_AXIS))
        world.addActor(vehicle)

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
                            useImageBasedLighting(ibl)
                            useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                            shadowMaps += shadows
                        }
                    }
                }.also { +it }
            }

            vehicleMesh = colorMesh {
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
            +vehicleMesh

            onUpdate += {
                transform.set(vehicle.transform)
                setDirty()
                for (i in 0..3) {
                    wheelMeshes[i].transform.set(vehicle.wheelTransforms[i])
                    wheelMeshes[i].setDirty()
                }

                timer?.let { t ->
                    if (t.timerState != TrackTimer.TimerState.STOPPED) {
                        val distToTrack = track?.distanceToTrack(vehicle.position) ?: 0f
                        if (distToTrack > 15f) {
                            t.reset()
                        }
                    }
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

    private fun Scene.makeTrack(world: PhysicsWorld) {
        track = Track().generate {
            subdivs = 2
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(0f, 0.05f, -40f), Vec3f(-10f, 0f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-10f, 0.05f, -40f), Vec3f(-10f, 0f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-70f, 10f, -40f), Vec3f(-10f, 1f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-200f, 15f, 50f), Vec3f(0f, 0f, 80f)), 40)
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(0f, 15f, 200f), Vec3f(100f, 0f, 0f)), 40)
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(100f, 15f, 100f), Vec3f(0f, 0f, -40f)), 30)
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(50f, 20f, 0f), Vec3f(0f, 0f, -40f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(50f, 20f, -120f), Vec3f(0f, 0f, -40f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-20f, 20f, -200f), Vec3f(-40f, 0f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-85f, 30f, -150f), Vec3f(0f, 0f, 25f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-20f, 30f, -100f), Vec3f(40f, 0f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(80f, 25f, -100f), Vec3f(20f, -2f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(120f, 15f, -60f), Vec3f(0f, -2f, 20f)), 15)
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(80f, 10f, -20f), Vec3f(-20f, -2f, 0f)), 15)
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(0f, 0.05f, -40f), Vec3f(-20f, 0f, 0f)), 15)
        }
        +track!!

        makeStaticCollisionBody(track!!.trackMesh.geometry, world)

        val texProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, maxAnisotropy = 1)
        val rand = Random(1337)
        val gradient = ColorGradient(Color.MD_ORANGE_50, Color.MD_ORANGE_300)
        val sz = 128
        val colorData = createUint8Buffer(sz * sz * 4)
        val roughnessData = createUint8Buffer(sz * sz)

        var c = gradient.getColor(rand.randomF())
        var len = rand.randomI(2, 5)
        for (i in 0 until sz * sz) {
            if (--len == 0) {
                c = gradient.getColor(rand.randomF())
                len = rand.randomI(2, 5)
            }
            colorData[i * 4 + 0] = (c.r * 255f).toInt().toByte()
            colorData[i * 4 + 1] = (c.g * 255f).toInt().toByte()
            colorData[i * 4 + 2] = (c.b * 255f).toInt().toByte()
            colorData[i * 4 + 3] = (c.a * 255f).toInt().toByte()
            roughnessData[i] = ((1f - c.brightness + 0.2f).clamp(0f, 1f) * 255f).toInt().toByte()
        }

        val albedo = Texture2d(texProps) {
            TextureData2d(colorData, sz, sz, TexFormat.RGBA)
        }
        val roughness = Texture2d(texProps) {
            TextureData2d(roughnessData, sz, sz, TexFormat.R)
        }

        onDispose += {
            albedo.dispose()
            roughness.dispose()
        }

        track!!.trackMesh.shader = pbrShader {
            albedoSource = Albedo.TEXTURE_ALBEDO
            shadowMaps += shadows
            useImageBasedLighting(ibl)
            useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
            useAlbedoMap(albedo)
            useRoughnessMap(roughness)
        }

        timer = TrackTimer(vehicle, world, groundMaterial).apply {
            enterPos = Vec3f(-15f, 2.5f, -40f)
            enterSize = Vec3f(5f, 5f, 15f)

            exitPos = Vec3f(10f, 2.5f, -40f)
            exitSize = Vec3f(5f, 5f, 15f)

            checkPos1 = Vec3f(100f, 17.5f, 150f)
            checkSize1 = Vec3f(15f, 5f, 5f)

            checkPos2 = Vec3f(-85f, 32.5f, -150f)
            checkSize2 = Vec3f(15f, 5f, 5f)

            buildTriggers()
//            +enterTrigger.toPrettyMesh(Color.MD_GREEN.toLinear())
//            +exitTrigger.toPrettyMesh(Color.MD_RED.toLinear())
//            +checkTrigger1.toPrettyMesh(Color.MD_BLUE.toLinear())
//            +checkTrigger2.toPrettyMesh(Color.MD_BLUE.toLinear())

            onCheckPoint1 = { dashboard?.sec1Time = it }
            onCheckPoint2 = { dashboard?.sec2Time = it }
        }
    }

    private fun makeStaticCollisionBody(mesh: IndexedVertexList, world: PhysicsWorld) {
        val body = RigidStatic().apply {
            setSimulationFilterData(obstacleSimFilterData)
            setQueryFilterData(obstacleQryFilterData)
            attachShape(Shape(TriangleMeshGeometry(mesh), groundMaterial))
        }
        world.addActor(body)
    }

    private fun Scene.makeGround(world: PhysicsWorld) {
        val groundAlbedo = Texture2d("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        val groundNormal = Texture2d("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")
        onDispose += {
            groundAlbedo.dispose()
            groundNormal.dispose()
        }

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
                useAlbedoMap(groundAlbedo, true)
                useNormalMap(groundNormal)
                useImageBasedLighting(ibl)
                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                albedo = Color.MD_ORANGE_100.mix(Color.WHITE, 0.5f).toLinear()
                shadowMaps += shadows
            }
        }
        +gndMesh

        val ground = RigidStatic().apply {
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