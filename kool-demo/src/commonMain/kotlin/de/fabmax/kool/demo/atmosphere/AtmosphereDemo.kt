package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.atmosphere.OpticalDepthLutPass
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.pipeline.deferred.DeferredPipelineConfig
import de.fabmax.kool.pipeline.deferred.PbrSceneShader
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.unlitShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.pow

class AtmosphereDemo : DemoScene("Atmosphere") {

    val sunColor = Color.WHITE
    val sun = Light().apply {
        setDirectional(Vec3f.NEG_Z_AXIS)
        setColor(sunColor, 5f)
    }
    private val sunIntensity = mutableStateOf(1f).onChange { updateSun() }

    val time = mutableStateOf(0.5f)
    var moonTime = 0f
    private val isAnimateTime = mutableStateOf(true)
    private val camHeightTxt = mutableStateOf("0 km")

    val textures = mutableMapOf<String, Texture2d>()
    val textures1d = mutableMapOf<String, Texture1d>()
    lateinit var deferredPipeline: DeferredPipeline
    val atmoShader = AtmosphericScatteringShader()

    val earthGroup = Group("earth")

    private lateinit var opticalDepthLutPass: OpticalDepthLutPass
    private val shadows = mutableListOf<SimpleShadowMap>()
    private val camTransform = EarthCamTransform(earthRadius)

    private var sceneSetupComplete = false

    private val atmoThickness = mutableStateOf(0f).onChange { updateAtmosphereThickness(it) }
    private val atmoFalloff = mutableStateOf(0f).onChange { opticalDepthLutPass.densityFalloff = it }

    private val scatteringR = mutableStateOf(0f).onChange { updateScatteringCoeffs(x = it) }
    private val scatteringG = mutableStateOf(0f).onChange { updateScatteringCoeffs(y = it) }
    private val scatteringB = mutableStateOf(0f).onChange { updateScatteringCoeffs(z = it) }

    private val rayleighR = mutableStateOf(0f).onChange { updateRayleighColor(r = it) }
    private val rayleighG = mutableStateOf(0f).onChange { updateRayleighColor(g = it) }
    private val rayleighB = mutableStateOf(0f).onChange { updateRayleighColor(b = it) }
    private val rayleighA = mutableStateOf(0f).onChange { updateRayleighColor(strength = it) }

    private val mieStr = mutableStateOf(0f).onChange { updateMieColor(strength = it) }
    private val mieG = mutableStateOf(0f).onChange { atmoShader.mieG = it }

    val cameraHeight: Float
        get() {
            return mainScene.camera.globalPos.distance(Vec3f.ZERO) - earthRadius
        }

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        loadTex(texMilkyway, "${DemoLoader.assetStorageBase}/solarsystem/stars_bg.jpg")
        loadTex(texSun, "${DemoLoader.assetStorageBase}/solarsystem/sun.png")
        loadTex(texSunBg, "${DemoLoader.assetStorageBase}/solarsystem/sun_bg.png")
        loadTex(texMoon, "${DemoLoader.assetStorageBase}/solarsystem/moon.jpg")

