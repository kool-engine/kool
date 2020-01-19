package de.fabmax.kool.demo

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.debugOverlay
import de.fabmax.kool.util.pbrMapGen.BrdfLutPass
import de.fabmax.kool.util.pbrMapGen.IrradianceMapPass
import de.fabmax.kool.util.pbrMapGen.ReflectionMapPass
import kotlin.math.max

/**
 * @author fabmax
 */
fun pbrDemo() {
    val ctx = createDefaultContext()
    ctx.assetMgr.assetsBaseDir = "./docs/assets"
    ctx.scenes += pbrDemoScene(ctx)
    ctx.scenes += debugOverlay(ctx)
    ctx.run()
}

fun pbrDemoScene(ctx: KoolContext): Scene = scene {
    +orbitInputTransform {
        zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
        +camera

        onPreRender += { ctx ->
            verticalRotation += ctx.deltaT * 5f
        }
    }

    ctx.clearColor = Color.fromHsv(0f, 0f, 0.1f, 1f)

//    val light1 = Light().setDirectional(Vec3f(-1f, -1f, -3f)).setColor(Color.WHITE, 5f)

    val lightStrength = 250f
    val zPos = 10f
    val light1 = Light().setPoint(Vec3f(10f, 10f, zPos)).setColor(Color.WHITE, lightStrength)
    val light2 = Light().setPoint(Vec3f(-10f, -10f, zPos)).setColor(Color.WHITE, lightStrength)
    val light3 = Light().setPoint(Vec3f(-10f, 10f, zPos)).setColor(Color.WHITE, lightStrength)
    val light4 = Light().setPoint(Vec3f(10f, -10f, zPos)).setColor(Color.WHITE, lightStrength)

    lighting.lights.clear()
    lighting.lights.add(light1)
    lighting.lights.add(light2)
    lighting.lights.add(light3)
    lighting.lights.add(light4)

    var irradianceMapPass: IrradianceMapPass? = null
    var reflectionMapPass: ReflectionMapPass? = null

    val hdris = listOf(
            "skybox/hdri/newport_loft.rgbe.png",
            "skybox/hdri/lakeside_2k.rgbe.png",
            "skybox/hdri/spruit_sunrise_2k.rgbe.png",
            "skybox/hdri/driving_school.rgbe.png",
            "skybox/hdri/royal_esplanade_2k.rgbe.png",
            "skybox/hdri/shanghai_bund_2k.rgbe.png",
            "skybox/hdri/vignaioli_night_2k.rgbe.png"
    )
    val hdriTextures = MutableList<Texture?>(hdris.size) { null }
    var hdriIndex = 0

    fun getHdriTex(idx: Int, recv: (Texture) -> Unit) {
        val tex = hdriTextures[idx]
        if (tex == null) {
            ctx.assetMgr.loadAndPrepareTexture(hdris[idx]) {
                hdriTextures[idx] = it
                recv(it)
            }
        } else {
            recv(tex)
        }
    }

    fun updateHdri(idx: Int) {
        getHdriTex(idx) { tex ->
            irradianceMapPass?.let {
                it.hdriTexture = tex
                it.offscreenPass.frameIdx = 0
                ctx.offscreenPasses += it.offscreenPass
            }
            reflectionMapPass?.let {
                it.hdriTexture = tex
                it.offscreenPass.frameIdx = 0
                ctx.offscreenPasses += it.offscreenPass
            }
        }
    }

    getHdriTex(hdriIndex) { tex ->
        val irrMapPass = IrradianceMapPass(tex)
        val reflMapPass = ReflectionMapPass(tex)
        val brdfLutPass = BrdfLutPass()
        irradianceMapPass = irrMapPass
        reflectionMapPass = reflMapPass

        ctx.offscreenPasses += irrMapPass.offscreenPass
        ctx.offscreenPasses += reflMapPass.offscreenPass
        ctx.offscreenPasses += brdfLutPass.offscreenPass

        //this += Skybox(irrMapPass.irradianceMap)
        this += Skybox(reflMapPass.reflectionMap, 1.2f)

//        colorGrid(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut)
//        roughnessMetallicGrid(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut)
//        bunny(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut, ctx)
        pbrMat(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut, ctx)
    }


    ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "switch hdri left", { it.isPressed }) {
        hdriIndex = (--hdriIndex + hdris.size) % hdris.size
        updateHdri(hdriIndex)
    }
    ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "switch hdri right", { it.isPressed }) {
        hdriIndex = ++hdriIndex % hdris.size
        updateHdri(hdriIndex)
    }
}

