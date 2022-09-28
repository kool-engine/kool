package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.mesh.HalfEdgeMesh
import de.fabmax.kool.modules.mesh.ListEdgeHandler
import de.fabmax.kool.modules.mesh.simplification.simplify
import de.fabmax.kool.modules.mesh.simplification.terminateOnFaceCountRel
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.logD
import kotlin.math.cos
import kotlin.math.sqrt

class SimplificationDemo : DemoScene("Simplification") {
    private val models = mutableListOf<DemoModel>()

    private val activeModel: MutableStateValue<DemoModel>
    private var heMesh: HalfEdgeMesh
    private val dispModel = Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS))
    private val modelWireframe = LineMesh()

    private val simplifcationRatio = mutableStateOf(1f)
    private val isAutoSimplify = mutableStateOf(true)
    private val isAutoRotate = mutableStateOf(true)
    private val isSolidVisible = mutableStateOf(true).onChange { dispModel.isVisible = it }
    private val isWireframeVisible = mutableStateOf(true).onChange { modelWireframe.isVisible = it }

    private val simplifiedNumFaces = mutableStateOf(0)
    private val simplifiedNumVerts = mutableStateOf(0)
    private val simplificationTime = mutableStateOf(0.0)

    private class DemoModel(val name: String, val geometry: IndexedVertexList) {
        override fun toString() = name
    }

    init {
        dispModel.shader = pbrShader {
            albedoSource = Albedo.STATIC_ALBEDO
            albedo = Color.WHITE
            roughness = 0.15f
        }

        val cosModel = DemoModel("Cosine grid", makeCosGrid())
        activeModel = mutableStateOf(cosModel)
        heMesh = HalfEdgeMesh(activeModel.value.geometry)
        models += cosModel
    }

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        loadModel("Bunny", "${DemoLoader.modelPath}/bunny.gltf.gz", 1f, Vec3f(0f, -3f, 0f))
        loadModel("Cow", "${DemoLoader.modelPath}/cow.gltf.gz", 1f, Vec3f.ZERO)
        loadModel("Teapot", "${DemoLoader.modelPath}/teapot.gltf.gz", 1f, Vec3f(0f, -1.5f, 0f))
        runSimplification(1f)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform()

        lighting.lights.apply {
            clear()
            add(Light().setDirectional(Vec3f(-1f, -1f, 1f)).setColor(MdColor.RED.mix(Color.WHITE, 0.25f).toLinear(), 2f))
            add(Light().setDirectional(Vec3f(1f, -1f, -1f)).setColor(MdColor.CYAN.mix(Color.WHITE, 0.25f).toLinear(), 2f))
        }

        +group {
            +dispModel
            +modelWireframe

            onUpdate += {
                if (isAutoRotate.value) {
                    rotate(ctx.deltaT * 3f, Vec3f.Y_AXIS)
                }
            }
        }
    }

    private fun runSimplification(ratio: Float) {
        val pt = PerfTimer()
        dispModel.geometry.batchUpdate {
            dispModel.geometry.clear()
            dispModel.geometry.addGeometry(activeModel.value.geometry)

            heMesh = HalfEdgeMesh(dispModel.geometry, ListEdgeHandler())
            if (ratio < 0.999f) {
                heMesh.simplify(terminateOnFaceCountRel(ratio.toDouble()))
            }

            modelWireframe.geometry.batchUpdate {
                modelWireframe.clear()
                heMesh.generateWireframe(modelWireframe, MdColor.LIME)
            }

            val time = pt.takeSecs()
            simplificationTime.set(time)
            simplifiedNumVerts.set(heMesh.vertCount)
            simplifiedNumFaces.set(heMesh.faceCount)
            if (time > 0.5) {
                isAutoSimplify.set(false)
            }
        }
    }

    private suspend fun AssetManager.loadModel(name: String, path: String, scale: Float, offset: Vec3f) {
        val modelCfg = GltfFile.ModelGenerateConfig(generateNormals = true, applyMaterials = false)
        loadGltfModel(path, modelCfg)?.let { model ->
            val mesh = model.meshes.values.first()
            val geometry = mesh.geometry
            for (i in 0 until geometry.numVertices) {
                geometry.vertexIt.index = i
                geometry.vertexIt.position.scale(scale).add(offset)
            }
            logD { "loaded: $name, bounds: ${geometry.bounds}" }
            models += DemoModel(name, geometry)
        }
    }

    private fun makeCosGrid(): IndexedVertexList {
        val builder = MeshBuilder(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS))
        builder.color = MdColor.RED
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

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuRow {
            Text("Model") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(models)
                    .selectedIndex(models.indexOf(activeModel.use()))
                    .onItemSelected {
                        activeModel.set(models[it])
                        runSimplification(if (isAutoSimplify.value) simplifcationRatio.value else 1f)
                    }
            }
        }
        MenuRow {
            Text("Ratio") { labelStyle() }
            MenuSlider(simplifcationRatio.use(), 0.01f, 1f, { "${(it * 100).toInt()} %" }) {
                simplifcationRatio.set(it)
                if (isAutoSimplify.value) {
                    runSimplification(simplifcationRatio.value)
                }
            }
        }
        Button("Simplify Mesh") {
            modifier
                .alignX(AlignmentX.Center)
                .width(Grow.Std)
                .margin(horizontal = 16.dp, vertical = 24.dp)
                .onClick {
                    runSimplification(simplifcationRatio.value)
                }
        }

        Text("Options") { sectionTitleStyle() }
        MenuRow { LabeledSwitch("Auto simplify mesh", isAutoSimplify) }
        MenuRow { LabeledSwitch("Draw solid", isSolidVisible) }
        MenuRow { LabeledSwitch("Draw wireframe", isWireframeVisible) }
        MenuRow { LabeledSwitch("Auto rotate view", isAutoRotate) }

        Text("Statistics") { sectionTitleStyle() }
        MenuRow {
            Text("Faces") { labelStyle(Grow.Std) }
            Text("${simplifiedNumFaces.use()}   /") {
                labelStyle()
                modifier.textAlignX(AlignmentX.End)
            }
            Text("${activeModel.value.geometry.numPrimitives}") {
                labelStyle()
                modifier
                    .width(64.dp)
                    .textAlignX(AlignmentX.End)
            }
        }
        MenuRow {
            Text("Vertices") { labelStyle(Grow.Std) }
            Text("${simplifiedNumVerts.use()}   /") {
                labelStyle()
                modifier.textAlignX(AlignmentX.End)
            }
            Text("${activeModel.value.geometry.numVertices}") {
                labelStyle()
                modifier
                    .width(64.dp)
                    .textAlignX(AlignmentX.End)
            }
        }
        MenuRow {
            Text("Time") { labelStyle(Grow.Std) }
            Text("${simplificationTime.use().toString(2)} s") { labelStyle() }
        }

    }
}