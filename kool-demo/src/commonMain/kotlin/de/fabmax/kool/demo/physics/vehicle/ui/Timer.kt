package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.round

class Timer(val vehicleUi: VehicleUi) : Composable {
    val trackTime = mutableStateOf(0f)
    val sec1Time = mutableStateOf(0f)
    val sec2Time = mutableStateOf(0f)

    private val isHeadlights = mutableStateOf(vehicleUi.vehicle.isHeadlightsOn).onChange { vehicleUi.vehicle.isHeadlightsOn = it }
    private val isSound = mutableStateOf(false).onChange { vehicleUi.onToggleSound(it) }

    private class TimerBackground(val bgColor: Color) : UiRenderer<UiNode> {
        override fun renderUi(node: UiNode) {
            node.apply {
                val draw = node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                val x0 = round(widthPx * 0f)
                val x1 = round(widthPx * 0.22f)
                val x2 = round(widthPx * 0.3f)
                val x3 = round(widthPx * 0.7f)
                val x4 = round(widthPx * 0.78f)
                val x5 = round(widthPx * 1f)

                val cOut = bgColor.withAlpha(0f)
                val cMid = bgColor.withAlpha(bgColor.a * 0.85f)

                draw.localRectGradient(x0, 0f, x1 - x0, heightPx, cOut, cMid, 1f, 0f)
                draw.localRectGradient(x1, 0f, x2 - x1, heightPx, cMid, bgColor, 1f, 0f)
                draw.localRect(x2, 0f, x3 - x2, heightPx, bgColor)
                draw.localRectGradient(x3, 0f, x4 - x3, heightPx, bgColor, cMid, 1f, 0f)
                draw.localRectGradient(x4, 0f, x5 - x4, heightPx, cMid, cOut, 1f, 0f)
            }
        }
    }

    private fun formatTime(time: Float): String {
        var secs = (time % 60f).toString(2)
        if (secs.length == 4) {
            secs = "0$secs"
        }
        return "${time.toInt() / 60}:$secs"
    }

    override fun UiScope.compose() = Box {
        modifier
            .width(UiSizes.baseSize * 16f)
            .height(UiSizes.baseSize * 1.7f)
            .background(TimerBackground(colors.background))

        val labelColor = MdColor.GREY tone 400

        Row {
            modifier
                .height(Grow.Std)
                .align(AlignmentX.Center, AlignmentY.Center)
                .padding(bottom = sizes.gap * -4f)

            Text(formatTime(trackTime.use())) {
                modifier
                    .height(Grow.Std)
                    .font(sizes.largeText)
                    .baselineMargin(sizes.gap * 1.5f)
            }
            Text("Total") {
                modifier
                    .height(Grow.Std)
                    .margin(start = sizes.smallGap)
                    .baselineMargin(sizes.gap * 1.5f)
                    .textColor(labelColor)
            }

            Column {
                modifier
                    .width(UiSizes.baseSize * 3.25f)
                    .height(Grow.Std)
                    .margin(start = sizes.largeGap, end = sizes.largeGap * 1.5f)
                    .margin(top = sizes.smallGap)
                Row {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                    val t = if (sec1Time.use() == 0f) "-:--.--" else formatTime(sec1Time.value)
                    Text(t) {
                        modifier
                            .alignY(AlignmentY.Center)
                            .width(Grow.Std)
                            .textAlign(AlignmentX.End)
                    }
                    Text("Sec 1") {
                        modifier
                            .alignY(AlignmentY.Center)
                            .margin(start = sizes.gap)
                            .textColor(labelColor)
                    }
                }
                Row {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                        .margin(end = sizes.smallGap)
                    val t = if (sec2Time.use() == 0f) "-:--.--" else formatTime(sec2Time.value)
                    Text(t) {
                        modifier
                            .width(Grow.Std)
                            .height(Grow.Std)
                            .baselineMargin(sizes.gap * 1.5f)
                            .textAlign(AlignmentX.End)
                    }
                    Text("Sec 2") {
                        modifier
                            .margin(start = sizes.gap)
                            .height(Grow.Std)
                            .baselineMargin(sizes.gap * 1.5f)
                            .textColor(labelColor)
                    }
                }
            }
            Column {
                modifier
                    .height(Grow.Std)
                    .margin(top = sizes.smallGap)
                Row {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                        .margin(start = sizes.smallGap)
                    Text("Sound") {
                        modifier
                            .width(Grow.Std)
                            .alignY(AlignmentY.Center)
                            .margin(end = sizes.gap)
                            .textColor(labelColor)
                            .onClick { isSound.toggle() }
                    }
                    Switch(isSound.use()) {
                        modifier
                            .alignY(AlignmentY.Center)
                            .onToggle { isSound.set(it) }
                    }
                }
                Row {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                    Text("Headlights") {
                        modifier
                            .width(Grow.Std)
                            .height(Grow.Std)
                            .margin(end = sizes.gap)
                            .baselineMargin(sizes.gap * 1.5f)
                            .textColor(labelColor)
                            .onClick { isHeadlights.toggle() }
                    }
                    Switch(isHeadlights.use()) {
                        modifier
                            .onToggle { isHeadlights.set(it) }
                            .margin(end = sizes.smallGap, top = sizes.smallGap * 0.5f)
                    }
                }
            }
        }

    }
}