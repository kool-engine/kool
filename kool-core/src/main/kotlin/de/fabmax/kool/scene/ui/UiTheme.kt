package de.fabmax.kool.scene.ui

import de.fabmax.kool.shading.blurShader
import de.fabmax.kool.util.*
import kotlin.Unit

/**
 * @author fabmax
 */
open class UiTheme {
    var backgroundColor = Color.BLACK
        protected set
    var foregroundColor = Color.WHITE
        protected set
    var accentColor = Color.LIME
        protected set

    var standardFont = FontProps(Font.SYSTEM_FONT, 24f)
        protected set
    fun standardFont(dpi: Float): Font {
        return uiFont(standardFont.family, standardFont.sizePts, dpi, standardFont.style, standardFont.chars)
    }

    var titleFont = FontProps(Font.SYSTEM_FONT, 24f, Font.BOLD)
        protected set
    fun titleFont(dpi: Float): Font {
        return uiFont(titleFont.family, titleFont.sizePts, dpi, titleFont.style, titleFont.chars)
    }

    var containerBackground: ((UiComponent) -> Background?) = { null }

    var componentBackground: ((UiComponent) -> Background?) = { BlurredBackground(it).apply {
        blurShader.colorMix = 0.7f
        color = backgroundColor
    }}

    companion object {
        val DEFAULT = UiTheme()

        val DARK = theme(DEFAULT) {
            backgroundColor(color("001419"))
            foregroundColor(Color.WHITE)
            accentColor(Color.LIME)
            componentBackground = { BlurredBackground(it).apply {
                blurShader.colorMix = 0.5f
                color = backgroundColor
            }}
        }

        val LIGHT = theme(DEFAULT) {
            backgroundColor(Color.WHITE)
            foregroundColor(color("3E2723"))
            accentColor(color("BF360C"))
            componentBackground = { BlurredBackground(it).apply {
                blurShader.colorMix = 0.5f
                color = backgroundColor
            }}
        }
    }
}

fun theme(base: UiTheme? = null, block: ThemeBuilder.() -> Unit): UiTheme {
    val builder = ThemeBuilder(base)
    builder.block()
    return builder
}

class ThemeBuilder(base: UiTheme?) : UiTheme() {
    init {
        if (base != null) {
            backgroundColor = base.backgroundColor
            foregroundColor = base.backgroundColor
            accentColor = base.backgroundColor
            standardFont = base.standardFont
            titleFont = base.titleFont
            containerBackground = base.containerBackground
            componentBackground = base.componentBackground
        }
    }

    fun backgroundColor(bgColor: Color) { backgroundColor = bgColor }
    fun foregroundColor(fgColor: Color) { foregroundColor = fgColor }
    fun accentColor(fgColor: Color) { accentColor = fgColor }
    fun standardFont(props: FontProps) { standardFont = props }
    fun titleFont(props: FontProps) { titleFont = props }
    fun containerBackground(fab: (UiComponent) -> Background?) { containerBackground = fab }
    fun componentBackground(fab: (UiComponent) -> Background?) { componentBackground = fab }
}

class ThemeOrCustomProp<T>(val defaultVal: T) {
    var prop: T? = null
        private set
    var themeVal: T? = null
        private set
    var isThemeSet = false
        private set
    var customVal: T? = null
        private set
    var isCustom = false
        private set

    val propOrDefault
        get() = prop ?: defaultVal

    fun setTheme(themeVal: T) {
        this.themeVal = themeVal
        isThemeSet = true
    }

    fun setCustom(customVal: T) {
        this.customVal = customVal
        isCustom = true
    }

    fun clearCustom() {
        customVal = null
        isCustom = false
    }

    fun updateProp(): T? {
        if (isCustom) {
            prop = customVal
        } else if (isThemeSet) {
            prop = themeVal
        }
        return prop
    }

    fun needsUpdate(): Boolean = (isCustom && prop != customVal) || (isThemeSet && prop != themeVal)
}
