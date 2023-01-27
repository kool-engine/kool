package de.fabmax.kool.demo.procedural

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomI
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.pipeline.deferred.DeferredPipelineConfig
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.scene.orbitInputTransform
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.Time

class ProceduralDemo : DemoScene("Procedural Geometry") {
    val isAutoRotate = mutableStateOf(true)
    val isReplaceRose = mutableStateOf(true)
    val seedText = mutableStateOf("")
    val randomSeedText = mutableStateOf("${randomI()}")
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
                if (isAutoRotate.value) {
                    verticalRotation += 5f * Time.deltaT
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

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuRow {
            Text("Seed") { labelStyle() }
            TextField(seedText.use()) {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .alignY(AlignmentY.Center)
                    .hint(randomSeedText.use())
                    .onChange { seedText.set(it) }
            }
        }
        Button("Generate rose") {
            modifier
                .alignX(AlignmentX.Center)
                .width(Grow.Std)
                .margin(horizontal = 16.dp, vertical = 24.dp)
                .onClick {
                    if (roses.children.isNotEmpty() && isReplaceRose.value) {
                        val remNd = roses.children.last()
                        roses.removeNode(remNd)
                        remNd.dispose(ctx)
                    }
                    val seed = if (seedText.value.isNotEmpty()) seedText.value else randomSeedText.value
                    randomSeedText.set("${randomI()}")
                    roses.makeRose(seed.hashCode())
                }
        }
        Button("Empty vase") {
            modifier
                .alignX(AlignmentX.Center)
                .width(Grow.Std)
                .margin(horizontal = 16.dp, vertical = 24.dp)
                .onClick {
                    roses.children.forEach { it.dispose(ctx) }
                    roses.removeAllChildren()
                }
        }
        LabeledSwitch("Replace last rose", isReplaceRose)
        LabeledSwitch("Auto rotate view", isAutoRotate)
    }
}