package de.fabmax.kool.demo.tests

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.randomF
import de.fabmax.kool.math.randomInUnitSphere
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.Time
import kotlin.math.sin

class InstancingTest : DemoScene("Instancing") {

    private val numObjects = mutableStateOf(5000)
    private val drawInstanced = mutableStateOf(false)
    private val animateInstances = mutableStateOf(false)
    private val updateInstances = mutableStateOf(false)
    private val numInstancesPerMesh = mutableStateOf(10)

    private val updateTstate = mutableStateOf(0.0)
    private val recordTstate = mutableStateOf(0.0)
    private val drawTstate = mutableStateOf(0.0)

    private val objects = mutableListOf<Mesh>()
    private val meshInstances = mutableListOf<MeshInstance>()
    private val directShader = KslUnlitShader {
        color { vertexColor() }
        colorSpaceConversion = ColorSpaceConversion.AsIs
    }
    private val instancedShader = KslUnlitShader {
        vertices {
            modelMatrixComposition = listOf(ModelMatrixComposition.InstanceModelMat())
        }
        color { vertexColor() }
        colorSpaceConversion = ColorSpaceConversion.AsIs
    }

    private val axes = List(1000) { randomInUnitSphere() }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera(0f)
        camera.setClipRange(0.1f, 10000f)

        updateObjects()
        onUpdate {
            if (drawInstanced.value && (updateInstances.value || animateInstances.value)) {
                if (animateInstances.value) {
                    for (i in meshInstances.indices) {
                        val ax = axes[i % axes.size]
                        val ang = 90f.deg * Time.deltaT * sin(Time.gameTime + i).toFloat()
                        meshInstances[i].pose.rotate(ang, ax)
                    }
                }
                for (i in objects.indices) {
                    objects[i].instances?.clear()
                }
                for (i in meshInstances.indices) {
                    meshInstances[i].addInstance()
                }
            }
        }

        mainRenderPass.isProfileGpu = true

        var updateT = 0.0
        var recordT = 0.0
        var drawT = 0.0
        onUpdate {
            updateT = updateT * 0.9 + (mainRenderPass.tUpdate.inWholeNanoseconds / 1e6) * 0.1
            recordT = recordT * 0.9 + (mainRenderPass.tRecord.inWholeNanoseconds / 1e6) * 0.1
            drawT = drawT * 0.9 + (mainRenderPass.tGpu.inWholeNanoseconds / 1e6) * 0.1

            if (Time.frameCount % 5 == 0) {
                updateTstate.set(updateT)
                recordTstate.set(recordT)
                drawTstate.set(drawT)
            }
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuSlider2("Num objects", numObjects.use().toFloat(), 100f, 10000f, txtFormat = { it.toInt().toString() }) {
            numObjects.set(it.toInt())
        }
        LabeledSwitch("Animate instances", animateInstances)
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
            Text("${updateTstate.use().toString(2)} ms") { labelStyle(Grow.Std) }
        }
        MenuRow {
            Text("Scene recording:") { labelStyle(Grow.Std) }
            Text("${recordTstate.use().toString(2)} ms") { labelStyle(Grow.Std) }
        }
        MenuRow {
            Text("GPU time:") { labelStyle(Grow.Std) }
            Text("${drawTstate.use().toString(2)} ms") { labelStyle(Grow.Std) }
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
                    color = ColorGradient.JET_MD.getColor(i / numObjects.value.toFloat())
                    cube { size.set(0.3f, 0.3f, 0.3f) }
                }
                shader = directShader
                transform.translate(x.toFloat(), -30f, z.toFloat())

                onUpdate {
                    if (animateInstances.value) {
                        val ang = 90f.deg * Time.deltaT * sin(Time.gameTime + i).toFloat()
                        transform.rotate(ang, axes[i % axes.size])
                    }
                }
            }
        }
    }

    private fun makeObjectsInstanced() {
        var instances: MeshInstanceList<InstanceLayouts.ModelMat>? = null
        var insts = 0

        meshInstances.clear()

        for (i in 0 until numObjects.value) {
            val x = i % 100 - 50
            val z = -i / 100 - 30

            if (instances == null || insts >= numInstancesPerMesh.value) {
                instances = MeshInstanceList(InstanceLayouts.ModelMat, numInstancesPerMesh.value)
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

            meshInstances += MeshInstance(instances, MutableMat4f().translate(x.toFloat(), -30f, z.toFloat())).apply { addInstance() }
        }
    }

    class MeshInstance(val instances: MeshInstanceList<InstanceLayouts.ModelMat>, val pose: MutableMat4f) {
        fun addInstance() {
            instances.addInstance {
                set(it.modelMat, pose)
            }
        }
    }
}