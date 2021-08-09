package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
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

class GltfDemo : DemoScene("glTF Models") {

    private val foxAnimator = FoxAnimator()
    private val models = Cycler(
            GltfModel("Flight Helmet", "${Demo.modelBasePath}/flight_helmet/FlightHelmet.gltf",
                    4f, Vec3f.ZERO, false, Vec3d(0.0, 1.25, 0.0), false, 3.5),
            GltfModel("Polly", "${Demo.modelBasePath}/project_polly_jpg.glb",
                    3f, Vec3f.ZERO, false, Vec3d(0.0, 1.25, 0.0), false, 3.5),
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
    private val contentGroupForward = Group()

    private lateinit var deferredPipeline: DeferredPipeline
    private val contentGroupDeferred = Group()

    private var animationSpeed = .5f
    private var animationTime = 0.0
    private var autoRotate = true
    private var useDeferredPipeline = true
    private var isAo = true
        set(value) {
            field = value
            deferredPipeline.isAoEnabled = value
        }

    override fun lateInit(ctx: KoolContext) {
        models.current.isVisible = true
        trackModel = models.current.trackModel
    }

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        showLoadText("Loading IBL Maps")
        envMaps = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.envMapBasePath}/shanghai_bund_1k.rgbe.png", this)

        mainScene.setupLighting()

        // create deferred pipeline
        val defCfg = DeferredPipelineConfig().apply {
            isWithAmbientOcclusion = true
            isWithScreenSpaceReflections = true
            isWithExtendedMaterials = true
            baseReflectionStep = 0.02f
            maxGlobalLights = 2
            useImageBasedLighting(envMaps)
        }
        deferredPipeline = DeferredPipeline(mainScene, defCfg)
        deferredPipeline.outputShader.setupVignette(strength = 0f)
        deferredPipeline.aoPipeline?.apply {
            radius = 0.2f
        }

        // create forward pipeline
        aoPipelineForward = AoPipeline.createForward(mainScene).apply {
            radius = 0.2f
        }
        shadowsForward += listOf(
            SimpleShadowMap(mainScene, 0, 2048, contentGroupForward),
            SimpleShadowMap(mainScene, 1, 2048, contentGroupForward))

        // load models
        models.forEach {
            showLoadText("Loading ${it.name}")
            it.load(false, ctx)
            it.load(true, ctx)
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupCamera()

        +Skybox.cube(envMaps.reflectionMap, 1f)

        makeDeferredContent(ctx)
        makeForwardContent(ctx)

        setupPipelines()

        onUpdate += {
            animationTime += ctx.deltaT * animationSpeed
            foxAnimator.updatePosition(ctx)
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

    private fun Scene.makeForwardContent(ctx: KoolContext) {
        contentGroupForward.setupContentGroup(false, ctx)
        +contentGroupForward
    }

    private fun Scene.makeDeferredContent(ctx: KoolContext) {
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

            onUpdate += { ev ->
                var translationTarget = camTranslationTarget
                if (trackModel) {
                    val model = models.current.forwardModel
                    model?.let {
                        val center = model.globalCenter
                        translationTarget = Vec3d(center.x.toDouble(), center.y.toDouble(), center.z.toDouble())
                    }
                } else if (autoRotate) {
                    verticalRotation -= ev.deltaT * 3f
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
            setColor(Color.WHITE.mix(MdColor.AMBER, 0.3f).toLinear(), 500f)
        })
        lighting.lights.add(Light().apply {
            val pos = Vec3f(-7f, 8f, 8f)
            val lookAt = Vec3f.ZERO
            setSpot(pos, lookAt.subtract(pos, MutableVec3f()).norm(), 25f)
            setColor(Color.WHITE.mix(MdColor.AMBER, 0.3f).toLinear(), 500f)
        })
    }

    private fun Group.setupContentGroup(isDeferredShading: Boolean, ctx: KoolContext) {
        rotate(-60.0, Vec3d.Y_AXIS)
        onUpdate += {
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
                useAmbientOcclusionMap("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_AmbientOcclusion.jpg")
                useRoughnessMap("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Roughness.jpg")

                onDispose += {
                    albedoMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                    aoMap?.dispose()
                }
            }

            shader = if (isDeferredShading) {
                DeferredPbrShader(pbrCfg)
            } else {
                PbrShader(pbrCfg)
            }
        }

        models.forEach { model ->
            if (isDeferredShading) {
                model.deferredModel?.let { +it }
            } else {
                model.forwardModel?.let { +it }
            }
        }
    }

    private fun cycleModel(prevModel: GltfModel, newModel: GltfModel, ctx: KoolContext) {
        prevModel.isVisible = false
        ctx.runDelayed(1) {
            prevModel.forwardModel?.dispose(ctx)
            prevModel.deferredModel?.dispose(ctx)
        }

        newModel.isVisible = true
        orbitTransform.zoom = newModel.zoom
        camTranslationTarget = newModel.lookAt
        trackModel = newModel.trackModel
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
        section("glTF models") {
            cycler("Model:", models) { cur, prev -> cycleModel(prev, cur, ctx) }
            sliderWithValue("Animation Speed:", animationSpeed, 0f, 1f, 2) { animationSpeed = value }
            toggleButton("Deferred Shading", useDeferredPipeline) {
                useDeferredPipeline = isEnabled
                setupPipelines()
            }
            toggleButton("Ambient Occlusion", isAo) {
                isAo = isEnabled
                setupPipelines()
            }
            toggleButton("Screen Space Reflections", true) {
                deferredPipeline.isSsrEnabled = isEnabled
                setupPipelines()
            }
            sliderWithValue("SSR Map Size:", 0.7f, 0.1f, 1f, 1) {
                deferredPipeline.reflectionMapSize = (value * 10).roundToInt() / 10f
            }
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled }
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

        var forwardModel: Model? = null
        var deferredModel: Model? = null
        var isVisible: Boolean = false

        var animate: Model.(Double, KoolContext) -> Unit = { t, _ ->
            applyAnimation(t)
        }

        override fun toString() = name

        suspend fun load(isDeferredShading: Boolean, ctx: KoolContext): Model? {
            var model: Model? = null
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

                    enableAnimation(0)
                    onUpdate += {
                        isVisible = this@GltfModel.isVisible
                        animate(animationTime, ctx)
                    }
                }
            }
            if (isDeferredShading) {
                deferredModel = model
            } else {
                forwardModel = model
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
