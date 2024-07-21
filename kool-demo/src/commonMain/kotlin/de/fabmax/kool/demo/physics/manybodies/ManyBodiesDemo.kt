package de.fabmax.kool.demo.physics.manybodies

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.Platform
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.logI

class ManyBodiesDemo : DemoScene("Many Bodies") {

    private val ibl by hdriImage("${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png")
    private val physicsWorld = PhysicsWorld(mainScene)

    private val cubeInstances = MeshInstanceList(100000, Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS)
    private val physBoxes = mutableListOf<PhysBox>()

    override fun Scene.setupMainScene(ctx: KoolContext) {
        (camera as PerspectiveCamera).apply {
            clipNear = 1f
            clipFar = 1000f
        }

        defaultOrbitCamera().apply {
            setZoom(100.0, max = 500.0)
        }
        makeGround()

        addColorMesh(instances = cubeInstances) {
            generate {
                cube {  }
            }
            shader = KslPbrShader {
                vertices { isInstanced = true }
                color { instanceColor(Attribute.COLORS) }
                lightingCfg.imageBasedAmbientLight(ibl.irradianceMap)
                reflectionMap = ibl.reflectionMap
            }

            onUpdate {
                cubeInstances.clear()
                cubeInstances.addInstances(physBoxes.size) { buf ->
                    physBoxes.forEach { box ->
                        box.actor.transform.matrixF.putTo(buf)
                        box.color.putTo(buf)
                    }
                }
            }
        }
        addNode(Skybox.cube(ibl.reflectionMap, 2f))
        spawnPhysicsBoxes()
    }

    private fun spawnPhysicsBoxes() {
        val ny = if (KoolSystem.platform == Platform.JAVASCRIPT) 10 else 50

        for (y in 0..ny) {
            for (x in -12..12) {
                for (z in -12..12) {
                    val body = RigidDynamic(pose = MutableMat4f().translate(x * 2f, y * 2f + 10f, z * 2f))
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
                lightingCfg.imageBasedAmbientLight(ibl.irradianceMap)
                reflectionMap = ibl.reflectionMap
            }
        }

        val ground = RigidStatic().apply {
            attachShape(Shape(PlaneGeometry(), Physics.defaultMaterial))
            setRotation(Mat3f.rotation(90f.deg, Vec3f.Z_AXIS))
        }
        physicsWorld.addActor(ground)
    }

    override fun onRelease(ctx: KoolContext) {
        super.onRelease(ctx)
        physicsWorld.release()
    }

    private data class PhysBox(val actor: RigidDynamic, val color: MutableColor)
}