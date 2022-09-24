package de.fabmax.kool.demo.menu

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class DrawerButton(val menu: DemoMenu) : ComposableComponent {

    private val isHovered = mutableStateOf(false)
    private val animator = AnimationState(0.25f)
    private val tooltipState = MutableTooltipState()

    override fun UiScope.compose() {
        Box {
            modifier
                .width(DemoMenu.itemSize.dp)
                .height(DemoMenu.itemSize.dp)
                .padding(4.dp)
                .background(buttonRenderer)
                .onEnter { isHovered.set(true) }
                .onExit { isHovered.set(false) }
                .onClick {
                    menu.isExpanded = !menu.isExpanded
                    animator.start()
                }

            Tooltip(tooltipState, "Toggle demo menu")
        }
    }

    private val buttonRenderer = UiRenderer { node ->
        node.apply {
            val buttonColor = if (isHovered.use()) colors.accent else Color.WHITE
            val p = animator.progressAndUse()
            val animationP = if (menu.isExpanded) p else 1f - p

            if (menu.isExpanded && isHovered.value) {
                val bgColor = colors.accentVariant.withAlpha(DemoMenu.navBarButtonHoveredAlpha)
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
                rotate(180f - animationP * 180f, Vec3f.Z_AXIS)

                withTransform {
                    translate(tx, hh, 0f)
                    rotate(animationP * 45f, Vec3f.Z_AXIS)
                    rect {
                        origin.set(hx, -ph / 2f, 0f)
                        size.set(w, ph)
                        zeroTexCoords()
                    }
                }
                rect {
                    origin.set(hx, -ph / 2f, 0f)
                    size.set(hw, ph)
                }
                withTransform {
                    translate(tx, -hh, 0f)
                    rotate(animationP * -45f, Vec3f.Z_AXIS)
                    rect {
                        origin.set(hx, -ph / 2f, 0f)
                        size.set(w, ph)
                    }
                }
            }
        }
    }
}