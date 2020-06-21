package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.gltf.loadGltfModel
import de.fabmax.kool.util.ibl.BrdfLutPass
import de.fabmax.kool.util.ibl.IrradianceMapPass
import de.fabmax.kool.util.ibl.ReflectionMapPass
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun gltfDemo(ctx: KoolContext): List<Scene> {
    val demo = GltfDemo(ctx)
    return listOf(demo.mainScene, demo.menu)
}

class GltfDemo(ctx: KoolContext) {
    val mainScene: Scene
    val menu: Scene

    private var autoRotate = true
    private var camTranslationTarget: Vec3d? = null

    private val models = Cycler(
            GltfModel("Camera", "${Demo.modelBasePath}/camera.glb",
                    20f, Vec3f.ZERO, true, Vec3d(0.0, 0.5, 0.0), 5f),
            GltfModel("Flight Helmet", "${Demo.modelBasePath}/flight_helmet/FlightHelmet.gltf",
                    4f, Vec3f.ZERO, false, Vec3d(0.0, 1.25, 0.0), 3.5f)
//            ,GltfModel("Sci-Fi Helmet", "${Demo.modelBasePath}/scifi_helmet/SciFiHelmet.gltf",
//                    0.75f, Vec3f(0f, 1.5f, 0f), false, Vec3d(0.0, 1.25, 0.0), 3.5f)
    )

    private lateinit var orbitTransform: OrbitInputTransform
    private val contentGroup = TransformGroup()

    private var irrMapPass: IrradianceMapPass? = null
    private var reflMapPass: ReflectionMapPass? = null
    private var brdfLutPass: BrdfLutPass? = null

    private val shadows = mutableListOf<ShadowMap>()
    private var aoPipeline: AoPipeline? = null

    init {
        models.current.isVisible = true

        mainScene = makeMainScene(ctx)
        menu = menu(ctx)
    }

    private fun makeMainScene(ctx: KoolContext) = scene {
        orbitTransform = orbitInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(0f, -30f)
            // Add camera to the transform group
            +camera
            zoom = 5.0


            translation.set(0.0, 0.5, 0.0)
            onUpdate += { _, _ ->
                if (autoRotate) {
                    verticalRotation -= ctx.deltaT * 3f
                }
                camTranslationTarget?.let {
                    val v = MutableVec3d(translation).scale(0.9).add(MutableVec3d(it).scale(0.1))
                    translation.set(v)
                    if (v.distance(it) < 0.01) {
                        camTranslationTarget = null
                    }
                }
            }
        }
        +orbitTransform

        lighting.lights.clear()
        lighting.lights.add(Light().apply {
            val pos = Vec3f(7f, 8f, 8f)
            val lookAt = Vec3f.ZERO
            setSpot(pos, lookAt.subtract(pos, MutableVec3f()).norm(), 25f)
            setColor(Color.WHITE.mix(Color.MD_AMBER, 0.3f).toLinear(), 500f)
        })
        lighting.lights.add(Light().apply {
            val pos = Vec3f(-7f, 8f, 8f)
            val lookAt = Vec3f.ZERO
            setSpot(pos, lookAt.subtract(pos, MutableVec3f()).norm(), 25f)
            setColor(Color.WHITE.mix(Color.MD_AMBER, 0.3f).toLinear(), 500f)
        })

        shadows += listOf(
                SimpleShadowMap(this, 0, 2048),
                SimpleShadowMap(this, 1, 2048))
        aoPipeline = AoPipeline.createForward(this)


