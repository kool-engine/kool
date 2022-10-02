package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.demo.Settings
import de.fabmax.kool.demo.physics.vehicle.DemoVehicle
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.MdColor

class VehicleUi(val vehicle: DemoVehicle) {

    var onToggleSound: (Boolean) -> Unit = { }

    val dashboard = Dashboard()
    val timerUi = Timer(this)

    private val menuColors = Colors.darkColors(
        accent = MdColor.ORANGE,
        accentVariant = MdColor.ORANGE.mix(Color.BLACK, 0.3f),
        background = Color("00000070")
    )

    val uiSurface = UiSurface(colors = menuColors) {
        val themeSizes = Settings.uiSize.use().sizes
        val nrmFont = themeSizes.normalText
        surface.sizes = themeSizes.copy(
            normalText = FontProps(nrmFont.family, nrmFont.sizePts * 1.1f, Font.ITALIC),
            largeText = FontProps(nrmFont.family, nrmFont.sizePts * 3.5f, Font.ITALIC, chars = "-01234567890.:"),
        )

        modifier
            .background(null)
            .width(Grow.Std)
            .height(Grow.Std)

        dashboard()
        timerUi()
    }
}