        loadTex(EarthShader.texEarthDay, "${DemoLoader.assetStorageBase}/solarsystem/earth_day.jpg")
        loadTex(EarthShader.texEarthNight, "${DemoLoader.assetStorageBase}/solarsystem/earth_night.jpg")
        loadTex(EarthShader.texEarthNrm, "${DemoLoader.assetStorageBase}/solarsystem/earth_nrm.jpg")
        loadTex(EarthShader.texOceanNrm, "${DemoLoader.assetStorageBase}/solarsystem/oceanNrm.jpg")
        val heightMapProps = TextureProps(TexFormat.R, AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE)
        loadTex(EarthShader.texEarthHeight, "${DemoLoader.assetStorageBase}/solarsystem/earth_height_8k.png", heightMapProps)
    }

    override fun lateInit(ctx: KoolContext) {
        camTransform.apply {
            +mainScene.camera
        }
    }

    private suspend fun AssetManager.loadTex(key: String, path: String, props: TextureProps = TextureProps()) {
        showLoadText("Loading texture \"$key\"...")
        textures[key] = loadAndPrepareTexture(path, props)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        opticalDepthLutPass = OpticalDepthLutPass()
        addOffscreenPass(opticalDepthLutPass)

        lighting.lights.clear()
        lighting.lights += sun
        shadows += SimpleShadowMap(this, 0)

        val defCfg = DeferredPipelineConfig().apply {
            isWithAmbientOcclusion = false
            isWithScreenSpaceReflections = false
            maxGlobalLights = 1
            shadowMaps = shadows
            pbrSceneShader = makeDeferredPbrShader(this)
            isWithVignette = true
            isWithChromaticAberration = true
        }
        deferredPipeline = DeferredPipeline(this, defCfg)
        deferredPipeline.lightingPassShader.ambient(Color(0.05f, 0.05f, 0.05f).toLinear())

        atmoShader.apply {
            opticalDepthLut(opticalDepthLutPass.colorTexture)
            surfaceRadius = earthRadius
            atmosphereRadius = 6500f / kmPerUnit

            scatteringCoeffs = Vec3f(0.75f, 1.10f, 1.35f)
            rayleighColor = Color(0.5f, 0.5f, 1f, 1f)
            mieColor = Color(1f, 0.35f, 0.35f, 0.5f)
            mieG = 0.8f
            scatteringCoeffStrength = 1.0f
        }
        deferredPipeline.onSwap += atmoShader

        val shadowScene = Group().apply {
            isFrustumChecked = false
            +colorMesh("shadowEarth") {
                isFrustumChecked = false
                generate {
                    icoSphere {
                        steps = 5
                        radius = earthRadius * 1f
                    }
                }
                shader = unlitShader {  }
            }
        }
        earthGroup += shadowScene

        shadows.forEach { shadow ->
            //shadow.drawNode = deferredPipeline.contentGroup
            shadow.drawNode = shadowScene
            shadow.shadowBounds = earthGroup.bounds
            //shadow.shadowBounds = deferredPipeline.contentGroup.bounds
        }

        onUpdate += {
            if (!sceneSetupComplete) {
                sceneSetupComplete = true
                finalizeSceneSetup(deferredPipeline)

                earthGroup -= shadowScene
            }

            (mainScene.camera as PerspectiveCamera).apply {
                val h = globalPos.length() - earthRadius
                position.set(Vec3f.ZERO)
                lookAt.set(Vec3f.NEG_Z_AXIS)
                clipNear = (h * 0.5f).clamp(0.003f, 5f)
                clipFar = clipNear * 1000f
                camHeightTxt.set("${(cameraHeight * kmPerUnit).toString(1)} km")
            }

            if (isAnimateTime.value) {
                val dt = Time.deltaT / 120
                // setting time slider value results in timer slider's onChange function being called which also sets time
                time.set((time.value + dt) % 1f)
                moonTime = (moonTime + dt / moonT)
            }
        }

        onDispose += {
            textures.values.forEach { it.dispose() }
            textures1d.values.forEach { it.dispose() }
        }

        atmoFalloff.set(opticalDepthLutPass.densityFalloff)
        atmoThickness.set((atmoShader.atmosphereRadius - earthRadius) * kmPerUnit)
        scatteringR.set(atmoShader.scatteringCoeffs.x)
        scatteringG.set(atmoShader.scatteringCoeffs.y)
        scatteringB.set(atmoShader.scatteringCoeffs.z)
        rayleighR.set(atmoShader.rayleighColor.r)
        rayleighG.set(atmoShader.rayleighColor.g)
        rayleighB.set(atmoShader.rayleighColor.b)
        rayleighA.set(atmoShader.rayleighColor.a)
        mieStr.set(atmoShader.mieColor.a)
        mieG.set(atmoShader.mieG)
    }

    private fun makeDeferredPbrShader(cfg: DeferredPipelineConfig): PbrSceneShader {
        val shaderCfg = PbrSceneShader.DeferredPbrConfig().apply {
            isScrSpcAmbientOcclusion = cfg.isWithAmbientOcclusion
            isScrSpcReflections = cfg.isWithScreenSpaceReflections
            maxLights = cfg.maxGlobalLights
            shadowMaps += shadows
            useImageBasedLighting(cfg.environmentMaps)
        }

        val model = PbrSceneShader.defaultDeferredPbrModel(shaderCfg).apply {
            fragmentStage {
                val lightNd = findNodeByType<MultiLightNode>()!!
                val pbrNd = findNodeByType<PbrMaterialNode>()!!

                val lightGradientTex = texture1dNode("tLightGradient")
                addNode(EarthLightColorNode(lightGradientTex, stage)).apply {
                    inWorldPos = pbrNd.inFragPos
                    inFragToLight = lightNd.outFragToLightDirection
                    inRadiance = lightNd.outRadiance
                    pbrNd.inRadiance = outRadiance
                }
            }
        }

        val lightGradientTex = GradientTexture(ColorGradient(
                cos(90f.toRad()) to MdColor.ORANGE.mix(Color.WHITE, 0.6f).toLinear(),
                cos(85f.toRad()) to MdColor.AMBER.mix(Color.WHITE, 0.6f).toLinear(),
                cos(80f.toRad()) to Color.WHITE.toLinear(),
                cos(0f.toRad()) to Color.WHITE.toLinear()
        ))
        textures1d[EarthShader.texLightGradient] = lightGradientTex

        return PbrSceneShader(shaderCfg, model).apply {
            onPipelineCreated += { _, _, _ ->
                model.findNode<Texture1dNode>("tLightGradient")?.sampler?.texture = lightGradientTex
            }
        }
    }

    private class EarthLightColorNode(val lightColorGradient: Texture1dNode, graph: ShaderGraph) : ShaderNode("earthLightColor", graph) {
        lateinit var inWorldPos: ShaderNodeIoVar
        lateinit var inFragToLight: ShaderNodeIoVar
        lateinit var inRadiance: ShaderNodeIoVar

        val outRadiance = ShaderNodeIoVar(ModelVar3f("${name}_outRadiance"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inWorldPos, inFragToLight, inRadiance)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                float ${name}_l = clamp(dot(normalize(${inWorldPos.ref3f()}), ${inFragToLight.ref3f()}), 0.0, 1.0);
                ${outRadiance.declare()} = ${inRadiance.ref3f()} * ${generator.sampleTexture1d(lightColorGradient.name, "${name}_l")}.rgb;
            """)
        }
    }

    private fun finalizeSceneSetup(deferredPipeline: DeferredPipeline) {
        val skyPass = SkyPass(this)
        atmoShader.skyColor(skyPass.colorTexture)

        deferredPipeline.sceneContent.setupContent()

        mainScene.apply {
            +textureMesh {
                isFrustumChecked = false
                generate {
                    rect {
                        mirrorTexCoordsY()
                    }
                }
                shader = atmoShader
            }
        }

        updateSun()
    }

    private fun Group.setupContent() {
        +earthGroup.apply {
            isFrustumChecked = false
            +camTransform

            val gridSystem = SphereGridSystem().apply {
                val earthShader = EarthShader(textures)
                earthShader.heightMap = textures[EarthShader.texEarthHeight]
                earthShader.oceanNrmTex = textures[EarthShader.texOceanNrm]
                shader = earthShader

                onUpdate += {
                    updateTiles(mainScene.camera.globalPos)

                    val camHeight = cameraHeight * kmPerUnit
                    val colorMix = (camHeight / 100f).clamp()
                    earthShader.uWaterColor?.value?.set(waterColorLow.mix(waterColorHigh, colorMix))
                    earthShader.uNormalShift?.value?.set(
                        Time.gameTime.toFloat() * 0.0051f, Time.gameTime.toFloat() * 0.0037f,
                        Time.gameTime.toFloat() * -0.0071f, Time.gameTime.toFloat() * -0.0039f)

                    val dirToSun = MutableVec3f(sun.direction).scale(-1f)
                    earthShader.uDirToSun?.value?.let { uSunDir ->
                        uSunDir.set(dirToSun)
                        toLocalCoords(uSunDir, 0f)
                    }
                    atmoShader.dirToSun = dirToSun
                }
            }
            +gridSystem


            onUpdate += {
                setIdentity()
                rotate(earthAxisTilt, Vec3f.NEG_X_AXIS)
                rotate(time.value * 360, Vec3f.Y_AXIS)
            }
        }
    }

    private fun UiScope.ColoredMenuSlider(
        color: Color,
        value: Float,
        min: Float,
        max: Float,
        txtFormat: (Float) -> String = { it.toString(2) },
        txtWidth: Dp = UiSizes.baseSize,
        onChange: (Float) -> Unit
    ) {
        Slider(value, min, max) {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
                .margin(horizontal = sizes.gap)
                .onChange(onChange)
                .colors(color, color.withAlpha(0.4f), color.withAlpha(0.7f))
        }
        if (txtWidth.value > 0f) {
            Text(txtFormat(value)) {
                labelStyle()
                modifier.width(txtWidth).textAlignX(AlignmentX.End)
            }
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        val lblW = UiSizes.baseSize * 1f
        val txtW = UiSizes.baseSize * 0.85f

        val timeFmt: (Float) -> String = {
            val t = it * 24
            val h = t.toInt()
            val m = ((t % 1f) * 60).toInt()
            val m0 = if (m < 10) "0" else ""
            "$h:$m0$m"
        }

        MenuRow {
            Text("Sun") { labelStyle(lblW) }
            MenuSlider(sunIntensity.use(), 0.1f, 5f, txtWidth = txtW) { sunIntensity.set(it) }
        }
        MenuRow {
            Text("Time") { labelStyle(lblW) }
            MenuSlider(time.use(), 0f, 1f, timeFmt, txtWidth = txtW) {
                time.set(it)
                updateSun()
            }
        }
        LabeledSwitch("Animate time", isAnimateTime)
        MenuRow {
            Text("Camera height") { labelStyle(Grow.Std) }
            Text(camHeightTxt.use()) { labelStyle() }
        }

        Text("Atmosphere") { sectionTitleStyle() }
        MenuRow {
            Text("Thickness") { labelStyle(UiSizes.baseSize * 1.75f) }
            MenuSlider(
                atmoThickness.use(),
                10f,
                1000f,
                { it.toString(0) },
                txtWidth = txtW)
            { atmoThickness.set(it) }
        }
        MenuRow {
            Text("Falloff") { labelStyle(UiSizes.baseSize * 1.75f) }
            MenuSlider(atmoFalloff.use(), 0f, 15f, txtWidth = txtW) { atmoFalloff.set(it) }
        }

        Text("Scattering") { sectionTitleStyle() }
        MenuRow {
            Text("Red") { labelStyle(lblW) }
            ColoredMenuSlider(MdColor.RED, scatteringR.use(), 0f, 4f, txtWidth = txtW) { scatteringR.set(it) }
        }
        MenuRow {
            Text("Green") { labelStyle(lblW) }
            ColoredMenuSlider(MdColor.GREEN, scatteringG.use(), 0f, 4f, txtWidth = txtW) { scatteringG.set(it) }
        }
        MenuRow {
            Text("Blue") { labelStyle(lblW) }
            ColoredMenuSlider(MdColor.BLUE, scatteringB.use(), 0f, 4f, txtWidth = txtW) { scatteringB.set(it) }
        }

        Text("Rayleigh") { sectionTitleStyle() }
        MenuRow {
            Text("Red") { labelStyle(lblW) }
            ColoredMenuSlider(MdColor.RED, rayleighR.use(), 0f, 4f, txtWidth = txtW) { rayleighR.set(it) }
        }
        MenuRow {
            Text("Green") { labelStyle(lblW) }
            ColoredMenuSlider(MdColor.GREEN, rayleighG.use(), 0f, 4f, txtWidth = txtW) { rayleighG.set(it) }
        }
        MenuRow {
            Text("Blue") { labelStyle(lblW) }
            ColoredMenuSlider(MdColor.BLUE, rayleighB.use(), 0f, 4f, txtWidth = txtW) { rayleighB.set(it) }
        }
        MenuRow {
            Text("Str") { labelStyle(lblW) }
            MenuSlider(rayleighA.use(), 0f, 2f, txtWidth = txtW) { rayleighA.set(it) }
        }

        Text("Mie") { sectionTitleStyle() }
        MenuRow {
            Text("Str") { labelStyle(lblW) }
            MenuSlider(mieStr.use(), 0f, 2f, txtWidth = txtW) { mieStr.set(it) }
        }
        MenuRow {
            Text("g") { labelStyle(lblW) }
            MenuSlider(mieG.use(), 0.5f, 0.999f, txtFormat = { it.toString(3) }, txtWidth = txtW) { mieG.set(it) }
        }
    }

    private fun updateSun() {
        val lightDir = MutableVec3f(0f, 0f, -1f)
        atmoShader.dirToSun = lightDir
        atmoShader.sunColor = sunColor.withAlpha(sunIntensity.value)

        mainScene.lighting.lights[0].apply {
            setDirectional(MutableVec3f(lightDir).scale(-1f))
            setColor(sunColor, sunIntensity.value * 5)
        }
    }

    private fun updateRayleighColor(r: Float = atmoShader.rayleighColor.r, g: Float = atmoShader.rayleighColor.g, b: Float = atmoShader.rayleighColor.b, strength: Float = atmoShader.rayleighColor.a) {
        atmoShader.rayleighColor = Color(r, g, b, strength)
    }

    private fun updateMieColor(r: Float = atmoShader.mieColor.r, g: Float = atmoShader.mieColor.g, b: Float = atmoShader.mieColor.b, strength: Float = atmoShader.mieColor.a) {
        atmoShader.mieColor = Color(r, g, b, strength)
    }

    private fun updateScatteringCoeffs(x: Float = atmoShader.scatteringCoeffs.x, y: Float = atmoShader.scatteringCoeffs.y, z: Float = atmoShader.scatteringCoeffs.z) {
        atmoShader.scatteringCoeffs = Vec3f(x, y, z)
    }

    private fun updateAtmosphereThickness(newThickness: Float) {
        val atmoRadius = earthRadius + newThickness / kmPerUnit
        atmoShader.atmosphereRadius = atmoRadius
        opticalDepthLutPass.atmosphereRadius = atmoRadius
    }

    companion object {
        const val kmPerUnit = 100f
        const val earthRadius = 6000f / kmPerUnit
        const val earthAxisTilt = 15f //23.44f

        const val moonRadius = 1750f / kmPerUnit
        const val moonDistScale = 0.25f
        const val moonDist = 384400 / kmPerUnit * moonDistScale
        const val moonInclination = 5.145f

        // scaled moon orbital period (according to kepler's 3rd law)
        val keplerC = (moonDist / moonDistScale).pow(3) / 27.32f.pow(2)
        val moonT = moonDist.pow(3) / keplerC

        val waterColorLow = Color.fromHex("0D1F56").toLinear()
        val waterColorHigh = Color.fromHex("020514").toLinear()

        const val texMilkyway = "Milkyway"
        const val texSun = "Sun"
        const val texSunBg = "Sun Background"
        const val texMoon = "Moon"
    }
}