package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.demo.physics.vehicle.ui.VehicleUi
import de.fabmax.kool.math.MutableMat3f
import de.fabmax.kool.math.SimpleSpline3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.util.ActorTrackingCamRig
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.pipeline.deferred.DeferredPipelineConfig
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class VehicleDemo : DemoScene("Vehicle Demo") {

    private val ibl by hdriImage("${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png")
    private val groundAlbedo by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
    private val groundNormal by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine_normal.png")
    private val vehicleModel by model(
        "${DemoLoader.modelPath}/kool-car.glb",
        GltfLoadConfig(materialConfig = GltfMaterialConfig(isDeferredShading = true))
    )

    lateinit var vehicleWorld: VehicleWorld
    private lateinit var vehicle: DemoVehicle

    private var ui: VehicleUi? = null
    private var track: Track? = null
    var timer: TrackTimer? = null

    private lateinit var deferredPipeline: DeferredPipeline

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        val shadows = CascadedShadowMap(mainScene, mainScene.lighting.lights[0], maxRange = 400f, mapSizes = listOf(4096, 2048, 2048)).apply {
            mapRanges[0].set(0f, 0.03f)
            mapRanges[1].set(0.03f, 0.17f)
            mapRanges[2].set(0.17f, 1f)
            subMaps.forEachIndexed { i, map ->
                map.directionalCamNearOffset = -120f
                map.shaderDepthOffset = if (i == 0) -0.0004f else -0.002f
            }
        }
        showLoadText("Loading Physics")

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
            lightingPassShader.ambientShadowFactor = 0.3f
            bloomStrength = 0.25f
            bloomScale = 1f
            setBloomBrightnessThresholds(1f, 2f)

            lightingPassContent += Skybox.cube(ibl.reflectionMap, 1f, colorSpaceConversion = ColorSpaceConversion.AsIs)
        }
        mainScene += deferredPipeline.createDefaultOutputQuad()

        shadows.drawNode = deferredPipeline.sceneContent

        showLoadText("Creating Physics World")
        val physics = PhysicsWorld(mainScene)
        vehicleWorld = VehicleWorld(mainScene, physics, deferredPipeline)

        vehicle = DemoVehicle(this@VehicleDemo, vehicleModel, ctx)
        showLoadText("Loading Vehicle Audio")
        vehicle.vehicleAudio.loadAudio(this)

        showLoadText("Creating Physics World")
        deferredPipeline.sceneContent.apply {
            addNode(vehicle.vehicleGroup)

            makeGround()
            showLoadText("Creating Playground")
            Playground.makePlayground(vehicleWorld)
            showLoadText("Creating Track")
            makeTrack(vehicleWorld)
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        (lighting.lights[0] as Light.Directional).apply {
            setup(Vec3f(-1f, -0.6f, -1f))
            setColor(Color.WHITE, 0.75f)
        }

        val camRig = ActorTrackingCamRig().apply {
            trackedActor = vehicle.vehicle
            camera.setClipRange(1f, 1000f)
            camera.setupCamera(Vec3f(0f, 2.75f, 6f), lookAt = Vec3f(0f, 1.75f, 0f))
            addNode(camera)
        }
        addNode(camRig)

        onUpdate += {
            updateDashboard()
        }
    }

    override fun onRelease(ctx: KoolContext) {
        super.onRelease(ctx)
        vehicleWorld.release()
        vehicle.cleanUp()
        track?.cleanUp()
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext): UiSurface {
        ui = VehicleUi(vehicle).apply {
            onToggleSound = { en -> vehicle.toggleSound(en) }
        }
        return ui!!.uiSurface
    }

    private fun updateDashboard() {
        ui?.apply {
            vehicle.vehicle.let { vehicle ->
                dashboard.speedKph.set(vehicle.forwardSpeed * 3.6f)
                dashboard.rpm.set(vehicle.engineSpeedRpm)
                dashboard.torqueNm.set(vehicle.engineTorqueNm)
                dashboard.powerKW.set(vehicle.enginePowerW / 1000f)
                dashboard.gear.set(vehicle.currentGear)
                dashboard.steering.set(-vehicle.steerInput)
                dashboard.throttle.set(vehicle.throttleInput)
                dashboard.brake.set(vehicle.brakeInput)
                dashboard.longitudinalAcceleration.set(vehicle.longitudinalAcceleration)
                dashboard.lateralAcceleration.set(vehicle.lateralAcceleration)
            }

            timer?.let { t ->
                timerUi.trackTime.set(t.trackTime)
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

    private fun Node.makeTrack(world: VehicleWorld) {
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
        addNode(track!!)

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

            onCheckPoint1 = { ui?.timerUi?.sec1Time?.set(it) }
            onCheckPoint2 = { ui?.timerUi?.sec2Time?.set(it) }
        }
    }

    private fun Node.makeGround() {
        addTextureMesh(isNormalMapped = true, name = "ground") {
            generate {
                isCastingShadow = false
                vertexModFun = {
                    texCoord.set(x / 10f, z / 10f)
                }
                grid {
                    sizeX = 5000f
                    sizeY = 5000f
                    stepsX = sizeX.toInt() / 100
                    stepsY = sizeY.toInt() / 100
                }
            }
            shader = deferredKslPbrShader {
                color {
                    textureColor(groundAlbedo)
                    constColor(color(100), blendMode = ColorBlockConfig.BlendMode.Multiply)
                }
                normalMapping {
                    setNormalMap(groundNormal)
                }
            }
        }

        val ground = RigidStatic().apply {
            attachShape(Shape(PlaneGeometry(), Physics.defaultMaterial))
            setRotation(MutableMat3f().rotate(90f.deg, Vec3f.Z_AXIS))
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