private fun Scene.colorGrid(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture): List<PbrShader> {
    val nRows = 4
    val nCols = 5
    val spacing = 4.5f

    val colors = mutableListOf<Color>()
    colors += Color.MD_COLORS
    colors.remove(Color.MD_LIGHT_BLUE)
    colors.remove(Color.MD_GREY)
    colors.remove(Color.MD_BLUE_GREY)
    colors += Color.WHITE
    colors += Color.MD_GREY
    colors += Color.MD_BLUE_GREY
    //colors += Color.BLACK
    colors += Color(0.1f, 0.1f, 0.1f)

    val shaders = mutableListOf<PbrShader>()

    for (y in 0 until nRows) {
        for (x in 0 until nCols) {
            +colorMesh {
                generator = {
                    //color = colors[(x * nRows + y) % colors.size].gamma()
                    color = colors[(y * nCols + x) % colors.size].gamma()
                    sphere {
                        steps = 100
                        center.set((-(nCols-1) * 0.5f + x) * spacing, ((nRows-1) * 0.5f - y) * spacing, 0f)
                        radius = 1.5f
                    }
                }

                val pbrConfig = PbrShader.PbrConfig()
                pbrConfig.irradianceMap = irradianceMap
                pbrConfig.reflectionMap = reflectionMap
                pbrConfig.brdfLut = brdfLut

                val shader = PbrShader(pbrConfig)
                shader.roughness = 0.1f
                shader.metallic = 1f
                pipelineConfig { shaderLoader = shader::setup }
                shaders += shader
            }
        }
    }
    return shaders
}

private fun Scene.roughnessMetallicGrid(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture): List<PbrShader> {
    val nRows = 7
    val nCols = 7
    val spacing = 2.5f

    val shaders = mutableListOf<PbrShader>()

    for (y in 0 until nRows) {
        for (x in 0 until nCols) {
            +colorMesh {
                generator = {
                    color = Color.DARK_RED
                    //color = Color.MD_GREEN.gamma()
                    sphere {
                        steps = 100
                        center.set((-(nCols-1) * 0.5f + x) * spacing, (-(nRows-1) * 0.5f + y) * spacing, 0f)
                        radius = 1f
                    }
                }

                val pbrConfig = PbrShader.PbrConfig()
                pbrConfig.irradianceMap = irradianceMap
                pbrConfig.reflectionMap = reflectionMap
                pbrConfig.brdfLut = brdfLut

                val shader = PbrShader(pbrConfig)
                shader.roughness = max(x / (nCols - 1).toFloat(), 0.05f)
                shader.metallic = y / (nRows - 1).toFloat()
                pipelineConfig { shaderLoader = shader::setup }
                shaders += shader
            }
        }
    }

    return shaders
}

private fun Scene.bunny(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture, ctx: KoolContext) {
    loadModel(ctx.assetMgr) { model ->
        val colorMesh = mesh(setOf(Attribute.POSITIONS, Attribute.COLORS, Attribute.NORMALS)) {
            meshData.vertexList.addFrom(model.meshData.vertexList)
            meshData.vertexList.forEach { it.color.set(Color(1.00f, 0.86f, 0.57f).gamma()) }

            val pbrConfig = PbrShader.PbrConfig()
            pbrConfig.irradianceMap = irradianceMap
            pbrConfig.reflectionMap = reflectionMap
            pbrConfig.brdfLut = brdfLut

            val shader = PbrShader(pbrConfig)
            shader.roughness = 0.05f
            shader.metallic = 1.0f
            pipelineConfig { shaderLoader = shader::setup }
        }
        +colorMesh
    }
}

