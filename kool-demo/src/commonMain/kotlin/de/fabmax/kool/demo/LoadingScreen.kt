package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.randomF
import de.fabmax.kool.pipeline.shading.unlitShader
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MeshBuilder
import kotlin.math.min

class LoadingScreen(ctx: KoolContext) : Scene("Loading Screen") {

    lateinit var loadingText1: Label
        private set
    lateinit var loadingText2: Label
        private set

    init {
        camera = OrthographicCamera().apply {
            isClipToViewport = true
            near = -1000f
            far = 1000f
        }

        +colorMesh {
            shader = unlitShader { }
            val builder = MeshBuilder(geometry)
            onUpdate += { ev ->
                geometry.clear()
                builder.animateLoading(ev.deltaT)
            }
        }

        +embeddedUi(1f, 1f, null, ctx.screenDpi) {
            isFillViewport = true

            theme = theme {
                componentUi { BlankComponentUi() }
                containerUi { BlankComponentUi() }
            }

            loadingText1 = label("Loading...") {
                layoutSpec.setOrigin(zero(), pcs(15f), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            loadingText2 = label("Blub") {
                layoutSpec.setOrigin(zero(), pcs(15f) - dps(35f), zero())
                layoutSpec.setSize(pcs(100f), dps(35f), full())
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            +loadingText1
            +loadingText2
        }
    }

    private var colors = listOf(
        MdColor.ORANGE, MdColor.CYAN, MdColor.RED, MdColor.GREEN, MdColor.LIME,
        MdColor.BLUE, MdColor.RED, MdColor.DEEP_ORANGE, MdColor.PINK, MdColor.TEAL,
        MdColor.PURPLE, MdColor.AMBER, MdColor.INDIGO, MdColor.DEEP_PURPLE, MdColor.YELLOW,
        MdColor.LIGHT_BLUE, MdColor.LIGHT_GREEN
    )
    private var arcLengths = FloatArray(colors.size) { randomF(10f, 50f) }
    private var arcSpeeds = FloatArray(colors.size) { randomF(90f, 360f) }
    private var arcRadii = FloatArray(colors.size) { randomF(0.2f, 0.3f) }
    private var pos = 0f

    private fun MeshBuilder.animateLoading(dt: Float) {
        val w = mainRenderPass.viewport.width
        val h = mainRenderPass.viewport.height
        val r = min(w, h)

        for (i in colors.indices) {
            color = colors[i]
            lineArc(w * 0.5f, h * 0.55f, r * arcRadii[i], (pos + i) * arcSpeeds[i], arcLengths[i], 15f, 5f)
        }
        pos += dt
    }
}