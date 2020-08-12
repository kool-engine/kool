package de.fabmax.kool.demo.procedural

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomI
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.scene.orbitInputTransform
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.deferred.DeferredPipeline
import de.fabmax.kool.util.deferred.DeferredPipelineConfig
import de.fabmax.kool.util.ibl.EnvironmentHelper

fun proceduralDemo(ctx: KoolContext): List<Scene> {
    val demo = ProceduralDemo(ctx)
    return listOf(demo.mainScene, demo.menu)
}

class ProceduralDemo(ctx: KoolContext) {
    val mainScene = makeScene(ctx)
    val menu = makeMenu(ctx)

    var autoRotate = true
    lateinit var roses: Roses

    fun makeScene(ctx: KoolContext) = scene {
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
                    +Vase()
                    +Table()

                    roses = Roses()
                    +roses
                }
            }
            shadowMap.drawNode = deferredPipeline.contentGroup
            +deferredPipeline.renderOutput
        }
    }

    fun makeMenu(ctx: KoolContext) = uiScene {
        val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
        val smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu container") {
            ui.setCustom(SimpleComponentUi(this))
            layoutSpec.setOrigin(dps(-370f), dps(-390f), zero())
            layoutSpec.setSize(dps(250f), dps(270f), full())

            var y = -40f
            +label("Roses") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +button("Empty Vase") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                onClick += { _, _, ctx ->
                    roses.children.forEach { it.dispose(ctx) }
                    roses.removeAllChildren()
                }
            }
            y -= 35f
            var seedTxt: Label? = null
            var replaceLastRose: ToggleButton? = null
            +button("Generate Rose") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                onClick += { _, _, ctx ->
                    if (roses.children.isNotEmpty() && replaceLastRose?.isEnabled == true) {
                        val remNd = roses.children.last()
                        roses.removeNode(remNd)
                        remNd.dispose(ctx)
                    }

                    val seed = randomI()
                    seedTxt?.text = "$seed"
                    roses.makeRose(seed)
                }
            }
            y -= 35f
            replaceLastRose = toggleButton("Replace Last Rose") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = true
            }
            +replaceLastRose
            y -= 35f
            +label("Seed:") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
            }
            seedTxt = label("0") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            +seedTxt

            y -= 40f
            +label("Scene") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            y -= 35f
            +toggleButton("Auto Rotate") {
                layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = autoRotate
                onStateChange += {
                    autoRotate = isEnabled
                }
            }
        }
    }
}