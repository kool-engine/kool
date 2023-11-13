package de.fabmax.kool.demo

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.math.noise.MultiPerlin3d
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.*

class ShellShadingDemo : DemoScene("Shell Shading") {

    private lateinit var envMap: EnvironmentMaps

    private val themes = mutableListOf<ColorTheme>()
    private val furShader = FurShader()

    private val shells = MeshInstanceList(128, Attribute.INSTANCE_MODEL_MAT, ATTRIB_SHELL)
    private val numShells = mutableStateOf(48).onChange { makeShells() }

    private val theme = mutableStateOf<ColorTheme?>(null).onChange { furShader.furGradient = it?.texture }
    private val density = mutableStateOf(furShader.density).onChange { furShader.density = it }
    private val hairLength = mutableStateOf(furShader.hairLength).onChange { furShader.hairLength = it }
    private val hairThickness = mutableStateOf(furShader.hairThickness).onChange { furShader.hairThickness = it }
    private val hairRandomness = mutableStateOf(furShader.hairRandomness).onChange { furShader.hairRandomness = it }
    private val curliness = mutableStateOf(0f).onChange { makeShells() }

    private val dispScale = mutableStateOf(furShader.noiseDispScale).onChange { furShader.noiseDispScale = it }
    private val dispStrength = mutableStateOf(furShader.noiseDispStrength).onChange { furShader.noiseDispStrength = it }
    private val lenScale = mutableStateOf(furShader.noiseLenScale).onChange { furShader.noiseLenScale = it }
    private val lenStrength = mutableStateOf(furShader.noiseLenStrength).onChange { furShader.noiseLenStrength = it }

    private val windSpeed = mutableStateOf(0.4f)
    private val windStrength = mutableStateOf(furShader.windStrength).onChange { furShader.windStrength = it }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        mainRenderPass.clearColor = MdColor.GREY tone 600

        defaultOrbitCamera().apply {
            zoom = 3.5
            onUpdate {
                verticalRotation += Time.deltaT * 5f
            }
        }

        addTextureMesh {
            generate {
                val rot = MutableMat3f()
                var uvOffset = 0f

                vertexModFun = {
                    // tangent warp function
                    val theta = PI_F / 4f
                    x = tan(x * theta) / tan(theta)
                    z = tan(z * theta) / tan(theta)
                    texCoord.y += uvOffset

                    norm()
                    rot.transform(this)
                }

                val stps = 20
                for (i in 0..3) {
                    grid {
                        center.set(0f, 1f, 0f)
                        sizeX = 2f
                        sizeY = 2f

                        stepsX = stps
                        stepsY = stps
                    }
                    rot.rotate(90f.deg, Vec3f.X_AXIS)
                    uvOffset += 1f
                }
                rot.setIdentity().rotate(90f.deg, Vec3f.Z_AXIS)
                for (i in 0..1) {
                    grid {
                        center.set(0f, 1f, 0f)
                        sizeX = 2f
                        sizeY = 2f

                        stepsX = stps
                        stepsY = stps
                    }
                    rot.rotate(180f.deg, Vec3f.Z_AXIS)
                    uvOffset += 1f
                }

                geometry.forEach {
                    it.normal.set(it.position).norm()
                }
            }
            shader = furShader
            instances = shells

            var windOffsetD = 0.0
            onUpdate {
                windOffsetD += Time.deltaT * windSpeed.value
                furShader.windOffset = windOffsetD.toFloat()
            }
        }

        addNode(Skybox.cube(envMap.reflectionMap, 2f))

