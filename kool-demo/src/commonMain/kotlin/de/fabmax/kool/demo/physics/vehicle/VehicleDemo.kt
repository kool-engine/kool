package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.physics.vehicle.ui.VehicleUi
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.SimpleSpline3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.util.ActorTrackingCamRig
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.AlbedoMapMode
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.deferred.deferredPbrShader
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfModel
import de.fabmax.kool.util.ibl.EnvironmentHelper

class VehicleDemo : DemoScene("Vehicle Demo") {

    private lateinit var vehicleWorld: VehicleWorld
    private lateinit var vehicleModel: Model
    private lateinit var vehicle: DemoVehicle

    private var dashboard: VehicleUi? = null
    private var track: Track? = null
    private var timer: TrackTimer? = null

    private lateinit var deferredPipeline: DeferredPipeline

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        showLoadText("Loading IBL maps")
        val ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.envMapBasePath}/syferfontein_0d_clear_1k.rgbe.png", this)
        val shadows = CascadedShadowMap(mainScene, 0, maxRange = 400f, mapSizes = listOf(4096, 2048, 2048)).apply {
            mapRanges[0].set(0f, 0.03f)
            mapRanges[1].set(0.03f, 0.17f)
            mapRanges[2].set(0.17f, 1f)
            cascades.forEachIndexed { i, map ->
                map.directionalCamNearOffset = -120f
                map.shaderDepthOffset = if (i == 0) -0.0004f else -0.002f
            }
        }

        showLoadText("Loading Vehicle Model")
        val modelCfg = GltfFile.ModelGenerateConfig(materialConfig = GltfFile.ModelMaterialConfig(isDeferredShading = true))
        vehicleModel = loadGltfModel("${Demo.modelBasePath}/kool-car.glb", modelCfg)!!

        showLoadText("Loading Physics")
        Physics.awaitLoaded()

        showLoadText("Creating Deferred Render Pipeline")
        val defCfg = DeferredPipelineConfig().apply {
            maxGlobalLights = 1
            isWithAmbientOcclusion = true
            isWithScreenSpaceReflections = false
            isWithBloom = true
            isWithVignette = true

            bloomKernelSize = 10
            bloomAvgDownSampling = false

            useImageBasedLighting(ibl)
            useShadowMaps(emptyList())
            useShadowMaps(listOf(shadows))

            // set output depth compare op to ALWAYS, so that the skybox with maximum depth value is drawn
            outputDepthTest = DepthCompareOp.ALWAYS
        }
        deferredPipeline = DeferredPipeline(mainScene, defCfg).apply {
            aoPipeline?.mapSize = 0.75f
            lightingPassShader.ambientShadowFactor(0.3f)
            bloomStrength = 0.25f
            bloomScale = 1f
            setBloomBrightnessThresholds(1f, 2f)

            lightingPassContent += Skybox.cube(ibl.reflectionMap, 1f, hdrOutput = true)
        }
        mainScene += deferredPipeline.createDefaultOutputQuad()

        shadows.drawNode = deferredPipeline.sceneContent

        showLoadText("Creating Physics World")
        val physics = PhysicsWorld()
        //physics.simStepper = ConstantPhysicsStepper()
        vehicleWorld = VehicleWorld(mainScene, physics, deferredPipeline)
        vehicleWorld.physics.registerHandlers(mainScene)

        vehicle = DemoVehicle(vehicleWorld, vehicleModel, ctx)
        showLoadText("Loading Vehicle Audio")
        vehicle.vehicleAudio.loadAudio(this)

        showLoadText("Creating Physics World")
        deferredPipeline.sceneContent.apply {
            +vehicle.vehicleGroup

            makeGround()
            showLoadText("Creating Playground")
            Playground.makePlayground(vehicleWorld)
            showLoadText("Creating Track")
            makeTrack(vehicleWorld)
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        mainRenderPass.clearColor = null

        lighting.singleLight {
            setDirectional(Vec3f(-1f, -0.6f, -1f))
            setColor(Color.WHITE, 0.75f)
        }

        +ActorTrackingCamRig().apply {
            trackedActor = vehicle.vehicle
            camera.setClipRange(1f, 1000f)
            camera.position.set(0f, 2f, 6f)
            camera.lookAt.set(0f, 1f, 0f)
            +camera
        }

        onUpdate += {
            updateDashboard()
        }
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        vehicleWorld.release()
        vehicle.cleanUp(ctx)
        track?.cleanUp()
    }

    override fun setupMenu(ctx: KoolContext): Scene {
        dashboard = VehicleUi(vehicle, ctx).apply {
            onToggleSound = { en -> vehicle.toggleSound(en) }
        }
        return dashboard!!.uiScene
    }

    private fun updateDashboard() {
        dashboard?.apply {
            vehicle.vehicle.let { vehicle ->
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
                    vehicle.vehicle.let { veh ->
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

        timer = TrackTimer(vehicle.vehicle, track!!, world).apply {
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
                useAlbedoMap(groundAlbedo, AlbedoMapMode.MULTIPLY_BY_UNIFORM)
                useNormalMap(groundNormal)
                albedo = color(100)
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
        fun color(c: Int, linear: Boolean = true): Color {
            val color = (MdColor.ORANGE tone c).mix(MdColor.GREY tone c, 0.25f)
            return if (linear) color.toLinear() else color
        }
    }
}