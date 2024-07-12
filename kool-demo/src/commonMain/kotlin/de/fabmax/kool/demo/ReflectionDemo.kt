package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.randomF
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.deferred.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.*

class ReflectionDemo : DemoScene("Reflections") {

    private lateinit var deferredPipeline: DeferredPipeline

    private val hdri by hdriGradient(ColorGradient(Color.DARK_GRAY.mix(Color.BLACK, 0.75f), Color.DARK_GRAY, toLinear = true))
    private val floorAlbedo by texture2d("${DemoLoader.materialPath}/woodfloor/WoodFlooringMahoganyAfricanSanded001_COL_2K.jpg")
    private val floorNormal by texture2d("${DemoLoader.materialPath}/woodfloor/WoodFlooringMahoganyAfricanSanded001_NRM_2K.jpg")
    private val floorRoughness by texture2d("${DemoLoader.materialPath}/woodfloor/WoodFlooringMahoganyAfricanSanded001_REFL_2K.jpg")

    private val model by model("${DemoLoader.modelPath}/bunny.gltf.gz", GltfLoadConfig(generateNormals = true, applyMaterials = false))

    private val lights = listOf(
            LightMesh(MdColor.CYAN),
            LightMesh(MdColor.RED),
            LightMesh(MdColor.AMBER),
            LightMesh(MdColor.GREEN))

    private val lightChoices = listOf("1", "2", "3", "4")
    private val lightGroup = Node("light-group")

    private val isSsrEnabled = mutableStateOf(true).onChange { _, new -> deferredPipeline.isSsrEnabled = new }
    private val ssrMapSize = mutableStateOf(0.5f).onChange { _, new -> deferredPipeline.reflectionMapSize = new }
    private val isShowSsrMap = mutableStateOf(true)
    private val lightCount = mutableStateOf(4)
    private val lightPower = mutableStateOf(500f)
    private val lightSaturation = mutableStateOf(0.4f)
    private val lightRandomness = mutableStateOf(0.3f)
    private val isAutoRotate = mutableStateOf(true)
    private val isShowLightIndicators = mutableStateOf(true)
    private val selectedColorIdx = mutableStateOf(1)
    private val roughness = mutableStateOf(0.1f)
    private val metallic = mutableStateOf(0.0f)

    private var bunnyMesh: Mesh? = null
    private var groundMesh: Mesh? = null

    private var modelShader: DeferredKslPbrShader? = null

    override fun lateInit(ctx: KoolContext) {
        updateLighting()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        orbitCamera {
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
            setZoom(17.0, max = 50.0)
            translation.set(0.0, 2.0, 0.0)
            setMouseRotation(0f, -5f)
            // let the camera slowly rotate around vertical axis
            onUpdate += {
                if (isAutoRotate.value) {
                    verticalRotation += Time.deltaT * 3f
                }
            }
        }

        addNode(lightGroup)
        lightGroup.onUpdate {
            lightGroup.transform.rotate((-3f).deg * Time.deltaT, Vec3f.Y_AXIS)
        }

        lighting.clear()
        lights.forEach {
            lighting.addLight(it.light)
            lightGroup.addNode(it)
        }

        setupDeferred(this)
    }

    private fun setupDeferred(scene: Scene) {
        val defCfg = DeferredPipelineConfig().apply {
            isWithAmbientOcclusion = false
            isWithScreenSpaceReflections = true
            useImageBasedLighting(hdri)
        }
        deferredPipeline = DeferredPipeline(scene, defCfg)

        scene += deferredPipeline.createDefaultOutputQuad().also {
            (it.shader as? DeferredOutputShader)?.setupVignette(0f)
        }
        scene += Skybox.cube(hdri.reflectionMap, 1f)

        modelShader = deferredKslPbrShader {
            color { uniformColor(matColors[selectedColorIdx.value].linColor) }
            roughness { uniformProperty(this@ReflectionDemo.roughness.value) }
            metallic { uniformProperty(this@ReflectionDemo.metallic.value) }
        }

        deferredPipeline.sceneContent.apply {
            addTextureMesh(isNormalMapped = true) {
                generate {
                    rect {
                        rotate((-90f).deg, Vec3f.X_AXIS)
                        size.set(100f, 100f)
                        generateTexCoords(4f)
                    }
                }

                // ground doesn't need to cast shadows (there's nothing underneath it...)
                isCastingShadow = false
                groundMesh = this

                shader = deferredKslPbrShader {
                    color { textureColor(floorAlbedo) }
                    normalMapping { setNormalMap(floorNormal) }
                    roughness { textureProperty(floorRoughness) }
                }
            }

            bunnyMesh = model.meshes.values.first()
            addNode(model)
            bunnyMesh!!.shader = modelShader
        }
    }

