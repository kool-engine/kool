package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.demo.UiSizes
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.round
import kotlin.math.roundToInt

class Dashboard : ComposableComponent {
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

    override fun UiScope.compose() = Box {
        modifier
            .align(AlignmentX.Start, AlignmentY.Bottom)
            .width(UiSizes.baseSize * 12f)
            .height(UiSizes.baseSize * 3.5f)
            .background(DashboardBackground(colors.background))
            .layout(RowLayout)

//        Box {
//            modifier.width(200.dp)
//        }


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
                .font(sizes.largeText.copy(sizePts = sizes.largeText.sizePts * 1.7f))
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


//        +container("dashboard") {
//            layoutSpec.setOrigin(zero(), zero(), zero())
//            layoutSpec.setSize(dps(720f * scale), dps(170f * scale), full())
//            ui.setCustom(DashboardComponentUi(this))
//
//            +RpmScale(this@uiScene).apply {
//                rpmBar = this
//                layoutSpec.setOrigin(dps(10f  * scale), dps(15f * scale), zero())
//                layoutSpec.setSize(dps(400f * scale), dps(140f * scale), full())
//                maxRpm = 6000f
//            }
//
//            +label("speedValue") {
//                speedValue = this
//                layoutSpec.setOrigin(dps(75f  * scale), dps(15f * scale), zero())
//                layoutSpec.setSize(dps(170f * scale), dps(90f * scale), full())
//                font.setCustom(largeFont)
//                textAlignment = Gravity(Alignment.END, Alignment.END)
//                text = "0"
//            }
//            +label("speedUnit") {
//                layoutSpec.setOrigin(dps(220f  * scale), dps(15f * scale), zero())
//                layoutSpec.setSize(dps(60f * scale), dps(35f * scale), full())
//                font.setCustom(smallFont)
//                textAlignment = Gravity(Alignment.START, Alignment.END)
//                text = "km/h"
//            }
//
//            +label("gearValue") {
//                gearValue = this
//                layoutSpec.setOrigin(dps(274f  * scale), dps(65f * scale), zero())
//                layoutSpec.setSize(dps(75f * scale), dps(35f * scale), full())
//                font.setCustom(smallFont)
//                textAlignment = Gravity(Alignment.END, Alignment.END)
//                text = "N"
//            }
//            +label("gearLabel") {
//                layoutSpec.setOrigin(dps(324f  * scale), dps(65f * scale), zero())
//                layoutSpec.setSize(dps(175f * scale), dps(35f * scale), full())
//                font.setCustom(smallFont)
//                textAlignment = Gravity(Alignment.START, Alignment.END)
//                text = "Gear"
//            }
//
//            +label("torqueValue") {
//                torqueValue = this
//                layoutSpec.setOrigin(dps(274f  * scale), dps(40f * scale), zero())
//                layoutSpec.setSize(dps(74.5f * scale), dps(35f * scale), full())
//                font.setCustom(smallFont)
//                textAlignment = Gravity(Alignment.END, Alignment.END)
//                text = "0"
//            }
//            +label("torqueUnit") {
//                layoutSpec.setOrigin(dps(324f  * scale), dps(40f * scale), zero())
//                layoutSpec.setSize(dps(174.5f * scale), dps(35f * scale), full())
//                font.setCustom(smallFont)
//                textAlignment = Gravity(Alignment.START, Alignment.END)
//                text = "Nm"
//            }
//
//            +label("powerValue") {
//                powerValue = this
//                layoutSpec.setOrigin(dps(274f  * scale), dps(15f * scale), zero())
//                layoutSpec.setSize(dps(74f * scale), dps(35f * scale), full())
//                font.setCustom(smallFont)
//                textAlignment = Gravity(Alignment.END, Alignment.END)
//                text = "0"
//            }
//            +label("powerUnit") {
//                layoutSpec.setOrigin(dps(324f  * scale), dps(15f * scale), zero())
//                layoutSpec.setSize(dps(174f * scale), dps(35f * scale), full())
//                font.setCustom(smallFont)
//                textAlignment = Gravity(Alignment.START, Alignment.END)
//                text = "kW"
//            }
//
//            +VerticalBar("throttleBar", this@uiScene).apply {
//                throttleBar = this
//                layoutSpec.setOrigin(dps(382f  * scale), dps(15f * scale), zero())
//                layoutSpec.setSize(dps(50f * scale), dps(140f * scale), full())
//                trackColor = ColorGradient(Color.WHITE.withAlpha(0.5f), MdColor.ORANGE)
//            }
//
//            +VerticalBar("brakeBar", this@uiScene).apply {
//                brakeBar = this
//                layoutSpec.setOrigin(dps(447f  * scale), dps(15f * scale), zero())
//                layoutSpec.setSize(dps(50f * scale), dps(140f * scale), full())
//                trackColor = ColorGradient(Color.WHITE.withAlpha(0.5f), MdColor.DEEP_ORANGE)
//            }
//
//            +HorizontalBar("steering", this@uiScene).apply {
//                steeringBar = this
//                layoutSpec.setOrigin(dps(512f  * scale), dps(15f * scale), zero())
//                layoutSpec.setSize(dps(110f * scale), dps(25f * scale), full())
//            }
//
//            +GMeter("gMeter", this@uiScene).apply {
//                gMeter = this
//                layoutSpec.setOrigin(dps(532f  * scale), dps(45f * scale), zero())
//                layoutSpec.setSize(dps(110f * scale), dps(110f * scale), full())
//            }
//        }