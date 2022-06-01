package de.fabmax.kool.demo.physics.joints

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Cycler
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.*
import de.fabmax.kool.physics.joints.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.PI
import kotlin.math.ceil

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

    private val pendulums = mutableListOf<Pendulum>()
    private val springs by lazy {
        Springs(world = physicsWorld, origin = Vec3f(-20f, 0f, -20f))
    }
    private val chainRoller by lazy {
        ChainRoller(origin = Vec3f(20f, 10f, -20f))
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {

        setupCamera()
        setupLighting()

        physicsWorld.addActor(ground)

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
        val segmentLength = 2 * radius

        pendulums.add( Pendulum(physicsWorld,
            bodyMaterial = Material(0.5f),
            translate = Vec3f(y, 0f, 0f),
            segmentLength = segmentLength,
            makeEdge = { prevNode, node ->
                SphericalJoint(prevNode, node,
                    Mat4f(),
                    Mat4f(),
                ).also {
                    it.setSoftLimitCone((PI/4f).toFloat(), (PI/4f).toFloat(), 100f, 100f)
                }
            }
        ).also {
            mainScene += it
            it.start()
        } )
        y += dy

        pendulums.add( Pendulum(physicsWorld,
            translate = Vec3f(y, 0f, 0f),
            segmentLength = segmentLength,
            makeEdge = { p, n ->
                D6Joint(p, n,
                    Mat4f(),
                    Mat4f(),
                ).also {
                    it.motionX = D6JointMotion.Locked
                    it.motionY = D6JointMotion.Locked
                    it.motionZ = D6JointMotion.Locked
                    it.setDistanceLimit(1f, 100f, 100f)
                    it.projectionLinearTolerance = 5f
                }
            }
        ).also {
            it.start()
            mainScene += it
        })
        y += dy

        pendulums.add( Pendulum(physicsWorld,
            translate = Vec3f(y, 0f, 0f),
            segmentLength = 1.98f * radius,
            makeEdge = { p, n ->
                D6Joint(p, n,
                    Mat4f(),
                    Mat4f(),
                ).also {
                    it.motionX = D6JointMotion.Locked
                    it.motionY = D6JointMotion.Locked
                    it.motionZ = D6JointMotion.Locked
                    it.setDistanceLimit(1f, 1f, 1f)
                }
            }
        ).also {
            it.start()
            mainScene += it
        })
        y += dy

        //ctx.inputMgr.registerKeyListener(left, selectedIdx ++ % n)

        mainScene += springs
        springs.start()

        mainScene += chainRoller
        chainRoller.start(physicsWorld)
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
                   private val bodyMaterial: Material = Material(0f, 0f, 0.9f),
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

        init {
            actors.add(pin)
            +pin.toMesh(Color.WHITE)
            var prevNode: RigidActor = pin

            for ( i in 0 until bodies) {
                val mass = (i + 1) / bodies.toFloat()
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

            children.last().addDebugAxis()
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

    class Springs(val world: PhysicsWorld,
                  private val origin: Vec3f = Vec3f(0f, 0f, 0f),
                  private val dy: Float = 1.5f,
                  private val totalHeight: Float = 20f,
                  private val pinHeight: Float = 5f,
                  private val pinRadius: Float = 0.2f,
                  private val pinMass: Float = 0.2f,
                  private val bodyMaterial: Material = Material(0.5f),
                  val n: Int = 10) : Group("springs") {
        private val actors = mutableListOf<RigidActor>()
        private val joints = mutableListOf<Joint>()

        init {
            for ( st in 0 until n) {
                val realStiff = st / n.toFloat()

                for ( dm in 0 until n ) {
                    val realDamp = dm / n.toFloat()
                    val initPos = Mat4f().translate(origin).translate(- dy * (st+1), totalHeight, - dy * (dm + 1))
                    val basement = RigidStatic(initPos.rotate(90f, Vec3f.X_AXIS)).apply {
                        attachShape(Shape(BoxGeometry(Vec3f(0.5f, 0.5f, 0.2f)), bodyMaterial))
                    }
                    actors.add(basement)
                    +basement.toMesh(Color(0f, realStiff, realDamp))

                    val pinPos = Mat4f().set(initPos).translate(0f, 0f, pinHeight)

                    val pin = RigidDynamic(mass = pinMass, pose = pinPos).apply {
                        attachShape(Shape(SphereGeometry(pinRadius), bodyMaterial))
                    }

                    actors.add(pin)
                    +pin.toMesh(Color.GREEN)

                    joints.add( PrismaticJoint(basement, pin,
                        Mat4f().rotate(90f, Vec3f.Y_AXIS),
                        Mat4f().translate(0f, 0f, pinHeight).rotate(-90f, Vec3f.Y_AXIS)).apply {
                        setLimit(0f, totalHeight - 2f, realStiff, realDamp)
                    })
                }
            }
        }

        fun start() {
            actors.forEach { world.addActor(it) }
        }

        override fun dispose(ctx: KoolContext) {
            actors.forEach { world.removeActor(it) }
            actors.clear()
            joints.forEach { it.dispose(ctx) }
            joints.clear()
        }

    }

    class ChainRoller(val origin: Vec3f = Vec3f(10f, 10f, -10f),
                      val shaftRadius: Float = 1f,
                      val shaftWidth: Float = 5f,
                      val n: Int = 20, // between end and start of semi-circled parts
                      val chainSegmentLength: Float = 1f,         // 1 chain segment length
                      val chainSegmentSize: Float = 0.5f,
                      val chainMass: Float = 10f,
    ): Group() {
        val pin = RigidStatic(Mat4f().translate(origin)).apply {
            attachShape(Shape(BoxGeometry(Vec3f(0.3f))))
        }

        val disk = RigidDynamic(1f).apply {
            // TBD rework
            attachShape(Shape(
                geometry = CylinderGeometry(shaftWidth, shaftRadius),
                localPose = Mat4f().rotate(90f, Vec3f.Y_AXIS)
            ))
            attachShape(Shape(
                geometry = CylinderGeometry(1f, shaftRadius + chainSegmentSize * 5),
                localPose = Mat4f().translate(0f, 0f, shaftWidth/2f).rotate(90f, Vec3f.Y_AXIS)
            ))
            attachShape(Shape(
                geometry = CylinderGeometry(1f, shaftRadius + chainSegmentSize * 5),
                localPose = Mat4f().translate(0f, 0f, -shaftWidth/2f).rotate(-90f, Vec3f.Y_AXIS)
            ))
        }

        private var diskJoint: Joint? = RevoluteJoint(pin, disk,
            Mat4f().rotate(90f, Vec3f.Y_AXIS),
            Mat4f().rotate(-90f, Vec3f.Y_AXIS)).apply {
            enableAngularMotor(1f, 100f)
        }

        val chains = mutableListOf<RigidActor>()
        val joints = mutableListOf<Joint>()

        lateinit var world: PhysicsWorld

        init {
            + pin.toMesh(Color.RED)
            + disk.toMesh(Color.GREEN)

            // logo time
            val halfCircleChains = ceil(PI * shaftRadius / chainSegmentLength).toInt()
            val angleStep = (PI / halfCircleChains.toFloat()).toDeg().toFloat()
            if ( halfCircleChains < 3 ) {
                println("warning shaftRadius too small $shaftRadius")
            }

            for ( c in 0 .. halfCircleChains ) {
                val r = MutableVec3f( shaftRadius + chainSegmentSize, 0f, 0f)
                r.rotate(c * angleStep, Vec3f.Z_AXIS, r)
                chains.add(RigidDynamic(chainMass, Mat4f().translate(origin).translate(r)).apply {
                    attachShape(Shape(SphereGeometry(chainSegmentSize)))
                })
            }

            for ( c in 1 until n ) {
                val pos = Mat4f().translate(origin).translate( -shaftRadius - chainSegmentSize, -c * chainSegmentLength, 0f)
                chains.add(RigidDynamic(chainMass, pos).apply {
                    attachShape(Shape(SphereGeometry(chainSegmentSize)))
                })
            }

            for ( c in 0 .. halfCircleChains ) {
                val r = MutableVec3f( -shaftRadius - chainSegmentSize, 0f, 0f)
                r.rotate(c * angleStep, Vec3f.Z_AXIS, r)
                val pos = Mat4f().translate(origin).translate(0f, -n * chainSegmentLength, 0f).translate(r)
                chains.add(RigidDynamic(chainMass, pos).apply {
                    attachShape(Shape(SphereGeometry(chainSegmentSize)))
                })
            }

            for ( c in 1 until n ) {
                val pos = Mat4f().translate(origin).translate(shaftRadius + chainSegmentSize, -c * chainSegmentLength, 0f)
                chains.add(RigidDynamic(chainMass, pos).apply {
                    attachShape(Shape(SphereGeometry(chainSegmentSize)))
                })
            }

            chains.fold(chains.first()) { prev, current ->
                joints.add(DistanceJoint(prev, current, Mat4f(), Mat4f()).apply {
                    setMaxDistance(chainSegmentLength)
                    setMinDistance(chainSegmentLength)
                })
                current
            }
            joints.add(DistanceJoint(chains.first(), chains.last(), Mat4f(), Mat4f()).apply {
                setMaxDistance(chainSegmentLength)
                setMinDistance(chainSegmentLength)
            })

            chains.forEach {
                +it.toMesh(Color.LIGHT_YELLOW).also {
                    it.addDebugAxis()
                }
            }
        }

        fun start(world: PhysicsWorld) {
            this.world = world
            world.addActor(pin)
            world.addActor(disk)
            chains.forEach {
                world.addActor(it)
            }
        }

        override fun dispose(ctx: KoolContext) {
            if ( !::world.isInitialized ) return

            world.removeActor(pin)
            world.removeActor(disk)
            chains.forEach { world.removeActor(it) }
            chains.clear()
            joints.forEach { it.dispose(ctx) }
            joints.clear()
        }
    }
}

fun Node.parentGroup(): Group {
    return when {
        this is Group -> this
        this.parent != null -> this.parent!!.parentGroup()
        else -> throw RuntimeException("no parent group $this")
    }
}

fun Node.addDebugAxis() {
    with(parentGroup()) {
        val x = lineMesh("x") { addLine(Vec3f.ZERO, Vec3f(1f, 0f, 0f), Color.RED) }
        val y = lineMesh("y") { addLine(Vec3f.ZERO, Vec3f(0f, 1f, 0f), Color.GREEN) }
        val z = lineMesh("z") { addLine(Vec3f.ZERO, Vec3f(0f, 0f, 1f), Color.BLUE) }
        +x
        +y
        +z
        onUpdate += {
            x.modelMat.set(this@addDebugAxis.modelMat)
            y.modelMat.set(this@addDebugAxis.modelMat)
            z.modelMat.set(this@addDebugAxis.modelMat)
        }
    }
}