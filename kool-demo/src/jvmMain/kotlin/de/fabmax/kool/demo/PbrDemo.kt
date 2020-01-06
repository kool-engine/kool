package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.debugOverlay
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

    val lightStrength = 500f
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

//    colorGrid()
    roughnessMetallicGrid()

    val cubeMapPass = hdriToCubeMapPass()
    ctx.offscreenPasses += cubeMapPass
    +Skybox(cubeMapPass.textureCube)
//    +Skybox("skybox/y-up/sky_ft.jpg", "skybox/y-up/sky_bk.jpg",
//            "skybox/y-up/sky_lt.jpg", "skybox/y-up/sky_rt.jpg",
//            "skybox/y-up/sky_up.jpg", "skybox/y-up/sky_dn.jpg")
}

private fun Scene.colorGrid() {
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

                val shader = ModeledShader.PbrShader()
                shader.roughness = 0.1f
                shader.metallic = 0f
                pipelineConfig { shaderLoader = shader::setup }
            }
        }
    }
}

private fun Scene.roughnessMetallicGrid() {
    val nRows = 7
    val nCols = 7
    val spacing = 2.5f

    for (y in 0 until nRows) {
        for (x in 0 until nCols) {
            +colorMesh {
                generator = {
                    color = Color.DARK_RED
                    sphere {
                        steps = 100
                        center.set((-(nCols-1) * 0.5f + x) * spacing, (-(nRows-1) * 0.5f + y) * spacing, 0f)
                        radius = 1f
                    }
                }

                val shader = ModeledShader.PbrShader()
                shader.roughness = max(x / (nCols - 1).toFloat(), 0.05f)
                shader.metallic = y / (nRows - 1).toFloat()
                pipelineConfig { shaderLoader = shader::setup }
            }
        }
    }
}
