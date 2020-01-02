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

    val lightStrength = 300f
    val zPos = 10f
    val light1 = Light().setPoint(Vec3f(-10f, -10f, zPos)).setColor(Color.WHITE, lightStrength)
    val light2 = Light().setPoint(Vec3f(-10f, 10f, zPos)).setColor(Color.WHITE, lightStrength)
    val light3 = Light().setPoint(Vec3f(10f, -10f, zPos)).setColor(Color.WHITE, lightStrength)
    val light4 = Light().setPoint(Vec3f(10f, 10f, zPos)).setColor(Color.WHITE, lightStrength)

    lighting.lights.clear()
    lighting.lights.add(light1)
    lighting.lights.add(light2)
    lighting.lights.add(light3)
    lighting.lights.add(light4)

    val nRows = 7
    val nCols = 7
    val spacing = 2.5f

    for (y in 0 until nRows) {
        for (x in 0 until nCols) {
            +colorMesh {
                generator = {
                    color = Color.MD_INDIGO.gamma()
                    sphere {
                        steps = 100
                        center.set((-nCols * 0.5f + x) * spacing, (-nRows * 0.5f + y) * spacing, 0f)
                        radius = 1f
                    }
                }

                val shader = ModeledShader.PbrShader()
                shader.roughness = max(x / (nCols - 1).toFloat(), 0.05f)
                shader.metallic = y / (nRows - 1).toFloat()
                pipelineConfig { shaderLoader = shader::loadShader }
            }
        }
    }
}
