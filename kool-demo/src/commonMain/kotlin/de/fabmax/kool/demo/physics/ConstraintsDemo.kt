package de.fabmax.kool.demo.physics

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.constraints.RevoluteConstraint
import de.fabmax.kool.physics.shapes.*
import de.fabmax.kool.physics.shapes.MultiShape
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.pipeline.shading.unlitShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.max

class ConstraintsDemo : DemoScene("Physics Constraints") {

    private var physicsWorld: PhysicsWorld? = null

    private var motorGearConstraint: RevoluteConstraint? = null
    private var motorStrength = 100f
    private var motorSpeed = 4f
    private var motorDirection = 1f
    private var numLinks = 40

    private val physMeshes = BodyMeshes(false).apply { isVisible = false }
    private val niceMeshes = BodyMeshes(true).apply { isVisible = true }
    private lateinit var constraintInfo: ConstraintsInfoMesh
    private var resetPhysics = false

    private val shadows = mutableListOf<SimpleShadowMap>()
    private lateinit var aoPipeline: AoPipeline
    private lateinit var ibl: EnvironmentMaps

    override fun setupMainScene(ctx: KoolContext) = scene {
        defaultCamTransform().apply {
            setMouseRotation(-20f, -20f)
            zoom = 50.0
            maxZoom = 200.0
        }
        (camera as PerspectiveCamera).apply {
            clipNear = 1f
            clipFar = 1000f
        }

        // light setup
        lightSetup()

        // group containing physics bodies
        +physMeshes
        +niceMeshes

        ctx.assetMgr.launch {
            ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/colorful_studio_1k.rgbe.png", this)

            Physics.awaitLoaded()
            val world = PhysicsWorld()
            physicsWorld = world
            resetPhysics = true
            constraintInfo = ConstraintsInfoMesh(world).apply { isVisible = false }
            +constraintInfo

            // ground plane
            +textureMesh(isNormalMapped = true) {
                isCastingShadow = false
                generate {
                    rotate(-90f, Vec3f.X_AXIS)
                    rect {
                        size.set(250f, 250f)
                        origin.set(-size.x * 0.5f, -size.y * 0.5f, -20f)
                        generateTexCoords(15f)
                    }
                }
                shader = pbrShader {
                    useAlbedoMap("${Demo.pbrBasePath}/tile_flat/tiles_flat_gray.png")
                    useNormalMap("${Demo.pbrBasePath}/tile_flat/tiles_flat_normal.png")
                    useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                    useImageBasedLighting(ibl)
                    shadowMaps += shadows
                }
            }

            +Skybox.cube(ibl.reflectionMap, 1f)
        }

        onUpdate += {
            if (resetPhysics) {
                resetPhysics = false
                makePhysicsScene()
            }
            physicsWorld?.stepPhysics(it.deltaT)
        }
    }

    private fun Scene.lightSetup() {
        aoPipeline = AoPipeline.createForward(this)
        lighting.apply {
            lights.clear()
            val l1 = Vec3f(80f, 120f, 100f)
            val l2 = Vec3f(-30f, 100f, 100f)
            lights += Light().apply {
                setSpot(l1, MutableVec3f(l1).scale(-1f).norm(), 45f)
                setColor(Color.WHITE.mix(Color.MD_AMBER, 0.1f), 50000f)
            }
            lights += Light().apply {
                setSpot(l2, MutableVec3f(l2).scale(-1f).norm(), 45f)
                setColor(Color.WHITE.mix(Color.MD_LIGHT_BLUE, 0.1f), 25000f)
            }
        }
        shadows.add(SimpleShadowMap(this, 0).apply {
            clipNear = 100f
            clipFar = 500f
            shaderDepthOffset = -0.2f
            shadowBounds = BoundingBox(Vec3f(-75f, -20f, -75f), Vec3f(75f, 20f, 75f))
        })
        shadows.add(SimpleShadowMap(this, 1).apply {
            clipNear = 100f
            clipFar = 500f
            shaderDepthOffset = -0.2f
            shadowBounds = BoundingBox(Vec3f(-75f, -20f, -75f), Vec3f(75f, 20f, 75f))
        })
    }

