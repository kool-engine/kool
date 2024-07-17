package de.fabmax.kool.demo

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.math.noise.MultiPerlin3d
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.*

class ShellShadingDemo : DemoScene("Shell Shading") {

    private val envMap by hdriImage("${DemoLoader.hdriPath}/mossy_forest_1k.rgbe.png")
    private lateinit var sphereMesh: Mesh
    private lateinit var bunnyMesh: Mesh

    private val themes = mutableListOf<ColorTheme>()

    private val furShaderSphere = FurShaderSettings(uvBased = true)
    private val furShaderBunny = FurShaderSettings(
        uvBased = false,
        initShells = 16,
        dispScale = 0.15f,
        dispStrength = 0.4f,
        lengthScale = 0.84f,
        hairLength = 0.2f,
        hairThickness = 1.5f,
        density = 500f
    )
    private var activeFurShader = furShaderBunny
    private val selectedModel = mutableStateOf(1)

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera().apply {
            zoom = 3.5
        }

        val windOffsetD = MutableVec3d()
        onUpdate {
            windOffsetD.x += Time.deltaT * activeFurShader.windSpeed.value * 0.7
            windOffsetD.y += Time.deltaT * activeFurShader.windSpeed.value * 0.9
            windOffsetD.z += Time.deltaT * activeFurShader.windSpeed.value * 1.1
            furShaderSphere.furShader.windOffset = windOffsetD.toVec3f()
            furShaderBunny.furShader.windOffset = windOffsetD.toVec3f()
        }

        addNode(bunnyMesh)
        bunnyMesh.apply {
            geometry.forEach { it.mul(0.35f).subtract(Vec3f(0f, 1.25f, 0f))  }
            shader = furShaderBunny.furShader
        }

        sphereMesh = addTextureMesh(instances = furShaderSphere.shells) {
            isVisible = false
            generate {
                generateFurSphere()
            }
            shader = furShaderSphere.furShader
        }

