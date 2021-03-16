package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.mesh.HalfEdgeMesh
import de.fabmax.kool.modules.mesh.ListEdgeHandler
import de.fabmax.kool.modules.mesh.simplification.simplify
import de.fabmax.kool.modules.mesh.simplification.terminateOnFaceCountRel
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.Label
import de.fabmax.kool.scene.ui.ToggleButton
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfModel
import kotlin.math.cos
import kotlin.math.sqrt

class SimplificationDemo : DemoScene("Simplification") {
    val models = mutableMapOf<String, IndexedVertexList>()

    val modelWireframe = LineMesh()

    var srcModel: IndexedVertexList
    val dispModel = Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS))
    var heMesh: HalfEdgeMesh

    var simplifcationGrade = 1f
    lateinit var autoRun: ToggleButton
    lateinit var facesValLbl: Label
    lateinit var vertsValLbl: Label
    lateinit var timeValLbl: Label

    var autoRotate = true

    init {
        dispModel.shader = pbrShader {
            albedoSource = Albedo.STATIC_ALBEDO
            albedo = Color.WHITE
            roughness = 0.15f
        }

        srcModel = makeCosGrid()
        models["cos"] = srcModel
        heMesh = HalfEdgeMesh(srcModel)
    }

    override fun lateInit(ctx: KoolContext) {
        ctx.assetMgr.launch {
            loadModel("${Demo.modelBasePath}/bunny.gltf.gz", 1f, Vec3f(0f, -3f, 0f))
            loadModel("${Demo.modelBasePath}/cow.gltf.gz", 1f, Vec3f.ZERO)
            loadModel("${Demo.modelBasePath}/teapot.gltf.gz", 1f, Vec3f(0f, -1.5f, 0f))
        }
        simplify()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform()

        lighting.lights.apply {
            clear()
            add(Light().setDirectional(Vec3f(-1f, -1f, 1f)).setColor(Color.MD_RED.mix(Color.WHITE, 0.25f).toLinear(), 2f))
            add(Light().setDirectional(Vec3f(1f, -1f, -1f)).setColor(Color.MD_CYAN.mix(Color.WHITE, 0.25f).toLinear(), 2f))
        }

        +group {
            +dispModel
            +modelWireframe

            onUpdate += {
                if (autoRotate) {
                    rotate(ctx.deltaT * 3f, Vec3f.Y_AXIS)
                }
            }
        }
    }

    fun simplify() {
        val pt = PerfTimer()
        dispModel.geometry.batchUpdate {
            dispModel.geometry.clear()
            dispModel.geometry.addGeometry(srcModel)

            heMesh = HalfEdgeMesh(dispModel.geometry, ListEdgeHandler())
            if (simplifcationGrade < 0.999f) {
                heMesh.simplify(terminateOnFaceCountRel(simplifcationGrade.toDouble()))
            }

            modelWireframe.geometry.batchUpdate {
                modelWireframe.clear()
                heMesh.generateWireframe(modelWireframe, Color.MD_LIME)
            }

            val time = pt.takeSecs()
            if (time > 0.5) {
                autoRun.isEnabled = false
            }
            facesValLbl.text = "${heMesh.faceCount}"
            vertsValLbl.text = "${heMesh.vertCount}"
            timeValLbl.text = "${time.toString(2)} s"
        }
    }

    private suspend fun AssetManager.loadModel(name: String, scale: Float, offset: Vec3f) {
        val modelCfg = GltfFile.ModelGenerateConfig(generateNormals = true, applyMaterials = false)
        loadGltfModel(name, modelCfg)?.let { model ->
            val mesh = model.meshes.values.first()
            val geometry = mesh.geometry
            for (i in 0 until geometry.numVertices) {
                geometry.vertexIt.index = i
                geometry.vertexIt.position.scale(scale).add(offset)
            }
            val modelKey = name.substring(name.lastIndexOf('/') + 1 until name.lastIndexOf(".gltf.gz"))
            models[modelKey] = geometry
            logD { "loaded: $name, bounds: ${models[name]?.bounds}" }
        }
    }

    private fun makeCosGrid(): IndexedVertexList {
        val builder = MeshBuilder(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS))
        builder.color = Color.MD_RED
        builder.grid {
            sizeX = 10f
            sizeY = 10f
            stepsX = 30
            stepsY = 30

            heightFun = { x, y ->
                val fx = (x.toFloat() / stepsX - 0.5f) * 10f
                val fy = (y.toFloat() / stepsY - 0.5f) * 10f
                cos(sqrt(fx*fx + fy*fy)) * 2
            }
        }
        return builder.geometry
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        section("Model") {
            button("Cow") {
                srcModel = models["cow"] ?: srcModel
                simplify()
            }
            button("Teapot") {
                srcModel = models["teapot"] ?: srcModel
                simplify()
            }
            button("Bunny") {
                srcModel = models["bunny"] ?: srcModel
                simplify()
            }
            button("Cosine Grid") {
                srcModel = models["cos"] ?: srcModel
                simplify()
            }
        }
        section("Scene") {
            val tbSolid = toggleButton("Draw Solid", dispModel.isVisible) { }
            val tbWireframe = toggleButton("Draw Wireframe", modelWireframe.isVisible) { }
            tbSolid.onStateChange += {
                dispModel.isVisible = isEnabled
                if (!isEnabled && !tbWireframe.isEnabled) {
                    tbWireframe.isEnabled = true
                }
            }
            tbWireframe.onStateChange += {
                modelWireframe.isVisible = isEnabled
                if (!isEnabled && !tbSolid.isEnabled) {
                    tbSolid.isEnabled = true
                }
            }
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled}
        }
        section("Simplification") {
            sliderWithValue("Ratio:", 100f, 1f, 100f) {
                simplifcationGrade = value / 100f
                if (autoRun.isEnabled) {
                    simplify()
                }
            }
            button("Update Mesh") { simplify() }
            autoRun = toggleButton("Auto Update", true) { }
            facesValLbl = textWithValue("Faces:", "")
            vertsValLbl = textWithValue("Vertices:", "")
            timeValLbl = textWithValue("Time:", "")
        }
    }
}