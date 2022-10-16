package de.fabmax.kool.demo.menu

import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class NavDemoButton(val menu: DemoMenu) : Composable {

    private val isHovered = mutableStateOf(false)
    private val animator = AnimationState(0.25f)

    override fun UiScope.compose() = Box {
        modifier
            .width(UiSizes.baseSize)
            .height(UiSizes.baseSize)
            .padding(sizes.gap)
            .background(buttonRenderer)
            .onEnter { isHovered.set(true) }
            .onExit { isHovered.set(false) }
            .onClick {
                menu.content.set(DemoMenu.MenuContent.Demos)
                animator.start()
            }

        Tooltip("Available demos")
    }

    private val buttonRenderer = UiRenderer { node ->
        node.apply {
            val animationP = animator.progressAndUse()
            val buttonColor = if (isHovered.use()) colors.primary else Color.WHITE
            val bgColor = when {
                isHovered.value -> colors.primaryVariant.withAlpha(DemoMenu.navBarButtonHoveredAlpha)
                menu.content.value == DemoMenu.MenuContent.Demos -> colors.primaryVariant.withAlpha(DemoMenu.navBarButtonSelectedAlpha)
                else -> colors.primaryVariant.withAlpha(DemoMenu.navBarButtonAlpha)
            }
            getUiPrimitives().localRect(0f, 0f, widthPx, heightPx, bgColor)

            getPlainBuilder(UiSurface.LAYER_FLOATING).configured(buttonColor) {
                val r = innerWidthPx * 0.45f

                translate((widthPx - r * 0.4f) / 2f, heightPx / 2f, 0f)
                rotate(animationP * 120f, Vec3f.Z_AXIS)

                val i0 = vertex { set(r, 0f, 0f) }
                val i1 = vertex { set(r * -0.5f, r * 0.866f, 0f) }
                val i2 = vertex { set(r * -0.5f, r * -0.866f, 0f) }
                addTriIndices(i0, i1, i2)
            }
        }
    }
}