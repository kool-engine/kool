package de.fabmax.kool.demo.menu

import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class DrawerButton(val menu: DemoMenu) : Composable {

    private val isHovered = mutableStateOf(false)
    private val animator = AnimatedFloat(0.25f)

    override fun UiScope.compose() = Box {
        modifier
            .width(UiSizes.baseSize)
            .height(UiSizes.baseSize)
            .padding(sizes.smallGap)
            .background(buttonRenderer)
            .onEnter { isHovered.set(true) }
            .onExit { isHovered.set(false) }
            .onClick {
                menu.isExpanded = !menu.isExpanded
                animator.start()
            }

        Tooltip("Toggle demo menu")
    }

    private val buttonRenderer = UiRenderer { node ->
        node.apply {
            val buttonColor = if (isHovered.use()) colors.primary else Color.WHITE
            val p = animator.progressAndUse()
            val animationP = if (menu.isExpanded) p else 1f - p

            if (menu.isExpanded && isHovered.value) {
                val bgColor = colors.primaryVariantAlpha(DemoMenu.navBarButtonHoveredAlpha)
                getUiPrimitives().localRect(0f, 0f, widthPx, heightPx, bgColor)
            }

            getPlainBuilder(UiSurface.LAYER_FLOATING).configured(buttonColor) {
                val hw = innerWidthPx * 0.5f
                val hh = innerWidthPx * 0.18f
                val hx = -hw / 2f
                val ph = 2.5f.dp.px

                val tx = animationP * -hw * 0.1f
                val w = hw - animationP * hw * 0.4f

                translate(widthPx / 2f, heightPx / 2f, 0f)
                rotate((180f - 180f * animationP).deg, Vec3f.Z_AXIS)

                withTransform {
                    translate(tx, hh, 0f)
                    rotate(45f.deg * animationP, Vec3f.Z_AXIS)
                    rect {
                        isCenteredOrigin = false
                        origin.set(hx, -ph / 2f, 0f)
                        size.set(w, ph)
                        zeroTexCoords()
                    }
                }
                rect {
                    isCenteredOrigin = false
                    origin.set(hx, -ph / 2f, 0f)
                    size.set(hw, ph)
                }
                withTransform {
                    translate(tx, -hh, 0f)
                    rotate((-45f).deg * animationP, Vec3f.Z_AXIS)
                    rect {
                        isCenteredOrigin = false
                        origin.set(hx, -ph / 2f, 0f)
                        size.set(w, ph)
                    }
                }
            }
        }
    }
}