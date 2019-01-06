package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Ray
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.isFuzzyZero
import de.fabmax.kool.modules.mesh.HalfEdgeMesh
import de.fabmax.kool.modules.mesh.simplification.simplify
import de.fabmax.kool.modules.mesh.simplification.terminateOnFaceCountRel
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.serialization.ModelData
import kotlin.math.cos
import kotlin.math.min
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
    val highlightedEdge = LineMesh().apply { isXray = true; lineWidth = 3f }
    val highlightedPt = PointMesh().apply { isXray = true; pointSize = 6f }

    var srcModel: MeshData
    val dispModel = Mesh(MeshData(Attribute.POSITIONS, Attribute.NORMALS))
    var heMesh: HalfEdgeMesh

    var simplifcationGrade = 1f
    lateinit var autoRun: ToggleButton
    lateinit var facesValLbl: Label
    lateinit var vertsValLbl: Label
    lateinit var timeValLbl: Label

    private val pickRay = Ray()
    private val edgeDistance = EdgeRayDistance()
    private val nearestEdgeTraverser = NearestToRayTraverser<HalfEdgeMesh.HalfEdge>().apply {
        rayDistance = edgeDistance
    }

    init {
        dispModel.shader = basicShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.STATIC_COLOR
            staticColor = Color.MD_ORANGE
        }

        srcModel = makeCosGrid()
        models["cos"] = srcModel
        heMesh = HalfEdgeMesh(srcModel)

        loadModel("bunny.kmf", 0.05f, ctx)
        loadModel("cow.kmf", 1f, ctx)


        simplificationScene = scene {
            defaultCamTransform()

            +dispModel
            +modelWireframe
            +highlightedEdge
            +highlightedPt

//            onPreRender += { ctx ->
//                if (ctx.inputMgr.primaryPointer.isValid) {
//                    val ptr = ctx.inputMgr.primaryPointer
//                    camera.computePickRay(pickRay, ptr, ctx)
//
//                    val ocTree = (heMesh.edgeHandler as HalfEdgeMesh.OcTreeEdgeHandler).edgeTree
//                    ocTree.traverse(nearestEdgeTraverser.setup(pickRay))
//                    val edge = nearestEdgeTraverser.nearest
//
//                    highlightedEdge.clear()
//                    highlightedPt.clear()
//                    if (edge != null) {
//                        highlightedEdge.addLine(edge.from, Color.MD_PINK, edge.to, Color.MD_PINK)
//                        val edgePt = edgeDistance.nearestPointOnEdge(edge, pickRay)
//                        highlightedPt.addPoint {
//                            position.set(edgePt)
//                            color.set(Color.MD_GREEN)
//                        }
//
//                        if (ptr.isLeftButtonEvent && !ptr.isLeftButtonDown) {
//                            // left mouse button clicked
//                            val fraction = edge.from.distance(edgePt) / edge.from.distance(edge.to)
//                            heMesh.splitEdge(edge, fraction)
//                            heMesh.rebuild()
//                            modelWireframe.meshData.batchUpdate {
//                                modelWireframe.clear()
//                                heMesh.generateWireframe(modelWireframe, Color.MD_LIGHT_BLUE)
//                            }
//                        }
//                    }
//                }
//            }
        }
        scenes += simplificationScene

        scenes += uiScene(ctx.screenDpi) {
            theme = theme(UiTheme.DARK_SIMPLE) {
                componentUi { BlankComponentUi() }
                containerUi { BlankComponentUi() }
            }

            +container("menu") {
                layoutSpec.setOrigin(dps(-200f, true), zero(), zero())
                layoutSpec.setSize(dps(200f, true), pcs(100f), full())
                ui.setCustom(SimpleComponentUi(this))

                var posY = -45f

                +label("Models") {
                    layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(40f, true), full())
                    textColor.setCustom(theme.accentColor)
                }
                +component("divider") {
                    layoutSpec.setOrigin(pcs(5f), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(90f), dps(1f, true), full())
                    val bg = SimpleComponentUi(this)
                    bg.color.setCustom(theme.accentColor)
                    ui.setCustom(bg)
                }
                posY -= 35f
                +button("Cow") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), full())
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
                    layoutSpec.setSize(pcs(100f), dps(35f, true), full())
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
                    layoutSpec.setSize(pcs(100f), dps(35f, true), full())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)

                    onClick += { _,_,_ ->
                        srcModel = models["cos"]!!
                        simplify()
                    }
                }

                posY -= 50f
                +label("Simplify") {
                    layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(40f, true), full())
                    textColor.setCustom(theme.accentColor)
                }
                +component("divider") {
                    layoutSpec.setOrigin(pcs(5f), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(90f), dps(1f, true), full())
                    val bg = SimpleComponentUi(this)
                    bg.color.setCustom(theme.accentColor)
                    ui.setCustom(bg)
                }
                posY -= 35f
                +label("Ratio:") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), full())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                }
                val faceCntVal = label("faceCntVal") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f, true), full())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    text = "100 %"
                }
                +faceCntVal
                posY -= 25f
                +slider("faceCnt") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), full())
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
                    layoutSpec.setSize(pcs(100f), dps(35f, true), full())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                    onClick += { _,_,_ ->
                        simplify()
                    }
                }
                posY -= 35f
                autoRun = toggleButton("Auto Update") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), full())
                    isEnabled = true
                }
                +autoRun
                posY -= 35f
                +label("Faces:") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), full())
                }
                facesValLbl = label("facesValLbl") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), full())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    text = ""
                }
                +facesValLbl
                posY -= 35f
                +label("Vertices:") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), full())
                }
                vertsValLbl = label("verticesValLbl") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), full())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    text = ""
                }
                +vertsValLbl
                posY -= 35f
                +label("Time:") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), full())
                }
                timeValLbl = label("timeValLbl") {
                    layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                    layoutSpec.setSize(pcs(100f), dps(25f, true), full())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    text = ""
                }
                +timeValLbl
            }
        }

        simplify()
    }

    fun simplify() {
        val pt = PerfTimer()
        dispModel.meshData.batchUpdate {
            dispModel.meshData.clear()
            dispModel.meshData.vertexList.addFrom(srcModel.vertexList)

            heMesh = HalfEdgeMesh(dispModel.meshData, HalfEdgeMesh.ListEdgeHandler())
            //heMesh = HalfEdgeMesh(dispModel.meshData)
            if (simplifcationGrade < 0.999f) {
                heMesh.simplify(terminateOnFaceCountRel(simplifcationGrade))
            }

            modelWireframe.meshData.batchUpdate {
                modelWireframe.clear()
                heMesh.generateWireframe(modelWireframe, Color.MD_LIGHT_BLUE)
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

    private fun loadModel(name: String, scale: Float, ctx: KoolContext) {
        loadingModels += name
        ctx.assetMgr.loadAsset(name) { data ->
            if (data == null) {
                logE { "Fatal: Failed loading model" }
            } else {
                val mesh = ModelData.load(data).meshes[0].toMesh()
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

    class EdgeRayDistance : RayDistance<HalfEdgeMesh.HalfEdge> {
        private val tmpVec1 = MutableVec3f()
        private val tmpVec2 = MutableVec3f()
        private val tmpVec3 = MutableVec3f()

        override fun itemSqrDistanceToRay(tree: SpatialTree<HalfEdgeMesh.HalfEdge>, item: HalfEdgeMesh.HalfEdge, ray: Ray): Float {
            val pt = nearestPointOnEdge(item, ray)
            return ray.sqrDistanceToPoint(pt)
        }

        fun nearestPointOnEdge(edge: HalfEdgeMesh.HalfEdge, ray: Ray): Vec3f {
            edge.to.subtract(edge.from, tmpVec1).norm()
            tmpVec2.set(ray.direction)

            val dot = tmpVec1 * tmpVec2
            val n = 1f - dot * dot
            if (n.isFuzzyZero()) {
                // edge and ray are parallel
                return if (edge.from.sqrDistance(ray.origin) < edge.to.sqrDistance(ray.origin)) {
                    edge.from
                } else {
                    edge.to
                }
            }

            ray.origin.subtract(edge.from, tmpVec3)
            val a = tmpVec3 * tmpVec1
            val b = tmpVec3 * tmpVec2
            val l = (a - b * dot) / n

            return if (l > 0) {
                val d = edge.from.distance(edge.to)
                tmpVec1.scale(min(l, d)).add(edge.from)
            } else {
                edge.from
            }
        }
    }
}