        makeShells()
    }

    private fun makeShells() {
        shells.clear()

        val nShells = numShells.value
        val c = curliness.value / numShells.value
        shells.addInstances(nShells) { buf ->
            val mat = MutableMat4f()
            for (i in 0 ..< nShells) {
                val rx = sin(i * 50f / nShells) * c
                val ry = cos(i * 50f / nShells) * c
                val rz = sin(i * 50f / nShells) * cos(i * 10f / nShells) * c

                mat.rotate(rx.deg, ry.deg, rz.deg)
                mat.putTo(buf)

                // outer shells first
                if (nShells > 1) {
                    buf.put((nShells - i - 1) / (nShells - 1f))
                } else {
                    buf.put(0f)
                }
            }
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuRow {
            Text("Color") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(themes)
                    .selectedIndex(themes.indexOf(theme.use()))
                    .onItemSelected { theme.set(themes[it]) }
            }
        }
        MenuRow {
            Text("Density") { labelStyle() }
            MenuSlider(density.use(), 10f, 1000f, txtFormat = { "${it.toInt()}" }) { value ->
                density.set(round(value))
            }
        }
        MenuRow {
            Text("Hair Length") { labelStyle() }
            MenuSlider(hairLength.use(), 0.01f, 1f) { value -> hairLength.set(value) }
        }
        MenuRow {
            Text("Hair Thickness") { labelStyle() }
            MenuSlider(hairThickness.use(), 0.01f, 1f) { value -> hairThickness.set(value) }
        }
        MenuRow {
            Text("Hair Randomness") { labelStyle() }
            MenuSlider(hairRandomness.use(), 0.01f, 1f) { value -> hairRandomness.set(value) }
        }
        MenuRow {
            Text("Num Shells") { labelStyle() }
            MenuSlider(numShells.use().toFloat(), 8f, 128f, txtFormat = { "${it.toInt()}" }) { value ->
                numShells.set(value.toInt())
            }
        }
        MenuRow {
            Text("Curliness") { labelStyle() }
            MenuSlider(curliness.use(), 0f, 25f, txtFormat = { it.toString(1) }) { value ->
                curliness.set(value)
            }
        }

        Text("Noise") { sectionTitleStyle() }
        MenuRow {
            Text("Disp Scale") { labelStyle() }
            MenuSlider(dispScale.use(), 0f, 5f) { value -> dispScale.set(value) }
        }
        MenuRow {
            Text("Disp Strength") { labelStyle() }
            MenuSlider(dispStrength.use(), 0f, 1f) { value -> dispStrength.set(value) }
        }
        MenuRow {
            Text("Length Scale") { labelStyle() }
            MenuSlider(lenScale.use(), 0f, 5f) { value -> lenScale.set(value) }
        }
        MenuRow {
            Text("Length Strength") { labelStyle() }
            MenuSlider(lenStrength.use(), 0f, 1f) { value -> lenStrength.set(value) }
        }
        MenuRow {
            Text("Wind") { labelStyle() }
            MenuSlider(windSpeed.use(), 0f, 1f) { value -> windSpeed.set(value) }
        }
    }

    class FurShader : KslShader(furProgram(), PipelineConfig()) {
        var furGradient by texture1d("tFurColor")
        var noise3d by texture3d("tNoise3d")
        var irradiance by textureCube("tIrradiance")

        var density by uniform1f("uDensity", 300f)
        var hairLength by uniform1f("uHairLength", 0.5f)
        var hairThickness by uniform1f("uThickness", 1f)
        var hairRandomness by uniform1f("uRandomness", 1f)

        var noiseDispScale by uniform1f("uNoiseDispScale", 0.4f)
        var noiseDispStrength by uniform1f("uNoiseDispStrength", 0.4f)
        var noiseLenScale by uniform1f("uNoiseLenScale", 1f)
        var noiseLenStrength by uniform1f("uNoiseLenStrength", 0.5f)

        var windStrength by uniform1f("uWindStrength", 0.25f)
        var windOffset by uniform1f("uWindOffset", 0f)

        companion object {
            private fun furProgram(): KslProgram = KslProgram("Fur program").apply {
                val noise3d = texture3d("tNoise3d")

                val uv = interStageFloat2()
                val shell = interStageFloat1()
                val basePos = interStageFloat3()
                val localPos = interStageFloat3()
                val worldNormal = interStageFloat3()
                val camCos = interStageFloat1()

                vertexStage {
                    main {
                        val modelMat = instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT)
                        val camData = cameraData()

                        uv.input.set(vertexAttribFloat2(Attribute.TEXTURE_COORDS))
                        shell.input set instanceAttribFloat1(ATTRIB_SHELL)
                        val pos = float3Var(vertexAttribFloat3(Attribute.POSITIONS))
                        basePos.input set pos

                        // noise based displacement: static and dynamic (wind) part
                        val scale = uniformFloat1("uNoiseDispScale")
                        val strength = uniformFloat1("uNoiseDispStrength") * 0.2f.const

                        val samplePos = float3Var(pos.float3("zyx") * scale + uniformFloat1("uWindOffset") * uniformFloat1("uWindStrength"))
                        var d = sampleTexture(noise3d, samplePos).xyz * 2f.const - 1f.const
                        pos += d * shell.input * strength
                        d = sampleTexture(noise3d, pos.float3("zyx") * scale).xyz * 2f.const - 1f.const
                        pos += d * shell.input * strength

                        pos set normalize(pos)

                        // scale position based on shell layer to increase sphere radius of outer shells
                        val disp = float1Var(pow(shell.input + 0.01f.const, 0.3f.const) * uniformFloat1("uHairLength"))
                        pos set pos * (1f.const + disp)

                        localPos.input set pos
                        val worldPos4 = float4Var(modelMat * float4Value(pos, 1f))

                        worldNormal.input set (modelMat * float4Value(vertexAttribFloat3(Attribute.NORMALS), 0f)).xyz
                        val camDir = float3Var(worldPos4.xyz - camData.position)
                        camCos.input set abs(dot(normalize(worldNormal.input), normalize(camDir)))

                        outPosition set (camData.projMat * camData.viewMat) * worldPos4
                    }
                }
                fragmentStage {
                    main {
                        val fragPos = float2Var(uv.output * uniformFloat1("uDensity"))
                        val cellCenter = float2Var(fragPos.toInt2().toFloat2() + 0.5f.const)

                        val hairRandomness = uniformFloat1("uRandomness")
                        val hairThickness = uniformFloat1("uThickness")

                        // test own and neighboring cells and determine their randomly displaced center positions
                        // and select the closest one.
                        val nearestCell = float2Var(cellCenter)
                        val distToNearestHair = float1Var(10f.const)
                        for (x in -1..1) {
                            for (y in -1..1) {
                                val sampleCellCenter = float2Var(cellCenter + float2Value(x.toFloat(), y.toFloat()))
                                val centerRandom = float2Var(noise22(sampleCellCenter) * 2f.const - 1f.const)
                                val filaCenter = float2Var(sampleCellCenter + centerRandom * hairRandomness)
                                val dist = float1Var(length(fragPos - filaCenter))
                                `if`(dist lt distToNearestHair) {
                                    distToNearestHair set dist
                                    nearestCell.set(sampleCellCenter)
                                }
                            }
                        }

                        // determine length of selected hair
                        val randomHairLen = float1Var(noise12(nearestCell))
                        val perlinNoiseLenFac = float1Var(sampleTexture(noise3d, basePos.output * uniformFloat1("uNoiseLenScale")).x)
                        perlinNoiseLenFac += (sampleTexture(noise3d, basePos.output * uniformFloat1("uNoiseLenScale") * 5f.const).x - 0.5f.const) * 0.25f.const
                        randomHairLen *= mix(1f.const, perlinNoiseLenFac * 2f.const - 0.25f.const, uniformFloat1("uNoiseLenStrength"))

                        // relative position along hair: 1 -> bottom, 0 -> tip of the hair (or higher)
                        val hairLenPos = float1Var(1f.const - clamp(shell.output / randomHairLen, 0f.const, 1f.const))
                        // non-linear thickness falloff
                        val hairThicknessFac = float1Var(1f.const - (1f.const - hairLenPos) * (1f.const - hairLenPos))
                        val hairRadius = hairThickness * hairThicknessFac

                        val isOutside = bool1Var((hairRadius - distToNearestHair lt 0f.const) or (shell.output gt randomHairLen))
                        `if`(isOutside and (shell.output gt 0f.const)) {
                            discard()

                        }.`else` {
                            val lightColor = sampleTexture(textureCube("tIrradiance"), worldNormal.output).rgb

                            val furColor = sampleTexture(texture1d("tFurColor"), pow(shell.output, 1.5f.const)).rgb
                            val linColor = furColor * lightColor

                            colorOutput(convertColorSpace(linColor, ColorSpaceConversion.LINEAR_TO_sRGB_HDR), 1f.const)
                        }
                    }
                }
            }
        }
    }

    override suspend fun Assets.loadResources(ctx: KoolContext) {
        val colors = mapOf(
            "Blueish Gray" to MdColor.BLUE_GREY.toLinear().toOklab(),
            "Brown" to (MdColor.BROWN tone 800).toOklab(),
            "Green" to MdColor.LIGHT_GREEN.toLinear().toOklab(),
            "Blue" to MdColor.INDIGO.toLinear().toOklab(),
            "Pink" to MdColor.PINK.toLinear().toOklab(),
            "Amber" to MdColor.AMBER.toLinear().toOklab(),
        )
        colors.forEach { (name, color) ->
            themes += ColorTheme(name, GradientTexture(ColorGradient(
                color.shiftLightness(-0.5f).toLinearRgb(),
                color.toLinearRgb(),
                color.shiftChroma(-0.02f).shiftLightness(0.25f).toLinearRgb()
            )))
        }

        themes += ColorTheme("Viridis", GradientTexture(ColorGradient.VIRIDIS.toLinear()))
        themes += ColorTheme("Plasma", GradientTexture(ColorGradient.PLASMA.toLinear()))

        theme.set(themes[0])

        showLoadText("Loading IBL Maps")

        val iblMap = "mossy_forest_1k.rgbe.png"
        envMap = EnvironmentHelper.hdriEnvironment(mainScene, "${DemoLoader.hdriPath}/$iblMap")
        furShader.irradiance = envMap.irradianceMap

        showLoadText("Generating Noise")
        furShader.noise3d = generate3dNoise()
    }

    private fun generate3dNoise(): Texture3d {
        val pt = PerfTimer()
        var min = 10f
        var max = -10f

        val noise = MultiPerlin3d(3, 3)
        val nMinP = -0.7f
        val nMaxP = 0.7f

        val sz = 96
        val buf = Float32Buffer(sz*sz*sz*4)
        for (z in 0 until sz) {
            for (y in 0 until sz) {
                for (x in 0 until sz) {
                    val nx = x / sz.toFloat()
                    val ny = y / sz.toFloat()
                    val nz = z / sz.toFloat()

                    val fp = noise.eval(nx, ny, nz)
                    val n = (fp - nMinP) / (nMaxP - nMinP) * 0.9f + 0.06f
                    min = min(min, n)
                    max = max(max, n)

                    buf.put(n)
                    buf.put(0f)
                    buf.put(0f)
                    buf.put(1f)
                }
            }
        }

        // rotate noise for g and b channels
        for (z in 0 until sz) {
            for (y in 0 until sz) {
                for (x in 0 until sz) {
                    val i = z * sz * sz + y * sz + x
                    buf[i * 4 + 1] = buf[((sz-1-z) * sz * sz + y * sz + x) * 4]
                    buf[i * 4 + 2] = buf[(z * sz * sz + (sz-1-y) * sz + x) * 4]
                }
            }
        }

        logD { "Generated 3d noise in ${pt.takeSecs().toString(3)} s, tex saturation: min = $min, max = $max" }

        val props = TextureProps(
            addressModeU = AddressMode.REPEAT,
            addressModeV = AddressMode.REPEAT,
            addressModeW = AddressMode.REPEAT,
            format = TexFormat.RGBA_F16,
            maxAnisotropy = 1,
            mipMapping = false
        )
        return Texture3d(props) { TextureData3d(buf, sz, sz, sz, TexFormat.RGBA_F16) }
    }

    private fun ColorGradient.toLinear(): ColorGradient {
        val colors = Array(32) { getColor(it / 31f).toLinear() }
        return ColorGradient(*colors)
    }

    data class ColorTheme(val name: String, val texture: GradientTexture) {
        override fun toString(): String = name
    }

    companion object {
        val ATTRIB_SHELL = Attribute("aLayer", GlslType.FLOAT)
    }
}