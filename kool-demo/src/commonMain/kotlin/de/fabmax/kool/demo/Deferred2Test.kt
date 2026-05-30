package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.BloomPass
import de.fabmax.kool.pipeline.ao.AoRadius
import de.fabmax.kool.pipeline.deferred2.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.l
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.round
import kotlin.math.sin
import kotlin.random.Random

class Deferred2Test : DemoScene("Deferred2 Test") {

    val ibl by hdriImage("${DemoLoader.hdriPath}/newport_loft.rgbe.png")

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

    private val gpuTimes = mutableStateOf(GpuTimes())
    private var gpuTimesAccu = GpuTimes()

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val content = deferredContent()
        val lighting = Lighting().apply {
            clear()
        }

        //val ibl = EnvironmentMap.fromSingleColor(Color.BLACK)
        pipeline = Deferred2Pipeline(content, scene = this, ibl, lighting = lighting)
        pipeline.enableScreenSpaceReflections()
        pipeline.renderScale = 0.5f
        pipeline.aoPass.radius = AoRadius.relativeRadius(1/20f)
        filterWeight.value = pipeline.filterPass.filterWeight.toInt()
        filterWeight.onChange { _, value -> pipeline.filterPass.filterWeight = value.toFloat() }

        bloomPass = pipeline.installBloomPass()
        bloomPass.isProfileGpu = true
        addNode(pipeline.defaultOutputQuad(bloomPass))

        val deferredLights = DeferredLights(pipeline)
        content.apply {
            val orbitCam = orbitCamera(pipeline.camera) {
                setRotation(100f, -7f)
            }
            addNode(orbitCam)
            addNode(deferredLights)
        }

        val r = Random(1234)
        repeat(50) {
            deferredLights.addPointLight {
                color.set(MdColor.PALETTE.random(r).toLinear())
                position.set(Vec3f(r.randomF(-15f, 15f), 1.5f, r.randomF(-15f, 15f)))
                strengthByIntensity(r.randomF(5f, 20f))
            }
            deferredLights.addSpotLight {
                color.set(MdColor.PALETTE.random(r).toLinear())
                position.set(Vec3f(r.randomF(-15f, 15f), r.randomF(2f, 5f), r.randomF(-15f, 15f)))
                setDirection(Vec3f(r.randomF(-1f, 1f), -1f, r.randomF(-1f, 1f)))
                strengthByIntensity(r.randomF(30f, 50f))
            }
        }

        val nAccu = 50
        onUpdate {
            gpuTimesAccu = GpuTimes(
                reproj = gpuTimesAccu.reproj + pipeline.reprojectMatrixComputePass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                gbuffer = gpuTimesAccu.gbuffer + pipeline.gbuffers.a.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                ao = gpuTimesAccu.ao + pipeline.aoPass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                lighting = gpuTimesAccu.lighting + pipeline.lightingPass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                filter = gpuTimesAccu.filter + pipeline.filterPass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
                bloom = gpuTimesAccu.bloom + bloomPass.tGpu.inWholeMicroseconds / 1000.0 / nAccu,
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
                    cube { origin.set(-2.5f, 0f, 0f)}
                    color = MdColor.LIGHT_BLUE.toLinear()
                    cube {
                        origin.set(-2.5f, 1f, 0f)
                        size.set(0.25f, 1f, 0.25f)
                    }
                }
                shader = gbufferShader {
                    color {
                        vertexColor()
                        uniformColor(uniformName = "uBaseCol", blendMode = ColorBlockConfig.BlendMode.Multiply)
                    }
                    roughness { constProperty(0.15f) }
                    emission { uniformProperty(10f) }
                }.apply {
                    onUpdate {
                        val str = 10f //(sin(Time.gameTime.toFloat() * 4f) + 1f) * 16f
                        var e = str
                        e = ceil(e * 4f) / 4f
                        val b = if (e > 0f) round(str / e * 255f) / 255f else 0f
                        color = Color(b, b, b)
                        emission = e
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
                shader = gbufferShader {
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
                        .rotate(90f.deg * Time.gameTime.toFloat(), Vec3f.Y_AXIS)
                        .translate(2.5f, 0f, 0f)
                }
                shader = gbufferShader {
                    color { textureColor(albedoMap) }
                    normalMapping { useNormalMap(normalMap) }
                    metallic { textureProperty(metallicMap) }
                    roughness { textureProperty(roughnessMap) }
                    ao { textureProperty(aoMap) }
                }
            }

            val colorCubeInstances = MeshInstanceList(InstanceLayouts.ModelMat)
            addColorMesh(instances = colorCubeInstances) {
                generate {
                    cube { colored() }
                }
                shader = gbufferShader {
                    vertices { instancedModelMatrix() }
                    color { vertexColor() }
                }
                transform.translate(0f, 0f, -5f)
                val modelMat = MutableMat4f()
                onUpdate {
                    colorCubeInstances.clear()
                    colorCubeInstances.addInstances(9) { buffer ->
                        var i = 0
                        for (x in -1..1) {
                            for (y in -1..1) {
                                buffer.set(i++) {
                                    val xRot = sin(Time.gameTime + i * 31).toFloat().rad * 2.7f
                                    val yRot = sin(Time.gameTime * 0.73 + i * 17).toFloat().rad * 2.7f
                                    modelMat.setIdentity()
                                        .translate(x * 2f, y * 2f + 3f, 0f)
                                        .rotate(xRot, Vec3f.X_AXIS)
                                        .rotate(yRot, Vec3f.Y_AXIS)
                                    set(it.modelMat, modelMat)
                                }
                            }
                        }
                    }
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
                shader = gbufferShader {
                    color {
                        textureColor(uvChecker)
                    }
                    roughness { uniformProperty(groundRoughness.value) }

                }.apply {
                    groundRoughness.onChange { _, newValue -> roughness = newValue }
                }
            }

            val modelMesh = teapot.meshes.values.first().apply {
                transform.translate(0f, -0.5f, 5f).scale(0.5f).rotate(20f.deg, Vec3f.Y_AXIS)
                shader = gbufferShader {
                    color {
                        constColor(MdColor.LIME toneLin 500)
                    }
                    roughness { constProperty(0.1f) }
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
                Text("Reproject Matrices:") { }
                Text("G-Buffer:") { }
                Text("Ambient Occlusion:") { }
                Text("Lighting + Reflections:") { }
                Text("Filter:") { }
                Text("Bloom:") { }
            }
            Column {
                val t = gpuTimes.use()
                Text("${t.reproj.toString(2)} ms") {  }
                Text("${t.gbuffer.toString(2)} ms") {  }
                Text("${t.ao.toString(2)} ms") {  }
                Text("${t.lighting.toString(2)} ms") {  }
                Text("${t.filter.toString(2)} ms") {  }
                Text("${t.bloom.toString(2)} ms") {  }
            }
        }
    }
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

private data class GpuTimes(
    val reproj: Double = 0.0,
    val gbuffer: Double = 0.0,
    val ao: Double = 0.0,
    val lighting: Double = 0.0,
    val filter: Double = 0.0,
    val bloom: Double = 0.0,
)
