package de.fabmax.kool.demo.physics.manybodies

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.logI

class ManyBodiesDemo : DemoScene("Many Bodies") {

    private lateinit var ibl: EnvironmentMaps
    private lateinit var physicsWorld: PhysicsWorld

    private val cubeInstances = MeshInstanceList(100000, Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS)
    private val physBoxes = mutableListOf<PhysBox>()

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        showLoadText("Loading IBL maps")
        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png")
        mainScene += Skybox.cube(ibl.reflectionMap, 2f)

        physicsWorld = PhysicsWorld()
        physicsWorld.registerHandlers(mainScene)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        (camera as PerspectiveCamera).apply {
            clipNear = 1f
            clipFar = 1000f
        }

        defaultOrbitCamera().apply {
            setZoom(100.0, max = 500.0)
        }
        makeGround()

        addColorMesh {
            instances = cubeInstances
            generate {
                cube {  }
            }
            shader = KslPbrShader {
                vertices { isInstanced = true }
                color { instanceColor(Attribute.COLORS) }
                imageBasedAmbientColor(ibl.irradianceMap)
                reflectionMap = ibl.reflectionMap
            }

            onUpdate {
                cubeInstances.clear()
                cubeInstances.addInstances(physBoxes.size) { buf ->
                    physBoxes.forEach { box ->
                        buf.put(box.actor.transform.array)
                        buf.put(box.color.array)
                    }
                }
            }
        }
        spawnPhysicsBoxes()
    }

    private fun spawnPhysicsBoxes() {
        val ny = if (KoolSystem.isJavascript) 10 else 50

        for (y in 0..ny) {
            for (x in -12..12) {
                for (z in -12..12) {
                    val body = RigidDynamic(pose = Mat4f().translate(x * 2f, y * 2f + 10f, z * 2f))
                    body.attachShape(Shape(BoxGeometry(Vec3f(1f, 1f, 1f)), Physics.defaultMaterial))
                    physicsWorld.addActor(body)
                    physBoxes += PhysBox(body, ColorGradient.JET_MD.getColor(y.toFloat(), 0f, ny.toFloat()).toLinear())
                }
            }
        }
        logI { "Spawned ${physBoxes.size} boxes" }
    }

    private fun Scene.makeGround() {
        addTextureMesh(isNormalMapped = true, name = "ground") {
            generate {
                isCastingShadow = false
                vertexModFun = {
                    texCoord.set(x / 10f, z / 10f)
                }
                grid {
                    sizeX = 1000f
                    sizeY = 1000f
                    stepsX = sizeX.toInt() / 10
                    stepsY = sizeY.toInt() / 10
                }
            }
            shader = KslPbrShader {
                color { constColor(Color.WHITE) }
                imageBasedAmbientColor(ibl.irradianceMap)
                reflectionMap = ibl.reflectionMap
            }
        }

        val ground = RigidStatic().apply {
            attachShape(Shape(PlaneGeometry(), Physics.defaultMaterial))
            setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
        }
        physicsWorld.addActor(ground)
    }

    private data class PhysBox(val actor: RigidDynamic, val color: MutableColor)
}