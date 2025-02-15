package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.plus
import de.fabmax.kool.modules.ksl.lang.rgb
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.seconds

class HelloBloom : DemoScene("Bloom") {

    private val ibl by hdriImage("${DemoLoader.Companion.hdriPath}/syferfontein_0d_clear_1k.rgbe.png")

    private val cubeBrightness = mutableStateOf(sqrt(5f))
    private val isSkyboxEnabled = mutableStateOf(false)

    private val isBloomEnabled = mutableStateOf(true)
    private val bloomStrength = mutableStateOf(2f)
    private val bloomRadius = mutableStateOf(2f)
    private val bloomThreshold = mutableStateOf(1f)
    private val bloomLuminance = mutableStateOf(BloomPass.Companion.defaultThresholdLuminanceFactors)

    private val bloomGpuTime = mutableStateOf(0.0.seconds)

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val hdrPass = OffscreenPass2d(
            drawNode = Node(),
            attachmentConfig = AttachmentConfig {
                addColor(TexFormat.RG11B10_F, filterMethod = FilterMethod.NEAREST)
                transientDepth()
            },
            initialSize = Vec2i(ctx.windowWidth, ctx.windowHeight),
            name = "forward-hdr",
            numSamples = 4
        )

        hdrPass.onUpdate { hdrPass.setSize(ctx.windowWidth, ctx.windowHeight) }

        hdrPass.sceneContent()
        addOffscreenPass(hdrPass)

        val bloomPass = BloomPass(hdrPass.colorTexture!!)
        addComputePass(bloomPass)

        bloomPass.isProfileTimes = true
        bloomPass.strength = bloomStrength.value
        bloomPass.threshold = bloomThreshold.value
        bloomPass.thresholdLuminanceFactors = bloomLuminance.value

        bloomRadius.onChange { _, radius -> bloomPass.radius = radius }
        bloomThreshold.onChange { _, thresh -> bloomPass.threshold = thresh }
        bloomLuminance.onChange { _, colors -> bloomPass.thresholdLuminanceFactors = colors }
        bloomStrength.onChange { _, strength -> bloomPass.strength = strength }

        onUpdate { bloomGpuTime.set(bloomPass.tGpu) }