    private fun updateLighting() {
        lights.forEachIndexed { i, light ->
            if (i < deferredPipeline.shadowMaps.size) {
                deferredPipeline.shadowMaps[i].isShadowMapEnabled = false
            }
            light.disable(mainScene.lighting)
        }

        var pos = 0f
        val step = 360f / lightCount.value
        for (i in 0 until min(lightCount.value, lights.size)) {
            lights[i].setup(pos)
            lights[i].enable(mainScene.lighting)
            pos += step
            if (i < deferredPipeline.shadowMaps.size) {
                deferredPipeline.shadowMaps[i].isShadowMapEnabled = true
            }
        }

        lights.forEach { it.updateVisibility() }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        val lblSize = UiSizes.baseSize * 2f
        val txtSize = UiSizes.baseSize * 0.75f

        LabeledSwitch("SSR enabled", isSsrEnabled)
        LabeledSwitch("Show map", isShowSsrMap)
        MenuRow {
            Text("Map size") { labelStyle(lblSize) }
            MenuSlider(ssrMapSize.use(), 0.1f, 1f, { it.toString(1) }, txtWidth = txtSize) {
                ssrMapSize.set((it * 10).roundToInt() / 10f)
            }
        }

        Text("Material") { sectionTitleStyle() }
        MenuRow {
            Text("Color") { labelStyle(lblSize) }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .items(matColors)
                    .selectedIndex(selectedColorIdx.use())
                    .onItemSelected {
                        selectedColorIdx.set(it)
                        modelShader?.color = matColors[it].linColor
                    }
            }
        }
        MenuRow {
            Text("Roughness") { labelStyle(lblSize) }
            MenuSlider(roughness.use(), 0f, 1f, txtWidth = txtSize) {
                roughness.set(it)
                modelShader?.roughness = it
            }
        }
        MenuRow {
            Text("Metallic") { labelStyle(lblSize) }
            MenuSlider(metallic.use(), 0f, 1f, txtWidth = txtSize) {
                metallic.set(it)
                modelShader?.metallic = it
            }
        }

