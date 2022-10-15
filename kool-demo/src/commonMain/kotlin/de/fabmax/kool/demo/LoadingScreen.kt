package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.shading.unlitShader
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import kotlin.math.min

class LoadingScreen(val ctx: KoolContext) : Scene("Loading Screen") {

    val loadingText1 = mutableStateOf("")
    val loadingText2 = mutableStateOf("")

    private val ui: UiSurface

    init {
        setupUiScene(true)

        +colorMesh {
            shader = unlitShader { }
            val builder = MeshBuilder(geometry)
            onUpdate += {
                geometry.clear()
                builder.animateLoading(Time.deltaT)
            }
        }

        ui = Panel(sizes = Sizes.large) {
            modifier
                .size(Grow.Std, Grow.Std)
                .alignX(AlignmentX.Center)
                .layout(ColumnLayout)
                .background(null)
            Box {
                modifier.height(Grow(0.83f))
            }
            Box {
                modifier
                    .size(Grow.Std, Grow(0.17f))
                    .alignX(AlignmentX.Center)
                    .layout(ColumnLayout)

                Text(loadingText1.use()) {
                    modifier
                        .width(Grow.Std)
                        .textAlignX(AlignmentX.Center)
                        .textColor(Color.WHITE)
                        .margin(vertical = sizes.gap)
                        .font(sizes.largeText)
                }
                Text(loadingText2.use()) {
                    modifier
                        .width(Grow.Std)
                        .textAlignX(AlignmentX.Center)
                        .textColor(Color.WHITE)
                        .margin(vertical = sizes.gap)
                        .font(sizes.largeText)
                }
            }
        }
        +ui
        ui.isInputEnabled = false
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
        val h = -mainRenderPass.viewport.height
        val r = min(w, h)

        for (i in colors.indices) {
            color = colors[i]
            lineArc(w * 0.5f, h * 0.45f, r * arcRadii[i], (pos + i) * arcSpeeds[i], arcLengths[i], 15f, 5f)
        }
        pos += dt
    }
}