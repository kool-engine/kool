package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.round
import kotlin.math.roundToInt

class Dashboard : Composable {
    val speedKph = mutableStateOf(0f)
    val torqueNm = mutableStateOf(0f)
    val powerKW = mutableStateOf(0f)
    val gear = mutableStateOf(0)
    val rpm = mutableStateOf(0f)
    val maxRpm = mutableStateOf(6000f)
    val criticalRpm = mutableStateOf(5000f)
    val throttle = mutableStateOf(0f)
    val brake = mutableStateOf(0f)
    val steering = mutableStateOf(0f)
    val lateralAcceleration = mutableStateOf(0f)
    val longitudinalAcceleration = mutableStateOf(0f)

    private val rpmScale = RpmScale(this)
    private val gMeter = GMeter(this)
    private val labelColor = MdColor.GREY tone 400

    private class DashboardBackground(val bgColor: Color) : UiRenderer<UiNode> {
        override fun renderUi(node: UiNode) {
            node.apply {
                val draw = node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                val x1 = round(widthPx * 0.6f)
                val x2 = round(widthPx * 0.7f)
                val x3 = round(widthPx * 1f)
                val c2 = bgColor.withAlpha(bgColor.a * 0.85f)
                val c3 = bgColor.withAlpha(0f)

                draw.localRect(0f, 0f, x1, heightPx, bgColor)
                draw.localRectGradient(x1, 0f, x2 - x1, heightPx, bgColor, c2, 1f, 0f)
                draw.localRectGradient(x2, 0f, x3 - x2, heightPx, c2, c3, 1f, 0f)
            }
        }
    }

    override fun UiScope.compose() = Row {
        modifier
            .width(UiSizes.baseSize * 12.5f)
            .height(UiSizes.baseSize * 3.5f)
            .background(DashboardBackground(colors.background))

        Column {
            modifier
                .alignY(AlignmentY.Bottom)

            rpmScale()
            numericValues()
        }

        gMeter()
    }

    private fun UiScope.numericValues() = Row {
        Text("${speedKph.use().roundToInt()}") {
            modifier
                .width(UiSizes.baseSize * 4.5f)
                .height(UiSizes.baseSize * 2.5f)
                .alignY(AlignmentY.Bottom)
                .font(VehicleUi.getUiFonts(sizes.normalText.sizePts).speedFont)
                .baselineMargin(sizes.largeGap)
                .textAlignX(AlignmentX.End)
        }
        Text("km/h") {
            modifier
                .alignY(AlignmentY.Bottom)
                .height(UiSizes.baseSize)
                .margin(start = sizes.smallGap)
                .baselineMargin(sizes.largeGap)
                .textColor(labelColor)
        }

        Column {
            modifier
                .width(UiSizes.baseSize)
                .margin(start = sizes.largeGap, bottom = sizes.largeGap)
                .alignY(AlignmentY.Bottom)
            Text(gearName(gear.use())) {
                modifier
                    .baselineMargin(0.dp)
                    .alignX(AlignmentX.End)
            }
            Text("${torqueNm.use().toInt()}") {
                modifier
                    .baselineMargin(0.dp)
                    .padding(end = sizes.smallGap * 1f)
                    .alignX(AlignmentX.End)
            }
            Text("${powerKW.use().toInt()}") {
                modifier
                    .baselineMargin(0.dp)
                    .padding(end = sizes.smallGap * 2f)
                    .alignX(AlignmentX.End)
            }
        }
        Column {
            modifier
                .width(UiSizes.baseSize * 1.5f)
                .margin(bottom = sizes.largeGap)
                .alignY(AlignmentY.Bottom)
            Text("Gear") {
                modifier
                    .baselineMargin(0.dp)
                    .padding(start = sizes.smallGap * 2f)
                    .textColor(labelColor)
            }
            Text("Nm") {
                modifier
                    .baselineMargin(0.dp)
                    .padding(start = sizes.smallGap * 1f)
                    .textColor(labelColor)
            }
            Text("kW") {
                modifier
                    .baselineMargin(0.dp)
                    .textColor(labelColor)
            }
        }
    }

    private fun gearName(gear: Int): String {
        return when {
            gear < 0 -> "Rev"
            gear == 0 -> "N"
            gear == 1 -> "1st"
            gear == 2 -> "2nd"
            gear == 3 -> "3rd"
            else -> "${gear}th"
        }
    }
}