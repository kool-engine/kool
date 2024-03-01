package de.fabmax.kool.demo

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

class InstanceDemo : DemoScene("Instanced Drawing") {

    private val nBunnies = mutableStateOf(10)
    private val isLodColors = mutableStateOf(false)
    private val isAutoRotate = mutableStateOf(true)
    private val numInstances = mutableStateListOf(0)

    private val modelCenter = MutableVec3f()
    private var modelRadius = 1f
    private val lodController = InstancedLodController<BunnyInstance>()

    private lateinit var model: GltfFile

    private val lods = mutableListOf(
            Lod(8, 10f, MutableColor(MdColor.PURPLE.toLinear())),
            Lod(32, 20f, MutableColor(MdColor.RED.toLinear())),
            Lod(128, 30f, MutableColor(MdColor.AMBER.toLinear())),
            Lod(500, 40f, MutableColor(MdColor.LIME.toLinear())),
            Lod(2000, 50f, MutableColor(MdColor.GREEN.toLinear())),
            Lod(10000, 1000f, MutableColor(MdColor.BLUE.toLinear()))
    )

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        model = loadGltfFile("${DemoLoader.modelPath}/bunny.gltf.gz")
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        orbitCamera {
            camera.apply {
                clipNear = 1f
                clipFar = 500f
            }
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            setZoom(40.0, 1.0, 250.0)

            setMouseRotation(30f, -40f)

            onUpdate += {
                if (isAutoRotate.value) {
                    verticalRotation += Time.deltaT * 3f
                }
                numInstances.clear()
                for (i in lods.indices) {
                    numInstances += lodController.getInstanceCount(i)
                }
            }
        }

        lighting.singleDirectionalLight {
            setup(Vec3f(-1f))
            setColor(Color.WHITE, 5f)
        }

        addNode(lodController)
        addLods(model)
    }

    private fun addLods(model: GltfFile) {
        val instanceAttribs = listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS)
        for (i in model.scenes.indices) {
            val modelCfg = GltfLoadConfig(generateNormals = true, applyMaterials = false, addInstanceAttributes = instanceAttribs)
            model.makeModel(modelCfg, i).meshes.values.first().apply {
                geometry.forEach { v ->
                    v.position.mul(0.3f).add(Vec3f(0f, -1f, 0f))
                }
                geometry.rebuildBounds()

                if (i == 0) {
                    modelCenter.set(geometry.bounds.center)
                    modelRadius = geometry.bounds.max.distance(geometry.bounds.center)
                }

                shader = instanceColorPbrShader()

                isFrustumChecked = false
                lods[i].mesh = this
                lodController.addLod(this, lods[i].maxDist, lods[i].maxInsts)
            }
        }
        lodController.setupInstances()
    }

    private fun InstancedLodController<BunnyInstance>.setupInstances() {
        val colors = listOf(Color.WHITE.toLinear(), MdColor.RED.toLinear(), MdColor.PINK.toLinear(),
                MdColor.PURPLE.toLinear(), MdColor.DEEP_PURPLE.toLinear(), MdColor.INDIGO.toLinear(),
                MdColor.BLUE.toLinear(), MdColor.CYAN.toLinear(), MdColor.TEAL.toLinear(), MdColor.GREEN.toLinear(),
                MdColor.LIGHT_GREEN.toLinear(), MdColor.LIME.toLinear(), MdColor.YELLOW.toLinear(),
                MdColor.AMBER.toLinear(), MdColor.ORANGE.toLinear(), MdColor.DEEP_ORANGE.toLinear(),
                MdColor.BROWN.toLinear(), MdColor.GREY.toLinear(), MdColor.BLUE_GREY.toLinear()
        )

        instances.clear()
        val n = nBunnies.value
        val rand = Random(17)
        val off = (n - 1) * 0.5f
        for (x in 0 until n) {
            for (y in 0 until n) {
                for (z in 0 until n) {
                    val position = MutableVec3f((x - off) * 5f + randomF(-2f, 2f), (y - off) * 5f + randomF(-2f, 2f), (z - off) * 5f + randomF(-2f, 2f))
                    val rotAxis = MutableVec3f(randomF(-1f, 1f), randomF(-1f, 1f), randomF(-1f, 1f))
                    instances += BunnyInstance(position, rotAxis).apply {
                        this.color.set(colors[rand.randomI(colors.indices)].toLinear())
                        this.center.set(modelCenter)
                        this.radius = modelRadius
                    }
                }
            }
        }
    }

    private fun instanceColorPbrShader() = KslPbrShader {
        vertices { isInstanced = true }
        color { instanceColor(Attribute.COLORS) }
    }

    private class Lod(val maxInsts: Int, val maxDist: Float, val color: MutableColor) {
        var mesh: Mesh? = null
    }

    private inner class BunnyInstance(val position: Vec3f, rotAxis: Vec3f) : InstancedLodController.Instance<BunnyInstance>() {
        val rotSpeed = rotAxis.length() * 120f
        val rotAxis = rotAxis.normed()

        val color = MutableColor()

        override fun update(lodCtrl: InstancedLodController<BunnyInstance>, cam: Camera, ctx: KoolContext) {
            instanceModelMat
                    .setIdentity()
                    .translate(position)
                    .rotate((Time.gameTime.toFloat() * rotSpeed).deg, rotAxis)
            super.update(lodCtrl, cam, ctx)
        }

        override fun addInstanceData(lod: Int, buffer: Float32Buffer, ctx: KoolContext) {
            instanceModelMat.putTo(buffer)
            if (isLodColors.value) {
                lods[lod].color.putTo(buffer)
            } else {
                color.putTo(buffer)
            }
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuRow {
            Text("Bunnies") { labelStyle() }
            MenuSlider(
                nBunnies.use().toFloat(), 5f, 20f,
                txtFormat = {
                    val iValue = it.toInt()
                    "${iValue * iValue * iValue}"
                }
            ) { value ->
                val n = value.roundToInt()
                if (n != nBunnies.value) {
                    nBunnies.set(n)
                    lodController.setupInstances()
                }
            }
        }
        LabeledSwitch("Color by LOD", isLodColors)
        LabeledSwitch("Auto rotate view", isAutoRotate)

        Text("Statistics") { sectionTitleStyle() }
        MenuRow {
            Box(width = Grow.Std) {  }
            Text("# Instances") {
                labelStyle(Grow.Std)
                modifier.textAlign(AlignmentX.End)
            }
            Text("# Faces") {
                labelStyle(Grow.Std)
                modifier.textAlign(AlignmentX.End)
            }
        }
        numInstances.use().forEachIndexed { i, numInsts ->
            MenuRow {
                Text("LOD $i") {
                    labelStyle(Grow.Std)
                    if (isLodColors.value) {
                        modifier.textColor(lods[i].color.toSrgb())
                    }
                }
                Text("$numInsts") {
                    labelStyle(Grow.Std)
                    modifier.textAlign(AlignmentX.End)
                    if (isLodColors.value) {
                        modifier.textColor(lods[i].color.toSrgb())
                    }
                }
                Text("${numInsts * (lods[i].mesh?.geometry?.numPrimitives ?: 0)}") {
                    labelStyle(Grow.Std)
                    modifier.textAlign(AlignmentX.End)
                    if (isLodColors.value) {
                        modifier.textColor(lods[i].color.toSrgb())
                    }
                }
            }
        }
    }
}
