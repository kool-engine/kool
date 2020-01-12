package de.fabmax.kool.demo

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shading.ModeledShader
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
    defaultCamTransform()

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
        this += Skybox(reflMapPass.reflectionMap)

        colorGrid(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut)
//        roughnessMetallicGrid(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut)
//        bunny(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut, ctx)
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

private fun Scene.colorGrid(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture): List<ModeledShader.PbrShader> {
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
    colors += Color.BLACK

    val shaders = mutableListOf<ModeledShader.PbrShader>()

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

                val pbrConfig = ModeledShader.PbrShader.PbrConfig()
                pbrConfig.irradianceMap = irradianceMap
                pbrConfig.reflectionMap = reflectionMap
                pbrConfig.brdfLut = brdfLut

                val shader = ModeledShader.PbrShader(pbrConfig)
                shader.roughness = 0.1f
                shader.metallic = 1f
                pipelineConfig { shaderLoader = shader::setup }
                shaders += shader
            }
        }
    }
    return shaders
}

private fun Scene.roughnessMetallicGrid(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture): List<ModeledShader.PbrShader> {
    val nRows = 7
    val nCols = 7
    val spacing = 2.5f

    val shaders = mutableListOf<ModeledShader.PbrShader>()

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

                val pbrConfig = ModeledShader.PbrShader.PbrConfig()
                pbrConfig.irradianceMap = irradianceMap
                pbrConfig.reflectionMap = reflectionMap
                pbrConfig.brdfLut = brdfLut

                val shader = ModeledShader.PbrShader(pbrConfig)
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

            val pbrConfig = ModeledShader.PbrShader.PbrConfig()
            pbrConfig.irradianceMap = irradianceMap
            pbrConfig.reflectionMap = reflectionMap
            pbrConfig.brdfLut = brdfLut

            val shader = ModeledShader.PbrShader(pbrConfig)
            shader.roughness = 0.05f
            shader.metallic = 1.0f
            pipelineConfig { shaderLoader = shader::setup }
        }
        +colorMesh
    }
}