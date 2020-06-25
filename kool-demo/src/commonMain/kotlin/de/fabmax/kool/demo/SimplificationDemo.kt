package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.mesh.HalfEdgeMesh
import de.fabmax.kool.modules.mesh.ListEdgeHandler
import de.fabmax.kool.modules.mesh.simplification.simplify
import de.fabmax.kool.modules.mesh.simplification.terminateOnFaceCountRel
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.gltf.loadGltfModel
import kotlin.math.cos
import kotlin.math.sqrt

fun simplificationDemo(ctx: KoolContext): List<Scene> {
    return SimplificationDemo(ctx).scenes
}

class SimplificationDemo(ctx: KoolContext) {
    val simplificationScene: Scene
    val scenes = mutableListOf<Scene>()
    val models = mutableMapOf<String, IndexedVertexList>()
    val loadingModels = mutableSetOf<String>()

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
        dispModel.pipelineLoader = PbrShader(PbrShader.PbrConfig().apply { albedoSource = Albedo.STATIC_ALBEDO }).apply {
            albedo = Color.WHITE
            roughness = 0.15f
        }

        srcModel = makeCosGrid()
        models["cos"] = srcModel
        heMesh = HalfEdgeMesh(srcModel)

        loadModel("${Demo.modelBasePath}/bunny.gltf.gz", 1f, Vec3f(0f, -3f, 0f), ctx)
        loadModel("${Demo.modelBasePath}/cow.gltf.gz", 1f, Vec3f.ZERO, ctx)
        loadModel("${Demo.modelBasePath}/teapot.gltf.gz", 1f, Vec3f(0f, -1.5f, 0f), ctx)


        simplificationScene = mainScene(ctx)
        scenes += simplificationScene
        scenes += menu(ctx)

        simplify()
    }

    private fun mainScene(ctx: KoolContext) = scene {
        defaultCamTransform()

        lighting.lights.apply {
            clear()
            add(Light().setDirectional(Vec3f(-1f, -1f, 1f)).setColor(Color.MD_RED.mix(Color.WHITE, 0.25f).toLinear(), 2f))
            add(Light().setDirectional(Vec3f(1f, -1f, -1f)).setColor(Color.MD_CYAN.mix(Color.WHITE, 0.25f).toLinear(), 2f))
        }

        +transformGroup {
            +dispModel
            +modelWireframe

            onUpdate += { _, _ ->
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

    private fun loadModel(name: String, scale: Float, offset: Vec3f, ctx: KoolContext) {
        loadingModels += name
        ctx.assetMgr.loadGltfModel(name) { model ->
            if (model != null) {
                val mesh = model.makeModel(generateNormals = true, applyMaterials = false).meshes.values.first()
                val geometry = mesh.geometry
                for (i in 0 until geometry.numVertices) {
                    geometry.vertexIt.index = i
                    geometry.vertexIt.position.scale(scale).add(offset)
                }
                val modelKey = name.substring(name.lastIndexOf('/') + 1 until name.lastIndexOf(".gltf.gz"))
                models[modelKey] = geometry
                loadingModels -= name
                logD { "loaded: $name, bounds: ${models[name]?.bounds}" }
            }
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

    fun menu(ctx: KoolContext): Scene = uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-450f), dps(-705f), zero())
            layoutSpec.setSize(dps(330f), dps(585f), full())

            var y = -40f
            +label("lights") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                text = "Model"
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +button("Cow") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                onClick += { _,_,_ ->
                    srcModel = models["cow"] ?: srcModel
                    simplify()
                }
            }
            y -= 35f
            +button("Teapot") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                onClick += { _,_,_ ->
                    srcModel = models["teapot"] ?: srcModel
                    simplify()
                }
            }
            y -= 35f
            +button("Bunny") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                onClick += { _,_,_ ->
                    srcModel = models["bunny"] ?: srcModel
                    simplify()
                }
            }
            y -= 35f
            +button("Cosine Grid") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                onClick += { _,_,_ ->
                    srcModel = models["cos"] ?: srcModel
                    simplify()
                }
            }
            y -= 45f
            val tbDrawSolid = toggleButton("Draw Solid") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                isEnabled = dispModel.isVisible
            }
            y -= 35f
            val tbDrawWireframe = toggleButton("Draw Wireframe") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                isEnabled = modelWireframe.isVisible
            }
            tbDrawSolid.onStateChange += {
                dispModel.isVisible = isEnabled
                if (!isEnabled && !tbDrawWireframe.isEnabled) {
                    tbDrawWireframe.isEnabled = true
                }
            }
            tbDrawWireframe.onStateChange += {
                modelWireframe.isVisible = isEnabled
                if (!isEnabled && !tbDrawSolid.isEnabled) {
                    tbDrawSolid.isEnabled = true
                }
            }
            +tbDrawSolid
            +tbDrawWireframe

            y -= 35f
            +toggleButton("Auto Rotate") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                isEnabled = autoRotate
                onStateChange += { autoRotate = isEnabled}
            }

            y -= 40f
            +label("Simplification") {
                layoutSpec.setOrigin(zero(), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }



            y -= 35f
            +label("Ratio:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
            }
            val faceCntVal = label("faceCntVal") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = "100 %"
            }
            +faceCntVal
            y -= 25f
            +slider("faceCnt") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                setValue(0.01f, 1f, 1f)
                onValueChanged += { value ->
                    faceCntVal.text = "${(value * 100f).toString(0)} %"
                    simplifcationGrade = value
                    if (autoRun.isEnabled) {
                        simplify()
                    }
                }
            }

            y -= 35f
            +button("Update Mesh") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                onClick += { _,_,_ ->
                    simplify()
                }
            }
            y -= 35f
            autoRun = toggleButton("Auto Update") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                isEnabled = true
            }
            +autoRun
            y -= 35f
            +label("Faces:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
            }
            facesValLbl = label("facesValLbl") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = ""
            }
            +facesValLbl
            y -= 35f
            +label("Vertices:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
            }
            vertsValLbl = label("verticesValLbl") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = ""
            }
            +vertsValLbl
            y -= 35f
            +label("Time:") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
            }
            timeValLbl = label("timeValLbl") {
                layoutSpec.setOrigin(dps(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(25f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                text = ""
            }
            +timeValLbl
        }
    }
}