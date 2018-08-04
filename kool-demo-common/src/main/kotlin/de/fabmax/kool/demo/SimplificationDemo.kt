package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.mesh.HalfEdgeMesh
import de.fabmax.kool.modules.mesh.simplification.MeshSimplifier
import de.fabmax.kool.modules.mesh.simplification.terminateOnFaceCountRel
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.serialization.loadMesh
import kotlin.math.cos
import kotlin.math.sqrt

fun simplificationDemo(ctx: KoolContext): List<Scene> {
    return SimplificationDemo(ctx).scenes
}

class SimplificationDemo(ctx: KoolContext) {
    val simplificationScene: Scene
    val scenes = mutableListOf<Scene>()
    val models = mutableMapOf<String, MeshData>()
    val loadingModels = mutableSetOf<String>()

    val modelWireframe = LineMesh()
    var srcModel: MeshData
    val dispModel = Mesh(MeshData(Attribute.POSITIONS, Attribute.NORMALS))

    var simplifcationGrade = 1f
    lateinit var autoRun: ToggleButton
    lateinit var timeValLbl: Label

    init {
        dispModel.shader = basicShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.STATIC_COLOR
            staticColor = Color.MD_ORANGE
        }

        loadModel("bunny.kmf", 0.05f, ctx)
        loadModel("cow.kmf", 1f, ctx)

        simplificationScene = scene {
            defaultCamTransform()

            +dispModel
            +modelWireframe
        }
        scenes += simplificationScene

        scenes += uiScene(ctx.screenDpi) {
            theme = theme(UiTheme.DARK_SIMPLE) {
                componentUi { BlankComponentUi() }
                containerUi { BlankComponentUi() }
            }

            +container("menu") {
                layoutSpec.setOrigin(dps(-200f, true), zero(), zero())
                layoutSpec.setSize(dps(200f, true), pcs(100f), zero())
                ui.setCustom(SimpleComponentUi(this))

                var posY = -45f

                +label("Models") {
                    layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(40f, true), zero())
                    textColor.setCustom(theme.accentColor)
                }
                +component("divider") {
                    layoutSpec.setOrigin(pcs(5f), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(90f), dps(1f, true), zero())
                    val bg = SimpleComponentUi(this)
                    bg.color.setCustom(theme.accentColor)
                    ui.setCustom(bg)
                }
                posY -= 35f
                +button("Cow") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), zero())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                    onClick += { _,_,_ ->
                        val m = models["cow.kmf"]
                        if (m != null) {
                            srcModel = m
                            simplify()
                        }
                    }
                }
                posY -= 35f
                +button("Bunny") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), zero())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                    onClick += { _,_,_ ->
                        val m = models["bunny.kmf"]
                        if (m != null) {
                            srcModel = m
                            simplify()
                        }
                    }
                }
                posY -= 35f
                +button("Cosine Grid") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), zero())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                    onClick += { _,_,_ ->
                        srcModel = models["cos"]!!
                        simplify()
                    }
                }

                posY -= 50f
                +label("Simplify") {
                    layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(40f, true), zero())
                    textColor.setCustom(theme.accentColor)
                }
                +component("divider") {
                    layoutSpec.setOrigin(pcs(5f), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(90f), dps(1f, true), zero())
                    val bg = SimpleComponentUi(this)
                    bg.color.setCustom(theme.accentColor)
                    ui.setCustom(bg)
                }
                posY -= 35f
                +label("Ratio:") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), zero())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                }
                val faceCntVal = label("faceCntVal") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), zero())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    text = "100 %"
                }
                +faceCntVal
                posY -= 25f
                +slider("faceCnt") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                    setValue(0.01f, 1f, 1f)
                    disableCamDrag()
                    onValueChanged += { value ->
                        faceCntVal.text = "${(value * 100f).toString(0)} %"
                        simplifcationGrade = value
                        if (autoRun.isEnabled) {
                            simplify()
                        }
                    }
                }
                posY -= 35f
                +button("Update Mesh") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), zero())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                    onClick += { _,_,_ ->
                        simplify()
                    }
                }
                posY -= 35f
                autoRun = toggleButton("Auto Update") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                    isEnabled = true
                }
                +autoRun
                posY -= 35f
                +label("Time:") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                }
                timeValLbl = label("timeValLbl") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    text = ""
                }
                +timeValLbl
            }
        }

        srcModel = makeCosGrid()
        models["cos"] = srcModel
        simplify()
    }

    fun simplify() {
        val pt = PerfTimer()
        dispModel.meshData.batchUpdate {
            dispModel.meshData.clear()
            dispModel.meshData.vertexList.addVertices(srcModel.vertexList)

            val heMesh = HalfEdgeMesh(dispModel.meshData)
            MeshSimplifier(terminateOnFaceCountRel(simplifcationGrade)).simplifyMesh(heMesh)

            modelWireframe.meshData.batchUpdate {
                modelWireframe.clear()
                heMesh.generateWireframe(modelWireframe, Color.MD_LIGHT_BLUE)
            }
        }

        val time = pt.takeSecs()
        if (time > 0.2) {
            autoRun.isEnabled = false
        }
        timeValLbl.text = "${time.toString(2)} s"
    }

    private fun loadModel(name: String, scale: Float, ctx: KoolContext) {
        loadingModels += name
        ctx.assetMgr.loadAsset(name) { data ->
            if (data == null) {
                logE { "Fatal: Failed loading model" }
            } else {
                val mesh = loadMesh(data)
                val meshdata = mesh.meshData
                for (i in 0 until meshdata.numVertices) {
                    meshdata.vertexList.vertexIt.index = i
                    meshdata.vertexList.vertexIt.position.scale(scale)
                }
                models[name] = meshdata
                loadingModels -= name
                logD { "loaded: $name, bounds: ${models[name]?.bounds}" }
            }
        }
    }

    private fun makeCosGrid(): MeshData {
        val builder = MeshBuilder(MeshData(Attribute.POSITIONS, Attribute.NORMALS))
        builder.color = Color.MD_RED
        builder.grid {
            sizeX = 5f
            sizeY = 5f
            stepsX = 30
            stepsY = 30

            heightFun = { x, y ->
                val fx = (x.toFloat() / stepsX - 0.5f) * 10f
                val fy = (y.toFloat() / stepsY - 0.5f) * 10f
                cos(sqrt(fx*fx + fy*fy))
            }
        }
        return builder.meshData
    }

    fun Slider.disableCamDrag() {
        onHoverEnter += { _, _, _ ->
            // disable mouse interaction on content scene while pointer is over menu
            simplificationScene.isPickingEnabled = false
        }
        onHoverExit += { _, _, _->
            // enable mouse interaction on content scene when pointer leaves menu (and nothing else in this scene
            // is hit instead)
            simplificationScene.isPickingEnabled = true
        }
    }
}