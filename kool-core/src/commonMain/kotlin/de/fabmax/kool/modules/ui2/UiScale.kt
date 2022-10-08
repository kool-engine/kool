package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontMap

object UiScale {

    var uiScale = mutableStateOf(1f)
    var windowScale = mutableStateOf(1f)

    var measuredScale = 1f
        private set

    val fonts = mutableSetOf<Font>()

    fun loadFont(font: Font, ctx: KoolContext): FontMap {
        val map = font.getOrLoadFontMap(ctx, measuredScale)
        fonts += font
        return map
    }

    fun updateScale(surface: UiSurface) {
        measuredScale = uiScale.use(surface) * windowScale.use(surface)
    }
}