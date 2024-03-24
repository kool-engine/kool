package de.fabmax.kool.demo.physics.ragdoll

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.input.UniversalKeyCode
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.physics.articulations.ArticulationDriveType
import de.fabmax.kool.physics.articulations.ArticulationJoint
import de.fabmax.kool.physics.articulations.ArticulationJointAxis
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.CylinderGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

class RagdollDemo : DemoScene("Ragdoll Demo") {

    private val rand = Random(1337)
    private lateinit var physicsWorld: PhysicsWorld
    private val physicsStepper = ConstantPhysicsStepperSync()//.apply { simTimeFactor = 0.1f }

    private val ibl by hdriImage("${DemoLoader.hdriPath}/colorful_studio_1k.rgbe.png")
    private val groundAlbedo by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
    private val groundNormal by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine_normal.png")

    private lateinit var ao: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()
    private val bodyInstanceData = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, ATTRIB_COLOR), 1500)
    private val bodyInstances = mutableListOf<BodyInstance>()

    private val ragdolls = mutableListOf<Articulation>()

    private val numRagdolls = mutableStateOf(50)
    private val physicsTimeTxt = mutableStateOf("0.00 ms")
    private val timeFactorTxt = mutableStateOf("1.00 x")

    private val bodyMaterialCfg: KslPbrShader.Config.Builder.() -> Unit = {
        color { vertexColor() }
        enableImageBasedLighting(ibl)
        shadow { addShadowMaps(shadows) }
        enableSsao(ao.aoMap)
        roughness(0.8f)
    }

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        ao = AoPipeline.createForward(mainScene).apply {
            mapSize = 0.7f
            radius = 0.5f
        }
        shadows += SimpleShadowMap(mainScene, mainScene.lighting.lights[0], mapSize = 4096).apply {
            setDefaultDepthOffset(true)
            shadowBounds = BoundingBoxF(Vec3f(-20f, 0f, -20f), Vec3f(20f, 10f, 20f))
        }
        mainScene += Skybox.cube(ibl.reflectionMap, 1.5f)

        physicsWorld = PhysicsWorld(mainScene)
        physicsWorld.simStepper = physicsStepper

        val gravKeyListener = KeyboardInput.addKeyListener(UniversalKeyCode(' '), "Change Gravity",  { true }) {
            if (it.isPressed) {
                physicsWorld.gravity = Vec3f(0f, 0.5f, 0f)
                physicsWorld.wakeUpAll()
            } else if (it.isReleased) {
                physicsWorld.gravity = Vec3f(0f, -9.81f, 0f)
                physicsWorld.wakeUpAll()
            }
        }

        val colBody = RigidStatic()
        for (i in 0 until 10) {
            val pose = MutableMat4f().translate(0f, 0.25f + i * 0.4f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
            colBody.attachShape(Shape(CylinderGeometry(0.7f, (10 - i) * 0.7f), localPose = pose))
        }
        physicsWorld.addActor(colBody)
        mainScene += colBody.toMesh(Color.LIGHT_GRAY.toLinear(), bodyMaterialCfg)

        val ground = RigidStatic()
        ground.attachShape(Shape(PlaneGeometry()))
        ground.setRotation(0f.deg, 0f.deg, 90f.deg)
        physicsWorld.addActor(ground)

        mainScene.apply {
            addTextureMesh(isNormalMapped = true) {
                isCastingShadow = false
                generate {
                    rotate((-90f).deg, Vec3f.X_AXIS)
                    rect {
                        size.set(100f, 100f)
                        generateTexCoords(20f)
                    }
                }
                shader = KslPbrShader {
                    color { textureColor(groundAlbedo) }
                    normalMapping { setNormalMap(groundNormal) }
                    shadow { addShadowMaps(shadows) }
                    imageBasedAmbientColor(ibl.irradianceMap)
                    enableSsao(ao.aoMap)
                    reflectionMap = ibl.reflectionMap
                }
            }
        }

        mainScene.onRelease {
            physicsWorld.release()
            KeyboardInput.removeKeyListener(gravKeyListener)
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        spawnDolls()

        defaultOrbitCamera().apply {
            setZoom(15.0, max = 50.0)
        }

        val forceHelper = ForceHelper()
        InputStack.defaultInputHandler.pointerListeners += forceHelper
        onRelease {
            InputStack.defaultInputHandler.pointerListeners -= forceHelper
        }

        addColorMesh(instances = bodyInstanceData) {
            isFrustumChecked = false
            generate {
                cube { }
            }
            shader = instancedBodyShader()
            onUpdate += {
                bodyInstanceData.clear()
                bodyInstanceData.addInstances(bodyInstances.size) { buf ->
                    for (i in bodyInstances.indices) {
                        bodyInstances[i].putInstanceData(buf)
                    }
                }
            }
        }

        addLineMesh {
            isCastingShadow = false
            shader = KslUnlitShader {
                pipeline { lineWidth = 3f }
                color { vertexColor() }
            }
            onUpdate += {
                clear()
                if (forceHelper.isActive) {
                    addLine(forceHelper.forceAppPosGlobal, forceHelper.forceDragPos, MdColor.PINK)
                }
            }
        }

        onUpdate += {
            physicsTimeTxt.set("${physicsStepper.perfCpuTime.toString(2)} ms")
            timeFactorTxt.set("${physicsStepper.perfTimeFactor.toString(2)} x")
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuSlider2("Number of ragdolls", numRagdolls.use().toFloat(), 1f, 100f, { "${it.roundToInt()}" }) {
            numRagdolls.set(it.roundToInt())
        }
        Button("Respawn") {
            modifier
                .alignX(AlignmentX.Center)
                .width(Grow.Std)
                .margin(horizontal = 16.dp, vertical = 24.dp)
                .onClick { spawnDolls() }
        }

        Text("Statistics") { sectionTitleStyle() }
        MenuRow {
            Text("Physics step CPU time") { labelStyle(Grow.Std) }
            Text(physicsTimeTxt.use()) { labelStyle() }
        }
        MenuRow {
            Text("Time factor") { labelStyle(Grow.Std) }
            Text(timeFactorTxt.use()) { labelStyle() }
        }

        Text("Controls") { sectionTitleStyle() }
        MenuRow {
            Text("[Space]") { labelStyle(Grow.Std) }
            Text("invert gravity") { labelStyle() }
        }
        MenuRow {
            Text("[Middle mouse drag]") { labelStyle(Grow.Std) }
            Text("grab ragdolls") { labelStyle() }
        }
    }

    private fun spawnDolls() {
        // remove existing dolls
        ragdolls.forEach {
            physicsWorld.removeArticulation(it)
            it.release()
        }
        ragdolls.clear()
        bodyInstances.clear()

        // spawn new dolls
        for (i in 0 until numRagdolls.value) {
            val h = 7f + 2.5f * (i / 8)
            val pose = MutableMat4f()
                .translate(rand.randomF(-4f, 4f), h, rand.randomF(-4f, 4f))
                .rotate(rand.randomF(-180f, 180f).deg, MutableVec3f(rand.randomF(), rand.randomF(), rand.randomF()).norm())

            ragdolls += makeRagdollYUp(pose)
        }
    }

    private fun makeRagdollYUp(pose: Mat4f): Articulation {
        val ragdoll = Articulation(false)
        //ragdoll.minPositionIterations = 8
        //ragdoll.minVelocityIterations = 2

        // create links / ragdoll bones

        val headSz = Vec3f(0.25f, 0.25f, 0.25f)
        val torsoSz = Vec3f(0.35f, 0.45f, 0.2f)
        val hipSz = Vec3f(0.35f, 0.1f, 0.2f)
        val upperArmSz = Vec3f(0.1f, 0.28f, 0.1f)
        val lowerArmSz = Vec3f(0.09f, 0.25f, 0.09f)
        val handSz = Vec3f(0.05f, 0.17f, 0.12f)
        val upperLegSz = Vec3f(0.15f, 0.4f, 0.15f)
        val lowerLegSz = Vec3f(0.13f, 0.35f, 0.13f)
        val footSz = Vec3f(0.13f, 0.075f, 0.25f)

        // center body
        val torso = ragdoll.createLink(null, pose)
        torso.attachShape(Shape(BoxGeometry(torsoSz)))
        torso.mass = 20f
        torso.updateInertiaFromShapesAndMass()

        val headPose = MutableMat4f().translate(0f, 0.4f, 0f)
        val head = ragdoll.createLink(torso, headPose)
        head.attachShape(Shape(BoxGeometry(headSz)))
        head.mass = 5f
        head.updateInertiaFromShapesAndMass()

        val hipPose = MutableMat4f().translate(0f, -0.3f, 0f)
        val hip = ragdoll.createLink(torso, hipPose)
        hip.attachShape(Shape(BoxGeometry(hipSz)))
        hip.mass = 10f
        hip.updateInertiaFromShapesAndMass()

        // arms
        val upperArmLtPose = MutableMat4f().translate(0.25f, 0.05f, 0f)
        val upperArmLt = ragdoll.createLink(torso, upperArmLtPose)
        upperArmLt.attachShape(Shape(BoxGeometry(upperArmSz)))
        upperArmLt.mass = 5f
        upperArmLt.updateInertiaFromShapesAndMass()

        val lowerArmLtPose = MutableMat4f().translate(0f, -0.3f, 0f)
        val lowerArmLt = ragdoll.createLink(upperArmLt, lowerArmLtPose)
        lowerArmLt.attachShape(Shape(BoxGeometry(lowerArmSz)))
        lowerArmLt.mass = 5f
        lowerArmLt.updateInertiaFromShapesAndMass()

        val handLtPose = MutableMat4f().translate(0f, -0.235f, 0f)
        val handLt = ragdoll.createLink(lowerArmLt, handLtPose)
        handLt.attachShape(Shape(BoxGeometry(handSz)))
        handLt.mass = 1f
        handLt.updateInertiaFromShapesAndMass()

        val upperArmRtPose = MutableMat4f().translate(-0.25f, 0.05f, 0f)
        val upperArmRt = ragdoll.createLink(torso, upperArmRtPose)
        upperArmRt.attachShape(Shape(BoxGeometry(upperArmSz)))
        upperArmRt.mass = 5f
        upperArmRt.updateInertiaFromShapesAndMass()

        val lowerArmRtPose = MutableMat4f().translate(0f, -0.3f, 0f)
        val lowerArmRt = ragdoll.createLink(upperArmRt, lowerArmRtPose)
        lowerArmRt.attachShape(Shape(BoxGeometry(lowerArmSz)))
        lowerArmRt.mass = 5f
        lowerArmRt.updateInertiaFromShapesAndMass()

        val handRtPose = MutableMat4f().translate(0f, -0.235f, 0f)
        val handRt = ragdoll.createLink(lowerArmRt, handRtPose)
        handRt.attachShape(Shape(BoxGeometry(handSz)))
        handRt.mass = 1f
        handRt.updateInertiaFromShapesAndMass()

        // legs
        val upperLegLtPose = MutableMat4f().translate(0.1f, -0.275f, 0f)
        val upperLegLt = ragdoll.createLink(hip, upperLegLtPose)
        upperLegLt.attachShape(Shape(BoxGeometry(upperLegSz)))
        upperLegLt.mass = 10f
        upperLegLt.updateInertiaFromShapesAndMass()

        val lowerLegLtPose = MutableMat4f().translate(0f, -0.4f, 0f)
        val lowerLegLt = ragdoll.createLink(upperLegLt, lowerLegLtPose)
        lowerLegLt.attachShape(Shape(BoxGeometry(lowerLegSz)))
        lowerLegLt.mass = 5f
        lowerLegLt.updateInertiaFromShapesAndMass()

        val footLtPos = MutableMat4f().translate(0f, -0.2375f, 0.05f)
        val footLt = ragdoll.createLink(lowerLegLt, footLtPos)
        footLt.attachShape(Shape(BoxGeometry(footSz)))
        footLt.mass = 1f
        footLt.updateInertiaFromShapesAndMass()

        val upperLegRtPose = MutableMat4f().translate(-0.1f, -0.275f, 0f)
        val upperLegRt = ragdoll.createLink(hip, upperLegRtPose)
        upperLegRt.attachShape(Shape(BoxGeometry(upperLegSz)))
        upperLegRt.mass = 10f
        upperLegRt.updateInertiaFromShapesAndMass()

        val lowerLegRtPose = MutableMat4f().translate(0f, -0.4f, 0f)
        val lowerLegRt = ragdoll.createLink(upperLegRt, lowerLegRtPose)
        lowerLegRt.attachShape(Shape(BoxGeometry(lowerLegSz)))
        lowerLegRt.mass = 5f
        lowerLegRt.updateInertiaFromShapesAndMass()

        val footRtPos = MutableMat4f().translate(0f, -0.2375f, 0.05f)
        val footRt = ragdoll.createLink(lowerLegRt, footRtPos)
        footRt.attachShape(Shape(BoxGeometry(footSz)))
        footRt.mass = 1f
        footRt.updateInertiaFromShapesAndMass()

        // configure joints

        // center body
        val torsoHeadLinkParent = MutableMat4f().translate(0f, 0.25f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        val torsoHeadLinkChild = MutableMat4f().translate(0f, -0.15f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        head.inboundJoint?.setup(torsoHeadLinkParent, torsoHeadLinkChild, 20f) {
            setupSphericalSymmetrical(90f, 45f)
        }

        val torsoHipLinkParent = MutableMat4f().translate(0f, -0.2375f, 0f).rotate(90f.deg, Vec3f.Z_AXIS).rotate(45f.deg, Vec3f.Y_AXIS)
        val torsoHipLinkChild = MutableMat4f().translate(0f, 0.0625f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        hip.inboundJoint?.setup(torsoHipLinkParent, torsoHipLinkChild, 20f) {
            setupSpherical(-60f, 0f, -50f, 50f, -25f, 25f)
            setDriveTarget(ArticulationJointAxis.ROT_TWIST, (-45f).toRad())
        }

        // arms
        val torsoArmLtLinkParent = MutableMat4f().translate(0.25f, 0.2f, 0f).rotate(180f.deg, Vec3f.Z_AXIS).rotate(45f.deg, Vec3f.Y_AXIS)
        val torsoArmLtLinkChild = MutableMat4f().translate(0f, 0.165f, 0f).rotate(90f.deg, Vec3f.Z_AXIS).rotate((-45f).deg, Vec3f.X_AXIS)
        upperArmLt.inboundJoint?.setup(torsoArmLtLinkParent, torsoArmLtLinkChild, 10f) {
            setupSpherical(-30f, 30f, -90f, 90f, -100f, 100f)
            setDriveTarget(ArticulationJointAxis.ROT_SWING1, (45f).toRad())
            setDriveTarget(ArticulationJointAxis.ROT_SWING2, (-45f).toRad())
        }

        val upperLowerArmLtLinkParent = MutableMat4f().translate(0f, -0.145f, 0f).rotate(90f.deg, Vec3f.Z_AXIS).rotate(70f.deg, Vec3f.Y_AXIS)
        val upperLowerArmLtLinkChild = MutableMat4f().translate(0f, 0.13f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        lowerArmLt.inboundJoint?.setup(upperLowerArmLtLinkParent, upperLowerArmLtLinkChild, 10f) {
            setupSpherical(-30f, 30f, -75f, 75f, -10f, 10f)
            setDriveTarget(ArticulationJointAxis.ROT_TWIST, (-70f).toRad())
        }

        val armHandLtLinkParent = MutableMat4f().translate(0f, -0.13f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        val armHandLtLinkChild = MutableMat4f().translate(0f, 0.0975f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        handLt.inboundJoint?.setup(armHandLtLinkParent, armHandLtLinkChild, 10f) {
            setupSphericalSymmetrical(45f, 70f)
        }

        val torsoArmRtLinkParent = MutableMat4f().translate(-0.25f, 0.2f, 0f).rotate(225f.deg, Vec3f.Y_AXIS)
        val torsoArmRtLinkChild = MutableMat4f().translate(0f, 0.165f, 0f).rotate((-90f).deg, Vec3f.Z_AXIS).rotate(135f.deg, Vec3f.X_AXIS)
        upperArmRt.inboundJoint?.setup(torsoArmRtLinkParent, torsoArmRtLinkChild, 10f) {
            setupSpherical(-30f, 30f, -90f, 90f, -100f, 100f)
            setDriveTarget(ArticulationJointAxis.ROT_SWING1, (-45f).toRad())
            setDriveTarget(ArticulationJointAxis.ROT_SWING2, (45f).toRad())
        }

        val upperLowerArmRtLinkParent = MutableMat4f().translate(0f, -0.13f, 0f).rotate(90f.deg, Vec3f.Z_AXIS).rotate(70f.deg, Vec3f.Y_AXIS)
        val upperLowerArmRtLinkChild = MutableMat4f().translate(0f, 0.145f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        lowerArmRt.inboundJoint?.setup(upperLowerArmRtLinkParent, upperLowerArmRtLinkChild, 10f) {
            setupSpherical(-30f, 30f, -75f, 75f, -10f, 10f)
            setDriveTarget(ArticulationJointAxis.ROT_TWIST, (-70f).toRad())
        }

        val armHandRtLinkParent = MutableMat4f().translate(0f, -0.13f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        val armHandRtLinkChild = MutableMat4f().translate(0f, 0.0975f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        handRt.inboundJoint?.setup(armHandRtLinkParent, armHandRtLinkChild, 10f) {
            setupSphericalSymmetrical(45f, 70f)
        }

        // legs
        val hipUpperLegLtLinkParent = MutableMat4f().translate(0.1f, -0.0625f, 0f).rotate(90f.deg, Vec3f.Z_AXIS).rotate(30f.deg, Vec3f.Y_AXIS).rotate(30f.deg, Vec3f.Z_AXIS)
        val hipUpperLegLtLinkChild = MutableMat4f().translate(0f, 0.2125f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        upperLegLt.inboundJoint?.setup(hipUpperLegLtLinkParent, hipUpperLegLtLinkChild, 20f) {
            setupSphericalSymmetrical(10f, 45f)
            setDriveTarget(ArticulationJointAxis.ROT_TWIST, (-15f).toRad())
            setDriveTarget(ArticulationJointAxis.ROT_SWING2, (-20f).toRad())
        }

        val upperLowerLegLtLinkParent = MutableMat4f().translate(0f, -0.2125f, 0f).rotate(90f.deg, Vec3f.Z_AXIS).rotate((-70f).deg, Vec3f.Y_AXIS)
        val upperLowerLegLtLinkChild = MutableMat4f().translate(0f, 0.1875f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        lowerLegLt.inboundJoint?.setup(upperLowerLegLtLinkParent, upperLowerLegLtLinkChild, 10f) {
            setupSpherical(-10f, 10f, -75f, 75f, -10f, 10f)
            setDriveTarget(ArticulationJointAxis.ROT_TWIST, (50f).toRad())
        }

        val legFootLtLinkParent = MutableMat4f().translate(0f, -0.1875f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        val legFootLtLinkChild = MutableMat4f().translate(0f, 0.05f, -0.05f).rotate(90f.deg, Vec3f.Z_AXIS).rotate(30f.deg, Vec3f.Y_AXIS)
        footLt.inboundJoint?.setup(legFootLtLinkParent, legFootLtLinkChild, 10f) {
            setupSpherical(-10f, 10f, -45f, 45f, -10f, 10f)
            setDriveTarget(ArticulationJointAxis.ROT_SWING1, (-30f).toRad())
        }

        val hipUpperLegRtLinkParent = MutableMat4f().translate(-0.1f, -0.0625f, 0f).rotate(90f.deg, Vec3f.Z_AXIS).rotate(30f.deg, Vec3f.Y_AXIS).rotate((-30f).deg, Vec3f.Z_AXIS)
        val hipUpperLegRtLinkChild = MutableMat4f().translate(0f, 0.2125f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        upperLegRt.inboundJoint?.setup(hipUpperLegRtLinkParent, hipUpperLegRtLinkChild, 20f) {
            setupSphericalSymmetrical(10f, 45f)
            setDriveTarget(ArticulationJointAxis.ROT_SWING1, (-15f).toRad())
            setDriveTarget(ArticulationJointAxis.ROT_SWING2, (20f).toRad())
        }

        val upperLowerLegRtLinkParent = MutableMat4f().translate(0f, -0.2125f, 0f).rotate(90f.deg, Vec3f.Z_AXIS).rotate((-70f).deg, Vec3f.Y_AXIS)
        val upperLowerLegRtLinkChild = MutableMat4f().translate(0f, 0.1875f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        lowerLegRt.inboundJoint?.setup(upperLowerLegRtLinkParent, upperLowerLegRtLinkChild, 10f) {
            setupSpherical(-10f, 10f, -75f, 75f, -10f, 10f)
            setDriveTarget(ArticulationJointAxis.ROT_TWIST, (50f).toRad())
        }

        val legFootRtLinkParent = MutableMat4f().translate(0f, -0.1875f, 0f).rotate(90f.deg, Vec3f.Z_AXIS)
        val legFootRtLinkChild = MutableMat4f().translate(0f, 0.05f, -0.05f).rotate(90f.deg, Vec3f.Z_AXIS).rotate(30f.deg, Vec3f.Y_AXIS)
        footRt.inboundJoint?.setup(legFootRtLinkParent, legFootRtLinkChild, 10f) {
            setupSpherical(-10f, 10f, -45f, 45f, -10f, 10f)
            setDriveTarget(ArticulationJointAxis.ROT_SWING1, (-30f).toRad())
        }

        bodyInstances += BodyInstance(torso.transform, torsoSz, MdColor.ORANGE.toLinear())
        bodyInstances += BodyInstance(head.transform, headSz, MdColor.DEEP_ORANGE.toLinear())
        bodyInstances += BodyInstance(hip.transform, hipSz, MdColor.AMBER.toLinear())

        bodyInstances += BodyInstance(upperArmLt.transform, upperArmSz, MdColor.CYAN.toLinear())
        bodyInstances += BodyInstance(lowerArmLt.transform, lowerArmSz, MdColor.CYAN toneLin 300)
        bodyInstances += BodyInstance(handLt.transform, handSz, MdColor.ORANGE toneLin 100)

        bodyInstances += BodyInstance(upperArmRt.transform, upperArmSz, MdColor.BLUE.toLinear())
        bodyInstances += BodyInstance(lowerArmRt.transform, lowerArmSz, MdColor.BLUE toneLin 300)
        bodyInstances += BodyInstance(handRt.transform, handSz, MdColor.ORANGE toneLin 100)

        bodyInstances += BodyInstance(upperLegLt.transform, upperLegSz, MdColor.LIME.toLinear())
        bodyInstances += BodyInstance(lowerLegLt.transform, lowerLegSz, MdColor.LIME toneLin 300)
        bodyInstances += BodyInstance(footLt.transform, footSz, MdColor.LIME toneLin 100)

        bodyInstances += BodyInstance(upperLegRt.transform, upperLegSz, MdColor.GREEN.toLinear())
        bodyInstances += BodyInstance(lowerLegRt.transform, lowerLegSz, MdColor.GREEN toneLin 300)
        bodyInstances += BodyInstance(footRt.transform, footSz, MdColor.GREEN toneLin 100)

        physicsWorld.addArticulation(ragdoll)

        return ragdoll
    }

    private fun ArticulationJoint.setup(parentPose: Mat4f, childPose: Mat4f, stiffness: Float, block: ArticulationJoint.() -> Unit) {
        setParentPose(parentPose)
        setChildPose(childPose)

        val s = stiffness * 1f

        setDriveParams(ArticulationJointAxis.ROT_SWING1, ArticulationDriveType.FORCE, s / 5f, s, s)
        setDriveParams(ArticulationJointAxis.ROT_SWING2, ArticulationDriveType.FORCE, s / 5f, s, s)
        setDriveParams(ArticulationJointAxis.ROT_TWIST, ArticulationDriveType.FORCE, s / 5f, s, s)
        block()
    }

    private fun instancedBodyShader() = KslPbrShader {
        vertices { isInstanced = true }
        color { instanceColor(ATTRIB_COLOR) }
        shadow { addShadowMaps(shadows) }
        enableSsao(ao.aoMap)
        roughness(0.8f)
        imageBasedAmbientColor(ibl.irradianceMap)
        reflectionMap = ibl.reflectionMap
    }

    private class BodyInstance(val trs: TrsTransformF, val size: Vec3f, color: Color) {
        private val mutColor = MutableColor(color)
        private val bufTransform = MutableMat4f()

        fun putInstanceData(buf: Float32Buffer) {
            bufTransform.set(trs.matrixF).scale(size)
            bufTransform.putTo(buf)
            mutColor.putTo(buf)
        }
    }

    private inner class ForceHelper : InputStack.PointerListener {
        val pickRay = RayF()
        val hitResult = HitResult()
        var hitActor: RigidBody? = null

        val initGlobalPos = MutableVec3f()

        val forceAppPosLocal = MutableVec3f()
        val forceAppPosGlobal = MutableVec3f()
        val forceDragPos = MutableVec3f()
        val force = MutableVec3f()
        val dragPlane = PlaneF()

        var isActive = false

        override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
            val dragPtr = pointerState.primaryPointer
            if (!dragPtr.isValid) { return }
            if (!(dragPtr.isMiddleButtonEvent || dragPtr.isMiddleButtonDown)) { return }

            mainScene.camera.computePickRay(pickRay, dragPtr, mainScene.mainRenderPass.viewport)
            when {
                dragPtr.isMiddleButtonPressed -> initDrag()
                dragPtr.isMiddleButtonDown -> applyForce()
                else -> isActive = false
            }
        }

        fun initDrag() {
            physicsWorld.raycast(pickRay, 100f, hitResult)
            hitActor = hitResult.nearestActor as? RigidBody
            hitActor?.let { actor ->
                initGlobalPos.set(hitResult.hitPosition)
                forceDragPos.set(hitResult.hitPosition)
                actor.toLocal(forceAppPosLocal.set(hitResult.hitPosition))
                dragPlane.p.set(hitResult.hitPosition)
                dragPlane.n.set(mainScene.camera.globalLookDir).mul(-1f)
            }
        }

        fun applyForce() {
            hitActor?.let { actor ->
                actor.toGlobal(forceAppPosGlobal.set(forceAppPosLocal))
                dragPlane.intersectionPoint(pickRay, forceDragPos)
                force.set(forceDragPos).subtract(forceAppPosGlobal).mul(500f)
                actor.addForceAtPos(force, forceAppPosGlobal)
                isActive = true
            }
        }
    }

    companion object {
        private val ATTRIB_COLOR = Attribute("aInstColor", GpuType.FLOAT4)
    }
}