private fun Scene.pbrMat(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture, ctx: KoolContext) {
    val matMaps = materials.values.toList()
    var matIdx = 1

    val noHeight = Texture { BufferedTextureData.singleColor(Color.BLACK) }
    val noAo = Texture { BufferedTextureData.singleColor(Color.WHITE) }

    val pbrConfig = PbrShader.PbrConfig()
    pbrConfig.irradianceMap = irradianceMap
    pbrConfig.reflectionMap = reflectionMap
    pbrConfig.brdfLut = brdfLut
    pbrConfig.albedoMap = matMaps[matIdx].albedo
    pbrConfig.normalMap = matMaps[matIdx].normal
    pbrConfig.metallicMap = matMaps[matIdx].metallic
    pbrConfig.roughnessMap = matMaps[matIdx].roughness
    pbrConfig.ambientOcclusionMap = matMaps[matIdx].ao ?: noAo
    pbrConfig.heightMap = matMaps[matIdx].height ?: noHeight

    fun setMaterial(shader: PbrShader, mat: MaterialMaps) {
        shader.albedoMap = mat.albedo
        shader.normalMap = mat.normal
        shader.metallicMap = mat.metallic
        shader.roughnessMap = mat.roughness
        shader.ambientOcclusionMap = mat.ao ?: noAo
        shader.heightMap = mat.height ?: noHeight
    }

    +transformGroup {
        +textureMesh(isNormalMapped = true) {
            generator = {
                sphere {
                    steps = 1000
                    radius = 3f
                }
            }

            val shader = PbrShader(pbrConfig)
            shader.roughness = 0.1f
            shader.metallic = 1f
            pipelineConfig { shaderLoader = shader::setup }

            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_UP, "Change Mat +", { it.isPressed }) {
                matIdx = (matIdx + 1) % matMaps.size
                setMaterial(shader, matMaps[matIdx])
            }
            ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_DOWN, "Change Mat -", { it.isPressed }) {
                matIdx = (matIdx + matMaps.size - 1) % matMaps.size
                setMaterial(shader, matMaps[matIdx])
            }
        }

        onPreRender += { ctx ->
            rotate(-5f * ctx.deltaT, Vec3f.Y_AXIS)
        }
    }
}

data class MaterialMaps(val albedo: Texture, val normal: Texture, val metallic: Texture, val roughness: Texture, val ao: Texture?, val height: Texture?)

