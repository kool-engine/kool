package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.demo.Settings
import de.fabmax.kool.demo.physics.vehicle.DemoVehicle
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.AtlasFont
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MdColor

class VehicleUi(val vehicle: DemoVehicle) {

    var onToggleSound: (Boolean) -> Unit = { }

    val dashboard = Dashboard()
    val timerUi = Timer(this)

    private val menuColors = Colors.singleColorDark(
        accent = MdColor.ORANGE,
        background = Color("00000070")
    )

    val uiSurface = Panel(colors = menuColors) {
        surface.sizes = getSizes(Settings.uiSize.use().sizes)

        modifier
            .background(null)
            .layout(CellLayout)
            .width(FitContent)
            .height(FitContent)
            .align(AlignmentX.Start, AlignmentY.Bottom)
        dashboard()

        surface.popup().apply {
            modifier
                .width(FitContent)
                .height(FitContent)
                .align(AlignmentX.Center, AlignmentY.Top)
            timerUi()
        }
    }

    class VehicleUiFonts(baseFontSize: Float) {
        val normalFont = AtlasFont(sizePts = baseFontSize * 1.1f, style = Font.ITALIC)
        val largeFont = AtlasFont(sizePts = baseFontSize * 3.5f, style = Font.ITALIC, chars = "-01234567890.:")
        val speedFont = AtlasFont(sizePts = baseFontSize * 6f, style = Font.ITALIC, chars = "-01234567890.:")
    }

    companion object {
        private val sizes = mutableMapOf<Sizes, Sizes>()
        private val fonts = mutableMapOf<Float, VehicleUiFonts>()

        fun getUiFonts(baseFontSize: Float): VehicleUiFonts {
            return fonts.getOrPut(baseFontSize) { VehicleUiFonts(baseFontSize) }
        }

        fun getSizes(globalSizes: Sizes): Sizes {
            return sizes.getOrPut(globalSizes) {
                val fonts = getUiFonts(globalSizes.normalText.sizePts)
                globalSizes.copy(normalText = fonts.normalFont, largeText = fonts.largeFont)
            }
        }
    }
}