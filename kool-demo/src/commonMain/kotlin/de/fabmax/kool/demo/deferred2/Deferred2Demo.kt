package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.BloomPass
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.SamplerSettings
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.ao.AoRadius
import de.fabmax.kool.pipeline.swapPipelineData
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.round

class Deferred2Demo : DemoScene("Deferred2 Demo") {

    val ibl by hdriImage("${DemoLoader.hdriPath}/newport_loft.rgbe.png")
//    val ibl by hdriImage("${DemoLoader.hdriPath}/shanghai_bund_1k.rgbe.png")
//    val ibl by hdriImage("${DemoLoader.hdriPath}/circus_arena_1k.rgbe.png")
//    val ibl by hdriImage("${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png")
//    val ibl by hdriImage("${DemoLoader.hdriPath}/colorful_studio_1k.rgbe.png")
//    val ibl by hdriImage("${DemoLoader.hdriPath}/spruit_sunrise_1k.rgbe.png")
//    val ibl by hdriImage("${DemoLoader.hdriPath}/mossy_forest_1k.rgbe.png")

    val teapot by model("${DemoLoader.modelPath}/teapot.gltf.gz", GltfLoadConfig(applyMaterials = false))

    private val albedoMap by texture2d("${DemoLoader.materialPath}/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg")
    private val normalMap by texture2d("${DemoLoader.materialPath}/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg")
    private val metallicMap by texture2d("${DemoLoader.materialPath}/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_METALNESS_2K_METALNESS.jpg")
    private val roughnessMap by texture2d("${DemoLoader.materialPath}/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_ROUGHNESS_2K_METALNESS.jpg")
    private val aoMap by texture2d("${DemoLoader.materialPath}/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_AO_2K_METALNESS.jpg")
//    private val uvChecker by texture2d("${DemoLoader.materialPath}/uv_checker_map.jpg")
    private val uvChecker by texture2d("${DemoLoader.materialPath}/kool-test-tex.png")

    private lateinit var pipeline: Deferred2Pipeline
    private lateinit var bloomPass: BloomPass
    private val filterWeight = mutableStateOf(16)
    private val groundRoughness = mutableStateOf(0.5f)
    private val bloom = mutableStateOf(true)

    private val gpuTimes = mutableStateOf(GpuTimes(0.0, 0.0, 0.0, 0.0, 0.0))
    private var gpuTimesAccu = GpuTimes(0.0, 0.0, 0.0, 0.0, 0.0)

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val content = deferredContent()
        val lighting = Lighting().apply {
            clear()
//            singlePointLight {
//                setup(Vec3f(1.2f, 3.2f, 2f))
//                setColor(Color.WHITE, intensity = 5f)
//            }
        }

        pipeline = Deferred2Pipeline(content, scene = this, ibl, isScreenSpaceReflections = true, lighting)
        pipeline.renderScale = 0.5f
        pipeline.aoPass.radius = AoRadius.relativeRadius(1/20f)
        filterWeight.value = pipeline.filterPass.filterWeight.toInt()
        filterWeight.onChange { _, value -> pipeline.filterPass.filterWeight = value.toFloat() }

        content.apply {
            val orbitCam = orbitCamera(pipeline.camera) {
                setRotation(100f, -7f)
            }
            addNode(orbitCam)
        }

        bloomPass = BloomPass(pipeline.filterPass.filterOutput.newVal)
        bloomPass.isProfileGpu = true
        addComputePass(bloomPass)
        bloom.onChange { _, value -> bloomPass.isEnabled = value }
        pipeline.onSwap {
            val filterOutput = pipeline.filterPass.filterOutput.newVal
            bloomPass.inputShader.swapPipelineData(filterOutput) {
                bloomPass.inputTexture = filterOutput
            }
        }

        addTextureMesh {
            generate {
                generateFullscreenQuad()
            }
            shader = deferredOutputShader(this@Deferred2Demo, pipeline, bloomPass)
        }

