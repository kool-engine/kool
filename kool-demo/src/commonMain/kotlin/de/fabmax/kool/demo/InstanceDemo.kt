package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.shadermodel.PbrMaterialNode
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import kotlin.math.roundToInt

class InstanceDemo : DemoScene("Instanced Drawing") {

    private var nBunnies = 10
    private var isLodColors = false
    private var isAutoRotate = true

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

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        model = loadGltfFile("${Demo.modelPath}/bunny.gltf.gz")!!
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        +orbitInputTransform {
            +camera.apply {
                this as PerspectiveCamera
                clipNear = 1f
                clipFar = 500f
            }
            minZoom = 1.0
            maxZoom = 250.0
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            zoom = 40.0

            setMouseRotation(30f, -40f)

            onUpdate += {
                if (isAutoRotate) {
                    verticalRotation += it.deltaT * 3f
                }
            }
        }

        lighting.singleLight {
            setDirectional(Vec3f(-1f))
            setColor(Color.WHITE, 5f)
        }

        +lodController
        addLods(model)
    }

    private fun addLods(model: GltfFile) {
        for (i in model.scenes.indices) {
            val modelCfg = GltfFile.ModelGenerateConfig(generateNormals = true, applyMaterials = false)
            val mesh = model.makeModel(modelCfg, i).meshes.values.first()
            mesh.apply {
                geometry.forEach { v ->
                    v.position.scale(0.3f).add(Vec3f(0f, -1f, 0f))
                }
                geometry.rebuildBounds()

                if (i == 0) {
                    modelCenter.set(geometry.bounds.center)
                    modelRadius = geometry.bounds.max.distance(geometry.bounds.center)
                }

                shader = instanceColorPbrShader()

                isFrustumChecked = false
                lods[i].mesh = this
                instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS), lods[i].maxInsts)
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
        val rand = Random(17)
        val off = (nBunnies - 1) * 0.5f
        for (x in 0 until nBunnies) {
            for (y in 0 until nBunnies) {
                for (z in 0 until nBunnies) {
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

    private fun instanceColorPbrShader(): Shader {
        val cfg = PbrMaterialConfig().apply {
            albedoSource = Albedo.STATIC_ALBEDO
            isInstanced = true
            roughness = 0.5f
        }
        val model = PbrShader.defaultPbrModel(cfg).apply {
            val ifInstColor: StageInterfaceNode
            vertexStage {
                ifInstColor = stageInterfaceNode("ifInstColor", instanceAttributeNode(Attribute.COLORS).output)
            }
            fragmentStage {
                findNodeByType<PbrMaterialNode>()!!.inAlbedo = ifInstColor.output
            }
        }
        return PbrShader(cfg, model)
    }

    private class Lod(val maxInsts: Int, val maxDist: Float, val color: MutableColor) {
        var mesh: Mesh? = null
    }

    private inner class BunnyInstance(val position: Vec3f, rotAxis: Vec3f) : InstancedLodController.Instance<BunnyInstance>() {
        val rotSpeed = rotAxis.length() * 120f
        val rotAxis = rotAxis.norm(MutableVec3f())

        val color = MutableColor()

        override fun update(lodCtrl: InstancedLodController<BunnyInstance>, cam: Camera, ctx: KoolContext) {
            instanceModelMat
                    .setIdentity()
                    .translate(position)
                    .rotate(ctx.time.toFloat() * rotSpeed, rotAxis)
            super.update(lodCtrl, cam, ctx)
        }

        override fun addInstanceData(lod: Int, buffer: Float32Buffer, ctx: KoolContext) {
            buffer.put(instanceModelMat.matrix)
            if (isLodColors) {
                buffer.put(lods[lod].color.array)
            } else {
                buffer.put(color.array)
            }
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi {
        section("Scene") {
            val bunnyTxtFormat: ((Float) -> String) = {
                val n = it.roundToInt()
                "${n * n * n}"
            }
            sliderWithValue("Bunnies:", nBunnies.toFloat(), 5f, 20f, textFormat = bunnyTxtFormat) {
                val n = value.roundToInt()
                if (n != nBunnies) {
                    nBunnies = n
                    lodController.setupInstances()
                }
            }
            toggleButton("Color by LOD", isLodColors) { isLodColors = isEnabled }
            toggleButton("Auto Rotate", isAutoRotate) { isAutoRotate = isEnabled }
        }
        section("Info") {
            for (i in lods.indices) {
                textWithValue("LOD $i:", "").apply {
                    onUpdate += {
                        val cnt = lodController.getInstanceCount(i)
                        val tris = cnt * (lods[i].mesh?.geometry?.numPrimitives ?: 0)
                        text = "$cnt insts / $tris tris"
                    }
                }
            }
        }
    }
}
