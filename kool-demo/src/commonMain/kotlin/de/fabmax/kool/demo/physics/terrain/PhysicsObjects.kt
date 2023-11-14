package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.scene.LineMesh
import de.fabmax.kool.scene.Scene

class PhysicsObjects(mainScene: Scene, terrain: Terrain, trees: Trees, ctx: KoolContext) {

    val world: PhysicsWorld

    val ground: RigidStatic
    val playerController: PlayerController
    val chainBridge: ChainBridge
    val boxes = mutableListOf<RigidDynamic>()
    private val boxInitPoses = mutableListOf<Mat4f>()

    val debugLines = LineMesh().apply { isVisible = false }

    init {
        // create a new physics world
        // CCD is recommended when using height fields, to avoid objects tunneling through the ground
        world = PhysicsWorld(mainScene, isContinuousCollisionDetection = true)

        // use constant time step for more stable bridge behavior
        world.simStepper = ConstantPhysicsStepperSync()

        world.addActor(terrain.terrainBody)

        trees.trees.asSequence().flatMap { it.instances }.forEach {
            world.addActor(it.physicsBody)
        }

        // put another infinitely large ground plane below terrain to catch stuff which falls of the edge of the world
        ground = RigidStatic()
        ground.attachShape(Shape(PlaneGeometry(), Physics.defaultMaterial))
        ground.position = Vec3f(0f, -35f, 0f)
        ground.setRotation(MutableMat3f().rotate(90f.deg, Vec3f.Z_AXIS))
        world.addActor(ground)

        // create a chain bridge, the player can walk across
        chainBridge = ChainBridge(world)

        // spawn a few dynamic boxes, the player can interact with
        spawnBoxes()

        // spawn player
        playerController = PlayerController(this, mainScene, ctx).apply {
            // set spawn position
            controller.position = Vec3d(-146.5, 47.8, -89.0)
        }

        world.onPhysicsUpdate += { timeStep ->
            playerController.onPhysicsUpdate(timeStep)
        }
    }

    private fun spawnBoxes() {
        val n = 10
        val boxSize = 2f
        for (x in -n..n) {
            for (z in -n..n) {
                val shape = BoxGeometry(Vec3f(boxSize))
                val body = RigidDynamic(100f)
                body.tags["isBox"] = true
                body.attachShape(Shape(shape, Physics.defaultMaterial))
                body.position = Vec3f(x * 5.5f, 100f, z * 5.5f)
                world.addActor(body)
                boxes += body

                boxInitPoses += MutableMat4f().set(body.transform.matrixF)
            }
        }
    }

    fun respawnBoxes() {
        boxes.forEachIndexed { i, body ->
            body.setTransform(boxInitPoses[i])
            body.linearVelocity = Vec3f.ZERO
            body.angularVelocity = Vec3f.ZERO
        }
    }

    fun release() {
        world.release()
        playerController.release()
    }
}