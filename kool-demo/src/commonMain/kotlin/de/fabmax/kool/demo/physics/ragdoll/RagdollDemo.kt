package de.fabmax.kool.demo.physics.ragdoll

import de.fabmax.kool.*
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.articulations.Articulation
import de.fabmax.kool.physics.articulations.ArticulationJoint
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.CylinderGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.shadermodel.PbrMaterialNode
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.pipeline.shading.unlitShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.roundToInt

class RagdollDemo : DemoScene("Ragdoll Demo") {

    private val rand = Random(1337)
    private lateinit var physicsWorld: PhysicsWorld
    private val physicsStepper = SimplePhysicsStepper()

    private lateinit var ibl: EnvironmentMaps
    private lateinit var ao: AoPipeline
    private val shadows = mutableListOf<ShadowMap>()
    private val bodyInstanceData = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, ATTRIB_COLOR), 1500)
    private val bodyInstances = mutableListOf<BodyInstance>()

    private val ragdolls = mutableListOf<Articulation>()

    private val numRagdolls = mutableStateOf(50)
    private val physicsTimeTxt = mutableStateOf("0.00 ms")
    private val timeFactorTxt = mutableStateOf("1.00 x")

    private val bodyMaterialCfg: PbrMaterialConfig.() -> Unit = {
        shadowMaps += shadows
        useImageBasedLighting(ibl)
        useScreenSpaceAmbientOcclusion(ao.aoMap)
        roughness = 0.8f
    }

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        ao = AoPipeline.createForward(mainScene).apply {
            mapSize = 0.7f
            radius = 0.5f
        }
        shadows += SimpleShadowMap(mainScene, 0, 4096).apply {
            setDefaultDepthOffset(true)
            shadowBounds = BoundingBox(Vec3f(-20f, 0f, -20f), Vec3f(20f, 10f, 20f))
        }
        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${DemoLoader.hdriPath}/colorful_studio_1k.rgbe.png", this)
        mainScene += Skybox.cube(ibl.reflectionMap, 1f)

        Physics.awaitLoaded()

        physicsWorld = PhysicsWorld()
        physicsWorld.simStepper = physicsStepper
        physicsWorld.registerHandlers(mainScene)

        val gravKeyListener = ctx.inputMgr.registerKeyListener(UniversalKeyCode(' '), "Change Gravity",  { true }) {
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
            val pose = Mat4f().translate(0f, 0.25f + i * 0.4f, 0f).rotate(90f, Vec3f.Z_AXIS)
            colBody.attachShape(Shape(CylinderGeometry(0.5f, (10 - i) * 0.5f), localPose = pose))
        }
        physicsWorld.addActor(colBody)
        mainScene += colBody.toMesh(Color.LIGHT_GRAY.toLinear(), bodyMaterialCfg)

        val ground = RigidStatic()
        ground.attachShape(Shape(PlaneGeometry()))
        ground.setRotation(0f, 0f, 90f)
        physicsWorld.addActor(ground)

        val groundAlbedo = loadAndPrepareTexture("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
        val groundNormal = loadAndPrepareTexture("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine_normal.png")

        mainScene += textureMesh(isNormalMapped = true) {
            isCastingShadow = false
            generate {
                rotate(-90f, Vec3f.X_AXIS)
                rect {
                    size.set(100f, 100f)
                    origin.set(size.x, size.y, 0f).scale(-0.5f)
                    generateTexCoords(20f)
                }
            }
            shader = pbrShader {
                shadowMaps += shadows
                useImageBasedLighting(ibl)
                useAlbedoMap(groundAlbedo)
                useNormalMap(groundNormal)
                useScreenSpaceAmbientOcclusion(ao.aoMap)
            }
        }

        mainScene.onDispose += {
            groundAlbedo.dispose()
            groundNormal.dispose()
            physicsWorld.release()
            ctx.inputMgr.removeKeyListener(gravKeyListener)
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        spawnDolls()

        defaultCamTransform().apply {
            setZoom(15.0, max = 50.0)
        }

        val forceHelper = ForceHelper(ctx)
        InputStack.defaultInputHandler.pointerListeners += forceHelper::handleDrag
        onDispose += {
            InputStack.defaultInputHandler.pointerListeners -= forceHelper::handleDrag
        }

        +colorMesh {
            isFrustumChecked = false
            instances = bodyInstanceData
            generate {
                cube {
                    centered()
                }
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

        +lineMesh {
            isCastingShadow = false
            shader = unlitShader { lineWidth = 3f }
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
            val h = 7f + 2.5f * (i / 10)
            val pose = Mat4f()
                .translate(rand.randomF(-3f, 3f), h, rand.randomF(-3f, 3f))
                .rotate(rand.randomF(-180f, 180f), MutableVec3f(rand.randomF(), rand.randomF(), rand.randomF()).norm())

            ragdolls += makeRagdollYUp(pose)
        }
    }

    private fun makeRagdollYUp(pose: Mat4f): Articulation {
        val ragdoll = Articulation()

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

        val headPose = Mat4f().set(pose).translate(0f, 0.4f, 0f)
        val head = ragdoll.createLink(torso, headPose)
        head.attachShape(Shape(BoxGeometry(headSz)))
        head.mass = 5f
        head.updateInertiaFromShapesAndMass()

        val hipPose = Mat4f().set(pose).translate(0f, -0.3f, 0f)
        val hip = ragdoll.createLink(torso, hipPose)
        hip.attachShape(Shape(BoxGeometry(hipSz)))
        hip.mass = 10f
        hip.updateInertiaFromShapesAndMass()

        // arms
        val upperArmLtPose = Mat4f().set(pose).translate(0.25f, 0.05f, 0f)
        val upperArmLt = ragdoll.createLink(torso, upperArmLtPose)
        upperArmLt.attachShape(Shape(BoxGeometry(upperArmSz)))
        upperArmLt.mass = 5f
        upperArmLt.updateInertiaFromShapesAndMass()

        val lowerArmLtPose = Mat4f().set(pose).translate(0.25f, -0.25f, 0f)
        val lowerArmLt = ragdoll.createLink(upperArmLt, lowerArmLtPose)
        lowerArmLt.attachShape(Shape(BoxGeometry(lowerArmSz)))
        lowerArmLt.mass = 5f
        lowerArmLt.updateInertiaFromShapesAndMass()

        val handLtPose = Mat4f().set(pose).translate(0.25f, -0.485f, 0f)
        val handLt = ragdoll.createLink(lowerArmLt, handLtPose)
        handLt.attachShape(Shape(BoxGeometry(handSz)))
        handLt.mass = 1f
        handLt.updateInertiaFromShapesAndMass()

        val upperArmRtPose = Mat4f().set(pose).translate(-0.25f, 0.05f, 0f)
        val upperArmRt = ragdoll.createLink(torso, upperArmRtPose)
        upperArmRt.attachShape(Shape(BoxGeometry(upperArmSz)))
        upperArmRt.mass = 5f
        upperArmRt.updateInertiaFromShapesAndMass()

        val lowerArmRtPose = Mat4f().set(pose).translate(-0.25f, -0.25f, 0f)
        val lowerArmRt = ragdoll.createLink(upperArmRt, lowerArmRtPose)
        lowerArmRt.attachShape(Shape(BoxGeometry(lowerArmSz)))
        lowerArmRt.mass = 5f
        lowerArmRt.updateInertiaFromShapesAndMass()

        val handRtPose = Mat4f().set(pose).translate(-0.25f, -0.485f, 0f)
        val handRt = ragdoll.createLink(lowerArmRt, handRtPose)
        handRt.attachShape(Shape(BoxGeometry(handSz)))
        handRt.mass = 1f
        handRt.updateInertiaFromShapesAndMass()

        // legs
        val upperLegLtPose = Mat4f().set(pose).translate(0.1f, -0.575f, 0f)
        val upperLegLt = ragdoll.createLink(hip, upperLegLtPose)
        upperLegLt.attachShape(Shape(BoxGeometry(upperLegSz)))
        upperLegLt.mass = 10f
        upperLegLt.updateInertiaFromShapesAndMass()

        val lowerLegLtPose = Mat4f().set(pose).translate(0.1f, -0.975f, 0f)
        val lowerLegLt = ragdoll.createLink(upperLegLt, lowerLegLtPose)
        lowerLegLt.attachShape(Shape(BoxGeometry(lowerLegSz)))
        lowerLegLt.mass = 5f
        lowerLegLt.updateInertiaFromShapesAndMass()

        val footLtPos = Mat4f().set(pose).translate(0.1f, -1.2125f, 0.05f)
        val footLt = ragdoll.createLink(lowerLegLt, footLtPos)
        footLt.attachShape(Shape(BoxGeometry(footSz)))
        footLt.mass = 1f
        footLt.updateInertiaFromShapesAndMass()

        val upperLegRtPose = Mat4f().set(pose).translate(-0.1f, -0.575f, 0f)
        val upperLegRt = ragdoll.createLink(hip, upperLegRtPose)
        upperLegRt.attachShape(Shape(BoxGeometry(upperLegSz)))
        upperLegRt.mass = 10f
        upperLegRt.updateInertiaFromShapesAndMass()

        val lowerLegRtPose = Mat4f().set(pose).translate(-0.1f, -0.975f, 0f)
        val lowerLegRt = ragdoll.createLink(upperLegRt, lowerLegRtPose)
        lowerLegRt.attachShape(Shape(BoxGeometry(lowerLegSz)))
        lowerLegRt.mass = 5f
        lowerLegRt.updateInertiaFromShapesAndMass()

        val footRtPos = Mat4f().set(pose).translate(-0.1f, -1.2125f, 0.05f)
        val footRt = ragdoll.createLink(lowerLegRt, footRtPos)
        footRt.attachShape(Shape(BoxGeometry(footSz)))
        footRt.mass = 1f
        footRt.updateInertiaFromShapesAndMass()

        // configure joints

        // center body
        val torsoHeadLinkParent = Mat4f().translate(0f, 0.25f, 0f).rotate(90f, Vec3f.Z_AXIS)
        val torsoHeadLinkChild = Mat4f().translate(0f, -0.15f, 0f).rotate(90f, Vec3f.Z_AXIS)
        head.inboundJoint.setup(torsoHeadLinkParent, torsoHeadLinkChild, 100f) {
            setSwingLimit(45f, 45f)
            setTwistLimit(-90f, 90f)
        }

        val torsoHipLinkParent = Mat4f().translate(0f, -0.2375f, 0f).rotate(90f, Vec3f.Z_AXIS).rotate(45f, Vec3f.Y_AXIS)
        val torsoHipLinkChild = Mat4f().translate(0f, 0.0625f, 0f).rotate(90f, Vec3f.Z_AXIS)
        hip.inboundJoint.setup(torsoHipLinkParent, torsoHipLinkChild, 100f) {
            setSwingLimit(25f, 50f)
            setTwistLimit(-30f, 30f)
            setTargetOrientation(0f, -45f, 0f)
        }

        // arms
        val torsoArmLtLinkParent = Mat4f().translate(0.25f, 0.2f, 0f).rotate(180f, Vec3f.Z_AXIS).rotate(45f, Vec3f.Y_AXIS)
        val torsoArmLtLinkChild = Mat4f().translate(0f, 0.165f, 0f).rotate(90f, Vec3f.Z_AXIS).rotate(-45f, Vec3f.X_AXIS)
        upperArmLt.inboundJoint.setup(torsoArmLtLinkParent, torsoArmLtLinkChild, 25f) {
            setSwingLimit(100f, 90f)
            setTwistLimit(-30f, 30f)
            setTargetOrientation(0f, 0f, -90f)
        }

        val upperLowerArmLtLinkParent = Mat4f().translate(0f, -0.145f, 0f).rotate(90f, Vec3f.Z_AXIS).rotate(70f, Vec3f.Y_AXIS)
        val upperLowerArmLtLinkChild = Mat4f().translate(0f, 0.13f, 0f).rotate(90f, Vec3f.Z_AXIS)
        lowerArmLt.inboundJoint.setup(upperLowerArmLtLinkParent, upperLowerArmLtLinkChild, 25f) {
            setSwingLimit(10f, 75f)
            setTwistLimit(-30f, 30f)
            setTargetOrientation(0f, -70f, 0f)
        }

        val armHandLtLinkParent = Mat4f().translate(0f, -0.13f, 0f).rotate(90f, Vec3f.Z_AXIS)
        val armHandLtLinkChild = Mat4f().translate(0f, 0.0975f, 0f).rotate(90f, Vec3f.Z_AXIS)
        handLt.inboundJoint.setup(armHandLtLinkParent, armHandLtLinkChild, 100f) {
            setSwingLimit(70f, 70f)
            setTwistLimit(-45f, 45f)
        }

        val torsoArmRtLinkParent = Mat4f().translate(-0.25f, 0.2f, 0f).rotate(225f, Vec3f.Y_AXIS)
        val torsoArmRtLinkChild = Mat4f().translate(0f, 0.165f, 0f).rotate(-90f, Vec3f.Z_AXIS).rotate(135f, Vec3f.X_AXIS)
        upperArmRt.inboundJoint.setup(torsoArmRtLinkParent, torsoArmRtLinkChild, 25f) {
            setSwingLimit(100f, 90f)
            setTwistLimit(-30f, 30f)
            setTargetOrientation(0f, 0f, -90f)
        }

        val upperLowerArmRtLinkParent = Mat4f().translate(0f, -0.13f, 0f).rotate(90f, Vec3f.Z_AXIS).rotate(70f, Vec3f.Y_AXIS)
        val upperLowerArmRtLinkChild = Mat4f().translate(0f, 0.145f, 0f).rotate(90f, Vec3f.Z_AXIS)
        lowerArmRt.inboundJoint.setup(upperLowerArmRtLinkParent, upperLowerArmRtLinkChild, 25f) {
            setSwingLimit(10f, 75f)
            setTwistLimit(-30f, 30f)
            setTargetOrientation(0f, -70f, 0f)
        }

        val armHandRtLinkParent = Mat4f().translate(0f, -0.13f, 0f).rotate(90f, Vec3f.Z_AXIS)
        val armHandRtLinkChild = Mat4f().translate(0f, 0.0975f, 0f).rotate(90f, Vec3f.Z_AXIS)
        handRt.inboundJoint.setup(armHandRtLinkParent, armHandRtLinkChild, 100f) {
            setSwingLimit(70f, 70f)
            setTwistLimit(-45f, 45f)
        }

        // legs
        val hipUpperLegLtLinkParent = Mat4f().translate(0.1f, -0.0625f, 0f).rotate(90f, Vec3f.Z_AXIS).rotate(30f, Vec3f.Y_AXIS).rotate(30f, Vec3f.Z_AXIS)
        val hipUpperLegLtLinkChild = Mat4f().translate(0f, 0.2125f, 0f).rotate(90f, Vec3f.Z_AXIS)
        upperLegLt.inboundJoint.setup(hipUpperLegLtLinkParent, hipUpperLegLtLinkChild, 25f) {
            setSwingLimit(45f, 45f)
            setTwistLimit(-10f, 10f)
            setTargetOrientation(Mat3f().rotate(-30f, Vec3f.Z_AXIS).rotate(-30f, Vec3f.Y_AXIS))
        }

        val upperLowerLegLtLinkParent = Mat4f().translate(0f, -0.2125f, 0f).rotate(90f, Vec3f.Z_AXIS).rotate(-70f, Vec3f.Y_AXIS)
        val upperLowerLegLtLinkChild = Mat4f().translate(0f, 0.1875f, 0f).rotate(90f, Vec3f.Z_AXIS)
        lowerLegLt.inboundJoint.setup(upperLowerLegLtLinkParent, upperLowerLegLtLinkChild, 10f) {
            setSwingLimit(10f, 75f)
            setTwistLimit(-10f, 10f)
            setTargetOrientation(0f, 70f, 0f)
        }

        val legFootLtLinkParent = Mat4f().translate(0f, -0.1875f, 0f).rotate(90f, Vec3f.Z_AXIS)
        val legFootLtLinkChild = Mat4f().translate(0f, 0.05f, -0.05f).rotate(90f, Vec3f.Z_AXIS).rotate(30f, Vec3f.Y_AXIS)
        footLt.inboundJoint.setup(legFootLtLinkParent, legFootLtLinkChild, 100f) {
            setSwingLimit(15f, 45f)
            setTwistLimit(-10f, 10f)
            setTargetOrientation(0f, -30f, 0f)
        }

        val hipUpperLegRtLinkParent = Mat4f().translate(-0.1f, -0.0625f, 0f).rotate(90f, Vec3f.Z_AXIS).rotate(30f, Vec3f.Y_AXIS).rotate(-30f, Vec3f.Z_AXIS)
        val hipUpperLegRtLinkChild = Mat4f().translate(0f, 0.2125f, 0f).rotate(90f, Vec3f.Z_AXIS)
        upperLegRt.inboundJoint.setup(hipUpperLegRtLinkParent, hipUpperLegRtLinkChild, 25f) {
            setSwingLimit(45f, 45f)
            setTwistLimit(-10f, 10f)
            setTargetOrientation(Mat3f().rotate(30f, Vec3f.Z_AXIS).rotate(-30f, Vec3f.Y_AXIS))
        }

        val upperLowerLegRtLinkParent = Mat4f().translate(0f, -0.2125f, 0f).rotate(90f, Vec3f.Z_AXIS).rotate(-70f, Vec3f.Y_AXIS)
        val upperLowerLegRtLinkChild = Mat4f().translate(0f, 0.1875f, 0f).rotate(90f, Vec3f.Z_AXIS)
        lowerLegRt.inboundJoint.setup(upperLowerLegRtLinkParent, upperLowerLegRtLinkChild, 10f) {
            setSwingLimit(10f, 75f)
            setTwistLimit(-10f, 10f)
            setTargetOrientation(0f, 70f, 0f)
        }

        val legFootRtLinkParent = Mat4f().translate(0f, -0.1875f, 0f).rotate(90f, Vec3f.Z_AXIS)
        val legFootRtLinkChild = Mat4f().translate(0f, 0.05f, -0.05f).rotate(90f, Vec3f.Z_AXIS).rotate(30f, Vec3f.Y_AXIS)
        footRt.inboundJoint.setup(legFootRtLinkParent, legFootRtLinkChild, 100f) {
            setSwingLimit(15f, 45f)
            setTwistLimit(-10f, 10f)
            setTargetOrientation(0f, -30f, 0f)
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

    private fun ArticulationJoint?.setup(parentPose: Mat4f, childPose: Mat4f, stiffness: Float, block: ArticulationJoint.() -> Unit) {
        this?.let {
            it.setParentPose(parentPose)
            it.setChildPose(childPose)
            it.damping = stiffness / 5f
            it.stiffness = stiffness
            it.tangentialDamping = 5f
            it.tangentialStiffness = 5f
            it.isSwingLimitEnabled = true
            it.isTwistLimitEnabled = true
            it.block()
        }
    }

    private fun instancedBodyShader(): PbrShader {
        val cfg = PbrMaterialConfig()
        cfg.bodyMaterialCfg()
        cfg.isInstanced = true

        val model = PbrShader.defaultPbrModel(cfg).apply {
            val ifColor: StageInterfaceNode
            vertexStage {
                ifColor = stageInterfaceNode("ifColor", instanceAttributeNode(ATTRIB_COLOR).output)
            }
            fragmentStage {
                findNodeByType<PbrMaterialNode>()?.inAlbedo = ifColor.output
            }
        }

        return PbrShader(cfg, model)
    }

    private class BodyInstance(val transform: Mat4f, val size: Vec3f, color: Color) {
        private val mutColor = MutableColor(color)
        private val bufTransform = Mat4f()

        fun putInstanceData(buf: Float32Buffer) {
            transform.scale(size.x, size.y, size.z, bufTransform)
            buf.put(bufTransform.matrix)
            buf.put(mutColor.array)
        }
    }

    private inner class ForceHelper(val ctx: KoolContext) {
        val pickRay = Ray()
        val hitResult = HitResult()
        var hitActor: RigidBody? = null

        val initGlobalPos = MutableVec3f()

        val forceAppPosLocal = MutableVec3f()
        val forceAppPosGlobal = MutableVec3f()
        val forceDragPos = MutableVec3f()
        val force = MutableVec3f()
        val dragPlane = Plane()

        var isActive = false

        fun handleDrag(pointerState: InputManager.PointerState) {
            val dragPtr = pointerState.primaryPointer
            if (!dragPtr.isValid) { return }
            if (!(dragPtr.isMiddleButtonEvent || dragPtr.isMiddleButtonDown)) { return }

            mainScene.camera.computePickRay(pickRay, dragPtr, mainScene.mainRenderPass.viewport, ctx)
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
                dragPlane.n.set(mainScene.camera.globalLookDir).scale(-1f)
            }
        }

        fun applyForce() {
            hitActor?.let { actor ->
                actor.toGlobal(forceAppPosGlobal.set(forceAppPosLocal))
                dragPlane.intersectionPoint(pickRay, forceDragPos)
                force.set(forceDragPos).subtract(forceAppPosGlobal).scale(500f)
                actor.addForceAtPos(force, forceAppPosGlobal)
                isActive = true
            }
        }
    }

    companion object {
        private val ATTRIB_COLOR = Attribute("aInstColor", GlslType.VEC_4F)
    }
}