package de.fabmax.kool.scene.ui

import de.fabmax.kool.util.*

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

    var componentUi: ((UiComponent) -> ComponentUi) = ::BlurredComponentUi
    var containerUi: ((UiContainer) -> ComponentUi) = { BlankComponentUi() }

    var buttonUi: ((Button) -> ButtonUi) = { c -> ButtonUi(c, componentUi(c)) }
    var labelUi: ((Label) -> LabelUi) = { c -> LabelUi(c, componentUi(c)) }
    var sliderUi: ((Slider) -> SliderUi) = { c -> SliderUi(c, componentUi(c)) }
    var textFieldUi: ((TextField) -> TextFieldUi) = { c -> TextFieldUi(c, componentUi(c)) }
    var toggleButtonUi: ((ToggleButton) -> ToggleButtonUi) = { c -> ToggleButtonUi(c, componentUi(c)) }

    companion object {
        val DEFAULT = UiTheme()

        val DARK = theme(DEFAULT) {
            backgroundColor(color("00141980"))
            foregroundColor(Color.WHITE)
            accentColor(Color.LIME)
        }

        val LIGHT = theme(DEFAULT) {
            backgroundColor(Color.WHITE.withAlpha(0.6f))
            foregroundColor(color("3E2723"))
            accentColor(color("BF360C"))
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
            componentUi = base.componentUi
            containerUi = base.containerUi
        }
    }

    fun backgroundColor(bgColor: Color) { backgroundColor = bgColor }
    fun foregroundColor(fgColor: Color) { foregroundColor = fgColor }
    fun accentColor(fgColor: Color) { accentColor = fgColor }
    fun standardFont(props: FontProps) { standardFont = props }
    fun titleFont(props: FontProps) { titleFont = props }
    fun componentUi(fab: (UiComponent) -> ComponentUi) { componentUi = fab }
    fun containerUi(fab: (UiContainer) -> ComponentUi) { containerUi = fab }
}

class ThemeOrCustomProp<T>(initVal: T) {
    var prop: T = initVal
        private set
    var themeVal: T = initVal
        private set
    var isThemeSet = false
        private set
    var customVal: T = initVal
        private set
    var isCustom = false
        private set

    val isUpdate: Boolean
        get() = (isCustom && prop != customVal) || (isThemeSet && prop != themeVal)

    fun setTheme(themeVal: T): ThemeOrCustomProp<T> {
        this.themeVal = themeVal
        isThemeSet = true
        return this
    }

    fun setCustom(customVal: T): ThemeOrCustomProp<T> {
        this.customVal = customVal
        isCustom = true
        return this
    }

    fun clearCustom() {
        isCustom = false
    }

    fun apply(): T {
        if (isCustom) {
            prop = customVal
        } else if (isThemeSet) {
            prop = themeVal
        }
        return prop
    }
}
