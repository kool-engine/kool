package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps

object UiScale {

    var uiScale = mutableStateOf(1f)
    var windowScale = mutableStateOf(1f)

    var measuredScale = 1f
        private set

    val fonts = mutableMapOf<FontProps, Font>()
    private var activeFontScale = 1f

    fun getOrCreateFont(fontProps: FontProps, ctx: KoolContext): Font {
        val noScaleProps = if (fontProps.isScaledByWindowScale) {
            fontProps.copy(isScaledByWindowScale = false)
        } else {
            fontProps
        }

        return fonts.getOrPut(noScaleProps) {
            val font = Font(noScaleProps)
            font.charMap = ctx.assetMgr.createCharMap(noScaleProps, activeFontScale)
            font
        }
    }

    fun updateScale(surface: UiSurface, ctx: KoolContext) {
        measuredScale = uiScale.use(surface) * windowScale.use(surface)
        if (measuredScale != activeFontScale) {
            activeFontScale = measuredScale
            fonts.values.forEach {
                ctx.assetMgr.updateCharMap(it.charMap!!, measuredScale)
            }
        }
    }
}