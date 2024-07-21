package de.fabmax.kool.demo

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.pipeline.deferred.DeferredOutputShader
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.pipeline.deferred.DeferredPipelineConfig
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlinx.coroutines.async
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class GltfDemo : DemoScene("glTF Models") {

    private val foxAnimator = FoxAnimator()
    private val models = listOf(
        GltfModel(
            "Flight Helmet", "${DemoLoader.modelPath}/flight_helmet/FlightHelmet.gltf",
            4f, Vec3f.ZERO, false, Vec3d(0.0, 1.25, 0.0), false, 3.5
        ),
        GltfModel(
            "Polly", "${DemoLoader.modelPath}/project_polly_jpg.glb",
            3f, Vec3f.ZERO, false, Vec3d(0.0, 1.25, 0.0), false, 3.5, normalizeBoneWeights = true
        ),
        GltfModel(
            "Coffee Cart", "${DemoLoader.modelPath}/CoffeeCart_01.glb",
            2f, Vec3f(0f, -0.01f, 0f), false, Vec3d(0.0, 1.75, 0.0), false, 3.5
        ),
        GltfModel(
            "Camera", "${DemoLoader.modelPath}/camera.glb",
            20f, Vec3f.ZERO, true, Vec3d(0.0, 0.5, 0.0), false, 5.0
        ),
        GltfModel(
            "Fox", "${DemoLoader.modelPath}/fox.glb",
            0.01f, Vec3f.ZERO, false, Vec3d(0.0, 1.25, 0.0), true, 3.5
        ).apply { animate = { _ -> foxAnimator.animate(this) } },
        GltfModel(
            "Animated Box", "${DemoLoader.modelPath}/BoxAnimated.gltf",
            1f, Vec3f(0f, 0.5f, 0f), false, Vec3d(0.0, 1.5, 0.0), false, 5.0
        ),
        GltfModel(
            "Morph Cube", "${DemoLoader.modelPath}/AnimatedMorphCube.glb",
            1f, Vec3f(0f, 1f, 0f), false, Vec3d(0.0, 1.0, 0.0), false, 3.5
        ),
        GltfModel(
            "Alpha Mode Test", "${DemoLoader.modelPath}/AlphaBlendModeTest.glb",
            0.5f, Vec3f(0f, 0.06f, 0f), false, Vec3d(0.0, 0.75, 0.0), false, 3.5
        )
    )

    private val selectedModelIdx = mutableStateOf(0)
    private val currentModel: GltfModel get() = models[selectedModelIdx.value]

    private val colorMap by texture2d("${DemoLoader.materialPath}/Fabric030/Fabric030_1K_Color2.jpg")
    private val normalMap by texture2d("${DemoLoader.materialPath}/Fabric030/Fabric030_1K_Normal.jpg")
    private val aoMap by texture2d("${DemoLoader.materialPath}/Fabric030/Fabric030_1K_AmbientOcclusion.jpg")
    private val roughnessMap by texture2d("${DemoLoader.materialPath}/Fabric030/Fabric030_1K_Roughness.jpg")

    private val envMaps by hdriImage("${DemoLoader.hdriPath}/shanghai_bund_1k.rgbe.png")

    private lateinit var orbitTransform: OrbitInputTransform
    private var camTranslationTarget: Vec3d? = null
    private var trackModel = false

    private val shadowsForward = mutableListOf<ShadowMap>()
    private var aoPipelineForward: AoPipeline? = null
    private val contentGroupForward = Node()

    private lateinit var deferredPipeline: DeferredPipeline
    private val contentGroupDeferred = Node()

    private var animationDeltaTime = 0f
    private val animationSpeed = mutableStateOf(0.5f)
    private val isAutoRotate = mutableStateOf(true)

    private val isDeferredShading: MutableStateValue<Boolean> = mutableStateOf(true).onChange { _, new ->
        setupPipelines(new, isAo.value)
    }
    private val isAo: MutableStateValue<Boolean> = mutableStateOf(true).onChange { _, new ->
        setupPipelines(isDeferredShading.value, new)
    }
    private val isSsr: MutableStateValue<Boolean> = mutableStateOf(true).onChange { _, new ->
        deferredPipeline.isSsrEnabled = new
        setupPipelines(isDeferredShading.value, isAo.value)
    }
    private val ssrMapSize = mutableStateOf(0.5f).onChange { _, new -> deferredPipeline.reflectionMapSize = new }

    override fun lateInit(ctx: KoolContext) {
        currentModel.isVisible = true
        trackModel = currentModel.trackModel
    }

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        mainScene.setupLighting()

        // create deferred pipeline
        val defCfg = DeferredPipelineConfig().apply {
            isWithAmbientOcclusion = true
            isWithScreenSpaceReflections = true
            baseReflectionStep = 0.02f
            maxGlobalLights = 2
            isWithVignette = true
            useImageBasedLighting(envMaps)
        }
        deferredPipeline = DeferredPipeline(mainScene, defCfg)
        deferredPipeline.aoPipeline?.apply {
            radius = 0.2f
        }
        ssrMapSize.set(deferredPipeline.reflectionMapSize)

        // create forward pipeline
        aoPipelineForward = AoPipeline.createForward(mainScene).apply {
            radius = 0.2f
        }
        shadowsForward += listOf(
            SimpleShadowMap(mainScene, mainScene.lighting.lights[0], contentGroupForward, mapSize = 2048),
            SimpleShadowMap(mainScene, mainScene.lighting.lights[1], contentGroupForward, mapSize = 2048)
        )

        // load models
        models.map {
            it to Assets.async {
                it.load(false)
                it.load(true)
            }
        }.forEach { (model, deferred) ->
            showLoadText("Loading ${model.name}")
            deferred.await()
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupCamera()

        addNode(Skybox.cube(envMaps.reflectionMap, 1.5f))

        makeDeferredContent()
        makeForwardContent()
        setupPipelines(isDeferredShading.value, isAo.value)

        onUpdate {
            animationDeltaTime = Time.deltaT * animationSpeed.value
            foxAnimator.updatePosition()
        }
    }

    private fun setupPipelines(isDeferred: Boolean, isAo: Boolean) {
        val fwdState = !isDeferred

        contentGroupForward.isVisible = fwdState
        shadowsForward.forEach { it.isShadowMapEnabled = fwdState }
        aoPipelineForward?.isEnabled = fwdState && isAo

        contentGroupDeferred.isVisible = isDeferred
        deferredPipeline.isEnabled = isDeferred
        deferredPipeline.isAoEnabled = isAo
    }

    private fun Scene.makeForwardContent() {
        contentGroupForward.setupContentGroup(false)
        addNode(contentGroupForward)
    }

    private fun Scene.makeDeferredContent() {
        deferredPipeline.sceneContent.setupContentGroup(true)

        // main scene only contains a quad used to draw the deferred shading output
        contentGroupDeferred.apply {
            isFrustumChecked = false
            val outputMesh = deferredPipeline.createDefaultOutputQuad()
            (outputMesh.shader as? DeferredOutputShader)?.setupVignette(0f)
            addNode(outputMesh)
        }
        addNode(contentGroupDeferred)
    }

    private fun Scene.setupCamera() {
        orbitTransform = orbitCamera {
            setMouseRotation(0f, -30f)
            zoom = currentModel.zoom
            translation.set(currentModel.lookAt)

            onUpdate += {
                var translationTarget = camTranslationTarget
                if (trackModel) {
                    val model = currentModel.forwardModel
                    model?.let {
                        val center = model.globalCenter
                        translationTarget = Vec3d(center.x.toDouble(), center.y.toDouble(), center.z.toDouble())
                    }
                } else if (isAutoRotate.value) {
                    verticalRotation -= Time.deltaT * 3f
                }

                translationTarget?.let {
                    val v = MutableVec3d(translation).mul(0.9).add(MutableVec3d(it).mul(0.1))
                    translation.set(v)
                    if (v.distance(it) < 0.01) {
                        camTranslationTarget = null
                    }
                }
            }
        }
    }

    private fun Scene.setupLighting() {
        lighting.clear()
        lighting.addSpotLight {
            val pos = Vec3f(7f, 8f, 8f)
            val lookAt = Vec3f.ZERO
            setup(pos, lookAt.subtract(pos, MutableVec3f()).norm(), 25f.deg)
            setColor(Color.WHITE.mix(MdColor.AMBER, 0.3f).toLinear(), 500f)
        }
        lighting.addSpotLight {
            val pos = Vec3f(-7f, 8f, 8f)
            val lookAt = Vec3f.ZERO
            setup(pos, lookAt.subtract(pos, MutableVec3f()).norm(), 25f.deg)
            setColor(Color.WHITE.mix(MdColor.AMBER, 0.3f).toLinear(), 500f)
        }
    }

    private fun Node.setupContentGroup(isDeferredShading: Boolean) {
        transform.rotate((-60.0).deg, Vec3d.Y_AXIS)
        onUpdate {
            if (isAutoRotate.value) {
                transform.setIdentity()
                transform.rotate((Time.gameTime * 3).deg, Vec3d.Y_AXIS)
            }
        }

        addTextureMesh(isNormalMapped = true) {
            generate {
                roundCylinder(4.1f, 0.2f)
            }

            fun KslPbrShader.Config.Builder.materialConfig() {
                color { textureColor(colorMap) }
                normalMapping { setNormalMap(normalMap) }
                ao { textureProperty(aoMap) }
                roughness { textureProperty(roughnessMap) }
            }

            shader = if (isDeferredShading) {
                deferredKslPbrShader {
                    materialConfig()
                }
            } else {
                KslPbrShader {
                    materialConfig()
                    lighting {
                        enableSsao(aoPipelineForward?.aoMap)
                        addShadowMaps(shadowsForward)
                        imageBasedAmbientLight(envMaps.irradianceMap)
                    }
                    reflectionMap = envMaps.reflectionMap
                }
            }
        }

        models.forEach { model ->
            if (isDeferredShading) {
                model.deferredModel?.let { addNode(it) }
            } else {
                model.forwardModel?.let { addNode(it) }
            }
        }
    }

    private fun cycleModel(prevModel: GltfModel, newModel: GltfModel) {
        // make model invisible, but do not release it, so that we can switch back to it
        prevModel.isVisible = false

        newModel.isVisible = true
        orbitTransform.zoom = newModel.zoom
        camTranslationTarget = newModel.lookAt
        trackModel = newModel.trackModel
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuRow {
            Text("Model") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(models)
                    .selectedIndex(selectedModelIdx.use())
                    .onItemSelected {
                        val prevModel = currentModel
                        selectedModelIdx.set(it)
                        cycleModel(prevModel, currentModel)
                    }
            }
        }
        if (currentModel.name == "Fox") {
            MenuSlider2("Movement speed", animationSpeed.use(), 0f, 1f) { animationSpeed.set(it) }
        }

        Text("Options") { sectionTitleStyle() }
        LabeledSwitch("Deferred shading", isDeferredShading)
        LabeledSwitch("Ambient occlusion", isAo)
        if (isDeferredShading.value) {
            LabeledSwitch("Screen space reflections", isSsr)
            MenuSlider2("SSR map size", ssrMapSize.use(), 0.1f, 1f, { it.toString(1) }) {
                ssrMapSize.set((it * 10).roundToInt() / 10f)
            }
        }
        LabeledSwitch("Auto rotate view", isAutoRotate)
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
                uv.mul(uvScale)
                uv.rotate(a.rad)
                val pt = cpt.rotate(a.rad, Vec3f.Y_AXIS, MutableVec3f())
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
        val zoom: Double,
        val normalizeBoneWeights: Boolean = false
    ) {

        var forwardModel: Model? = null
        var deferredModel: Model? = null
        var isVisible: Boolean = false

        var animate: Model.(Float) -> Unit = { dt ->
            applyAnimation(dt)
        }

        override fun toString() = name

        suspend fun load(isDeferredShading: Boolean): Model {
            val materialCfg = GltfMaterialConfig(
                shadowMaps = if (isDeferredShading) deferredPipeline.shadowMaps else shadowsForward,
                scrSpcAmbientOcclusionMap = if (isDeferredShading) deferredPipeline.aoPipeline?.aoMap else aoPipelineForward?.aoMap,
                environmentMaps = envMaps,
                isDeferredShading = isDeferredShading
            )
            val modelCfg = GltfLoadConfig(
                generateNormals = generateNormals,
                materialConfig = materialCfg,
                loadAnimations = true,
                applyMorphTargets = true,
                applySkins = true,
                applyTransforms = true,
                mergeMeshesByMaterial = true
            )
            val model = Assets.loadGltfModel(assetPath, modelCfg).apply {
                transform.translate(translation)
                transform.scale(scale)

                if (normalizeBoneWeights) {
                    meshes.values.forEach { mesh ->
                        mesh.geometry.forEach { v ->
                            v.weights.mul(1f / (v.weights.x + v.weights.y + v.weights.z + v.weights.w))
                        }
                    }
                }

                enableAnimation(0)
                onUpdate += {
                    isVisible = this@GltfModel.isVisible
                    animate(animationDeltaTime)
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

        fun updatePosition() {
            val speed = animationSpeed.value * 2
            angle += speed * Time.deltaT / radius
        }

        fun animate(model: Model) {
            // mix survey / walk / run animations according to animation speed
            if (animationSpeed.value < 0.5f) {
                val w1 = animationSpeed.value * 2f
                val w0 = 1f - w1
                model.setAnimationWeight(0, w0)
                model.setAnimationWeight(1, w1)
                model.setAnimationWeight(2, 0f)
            } else {
                val w1 = (animationSpeed.value - 0.5f) * 2f
                val w0 = 1f - w1
                model.setAnimationWeight(0, 0f)
                model.setAnimationWeight(1, w0)
                model.setAnimationWeight(2, w1)
            }
            model.applyAnimation(Time.deltaT)

            // move model according to animation speed
            model.transform.setIdentity()
            position.set(radius, 0.0, 0.0).rotate(angle.rad, Vec3d.Y_AXIS)
            model.transform.translate(position)
            model.transform.rotate((angle.toDeg() + 180).deg, Vec3d.Y_AXIS)
            model.transform.scale(0.01)
        }
    }
}