    private fun makePhysicsScene() {
        physMeshes.clearBodies()
        niceMeshes.clearBodies()

        physicsWorld?.apply {
            clear()
            addRigidBody(RigidBody(PlaneShape(Vec3f.Y_AXIS, -20f), 0f))
        }

        val frame = Mat4f().rotate(90f, Vec3f.Z_AXIS)
        makeGearChain(numLinks, frame)
        updateMotor()
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        section("Physics") {
            sliderWithValue("Number of Links:", numLinks.toFloat() / 2, 10f, 50f, textFormat = { "${it.toInt() * 2}" }) {
                val lnks = value.toInt() * 2
                if (lnks != numLinks) {
                    numLinks = lnks
                    resetPhysics = true
                }
            }
            sliderWithValue("Motor Strength:", motorStrength, 0f, 200f, 0) {
                motorStrength = value
                updateMotor()
            }
            sliderWithValue("Motor Speed:", motorSpeed, 0f, 10f, 1) {
                motorSpeed = value
                updateMotor()
            }
            toggleButton("Reverse Motor Direction", motorDirection < 0) {
                motorDirection = if (isEnabled) -1f else 1f
                updateMotor()
            }
        }
        section("Rendering") {
            val showNiceMeshes = toggleButton("Draw Nice Meshes", niceMeshes.isVisible) { }
            val showPhysMeshes = toggleButton("Draw Physics Meshes", physMeshes.isVisible) { }
            var ignoreStateChange = false
            showNiceMeshes.onStateChange += {
                if (!ignoreStateChange) {
                    ignoreStateChange = true
                    niceMeshes.isVisible = isEnabled
                    physMeshes.isVisible = !isEnabled
                    showPhysMeshes.isEnabled = !isEnabled
                    ignoreStateChange = false
                }
            }
            showPhysMeshes.onStateChange += {
                if (!ignoreStateChange) {
                    ignoreStateChange = true
                    physMeshes.isVisible = isEnabled
                    niceMeshes.isVisible = !isEnabled
                    showNiceMeshes.isEnabled = !isEnabled
                    ignoreStateChange = false
                }
            }
            toggleButton("Draw Constraints", false) {
                constraintInfo.isVisible = isEnabled
            }
        }
        section("Performance") {
            textWithValue("Physics:", "0.00 ms").apply {
                onUpdate += {
                    text = "${physicsWorld?.cpuTime?.toString(2)} ms"
                }
            }
            textWithValue("Time Factor:", "1.00 x").apply {
                onUpdate += {
                    text = "${physicsWorld?.timeFactor?.toString(2)} x"
                }
            }
            textWithValue("Number of Bodies:", "").apply {
                onUpdate += {
                    text = "${physicsWorld?.bodies?.size ?: 0}"
                }
            }
            textWithValue("Number of Constraints:", "").apply {
                onUpdate += {
                    text = "${physicsWorld?.constraints?.size ?: 0}"
                }
            }
        }
    }

    private fun updateMotor() {
        motorGearConstraint?.apply {
            enableAngularMotor(motorSpeed * motorDirection, motorStrength)
        }
    }

    private fun computeAxleDist(): Float {
        val linkLen = 4f
        return (numLinks - 12) / 2 * linkLen
    }

    private fun makeGearChain(numLinks: Int, frame: Mat4f) {
        val world = physicsWorld ?: return

        val linkMass = 1f
        val gearMass = 10f
        val gearR = 7f
        val axleDist = computeAxleDist()
        val tension = 0.1f

        if (numLinks % 2 != 0) {
            throw IllegalArgumentException("numLinks must be even")
        }

        makeChain(linkMass, tension, gearR, axleDist, frame, world)
        makeGearAndAxle(gearR, Vec3f(0f, axleDist / 2f, 0f), gearMass, true, frame)
        makeGearAndAxle(gearR, Vec3f(0f, -axleDist / 2f, 0f), gearMass, false, frame)

    }

