package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.deferred.DeferredPbrShader
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.gltf.GltfFile
import de.fabmax.kool.util.gltf.loadGltfFile
import de.fabmax.kool.util.ibl.EnvironmentHelper
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

fun gltfDemo(ctx: KoolContext): List<Scene> {
    val demo = GltfDemo(ctx)
    return listOf(demo.mainScene, demo.menu)
}

class GltfDemo(ctx: KoolContext) {
    val mainScene: Scene
    val menu: Scene

    private val foxAnimator = FoxAnimator()
    private val models = Cycler(
            GltfModel("Flight Helmet", "${Demo.modelBasePath}/flight_helmet/FlightHelmet.gltf",
                    4f, Vec3f.ZERO, false, Vec3d(0.0, 1.25, 0.0), false, 3.5),
            GltfModel("Coffee Cart", "${Demo.modelBasePath}/CoffeeCart_01.glb",
                    2f, Vec3f(0f, -0.01f, 0f), false, Vec3d(0.0, 1.75, 0.0), false, 3.5),
            GltfModel("Camera", "${Demo.modelBasePath}/camera.glb",
                    20f, Vec3f.ZERO, true, Vec3d(0.0, 0.5, 0.0), false, 5.0),
            GltfModel("Fox", "${Demo.modelBasePath}/fox.glb",
                    0.01f, Vec3f.ZERO, false, Vec3d(0.0, 1.25, 0.0), true, 3.5)
                    .apply { animate = { _, ctx -> foxAnimator.animate(this, ctx) } },
            GltfModel("Animated Box", "${Demo.modelBasePath}/BoxAnimated.gltf",
                    1f, Vec3f(0f, 0.5f, 0f), false, Vec3d(0.0, 1.5, 0.0), false, 5.0),
            GltfModel("Morph Cube", "${Demo.modelBasePath}/AnimatedMorphCube.glb",
                    1f, Vec3f(0f, 1f, 0f), false, Vec3d(0.0, 1.0, 0.0), false, 3.5),
            GltfModel("Alpha Mode Test", "${Demo.modelBasePath}/AlphaBlendModeTest.glb",
                    0.5f, Vec3f(0f, 0.06f, 0f), false, Vec3d(0.0, 0.75, 0.0), false, 3.5)
    )

    private lateinit var orbitTransform: OrbitInputTransform
    private var camTranslationTarget: Vec3d? = null
    private var trackModel = false

    private lateinit var envMaps: EnvironmentMaps

    private val shadowsForward = mutableListOf<ShadowMap>()
    private var aoPipelineForward: AoPipeline? = null
    private val contentGroupForward = TransformGroup()

    private lateinit var deferredPipeline: DeferredPipeline
    private val contentGroupDeferred = TransformGroup()

    private var animationSpeed = .5f
    private var animationTime = 0.0
    private var autoRotate = true
    private var useDeferredPipeline = true
    private var isAo = true
        set(value) {
            field = value
            deferredPipeline.isAoEnabled = value
        }

    init {
        models.current.isVisible = true
        trackModel = models.current.trackModel

        mainScene = makeMainScene(ctx)
        menu = menu(ctx)

        mainScene.onUpdate += { _, _ ->
            animationTime += ctx.deltaT * animationSpeed
            foxAnimator.updatePosition(ctx)
        }
    }

    private fun makeMainScene(ctx: KoolContext) = scene("gltfDemo") {
        setupLighting()
        setupCamera()

        ctx.assetMgr.launch {
            envMaps = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/shanghai_bund_1k.rgbe.png", this)

            +Skybox(envMaps.reflectionMap, 1f)

            makeDeferredContent(ctx)
            makeForwardContent(ctx)

            setupPipelines()
        }
    }

    private fun setupPipelines() {
        val defState = useDeferredPipeline
        val fwdState = !defState

        contentGroupForward.isVisible = fwdState
        shadowsForward.forEach { it.isShadowMapEnabled = fwdState }
        aoPipelineForward?.isEnabled = fwdState && isAo

        contentGroupDeferred.isVisible = defState
        deferredPipeline.isEnabled = defState
        deferredPipeline.isAoEnabled = isAo
    }

