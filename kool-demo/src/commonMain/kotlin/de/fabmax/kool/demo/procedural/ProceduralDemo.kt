package de.fabmax.kool.demo.procedural

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.scene.orbitInputTransform
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.ibl.EnvironmentHelper

fun proceduralDemo(ctx: KoolContext): List<Scene> {
    val demo = ProceduralDemo(ctx)
    return listOf(demo.mainScene)
}

class ProceduralDemo(ctx: KoolContext) {
    val mainScene = makeScene(ctx)

    fun makeScene(ctx: KoolContext) = scene {
        +orbitInputTransform {
            setMouseRotation(-20f, -10f)
            setMouseTranslation(0f, 16f, 0f)
            zoom = 40.0
            +camera

            onUpdate += {
                verticalRotation += 5f * it.deltaT
            }
        }

        lighting.singleLight {
            setDirectional(Vec3f(-1f, -0.3f, -1f))
            setColor(Color.MD_AMBER.mix(Color.WHITE, 0.5f).toLinear(), 3f)
        }

        ctx.assetMgr.launch {
            val ibl = EnvironmentHelper.hdriEnvironment(this@scene, "${Demo.envMapBasePath}/syferfontein_0d_clear_1k.rgbe.png", this)
            +Skybox(ibl.reflectionMap, 1f)

            val shadowMap = SimpleShadowMap(this@scene, 0).apply {
                optimizeForDirectionalLight = true
                shadowBounds = BoundingBox(Vec3f(-30f, 0f, -30f), Vec3f(30f, 60f, 30f))
            }
            val deferredCfg = DeferredPipelineConfig().apply {
                isWithScreenSpaceReflections = true
                isWithAmbientOcclusion = true
                isWithEmissive = true
                maxGlobalLights = 1
                useImageBasedLighting(ibl)
                useShadowMaps(listOf(shadowMap))
            }
            val deferredPipeline = DeferredPipeline(this@scene, deferredCfg).apply {
                aoPipeline?.radius = 0.6f

                contentGroup.apply {
                    +Glas(pbrPass.colorTexture!!, ibl)
                    +Roses()
                    +Vase()
                    +Table()
                }
            }
            shadowMap.drawNode = deferredPipeline.contentGroup
            +deferredPipeline.renderOutput
        }
    }
}