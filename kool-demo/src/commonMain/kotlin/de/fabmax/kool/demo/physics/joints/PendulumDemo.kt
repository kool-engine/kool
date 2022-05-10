package de.fabmax.kool.demo.physics.joints

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Cycler
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.geometry.SphereGeometry
import de.fabmax.kool.physics.joints.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class PendulumDemo : DemoScene("Physics - Pendulum") {
    private val physicsWorld by lazy { PhysicsWorld().also {
        it.simStepper = SimplePhysicsStepper()
    } }

    private val ground by lazy { RigidStatic().also {
        it.simulationFilterData = staticSimFilterData
        it.attachShape(Shape(PlaneGeometry(), material))
        it.position = Vec3f(0f, -20f, 0f)
        it.setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
    } }

    private var motorGearConstraint: RevoluteJoint? = null
    private var motorStrength = 50f
    private var motorSpeed = 1.5f
    private var motorDirection = 1f

    private val staticCollGroup = 1
    private val staticSimFilterData = FilterData {
        setCollisionGroup(staticCollGroup)
        clearCollidesWith(staticCollGroup)
    }

    private val material = Material(0.0f)
    private lateinit var groundAlbedo: Texture2d
    private lateinit var groundNormal: Texture2d
    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        Physics.awaitLoaded()
        groundAlbedo = loadAndPrepareTexture("${Demo.materialPath}/tile_flat/tiles_flat_fine.png")
        groundNormal = loadAndPrepareTexture("${Demo.materialPath}/tile_flat/tiles_flat_fine_normal.png")

        physicsWorld.registerHandlers(mainScene)
    }

    private var pendulums = mutableListOf<Pendulum>()

    override fun Scene.setupMainScene(ctx: KoolContext) {

        setupCamera()
        setupLighting()

        physicsWorld.apply {
            addActor(ground)
        }

        // ground plane
        mainScene += textureMesh(isNormalMapped = true) {
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
                useAlbedoMap(groundAlbedo)
                useNormalMap(groundNormal)
                //useScreenSpaceAmbientOcclusion(aoPipeline.aoMap)
                //useImageBasedLighting(ibl)
                //shadowMaps += shadows
            }
        }

        pendulums.add(Pendulum(physicsWorld).also {
            it.start()
            mainScene += it
        })

        val dy = 1.5f
        var y = dy
        val radius = 0.3f
        val segmentLength = 2f

        pendulums.add( Pendulum(physicsWorld,
            bodies = 4,
            bodyMaterial = Material(0.5f),
            translate = Vec3f(0f, 2f, 6f),
            segmentLength = segmentLength,
            makeEdge = { prevNode, node ->
                //SphericalJoint(prevNode, node,
                FixedJoint(prevNode, node,
                    Mat4f().translate(0f, -segmentLength/2,  0f, /*  + segmentLength/2f*/),
                    Mat4f().translate( 0f, segmentLength/2, 0f, /* - segmentLength/2f*/)
                ).also {
                    //it.setSoftLimitCone((PI/4f).toFloat(), (PI/4f).toFloat(), 100f, 100f)
                }
            }
        ).also {
            mainScene += it
            it.start()
        } )
        y += dy

        pendulums.add( Pendulum(physicsWorld,
            translate = Vec3f(y, 0f, 0f),
            makeEdge = { p, n ->
                D6Joint(p, n,
                    Mat4f().translate(0f, -0.21f, 0f),
                    Mat4f().translate(0f, 0.21f, 0f)).also {
                    it.motionX = D6JointMotion.Locked
                    it.motionY = D6JointMotion.Locked
                    it.motionZ = D6JointMotion.Locked
                }
            }
        ).also {
            it.start()
            mainScene += it
        })
        y += dy

        pendulums.add( Pendulum(physicsWorld,
            translate = Vec3f(y, 0f, 0f),
            segmentLength = 1f,
            makeEdge = { p, n ->
                val minBounds = MutableVec3f(p.worldBounds.min).also {
                    it -=p.worldBounds.center
                }

                val maxBounds = MutableVec3f(p.worldBounds.max).also {
                    it -= p.worldBounds.center
                }
                D6Joint(p, n,
                    Mat4f().translate(0f, minBounds.y, 0f),
                    Mat4f().translate(0f, maxBounds.y, 0f)).also {
                    it.motionX = D6JointMotion.Free
                    it.motionY = D6JointMotion.Limited
                    it.motionZ = D6JointMotion.Limited
                    it.setDistanceLimit(0.5f, 10f, 10f)
                }
            }
        ).also {
            it.start()
            mainScene += it
        })
        y += dy

        //ctx.inputMgr.registerKeyListener()
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        pendulums.forEach { it.dispose(ctx) }
    }

    private fun Scene.setupCamera() {
        defaultCamTransform().apply {
            setMouseRotation(-20f, -20f)
            zoom = 50.0
            maxZoom = 200.0
        }
        (camera as PerspectiveCamera).apply {
            clipNear = 1f
            clipFar = 1000f
        }
    }

    private fun Scene.setupLighting() {
        lighting.singleLight {
            setDirectional(Vec3f(0.8f, -1.2f, 1f))
        }
    }

    class Pendulum(val world: PhysicsWorld,
                   private val bodies: Int = 10,
                   private val bodyRadius: Float = 0.2f,
                   private val bodyMaterial: Material = Material(0f, 0f, 0f),
                   private val segmentLength: Float = 1.9f,
                   private val translate: Vec3f = Vec3f.ZERO,
                   val makeEdge: Pendulum.(prevNode: RigidActor, node: RigidActor) -> Joint = { prevNode, node ->
                             DistanceJoint(prevNode, node, Mat4f(), Mat4f()).apply {
                             setMaxDistance(segmentLength)
                             debugVisualize = true
                         } }
    ) : Group("double pendulum root") {
        private val actors = mutableListOf<RigidActor>()
        private val joints = mutableListOf<Joint>()

        private val nextRandomAngle get() = randomF(-60f, 60f)

        val pin by lazy { RigidStatic().apply {
            translate(translate)
            attachShape(Shape(SphereGeometry(bodyRadius), bodyMaterial))
        } }

        private val colorMap = Cycler(listOf(
            MdColor.RED, MdColor.PINK, MdColor.PURPLE, MdColor.DEEP_PURPLE,
            MdColor.INDIGO, MdColor.BLUE, MdColor.LIGHT_BLUE, MdColor.CYAN, MdColor.TEAL, MdColor.GREEN,
            MdColor.LIGHT_GREEN, MdColor.LIME, MdColor.YELLOW, MdColor.AMBER, MdColor.ORANGE, MdColor.DEEP_ORANGE))
            .apply { index = 1 }

        // chaingenerator -
        // interface ChainGenerator {
        // firstNode
        // on edge
        // on node
        init {
            actors.add(pin)
            +pin.toMesh(Color.WHITE)
            var prevNode: RigidActor = pin

            for ( i in 0 until bodies) {
                val mass = (i + 1) * 5f
                val pos = MutableVec3f(0f, segmentLength, 0f)

                pos.rotate(nextRandomAngle, Vec3f.X_AXIS)
                pos.rotate(nextRandomAngle, Vec3f.Z_AXIS)

                val pong = RigidDynamic(mass).apply {
                    attachShape(Shape(SphereGeometry(bodyRadius), bodyMaterial))
                    position = add(prevNode.position, pos)
                    translate(translate)
                }

                actors.add(pong)
                +pong.toMesh(colorMap.next())

                joints.add(makeEdge(prevNode, pong))
                prevNode = pong
            }
        }

        fun start() = expose()
        fun stop(ctx: KoolContext) = dispose(ctx)

        fun expose() {
            actors.forEach { world.addActor(it) }
        }

        override fun dispose(ctx: KoolContext) {
            super.dispose(ctx)
            actors.forEach { world.removeActor(it); } // stop

            joints.forEach { it.dispose(ctx) }
            actors.forEach { it.dispose(ctx) }
            actors.clear()
            joints.clear()
        }
    }
}


fun Group.addDebugAxis() {
//    val x = lineMesh("x") { addLine(Vec3f.ZERO, Vec3f(1f, 0f, 0f), Color.RED) }
//    +x
//    +lineMesh("y") { addLine(Vec3f.ZERO, Vec3f(0f, 1f, 0f), Color.GREEN) }
//    +lineMesh("z") { addLine(Vec3f.ZERO, Vec3f(0f, 0f, 1f), Color.BLUE) }
//    onUpdate += {
//        x.transform.set(this@CommonRigidActor.transform)
//            this@group.setDirty()
//
//    }
}