        addNode(Skybox.cube(envMap.reflectionMap, 2f))
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuRow {
            Text("Model") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(listOf("Sphere", "Bunny"))
                    .selectedIndex(selectedModel.use())
                    .onItemSelected {
                        selectedModel.set(it)
                        when (it) {
                            0 -> {
                                sphereMesh.isVisible = true
                                bunnyMesh.isVisible = false
                                activeFurShader = furShaderSphere
                            }
                            1 -> {
                                sphereMesh.isVisible = false
                                bunnyMesh.isVisible = true
                                activeFurShader = furShaderBunny
                            }
                        }
                    }
            }
        }
        MenuRow {
            Text("Color") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(themes)
                    .selectedIndex(themes.indexOf(activeFurShader.theme.use()))
                    .onItemSelected { activeFurShader.theme.set(themes[it]) }
            }
        }
        MenuRow {
            Text("Density") { labelStyle() }
            MenuSlider(activeFurShader.density.use(), 10f, 1000f, txtFormat = { "${it.toInt()}" }) { value ->
                activeFurShader.density.set(round(value))
            }
        }
        MenuRow {
            Text("Hair Length") { labelStyle() }
            MenuSlider(activeFurShader.hairLength.use(), 0.01f, 1f) { value -> activeFurShader.hairLength.set(value) }
        }
        MenuRow {
            Text("Hair Thickness") { labelStyle() }
            MenuSlider(activeFurShader.hairThickness.use(), 0.01f, 2f) { value -> activeFurShader.hairThickness.set(value) }
        }
        MenuRow {
            Text("Hair Random") { labelStyle() }
            MenuSlider(activeFurShader.hairRandomness.use(), 0.01f, 1f) { value -> activeFurShader.hairRandomness.set(value) }
        }
        MenuRow {
            Text("Num Shells") { labelStyle() }
            MenuSlider(activeFurShader.numShells.use().toFloat(), 8f, 128f, txtFormat = { "${it.toInt()}" }) { value ->
                activeFurShader.numShells.set(value.toInt())
            }
        }
        MenuRow {
            Text("Curliness") { labelStyle() }
            MenuSlider(activeFurShader.curliness.use(), 0f, 25f, txtFormat = { it.toString(1) }) { value ->
                activeFurShader.curliness.set(value)
            }
        }

        Text("Noise") { sectionTitleStyle() }
        MenuRow {
            Text("Length Scale") { labelStyle() }
            MenuSlider(activeFurShader.lenScale.use(), 0f, 5f) { value -> activeFurShader.lenScale.set(value) }
        }
        MenuRow {
            Text("Length Strength") { labelStyle() }
            MenuSlider(activeFurShader.lenStrength.use(), 0f, 1f) { value -> activeFurShader.lenStrength.set(value) }
        }
        MenuRow {
            Text("Wind Scale") { labelStyle() }
            MenuSlider(activeFurShader.dispScale.use(), 0f, 3f) { value -> activeFurShader.dispScale.set(value) }
        }
        MenuRow {
            Text("Wind Strength") { labelStyle() }
            MenuSlider(activeFurShader.dispStrength.use(), 0f, 1f) { value -> activeFurShader.dispStrength.set(value) }
        }
        MenuRow {
            Text("Wind Speed") { labelStyle() }
            MenuSlider(activeFurShader.windSpeed.use(), 0f, 1f) { value -> activeFurShader.windSpeed.set(value) }
        }
    }

    private fun MeshBuilder.generateFurSphere() {
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
        furShaderSphere.theme.set(themes[0])
        furShaderBunny.theme.set(themes[1])

        showLoadText("Generating Noise")
        val perlinNoise = generate3dNoise().also { it.releaseWith(mainScene) }
        val irradiance = envMap.irradianceMap

        furShaderSphere.furShader.noise3d = perlinNoise
        furShaderSphere.furShader.irradiance = irradiance
        furShaderBunny.furShader.noise3d = perlinNoise
        furShaderBunny.furShader.irradiance = irradiance

        mainScene.onRelease {
            themes.forEach { it.texture.release() }
        }

        val modelCfg = GltfLoadConfig(generateNormals = true, applyMaterials = false)
        val model = loadGltfFile("${DemoLoader.modelPath}/bunny.gltf.gz").makeModel(modelCfg, 1).meshes.values.first()
        bunnyMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, instances = furShaderBunny.shells).apply {
            generate {
                geometry.addGeometry(model.geometry)
            }
        }
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

        val props = TextureProps(format = TexFormat.RGBA_F16, generateMipMaps = false)
        return Texture3d(props) { TextureData3d(buf, sz, sz, sz, TexFormat.RGBA_F16) }
    }

    private fun ColorGradient.toLinear(): ColorGradient {
        val colors = Array(32) { getColor(it / 31f).toLinear() }
        return ColorGradient(*colors)
    }

    data class ColorTheme(val name: String, val texture: GradientTexture) {
        override fun toString(): String = name
    }

    private class FurShaderSettings(
        val uvBased: Boolean,
        initShells: Int = 48,
        dispScale: Float = 0.4f,
        dispStrength: Float = 0.4f,
        lengthScale: Float = 1f,
        lengthStrength: Float = 0.5f,
        hairLength: Float = 0.5f,
        hairThickness: Float = 1f,
        density: Float = 300f
    ) {
        val furShader = FurShader(uvBased)

        val shells = MeshInstanceList(128, Attribute.INSTANCE_MODEL_MAT, ATTRIB_SHELL)
        val numShells = mutableStateOf(initShells).onChange { _, _ -> makeShells() }

        val theme = mutableStateOf<ColorTheme?>(null).onChange { _, new -> furShader.furGradient = new?.texture }
        val density = mutableStateOf(density).onChange { _, new -> furShader.density = new }
        val hairLength = mutableStateOf(hairLength).onChange { _, new -> furShader.hairLength = new }
        val hairThickness = mutableStateOf(hairThickness).onChange { _, new -> furShader.hairThickness = new }
        val hairRandomness = mutableStateOf(furShader.hairRandomness).onChange { _, new -> furShader.hairRandomness = new }
        val curliness = mutableStateOf(0f).onChange { _, _ -> makeShells() }

        val dispScale = mutableStateOf(dispScale).onChange { _, new -> furShader.noiseDispScale = new }
        val dispStrength = mutableStateOf(dispStrength).onChange { _, new -> furShader.noiseDispStrength = new }
        val lenScale = mutableStateOf(lengthScale).onChange { _, new -> furShader.noiseLenScale = new }
        val lenStrength = mutableStateOf(lengthStrength).onChange { _, new -> furShader.noiseLenStrength = new }

        val windSpeed = mutableStateOf(0.4f)

        init {
            makeShells()
            furShader.density = density
            furShader.noiseDispScale = dispScale
            furShader.noiseDispStrength = dispStrength
            furShader.noiseLenScale = lengthScale
            furShader.noiseLenStrength = lengthStrength
            furShader.hairLength = hairLength
            furShader.hairThickness = hairThickness
        }

        fun makeShells() {
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
    }

    companion object {
        val ATTRIB_SHELL = Attribute("aLayer", GpuType.FLOAT1)
    }
}

class FurShader(uvBased: Boolean) : KslShader("Fur shader") {
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
    var windOffset by uniform3f("uWindOffset")

    init {
        program.furProgram(uvBased)
    }

