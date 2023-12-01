package de.fabmax.kool.demo.procedural

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomI
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.deferred.DeferredPipeline
import de.fabmax.kool.pipeline.deferred.DeferredPipelineConfig
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.scene.orbitCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.Time

class ProceduralDemo : DemoScene("Procedural Geometry") {
    private val isAutoRotate = mutableStateOf(true)
    private val isReplaceRose = mutableStateOf(true)
    private val seedText = mutableStateOf("")
    private val randomSeedText = mutableStateOf("${randomI()}")
    private lateinit var roses: Roses

    private val ibl by hdriImage("${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png")

    val tableColor by texture2d("${DemoLoader.materialPath}/granitesmooth1/granitesmooth1-albedo4.jpg")
    val tableNormal by texture2d("${DemoLoader.materialPath}/granitesmooth1/granitesmooth1-normal2.jpg")
    val tableRoughness by texture2d("${DemoLoader.materialPath}/granitesmooth1/granitesmooth1-roughness3.jpg")

    override fun Scene.setupMainScene(ctx: KoolContext) {
        orbitCamera {
            setMouseRotation(-20f, -10f)
            setMouseTranslation(0f, 16f, 0f)
            setZoom(40.0)

            onUpdate += {
                if (isAutoRotate.value) {
                    verticalRotation += 5f * Time.deltaT
                }
            }
        }

        lighting.singleDirectionalLight {
            setup(Vec3f(-1f, -0.3f, -1f))
            setColor(MdColor.AMBER.mix(Color.WHITE, 0.5f).toLinear(), 3f)
        }

        val shadowMap = SimpleShadowMap(this@setupMainScene, lighting.lights[0]).apply {
            setDefaultDepthOffset(true)
            shadowBounds = BoundingBoxF(Vec3f(-30f, 0f, -30f), Vec3f(30f, 60f, 30f))
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
                addNode(Glas(ibl, shadowMap).also { onSwap += it })
                addNode(Vase())
                addNode(Table(this@ProceduralDemo))

                roses = Roses()
                addNode(roses)
            }

            lightingPassContent += Skybox.cube(ibl.reflectionMap, 1f, hdrOutput = true)
        }
        shadowMap.drawNode = deferredPipeline.sceneContent

        addNode(deferredPipeline.createDefaultOutputQuad())
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
                        remNd.release()
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
                    roses.children.forEach { it.release() }
                    roses.clearChildren()
                }
        }
        LabeledSwitch("Replace last rose", isReplaceRose)
        LabeledSwitch("Auto rotate view", isAutoRotate)
    }
}