package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomF
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.PhongShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfFile

fun instanceDemo(ctx: KoolContext) : List<Scene> {
    return InstanceDemo(ctx).scenes
}

class InstanceDemo(ctx: KoolContext) {
    val scenes = mutableListOf<Scene>()

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

    init {
        scenes += mainScene(ctx)
        scenes += menu(ctx)
    }

    private fun mainScene(ctx: KoolContext) = scene {
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

            onUpdate += { _, ctx ->
                if (isAutoRotate) {
                    verticalRotation += ctx.deltaT * 3f
                }
            }
        }

        lighting.singleLight {
            setDirectional(Vec3f(-1f))
            setColor(Color.WHITE, 1f)
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

                pipelineLoader = PhongShader(model = instanceColorPhongModel())

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
                        this.color.set(colors[rand.randomI(colors.indices)])
                        this.center.set(modelCenter)
                        this.radius = modelRadius
                    }
                }
            }
        }
    }

    private fun instanceColorPhongModel() = ShaderModel("instanceColorPhongModel()").apply {
        val ifNormals: StageInterfaceNode
        val ifColors: StageInterfaceNode
        val ifFragPos: StageInterfaceNode
        val mvpNode: UniformBufferMvp

        vertexStage {
            ifColors = stageInterfaceNode("ifColors", instanceAttributeNode(Attribute.COLORS).output)

            mvpNode = mvpNode()
            val modelMat = multiplyNode(mvpNode.outModelMat, instanceAttrModelMat().output).output
            val mvpMat = multiplyNode(mvpNode.outMvpMat, instanceAttrModelMat().output).output

            val nrm = vec3TransformNode(attrNormals().output, modelMat, 0f)
            ifNormals = stageInterfaceNode("ifNormals", nrm.outVec3)

            val worldPos = vec3TransformNode(attrPositions().output, modelMat, 1f).outVec3
            ifFragPos = stageInterfaceNode("ifFragPos", worldPos)
            positionOutput = vec4TransformNode(attrPositions().output, mvpMat).outVec4
        }
        fragmentStage {
            val mvpFrag = mvpNode.addToStage(fragmentStageGraph)
            val lightNode = multiLightNode()
            val albedo = ifColors.output
            val normal = ifNormals.output
            val phongMat = phongMaterialNode(albedo, normal, ifFragPos.output, mvpFrag.outCamPos, lightNode).apply {
                inShininess = pushConstantNode1f("uShininess").output
                inSpecularIntensity = pushConstantNode1f("uSpecularIntensity").output
            }
            colorOutput(phongMat.outColor)
        }
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

    private fun menu(ctx: KoolContext): Scene = uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-450f), dps(-535f), zero())
            layoutSpec.setSize(dps(330f), dps(415f), full())

            var y = -40f
            +label("Scene") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }

            y -= 35f
            +label("Bunnies:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val btnBunnyCnt = button("bunnyCnt") {
                layoutSpec.setOrigin(pcs(45f), dps(y), zero())
                layoutSpec.setSize(pcs(40f), dps(35f), full())
                text = "${nBunnies * nBunnies * nBunnies}"

                onClick += { _, _, _ ->
                    if (nBunnies < 20) {
                        nBunnies++
                        text = "${nBunnies * nBunnies * nBunnies}"
                        lodController.setupInstances()
                    }
                }
            }
            +btnBunnyCnt
            +button("decBunnyCnt") {
                layoutSpec.setOrigin(pcs(35f), dps(y), zero())
                layoutSpec.setSize(pcs(10f), dps(35f), full())
                text = "<"

                onClick += { _, _, _ ->
                    if (nBunnies > 5) {
                        nBunnies--
                        btnBunnyCnt.text = "${nBunnies * nBunnies * nBunnies}"
                        lodController.setupInstances()
                    }
                }
            }
            +button("incBunnyCnt") {
                layoutSpec.setOrigin(pcs(85f), dps(y), zero())
                layoutSpec.setSize(pcs(10f), dps(35f), full())
                text = ">"

                onClick += { _, _, _ ->
                    if (nBunnies < 20) {
                        nBunnies++
                        btnBunnyCnt.text = "${nBunnies * nBunnies * nBunnies}"
                        lodController.setupInstances()
                    }
                }
            }
            y -= 35f
            +toggleButton("Color by LOD") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = isLodColors
                onStateChange += {
                    isLodColors = isEnabled
                }
            }
            y -= 35f
            +toggleButton("Auto Rotate") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = isAutoRotate
                onStateChange += {
                    isAutoRotate = isEnabled
                }
            }

            y -= 40f
            +label("Info") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            for (i in lods.indices) {
                y -= 35f
                +label("LOD $i:") {
                    layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                    layoutSpec.setSize(pcs(20f), dps(30f), full())
                }
                +label("LOD $i:") {
                    layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                    layoutSpec.setSize(pcs(20f), dps(30f), full())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                    onUpdate += { _, _ ->
                        val cnt = lodController.getInstanceCount(i)
                        val tris = cnt * (lods[i].mesh?.geometry?.numPrimitives ?: 0)
                        text = "$cnt insts / $tris tris"
                    }
                }
            }
        }
    }
}
