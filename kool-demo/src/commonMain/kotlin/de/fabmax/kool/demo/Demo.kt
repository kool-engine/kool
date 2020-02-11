package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.demo.pbr.pbrDemoScene
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.DebugOverlay
import de.fabmax.kool.util.Position

/**
 * @author fabmax
 */

fun demo(startScene: String? = null) {
    val ctx = createDefaultContext()

    val assetsBaseDir = Demo.getProperty("assetsBaseDir", "")
    if (assetsBaseDir.isNotEmpty()) {
        ctx.assetMgr.assetsBaseDir = assetsBaseDir
    }

    // launch demo
    Demo(ctx, startScene)
}

class Demo(ctx: KoolContext, startScene: String? = null) {

    private val dbgOverlay = DebugOverlay(ctx, Position.LOWER_LEFT)
    private val newScenes = mutableListOf<Scene>()
    private val currentScenes = mutableListOf<Scene>()

    //private val defaultScene = DemoEntry("Simple Demo") { add(simpleShapesScene(it)) }
    private val defaultScene = DemoEntry("PBR/IBL Demo") { addAll(pbrDemoScene(it)) }

    private val demos = mutableMapOf(
            "pbrDemo" to DemoEntry("PBR/IBL Demo") { addAll(pbrDemoScene(it)) }
            //"simplificationDemo" to DemoEntry("Simplification Demo") { addAll(simplificationDemo(it)) }

//            "simpleDemo" to DemoEntry("Simple Demo") { add(uiDemoScene()) },
//            "multiDemo" to DemoEntry("Split Viewport Demo") { addAll(multiScene(it)) },
//            "pointDemo" to DemoEntry("Point Tree Demo") { add(pointScene()) },
//            "synthieDemo" to DemoEntry("Synthie Demo") { addAll(synthieScene(it)) },
//            "globeDemo" to DemoEntry("Globe Demo") { addAll(globeScene(it)) },
//            "modelDemo" to DemoEntry("Model Demo") { add(modelScene(it)) },
//            "treeDemo" to DemoEntry("Tree Demo") { addAll(treeScene(it)) },
//            "simplificationDemo" to DemoEntry("Simplification Demo") { addAll(simplificationDemo(it)) },
//            "instancedDemo" to DemoEntry("Instanced Demo") { add(instancedDemo(it)) },
//            "reflectionDemo" to DemoEntry("Reflection Demo") { addAll(reflectionDemo(it)) },
//            "particleDemo" to DemoEntry("Particle Demo") { addAll(particleDemo(it)) }
    )

    init {
        ctx.scenes += dbgOverlay.ui
        ctx.scenes += demoOverlay(ctx)
        ctx.onRender += this::onRender

        (demos[startScene] ?: defaultScene).loadScene(newScenes, ctx)

        ctx.run()
    }

    private fun onRender(ctx: KoolContext) {
        if (newScenes.isNotEmpty()) {
            currentScenes.forEach { s ->
                ctx.scenes -= s
                s.dispose(ctx)
            }
            currentScenes.clear()

            // new scenes have to be inserted in front, so that demo menu is rendered after it
            newScenes.forEachIndexed { i, s ->
                ctx.scenes.add(i, s)
                currentScenes.add(s)
            }
            newScenes.clear()
        }
    }

    private fun demoOverlay(ctx: KoolContext): Scene = uiScene(ctx.screenDpi, "demo-overlay") {
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi(::BlurredComponentUi)
        }
        content.ui.setCustom(BlankComponentUi())

        +drawerMenu("menu", "Demos") {
            // no nice layouting functions yet, choose start y such that menu items start somewhere below the title
            // negative value means it's measured from top

            +container("demos") {
                ui.setCustom(BlankComponentUi())
                layoutSpec.setOrigin(zero(), dps(45f, true), zero())
                layoutSpec.setSize(full(), pcs(100f, true) - dps(110f, true), full())

                //+ScrollHandler(this)

                var y = -30f
                for (demo in demos) {
                    +button(demo.key) {
                        layoutSpec.setOrigin(zero(), dps(y, true), zero())
                        layoutSpec.setSize(pcs(100f, true), dps(30f, true), full())
                        textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                        text = demo.value.label
                        y -= 35f

                        onClick += { _,_,_ ->
                            demo.value.loadScene.invoke(newScenes, ctx)
                            isOpen = false
                        }
                    }
                }
            }

            +toggleButton("showDbg") {
                layoutSpec.setOrigin(zero(), dps(10f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), full())
                text = "Debug Info"
                isEnabled = dbgOverlay.ui.isVisible

                onClick += { _,_,_ -> dbgOverlay.ui.isVisible = isEnabled }
            }

            onPreRender += {
                dbgOverlay.xOffset = animationPos * width
            }
        }
    }

    private class DemoEntry(val label: String, val loadScene: MutableList<Scene>.(KoolContext) -> Unit)

    companion object {
        val demoProps = mutableMapOf<String, Any>()

        fun setProperty(key: String, value: Any) {
            demoProps[key] = value
        }

        inline fun <reified T> getProperty(key: String, default: T): T {
            return demoProps[key] as? T ?: default
        }
    }
}
