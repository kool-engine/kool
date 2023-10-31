package de.fabmax.kool.demo.menu

import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.PolyUtil
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class NavSettingsButton(val menu: DemoMenu) : Composable {

    private val isHovered = mutableStateOf(false)
    private val animator = AnimatedFloat(0.25f)

    private val gearPoly = makeGearPoly()

    private fun makeGearPoly(): PolyUtil.TriangulatedPolygon {
        val nTeeth = 8

        val rOut = 1f
        val rIn = 0.75f
        val rHole = 0.4f

        val outside = mutableListOf<Vec3f>()
        val inside = mutableListOf<Vec3f>()

        for (i in 1..nTeeth * 4) {
            val isInner = (i / 2) % 2 != 0
            val aMod = 0.04f * if (i % 2 == 1) 1f else -1f
            val a = (i + 1.5f) / (nTeeth * 4f) * 2f * PI.toFloat() + aMod
            val r = if (isInner) rIn else rOut

            outside += Vec3f(cos(a) * r, sin(a) * r, 0f)
        }
        for (i in 1..12) {
            val a = -2f * i / 12 * PI.toFloat()
            inside += Vec3f(cos(a) * rHole, sin(a) * rHole, 0f)
        }
        return PolyUtil.fillPolygon(outside, listOf(inside))
    }

    override fun UiScope.compose() = Box {
        modifier
            .width(UiSizes.baseSize)
            .height(UiSizes.baseSize)
            .padding(sizes.gap)
            .background(buttonRenderer)
            .onEnter { isHovered.set(true) }
            .onExit { isHovered.set(false) }
            .onClick {
                menu.content.set(DemoMenu.MenuContent.Settings)
                animator.start()
            }

        Tooltip("Settings")
    }

    private val buttonRenderer = UiRenderer { node ->
        node.apply {
            val animationP = animator.progressAndUse()
            val buttonColor = if (isHovered.use()) colors.primary else Color.WHITE
            val bgColor = when {
                isHovered.value -> colors.primaryVariantAlpha(DemoMenu.navBarButtonHoveredAlpha)
                menu.content.value == DemoMenu.MenuContent.Settings -> colors.primaryVariantAlpha(DemoMenu.navBarButtonSelectedAlpha)
                else -> null
            }
            bgColor?.let {
                getUiPrimitives().localRect(0f, 0f, widthPx, heightPx, it)
            }

            getPlainBuilder(UiSurface.LAYER_FLOATING).configured(buttonColor) {
                val r = innerWidthPx * 0.5f

                translate(widthPx / 2f, heightPx / 2f, 0f)
                rotate(45f.deg * animationP, Vec3f.Z_AXIS)

                val i0 = geometry.numVertices
                for (v in gearPoly.vertices) {
                    vertex { set(v.x * r, v.y * r, 0f) }
                }
                for (i in gearPoly.indices) {
                    geometry.addIndex(i0 + i)
                }
            }
        }
    }
}