        val nAccu = 50
        onUpdate {
            gpuTimesAccu = GpuTimes(
                gpuTimesAccu.gbuffer + pipeline.gbuffers.a.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                gpuTimesAccu.ao + pipeline.aoPass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                gpuTimesAccu.lighting + pipeline.lightingPass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                gpuTimesAccu.filter + pipeline.filterPass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                gpuTimesAccu.bloom + bloomPass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
            )
            if (Time.frameCount % nAccu == 0) {
                gpuTimes.set(gpuTimesAccu)
                gpuTimesAccu = GpuTimes(0.0, 0.0, 0.0, 0.0, 0.0)
            }
        }
    }

    private fun deferredContent() = Node("deferred content").apply {
        addGroup {
            transform.rotate(45f.deg, Vec3f.Y_AXIS)

            addColorMesh {
                generate {
                    color = MdColor.PINK.toLinear()
//                    color = Color.WHITE
                    cube { origin.set(-2.5f, 0f, 0f)}
                    color = MdColor.LIGHT_BLUE.toLinear()
                    cube {
                        origin.set(-2.5f, 1f, 0f)
                        size.set(0.25f, 1f, 0.25f)
                    }
                }
                shader = gbufferShader(objectId = 1) {
                    color {
                        vertexColor()
                        uniformColor(uniformName = "uBaseCol", blendMode = ColorBlockConfig.BlendMode.Multiply)
                    }
                    roughness { constProperty(0.15f) }
                    emission { uniformProperty(10f, "uEmi") }
                }.apply {
                    var baseColFac by bindUniformColor("uBaseCol")
                    var emi by bindUniformFloat1("uEmi")
                    onUpdate {
                        val str = 10f //(sin(Time.gameTime.toFloat() * 4f) + 1f) * 16f
                        var e = str
                        e = ceil(e * 4f) / 4f
                        val b = if (e > 0f) round(str / e * 255f) / 255f else 0f
                        baseColFac = Color(b, b, b)
                        emi = e
                    }
                }
            }
            addColorMesh {
                generate {
                    color = MdColor.AMBER.toLinear()
                    icoSphere {
                        center.set(-2.5f, 0f, 2.5f)
                        steps = 4
                        radius = 0.5f
                    }
                }
                shader = gbufferShader(objectId = 6) {
                    color { vertexColor() }
                    metallic { constProperty(1f) }
                    roughness { constProperty(0f) }
                }
            }
            addTextureMesh(isNormalMapped = true) {
                generate {
                    cube { }
                }
                onUpdate {
                    transform
                        .setIdentity()
//                        .rotate(90f.deg * Time.gameTime.toFloat(), Vec3f.Y_AXIS)
                        //.rotate(20f.deg, Vec3f.Y_AXIS)
                        .translate(2.5f, 0f, 0f)
                }
                shader = gbufferShader(objectId = 2) {
                    color { textureColor(albedoMap) }
                    normalMapping { useNormalMap(normalMap) }
                    metallic { textureProperty(metallicMap) }
                    roughness { textureProperty(roughnessMap) }
                    ao { textureProperty(aoMap) }

//                    color { constColor(Color.WHITE) }
//                    metallic { constProperty(1f) }
//                    roughness { constProperty(0f) }
                }.apply {
                    bindTexture2d("tbaseColor", albedoMap)
                    bindTexture2d("tNormalMap", normalMap)
                    bindTexture2d("tmetallic", metallicMap)
                    bindTexture2d("troughness", roughnessMap)
                    bindTexture2d("tao", aoMap)
                }
            }
            addColorMesh {
                generate {
                    cube { colored() }
                }
                onUpdate {
                    transform.rotate(90f.deg * Time.deltaT, Vec3f.Y_AXIS)
                }
                shader = gbufferShader(objectId = 3) {
                    color { vertexColor() }
                }
            }
            addTextureMesh {
                generate {
                    translate(0f, -0.5f, 0f)
                    grid {
                        sizeX = 50f
                        sizeY = 50f
                        texCoordScale.set(10f, 10f)
                    }
                }
                shader = gbufferShader(objectId = 4) {
                    color {
                        textureColor(uvChecker)
                    }
                    roughness { uniformProperty(groundRoughness.value, "uRough") }
//                    metallic { constProperty(1f) }

                }.apply {
                    bindTexture2d("tbaseColor", uvChecker)

                    var rough by bindUniformFloat1("uRough", groundRoughness.value)
                    groundRoughness.onChange { _, newValue -> rough = newValue }
                }
            }

            val modelMesh = teapot.meshes.values.first().apply {
                transform.translate(0f, -0.5f, 5f).scale(0.5f).rotate(20f.deg, Vec3f.Y_AXIS)
                shader = gbufferShader(objectId = 5) {
                    color {
                        constColor(MdColor.LIME toneLin 500)
//                        constColor(Color.WHITE)
                    }
                    roughness { constProperty(0.1f) }
//                    metallic { constProperty(1f) }
                }
            }
            addNode(modelMesh)
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext): UiSurface = menuSurface {
        LabeledSwitch("Bloom", bloom) { }
        MenuSlider1("Filter", filterWeight.use().toFloat(), 0f, 32f, { "${it.toInt()}" }) {
            filterWeight.set(it.toInt())
        }
        MenuRow {
            var tsaaIndex by remember(2)
            Text("Temporal AA".l) { labelStyle(120.dp) }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(TsaaItem.items.map { it.label })
                    .selectedIndex(tsaaIndex)
                    .onItemSelected {
                        tsaaIndex = it
                        pipeline.tsaa = TsaaItem.items[it].tsaa
                        pipeline.aoPass.temporalKernels = TsaaItem.items[it].numSamples
                    }
            }
        }
        MenuRow {
            var scaleIndex by remember {
                val initialScale = ScaleItem.items.minBy { abs(1f - it.scale * UiScale.windowScale.value) }
                mutableStateOf(ScaleItem.items.indexOf(initialScale))
            }
            Text("Render scale".l) { labelStyle(120.dp) }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(ScaleItem.items.map { it.label })
                    .selectedIndex(scaleIndex)
                    .onItemSelected {
                        scaleIndex = it
                        pipeline.renderScale = ScaleItem.items[it].scale
                    }
            }
        }
        MenuSlider1("Roughness", groundRoughness.use(), 0f, 1f) {
            groundRoughness.set(it)
        }
        Text("Timings".l) { sectionTitleStyle() }
        MenuRow {
            Column(width = Grow.Std) {
                Text("G-Buffer:") { }
                Text("Ambient Occlusion:") { }
                Text("Lighting + Reflections:") { }
                Text("Filter:") { }
                Text("Bloom:") { }
            }
            Column {
                val t = gpuTimes.use()
                Text("${t.gbuffer.toString(2)} ms") {  }
                Text("${t.ao.toString(2)} ms") {  }
                Text("${t.lighting.toString(2)} ms") {  }
                Text("${t.filter.toString(2)} ms") {  }
                Text("${t.bloom.toString(2)} ms") {  }
            }
        }
    }
}

