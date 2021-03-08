package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.physics.vehicle.ui.VehicleUi
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.ibl.EnvironmentHelper

class VehicleDemo : DemoScene("Vehicle") {

    private lateinit var vehicleWorld: VehicleWorld

    private var vehicle: DemoVehicle? = null
    private var dashboard: VehicleUi? = null
    private var track: Track? = null
    private var timer: TrackTimer? = null


    override fun setupMainScene(ctx: KoolContext) = scene {
        var createObjects = false

        lighting.singleLight {
            setDirectional(Vec3f(0.5f, -1f, -0.5f))
            setColor(Color.WHITE, 0.75f)
//                setDirectional(Vec3f(-1f, -0.6f, -1f))
//                setColor(Color.WHITE, 1f)
        }

        ctx.assetMgr.launch {
            val ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/colorful_studio_1k.rgbe.png", this)
//            val ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/syferfontein_0d_clear_1k.rgbe.png", this)
            val aoPipeline = AoPipeline.createForward(this@scene).apply { mapSize = 0.75f }
            val shadows = CascadedShadowMap(this@scene, 0, maxRange = 400f).apply {
                mapRanges[0].set(0f, 0.05f)
                mapRanges[1].set(0.05f, 0.2f)
                mapRanges[2].set(0.2f, 1f)
                cascades.forEach { it.directionalCamNearOffset = -80f }
            }
            +Skybox.cube(ibl.reflectionMap, 1f)
            Physics.awaitLoaded()

            vehicleWorld = VehicleWorld(this@scene, PhysicsWorld(), ibl, listOf(shadows), aoPipeline.aoMap)
            vehicleWorld.physics.registerHandlers(this@scene)

            createObjects = true
        }

        onRenderScene += {
            if (createObjects) {
                createObjects = false
                createWorldObjects(ctx)
            }
        }

        onDispose += {
            cleanUp(it)
        }
    }

    private fun Scene.createWorldObjects(ctx: KoolContext) {
        val demoVehicle = DemoVehicle(vehicleWorld, ctx)
        +demoVehicle.vehicleGroup
        vehicle = demoVehicle

        makeGround()
        makeTrack(vehicleWorld)
        Playground.makePlayground(vehicleWorld)

        +CamRig().apply {
            (camera as PerspectiveCamera).apply {
                clipNear = 1f
                clipFar = 1000f
            }
            trackedNode = demoVehicle.vehicleMesh
            +orbitInputTransform {
                setMouseRotation(0f, -10f)
                setMouseTranslation(0f, 1.5f, 0f)
                +camera
                maxZoom = 500.0
            }
            vehicleWorld.physics.onFixedUpdate += {
                updateTracking()
            }
        }

        onUpdate += {
            updateDashboard()
        }
    }

    private fun cleanUp(ctx: KoolContext) {
        vehicleWorld.release()
        vehicle?.cleanUp(ctx)
    }

    override fun setupMenu(ctx: KoolContext): Scene {
        dashboard = VehicleUi(ctx).apply {
            onToggleSound = { en -> vehicle?.toggleSound(en) }
        }
        return dashboard!!.uiScene
    }

    private fun updateDashboard() {
        dashboard?.apply {
            vehicle?.vehicle?.let { vehicle ->
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

            timer?.let { t ->
                trackTime = t.trackTime
                if (t.timerState != TrackTimer.TimerState.STOPPED) {
                    vehicle?.vehicle?.let { veh ->
                        val distToTrack = track?.distanceToTrack(veh.position) ?: 0f
                        if (distToTrack > 15f) {
                            t.reset()
                        }
                    }
                }
            }
        }
    }

    private fun Scene.makeTrack(world: VehicleWorld) {
        track = Track().generate {
            subdivs = 2
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(0f, 0.05f, -40f), Vec3f(-3f, 0f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-10f, 0.05f, -40f), Vec3f(-8f, 0f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-70f, 10f, -40f), Vec3f(-10f, 2f, 0f)))
            addControlPoint(SimpleSpline3f.CtrlPoint(Vec3f(-200f, 25f, 50f), Vec3f(0f, 0f, 80f)), 40)
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

        val texProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, maxAnisotropy = 1)
        val rand = Random(1337)
        val gradient = ColorGradient(color(50f, false), color(300f, false))
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

        val albedoMap = Texture2d(texProps) {
            TextureData2d(colorData, sz, sz, TexFormat.RGBA)
        }
        val roughnessMap = Texture2d(texProps) {
            TextureData2d(roughnessData, sz, sz, TexFormat.R)
        }

        onDispose += {
            albedoMap.dispose()
            roughnessMap.dispose()
        }

        track!!.apply {
            val collisionMesh = IndexedVertexList(Attribute.POSITIONS)
            collisionMesh.addGeometry(trackMesh.geometry)
            collisionMesh.addGeometry(trackSupportMesh.geometry)
            world.addStaticCollisionBody(collisionMesh)

            trackMesh.shader = pbrShader {
                albedoSource = Albedo.TEXTURE_ALBEDO
                shadowMaps += world.shadows
                useImageBasedLighting(world.envMaps)
                useScreenSpaceAmbientOcclusion(world.aoMap)
                useAlbedoMap(albedoMap)
                useRoughnessMap(roughnessMap)
            }

            trackSupportMesh.shader = makeSupportMeshShader(world.shadows, world.envMaps, world.aoMap)
        }

        timer = TrackTimer(vehicle!!.vehicle, world.physics, world.defaultMaterial).apply {
            enterPos = Vec3f(-15f, 2.5f, -40f)
            enterSize = Vec3f(5f, 5f, 15f)

            exitPos = Vec3f(10f, 2.5f, -40f)
            exitSize = Vec3f(5f, 5f, 15f)

            checkPos1 = Vec3f(100f, 17.5f, 150f)
            checkSize1 = Vec3f(15f, 5f, 5f)

            checkPos2 = Vec3f(-85f, 32.5f, -150f)
            checkSize2 = Vec3f(15f, 5f, 5f)

            buildTriggers()

            onCheckPoint1 = { dashboard?.sec1Time = it }
            onCheckPoint2 = { dashboard?.sec2Time = it }
        }
    }

    private fun Scene.makeGround() {
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
                useImageBasedLighting(vehicleWorld.envMaps)
                useScreenSpaceAmbientOcclusion(vehicleWorld.aoMap)
                albedo = color(100f)
                shadowMaps += vehicleWorld.shadows
            }
        }
        +gndMesh

        val ground = RigidStatic().apply {
            setSimulationFilterData(vehicleWorld.groundSimFilterData)
            setQueryFilterData(vehicleWorld.groundQryFilterData)
            attachShape(Shape(PlaneGeometry(), vehicleWorld.defaultMaterial))
            setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
        }
        vehicleWorld.physics.addActor(ground)
    }

    companion object {
        fun color(c: Float, linear: Boolean = true): Color {
            val color = ColorGradient.MD_ORANGE.getColor(c, 0f, 900f)
                .mix(ColorGradient.MD_GREY.getColor(c, 0f, 900f), 0.25f)
            return if (linear) color.toLinear() else color
        }
    }
}