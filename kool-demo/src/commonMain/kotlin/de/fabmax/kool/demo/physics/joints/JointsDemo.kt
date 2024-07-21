package de.fabmax.kool.demo.physics.joints

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.ConvexMeshGeometry
import de.fabmax.kool.physics.geometry.CylinderGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.joints.RevoluteJoint
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.SimpleShadowMap
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.roundToInt

class JointsDemo : DemoScene("Physics - Joints") {

    private val physicsStepper = ConstantPhysicsStepperSync().apply {
        // make the chain spin faster by using double speed simulation
        simTimeFactor = 2f
    }
    private val physicsWorld: PhysicsWorld = PhysicsWorld(mainScene).apply {
        simStepper = physicsStepper
    }

    private val motorStrength = mutableStateOf(50f)
    private val motorSpeed = mutableStateOf(1.5f)
    private val motorDirection = mutableStateOf(1f)
    private val numLinks = mutableStateOf(40)

    private val drawNiceMeshes = mutableStateOf(true).onChange { _, new -> niceMeshes.isVisible = new }
    private val drawPhysMeshes = mutableStateOf(false).onChange { _, new -> physMeshes.isVisible = new }
    private val drawJointInfos = mutableStateOf(false).onChange { _, new -> constraintInfo.isVisible = new }

    private val physicsTimeTxt = mutableStateOf("0.00 ms")
    private val numBodiesTxt = mutableStateOf("0")
    private val numJointsTxt = mutableStateOf("0")
    private val timeFactorTxt = mutableStateOf("1.00 x")

    private var motorGearConstraint: RevoluteJoint? = null
    private val joints = mutableListOf<RevoluteJoint>()
    private val physMeshes = BodyMeshes(false).apply { isVisible = false }
    private val niceMeshes = BodyMeshes(true).apply { isVisible = true }
    private val constraintInfo = ConstraintsInfoMesh().apply { isVisible = false }
    private var resetPhysics = true

    private val shadows = mutableListOf<SimpleShadowMap>()
    private lateinit var aoPipeline: AoPipeline
    private val ibl by hdriImage("${DemoLoader.hdriPath}/colorful_studio_1k.rgbe.png")
    private val groundAlbedo by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
    private val groundNormal by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine_normal.png")

    private val staticCollGroup = 1
    private val staticSimFilterData = FilterData {
        setCollisionGroup(staticCollGroup)
        clearCollidesWith(staticCollGroup)
    }
    private val material = Material(0.5f)

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera(-20f, -20f).apply {
            setZoom(50.0, max = 200.0)
        }
        camera.apply {
            clipNear = 1f
            clipFar = 1000f
        }

        // light setup
        lightSetup()

        // group containing physics bodies
        addNode(physMeshes)
        addNode(niceMeshes)
        addNode(constraintInfo)

        // ground plane
        addTextureMesh(isNormalMapped = true) {
            isCastingShadow = false
            generate {
                rotate((-90f).deg, Vec3f.X_AXIS)
                rect {
                    size.set(250f, 250f)
                    origin.set(0f, 0f, -20f)
                    generateTexCoords(15f)
                }
            }
            shader = KslPbrShader {
                color { textureColor(groundAlbedo) }
                normalMapping { setNormalMap(groundNormal) }
                enableSsao(aoPipeline.aoMap)
                lighting {
                    addShadowMaps(shadows)
                    imageBasedAmbientLight(ibl.irradianceMap)
                }
                reflectionMap = ibl.reflectionMap
            }
        }

        addNode(Skybox.cube(ibl.reflectionMap, 1.5f))