        addTextureMesh {
            generateFullscreenQuad()
            shader = KslShader("hdr-output") {
                val uv = interStageFloat2()
                fullscreenQuadVertexStage(uv)
                fragmentStage {
                    main {
                        val hdrInput = texture2d("hdrPass")
                        val bloomInput = texture2d("bloom")

                        val hdr = float3Var(sampleTexture(hdrInput, uv.output).rgb)
                        val bloom = float3Var(sampleTexture(bloomInput, uv.output, 0f.const).rgb)

                        bloom set bloom * uniformFloat1("bloomIntensity")
                        val sdr = convertColorSpace(hdr + bloom, ColorSpaceConversion.LinearToSrgbHdr())
                        colorOutput(sdr)
                    }
                }
            }.apply {
                texture2d("hdrPass", hdrPass.colorTexture)
                texture2d("bloom", bloomPass.bloomMap)

                var bloomIntensity by uniform1f("bloomIntensity", bloomStrength.value)
                isBloomEnabled.onChange { _, flag -> bloomIntensity = if (flag) 1f else 0f }
            }
        }
    }

    private fun OffscreenPass2d.sceneContent() {
        orbitCamera(defaultView) {
            setRotation(0f, -30f)
            zoom = 3.0
        }

        val cubeShader = KslUnlitShader {
            color {
                vertexColor()
                uniformColor(Color.Companion.WHITE, blendMode = ColorBlockConfig.BlendMode.Multiply)
                colorSpaceConversion = ColorSpaceConversion.AsIs
            }
        }

        drawNode.addColorMesh {
            generate {
                cube {
                    // use slightly different colors wit more uniform luminance than regular colored() cube
                    colors = listOf(
                        MdColor.Companion.DEEP_ORANGE.toLinear(),
                        MdColor.Companion.AMBER.toLinear(),
                        MdColor.Companion.BLUE.toLinear(),
                        MdColor.Companion.CYAN.toLinear(),
                        MdColor.Companion.PINK.toLinear(),
                        MdColor.Companion.LIGHT_GREEN.toLinear(),
                    )
                }
            }
            shader = cubeShader
            onUpdate {
                transform.rotate(45f.deg * Time.deltaT, Vec3f.Companion.Y_AXIS)
            }
            cubeShader.color = Color.Companion.WHITE.mulRgb(cubeBrightness.value * cubeBrightness.value)
            cubeBrightness.onChange { _, brightness ->
                cubeShader.color = Color.Companion.WHITE.mulRgb(brightness * brightness)
            }
        }

        val skybox = Skybox.cube(ibl.reflectionMap, texLod = 1f, colorSpaceConversion = ColorSpaceConversion.AsIs)
        skybox.isVisible = isSkyboxEnabled.value
        isSkyboxEnabled.onChange { _, flag -> skybox.isVisible = flag }
        drawNode.addNode(skybox)
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuSlider2("Box brightness", cubeBrightness.use(), 0.1f, sqrt(50f), txtFormat = { (it*it).toString(2) }) { cubeBrightness.set(it) }
        LabeledSwitch("Skybox", isSkyboxEnabled)

        Text("Bloom Settings") { sectionTitleStyle() }

        LabeledSwitch("Enabled", isBloomEnabled)
        MenuSlider2("Strength", bloomStrength.use(), 0.01f, 4f) { bloomStrength.set(it) }
        MenuSlider2("Radius", bloomRadius.use(), 0.1f, 10f) { bloomRadius.set(it) }
        MenuSlider2("Threshold", bloomThreshold.use(), 0f, 10f) { bloomThreshold.set(it) }
        MenuRow {
            val lumi = bloomLuminance.use()
            Text("Luminance weights:") { labelStyle(Grow.Companion.Std) }
            Text("${lumi.x.toString(2)}, ${lumi.y.toString(2)}, ${lumi.z.toString(2)}") { labelStyle() }
        }
        MenuRow(vGap = 4.dp) {
            Slider(bloomLuminance.use().x, 0f, 1f) {
                modifier
                    .width(Grow.Companion.Std)
                    .alignY(AlignmentY.Center)
                    .colors(knobColor = MdColor.Companion.RED, trackColor = MdColor.Companion.RED.withAlpha(0.4f), trackColorActive = MdColor.Companion.RED.withAlpha(0.7f))
                    .onChange {
                        bloomLuminance.set(Vec3f(it, bloomLuminance.value.y, bloomLuminance.value.z))
                    }
            }
        }
        MenuRow(vGap = 4.dp) {
            Slider(bloomLuminance.use().y, 0f, 1f) {
                modifier
                    .width(Grow.Companion.Std)
                    .alignY(AlignmentY.Center)
                    .colors(knobColor = MdColor.Companion.GREEN, trackColor = MdColor.Companion.GREEN.withAlpha(0.4f), trackColorActive = MdColor.Companion.GREEN.withAlpha(0.7f))
                    .onChange {
                        bloomLuminance.set(Vec3f(bloomLuminance.value.x, it, bloomLuminance.value.z))
                    }
            }
        }
        MenuRow(vGap = 4.dp) {
            Slider(bloomLuminance.use().z, 0f, 1f) {
                modifier
                    .width(Grow.Companion.Std)
                    .alignY(AlignmentY.Center)
                    .colors(knobColor = MdColor.Companion.BLUE, trackColor = MdColor.Companion.BLUE.withAlpha(0.4f), trackColorActive = MdColor.Companion.BLUE.withAlpha(0.7f))
                    .onChange {
                        bloomLuminance.set(Vec3f(bloomLuminance.value.x, bloomLuminance.value.y, it))
                    }
            }
        }

        MenuRow {
            Text("Bloom pass:") { labelStyle(Grow.Companion.Std) }
            Text("${(bloomGpuTime.use().inWholeMicroseconds / 1000.0).toString(3)} ms") { labelStyle() }
        }
    }
}