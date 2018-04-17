package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
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

    fun standardFont(uiDpi: Float, ctx: KoolContext): Font =
            uiFont(standardFontProps.family, standardFontProps.sizePts, uiDpi, ctx, standardFontProps.style, standardFontProps.chars)
    var standardFontProps = FontProps(Font.SYSTEM_FONT, 20f)
        protected set

    fun titleFont(uiDpi: Float, ctx: KoolContext): Font =
            uiFont(titleFontProps.family, titleFontProps.sizePts, uiDpi, ctx, titleFontProps.style, titleFontProps.chars)
    var titleFontProps = FontProps(Font.SYSTEM_FONT, 28f)
        protected set

    fun newComponentUi(c: UiComponent): ComponentUi = componentUi(c)
    var componentUi: ((UiComponent) -> ComponentUi) = ::BlurredComponentUi
        protected set
    fun newContainerUi(c: UiContainer): ComponentUi = containerUi(c)
    var containerUi: ((UiContainer) -> ComponentUi) = { BlankComponentUi() }
        protected set

    fun newButtonUi(c: Button): ButtonUi = buttonUi(c, newComponentUi(c))
    var buttonUi: ((Button, ComponentUi) -> ButtonUi) = ::ButtonUi
        protected set
    fun newLabelUi(c: Label): LabelUi = labelUi(c, newComponentUi(c))
    var labelUi: ((Label, ComponentUi) -> LabelUi) = ::LabelUi
        protected set
    fun newSliderUi(c: Slider): SliderUi = sliderUi(c, newComponentUi(c))
    var sliderUi: ((Slider, ComponentUi) -> SliderUi) = ::SliderUi
        protected set
    fun newTextFieldUi(c: TextField): TextFieldUi = textFieldUi(c, newComponentUi(c))
    var textFieldUi: ((TextField, ComponentUi) -> TextFieldUi) = ::TextFieldUi
        protected set
    fun newToggleButtonUi(c: ToggleButton): ToggleButtonUi = toggleButtonUi(c, newComponentUi(c))
    var toggleButtonUi: ((ToggleButton, ComponentUi) -> ToggleButtonUi) = ::ToggleButtonUi
        protected set

    companion object {
        val DARK = theme {
            backgroundColor(color("00141980"))
            foregroundColor(Color.WHITE)
            accentColor(Color.LIME)
        }

        val DARK_SIMPLE = theme(DARK) {
            componentUi(::SimpleComponentUi)
        }

        val LIGHT = theme {
            backgroundColor(Color.WHITE.withAlpha(0.6f))
            foregroundColor(color("3E2723"))
            accentColor(color("BF360C"))
        }

        val LIGHT_SIMPLE = theme(LIGHT) {
            componentUi(::SimpleComponentUi)
        }
    }
}

fun UiComponent.standardFont(ctx: KoolContext): Font = root.theme.standardFont(dpi, ctx)
fun UiComponent.titleFont(ctx: KoolContext): Font = root.theme.titleFont(dpi, ctx)

fun theme(base: UiTheme? = null, block: ThemeBuilder.() -> Unit): UiTheme {
    val builder = ThemeBuilder(base)
    builder.block()
    return builder
}

class ThemeBuilder(base: UiTheme?) : UiTheme() {
    init {
        if (base != null) {
            backgroundColor = base.backgroundColor
            foregroundColor = base.foregroundColor
            accentColor = base.accentColor
            standardFontProps = base.standardFontProps
            titleFontProps = base.titleFontProps
            componentUi = base.componentUi
            containerUi = base.containerUi
            buttonUi = base.buttonUi
            labelUi = base.labelUi
            sliderUi = base.sliderUi
            textFieldUi = base.textFieldUi
            toggleButtonUi = base.toggleButtonUi
        }
    }

    fun backgroundColor(bgColor: Color) { backgroundColor = bgColor }
    fun foregroundColor(fgColor: Color) { foregroundColor = fgColor }
    fun accentColor(fgColor: Color) { accentColor = fgColor }
    fun standardFont(props: FontProps) { standardFontProps = props }
    fun titleFont(props: FontProps) { titleFontProps = props }
    fun componentUi(fab: (UiComponent) -> ComponentUi) { componentUi = fab }
    fun containerUi(fab: (UiContainer) -> ComponentUi) { containerUi = fab }
    fun buttonUi(fab: (Button, ComponentUi) -> ButtonUi) { buttonUi = fab}
    fun labelUi(fab: (Label, ComponentUi) -> LabelUi) { labelUi = fab}
    fun sliderUi(fab: (Slider, ComponentUi) -> SliderUi) { sliderUi = fab}
    fun textFieldUi(fab: (TextField, ComponentUi) -> TextFieldUi) { textFieldUi = fab}
    fun toggleButtonUi(fab: (ToggleButton, ComponentUi) -> ToggleButtonUi) { toggleButtonUi = fab}
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