    private fun makeChain(linkMass: Float, tension: Float, gearR: Float, axleDist: Float, frame: Mat4f, world: PhysicsWorld) {
        val t = Mat4f().set(frame).translate(0f, axleDist / 2f + gearR + 0.4f, 0f)
        val r = Mat3f()

        val rotLinks = mutableSetOf(1, 2, 3, numLinks - 2, numLinks - 1, numLinks)
        for (i in (numLinks / 2 - 2)..(numLinks / 2 + 3)) {
            rotLinks += i
        }

        val firstOuter = makeOuterChainLink(linkMass * 2)
        firstOuter.origin = t.getOrigin(MutableVec3f())
        firstOuter.setRotation(t.getRotation(r))
        world.addRigidBody(firstOuter)

        var prevInner = makeInnerChainLink(linkMass)
        t.translate(1.25f, 0f, 0f)
        t.rotate(0f, 0f, -15f)
        t.translate(0.75f, 0f, 0f)
        prevInner.origin = t.getOrigin(MutableVec3f())
        prevInner.setRotation(t.getRotation(r))
        world.addRigidBody(prevInner)

        connectLinksOuterInner(firstOuter, prevInner, tension, world)

        physMeshes.linksO += firstOuter
        niceMeshes.linksO += firstOuter
        physMeshes.linksI += prevInner
        niceMeshes.linksI += prevInner

        for (i in 1 until numLinks) {
            t.translate(0.75f, 0f, 0f)
            if (i in rotLinks) {
                t.rotate(0f, 0f, -15f)
            }
            t.translate(1.25f, 0f, 0f)

            val outer = makeOuterChainLink(linkMass * 2)
            outer.origin = t.getOrigin(MutableVec3f())
            outer.setRotation(t.getRotation(r))
            world.addRigidBody(outer)

            connectLinksInnerOuter(prevInner, outer, tension, world)

            prevInner = makeInnerChainLink(linkMass)
            t.translate(1.25f, 0f, 0f)
            if ((i + 1) in rotLinks) {
                t.rotate(0f, 0f, -15f)
            }
            t.translate(0.75f, 0f, 0f)
            prevInner.origin = t.getOrigin(MutableVec3f())
            prevInner.setRotation(t.getRotation(r))
            world.addRigidBody(prevInner)

            connectLinksOuterInner(outer, prevInner, tension, world)

            physMeshes.linksO += outer
            niceMeshes.linksO += outer
            physMeshes.linksI += prevInner
            niceMeshes.linksI += prevInner
        }

        connectLinksInnerOuter(prevInner, firstOuter, tension, world)
    }

    private fun connectLinksOuterInner(outer: RigidBody, inner: RigidBody, t: Float, world: PhysicsWorld) {
        val hinge = RevoluteConstraint(outer, inner,
            Vec3f(1.5f - t, 0f, 0f), Vec3f(-0.5f, 0f, 0f),
            Vec3f.Z_AXIS, Vec3f.Z_AXIS)
        world.addConstraint(hinge, true)
    }

    private fun connectLinksInnerOuter(inner: RigidBody, outer: RigidBody, t: Float, world: PhysicsWorld) {
        val hinge = RevoluteConstraint(inner, outer,
            Vec3f(0.5f, 0f, 0f), Vec3f(-1.5f + t, 0f, 0f),
            Vec3f.Z_AXIS, Vec3f.Z_AXIS)
        world.addConstraint(hinge, true)
    }