val materials = mutableMapOf(
        "Bamboo" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-albedo.png") },
            Texture { it.loadImageData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-normal.png") },
            Texture { it.loadImageData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-metal.png") },
            Texture { it.loadImageData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-roughness.png") },
            Texture { it.loadImageData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-ao.png") },
            null
        ),

        "Castle Brick" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/castle_brick/castle_brick_02_red_diff_2k.jpg") },
            Texture { it.loadImageData("reserve/pbr/castle_brick/castle_brick_02_red_nor_2k.jpg") },
            Texture { BufferedTextureData.singleColor(Color.BLACK) },
            Texture { it.loadImageData("reserve/pbr/castle_brick/castle_brick_02_red_rough_2k.jpg") },
            Texture { it.loadImageData("reserve/pbr/castle_brick/castle_brick_02_red_ao_2k.jpg") },
            Texture { it.loadImageData("reserve/pbr/castle_brick/castle_brick_02_red_disp_2k.jpg") }
        ),

        "Copper Rock" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/copper_rock/copper-rock1-alb.png") },
            Texture { it.loadImageData("reserve/pbr/copper_rock/copper-rock1-normal.png") },
            Texture { it.loadImageData("reserve/pbr/copper_rock/copper-rock1-metal.png") },
            Texture { it.loadImageData("reserve/pbr/copper_rock/copper-rock1-rough.png") },
            Texture { it.loadImageData("reserve/pbr/copper_rock/copper-rock1-ao.png") },
            Texture { it.loadImageData("reserve/pbr/copper_rock/copper-rock1-height.png") }
        ),

        "Dark Tiles" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/dark_tiles/darktiles1_basecolor.png") },
            Texture { it.loadImageData("reserve/pbr/dark_tiles/darktiles1_normal-DX.png") },
            Texture { it.loadImageData("reserve/pbr/dark_tiles/darktiles1_metallic.png") },
            Texture { it.loadImageData("reserve/pbr/dark_tiles/darktiles1_roughness.png") },
            Texture { it.loadImageData("reserve/pbr/dark_tiles/darktiles1_AO.png") },
            null
        ),

        "Dungeon Stone" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/dungeon-stone1/dungeon-stone1-albedo2.png") },
            Texture { it.loadImageData("reserve/pbr/dungeon-stone1/dungeon-stone1-normal.png") },
            Texture { it.loadImageData("reserve/pbr/dungeon-stone1/dungeon-stone1-metalness.png") },
            Texture { it.loadImageData("reserve/pbr/dungeon-stone1/dungeon-stone1-roughness.png") },
            Texture { it.loadImageData("reserve/pbr/dungeon-stone1/dungeon-stone1-ao.png") },
            Texture { it.loadImageData("reserve/pbr/dungeon-stone1/dungeon-stone1-height.png") }
        ),

        "Granite" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/granitesmooth1/granitesmooth1-albedo4.png") },
            Texture { it.loadImageData("reserve/pbr/granitesmooth1/granitesmooth1-normal2.png") },
            Texture { it.loadImageData("reserve/pbr/granitesmooth1/granitesmooth1-metalness.png") },
            Texture { it.loadImageData("reserve/pbr/granitesmooth1/granitesmooth1-roughness3.png") },
            null,
            null
        ),

        "Greasy Metal" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/greasy_pan/greasy-metal-pan1-albedo.png") },
            Texture { it.loadImageData("reserve/pbr/greasy_pan/greasy-metal-pan1-normal.png") },
            Texture { it.loadImageData("reserve/pbr/greasy_pan/greasy-metal-pan1-metal.png") },
            Texture { it.loadImageData("reserve/pbr/greasy_pan/greasy-metal-pan1-roughness.png") },
            null,
            null
        ),

        "Greasy Pan" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/greasy_pan2/greasy-pan-2-albedo.png") },
            Texture { it.loadImageData("reserve/pbr/greasy_pan2/greasy-pan-2-normal.png") },
            Texture { it.loadImageData("reserve/pbr/greasy_pan2/greasy-pan-2-metal.png") },
            Texture { it.loadImageData("reserve/pbr/greasy_pan2/greasy-pan-2-roughness.png") },
            null,
            null
        ),

        "Hardwood Planks" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/hardwood_planks/hardwood-brown-planks-albedo.png") },
            Texture { it.loadImageData("reserve/pbr/hardwood_planks/hardwood-brown-planks-normal-dx.png") },
            Texture { it.loadImageData("reserve/pbr/hardwood_planks/hardwood-brown-planks-metallic.png") },
            Texture { it.loadImageData("reserve/pbr/hardwood_planks/hardwood-brown-planks-roughness.png") },
            Texture { it.loadImageData("reserve/pbr/hardwood_planks/hardwood-brown-planks-ao.png") },
            null //Texture { it.loadImageData("reserve/pbr/hardwood_planks/hardwood-brown-planks-height.png") }
        ),

        "Harsh Bricks" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/harshbricks/harshbricks-albedo.png") },
            Texture { it.loadImageData("reserve/pbr/harshbricks/harshbricks-normal.png") },
            Texture { it.loadImageData("reserve/pbr/harshbricks/harshbricks-metalness.png") },
            Texture { it.loadImageData("reserve/pbr/harshbricks/harshbricks-roughness.png") },
            Texture { it.loadImageData("reserve/pbr/harshbricks/harshbricks-ao2.png") },
            Texture { it.loadImageData("reserve/pbr/harshbricks/harshbricks-height5-16.png") }
        ),

        "Splotchy Metal" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/metal_splotchy/metal-splotchy-albedo.png") },
            Texture { it.loadImageData("reserve/pbr/metal_splotchy/metal-splotchy-normal-dx.png") },
            Texture { it.loadImageData("reserve/pbr/metal_splotchy/metal-splotchy-metal.png") },
            Texture { it.loadImageData("reserve/pbr/metal_splotchy/metal-splotchy-rough.png") },
            null,
            null
        ),

        "Modern Tiles" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/modern-tile1/modern-tile1-albedo.png") },
            Texture { it.loadImageData("reserve/pbr/modern-tile1/modern-tile1-normal-dx.png") },
            Texture { it.loadImageData("reserve/pbr/modern-tile1/modern-tile1-metallic.png") },
            Texture { it.loadImageData("reserve/pbr/modern-tile1/modern-tile1-roughness.png") },
            Texture { it.loadImageData("reserve/pbr/modern-tile1/modern-tile1-ao.png") },
            Texture { it.loadImageData("reserve/pbr/modern-tile1/modern-tile1-height.png") }
        ),

        "Octo Stone" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/octostone/octostoneAlbedo.png") },
            Texture { it.loadImageData("reserve/pbr/octostone/octostoneNormalc.png") },
            Texture { it.loadImageData("reserve/pbr/octostone/octostoneMetallic.png") },
            Texture { it.loadImageData("reserve/pbr/octostone/octostoneRoughness2.png") },
            Texture { it.loadImageData("reserve/pbr/octostone/octostoneAmbient_Occlusion.png") },
            Texture { it.loadImageData("reserve/pbr/octostone/octostoneHeight.png") }
        ),

        "Ornate Brass" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/ornate-brass3/ornate-brass3_albedo.png") },
            Texture { it.loadImageData("reserve/pbr/ornate-brass3/ornate-brass3_normal-dx.png") },
            Texture { it.loadImageData("reserve/pbr/ornate-brass3/ornate-brass3_metallic.png") },
            Texture { it.loadImageData("reserve/pbr/ornate-brass3/ornate-brass3_roughness.png") },
            Texture { it.loadImageData("reserve/pbr/ornate-brass3/ornate-brass3_ao.png") },
            Texture { it.loadImageData("reserve/pbr/ornate-brass3/ornate-brass3_height.png") }
        ),

        "Streaked Rock" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/rock_vstreaks/rock_vstreaks_Base_Color.png") },
            Texture { it.loadImageData("reserve/pbr/rock_vstreaks/rock_vstreaks_Normal-ue.png") },
            Texture { it.loadImageData("reserve/pbr/rock_vstreaks/rock_vstreaks_Metallic.png") },
            Texture { it.loadImageData("reserve/pbr/rock_vstreaks/rock_vstreaks_Roughness.png") },
            Texture { it.loadImageData("reserve/pbr/rock_vstreaks/rock_vstreaks_Ambient_Occlusion.png") },
            Texture { it.loadImageData("reserve/pbr/rock_vstreaks/rock_vstreaks_Height.png") }
        ),

        "Rusted Iron" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/rusted_iron2/rustediron2_basecolor.png") },
            Texture { it.loadImageData("reserve/pbr/rusted_iron2/rustediron2_normal.png") },
            Texture { it.loadImageData("reserve/pbr/rusted_iron2/rustediron2_metallic.png") },
            Texture { it.loadImageData("reserve/pbr/rusted_iron2/rustediron2_roughness.png") },
            null,
            null
        ),

        "Scuffed Plastic" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/scuffed-plastic-1/scuffed-plastic4-alb.png") },
            Texture { it.loadImageData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-normal.png") },
            Texture { it.loadImageData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-metal.png") },
            Texture { it.loadImageData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-rough.png") },
            Texture { it.loadImageData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-ao.png") },
            null
        ),

        "Snow Covered Path" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/snowcoveredpath/snowcoveredpath_albedo.png") },
            Texture { it.loadImageData("reserve/pbr/snowcoveredpath/snowcoveredpath_normal-dx.png") },
            Texture { it.loadImageData("reserve/pbr/snowcoveredpath/snowcoveredpath_metallic.png") },
            Texture { it.loadImageData("reserve/pbr/snowcoveredpath/snowcoveredpath_roughness.png") },
            Texture { it.loadImageData("reserve/pbr/snowcoveredpath/snowcoveredpath_ao.png") },
            Texture { it.loadImageData("reserve/pbr/snowcoveredpath/snowcoveredpath_height.png") }
        ),

        "Stepping Stones" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/steppingstones1/steppingstones1_albedo.png") },
            Texture { it.loadImageData("reserve/pbr/steppingstones1/steppingstones1_normal-dx.png") },
            Texture { it.loadImageData("reserve/pbr/steppingstones1/steppingstones1_metallic.png") },
            Texture { it.loadImageData("reserve/pbr/steppingstones1/steppingstones1_roughness.png") },
            Texture { it.loadImageData("reserve/pbr/steppingstones1/steppingstones1_ao.png") },
            Texture { it.loadImageData("reserve/pbr/steppingstones1/steppingstones1_height.png") }
        ),

        "Marble" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/streaked-marble/streaked-marble-albedo2.png") },
            Texture { it.loadImageData("reserve/pbr/streaked-marble/streaked-marble-normal.png") },
            Texture { it.loadImageData("reserve/pbr/streaked-marble/streaked-marble-metalness.png") },
            Texture { it.loadImageData("reserve/pbr/streaked-marble/streaked-marble-roughness1.png") },
            null,
            null
        ),

        "Water Worn Stone" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/waterwornstone1/waterwornstone1_Base_Color.png") },
            Texture { it.loadImageData("reserve/pbr/waterwornstone1/waterwornstone1_Normal.png") },
            Texture { it.loadImageData("reserve/pbr/waterwornstone1/waterwornstone1_Metallic.png") },
            Texture { it.loadImageData("reserve/pbr/waterwornstone1/waterwornstone1_Roughness.png") },
            Texture { it.loadImageData("reserve/pbr/waterwornstone1/waterwornstone1_Ambient_Occlusion.png") },
            Texture { it.loadImageData("reserve/pbr/waterwornstone1/waterwornstone1_Height.png") }
        ),

        "Wet Cobblestone" to MaterialMaps(
            Texture { it.loadImageData("reserve/pbr/wetcobble/wetcobble_albedo.png") },
            Texture { it.loadImageData("reserve/pbr/wetcobble/wetcobble_normal-dx.png") },
            Texture { it.loadImageData("reserve/pbr/wetcobble/wetcobble_metallic.png") },
            Texture { it.loadImageData("reserve/pbr/wetcobble/wetcobble_roughness.png") },
            Texture { it.loadImageData("reserve/pbr/wetcobble/wetcobble_ao.png") },
            Texture { it.loadImageData("reserve/pbr/wetcobble/wetcobble_height.png") }
        )
)