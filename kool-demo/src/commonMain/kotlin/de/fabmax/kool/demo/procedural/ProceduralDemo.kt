package de.fabmax.kool.demo.procedural

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.controlUi
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomI
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.scene.orbitInputTransform
import de.fabmax.kool.scene.ui.Label
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.ibl.EnvironmentHelper

class ProceduralDemo : DemoScene("Procedural Geometry") {
    var autoRotate = true
    lateinit var roses: Roses

    override fun Scene.setupMainScene(ctx: KoolContext) {
        +orbitInputTransform {
            setMouseRotation(-20f, -10f)
            setMouseTranslation(0f, 16f, 0f)
            zoom = 40.0
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

        ctx.assetMgr.launch {
            val ibl = EnvironmentHelper.hdriEnvironment(this@setupMainScene, "${Demo.envMapBasePath}/syferfontein_0d_clear_1k.rgbe.png", this)

            val shadowMap = SimpleShadowMap(this@setupMainScene, 0).apply {
                setDefaultDepthOffset(true)
                shadowBounds = BoundingBox(Vec3f(-30f, 0f, -30f), Vec3f(30f, 60f, 30f))
            }
            val deferredCfg = DeferredPipelineConfig().apply {
                isWithScreenSpaceReflections = true
                isWithAmbientOcclusion = true
                isWithExtendedMaterials = true
                maxGlobalLights = 1
                isWithVignette = true
                isWithBloom = true
                useImageBasedLighting(ibl)
                useShadowMaps(listOf(shadowMap))
                outputDepthTest = DepthCompareOp.ALWAYS
            }
            val deferredPipeline = DeferredPipeline(this@setupMainScene, deferredCfg).apply {
                aoPipeline?.radius = 0.6f
                outputShader.setupVignette(strength = 0f)

                contentGroup.apply {
                    +Glas(pbrPass.colorTexture!!, ibl)
                    +Vase()
                    +Table()

                    roses = Roses()
                    +roses
                }

                pbrPass.content.apply {
                    +Skybox.cube(ibl.reflectionMap, 1f, hdrOutput = true)
                }
            }
            shadowMap.drawNode = deferredPipeline.contentGroup
            +deferredPipeline.renderOutput
        }
    }

    override fun setupMenu(ctx: KoolContext) = controlUi(ctx) {
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