package de.fabmax.kool.demo.physics

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.constraints.RevoluteConstraint
import de.fabmax.kool.physics.shapes.*
import de.fabmax.kool.physics.shapes.MultiShape
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps

class ConstraintsDemo : DemoScene("Physics Constraints") {

    private var physicsWorld: PhysicsWorld? = null

    private var motorGearConstraint: RevoluteConstraint? = null
    private var motorStrength = 50f
    private var motorSpeed = 3f
    private var motorDirection = 1f
    private var numLinks = 40

    private val physicsMeshes = Group()
    private var resetPhysics = false

    private val shadows = mutableListOf<ShadowMap>()
    private lateinit var aoPipeline: AoPipeline
    private lateinit var ibl: EnvironmentMaps

    override fun setupMainScene(ctx: KoolContext) = scene {
        defaultCamTransform().apply {
            zoom = 75.0
            maxZoom = 200.0
        }
        (camera as PerspectiveCamera).apply {
            clipNear = 1f
            clipFar = 1000f
        }

        // light setup
        lightSetup()

        // group containing physics bodies
        +physicsMeshes

        ctx.assetMgr.launch {
            ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/colorful_studio_1k.rgbe.png", this)

            Physics.awaitLoaded()

            val world = PhysicsWorld()
            physicsWorld = world
            resetPhysics = true

            // ground plane
            +textureMesh(isNormalMapped = true) {
                generate {
                    rotate(-90f, Vec3f.X_AXIS)
                    rect {
                        size.set(250f, 250f)
                        origin.set(-size.x * 0.5f, -size.y * 0.5f, -20f)
                        generateTexCoords(15f)
                    }
                }
                shader = pbrShader {
                    useAlbedoMap("tiles_flat_gray.png")
                    useNormalMap("tiles_flat_normal.png")
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
                makePhysicsScene(it.ctx)
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

    private fun makePhysicsScene(ctx: KoolContext) {
        physicsMeshes.children.forEach { it.dispose(ctx) }
        physicsMeshes.removeAllChildren()

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
            sliderWithValue("Motor Strength:", motorStrength, 0f, 100f, 0) {
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
        }
    }

    private fun updateMotor() {
        motorGearConstraint?.apply {
            enableAngularMotor(motorSpeed * motorDirection, motorStrength)
        }
    }

    private fun computeAxleDist(): Float {
        val linkLen = 4f
        return (numLinks - 12) / 2 * linkLen + 0.3f
    }

    private fun makeGearChain(numLinks: Int, frame: Mat4f) {
        val world = physicsWorld ?: return

        val linkMass = 1f
        val gearMass = 10f
        val gearR = 7f
        val axleDist = computeAxleDist()

        if (numLinks % 2 != 0) {
            throw IllegalArgumentException("numLinks must be even")
        }

        makeGearAndAxle(gearR, Vec3f(0f, axleDist / 2f, 0f), gearMass, true, frame)
        makeGearAndAxle(gearR, Vec3f(0f, -axleDist / 2f, 0f), gearMass, false, frame)

        val t = Mat4f().set(frame).translate(0f, axleDist / 2f + gearR + 0.4f, 0f)
        val r = Mat3f()

        val rotLinks = mutableSetOf(1, 2, 3, numLinks - 2, numLinks - 1)
        for (i in (numLinks / 2 - 2)..(numLinks / 2 + 3)) {
            rotLinks += i
        }

        val firstLink = makeChainLink(linkMass)
        firstLink.origin = t.getOrigin(MutableVec3f())
        world.addRigidBody(firstLink)
        addPhysicsMesh(firstLink, Color.MD_LIME.toLinear())
        var prevLink = firstLink

        for (i in 1 until numLinks) {
            val link = makeChainLink(linkMass)

            t.translate(2f, 0f, 0f)
            if (i in rotLinks) {
                t.rotate(0f, 0f, -30f)
            }
            t.translate(2f, 0f, 0f)
            link.origin = t.getOrigin(MutableVec3f())
            link.setRotation(t.getRotation(r))

            world.addRigidBody(link)
            addPhysicsMesh(link, Color.MD_GREEN.toLinear())

            val chainHinge = RevoluteConstraint(prevLink, link,
                Vec3f(2f, 0f, 0f), Vec3f(-2f, 0f, 0f),
                Vec3f.Z_AXIS, Vec3f.Z_AXIS)
            world.addConstraint(chainHinge)

            prevLink = link
        }

        val chainHinge = RevoluteConstraint(prevLink, firstLink,
            Vec3f(2f, 0f, 0f), Vec3f(-2f, 0f, 0f),
            Vec3f.Z_AXIS, Vec3f.Z_AXIS)
        world.addConstraint(chainHinge)
    }

    private fun makeGearAndAxle(gearR: Float, origin: Vec3f, gearMass: Float, isDriven: Boolean, frame: Mat4f) {
        val world = physicsWorld ?: return

        val axleHolder = RigidBody(BoxShape(Vec3f(22f, 3f, 2f)), 0f)
        axleHolder.setRotation(frame.getRotation(Mat3f()))
        axleHolder.origin = frame.transform(MutableVec3f(origin).add(Vec3f(-9f, 0f, -4f)))
        world.addRigidBody(axleHolder)
        addPhysicsMesh(axleHolder, Color.LIGHT_GRAY.toLinear())

        val axle = RigidBody(CylinderShape(7f, 1f), 0f)
        axle.setRotation(frame.getRotation(Mat3f()).rotate(90f, 0f, 0f))
        axle.origin = frame.transform(MutableVec3f(origin))
        world.addRigidBody(axle)
        addPhysicsMesh(axle, Color.MD_LIGHT_BLUE.toLinear())

        val gear = makeGear(gearR, gearMass)
        gear.setRotation(frame.getRotation(Mat3f()))
        gear.origin = frame.transform(MutableVec3f(origin))
        world.addRigidBody(gear)
        addPhysicsMesh(gear, Color.MD_PINK.toLinear())

        val motor = RevoluteConstraint(axle, gear,
            Vec3f(0f, 0f, 0f), Vec3f(0f, 0f, 0f),
            Vec3f.Y_AXIS, Vec3f.Z_AXIS)
        world.addConstraint(motor, true)
        if (isDriven) {
            motorGearConstraint = motor
        }
    }

    private fun makeGear(gearR: Float, mass: Float): RigidBody {
        val toothH = 1f
        val toothWb = 1f
        val toothWt = 0.7f
        val gearShape = MultiShape()
        gearShape.addShape(CylinderShape(3f, gearR), Mat4f().rotate(90f, 0f, 0f))
        val toothPts = listOf(
            Vec3f(toothWt, gearR + toothH, -0.45f), Vec3f(toothWt, gearR + toothH, 0.45f),
            Vec3f(-toothWt, gearR + toothH, -0.45f), Vec3f(-toothWt, gearR + toothH, 0.45f),

            Vec3f(toothWb, gearR - 0.1f, -0.6f), Vec3f(toothWb, gearR - 0.1f, 0.6f),
            Vec3f(-toothWb, gearR - 0.1f, -0.6f), Vec3f(-toothWb, gearR - 0.1f, 0.6f)
        )
        for (i in 0..11) {
            gearShape.addShape(ConvexHullShape(toothPts), Mat4f().rotate(0f, 0f, 30f * i - 3f))
        }
        return RigidBody(gearShape, mass)
    }

    private fun makeChainLink(mass: Float): RigidBody {
        val boxA = BoxShape(Vec3f(1.8f, 0.8f, 1f))
        val boxB = BoxShape(Vec3f(3.6f, 0.8f, 1f))
        val boxC = BoxShape(Vec3f(3.6f, 0.8f, 1f))

        val shape = MultiShape()
        shape.addShape(boxA, Mat4f().translate(-1.6f, 0f, 0f))
        shape.addShape(boxB, Mat4f().translate(0.7f, 0f, 1.1f))
        shape.addShape(boxC, Mat4f().translate(0.7f, 0f, -1.1f))

        val hingeBodyProps = rigidBodyProperties {
            friction = 0.1f
            linearDamping = 0.1f
            angularDamping = 0.5f
            sleepThreshold = 0.3f
        }
        return RigidBody(shape, mass, hingeBodyProps)
    }

    private fun addPhysicsMesh(body: RigidBody, color: Color) {
        val mesh = body.toColorMesh(color)
        (mesh.children[0] as Mesh).apply {
            shader = pbrShader {
                albedoSource = Albedo.VERTEX_ALBEDO
                useImageBasedLighting(ibl)
                useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                shadowMaps += shadows
            }
        }
        physicsMeshes += mesh
    }
}