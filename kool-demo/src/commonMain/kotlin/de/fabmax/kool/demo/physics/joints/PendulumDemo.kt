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
import de.fabmax.kool.physics.joints.DistanceJoint
import de.fabmax.kool.physics.joints.Joint
import de.fabmax.kool.physics.joints.RevoluteJoint
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.PI

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

    private val joints = mutableListOf<Joint>()

    private val material = Material(0.0f)
    private lateinit var groundAlbedo: Texture2d
    private lateinit var groundNormal: Texture2d
    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        Physics.awaitLoaded()
        groundAlbedo = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        groundNormal = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")

        physicsWorld.registerHandlers(mainScene)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupCamera()
        lighting.singleLight {
            setDirectional(Vec3f(0.8f, -1.2f, 1f))
        }

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
        mainScene += physicsWorld.generatePendulum(joints = joints)
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

    private fun PhysicsWorld.generatePendulum(bodies: Int = 10, segmentLength: Float = 1.9f, joints: MutableList<Joint>) = group {
        val minAng = -90f
        val maxAng = 90f
        val pin = RigidStatic().apply {
            attachShape(Shape(SphereGeometry(0.2f), material))
        }

        addActor(pin)
        +pin.toMesh(Color.WHITE)
        var prevNode: RigidActor = pin

        for ( i in 0 until bodies) {
            val mass = (bodies - i) * 5f
            val pos = MutableVec3f(0f, segmentLength, 0f)

            pos.rotate(randomF(minAng, maxAng), Vec3f.X_AXIS)
            pos.rotate(randomF(minAng, maxAng), Vec3f.Z_AXIS)

            val pong = RigidDynamic(mass).apply {
                attachShape(Shape(SphereGeometry(0.2f), material))
                position = add(prevNode.position, pos)
            }

            addActor(pong)
            +pong.toMesh(colorMap.next())

            joints.add(DistanceJoint(prevNode, pong, Mat4f(), Mat4f()).apply {
                setMaxDistance(segmentLength)
                debugVisualize = true

            })
            prevNode = pong
        }
    }

    private val colorMap = Cycler(listOf(
            MdColor.RED, MdColor.PINK, MdColor.PURPLE, MdColor.DEEP_PURPLE,
            MdColor.INDIGO, MdColor.BLUE, MdColor.LIGHT_BLUE, MdColor.CYAN, MdColor.TEAL, MdColor.GREEN,
            MdColor.LIGHT_GREEN, MdColor.LIME, MdColor.YELLOW, MdColor.AMBER, MdColor.ORANGE, MdColor.DEEP_ORANGE))
        .apply { index = 1 }
}