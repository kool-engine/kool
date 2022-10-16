package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import kotlin.math.min

class RpmScale(val dashboard: Dashboard) : Composable {

    private val rpm: Float
        get() = dashboard.rpm.value
    private val maxRpm: Float
        get() = dashboard.maxRpm.value
    private val critRpm: Float
        get() = dashboard.criticalRpm.value

    private val trackColor = ColorGradient(Color.WHITE.withAlpha(0.7f), MdColor.ORANGE.withAlpha(0.7f))
    private val colorCrit = MdColor.RED
    private val colorCritBg = colorCrit.withAlpha(0.4f)

    private fun rpmX(rpm: Float, innerWidth: Float) = rpm / maxRpm * innerWidth

    private val rpmRenderer = UiRenderer {
        it.apply {
            val indent = sizes.gap.px * 0.5f
            getPlainBuilder().configured {
                translate(paddingStartPx, paddingTopPx, 0f)

                val iw = innerWidthPx - indent

                // track
                fillTrack(rpmX(critRpm, iw), rpmX(maxRpm, iw), innerHeightPx, indent, colorCritBg, colorCritBg)
                val nrmRpm = min(rpm, critRpm)
                fillTrack(rpmX(0f, iw), rpmX(nrmRpm, iw), innerHeightPx, indent, trackColor.getColor(0f), trackColor.getColor(rpm / critRpm))
                if (rpm > critRpm) {
                    fillTrack(rpmX(critRpm, iw), rpmX(rpm, iw), innerHeightPx, indent, colorCrit, colorCrit)
                }

                // border
                color = Color.WHITE.withAlpha(0.75f)
                line(indent, 0f, innerWidthPx, 0f, 1.dp.px)
                line(0f, innerHeightPx, innerWidthPx - indent, innerHeightPx, 1.dp.px)
                line(0f, innerHeightPx, indent, 0f, 1.dp.px)
                line(innerWidthPx - indent, innerHeightPx, innerWidthPx, 0f, 1.dp.px)

                // grid
                color = Color.WHITE.withAlpha(0.3f)
                val step = (innerWidthPx - indent) / 6f
                var x = step
                for (i in 1..4) {
                    line(x, innerHeightPx, x + indent, 0f, 1.dp.px)
                    x += step
                }
            }
        }
    }

    private fun MeshBuilder.fillTrack(fromX: Float, toX: Float, innerHeight: Float, indent: Float, colorLt: Color, colorRt: Color) {
        color = colorLt
        val i1 = vertex { set(fromX + indent, 0f, 0f) }
        val i2 = vertex { set(fromX, innerHeight, 0f) }
        color = colorRt
        val i3 = vertex { set(toX, innerHeight, 0f) }
        val i4 = vertex { set(toX + indent, 0f, 0f) }

        addTriIndices(i1, i2, i3)
        addTriIndices(i1, i3, i4)
    }

    override fun UiScope.compose() = Box {
        modifier
            .width(Grow.Std)
            .height(sizes.largeGap * 1.25f)
            .margin(start = sizes.gap)
            .padding(2.dp)
            .background(rpmRenderer)
    }
}