package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.UnlitShaderConfig
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.deferred.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import kotlin.math.roundToInt
import kotlin.random.Random

class DeferredDemo : DemoScene("Deferred Shading") {

    private lateinit var deferredPipeline: DeferredPipeline

    private lateinit var objects: Mesh
    private lateinit var objectShader: DeferredKslPbrShader

    private lateinit var lightPositionMesh: Mesh
    private lateinit var lightVolumeMesh: LineMesh
    private val lightPosInsts = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS), MAX_LIGHTS)
    private val lightVolInsts = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS), MAX_LIGHTS)

    private val rand = Random(1337)

    private val isShowMaps = mutableStateOf(false)
    private val isAutoRotate = mutableStateOf(true)
    private val lightCount = mutableStateOf(2000)
    private val lightPower = mutableStateOf(1f)
    private val lightRadius = mutableStateOf(1f)
    private val isObjects = mutableStateOf(true).onChange { _, new -> objects.isVisible = new }
    private val isLightBodies = mutableStateOf(true).onChange { _, new -> lightPositionMesh.isVisible = new }
    private val isLightVolumes = mutableStateOf(false).onChange { _, new -> lightVolumeMesh.isVisible = new }
    private val roughness = mutableStateOf(0.15f).onChange { _, new -> objectShader.roughness = new }
    private val bloomStrength = mutableStateOf(0.75f).onChange { _, new -> deferredPipeline.bloomStrength = new }
    private val bloomRadius = mutableStateOf(0.5f).onChange { _, new -> deferredPipeline.bloomScale = new }
    private val bloomThreshold = mutableStateOf(0.5f).onChange { _, new ->
        deferredPipeline.bloom?.lowerThreshold = new
        deferredPipeline.bloom?.upperThreshold = new + 0.5f
    }

    private val ibl by hdriSingleColor(Color(0.22f, 0.22f, 0.22f))
    private val groundColor by texture2d("${DemoLoader.materialPath}/futuristic-panels1/futuristic-panels1-albedo1.jpg")
    private val groundNormals by texture2d("${DemoLoader.materialPath}/futuristic-panels1/futuristic-panels1-normal.jpg")
    private val groundRoughness by texture2d("${DemoLoader.materialPath}/futuristic-panels1/futuristic-panels1-roughness.jpg")
    private val groundMetallic by texture2d("${DemoLoader.materialPath}/futuristic-panels1/futuristic-panels1-metallic.jpg")
    private val groundAo by texture2d("${DemoLoader.materialPath}/futuristic-panels1/futuristic-panels1-ao.jpg")

    private val lights = mutableListOf<AnimatedLight>()

    private val colorMap = listOf(
        ColorMap(
            "Colorful", listOf(
                MdColor.RED, MdColor.PINK, MdColor.PURPLE, MdColor.DEEP_PURPLE,
                MdColor.INDIGO, MdColor.BLUE, MdColor.LIGHT_BLUE, MdColor.CYAN, MdColor.TEAL, MdColor.GREEN,
                MdColor.LIGHT_GREEN, MdColor.LIME, MdColor.YELLOW, MdColor.AMBER, MdColor.ORANGE, MdColor.DEEP_ORANGE
            )
        ),
        ColorMap("Hot-Cold", listOf(MdColor.PINK, MdColor.CYAN)),
        ColorMap("Summer", listOf(MdColor.ORANGE, MdColor.BLUE, MdColor.GREEN)),
        ColorMap("Sepia", listOf(MdColor.ORANGE tone 100))
    )
    val colorMapIdx = mutableStateOf(1)

    override fun lateInit(ctx: KoolContext) {
        updateLights()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        orbitCamera {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(0f, -40f)
            setZoom(28.0, max = 50.0)

            translation.set(0.0, -11.0, 0.0)
            onUpdate += {
                if (isAutoRotate.value) {
                    verticalRotation += Time.deltaT * 3f
                }
            }
        }

        // don't use any global lights
        lighting.clear()

        val defCfg = DeferredPipelineConfig().apply {
            maxGlobalLights = 1
            isWithAmbientOcclusion = true
            isWithScreenSpaceReflections = false
            isWithImageBasedLighting = false
            isWithBloom = true
            isWithVignette = true
            isWithChromaticAberration = true

            // set output depth compare op to ALWAYS, so that the skybox with maximum depth value is drawn
            outputDepthTest = DepthCompareOp.ALWAYS
        }
        deferredPipeline = DeferredPipeline(this, defCfg)
        deferredPipeline.apply {
            bloomScale = this@DeferredDemo.bloomRadius.value
            bloomStrength = this@DeferredDemo.bloomStrength.value
            setBloomBrightnessThresholds(bloomThreshold.value, bloomThreshold.value + 0.5f)

            lightingPassContent += Skybox.cube(ibl.reflectionMap, 1f, colorSpaceConversion = ColorSpaceConversion.AsIs)
        }
        deferredPipeline.sceneContent.makeContent()
        addNode(deferredPipeline.createDefaultOutputQuad())
        makeLightOverlays()

        onUpdate += {
            lights.forEach { it.animate(Time.deltaT) }
        }
    }

    private fun Scene.makeLightOverlays() {
        apply {
            lightVolumeMesh = addWireframeMesh(deferredPipeline.dynamicPointLights.mesh.geometry, instances = lightVolInsts).apply {
                isFrustumChecked = false
                isVisible = false
                isCastingShadow = false
                shader = KslUnlitShader {
                    vertices { isInstanced = true }
                    color { instanceColor(Attribute.COLORS) }
                    colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
                }
            }

            val lightModelMat = MutableMat4f()
            onUpdate += {
                if (lightPositionMesh.isVisible || lightVolumeMesh.isVisible) {
                    lightPosInsts.clear()
                    lightVolInsts.clear()

                    deferredPipeline.dynamicPointLights.lightInstances.forEach { light ->
                        lightModelMat.setIdentity()
                        lightModelMat.translate(light.position)

                        if (lightPositionMesh.isVisible) {
                            lightPosInsts.addInstance {
                                lightModelMat.putTo(this)
                                light.color.putTo(this)
                            }
                        }
                        if (lightVolumeMesh.isVisible) {
                            lightModelMat.scale(light.radius)
                            lightVolInsts.addInstance {
                                lightModelMat.putTo(this)
                                light.color.putTo(this)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Node.makeContent() {
        objects = addColorMesh {
            generate {
                val sphereProtos = mutableListOf<IndexedVertexList>()
                for (i in 0..10) {
                    val builder = MeshBuilder(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS))
                    sphereProtos += builder.geometry
                    builder.apply {
                        icoSphere {
                            steps = 3
                            radius = rand.randomF(0.3f, 0.4f)
                            center.set(0f, 0.1f + radius, 0f)
                        }
                    }
                }

                for (x in -19..19) {
                    for (y in -19..19) {
                        color = Color.WHITE
                        withTransform {
                            translate(x.toFloat(), 0f, y.toFloat())
                            if ((x + 100) % 2 == (y + 100) % 2) {
                                cube {
                                    size.set(
                                        rand.randomF(0.6f, 0.8f),
                                        rand.randomF(0.6f, 0.95f),
                                        rand.randomF(0.6f, 0.8f)
                                    )
                                    origin.set(0f, 0.1f + size.y * 0.5f, 0f)
                                }
                            } else {
                                geometry(sphereProtos[rand.randomI(sphereProtos.indices)])
                            }
                        }
                    }
                }
            }
            objectShader = deferredKslPbrShader {
                color { vertexColor() }
                roughness(0.15f)
            }
            shader = objectShader
        }

        lightPositionMesh = addMesh(Attribute.POSITIONS, Attribute.NORMALS, instances = lightPosInsts) {
            isFrustumChecked = false
            isVisible = true
            isCastingShadow = false
            generate {
                icoSphere {
                    steps = 1
                    radius = 0.05f
                    center.set(Vec3f.ZERO)
                }
            }
            shader = deferredKslPbrShader {
                vertices { isInstanced = true }
                emission {
                    instanceColor(Attribute.COLORS)
                    constColor(Color(2f, 2f, 2f), blendMode = ColorBlockConfig.BlendMode.Multiply)
                }
            }
        }

        addTextureMesh(isNormalMapped = true) {
            generate {
                rotate(90f.deg, Vec3f.NEG_X_AXIS)
                color = Color.WHITE
                rect {
                    size.set(40f, 40f)
                    generateTexCoords(30f)
                }
            }

            shader = deferredKslPbrShader {
                color { textureColor(groundColor) }
                normalMapping { setNormalMap(groundNormals) }
                roughness { textureProperty(groundRoughness) }
                metallic { textureProperty(groundMetallic) }
                ao { textureProperty(groundAo) }
            }
        }
    }

    private fun updateLights(forced: Boolean = false) {
        val rows = 41
        val travel = rows.toFloat()
        val start = travel / 2

        val objOffset = if (objects.isVisible) 0.7f else 0f
        val lightGroups = listOf(
            LightGroup(Vec3f(1 - start, 0.45f, -start), Vec3f(1f, 0f, 0f), Vec3f(0f, 0f, 1f), rows - 1),
            LightGroup(Vec3f(-start, 0.45f, 1 - start), Vec3f(0f, 0f, 1f), Vec3f(1f, 0f, 0f), rows - 1),

            LightGroup(Vec3f(1.5f - start, 0.45f + objOffset, start), Vec3f(1f, 0f, 0f), Vec3f(0f, 0f, -1f), rows - 2),
            LightGroup(Vec3f(start, 0.45f + objOffset, 1.5f - start), Vec3f(0f, 0f, 1f), Vec3f(-1f, 0f, 0f), rows - 2)
        )

        if (forced) {
            lights.clear()
            deferredPipeline.dynamicPointLights.lightInstances.clear()
        } else {
            while (lights.size > lightCount.value) {
                lights.removeAt(lights.lastIndex)
                deferredPipeline.dynamicPointLights.lightInstances.removeAt(deferredPipeline.dynamicPointLights.lightInstances.lastIndex)
            }
        }

        while (lights.size < lightCount.value) {
            val grp = lightGroups[rand.randomI(lightGroups.indices)]
            val x = rand.randomI(0 until grp.rows)
            val light = deferredPipeline.dynamicPointLights.addPointLight { }
            val animLight = AnimatedLight(light).apply {
                startColor = colorMap[colorMapIdx.value].getColor(lights.size).toLinear()
                desiredColor = startColor
                colorMix = 1f
            }
            lights += animLight
            grp.setupLight(animLight, x, travel, rand.randomF())
        }

        updateLightColors()
        deferredPipeline.dynamicPointLights.lightInstances.forEach {
            it.radius = lightRadius.value
            it.intensity = lightPower.value
        }
    }

    private fun updateLightColors() {
        lights.forEachIndexed { iLight, it ->
            it.startColor = it.desiredColor
            it.desiredColor = colorMap[colorMapIdx.value].getColor(iLight).toLinear()
            it.colorMix = 0f
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        val lblSize = UiSizes.baseSize * 2f
        val txtSize = UiSizes.baseSize * 0.75f

        MenuSlider2("Number of lights", lightCount.use().toFloat(), 1f, MAX_LIGHTS.toFloat(), { "${it.roundToInt()}" }) {
            lightCount.set(it.roundToInt())
            updateLights()
        }
        MenuSlider2("Light power", lightPower.use().toFloat(), 0f, 10f) {
            lightPower.set(it)
            updateLights()
        }
        MenuSlider2("Light radius", lightRadius.use().toFloat(), 0f, 4f) {
            lightRadius.set(it)
            updateLights()
        }
        LabeledSwitch("Show maps", isShowMaps)
        LabeledSwitch("Light bodies", isLightBodies)
        LabeledSwitch("Light volumes", isLightVolumes)
        LabeledSwitch("Auto rotate view", isAutoRotate)
        MenuRow {
            Text("Color theme") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(colorMap)
                    .selectedIndex(colorMapIdx.use())
                    .onItemSelected {
                        colorMapIdx.set(it)
                        updateLightColors()
                    }
            }
        }

        Text("Bloom") { sectionTitleStyle() }
        MenuRow {
            Text("Strength") { labelStyle(lblSize) }
            MenuSlider(bloomStrength.use(), 0f, 2f, txtWidth = txtSize) { bloomStrength.set(it) }
        }
        MenuRow {
            Text("Radius") { labelStyle(lblSize) }
            MenuSlider(bloomRadius.use(), 0f, 2f, txtWidth = txtSize) { bloomRadius.set(it) }
        }
        MenuRow {
            Text("Threshold") { labelStyle(lblSize) }
            MenuSlider(bloomThreshold.use(), 0f, 2f, txtWidth = txtSize) { bloomThreshold.set(it) }
        }


        Text("Objects") { sectionTitleStyle() }
        LabeledSwitch("Show objects", isObjects)
        MenuRow {
            Text("Roughness") { labelStyle(lblSize) }
            MenuSlider(roughness.use(), 0f, 1f, txtWidth = txtSize) { roughness.set(it) }
        }

        if (isShowMaps.value) {
            surface.popup().apply {
                modifier
                    .zLayer(UiSurface.LAYER_BACKGROUND)
                    .align(AlignmentX.Start, AlignmentY.Bottom)
                    .layout(ColumnLayout)

                val albedoMetal = deferredPipeline.activePass.materialPass.albedoMetal
                val normalRough = deferredPipeline.activePass.materialPass.normalRoughness
                val positionFlags = deferredPipeline.activePass.materialPass.positionFlags
                val bloom = deferredPipeline.bloom?.bloomMap
                val ao = deferredPipeline.aoPipeline?.aoMap

                Row {
                    modifier.margin(vertical = sizes.gap)
                    Image {
                        modifier
                            .imageSize(ImageSize.FixedScale(0.3f))
                            .imageProvider(FlatImageProvider(albedoMetal, true).mirrorY())
                            .margin(horizontal = sizes.gap)
                            .customShader(albedoMapShader.apply { colorMap = albedoMetal })
                        Text("Albedo") { imageLabelStyle() }
                    }
                    Image {
                        modifier
                            .imageSize(ImageSize.FixedScale(0.3f))
                            .imageProvider(FlatImageProvider(normalRough, true).mirrorY())
                            .margin(horizontal = sizes.gap)
                            .customShader(normalMapShader.apply { colorMap = normalRough })
                        Text("Normals") { imageLabelStyle() }
                    }
                }
                Row {
                    modifier.margin(vertical = sizes.gap)
                    Image {
                        modifier
                            .imageSize(ImageSize.FixedScale(0.3f))
                            .imageProvider(FlatImageProvider(positionFlags, true).mirrorY())
                            .margin(horizontal = sizes.gap)
                            .customShader(positionMapShader.apply { colorMap = positionFlags })
                        Text("Position") { imageLabelStyle() }
                    }
                    Image(ao) {
                        modifier
                            .imageSize(ImageSize.FixedScale(0.3f / deferredPipeline.aoMapSize))
                            .imageProvider(FlatImageProvider(ao, true).mirrorY())
                            .margin(horizontal = sizes.gap)
                            .customShader(AoDemo.aoMapShader.apply { colorMap = ao })
                        Text("Ambient occlusion") { imageLabelStyle() }
                    }
                }
                Row {
                    modifier.margin(vertical = sizes.gap)
                    Image(positionFlags) {
                        modifier
                            .imageSize(ImageSize.FixedScale(0.3f))
                            .imageProvider(FlatImageProvider(positionFlags, true).mirrorY())
                            .margin(horizontal = sizes.gap)
                            .customShader(metalRoughFlagsShader.apply {
                                metal = albedoMetal
                                rough = normalRough
                                flags = deferredPipeline.activePass.materialPass.positionFlags
                            })
                        Text("Metal (r), roughness (g), flags (b)") { imageLabelStyle() }
                    }
                    Image(bloom) {
                        modifier
                            .imageSize(ImageSize.FixedScale(0.3f * ((positionFlags.height) / deferredPipeline.bloomMapSize)))
                            .imageProvider(FlatImageProvider(bloom, true).mirrorY())
                            .margin(horizontal = sizes.gap)
                            .customShader(bloomMapShader.apply { colorMap = bloom })
                        Text("Bloom") { imageLabelStyle() }
                    }
                }
            }
        }
    }

    private fun TextScope.imageLabelStyle() {
        modifier
            .zLayer(UiSurface.LAYER_FLOATING)
            .backgroundColor(colors.background)
            .padding(horizontal = sizes.gap)
    }

    private inner class LightGroup(val startConst: Vec3f, val startIt: Vec3f, val travelDir: Vec3f, val rows: Int) {
        fun setupLight(light: AnimatedLight, x: Int, travelDist: Float, travelPos: Float) {
            light.startPos.set(startIt).mul(x.toFloat()).add(startConst)
            light.dir.set(travelDir)

            light.travelDist = travelDist
            light.travelPos = travelPos * travelDist
            light.speed = rand.randomF(1f, 3f) * 0.25f
        }
    }

    private class AnimatedLight(val light: DeferredPointLights.PointLight) {
        val startPos = MutableVec3f()
        val dir = MutableVec3f()
        var speed = 1.5f
        var travelPos = 0f
        var travelDist = 10f

        var startColor = Color.WHITE
        var desiredColor = Color.WHITE
        var colorMix = 0f

        fun animate(deltaT: Float) {
            travelPos += deltaT * speed
            if (travelPos > travelDist) {
                travelPos -= travelDist
            }
            light.position.set(dir).mul(travelPos).add(startPos)

            if (colorMix < 1f) {
                colorMix += deltaT * 2f
                if (colorMix > 1f) {
                    colorMix = 1f
                }
                startColor.mix(desiredColor, colorMix, light.color)
            }
        }
    }

    private class ColorMap(val name: String, val colors: List<Color>) {
        fun getColor(idx: Int): Color = colors[idx % colors.size]
        override fun toString() = name
    }

    companion object {
        const val MAX_LIGHTS = 5000

        private val albedoMapShader = gBufferShader(0f, 1f)
        private val normalMapShader = gBufferShader(1f, 0.5f)
        private val positionMapShader = gBufferShader(10f, 0.05f)
        private val metalRoughFlagsShader = MetalRoughFlagsShader()
        private val bloomMapShader = KslUnlitShader {
            pipeline { depthTest = DepthCompareOp.ALWAYS }
            color { textureData() }
            colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
            modelCustomizer = {
                fragmentStage {
                    main {
                        val baseColorPort = getFloat4Port("baseColor")
                        val inColor = float4Var(baseColorPort.input.input)
                        baseColorPort.input(float4Value(inColor.rgb, 1f.const))
                    }
                }
            }
        }

        private fun gBufferShader(offset: Float, scale: Float) = KslUnlitShader {
            pipeline { depthTest = DepthCompareOp.ALWAYS }
            color { textureData() }
            modelCustomizer = {
                fragmentStage {
                    main {
                        val baseColorPort = getFloat4Port("baseColor")
                        val inColor = float4Var(baseColorPort.input.input)
                        inColor.rgb set (inColor.rgb + offset.const) * scale.const
                        baseColorPort.input(float4Value(inColor.rgb, 1f.const))
                    }
                }
            }
        }
    }

    private class MetalRoughFlagsShader : KslUnlitShader(cfg) {
        var flags by texture2d("tFlags")
        var rough by texture2d("tRough")
        var metal by texture2d("tMetal")

        companion object {
            val cfg = UnlitShaderConfig {
                pipeline { depthTest = DepthCompareOp.ALWAYS }
                colorSpaceConversion = ColorSpaceConversion.AsIs
                modelCustomizer = {
                    val uv = interStageFloat2()
                    vertexStage {
                        main {
                            uv.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                        }
                    }
                    fragmentStage {
                        main {
                            val metal = sampleTexture(texture2d("tMetal"), uv.output).a
                            val rough = sampleTexture(texture2d("tRough"), uv.output).a
                            val flags = sampleTexture(texture2d("tFlags"), uv.output).a
                            val color = float4Var(float4Value(metal, rough, flags, 1f.const))
                            getFloat4Port("baseColor").input(color)
                        }
                    }
                }
            }
        }
    }
}