package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.BloomPass
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.swapPipelineDataCapturing
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*

class Deferred2Demo : DemoScene("Deferred2 Demo") {

    val ibl by hdriImage("hdri/newport_loft.rgbe.png")
    private val albedoMap by texture2d("materials/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg")
    private val normalMap by texture2d("materials/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg")

    private lateinit var pipeline: Deferred2Pipeline
    private val filterWeight = mutableStateOf(16)
    private val bloom = mutableStateOf(true)

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val content = deferredContent()
        val lighting = Lighting().apply {
            singlePointLight {
                setup(Vec3f(1.2f, 3.2f, 2f))
                setColor(Color.WHITE, intensity = 5f)
            }
        }

        pipeline = Deferred2Pipeline(content, scene = this, ibl, lighting)
        pipeline.renderScale = 0.5f
        filterWeight.value = pipeline.filterPass.filterWeight.toInt()
        filterWeight.onChange { _, value -> pipeline.filterPass.filterWeight = value.toFloat() }

        content.apply {
            val orbitCam = orbitCamera(pipeline.camera) { }
            addNode(orbitCam)
        }

        val bloomPass = BloomPass(pipeline.filterPass.filterOutput.newVal)
        addComputePass(bloomPass)
        bloom.onChange { _, value -> bloomPass.isEnabled = value }
        pipeline.onSwap {
            val filterOutput = pipeline.filterPass.filterOutput.newVal
            bloomPass.inputShader.swapPipelineDataCapturing(filterOutput) {
                bloomPass.inputTexture = filterOutput
            }
        }

        addTextureMesh {
            generate {
                generateFullscreenQuad()
            }
            shader = deferredOutputShader(this@Deferred2Demo, pipeline, bloomPass)
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
            var scaleIndex by remember(2)
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
    }

    private fun deferredContent() = Node("deferred content").apply {
        addGroup {
            onUpdate {
//                transform.rotate(-30f.deg * Time.deltaT, Vec3f.Y_AXIS)
            }

            addColorMesh {
                generate {
                    color = MdColor.PINK.toLinear()
                    cube { origin.set(-2.5f, 0f, 0f)}
                    color = MdColor.AMBER.toLinear()
                    icoSphere {
                        center.set(-2.5f, 0f, 2.5f)
                        steps = 4
                        radius = 0.5f
                    }
                }
                onUpdate {
//                    transform.rotate(360f.deg * Time.deltaT, Vec3f.Y_AXIS)
                }
                shader = gbufferShader(objectId = 1) {
                    color { vertexColor() }
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
                    color {
                        textureColor(albedoMap)
                    }
                    normalMapping {
                        useNormalMap(normalMap)
                    }
                }.apply {
//                    bindTexture2d("tbaseColor", uvChecker)
                    bindTexture2d("tbaseColor", albedoMap)
                    bindTexture2d("tNormalMap", normalMap)
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
            addColorMesh {
                generate {
                    translate(0f, -0.5f, 0f)
                    color = Color.WHITE
                    grid {
                        sizeX = 50f
                        sizeY = 50f
                    }
                }
                shader = gbufferShader(objectId = 4) {
                    color { vertexColor() }
                }
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
                val color by output.load(uvi).rgb + bloom.sample(uv.output).rgb

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
    var inputTex by outputShader.bindTexture2d("deferredOutput")
    val noBloom = SingleColorTexture(Color.BLACK)
    deferred2Pipeline.onSwap {
        val filterOutput = deferred2Pipeline.filterPass.filterOutput.newVal
        outputShader.swapPipelineDataCapturing(filterOutput) {
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
            ScaleItem("0.1x", 0.1f),
            ScaleItem("0.25x", 0.25f),
            ScaleItem("0.5x", 0.5f),
            ScaleItem("0.75x", 0.75f),
            ScaleItem("1x", 1f),
        )
    }
}