        onUpdate += {
            if (resetPhysics) {
                resetPhysics = false
                makePhysicsScene()
            }
            physicsTimeTxt.set("${physicsStepper.perfCpuTime.toString(2)} ms")
            timeFactorTxt.set("${physicsStepper.perfTimeFactor.toString(2)} x")
            numBodiesTxt.set("${physicsWorld.actors.size}")
            numJointsTxt.set("${joints.size}")
        }
    }

    private fun Scene.lightSetup() {
        aoPipeline = AoPipeline.createForward(this)
        lighting.apply {
            clear()
            val l1 = Vec3f(80f, 120f, 100f)
            val l2 = Vec3f(-30f, 100f, 100f)
            addSpotLight {
                setup(l1, MutableVec3f(l1).mul(-1f).norm(), 45f.deg)
                setColor(Color.WHITE.mix(MdColor.AMBER, 0.1f), 50000f)
            }
            addSpotLight {
                setup(l2, MutableVec3f(l2).mul(-1f).norm(), 45f.deg)
                setColor(Color.WHITE.mix(MdColor.LIGHT_BLUE, 0.1f), 25000f)
            }
        }
        shadows.add(SimpleShadowMap(this, lighting.lights[0]).apply {
            clipNear = 100f
            clipFar = 500f
            shaderDepthOffset = -0.2f
            shadowBounds = BoundingBoxF(Vec3f(-75f, -20f, -75f), Vec3f(75f, 20f, 75f))
        })
        shadows.add(SimpleShadowMap(this, lighting.lights[1]).apply {
            clipNear = 100f
            clipFar = 500f
            shaderDepthOffset = -0.2f
            shadowBounds = BoundingBoxF(Vec3f(-75f, -20f, -75f), Vec3f(75f, 20f, 75f))
        })
    }

    private fun makePhysicsScene() {
        physMeshes.clearBodies()
        niceMeshes.clearBodies()
        joints.forEach { it.release() }
        joints.clear()

        physicsWorld.apply {
            clear()

            val groundPlane = RigidStatic()
            groundPlane.simulationFilterData = staticSimFilterData
            groundPlane.attachShape(Shape(PlaneGeometry(), material))
            groundPlane.position = Vec3f(0f, -20f, 0f)
            groundPlane.setRotation(Mat3f.rotation(90f.deg, Vec3f.Z_AXIS))
            addActor(groundPlane)
        }

        val frame = MutableMat4f().rotate(90f.deg, Vec3f.Z_AXIS)
        makeGearChain(numLinks.value, frame)
        updateMotor()
    }

    override fun onRelease(ctx: KoolContext) {
        super.onRelease(ctx)
        joints.forEach { it.release() }
        physicsWorld.release()
        material.release()
    }

    private fun drawNiceMeshes() {
        drawNiceMeshes.set(true)
        drawPhysMeshes.set(false)
    }

    private fun drawPhysMeshes() {
        drawNiceMeshes.set(false)
        drawPhysMeshes.set(true)
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuSlider2("Number of links", numLinks.use() / 2f, 10f, 50f, { "${it.roundToInt() * 2}" }) {
            val links = it.roundToInt() * 2
            if (links != numLinks.value) {
                numLinks.set(links)
                resetPhysics = true
            }
        }
        MenuSlider2("Motor strength", motorStrength.use(), 0f, 100f, { "${it.toInt()}" }) {
            motorStrength.set(it)
            updateMotor()
        }
        MenuSlider2("Motor speed", motorSpeed.use(), 0f, 10f, { it.toString(1) }) {
            motorSpeed.set(it)
            updateMotor()
        }
        MenuRow {
            Text("Reverse motor") {
                labelStyle(Grow.Std)
                modifier.onClick {
                    motorDirection.set(-motorDirection.value)
                    updateMotor()
                }
            }
            Switch(motorDirection.use() < 0f) {
                modifier
                    .alignY(AlignmentY.Center)
                    .onToggle {
                        motorDirection.set(-motorDirection.value)
                        updateMotor()
                    }
            }
        }

        Text("Visualization") { sectionTitleStyle() }
        MenuRow {
            RadioButton(drawNiceMeshes.use()) {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(end = sizes.gap)
                    .onToggle {
                        if (it) {
                            drawNiceMeshes()
                        }
                    }
            }
            Text("Draw nice meshes") {
                labelStyle(Grow.Std)
                modifier.onClick { drawNiceMeshes() }
            }
        }
        MenuRow {
            RadioButton(drawPhysMeshes.use()) {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(end = sizes.gap)
                    .onToggle {
                        if (it) {
                            drawPhysMeshes()
                        }
                    }
            }
            Text("Draw physics meshes") {
                labelStyle(Grow.Std)
                modifier.onClick { drawPhysMeshes() }
            }
        }
        LabeledSwitch("Draw joint infos", drawJointInfos)

        Text("Statistics") { sectionTitleStyle() }
        MenuRow {
            Text("Number of joints") { labelStyle(Grow.Std) }
            Text(numJointsTxt.use()) { labelStyle() }
        }
        MenuRow {
            Text("Number of bodies") { labelStyle(Grow.Std) }
            Text(numBodiesTxt.use()) { labelStyle() }
        }
        MenuRow {
            Text("Physics step CPU time") { labelStyle(Grow.Std) }
            Text(physicsTimeTxt.use()) { labelStyle() }
        }
        MenuRow {
            Text("Time factor") { labelStyle(Grow.Std) }
            Text(timeFactorTxt.use()) { labelStyle() }
        }
    }

    private fun updateMotor() {
        motorGearConstraint?.apply {
            enableAngularMotor(motorSpeed.value * -motorDirection.value, motorStrength.value)
        }
    }

    private fun computeAxleDist(): Float {
        val linkLen = 4f
        return (numLinks.value - 12) / 2 * linkLen
    }

    private fun makeGearChain(numLinks: Int, frame: Mat4f) {
        val world = physicsWorld

        val linkMass = 1f
        val gearMass = 10f
        val gearR = 6.95f
        val axleDist = computeAxleDist()
        val tension = 0.05f

        if (numLinks % 2 != 0) {
            throw IllegalArgumentException("numLinks must be even")
        }

        makeGearAndAxle(gearR, Vec3f(0f, axleDist / 2f, 0f), gearMass, true, frame)
        makeGearAndAxle(gearR, Vec3f(0f, -axleDist / 2f, 0f), gearMass, false, frame)
        makeChain(linkMass, tension, gearR, axleDist, frame, world)
    }

    private fun makeChain(linkMass: Float, tension: Float, gearR: Float, axleDist: Float, frame: Mat4f, world: PhysicsWorld) {
        val t = MutableMat4f().set(frame).translate(0f, axleDist / 2f + gearR + 0.6f, 0f)

        val nLinks = numLinks.value
        val rotLinks = mutableSetOf(1, 2, 3, nLinks - 2, nLinks - 1, nLinks)
        for (i in (nLinks / 2 - 2)..(nLinks / 2 + 3)) {
            rotLinks += i
        }

        val firstOuter = makeOuterChainLink(linkMass)
        firstOuter.position = t.getTranslation()
        firstOuter.rotation = t.getRotation()
        world.addActor(firstOuter)

        var prevInner = makeInnerChainLink(linkMass)
        t.translate(1.5f, 0f, 0f)
        t.rotate(0f.deg, 0f.deg, (-15f).deg)
        t.translate(0.5f, 0f, 0f)
        prevInner.position = t.getTranslation()
        prevInner.rotation = t.getRotation()
        world.addActor(prevInner)

        connectLinksOuterInner(firstOuter, prevInner, tension)

        physMeshes.linksO += firstOuter
        niceMeshes.linksO += firstOuter
        physMeshes.linksI += prevInner
        niceMeshes.linksI += prevInner

        for (i in 1 until nLinks) {
            t.translate(0.5f, 0f, 0f)
            if (i in rotLinks) {
                t.rotate(0f.deg, 0f.deg, (-15f).deg)
            }
            t.translate(1.5f, 0f, 0f)

            val outer = makeOuterChainLink(linkMass * 2)
            outer.position = t.getTranslation()
            outer.rotation = t.getRotation()
            world.addActor(outer)

            connectLinksInnerOuter(prevInner, outer, tension)

            prevInner = makeInnerChainLink(linkMass)
            t.translate(1.5f, 0f, 0f)
            if ((i + 1) in rotLinks) {
                t.rotate(0f.deg, 0f.deg, (-15f).deg)
            }
            t.translate(0.5f, 0f, 0f)
            prevInner.position = t.getTranslation()
            prevInner.rotation = t.getRotation()
            world.addActor(prevInner)

            connectLinksOuterInner(outer, prevInner, tension)

            physMeshes.linksO += outer
            niceMeshes.linksO += outer
            physMeshes.linksI += prevInner
            niceMeshes.linksI += prevInner
        }

        connectLinksInnerOuter(prevInner, firstOuter, tension)
    }

    private fun connectLinksOuterInner(outer: RigidDynamic, inner: RigidDynamic, t: Float) {
        val hinge = RevoluteJoint(outer, inner,
            Vec3f(1.5f - t, 0f, 0f), Vec3f(-0.5f, 0f, 0f),
            Vec3f.Z_AXIS, Vec3f.Z_AXIS)
        joints += hinge
    }

    private fun connectLinksInnerOuter(inner: RigidDynamic, outer: RigidDynamic, t: Float) {
        val hinge = RevoluteJoint(outer, inner,
            Vec3f(-1.5f + t, 0f, 0f), Vec3f(0.5f, 0f, 0f),
            Vec3f.Z_AXIS, Vec3f.Z_AXIS)
        joints += hinge
    }

    private fun makeGearAndAxle(gearR: Float, origin: Vec3f, gearMass: Float, isDriven: Boolean, frame: Mat4f) {
        val world = physicsWorld

        val axleGeom = CylinderGeometry(7f, 1f)
        val axle = RigidStatic()
        axle.simulationFilterData = staticSimFilterData
        axle.attachShape(Shape(axleGeom, material))

        axle.setRotation(MutableMat3f().rotate(frame.getRotation()).rotate(0f.deg, (-90f).deg, 0f.deg))
        axle.position = frame.transform(MutableVec3f(origin))
        world.addActor(axle)
        physMeshes.axles += axle
        niceMeshes.axles += axle
        axleGeom.release()

        val gear = makeGear(gearR, gearMass)
        gear.rotation = frame.getRotation()
        gear.position = frame.transform(MutableVec3f(origin))
        world.addActor(gear)
        physMeshes.gears += gear
        niceMeshes.gears += gear

        val motor = RevoluteJoint(axle, gear,
            Vec3f(0f, 0f, 0f), Vec3f(0f, 0f, 0f),
            Vec3f.X_AXIS, Vec3f.Z_AXIS)
        joints += motor
        if (isDriven) {
            motorGearConstraint = motor
        }
    }

    private fun makeGear(gearR: Float, mass: Float): RigidDynamic {
        val s = 1f
        val toothH = 1f * s
        val toothBb = 0.55f * s
        val toothBt = 0.4f * s
        val toothWb = 1f * s
        val toothWt = 0.7f * s
        val gearShapes = mutableListOf<Shape>()

        val toothPts = listOf(
            Vec3f(toothWt, gearR + toothH, -toothBt), Vec3f(toothWt, gearR + toothH, toothBt),
            Vec3f(-toothWt, gearR + toothH, -toothBt), Vec3f(-toothWt, gearR + toothH, toothBt),

            Vec3f(toothWb, gearR - 0.1f, -toothBb), Vec3f(toothWb, gearR - 0.1f, toothBb),
            Vec3f(-toothWb, gearR - 0.1f, -toothBb), Vec3f(-toothWb, gearR - 0.1f, toothBb)
        )
        val toothGeom = ConvexMeshGeometry(toothPts)
        val cylGeom = CylinderGeometry(2.5f, gearR)

        gearShapes += Shape(cylGeom, material, Mat4f.rotation(0f.deg, 90f.deg, 0f.deg))
        for (i in 0..11) {
            gearShapes += Shape(toothGeom, material, Mat4f.rotation(0f.deg, 0f.deg, (30f * i).deg))
        }

        val gearFilterData = FilterData {
            setCollisionGroup(0)
            clearCollidesWith(staticCollGroup)
        }
        val gear = RigidDynamic(mass)
        gear.simulationFilterData = gearFilterData
        gearShapes.forEach { shape ->
            gear.attachShape(shape)
        }
        toothGeom.release()
        cylGeom.release()
        return gear
    }

    private fun makeOuterChainLink(mass: Float): RigidDynamic {
        val boxA = BoxGeometry(Vec3f(3.4f, 0.8f, 0.3f))
        val boxB = BoxGeometry(Vec3f(3.4f, 0.8f, 0.3f))

        val shapes = mutableListOf<Shape>()
        shapes += Shape(boxA, material, MutableMat4f().translate(0f, 0f, 0.75f))
        shapes += Shape(boxB, material, MutableMat4f().translate(0f, 0f, -0.75f))

        val link = RigidDynamic(mass)
        shapes.forEach { shape ->
            link.attachShape(shape)
        }
        boxA.release()
        boxB.release()
        return link
    }

    private fun makeInnerChainLink(mass: Float): RigidDynamic {
        val w1 = 0.95f
        val h1 = 0.2f
        val w2 = 0.7f
        val h2 = 0.6f
        val d = 0.5f
        val points = listOf(
            Vec3f(-w1, -h1, -d), Vec3f(-w1, -h1, d),
            Vec3f(-w1,  h1, -d), Vec3f(-w1,  h1, d),
            Vec3f( w1, -h1, -d), Vec3f( w1, -h1, d),
            Vec3f( w1,  h1, -d), Vec3f( w1,  h1, d),

            Vec3f(-w2, -h2, -d), Vec3f(-w2, -h2, d),
            Vec3f(-w2,  h2, -d), Vec3f(-w2,  h2, d),
            Vec3f( w2, -h2, -d), Vec3f( w2, -h2, d),
            Vec3f( w2,  h2, -d), Vec3f( w2,  h2, d),
        )
        val geom = ConvexMeshGeometry(points)

        val link = RigidDynamic(mass)
        link.attachShape(Shape(geom, material))
        geom.release()
        return link
    }

    private inner class BodyMesh(val color: Color, val onCreate: (Mesh) -> Unit) {
        var mesh: Mesh? = null

        var factory: (RigidActor) -> Mesh = { proto ->
            Mesh(
                Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS,
                instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
            ).apply {
                isFrustumChecked = false
                generate {
                    color = this@BodyMesh.color
                    proto.shapes.forEach { shape ->
                        withTransform {
                            transform.mul(shape.localPose)
                            shape.geometry.generateMesh(this)
                        }
                    }
                }
                shader = KslPbrShader {
                    vertices { isInstanced = true }
                    color { vertexColor() }
                    roughness(1f)
                    enableSsao(aoPipeline.aoMap)
                    lighting {
                        addShadowMaps(shadows)
                        imageBasedAmbientLight(ibl.irradianceMap)
                    }
                    reflectionMap = ibl.reflectionMap
                }
            }
        }

        fun getOrCreate(protoBody: RigidActor): Mesh {
            if (mesh == null) {
                mesh = factory(protoBody)
                onCreate(mesh!!)
            }
            return mesh!!
        }

        fun updateInstances(bodies: List<RigidActor>) {
            if (bodies.isNotEmpty()) {
                getOrCreate(bodies[0]).instances!!.apply {
                    clear()
                    addInstances(bodies.size) { buf ->
                        for (i in bodies.indices) {
                            bodies[i].transform.matrixF.putTo(buf)
                        }
                    }
                }
            }
        }
    }

    private inner class BodyMeshes(isNice: Boolean): Node() {
        var linkMeshO = BodyMesh(MdColor.BLUE_GREY.toLinear()) { addNode(it) }
        var linkMeshI = BodyMesh(MdColor.BLUE_GREY toneLin 350) { addNode(it) }
        var gearMesh = BodyMesh(MdColor.BLUE_GREY toneLin 200) { addNode(it) }
        var axleMesh = BodyMesh(MdColor.BLUE_GREY toneLin 700) { addNode(it) }

        val linksO = mutableListOf<RigidDynamic>()
        val linksI = mutableListOf<RigidDynamic>()
        val gears = mutableListOf<RigidDynamic>()
        val axles = mutableListOf<RigidStatic>()

        init {
            isFrustumChecked = false

            if (isNice) {
                linkMeshO.factory = { GearChainMeshGen.makeNiceOuterLinkMesh(ibl, aoPipeline.aoMap, shadows) }
                linkMeshI.factory = { GearChainMeshGen.makeNiceInnerLinkMesh(ibl, aoPipeline.aoMap, shadows) }
                gearMesh.factory = { GearChainMeshGen.makeNiceGearMesh(ibl, aoPipeline.aoMap, shadows) }
                axleMesh.factory = { GearChainMeshGen.makeNiceAxleMesh(ibl, aoPipeline.aoMap, shadows) }
            }

            onUpdate += {
                linkMeshO.updateInstances(linksO)
                linkMeshI.updateInstances(linksI)
                gearMesh.updateInstances(gears)
                axleMesh.updateInstances(axles)
            }
        }

        fun clearBodies() {
            linksO.clear()
            linksI.clear()
            gears.clear()
            axles.clear()
        }
    }

    private inner class ConstraintsInfoMesh : LineMesh() {
        val gradient = ColorGradient.RED_YELLOW_GREEN.inverted()

        // keep temp vectors as members to not re-allocate them all the time
        val tmpAx = MutableVec3f()
        val tmpP1 = MutableVec3f()
        val tmpP2 = MutableVec3f()
        val tmpA1 = MutableVec3f()
        val tmpA2 = MutableVec3f()

        val tmpL1 = MutableVec3f()
        val tmpL2 = MutableVec3f()

        init {
            isCastingShadow = false
            shader = KslUnlitShader {
                color { vertexColor() }
                pipeline { lineWidth = 3f }
            }
        }

        override fun update(updateEvent: RenderPass.UpdateEvent) {
            if (isVisible) {
                clear()
                joints.forEach {
                    renderRevoluteConstraint(it)
                }
            }
            super.update(updateEvent)
        }

        private fun renderRevoluteConstraint(rc: RevoluteJoint) {
            val tA = rc.bodyA.transform
            val tB = rc.bodyB.transform

            rc.frameA.transform(tmpAx.set(Vec3f.X_AXIS), 0f)
            rc.frameA.transform(tmpP1.set(Vec3f.ZERO), 1f)
            tA.transform(tmpA1.set(tmpAx), 0f)
            tA.transform(tmpP1)
            val lenA = rc.bodyA.worldBounds.size.dot(tmpAx) * 0.5f + 1f

            rc.frameB.transform(tmpAx.set(Vec3f.X_AXIS), 0f)
            rc.frameB.transform(tmpP2.set(Vec3f.ZERO), 1f)
            tB.transform(tmpA2.set(tmpAx), 0f)
            tB.transform(tmpP2)
            val lenB = rc.bodyB.worldBounds.size.dot(tmpAx) * 0.5f + 1f

            val drawLen = max(lenA, lenB)
            val diff = tmpP1.distance(tmpP2) + abs(acos(tmpA1.dot(tmpA2)).toDeg()) / 20
            val color = gradient.getColor(diff, 0f, 0.5f)

            tmpL1.set(tmpA1).mul(drawLen).add(tmpP1)
            tmpL2.set(tmpA1).mul(-drawLen).add(tmpP1)
            addLine(tmpL1, tmpL2, color)

            tmpL1.set(tmpA2).mul(drawLen).add(tmpP2)
            tmpL2.set(tmpA2).mul(-drawLen).add(tmpP2)
            addLine(tmpL1, tmpL2, color)

            tmpL1.set(tmpA1).mul(drawLen).add(tmpP1)
            tmpL2.set(tmpA2).mul(drawLen).add(tmpP2)
            addLine(tmpL1, tmpL2, color)

            tmpL1.set(tmpA1).mul(-drawLen).add(tmpP1)
            tmpL2.set(tmpA2).mul(-drawLen).add(tmpP2)
            addLine(tmpL1, tmpL2, color)
        }
    }
}