    private fun KslProgram.furProgram(uvBased: Boolean) {
        val noise3d = texture3d("tNoise3d")

        val basePos = interStageFloat3()
        val localPos = interStageFloat3()
        val worldNormal = interStageFloat3()
        val uv = interStageFloat2()
        val shell = interStageFloat1()
        val camCos = interStageFloat1()

        vertexStage {
            main {
                val modelMat = instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT)
                val camData = cameraData()

                if (uvBased) {
                    uv.input.set(vertexAttribFloat2(Attribute.TEXTURE_COORDS))
                }
                shell.input set instanceAttribFloat1(ShellShadingDemo.ATTRIB_SHELL)
                val nrm = float3Var(vertexAttribFloat3(Attribute.NORMALS))
                val pos = float3Var(vertexAttribFloat3(Attribute.POSITIONS))
                basePos.input set pos

                // noise based displacement: static and dynamic (wind) part
                val scale = uniformFloat1("uNoiseDispScale")
                val strength = uniformFloat1("uNoiseDispStrength") * 0.2f.const

                val samplePos = float3Var(pos * scale + uniformFloat3("uWindOffset") * uniformFloat1("uWindStrength"))
                val d = float3Var(sampleTexture(noise3d, samplePos, 0f.const).xyz * 2f.const - 1f.const)
                val windPos = float3Var(pos + d * shell.input * strength)
                d set sampleTexture(noise3d, pos.float3("zyx") * scale, 0f.const).xyz * 2f.const - 1f.const
                windPos += d * shell.input * strength

                // reproject distorted (windy) position to shell surface
                windPos -= nrm * dot((windPos - pos), nrm)

                // scale position based on shell layer to increase sphere radius of outer shells
                val disp = float1Var(pow(shell.input + 0.01f.const, 0.3f.const) * uniformFloat1("uHairLength"))
                pos set windPos + nrm * disp

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
                val hairRandomness = uniformFloat1("uRandomness")
                val hairThickness = uniformFloat1("uThickness")
                val randomHairLen = float1Var()
                val distToNearestHair = float1Var(10f.const)

                // test own and neighboring cells and determine their randomly displaced center positions
                // and select the closest one.

                if (uvBased) {
                    val fragPos = float2Var(uv.output * uniformFloat1("uDensity"))
                    val cellCenter = float2Var(fragPos.toInt2().toFloat2() + 0.5f.const)
                    val nearestCell = float2Var(cellCenter)
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
                    randomHairLen set float1Var(noise12(nearestCell) * 0.5f.const + 0.5f.const)

                } else {
                    val fragPos = float3Var((basePos.output + 5f.const) * uniformFloat1("uDensity"))
                    val cellCenter = float3Var(fragPos.toInt3().toFloat3() + 0.5f.const)
                    val nearestCell = float3Var(cellCenter)

                    val sampleCellCenter = float3Var()
                    val centerRandom = float3Var()
                    val filaCenter = float3Var()
                    val dist = float1Var()

                    for (x in -1..1) {
                        for (y in -1..1) {
                            for (z in -1..1) {
                                sampleCellCenter set float3Var(cellCenter + float3Value(x.toFloat(), y.toFloat(), z.toFloat()))
                                centerRandom set float3Var(noise33(sampleCellCenter) * 2f.const - 1f.const)
                                filaCenter set float3Var(sampleCellCenter + centerRandom * hairRandomness)
                                dist set float1Var(length(fragPos - filaCenter))
                                `if`(dist lt distToNearestHair) {
                                    distToNearestHair set dist
                                    nearestCell.set(sampleCellCenter)
                                }
                            }
                        }
                    }
                    randomHairLen set noise13(nearestCell) * 0.5f.const + 0.5f.const
                }

                // determine length of selected hair
                val perlinNoiseLenFac = float1Var(sampleTexture(noise3d, basePos.output * uniformFloat1("uNoiseLenScale")).x)
                randomHairLen *= mix(1f.const, perlinNoiseLenFac * 2f.const - 0.25f.const, uniformFloat1("uNoiseLenStrength"))

                // relative position along hair: 1 -> bottom, 0 -> tip of the hair (or higher)
                val hairLenPos = float1Var(1f.const - clamp(shell.output / randomHairLen, 0f.const, 1f.const))
                // non-linear thickness falloff
                val hairThicknessFac = float1Var(1f.const - (1f.const - hairLenPos) * (1f.const - hairLenPos))
                val hairRadius = hairThickness * hairThicknessFac

                // 1d texture needs to be sampled in uniform control flow to work in webgpu (the usual level = 0 trick does not work for 1d textures...)
                val furColor = float3Var(sampleTexture(texture1d("tFurColor"), pow(shell.output, 1.5f.const)).rgb)
                val isOutside = bool1Var((hairRadius - distToNearestHair lt 0f.const) or (shell.output gt randomHairLen))

                `if`(isOutside and (shell.output gt 0f.const)) {
                    discard()
                }.`else` {
                    val lightColor = float3Var(sampleTexture(textureCube("tIrradiance"), worldNormal.output, 0f.const).rgb)
                    val linColor = float3Var(furColor * lightColor)
                    colorOutput(convertColorSpace(linColor, ColorSpaceConversion.LinearToSrgbHdr()), 1f.const)
                }
            }
        }
    }
}