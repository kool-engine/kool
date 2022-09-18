package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

/**
 * Material Design color system:
 * https://material.io/design/color/the-color-system.html#color-theme-creation
 *
 * - [primary]: The color displayed most frequently across your app's screens and components.
 * - [primaryVariant]: Used to distinguish two elements of the app using the primary color.
 * - [secondary]: Provides more ways to accent and distinguish your product. Secondary colors are best for:
 *   - Selection controls like sliders and switches
 *   - Highlighting selected text
 *   - Headlines
 * - [secondaryVariant]: Used to distinguish two elements of the app using the secondary color.
 * - [background]: Appears behind scrollable content
 * - [surface]: Used on surfaces of components, such as menus.
 * - [error]: Indicates errors in components, such as invalid text in a text field.
 * - [onPrimary]: Used for icons and text displayed on top of the primary color.
 * - [onSecondary]: Used for icons and text displayed on top of the secondary color.
 * - [onBackground]: Used for icons and text displayed on top of the background color.
 * - [onSurface]: Used for icons and text displayed on top of the surface color.
 * - [onError]: Used for icons and text displayed on top of the error color.
 * - [isLight]: Whether this color is considered as a 'light' or 'dark' set of colors.
 */
data class Colors(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val background: Color,
    val surface: Color,
    val error: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color,
    val isLight: Boolean
) {
    companion object {
        fun darkColors(
            primary: Color = Color("b2ff00"),
            primaryVariant: Color = Color("7cb200"),
            secondary: Color = MdColor.PINK,
            secondaryVariant: Color = MdColor.PINK tone 800,
            background: Color = Color("20202080"),
            surface: Color = Color("00000080"),
            error: Color = Color("b00020"),
            onPrimary: Color = Color.BLACK,
            onSecondary: Color = Color.BLACK,
            onBackground: Color = Color.WHITE,
            onSurface: Color = Color.WHITE,
            onError: Color = Color.BLACK,
            isLight: Boolean = false
        ): Colors = Colors(
            primary,
            primaryVariant,
            secondary,
            secondaryVariant,
            background,
            surface,
            error,
            onPrimary,
            onSecondary,
            onBackground,
            onSurface,
            onError,
            isLight
        )
    }
}
