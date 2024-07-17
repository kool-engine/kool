package de.fabmax.kool.demo.tests

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time

class InstancingTest : DemoScene("Instancing") {

    private val numObjects = mutableStateOf(5000)
    private val drawInstanced = mutableStateOf(false)
    private val updateInstances = mutableStateOf(false)
    private val numInstancesPerMesh = mutableStateOf(10)

    private val objects = mutableListOf<Mesh>()
    private val meshInstances = mutableListOf<MeshInstance>()
    private val directShader = KslUnlitShader {
        color { vertexColor() }
        colorSpaceConversion = ColorSpaceConversion.AsIs
    }
    private val instancedShader = KslUnlitShader {
        vertices {
            isInstanced = true
            modelMatrixComposition = listOf(ModelMatrixComposition.INSTANCE_MODEL_MAT)
        }
        color { vertexColor() }
        colorSpaceConversion = ColorSpaceConversion.AsIs
    }

    private val profilingScene = ProfilingScene()

    init {
        mainScene = profilingScene
        scenes.clear()
        scenes.add(mainScene)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera(0f)
        camera.setClipRange(0.1f, 10000f)

        updateObjects()

        onUpdate {
            if (drawInstanced.value && updateInstances.value) {
                for (i in objects.indices) {
                    objects[i].instances?.clear()
                }
                for (i in meshInstances.indices) {
                    meshInstances[i].addInstance()
                }
            }
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuSlider2("Num objects", numObjects.use().toFloat(), 100f, 10000f, txtFormat = { it.toInt().toString() }) {
            numObjects.set(it.toInt())
        }
        LabeledSwitch("Draw instanced", drawInstanced) {
            updateObjects()
        }
        if (drawInstanced.use()) {
            MenuSlider2("Num instances per mesh", numInstancesPerMesh.use().toFloat(), 1f, 100f, txtFormat = { it.toInt().toString() }) {
                numInstancesPerMesh.set(it.toInt())
            }
            LabeledSwitch("Update instances each frame", updateInstances) { }
        }

        MenuRow {
            Button("Apply") {
                modifier
                    .width(Grow.Std)
                    .onClick { updateObjects() }
            }
        }

        MenuRow {
            Text("Update scene:") { labelStyle(Grow.Std) }
            Text("${profilingScene.updateTstate.use().toString(2)} ms") { labelStyle(Grow.Std) }
        }
        MenuRow {
            Text("Draw scene:") { labelStyle(Grow.Std) }
            Text("${profilingScene.drawTstate.use().toString(2)} ms") { labelStyle(Grow.Std) }
        }
    }

    private fun updateObjects() {
        for (i in objects.indices.reversed()) {
            mainScene.removeNode(objects[i])
        }
        objects.forEach { it.release() }
        objects.clear()

        if (drawInstanced.value) {
            makeObjectsInstanced()
        } else {
            makeObjectsDirect()
        }
    }

    private fun makeObjectsDirect() {
        for (i in 0 until numObjects.value) {
            val x = i % 100 - 50
            val z = -i / 100 - 30

            objects += mainScene.addColorMesh {
                isFrustumChecked = false
                generate {
                    color = MdColor.RED
                    cube {
                        origin.set(x.toFloat(), -30f, z.toFloat())
                        size.set(0.3f, 0.3f, 0.3f)
                    }
                }
                shader = directShader
            }
        }
    }

    private fun makeObjectsInstanced() {
        var instances: MeshInstanceList? = null
        var insts = 0

        meshInstances.clear()

        for (i in 0 until numObjects.value) {
            val x = i % 100 - 50
            val z = -i / 100 - 30

            if (instances == null || insts >= numInstancesPerMesh.value) {
                instances = MeshInstanceList(numInstancesPerMesh.value, Attribute.INSTANCE_MODEL_MAT)
                objects += mainScene.addColorMesh(instances = instances) {
                    generate {
                        color = Color.Hsv(randomF(0f, 360f), randomF(0.5f, 1f), randomF(0.5f, 1f)).toSrgb()
                        cube { size.set(0.3f, 0.3f, 0.3f) }
                    }
                    shader = instancedShader
                }
                insts = 0
            }
            insts++

            meshInstances += MeshInstance(instances, Mat4f.translation(x.toFloat(), -30f, z.toFloat())).apply { addInstance() }
        }
    }

    class MeshInstance(val instances: MeshInstanceList, val pose: Mat4f) {
        fun addInstance() {
            instances.addInstance {
                pose.putTo(this)
            }
        }
    }

    private class ProfilingScene : Scene() {
        val updateTstate = mutableStateOf(0.0)
        val drawTstate = mutableStateOf(0.0)

        var updateT = 0.0
        var drawT = 0.0

        override fun renderScene(ctx: KoolContext) {
            val t = Time.precisionTime
            super.renderScene(ctx)
            val p = (Time.precisionTime - t)
            updateT = updateT * 0.9 + p * 1000.0 * 0.1
            drawT = drawT * 0.9 + sceneDrawTime * 1000.0 * 0.1

            if (Time.frameCount % 5 == 0) {
                updateTstate.set(updateT)
                drawTstate.set(drawT)
            }
        }
    }
}