    private fun makeGearAndAxle(gearR: Float, origin: Vec3f, gearMass: Float, isDriven: Boolean, frame: Mat4f) {
        val world = physicsWorld ?: return

        val axle = RigidBody(CylinderShape(7f, 1f), 0f)
        axle.setRotation(frame.getRotation(Mat3f()).rotate(90f, 0f, 0f))
        axle.origin = frame.transform(MutableVec3f(origin))
        world.addRigidBody(axle)
        physMeshes.axles += axle
        niceMeshes.axles += axle

        val gear = makeGear(gearR, gearMass)
        gear.setRotation(frame.getRotation(Mat3f()))
        gear.origin = frame.transform(MutableVec3f(origin))
        world.addRigidBody(gear)
        physMeshes.gears += gear
        niceMeshes.gears += gear

        val motor = RevoluteConstraint(axle, gear,
            Vec3f(0f, 0f, 0f), Vec3f(0f, 0f, 0f),
            Vec3f.Y_AXIS, Vec3f.Z_AXIS)
        world.addConstraint(motor, true)
        if (isDriven) {
            motorGearConstraint = motor
        }
    }

    private fun makeGear(gearR: Float, mass: Float): RigidBody {
        val s = 1f
        val toothH = 1f * s
        val toothBb = 0.55f * s
        val toothBt = 0.4f * s
        val toothWb = 1f * s
        val toothWt = 0.7f * s
        val gearShape = MultiShape()
        gearShape.addShape(CylinderShape(3f, gearR), Mat4f().rotate(90f, 0f, 0f))
        val toothPts = listOf(
            Vec3f(toothWt, gearR + toothH, -toothBt), Vec3f(toothWt, gearR + toothH, toothBt),
            Vec3f(-toothWt, gearR + toothH, -toothBt), Vec3f(-toothWt, gearR + toothH, toothBt),

            Vec3f(toothWb, gearR - 0.1f, -toothBb), Vec3f(toothWb, gearR - 0.1f, toothBb),
            Vec3f(-toothWb, gearR - 0.1f, -toothBb), Vec3f(-toothWb, gearR - 0.1f, toothBb)
        )
        for (i in 0..11) {
            gearShape.addShape(ConvexHullShape(toothPts), Mat4f().rotate(0f, 0f, 30f * i))
        }

        val gearBodyProps = rigidBodyProperties {
            friction = 0.2f
        }
        return RigidBody(gearShape, mass, gearBodyProps)
    }

    private fun makeOuterChainLink(mass: Float): RigidBody {
        val boxA = BoxShape(Vec3f(3.4f, 0.8f, 1f))
        val boxB = BoxShape(Vec3f(3.4f, 0.8f, 1f))

        val shape = MultiShape()
        shape.addShape(boxA, Mat4f().translate(0f, 0f, 1.1f))
        shape.addShape(boxB, Mat4f().translate(0f, 0f, -1.1f))

        val linkBodyProps = rigidBodyProperties {
            friction = 0.2f
            linearDamping = 0.1f
            angularDamping = 0.5f
            sleepThreshold = 0.3f
        }
        return RigidBody(shape, mass, linkBodyProps)
    }

    private fun makeInnerChainLink(mass: Float): RigidBody {
        val box = BoxShape(Vec3f(1.5f, 0.8f, 1f))

        val linkBodyProps = rigidBodyProperties {
            friction = 0.2f
            linearDamping = 0.1f
            angularDamping = 0.5f
            sleepThreshold = 0.3f
        }
        return RigidBody(box, mass, linkBodyProps)
    }