        val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = true)
        ctx.assetMgr.loadAndPrepareTexture("${Demo.envMapBasePath}/shanghai_bund_1k.rgbe.png", hdriTexProps) { hdri ->
            irrMapPass = IrradianceMapPass(this, hdri)
            reflMapPass = ReflectionMapPass(this, hdri)
            brdfLutPass = BrdfLutPass(this)

            onDispose += {
                hdri.dispose()
            }

            setupContentGroup()
            models.forEach {
                it.load(ctx)
            }

            +contentGroup
            +Skybox(reflMapPass!!.colorTextureCube, 1f)
        }
    }

    private fun setupContentGroup() {
        contentGroup.apply {
            rotate(-60.0, Vec3d.Y_AXIS)
            onUpdate += { _, ctx ->
                if (autoRotate) {
                    rotate(ctx.deltaT.toDouble() * 3, Vec3d.Y_AXIS)
                }
            }

            +textureMesh(isNormalMapped = true) {
                generate {
                    roundCylinder(4.1f, 0.2f)
                }
                pipelineLoader = pbrShader {
                    shadowMaps += shadows
                    isScrSpcAmbientOcclusion = true
                    scrSpcAmbientOcclusionMap = aoPipeline?.aoMap

                    albedoSource = Albedo.TEXTURE_ALBEDO
                    isAmbientOcclusionMapped = true
                    isNormalMapped = true
                    isRoughnessMapped = true

                    albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Color2.jpg") }
                    normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Normal.jpg") }
                    roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Roughness.jpg") }
                    ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_AmbientOcclusion.jpg") }

                    setImageBasedLighting(irrMapPass?.colorTextureCube, reflMapPass?.colorTextureCube, brdfLutPass?.colorTexture)

                    onDispose += {
                        albedoMap?.dispose()
                        normalMap?.dispose()
                        roughnessMap?.dispose()
                        ambientOcclusionMap?.dispose()
                    }
                }
            }
        }
    }

    private fun menu(ctx: KoolContext) = uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-370f), dps(-280f), zero())
            layoutSpec.setSize(dps(250f), dps(160f), full())

            var y = -40f
            +label("glTF Models") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }

            y -= 35f
            +label("Model:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
            }
            y -= 35f
            val modelName = button(models.current.name) {
                layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                layoutSpec.setSize(pcs(70f), dps(35f), full())
                onClick += { _, _, _ ->
                    models.current.isVisible = false
                    text = models.next().name
                    models.current.isVisible = true
                    orbitTransform.zoom = models.current.zoom.toDouble()
                    camTranslationTarget = models.current.lookAt
                }
            }
            +modelName
            +button("prevModel") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = "<"

                onClick += { _, _, _ ->
                    models.current.isVisible = false
                    modelName.text = models.prev().name
                    models.current.isVisible = true
                    orbitTransform.zoom = models.current.zoom.toDouble()
                    camTranslationTarget = models.current.lookAt
                }
            }
            +button("nextModel") {
                layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                layoutSpec.setSize(pcs(20f), dps(35f), full())
                text = ">"

                onClick += { _, _, _ ->
                    models.current.isVisible = false
                    modelName.text = models.next().name
                    models.current.isVisible = true
                    orbitTransform.zoom = models.current.zoom.toDouble()
                    camTranslationTarget = models.current.lookAt
                }
            }
            y -= 35f
            +toggleButton("Auto Rotate") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = autoRotate
                onStateChange += {
                    autoRotate = isEnabled
                }
            }
        }
    }

    private fun MeshBuilder.roundCylinder(radius: Float, height: Float) {
        val nCorner = 20
        val cornerR = height / 2
        val cornerPts = mutableListOf<Vec3f>()
        for (i in 0..nCorner) {
            val a = (PI / nCorner * i).toFloat()
            val x = sin(a) * cornerR + radius
            val y = cos(a) * cornerR - cornerR
            cornerPts += Vec3f(x, y, 0f)
        }

        val uvScale = 0.3f
        val nCyl = 100
        var firstI = 0
        for (i in 0 .. nCyl) {
            val a = (PI / nCyl * i * 2).toFloat()
            cornerPts.forEachIndexed { ci, cpt ->
                val uv = MutableVec2f(radius + ci.toFloat() / cornerPts.size * PI.toFloat() * cornerR, 0f)
                uv.scale(uvScale)
                uv.rotate(a.toDeg())
                val pt = cpt.rotate(a.toDeg(), Vec3f.Y_AXIS, MutableVec3f())
                val iv = vertex(pt, Vec3f.ZERO, uv)
                if (i > 0 && ci > 0) {
                    geometry.addTriIndices(iv - 1, iv - cornerPts.size - 1, iv - cornerPts.size)
                    geometry.addTriIndices(iv, iv - 1, iv - cornerPts.size)
                }
                if (i == 0 && ci == 0) {
                    firstI = iv
                }
            }
        }
        val firstIBot = firstI + cornerPts.size - 1
        for (i in 2 .. nCyl) {
            geometry.addTriIndices(firstI, firstI + ((i - 1) * cornerPts.size), firstI + (i * cornerPts.size))
            geometry.addTriIndices(firstIBot, firstIBot + (i * cornerPts.size), firstIBot + ((i - 1) * cornerPts.size))
        }
        geometry.generateNormals()
    }

    private inner class GltfModel(
            val name: String,
            val assetPath: String,
            val scale: Float,
            val translation: Vec3f,
            val generateNormals: Boolean,
            val lookAt: Vec3d,
            val zoom: Float) {

        var model: Model? = null
        var isVisible: Boolean = false
            set(value) {
                field = value
                model?.isVisible = value
            }

        fun load(ctx: KoolContext) {
            ctx.assetMgr.loadGltfModel(assetPath) { gltf ->
                gltf?.let {
                    model = it.makeModel(generateNormals = generateNormals) {
                        shadowMaps += shadows
                        scrSpcAmbientOcclusionMap = aoPipeline?.aoMap
                        isScrSpcAmbientOcclusion = true

                        setImageBasedLighting(irrMapPass?.colorTextureCube, reflMapPass?.colorTextureCube, brdfLutPass?.colorTexture)
                    }.apply {
                        scale(scale, scale, scale)
                        translate(translation)
                        isVisible = this@GltfModel.isVisible
                        contentGroup += this
                    }
                }
            }
        }
    }
}
