package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.demo.physics.vehicle.ui.VehicleUi
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.toConfig
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.util.ActorTrackingCamRig
import de.fabmax.kool.pipeline.deferred2.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*

class VehicleDemo : DemoScene("Vehicle Demo") {

    private val ibl by hdriImage("${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png", brightness = 0.7f)
    private val groundAlbedo by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
    private val groundNormal by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine_normal.png")
    private val vehicleModel by model(
        "${DemoLoader.modelPath}/kool-car.glb",
        GltfLoadConfig(materialConfig = GltfMaterialConfig(
            shaderFactory = { pbrConfig ->
                gbufferShader {
                    vertexCfg.modelMatrixComposition = pbrConfig.vertexCfg.modelMatrixComposition
                    colorCfg.colorSources.addAll(pbrConfig.colorCfg.colorSources)
                    normalMapCfg.set(pbrConfig.normalMapCfg)
                    roughnessCfg.propertySources.addAll(pbrConfig.roughnessCfg.propertySources)
                    metallicCfg.propertySources.addAll(pbrConfig.metallicCfg.propertySources)
                    aoCfg.propertySources.addAll(pbrConfig.aoCfg.propertySources)
                }
            }
        ))
    )

    lateinit var vehicleWorld: VehicleWorld
    private lateinit var vehicle: DemoVehicle

    private var ui: VehicleUi? = null
    private var track: Track? = null
    var timer: TrackTimer? = null

    internal lateinit var deferredPipeline: Deferred2Pipeline

    override suspend fun loadResources(ctx: KoolContext) {
        val sceneCam = PerspectiveCamera()
        val sceneContent = Node()

        val shadows = CascadedShadowMap(sceneCam, sceneContent, mainScene.lighting.lights[0], maxRange = 400f, mapSizes = listOf(4096, 2048, 2048)).apply {
            mapRanges[0].set(0f, 0.03f)
            mapRanges[1].set(0.03f, 0.17f)
            mapRanges[2].set(0.17f, 1f)
            subMaps.forEachIndexed { i, map ->
                map.directionalCamNearOffset = -120f
                map.shaderDepthOffset = if (i == 0) -0.0004f else -0.002f
            }
        }
        shadows.addToScene(mainScene)

        showLoadText("Creating Deferred Render Pipeline".l())
        deferredPipeline = Deferred2Pipeline(
            content = sceneContent,
            camera = sceneCam,
            scene = mainScene,
            ibl = ibl,
            lighting = mainScene.lighting,
            shadowMapConfig = listOf(shadows).toConfig(),
            renderScale = 1f / UiScale.windowScale.value,
        )
        deferredPipeline.enableScreenSpaceReflections()
        deferredPipeline.lightingPass.ambientShadowFactor = 0.3f
        val bloom = deferredPipeline.installBloomPass()
        mainScene += deferredPipeline.defaultOutputQuad(bloom)
        shadows.drawNode = deferredPipeline.content

        val deferredLights = DeferredLights(deferredPipeline)

        showLoadText("Creating Physics World".l())
        val physics = PhysicsWorld(mainScene)
        vehicleWorld = VehicleWorld(mainScene, physics, deferredPipeline, deferredLights)

        vehicle = DemoVehicle(this@VehicleDemo, vehicleModel, ctx)
        showLoadText("Loading Vehicle Audio".l())
        vehicle.vehicleAudio.loadAudio()

        showLoadText("Creating Physics World".l())
        deferredPipeline.content.apply {
            addNode(vehicle.vehicleGroup)
            makeGround()
            showLoadText("Creating Playground".l())
            Playground.makePlayground(vehicleWorld)
            showLoadText("Creating Track".l())
            makeTrack(vehicleWorld)
            addNode(deferredLights)
//            onUpdate {
//                val w = (8f - vehicle.vehicle.linearVelocity.length()).coerceAtLeast(0f)
//                deferredPipeline.filterPass.filterWeight = w
//            }
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        (lighting.lights[0] as Light.Directional).apply {
            setup(Vec3f(-1f, -0.6f, -1f))
            setColor(Color.WHITE, 0.75f)
        }

        val camera = deferredPipeline.camera
        val camRig = ActorTrackingCamRig(vehicleWorld.physics, vehicle.vehicle).apply {
            camera.setupCamera(Vec3f(0f, 2.75f, 6f), lookAt = Vec3f(0f, 1.75f, 0f))
            addNode(camera)
        }
        deferredPipeline.content.addNode(camRig)

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
        ui = VehicleUi(mainScene, vehicle).apply {
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
                        val distToTrack = track?.distanceToTrack(veh.pose.position) ?: 0f
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
                vertexCustomizer = {
                    val pos = it.position.get(MutableVec3f())
                    it.texCoord.set(pos.x / 10f, pos.z / 10f)
                }
                grid {
                    sizeX = 5000f
                    sizeY = 5000f
                    stepsX = sizeX.toInt() / 100
                    stepsY = sizeY.toInt() / 100
                }
            }
            shader = gbufferShader {
                color {
                    textureColor(groundAlbedo)
                    constColor(color(100), blendMode = ColorBlockConfig.BlendMode.Multiply)
                }
                normalMapping {
                    useNormalMap(groundNormal)
                }
                roughness(0.35f)
            }
        }

        val ground = RigidStatic().apply {
            attachShape(Shape(PlaneGeometry(), Physics.defaultMaterial))
            setRotation(QuatF.rotation(90f.deg, Vec3f.Z_AXIS))
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