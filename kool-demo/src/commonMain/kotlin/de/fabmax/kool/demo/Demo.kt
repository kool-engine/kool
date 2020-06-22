package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.createDefaultContext
import de.fabmax.kool.demo.pbr.pbrDemoScene
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.DebugOverlay

/**
 * @author fabmax
 */

fun demo(startScene: String? = null, ctx: KoolContext = createDefaultContext()) {
    val assetsBaseDir = Demo.getProperty("assetsBaseDir", "")
    if (assetsBaseDir.isNotEmpty()) {
        ctx.assetMgr.assetsBaseDir = assetsBaseDir
    }

    // launch demo
    Demo(ctx, startScene)
}

class Demo(ctx: KoolContext, startScene: String? = null) {

    private val dbgOverlay = DebugOverlay(ctx, DebugOverlay.Position.LOWER_RIGHT)
    private val newScenes = mutableListOf<Scene>()
    private val currentScenes = mutableListOf<Scene>()

    private val defaultScene = DemoEntry("PBR / IBL") { addAll(pbrDemoScene(it)) }

    private val demos = mutableMapOf(
            "deferredDemo" to DemoEntry("Deferred Shading") { addAll(deferredScene(it)) },
            "gltfDemo" to DemoEntry("glTF Models") { addAll(gltfDemo(it)) },
            "pbrDemo" to DemoEntry("PBR Materials") { addAll(pbrDemoScene(it)) },
            "aoDemo" to DemoEntry("Ambient Occlusion") { addAll(aoDemo(it)) },
            "multiShadowDemo" to DemoEntry("Multi Shadow") { addAll(multiLightDemo(it)) },
            "treeDemo" to DemoEntry("Procedural Tree") { addAll(treeScene(it)) },
            "simplificationDemo" to DemoEntry("Simplification") { addAll(simplificationDemo(it)) },
            "instanceDemo" to DemoEntry("Instanced Drawing") { addAll(instanceDemo(it)) },
            "helloWorldDemo" to DemoEntry("Hello World", true) { add(helloWorldScene()) }
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
            +container("demos") {
                ui.setCustom(BlankComponentUi())
                layoutSpec.setOrigin(zero(), dps(45f, true), zero())
                layoutSpec.setSize(full(), pcs(100f, true) - dps(110f, true), full())

                //+ScrollHandler(this)

                var y = -30f
                demos.values.filter { !it.isHidden }.forEach { demo ->
                    +button(demo.label) {
                        layoutSpec.setOrigin(zero(), dps(y, true), zero())
                        layoutSpec.setSize(pcs(100f, true), dps(30f, true), full())
                        textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                        y -= 35f

                        onClick += { _,_,_ ->
                            demo.loadScene.invoke(newScenes, ctx)
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
        }
    }

    private class DemoEntry(val label: String, val isHidden: Boolean = false, val loadScene: MutableList<Scene>.(KoolContext) -> Unit)

    companion object {
        val demoProps = mutableMapOf<String, Any>()

        val envMapBasePath: String
            get() = getProperty("pbrDemo.envMaps", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/hdri")

        val pbrBasePath: String
            get() = getProperty("pbrDemo.materials", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/materials")

        val modelBasePath: String
            get() = getProperty("pbrDemo.models", "https://fabmax-kool-pbr.s3.eu-central-1.amazonaws.com/models")

        fun setProperty(key: String, value: Any) {
            demoProps[key] = value
        }

        inline fun <reified T> getProperty(key: String, default: T): T {
            return demoProps[key] as? T ?: default
        }
    }
}

class Cycler<T>(elements: List<T>) : List<T> by elements {

    constructor(vararg elements: T) : this(listOf(*elements))

    var index = 0

    val current: T
        get() = get(index)

    fun next(): T {
        index = (index + 1) % size
        return current
    }

    fun prev(): T {
        index = (index + size - 1 + size) % size
        return current
    }
}