    private suspend fun Scene.makeForwardContent(ctx: KoolContext) {
        aoPipelineForward = AoPipeline.createForward(this).apply {
            radius = 0.2f
        }
        shadowsForward += listOf(
                SimpleShadowMap(this, 0, 2048, contentGroupForward),
                SimpleShadowMap(this, 1, 2048, contentGroupForward))

        contentGroupForward.setupContentGroup(false, ctx)
        +contentGroupForward
    }

    private suspend fun Scene.makeDeferredContent(ctx: KoolContext) {
        val defCfg = DeferredPipelineConfig().apply {
            isWithAmbientOcclusion = true
            isWithScreenSpaceReflections = true
            isWithEmissive = true
            maxGlobalLights = 2
            useImageBasedLighting(envMaps)
        }
        deferredPipeline = DeferredPipeline(this, defCfg)
        deferredPipeline.aoPipeline?.apply {
            radius = 0.2f
        }
        deferredPipeline.contentGroup.setupContentGroup(true, ctx)

        // main scene only contains a quad used to draw the deferred shading output
        +contentGroupDeferred.apply {
            isFrustumChecked = false
            +deferredPipeline.renderOutput
        }
    }

    private fun Scene.setupCamera() {
        orbitTransform = orbitInputTransform {
            setMouseRotation(0f, -30f)
            +camera
            zoom = models.current.zoom
            translation.set(models.current.lookAt)

            onUpdate += { _, ctx ->
                var translationTarget = camTranslationTarget
                if (trackModel) {
                    val model = models.current.model
                    model?.let {
                        val center = model.globalCenter
                        translationTarget = Vec3d(center.x.toDouble(), center.y.toDouble(), center.z.toDouble())
                    }
                } else if (autoRotate) {
                    verticalRotation -= ctx.deltaT * 3f
                }

                translationTarget?.let {
                    val v = MutableVec3d(translation).scale(0.9).add(MutableVec3d(it).scale(0.1))
                    translation.set(v)
                    if (v.distance(it) < 0.01) {
                        camTranslationTarget = null
                    }
                }
            }
        }
        +orbitTransform
    }

    private fun Scene.setupLighting() {
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
    }

