package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.PhongShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.InstancedLodController
import de.fabmax.kool.util.MeshInstanceList
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfFile
import kotlin.math.roundToInt

class InstanceDemo() : DemoScene("Instanced Drawing") {

    private var nBunnies = 10
    private var isLodColors = false
    private var isAutoRotate = true

    private val modelCenter = MutableVec3f()
    private var modelRadius = 1f
    private val lodController = InstancedLodController<BunnyInstance>()

    private val lods = mutableListOf(
            Lod(8, 10f, MutableColor(Color.MD_PURPLE)),
            Lod(32, 20f, MutableColor(Color.MD_RED)),
            Lod(128, 30f, MutableColor(Color.MD_AMBER)),
            Lod(500, 40f, MutableColor(Color.MD_LIME)),
            Lod(2000, 50f, MutableColor(Color.MD_GREEN)),
            Lod(10000, 1000f, MutableColor(Color.MD_BLUE))
    )

    override fun setupMainScene(ctx: KoolContext) = scene {
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

        ctx.assetMgr.launch {
            loadGltfFile("${Demo.modelBasePath}/bunny.gltf.gz")?.let { addLods(it) }
        }
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

                //shader = instanceColorPhongShader()
                shader = instanceColorPbrShader()

                isFrustumChecked = false
                lods[i].mesh = this
                instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS), lods[i].maxInsts)
                lodController.addLod(this, lods[i].maxDist)
            }
        }
        lodController.setupInstances()
    }

    private fun InstancedLodController<BunnyInstance>.setupInstances() {
        val colors = listOf(Color.WHITE.toLinear(), Color.MD_RED.toLinear(), Color.MD_PINK.toLinear(),
                Color.MD_PURPLE.toLinear(), Color.MD_DEEP_PURPLE.toLinear(), Color.MD_INDIGO.toLinear(),
                Color.MD_BLUE.toLinear(), Color.MD_CYAN.toLinear(), Color.MD_TEAL.toLinear(), Color.MD_GREEN.toLinear(),
                Color.MD_LIGHT_GREEN.toLinear(), Color.MD_LIME.toLinear(), Color.MD_YELLOW.toLinear(),
                Color.MD_AMBER.toLinear(), Color.MD_ORANGE.toLinear(), Color.MD_DEEP_ORANGE.toLinear(),
                Color.MD_BROWN.toLinear(), Color.MD_GREY.toLinear(), Color.MD_BLUE_GREY.toLinear()
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
            roughness = 0.3f
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

    private fun instanceColorPhongShader(): Shader {
        val cfg = PhongShader.PhongConfig().apply {
            albedoSource = Albedo.STATIC_ALBEDO
            isInstanced = true
        }
        val model = PhongShader.defaultPhongModel(cfg).apply {
            val ifInstColor: StageInterfaceNode
            vertexStage {
                ifInstColor = stageInterfaceNode("ifInstColor", instanceAttributeNode(Attribute.COLORS).output)
            }
            fragmentStage {
                findNodeByType<PhongMaterialNode>()!!.inAlbedo = ifInstColor.output
            }
        }
        return PhongShader(cfg, model)
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

        override fun addInstanceData(lod: Int, instanceList: MeshInstanceList, ctx: KoolContext) {
            instanceList.addInstance {
                put(instanceModelMat.matrix)
                if (isLodColors) {
                    put(lods[lod].color.array)
                } else {
                    put(color.array)
                }
            }
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
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
