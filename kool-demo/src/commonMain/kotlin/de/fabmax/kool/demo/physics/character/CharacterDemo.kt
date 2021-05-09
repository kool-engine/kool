package de.fabmax.kool.demo.physics.character

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.character.CharacterController
import de.fabmax.kool.physics.character.CharacterControllerManager
import de.fabmax.kool.physics.character.CharacterProperties
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.geometry.SphereGeometry
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.pipeline.shading.unlitShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CharacterDemo : DemoScene("Character Demo") {

    private lateinit var ibl: EnvironmentMaps
    private lateinit var groundAlbedo: Texture2d
    private lateinit var groundNormal: Texture2d

    private lateinit var physicsWorld: PhysicsWorld
    private lateinit var charManager: CharacterControllerManager
    private lateinit var egoCharacter: CharacterController

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.envMapBasePath}/colorful_studio_1k.rgbe.png", this)
        groundAlbedo = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        groundNormal = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")

        Physics.awaitLoaded()
        physicsWorld = PhysicsWorld(mainScene)
        charManager = CharacterControllerManager(physicsWorld)
    }

    override fun dispose(ctx: KoolContext) {
        groundAlbedo.dispose()
        groundNormal.dispose()

        charManager.release()
        physicsWorld.release()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform()
        camera.setClipRange(0.5f, 500f)

        makeGround()
        spawnCharacter()
    }

    private fun spawnCharacter() {
        val charProps = CharacterProperties()
        egoCharacter = charManager.createController(charProps)
        egoCharacter.position = Vec3d(0.0, 4.0, 4.0)

        mainScene += makeCharModel(charProps)
    }

    private fun makeGround() {
        val groundPlane = RigidStatic()
        groundPlane.attachShape(Shape(PlaneGeometry()))
        groundPlane.setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
        physicsWorld.addActor(groundPlane)

        mainScene += textureMesh(isNormalMapped = true) {
            isCastingShadow = false
            generate {
                rotate(-90f, Vec3f.X_AXIS)
                rect {
                    size.set(250f, 250f)
                    origin.set(-size.x * 0.5f, -size.y * 0.5f, 0f)
                    generateTexCoords(25f)
                }
            }
            shader = pbrShader {
                useAlbedoMap(groundAlbedo)
                useNormalMap(groundNormal)
                useImageBasedLighting(ibl)
            }
        }

        val ball = RigidDynamic()
        ball.position = Vec3f(4f, 4f, 4f)
        ball.attachShape(Shape(SphereGeometry(0.75f)))
        physicsWorld.addActor(ball)
        mainScene += ball.toMesh(Color.MD_PINK)
    }

    private fun makeCharModel(charProps: CharacterProperties) = group {
        +lineMesh {
            val cr = Color.MD_RED
            val cg = Color.MD_GREEN
            val cb = Color.MD_BLUE
            val r = charProps.radius
            val h = charProps.height / 2
            for (i in 0 until 40) {
                val a0 = (i / 40f) * 2 * PI.toFloat()
                val a1 = ((i + 1) / 40f) * 2 * PI.toFloat()
                addLine(Vec3f(cos(a0) * r, h, sin(a0) * r), Vec3f(cos(a1) * r, h, sin(a1) * r), cg)
                addLine(Vec3f(cos(a0) * r, -h, sin(a0) * r), Vec3f(cos(a1) * r, -h, sin(a1) * r), cg)
            }

            for (i in 0 until 20) {
                val a0 = (i / 40f) * 2 * PI.toFloat()
                val a1 = ((i + 1) / 40f) * 2 * PI.toFloat()
                addLine(Vec3f(cos(a0) * r, sin(a0) * r + h, 0f), Vec3f(cos(a1) * r, sin(a1) * r + h, 0f), cr)
                addLine(Vec3f(cos(a0) * r, -sin(a0) * r - h, 0f), Vec3f(cos(a1) * r, -sin(a1) * r - h, 0f), cr)

                addLine(Vec3f(0f, sin(a0) * r + h, cos(a0) * r), Vec3f(0f, sin(a1) * r + h, cos(a1) * r), cb)
                addLine(Vec3f(0f, -sin(a0) * r - h, cos(a0) * r), Vec3f(0f, -sin(a1) * r - h, cos(a1) * r), cb)
            }

            addLine(Vec3f(-r, h, 0f), Vec3f(-r, -h, 0f), cr)
            addLine(Vec3f(r, h, 0f), Vec3f(r, -h, 0f), cr)
            addLine(Vec3f(0f, h, -r), Vec3f(0f, -h, -r), cb)
            addLine(Vec3f(0f, h, r), Vec3f(0f, -h, r), cb)

            shader = unlitShader {
                lineWidth = 2f
            }
        }

        onUpdate += {
            setIdentity()
            translate(egoCharacter.position)
        }
    }
}