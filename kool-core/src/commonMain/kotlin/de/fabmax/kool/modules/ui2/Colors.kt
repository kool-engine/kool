package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color

/**
 * UI colors. Somewhat based on the Material Design color system:
 *   https://material.io/design/color/the-color-system.html#color-theme-creation
 * However, primary and secondary color are replaced by a single accent color.
 *
 * - [accent]: Accent color used by UI elements.
 * - [accentVariant]: A little less prominent than the main accent color.
 * - [background]: Used on surfaces of components, such as menus.
 * - [backgroundVariant]: Appears behind scrollable content.
 * - [error]: Indicates errors in components, such as invalid text in a text field.
 * - [onAccent]: Used for icons and text displayed on top of the secondary color.
 * - [onBackground]: Used for icons and text displayed on top of the background color.
 * - [onError]: Used for icons and text displayed on top of the error color.
 * - [isLight]: Whether this color is considered as a 'light' or 'dark' set of colors.
 */
data class Colors(
    val accent: Color,
    val accentVariant: Color,
    val background: Color,
    val backgroundVariant: Color,
    val error: Color,
    val onAccent: Color,
    val onBackground: Color,
    val onError: Color,
    val isLight: Boolean
) {
    companion object {
        fun darkColors(
            accent: Color = Color("b2ff00"),
            accentVariant: Color = Color("7cb200"),
            background: Color = Color("101010d0"),
            backgroundVariant: Color = Color("202020d0"),
            error: Color = Color("b00020"),
            onAccent: Color = Color.BLACK,
            onBackground: Color = Color.WHITE,
            onError: Color = Color.BLACK,
            isLight: Boolean = false
        ): Colors = Colors(
            accent,
            accentVariant,
            background,
            backgroundVariant,
            error,
            onAccent,
            onBackground,
            onError,
            isLight
        )
    }
}
