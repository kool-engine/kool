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
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.deferred.deferredPbrShader
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfModel
import de.fabmax.kool.util.ibl.EnvironmentHelper

class VehicleDemo : DemoScene("Vehicle") {

    private lateinit var vehicleWorld: VehicleWorld
    private lateinit var vehicleModel: Model

    private var vehicle: DemoVehicle? = null
    private var dashboard: VehicleUi? = null
    private var track: Track? = null
    private var timer: TrackTimer? = null

    override fun setupMainScene(ctx: KoolContext) = scene {
        var createObjects = false

        lighting.singleLight {
//            setDirectional(Vec3f(0.5f, -1f, -0.5f))
//            setColor(Color.WHITE, 0.75f)
                setDirectional(Vec3f(-1f, -0.6f, -1f))
                setColor(Color.WHITE, 0.75f)
        }

        ctx.assetMgr.launch {
//            val ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/colorful_studio_1k.rgbe.png", this)
            val ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/syferfontein_0d_clear_1k.rgbe.png", this)
            val shadows = CascadedShadowMap(this@scene, 0, maxRange = 400f).apply {
                mapRanges[0].set(0f, 0.05f)
                mapRanges[1].set(0.05f, 0.2f)
                mapRanges[2].set(0.2f, 1f)
                cascades.forEach { it.directionalCamNearOffset = -80f }
            }
            +Skybox.cube(ibl.reflectionMap, 1f)
            Physics.awaitLoaded()

            val defCfg = DeferredPipelineConfig().apply {
                maxGlobalLights = 1
                isWithEmissive = true
                isWithAmbientOcclusion = true
                isWithScreenSpaceReflections = false

                useImageBasedLighting(ibl)
                useShadowMaps(emptyList())
                useShadowMaps(listOf(shadows))
            }
            val deferredPipeline = DeferredPipeline(this@scene, defCfg)
            deferredPipeline.aoPipeline?.mapSize = 0.75f
            deferredPipeline.pbrPass.sceneShader.ambientShadowFactor = 0.3f
            +deferredPipeline.renderOutput

            shadows.drawNode = deferredPipeline.contentGroup

            vehicleWorld = VehicleWorld(this@scene, PhysicsWorld(), deferredPipeline)
            vehicleWorld.physics.registerHandlers(this@scene)

            val modelCfg = GltfFile.ModelGenerateConfig(materialConfig = GltfFile.ModelMaterialConfig(isDeferredShading = true))
            vehicleModel = loadGltfModel("${Demo.modelBasePath}/kool-car.glb", modelCfg)!!

            createObjects = true
        }

        onRenderScene += {
            if (createObjects) {
                createObjects = false
                vehicleWorld.deferredPipeline.contentGroup.createWorldObjects(ctx)
            }
        }

        onDispose += {
            cleanUp(it)
        }
    }

    private fun Group.createWorldObjects(ctx: KoolContext) {
        val demoVehicle = DemoVehicle(vehicleWorld, vehicleModel, ctx)
        +demoVehicle.vehicleGroup
        vehicle = demoVehicle

        makeGround()
        makeTrack(vehicleWorld)
        Playground.makePlayground(vehicleWorld)

        vehicleWorld.scene += CamRig().apply {
            val cam = vehicleWorld.scene.camera as PerspectiveCamera
            cam.apply {
                clipNear = 1f
                clipFar = 1000f
            }
            trackedActor = demoVehicle.vehicle
            +OrbitInputTransform(vehicleWorld.scene).apply {
                setMouseRotation(0f, -10f)
                setMouseTranslation(0f, 1f, 0f)
                +cam
                zoom = 6.0
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
        track?.cleanUp()
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
                            t.reset(t.isReverse)
                        }
                    }
                }
            }
        }
    }

    private fun Group.makeTrack(world: VehicleWorld) {
        track = Track(world).generate {
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

            addGuardRailSection(170f, 240f, false)
            addGuardRailSection(620f, 670f, false)
            addGuardRailSection(735f, 785f, false)
            addGuardRailSection(800f, 850f, true)
            addGuardRailSection(1020f, 1070f, false)
            addGuardRailSection(1130f, 1180f, false)
            addGuardRailSection(1210f, 1260f, false)
            addGuardRailSection(1395f, 1445f, true)
            addGuardRailSection(1460f, 1510f, true)
        }
        +track!!

        timer = TrackTimer(vehicle!!.vehicle, track!!, world).apply {
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

    private fun Group.makeGround() {
        val groundAlbedo = Texture2d("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        val groundNormal = Texture2d("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")
        onDispose += {
            groundAlbedo.dispose()
            groundNormal.dispose()
        }

        val gndMesh = textureMesh(isNormalMapped = true, name = "ground") {
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
            shader = deferredPbrShader {
                useAlbedoMap(groundAlbedo, true)
                useNormalMap(groundNormal)
                albedo = color(100f)
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