        Text("Lighting") { sectionTitleStyle() }
        MenuRow {
            Text("Lights") { labelStyle(lblSize) }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .items(lightChoices)
                    .selectedIndex(lightCount.use() - 1)
                    .onItemSelected { lightCount.set(it + 1) }
            }
        }
        MenuRow {
            Text("Strength") { labelStyle(lblSize) }
            MenuSlider(lightPower.use(), 0f, 1000f, { "${it.roundToInt()}" }, txtSize) { lightPower.set(it) }
        }
        MenuRow {
            Text("Saturation") { labelStyle(lblSize) }
            MenuSlider(lightSaturation.use(), 0f, 1f, txtWidth = txtSize) { lightSaturation.set(it) }
        }
        LabeledSwitch("Light indicators", isShowLightIndicators)
        LabeledSwitch("Auto rotate view", isAutoRotate)

        if (isShowSsrMap.value) {
            surface.popup().apply {
                modifier
                    .margin(sizes.gap)
                    .align(AlignmentX.Start, AlignmentY.Bottom)

                Image(deferredPipeline.reflections?.reflectionMap) {
                    modifier
                        .height(500.dp)
                        .imageProvider(FlatImageProvider(deferredPipeline.reflections?.reflectionMap, true).mirrorY())
                        .backgroundColor(Color.BLACK)
                }
            }
        }

        updateLighting()
    }

    private inner class LightMesh(val color: Color) : Node() {
        val light = Light.Spot()

        private val spotAngleMesh = LineMesh().apply { isCastingShadow = false }

        private var isEnabled = true
        private var animPos = 0.0
        private val lightMeshShader = KslUnlitShader { color { uniformColor() } }
        private val rotOff = randomF(0f, 3f)

        init {
            transform = MatrixTransformF()
            light.setup(Vec3f.ZERO, Vec3f.X_AXIS, 50f.deg)
            addNode(light)

            addColorMesh {
                isCastingShadow = false
                generate {
                    uvSphere {
                        radius = 0.1f
                    }
                    rotate(90f.deg, Vec3f.Z_AXIS)
                    translate(0f, 0.45f, 0f)
                    cylinder {
                        bottomRadius = 0.015f
                        topRadius = 0.015f
                        height = 0.9f
                        steps = 4
                    }
                    translate(0f, 0.45f, 0f)
                    cylinder {
                        bottomRadius = 0.1f
                        topRadius = 0.0025f
                        height = 0.15f
                    }
                }
                shader = lightMeshShader
            }
            addNode(spotAngleMesh)

            onUpdate += {
                animPos += Time.deltaT
                val r = cos(animPos / 15 + rotOff).toFloat() * lightRandomness.value
                updateSpotAngleMesh()
                light.setup(Vec3f.ZERO, Vec3f.NEG_X_AXIS, (60f - r * 20f).deg)
            }
        }

        private fun updateSpotAngleMesh() {
            val r = 1f * tan(light.spotAngle.rad / 2)
            val c = MutableColor().set(lightMeshShader.color)
            val n = 40

            spotAngleMesh.clear()
            for (i in 0 until n) {
                val a0 = i.toFloat() / n * 2 * PI
                val a1 = (i+1).toFloat() / n * 2 * PI
                spotAngleMesh.addLine(Vec3f(-1f, cos(a0).toFloat() * r, sin(a0).toFloat() * r), c,
                        Vec3f(-1f, cos(a1).toFloat() * r, sin(a1).toFloat() * r), c)
            }
            val e = cos(45f.toRad()) * r
            spotAngleMesh.addLine(Vec3f.ZERO, c, Vec3f(-1f, e, e), c)
            spotAngleMesh.addLine(Vec3f.ZERO, c, Vec3f(-1f, e, -e), c)
            spotAngleMesh.addLine(Vec3f.ZERO, c, Vec3f(-1f, -e, -e), c)
            spotAngleMesh.addLine(Vec3f.ZERO, c, Vec3f(-1f, -e, e), c)
        }

        fun setup(angPos: Float) {
            val r = cos(angPos / 15f + rotOff) * lightRandomness.value

            transform.setIdentity()
            transform.rotate(angPos.deg, Vec3f.Y_AXIS)
            transform.translate(10f, 9f, 0f)
            transform.rotate((30f + 20f * r).deg, Vec3f.Z_AXIS)

            val color = Color.WHITE.mix(color, lightSaturation.value, MutableColor())
            light.setColor(color.toLinear(), lightPower.value)
            lightMeshShader.color = color
            updateSpotAngleMesh()
        }

        fun enable(lighting: Lighting) {
            isEnabled = true
            if (!lighting.lights.contains(light)) {
                lighting.addLight(light)
            }
            updateVisibility()
        }

        fun disable(lighting: Lighting) {
            isEnabled = false
            lighting.removeLight(light)
            updateVisibility()
        }

        fun updateVisibility() {
            isVisible = isEnabled && isShowLightIndicators.value
        }
    }

    private data class MatColor(val name: String, val linColor: Color) {
        override fun toString() = name
    }

    companion object {
        private val matColors = listOf(
                MatColor("White", Color.WHITE.toLinear()),
                MatColor("Red", MdColor.RED.toLinear()),
                MatColor("Pink", MdColor.PINK.toLinear()),
                MatColor("Purple", MdColor.PURPLE.toLinear()),
                MatColor("Deep Purple", MdColor.DEEP_PURPLE.toLinear()),
                MatColor("Indigo", MdColor.INDIGO.toLinear()),
                MatColor("Blue", MdColor.BLUE.toLinear()),
                MatColor("Cyan", MdColor.CYAN.toLinear()),
                MatColor("Teal", MdColor.TEAL.toLinear()),
                MatColor("Green", MdColor.GREEN.toLinear()),
                MatColor("Light Green", MdColor.LIGHT_GREEN.toLinear()),
                MatColor("Lime", MdColor.LIME.toLinear()),
                MatColor("Yellow", MdColor.YELLOW.toLinear()),
                MatColor("Amber", MdColor.AMBER.toLinear()),
                MatColor("Orange", MdColor.ORANGE.toLinear()),
                MatColor("Deep Orange", MdColor.DEEP_ORANGE.toLinear()),
                MatColor("Brown", MdColor.BROWN.toLinear()),
                MatColor("Grey", MdColor.GREY.toLinear()),
                MatColor("Blue Grey", MdColor.BLUE_GREY.toLinear()),
                MatColor("Almost Black", Color(0.1f, 0.1f, 0.1f).toLinear())
        )
    }
}