private fun deferredOutputShader(
    demo: Deferred2Demo,
    deferred2Pipeline: Deferred2Pipeline,
    bloomPass: BloomPass
): KslShader {
    val outputShader = KslShader("deferred2-output") {
        val uv = interStageFloat2()
        fullscreenQuadVertexStage(uv)
        fragmentStage {
            main {
                val output = texture2d("deferredOutput")
                val bloom = texture2d("bloomOutput")
                val uvi = (uv.output * output.size().toFloat2()).toInt2()
                val color by output.sample(uv.output).rgb + bloom.sample(uv.output).rgb

                val ditherTex = texture2d("ditherPattern")
                val ditherC by uvi % ditherTex.size()
                val ditherNoise by ditherTex.load(ditherC).r
                val srgb by convertColorSpace(color, ColorSpaceConversion.LinearToSrgbHdr()) + (ditherNoise - 0.5f.const) / 255f.const
                colorOutput(srgb)
//                colorOutput(color)
            }
        }
    }

    val ditherTex = makeDitherPattern()
    ditherTex.releaseWith(demo.mainScene)

    outputShader.bindTexture2d("ditherPattern", ditherTex)
    var bloomTex by outputShader.bindTexture2d("bloomOutput", bloomPass.bloomMap)
    var inputTex by outputShader.bindTexture2d("deferredOutput", defaultSampler = SamplerSettings().nearest().clamped())
    val noBloom = SingleColorTexture(Color.BLACK)
    deferred2Pipeline.onSwap {
        val filterOutput = deferred2Pipeline.filterPass.filterOutput.newVal
        outputShader.swapPipelineData(filterOutput) {
            inputTex = filterOutput
            bloomTex = if (bloomPass.isEnabled) bloomPass.bloomMap else noBloom
        }
    }
    return outputShader
}

private data class TsaaItem(val label: String, val tsaa: List<Vec2f>, val numSamples: Int) {
    companion object {
        val items = listOf(
            TsaaItem("None", Deferred2Pipeline.TSAA_NONE, 1),
            TsaaItem("2x", Deferred2Pipeline.TSAA_2, 2),
            TsaaItem("4x", Deferred2Pipeline.TSAA_4, 4),
            TsaaItem("8x", Deferred2Pipeline.TSAA_8, 8),
            TsaaItem("16x", Deferred2Pipeline.TSAA_16, 16)
        )
    }
}

private data class ScaleItem(val label: String, val scale: Float) {
    companion object {
        val items = listOf(
            ScaleItem("10 %", 0.1f),
            ScaleItem("25 %", 0.25f),
            ScaleItem("50 %", 0.5f),
            ScaleItem("75 %", 0.75f),
            ScaleItem("100 %", 1f),
        )
    }
}

private data class GpuTimes(val gbuffer: Double, val ao: Double, val lighting: Double, val filter: Double, val bloom: Double)
