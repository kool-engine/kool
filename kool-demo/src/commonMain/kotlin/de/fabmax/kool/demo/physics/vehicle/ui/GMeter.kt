package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color


class GMeter(val dashboard: Dashboard) : Composable {

    private val bgColor = Color.WHITE.withAlpha(0.2f)
    private val gridColor = Color.WHITE.withAlpha(0.1f)

    private val meterRenderer = UiRenderer {
        it.apply {
            val draw = getUiPrimitives()
            val r = innerWidthPx * 0.5f
            val center = Vec2f(widthPx * 0.5f, heightPx * 0.5f)
            val rDot = sizes.smallGap.px * 1.3f
            val rCircle = r - rDot

            // grid
            draw.localRect(center.x - 1.dp.px, center.y - rCircle, 2.dp.px, 2f * rCircle, gridColor)
            draw.localRect(center.x - rCircle, center.y - 1.dp.px, 2f * rCircle, 2.dp.px, gridColor)

            // background
            val rStep = rCircle * 0.333f
            var rSection = rCircle
            for (i in 1..3) {
                draw.localCircle(center.x, center.y, rSection, bgColor)
                rSection -= rStep
            }

            // dot
            val accX = (dashboard.lateralAcceleration.value / 15f).clamp(-1f, 1f)
            val accY = (dashboard.longitudinalAcceleration.value / 15f).clamp(-1f, 1f)
            val a = MutableVec2f(accX, accY)
            if (a.length() > 1f) {
                a.norm()
            }
            a.mul(rCircle).add(center)
            draw.localCircle(a.x, a.y, rDot, colors.primary)
        }
    }

    override fun UiScope.compose() = Box {
        dashboard.lateralAcceleration.use()
        dashboard.longitudinalAcceleration.use()
        modifier
            .height(UiSizes.baseSize * 3f)
            .width(UiSizes.baseSize * 3f)
            .alignY(AlignmentY.Center)
            .margin(start = sizes.largeGap)
            .background(meterRenderer)
    }
}