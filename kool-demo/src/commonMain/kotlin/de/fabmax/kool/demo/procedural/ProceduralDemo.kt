package de.fabmax.kool.demo.procedural

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomI
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.pipeline.deferred.DeferredPipelineConfig
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.scene.orbitInputTransform
import de.fabmax.kool.scene.ui.Label
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.SimpleShadowMap

class ProceduralDemo : DemoScene("Procedural Geometry") {
    var autoRotate = true
    lateinit var roses: Roses

    lateinit var ibl: EnvironmentMaps

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png", this)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        +orbitInputTransform {
            setMouseRotation(-20f, -10f)
            setMouseTranslation(0f, 16f, 0f)
            setZoom(40.0)
            +camera

            onUpdate += {
                if (autoRotate) {
                    verticalRotation += 5f * it.deltaT
                }
            }
        }

        lighting.singleLight {
            setDirectional(Vec3f(-1f, -0.3f, -1f))
            setColor(MdColor.AMBER.mix(Color.WHITE, 0.5f).toLinear(), 3f)
        }

        val shadowMap = SimpleShadowMap(this@setupMainScene, 0).apply {
            setDefaultDepthOffset(true)
            shadowBounds = BoundingBox(Vec3f(-30f, 0f, -30f), Vec3f(30f, 60f, 30f))
        }
        val deferredCfg = DeferredPipelineConfig().apply {
            isWithScreenSpaceReflections = true
            isWithAmbientOcclusion = true
            maxGlobalLights = 1
            isWithVignette = true
            isWithBloom = true
            useImageBasedLighting(ibl)
            useShadowMaps(listOf(shadowMap))
            outputDepthTest = DepthCompareOp.ALWAYS
        }
        val deferredPipeline = DeferredPipeline(this@setupMainScene, deferredCfg).apply {
            aoPipeline?.radius = 0.6f

            sceneContent.apply {
                +Glas(ibl).also { onSwap += it }
                +Vase()
                +Table()

                roses = Roses()
                +roses
            }

            lightingPassContent += Skybox.cube(ibl.reflectionMap, 1f, hdrOutput = true)
        }
        shadowMap.drawNode = deferredPipeline.sceneContent
        +deferredPipeline.createDefaultOutputQuad()
    }

    override fun setupMenu(ctx: KoolContext) = controlUi {
        section("Roses") {
            button("Empty Vase") {
                roses.children.forEach { it.dispose(ctx) }
                roses.removeAllChildren()
            }
            var seedTxt: Label? = null
            var replaceLastRose = true
            button("Generate Rose") {
                if (roses.children.isNotEmpty() && replaceLastRose) {
                    val remNd = roses.children.last()
                    roses.removeNode(remNd)
                    remNd.dispose(ctx)
                }

                val seed = randomI()
                seedTxt?.text = "$seed"
                roses.makeRose(seed)
            }
            toggleButton("Replace Last Rose", replaceLastRose) { replaceLastRose = isEnabled }
            seedTxt = textWithValue("Seed:", "")
        }
        section("Scene") {
            toggleButton("Auto Rotate", autoRotate) { autoRotate = isEnabled }
        }
    }
}