    private inner class BodyMesh(val color: Color, val onCreate: (Mesh) -> Unit) {
        var mesh: Mesh? = null

        var factory: (RigidBody) -> Mesh = { proto ->
            colorMesh {
                isFrustumChecked = false
                instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT))
                generate {
                    color = this@BodyMesh.color
                    proto.collisionShape.generateGeometry(this)
                }
                shader = pbrShader {
                    roughness = 1f
                    isInstanced = true
                    shadowMaps += shadows
                    useImageBasedLighting(ibl)
                    useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                }
            }
        }

        fun getOrCreate(protoBody: RigidBody): Mesh {
            if (mesh == null) {
                mesh = factory(protoBody)
                onCreate(mesh!!)
            }
            return mesh!!
        }

        fun updateInstances(bodies: List<RigidBody>) {
            if (bodies.isNotEmpty()) {
                getOrCreate(bodies[0]).instances!!.apply {
                    clear()
                    for (i in bodies.indices) {
                        addInstance {
                            put(bodies[i].transform.matrix)
                        }
                    }
                }
            }
        }
    }

    private inner class BodyMeshes(isNice: Boolean): Group() {
        var linkMeshO = BodyMesh(Color.MD_BLUE_GREY.toLinear()) { addNode(it) }
        var linkMeshI = BodyMesh((Color.MD_BLUE_GREY_300.mix(Color.MD_BLUE_GREY_400, 0.5f)).toLinear()) { addNode(it) }
        var gearMesh = BodyMesh(Color.MD_BLUE_GREY_200.toLinear()) { addNode(it) }
        var axleMesh = BodyMesh(Color.MD_BLUE_GREY_700.toLinear()) { addNode(it) }

        val linksO = mutableListOf<RigidBody>()
        val linksI = mutableListOf<RigidBody>()
        val gears = mutableListOf<RigidBody>()
        val axles = mutableListOf<RigidBody>()

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

    private class ConstraintsInfoMesh(val world: PhysicsWorld) : LineMesh() {
        val gradient = ColorGradient.RED_YELLOW_GREEN.inverted()

        // keep temp vectors as members to not re-allocate them all the time
        val tmpP1 = MutableVec3f()
        val tmpP2 = MutableVec3f()
        val tmpA1 = MutableVec3f()
        val tmpA2 = MutableVec3f()

        val tmpL1 = MutableVec3f()
        val tmpL2 = MutableVec3f()

        val tmpBnds = BoundingBox()

        init {
            shader = unlitShader {
                lineWidth = 3f
            }
        }

        override fun update(updateEvent: RenderPass.UpdateEvent) {
            if (isVisible) {
                clear()
                world.constraints.forEach {
                    when (it) {
                        is RevoluteConstraint -> renderRevoluteConstraint(it)
                    }
                }
            }
            super.update(updateEvent)
        }

        private fun renderRevoluteConstraint(rc: RevoluteConstraint) {
            val tA = rc.bodyA.transform
            val tB = rc.bodyB.transform

            tA.transform(tmpA1.set(rc.axisA), 0f)
            tA.transform(tmpP1.set(rc.pivotA))
            rc.bodyA.collisionShape.getAabb(tmpBnds)
            val lenA = tmpBnds.size * rc.axisA * 0.5f + 1f

            tB.transform(tmpA2.set(rc.axisB), 0f)
            tB.transform(tmpP2.set(rc.pivotB))
            rc.bodyB.collisionShape.getAabb(tmpBnds)
            val lenB = tmpBnds.size * rc.axisB * 0.5f + 1f

            val drawLen = max(lenA, lenB)
            val diff = tmpP1.distance(tmpP2) + (1 - tmpA1 * tmpA2)
            val color = gradient.getColor(diff, 0f, 0.5f)

            tmpL1.set(tmpA1).scale(drawLen).add(tmpP1)
            tmpL2.set(tmpA1).scale(-drawLen).add(tmpP1)
            addLine(tmpL1, tmpL2, color)

            tmpL1.set(tmpA2).scale(drawLen).add(tmpP2)
            tmpL2.set(tmpA2).scale(-drawLen).add(tmpP2)
            addLine(tmpL1, tmpL2, color)

            tmpL1.set(tmpA1).scale(drawLen).add(tmpP1)
            tmpL2.set(tmpA2).scale(drawLen).add(tmpP2)
            addLine(tmpL1, tmpL2, color)

            tmpL1.set(tmpA1).scale(-drawLen).add(tmpP1)
            tmpL2.set(tmpA2).scale(-drawLen).add(tmpP2)
            addLine(tmpL1, tmpL2, color)
        }
    }
}