    private suspend fun TransformGroup.setupContentGroup(isDeferredShading: Boolean, ctx: KoolContext) {
        rotate(-60.0, Vec3d.Y_AXIS)
        onUpdate += { _, _ ->
            if (autoRotate) {
                setIdentity()
                rotate(ctx.time * 3, Vec3d.Y_AXIS)
            }
        }

        +textureMesh(isNormalMapped = true) {
            generate {
                roundCylinder(4.1f, 0.2f)
            }
            val pbrCfg = PbrMaterialConfig().apply {
                if (!isDeferredShading) {
                    shadowMaps += shadowsForward
                    useScreenSpaceAmbientOcclusion(aoPipelineForward?.aoMap)
                    useImageBasedLighting(envMaps)
                }

                useAlbedoMap("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Color2.jpg")
                useNormalMap("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Normal.jpg")
                useOcclusionMap("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_AmbientOcclusion.jpg")
                useRoughnessMap("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Roughness.jpg")

                onDispose += {
                    albedoMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                    occlusionMap?.dispose()
                }
            }

            shader = if (isDeferredShading) {
                DeferredPbrShader(pbrCfg)
            } else {
                PbrShader(pbrCfg)
            }
        }

        models.forEach { model ->
            model.load(isDeferredShading, ctx)?.let { +it }
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
            layoutSpec.setOrigin(dps(-420f), dps(-525f), zero())
            layoutSpec.setSize(dps(300f), dps(405f), full())

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
                    orbitTransform.zoom = models.current.zoom
                    camTranslationTarget = models.current.lookAt
                    trackModel = models.current.trackModel
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
                    orbitTransform.zoom = models.current.zoom
                    camTranslationTarget = models.current.lookAt
                    trackModel = models.current.trackModel
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
                    orbitTransform.zoom = models.current.zoom
                    camTranslationTarget = models.current.lookAt
                    trackModel = models.current.trackModel
                }
            }
            y -= 35f
            +label("Animation Speed:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val speedVal = label(animationSpeed.toString(2)) {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +speedVal
            y -= 35f
            +slider("speedSlider", 0f, 1f, animationSpeed) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    speedVal.text = value.toString(2)
                    animationSpeed = value
                }
            }
            y -= 35f
            +toggleButton("Deferred Shading") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = useDeferredPipeline
                onStateChange += {
                    useDeferredPipeline = isEnabled
                    setupPipelines()
                }
            }
            y -= 35f
            +toggleButton("Ambient Occlusion") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = true
                onStateChange += {
                    isAo = isEnabled
                    setupPipelines()
                }
            }
            y -= 35f
            +toggleButton("Screen Space Reflections") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = true
                onStateChange += {
                    deferredPipeline.isSsrEnabled = isEnabled
                    setupPipelines()
                }
            }
            y -= 35f
            +label("SSR Map Size:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
            }
            val mapSzVal = label("0.7 x") {
                layoutSpec.setOrigin(pcs(75f), dps(y), zero())
                layoutSpec.setSize(pcs(25f), dps(35f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +mapSzVal
            y -= 35f
            +slider("mapSizeSlider", 1f, 10f, 7f) {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                onValueChanged += {
                    val sz = value.roundToInt() / 10f
                    deferredPipeline.reflectionMapSize = sz
                    mapSzVal.text = "${sz.toString(1)} x"
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
            val trackModel: Boolean,
            val zoom: Double) {

        var model: Model? = null
        var isVisible: Boolean = false

        var animate: Model.(Double, KoolContext) -> Unit = { t, _ ->
            applyAnimation(t)
        }

        suspend fun load(isDeferredShading: Boolean, ctx: KoolContext): Model? {
            ctx.assetMgr.loadGltfFile(assetPath)?.let {
                val materialCfg = GltfFile.ModelMaterialConfig(
                        shadowMaps = if (isDeferredShading) deferredPipeline.shadowMaps else shadowsForward,
                        scrSpcAmbientOcclusionMap = if (isDeferredShading) deferredPipeline.aoPipeline?.aoMap else aoPipelineForward?.aoMap,
                        environmentMaps = envMaps,
                        isDeferredShading = isDeferredShading
                )
                val modelCfg = GltfFile.ModelGenerateConfig(
                        generateNormals = generateNormals,
                        materialConfig = materialCfg,
                        loadAnimations = true,
                        applyMorphTargets = true,
                        applySkins = true,
                        applyTransforms = true,
                        mergeMeshesByMaterial = true
                )
                model = it.makeModel(modelCfg).apply {
                    translate(translation)
                    scale(scale)

                    // only relevant for Polly, but doesn't harm the other models...
                    findNode("Ground")?.isVisible = false

                    enableAnimation(0)
                    onUpdate += { _, ctx ->
                        isVisible = this@GltfModel.isVisible
                        animate(animationTime, ctx)
                    }
                }
            }
            return model
        }
    }

    private inner class FoxAnimator {
        var angle = 0.0
        val radius = 3.0
        val position = MutableVec3d()

        fun updatePosition(ctx: KoolContext) {
            val speed = animationSpeed * 2
            angle += speed * ctx.deltaT / radius
        }

        fun animate(model: Model, ctx: KoolContext) {
            // mix survey / walk / run animations according to animation speed
            if (animationSpeed < 0.5f) {
                val w1 = animationSpeed * 2f
                val w0 = 1f - w1
                model.setAnimationWeight(0, w0)
                model.setAnimationWeight(1, w1)
                model.setAnimationWeight(2, 0f)
            } else {
                val w1 = (animationSpeed - 0.5f) * 2f
                val w0 = 1f - w1
                model.setAnimationWeight(0, 0f)
                model.setAnimationWeight(1, w0)
                model.setAnimationWeight(2, w1)
            }
            model.applyAnimation(ctx.time)

            // move model according to animation speed
            model.setIdentity()
            position.set(radius, 0.0, 0.0).rotate(angle.toDeg(), Vec3d.Y_AXIS)
            model.translate(position)
            model.rotate(angle.toDeg() + 180, Vec3d.Y_AXIS)
            model.scale(0.01)
        }
    }
}
